# sbt-webapp

Integrates [NPM](https://www.npmjs.org/) into your SBT build process.

## Usage

Suppose you have a SBT multi-project with a subproject for your web application frontend that gets build with NPM and a subproject for your Scala backend. Then add the following line to your projects `project/plugins.sbt` file:

~~~
addSbtPlugin("de.choffmeister" % "sbt-webapp" % "0.1.0")
~~~

Your `project/Build.scala` could look something like this:

~~~ scala
import sbt._
import sbt.Keys._
import de.choffmeister.sbt.WebAppPlugin._

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
    .settings(dist <<= (streams, target, compile in server, npmBuild in web) map { (s, target, server, web) =>
      val distDir = target / "dist"
      s.log(s"Composing all parts to $distDir" )
      IO.copyDirectory(server, distDir)
      IO.copyDirectory(web, distDir / "web")
      distDir
    })
    .aggregate(server, web)
}
~~~

To specify what is actually executed, configure your `package.json` like for example so (if you are using [Gulp](http://gulpjs.com/)):

~~~ json
{
  "name": "myproject",
  "private": true,
  "scripts": {
    "start": "./node_modules/.bin/gulp",
    "build": "./node_modules/.bin/gulp build --dist",
    "test": "echo \"Error: no test specified\" && exit 1"
  },
  "devDependencies": {
    "gulp": "^3.8.10"
  },
  "dependencies": {
  }
}
~~~

~~~ bash
# runs npm install and npm run build
$ sbt web/npmBuild

# asynchronously runs npm start
$ sbt web/npmStart

# stop asynchronously started npm start
$ sbt web/npmStop
~~~

While development you can run your backend and your frontend in parallel by executing:

~~~ bash
$ sbt web/npmStart server/run
~~~

To both, build your backend and your frontend you can run:

~~~ bash
$ sbt dist
~~~

## License

Published under the permissive [MIT](http://opensource.org/licenses/MIT) license.
