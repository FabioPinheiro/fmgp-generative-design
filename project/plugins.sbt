//https://www.scala-js.org/news/2019/12/13/announcing-scalajs-1.0.0-RC2/
val scalaJSVersion = sys.env.getOrElse("SCALAJS_VERSION", "1.0.0-RC2")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % scalaJSVersion)
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.10")

/** scalajs-bundler
  * https://scalacenter.github.io/scalajs-bundler/getting-started.html
  * enablePlugins(ScalaJSBundlerPlugin)
  *
  * You need to have npm installed on your system.
  */
addSbtPlugin(
  "ch.epfl.scala" % "sbt-scalajs-bundler" % "0.16.0"
  //"ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.16.0+1-bcf16542+20191216-0115"
)
