import sbt._
import sbt.Keys._
import de.choffmeister.sbt.WebAppPlugin._
import xerial.sbt.Pack._

object Build extends sbt.Build {
  lazy val dist = TaskKey[File]("dist", "Builds the distribution packages")

  lazy val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.mycompany",
    version := "0.1.1",
    scalaVersion := "2.10.4"
  )

  lazy val server = (project in file("myproject-server"))
    .settings(commonSettings: _*)
    .settings(packSettings: _*)
    .settings(packMain := Map("myproject" -> "myproject.Server"))
    .settings(name := "myproject-server")

  lazy val web = (project in file("myproject-web"))
    .settings(commonSettings: _*)
    .settings(webAppSettings: _*)
    .settings(name := "myproject-web")

  lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(name := "myproject")
    .settings(dist <<= (streams, target, pack in server, npmBuild in web) map { (s, target, server, web) =>
      // this task combines the backend and frontend building into one task
      val distDir = target / "dist"
      s.log(s"Composing all parts to $distDir" )
      IO.copyDirectory(server, distDir)
      IO.copyDirectory(web, distDir / "web")
      distDir
    })
    .aggregate(server, web)
}
