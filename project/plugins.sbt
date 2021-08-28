val scalaJSVersion = sys.env.getOrElse("SCALAJS_VERSION", "1.7.0")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.1")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0"

// addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.1.0")
// FIXME
// [error] (update) found version conflict(s) in library dependencies; some are suspected to be binary incompatible:
// [error]
// [error] 	* org.scala-lang.modules:scala-java8-compat_2.12:1.0.0 (early-semver) is selected over 0.8.0
// [error] 	    +- org.scalablytyped.converter:sbt-converter:1.0.0-beta34 (sbtVersion=1.0, scalaVersion=2.12) (depends on 1.0.0)
// [error] 	    +- com.typesafe.akka:akka-actor_2.12:2.5.17           (depends on 0.8.0)

/** scalajs-bundler https://scalacenter.github.io/scalajs-bundler/getting-started.html
  * enablePlugins(ScalaJSBundlerPlugin)
  *
  * You need to have npm installed on your system.
  */
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")

//https://scalablytyped.org/docs/plugin
//https://github.com/ScalablyTyped/Converter/releases
resolvers += Resolver.bintrayRepo("oyvindberg", "converter")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta34")

// CI
addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.3")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.5.1") // sbt> dependencyUpdates

// PUBLISH
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.8.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.0") //https://github.com/sbt/sbt-pgp#sbt-pgp
