inThisBuild(
  Seq(
    organization := "app.fmgp",
    scalaVersion := "3.0.0",
    updateOptions := updateOptions.value.withLatestSnapshots(false),
  )
)

lazy val noPublishSettings = skip / publish := true
lazy val publishSettings = Seq(
  publishArtifact in Test := false,
  pomIncludeRepository := (_ => false),
  homepage := Some(url("https://github.com/FabioPinheiro/fmgp-threejs")),
  licenses := Seq("MIT License" -> url("https://github.com/FabioPinheiro/fmgp-threejs/blob/master/LICENSE")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/FabioPinheiro/fmgp-threejs"),
      "scm:git:git@github.com:FabioPinheiro/fmgp-threejs.git"
    )
  ),
  developers := List(
    Developer("FabioPinheiro", "Fabio Pinheiro", "fabiomgpinheiro@gmail.com", url("http://fmgp.app"))
  )
)
// ### PUBLISH ###
//usePgpKeyHex("E1FC5E4D458BB2DB0B99B285F1CBAB1E3F257949") //This is just a reference of the key
// must the version //in version := "0.1-M4",
//> publishSigned
//> sonatypePrepare
//> sonatypeBundleUpload

lazy val commonSettings: Seq[sbt.Def.SettingsDefinition] = Seq(
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8", // source files are in UTF-8
    "-deprecation", // warn about use of deprecated APIs
    "-unchecked", // warn about unchecked type parameters
    "-feature", // warn about misused language features
    "-Xfatal-warnings",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    //"-Xsource:3", //https://scalacenter.github.io/scala-3-migration-guide/docs/tooling/migration-tools.html
    //"-Ytasty-reader",
  ),
  sources in (Compile, doc) := Nil,
  libraryDependencies += "org.scalameta" %% "munit" % "0.7.26" % Test,
  testFrameworks += new TestFramework("munit.Framework"),
)

lazy val modules: List[ProjectReference] =
  List(threeUtils, geometryModelJvm, geometryModelJs, geometryCore) //, controller)

lazy val root = project
  .in(file("."))
  .aggregate(modules: _*)
  .settings(commonSettings: _*)
  .settings(noPublishSettings)

val threeVersion = "0.117.1" // https://www.npmjs.com/package/three
val circeVersion = "0.14.1"
val scalajsDomVersion = "1.0.0"
//FIXME val scalajsLoggingVersion = "1.1.2-SNAPSHOT" //"1.1.2"
val akkaVersion = "2.6.4"
val akkaHttpVersion = "10.1.11"

lazy val baseSettings: Project => Project =
  _.settings(commonSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .settings(
      scalaJSLinkerConfig ~= (_
      /* disabled because it somehow triggers many warnings */
        .withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule))
    )
    .enablePlugins(ScalablyTypedConverterPlugin)
    .settings(
      Compile / fastOptJS / webpackExtraArgs += "--mode=development",
      Compile / fullOptJS / webpackExtraArgs += "--mode=production",
      Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
      Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production",
      useYarn := true
    )

lazy val geometryModel = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/01-model"))
  .settings(name := "fmgp-geometry-model")
  .settings(commonSettings: _*)
  .settings(publishSettings)

lazy val threeUtils = project
  .in(file("modules/01-threejs-utils"))
  .settings(name := "fmgp-geometry-threejs-utils")
  .configure(baseSettings)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    useYarn := true,
    scalaJSUseMainModuleInitializer := false,
    npmDependencies in Compile += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly(), //LibraryAndApplication
  )
  .settings(noPublishSettings)

lazy val geometryModelJs = geometryModel.js
lazy val geometryModelJvm = geometryModel.jvm

lazy val geometryCore = project
  .in(file("modules/02-core"))
  .settings(name := "fmgp-geometry-core")
  .configure(baseSettings)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % scalajsDomVersion).cross(CrossVersion.for3Use2_13),
    //libraryDependencies += ("org.scala-js" %% "scalajs-logging" % scalajsLoggingVersion), //jsDependencies FIXME
    //  .cross(CrossVersion.for3Use2_13),
    libraryDependencies ++= Seq("core", "generic", "parser")
      .map(e => "io.circe" %%% ("circe-" + e) % circeVersion),
    npmDependencies in Compile ++= Seq(
      "three" -> threeVersion,
      "stats.js" -> "0.17.0",
      "@types/stats.js" -> "0.17.0", //https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/stats.js
    ),
    scalaJSUseMainModuleInitializer := true,
    mainClass := Some("fmgp.Main"),
    //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  )
  .dependsOn(threeUtils, geometryModelJs)
  .settings(publishSettings)

lazy val controller = project
  .in(file("modules/03-controller"))
  //.settings(scalaVersion := "3.0.2-RC1-bin-20210706-6011847-NIGHTLY")
  .settings(commonSettings: _*)
  .settings(
    scalaVersion := "2.13.5",
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion, //0.14.1 does not work with scala 3
      "io.circe" %%% "circe-parser" % circeVersion,
    ),
    libraryDependencies ++= Seq(
      ("com.typesafe.akka" %% "akka-http" % akkaHttpVersion).cross(CrossVersion.for3Use2_13),
      ("com.typesafe.akka" %% "akka-stream" % akkaVersion).cross(CrossVersion.for3Use2_13),
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
    ),
    initialCommands in console += """
    import scala.math._
    import scala.util.chaining._
    app.fmgp.Main.start()
    val myAkkaServer = app.fmgp.Main.server.get
    import myAkkaServer.GeoSyntax._
    import app.fmgp.geo._
    """,
    cleanupCommands += """
    app.fmgp.Main.stop
    """,
  )
  .dependsOn(geometryModelJvm)
  .settings(noPublishSettings)

// lazy val demo = project
//   .in(file("modules/03-demo"))
//   .configure(baseSettings)
//   .settings(
//     name := "fmgp-threejs-demo",
//     publishArtifact := false,
//     libraryDependencies += "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
//     npmDependencies in Compile += "three" -> threeVersion,
//     scalaJSUseMainModuleInitializer := true,
//     mainClass := Some("fmgp.threejs.Demo"),
//     //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
//     //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
//     webpackBundlingMode := BundlingMode.LibraryAndApplication()
//   )
//   .dependsOn(threeUtils)
//   .settings(noPublishSettings)
