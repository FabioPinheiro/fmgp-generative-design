val scalaJSVersion = sys.env.getOrElse("SCALAJS_VERSION", "1.22.0")
// crossproject - https://github.com/portable-scala/sbt-crossproject/tags
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.4.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2")

libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.1"

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.6.2")

// addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.1.0") //we can now update 1.2.0!
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
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.21.1")

//https://scalablytyped.org/docs/plugin
//https://github.com/ScalablyTyped/Converter/releases
// Converter releases are published to Maven Central (Bintray is retired).
// beta45 uses parser-combinators 2.x; PGP and ssl-config only use its retained RegexParsers API.
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % VersionScheme.Always
//resolvers += MavenRepository("sonatype-s01-snapshots", "https://s01.oss.sonatype.org/content/repositories/snapshots")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta45")

// Utils Buildinfo
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.13.1")

// CI
addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.12.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.7.0") // sbt> dependencyUpdates

// PUBLISH
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.2") //https://github.com/sbt/sbt-pgp#sbt-pgp

// https://zio.dev/howto/migrate/zio-2.x-migration-guide%20v0.9.31
//sbt "scalafixEnable; scalafixAll github:zio/zio/Zio2Upgrade?sha=series/2.x"
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.8")

// GRPC ScalaPB
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.8")
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.20"
libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.3" // https://mvnrepository.com/artifact/com.thesamet.scalapb.zio-grpc/zio-grpc-codegen
libraryDependencies += "com.thesamet.scalapb.grpcweb" %% "scalapb-grpcweb-code-gen" % "0.7.0" //https://mvnrepository.com/artifact/com.thesamet.scalapb.grpcweb/scalapb-grpcweb
