import sbt._
import sbt.Keys._
import com.typesafe.sbt.GitVersioning
import com.typesafe.sbt.SbtGit.git

object Build extends sbt.Build {
  lazy val buildSettings = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-encoding", "utf8", "-deprecation"),
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

  lazy val mavenInfos = {
    <url>https://github.com/choffmeister/sbt-webapp</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <url>github.com/choffmeister/sbt-webapp.git</url>
      <connection>scm:git:github.com/choffmeister/sbt-webapp.git</connection>
      <developerConnection>scm:git:git@github.com:choffmeister/sbt-webapp.git</developerConnection>
    </scm>
    <developers>
      <developer>
        <id>choffmeister</id>
        <name>Christian Hoffmeister</name>
        <url>http://choffmeister.de/</url>
      </developer>
    </developers> }

  lazy val root = (project in file("."))
    .settings(Defaults.coreDefaultSettings: _*)
    .enablePlugins(GitVersioning)
    .settings(git.formattedShaVersion := git.gitHeadCommit.value map(sha => s"${sha.take(7)}-SNAPSHOT"))
    .settings(buildSettings: _*)
    .settings(publishSettings: _*)
    .settings(resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases")
    .settings(libraryDependencies ++= Seq("org.specs2" %% "specs2-core" % "3.3.1" % "test"))
    .settings(pomExtra := mavenInfos)
    .settings(
      name := "sbt-webapp",
      organization := "de.choffmeister",
      organizationName := "Christian Hoffmeister",
      organizationHomepage := Some(new URL("http://choffmeister.de/")))
}
