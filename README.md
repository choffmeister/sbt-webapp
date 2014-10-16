# sbt-webapp

Integrates [NPM](https://www.npmjs.org/), [Bower](http://bower.io/) and [Gulp](http://gulpjs.com/) into your SBT build process.

## Usage

Suppose you have a SBT multi-project with a subproject for your web application frontend that gets build with Gulp and a subproject for your Scala backend. Then add the following line to your projects `project/plugins.sbt` file:

~~~
addSbtPlugin("de.choffmeister" % "sbt-webapp" % "0.0.1-SNAPSHOT")
~~~

Your `project/Build.scala` could look something like this:

~~~ scala
import sbt._
import sbt.Keys._
import WebAppPlugin._

object Build extends sbt.Build {
  lazy val dist = TaskKey[File]("dist", "Builds the distribution packages")

  lazy val commonSettings = Defaults.defaultSettings ++ Seq(
    organization := "com.mycompany",
    version := "0.1.1",
    scalaVersion := "2.10.4"
  )

  lazy val server = (project in file("myproject-server"))
    .settings(commonSettings: _*)
    .settings(name := "myproject-server")

  lazy val web = (project in file("myproject-web"))
    .settings(commonSettings: _*)
    // this line is important!!!
    .settings(webAppSettings: _*)
    .settings(name := "myproject-web")

  lazy val root = (project in file("."))
    .settings(commonSettings: _*)
    .settings(name := "myproject")
    // this task combines the backend and frontend building into one task
    .settings(dist <<= (streams, target, compile in server, webAppBuild in web) map { (s, target, server, web) =>
      val distDir = target / "dist"
      s.log(s"Composing all parts to $distDir" )
      IO.copyDirectory(server, distDir)
      IO.copyDirectory(web, distDir / "web")
      distDir
    })
    .aggregate(server, web)
}
~~~

Make sure, that NodeJS, NPM, Bower and Gulp are installed globally. To allow this plugin to properly control Gulp your `gulpfile.js` should be configured in a way that respects passing the

* `--target=/path/to/build/target` argument (to specify where gulp should put the files) and the
* `--dist` argument (to indicate that this build is meant for distribution and hence should use stuff like minification and such).

Now you have some new sbt commands available:

~~~ bash
# initialize webapp by executing npm install and bower install
$ sbt web/webAppInit

# builds the webapp in dist mode
$ sbt web/webAppBuild

# asynchronously runs gulp
$ sbt web/webAppStart

# stop asynchronously started gulp command
$ sbt web/webAppStop
~~~

While development you can run your backend and your frontend in parallel by executing:

~~~ bash
$ sbt web/webAppStart server/run
~~~

To both, build your backend and your frontend you can run:

~~~ bash
$ sbt dist
~~~

## License

Published under the permissive [MIT](http://opensource.org/licenses/MIT) license.
