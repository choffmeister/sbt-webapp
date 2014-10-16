import sbt._
import sbt.Keys._

object Build extends sbt.Build {
  lazy val buildSettings = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-encoding", "utf8"),
    sbtPlugin := true)

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials"))

  lazy val root = (project in file("."))
    .settings(Defaults.defaultSettings: _*)
    .settings(buildSettings: _*)
    .settings(publishSettings: _*)
    .settings(
      name := "sbt-webapp",
      organization := "de.choffmeister",
      version := "0.0.1-SNAPSHOT")
}
