package de.choffmeister.sbt

import sbt._
import sbt.Keys._

case class WebAppToolsVersions(
  nodeVersion: Option[VersionString],
  npmVersion: Option[VersionString],
  bowerVersion: Option[VersionString],
  gulpVersion: Option[VersionString]
)

object WebAppPlugin extends Plugin {
  val webAppTest = taskKey[Unit]("executes gulp task 'test'")
  val webAppBuild = taskKey[File]("executes gulp task 'build --dist'")
  val webAppStart = taskKey[Unit]("starts a gulp development server as background process")
  val webAppStop = taskKey[Unit]("stops the running gulp development server backgrund process")

  val webAppToolsVersions = taskKey[WebAppToolsVersions]("retrieves the versions of node, npm, bower and gulp")
  val webAppInit = taskKey[Unit]("checks for node, npm, bower and gulp and installs node modules and bower components")
  val webAppSourceDir = settingKey[File]("the path to the wep app source directory")
  val webAppTargetDir = settingKey[File]("the path to the wep app target directory")

  lazy val webAppSettings = Seq[Def.Setting[_]](
    webAppSourceDir := baseDirectory.value,
    webAppTargetDir := target.value / "web",

    webAppTest := {
      val s = streams.value
      val sourceDir = webAppSourceDir.value
      val targetDir = webAppTargetDir.value

      runGulp(sourceDir, "test", targetDir, dist = false)
    },
    webAppBuild := {
      val s = streams.value
      val sourceDir = webAppSourceDir.value
      val targetDir = webAppTargetDir.value

      webAppInit.value
      s.log.info("Building web app")
      runGulp(sourceDir, "build", targetDir, dist = true)
      s.log.info("Done.")
      targetDir
    },
    webAppStart := {
      val s = streams.value
      val sourceDir = webAppSourceDir.value
      val targetDir = webAppTargetDir.value

      startGulp(sourceDir, "default", targetDir, dist = false)
    },
    webAppStop := {
      val s = streams.value

      stopGulp()
    },
    webAppInit := {
      val s = streams.value
      val sourceDir = webAppSourceDir.value
      val targetDir = webAppTargetDir.value
      val versions = webAppToolsVersions.value

      ensureToolsVersions(versions)
      s.log.info(s"Web app tools node-${versions.nodeVersion.get}, npm-${versions.npmVersion.get}, bower-${versions.bowerVersion.get} and gulp-${versions.gulpVersion.get}")
      s.log.info("Initializing web app")
      npmInstall(sourceDir)
      bowerInstall(sourceDir)
      s.log.info("Done.")
    },
    webAppToolsVersions := {
      val node = getToolVersion("node")
      val npm = getToolVersion("npm")
      val bower = getToolVersion("bower")
      val gulp = getToolVersion("gulp")

      WebAppToolsVersions(node, npm, bower, gulp)
    }
  )

  private def npmInstall(cwd: File) {
    execute("npm" :: "prune" :: Nil, cwd, "Pruning extraneous Node modules failed")
    execute("npm" :: "install" :: Nil, cwd, "Installing Node modules failed")
  }

  private def bowerInstall(cwd: File) {
    execute("bower" :: "prune" :: Nil, cwd, "Pruning extraneous Bower components failed")
    execute("bower" :: "install" :: Nil, cwd, "Installing Bower components failed")
  }

  private def runGulp(cwd: File, task: String, target: File, dist: Boolean) {
    val command = dist match {
      case false => "gulp" :: task :: s"--target=$target" :: Nil
      case true => "gulp" :: task :: s"--target=$target" :: "--dist" :: Nil
    }
    val returnValue = Process(command, cwd) !

    if (returnValue != 0) {
      throw new Exception(s"Gulp task $task failed")
    }
  }

  private def startGulp(cwd: File, task: String, target: File, dist: Boolean) {
    if (running)  stopGulp()

    process = dist match {
      case false => Process("gulp" :: task :: s"--target=$target" :: Nil, cwd).run()
      case true => Process("gulp" :: task :: s"--target=$target" :: "--dist" :: Nil, cwd).run()
    }
    running = true
  }

  private def stopGulp() {
    process.destroy()
    running = true
  }

  private def execute(cmd: List[String], cwd: File, errorMsg: String) {
    val returnValue = Process(cmd, cwd) !

    if (returnValue != 0) {
      throw new Exception(errorMsg)
    }
  }

  private def ensureToolsVersions(versions: WebAppToolsVersions) {
    versions match {
      case WebAppToolsVersions(None, _, _, _) =>
        throw new Exception("NodeJS is not installed. Please refer to http://nodejs.org/ for installation instructions.")
      case WebAppToolsVersions(_, None, _, _) =>
        throw new Exception("NPM is not installed. Please refer to http://nodejs.org/ for installation instructions.")
      case WebAppToolsVersions(_, _, None, _) =>
        throw new Exception("Bower is not installed. Please execute 'npm install -g bower'.")
      case WebAppToolsVersions(_, _, _, None) =>
        throw new Exception("Gulp is not installed. Please execute 'npm install -g gulp'.")
      case WebAppToolsVersions(Some(node), Some(npm), Some(bower), Some(gulp)) =>
        // TODO: validate min versions for tools
    }
  }

  private def getToolVersion(name: String): Option[VersionString] = {
    try {
      VersionString(s"$name --version" !!)
    } catch {
      case e: Throwable => None
    }
  }

  private var running: Boolean = false
  private var process: Process = _
}
