val scalaJSVersion = sys.env.getOrElse("SCALAJS_VERSION", "1.1.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.1")
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.1.0")

/** scalajs-bundler
  * https://scalacenter.github.io/scalajs-bundler/getting-started.html
  * enablePlugins(ScalaJSBundlerPlugin)
  *
  * You need to have npm installed on your system.
  */
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.18.0")

//https://github.com/ScalablyTyped/Distribution
resolvers += Resolver.bintrayRepo("oyvindberg", "ScalablyTyped")
addSbtPlugin("org.scalablytyped" % "sbt-scalablytyped" % "202005220924")

//https://scalablytyped.org/docs/plugin
//https://github.com/ScalablyTyped/Converter/releases
resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta13")

// CI
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.3")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1") // sbt> dependencyUpdates

// PUBLISH
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.0") //https://github.com/sbt/sbt-pgp#sbt-pgp
