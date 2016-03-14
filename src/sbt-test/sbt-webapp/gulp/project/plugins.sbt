{
  val pluginVersion = System.getProperty("plugin.version")
  if(pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
  else addSbtPlugin("de.choffmeister" % "sbt-webapp" % pluginVersion)
}

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.6.12")
