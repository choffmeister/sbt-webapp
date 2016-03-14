import de.choffmeister.sbt.VersionString
import sbt.Keys._
import sbt._

object WebApp extends sbt.AutoPlugin {
  private var npmStartProcess: Option[Process] = None

  object autoImport {
    lazy val npmInstall = taskKey[Unit]("runs 'npm install'")
    lazy val npmTest = taskKey[Unit]("runs 'npm test'")
    lazy val npmBuild = taskKey[File]("runs 'npm install' and then 'npm run build'")
    lazy val npmStart = taskKey[Unit]("runs 'npm install' and then starts 'npm start' as background process")
    lazy val npmStop = taskKey[Unit]("stops 'npm start' background process")

    lazy val webAppNodeVersion = taskKey[Option[VersionString]]("the version of node")
    lazy val webAppNpmVersion = taskKey[Option[VersionString]]("the version of npm")
    lazy val webAppDir = settingKey[File]("the path to the wep app root directory")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    webAppDir := baseDirectory.value,
    webAppNodeVersion := {
      getToolVersion("node")
    },
    webAppNpmVersion := {
      getToolVersion("npm")
    },
    npmInstall <<= (streams, webAppDir) map { (s, dir) =>
      s.log.info("Running 'npm prune'")
      run("npm" :: "prune" :: Nil, dir, s.log)
      s.log.info("Done")
      s.log.info("Running 'npm install'")
      run("npm" :: "install" :: Nil, dir, s.log)
      s.log.info("Done")
    },
    npmTest <<= (streams, webAppDir, npmInstall) map { (s, dir, _) =>
      s.log.info("Running 'npm test'")
      run("npm" :: "test" :: Nil, dir, s.log)
      s.log.info("Done")
    },
    npmBuild <<= (streams, webAppDir, npmInstall) map { (s, dir, _) =>
      s.log.info("Running 'npm run build'")
      run("npm" :: "run" :: "build" :: Nil, dir, s.log)
      s.log.info("Done")
      dir
    },
    npmStart <<= (streams, webAppDir, npmStop) map { (s, dir, _) =>
      s.log.info("Starting web app")
      npmStartProcess = Some(Process("npm" :: "start" :: Nil, dir).run(s.log))
    },
    npmStop <<= streams map { s =>
      if (npmStartProcess.isDefined) {
        s.log.info("Stopping web app")
        npmStartProcess.get.destroy()
        npmStartProcess = None
      }
    }
  )

  private def run(cmd: Seq[String], cwd: File, log: Logger): Unit = {
    val exitCode = Process(cmd, cwd).!(log)
    if (exitCode != 0) throw new Exception(s"Running '${cmd.mkString(" ")}' failed with exit code $exitCode")
  }

  private def getToolVersion(name: String): Option[VersionString] = {
    try {
      Some(VersionString(s"$name --version".!!))
    } catch {
      case e: Exception => None
    }
  }
}
