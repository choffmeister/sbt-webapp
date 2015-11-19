package de.choffmeister.sbt

import sbt._
import sbt.Keys._

case class WebAppToolsVersions(nodeVersion: Option[VersionString], npmVersion: Option[VersionString])

object WebAppPlugin extends Plugin {
  private var npmStartProcess: Option[Process] = None

  val npmInstall = taskKey[Unit]("runs 'npm install'")
  val npmTest = taskKey[Unit]("runs 'npm test'")
  val npmBuild = taskKey[File]("runs 'npm install' and then 'npm run build'")
  val npmStart = taskKey[Unit]("runs 'npm install' and then starts 'npm start' as background process")
  val npmStop = taskKey[Unit]("stops 'npm start' background process")

  val webAppNodeVersion = taskKey[Option[VersionString]]("the version of node")
  val webAppNpmVersion = taskKey[Option[VersionString]]("the version of npm")
  val webAppDir = settingKey[File]("the path to the wep app root directory")

  lazy val webAppSettings = Seq[Def.Setting[_]](
    webAppDir := baseDirectory.value,

    npmInstall <<= (streams, webAppDir) map { (s, dir) =>
      s.log.info("Running web app tests")
      Process("npm" :: "install" :: Nil, dir).!(s.log)
      s.log.info("Done")
    },
    npmTest <<= (streams, webAppDir) map { (s, dir) =>
      s.log.info("Running web app tests")
      Process("npm" :: "install" :: Nil, dir).!!(s.log)
      Process("npm" :: "test" :: Nil, dir).!(s.log)
      s.log.info("Done")
    },
    npmBuild <<= (streams, webAppDir) map { (s, dir) =>
      s.log.info("Building web app")
      Process("npm" :: "install" :: Nil, dir).!!(s.log)
      Process("npm" :: "run" :: "build" :: Nil, dir).!(s.log)
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
    },
    webAppNodeVersion := {
      getToolVersion("node")
    },
    webAppNpmVersion := {
      getToolVersion("npm")
    }
  )

  private def getToolVersion(name: String): Option[VersionString] = {
    try {
      Some(VersionString(s"$name --version".!!))
    } catch {
      case e: Exception => None
    }
  }
}
