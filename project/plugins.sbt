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
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.16.0")

//Note: sbt-scalajs-bundler version 0.16.0 does not work with scalajs 1.0.0-RC2
//because is complied for scalajs version 1.0.0-RC1
//Workaround by @sjrd is to use the scalajs-linker:
//https://github.com/scalacenter/scalajs-bundler/pull/319#issuecomment-569463765
libraryDependencies += "org.scala-js" %% "scalajs-linker" % "1.0.0-RC2"
