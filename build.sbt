inThisBuild(
  Seq(
    organization := "app.fmgp",
    scalaVersion := "3.0.0",
    updateOptions := updateOptions.value.withLatestSnapshots(false),
  )
)

lazy val noPublishSettings = skip / publish := true
lazy val publishSettings = Seq(
  Test / publishArtifact := false,
  pomIncludeRepository := (_ => false),
  homepage := Some(url("https://github.com/FabioPinheiro/fmgp-generative-design")),
  licenses := Seq("MIT License" -> url("https://github.com/FabioPinheiro/fmgp-generative-design/blob/master/LICENSE")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/FabioPinheiro/fmgp-generative-design"),
      "scm:git:git@github.com:FabioPinheiro/fmgp-generative-design.git"
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

lazy val settingsFlags: Seq[sbt.Def.SettingsDefinition] = Seq(
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8", // source files are in UTF-8
    "-deprecation", // warn about use of deprecated APIs
    "-unchecked", // warn about unchecked type parameters
    "-feature", // warn about misused language features
    "-Xfatal-warnings",
    //"-Yexplicit-nulls",
    //TODO "-Ysafe-init",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    //"-Xsource:3", //https://scalacenter.github.io/scala-3-migration-guide/docs/tooling/migration-tools.html
    //"-Ytasty-reader",
    "-Xprint-diff-del", //"-Xprint-diff",
    "-Xprint-inline",
  ) //++ Seq("-rewrite", "-indent", "-source", "future-migration") //++ Seq("-source", "future")
)

val setupTestConfig: Seq[sbt.Def.SettingsDefinition] = Seq(
  Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
  libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test,
)

lazy val commonSettings: Seq[sbt.Def.SettingsDefinition] = settingsFlags ++ Seq(
  Compile / doc / sources := Nil,
)

lazy val scalaJSBundlerConfigure: Project => Project =
  _.settings(commonSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(setupTestConfig: _*)
    .settings(
      /* disabled because it somehow triggers many warnings */
      scalaJSLinkerConfig ~= (_.withSourceMap(false).withModuleKind(ModuleKind.CommonJSModule)),
      scalaJSLinkerConfig ~= {
        _.withJSHeader(
          """/* FMGP generative design (a geometric liberty)
            | * https://github.com/FabioPinheiro/fmgp-generative-design
            | * Copyright: Fabio Pinheiro - fabiomgpinheiro@gmail.com
            | */""".stripMargin.trim() + "\n"
        )
      }
    )
    // .settings( //TODO https://scalacenter.github.io/scalajs-bundler/reference.html#jsdom
    //   //jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    //   //Test / requireJsDomEnv := true)
    // )
    .enablePlugins(ScalablyTypedConverterPlugin)
    .settings(
      // Compile / fastOptJS / webpackExtraArgs += "--mode=development",
      // Compile / fullOptJS / webpackExtraArgs += "--mode=production",
      Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
      Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production",
      useYarn := true
    )

lazy val modules: List[ProjectReference] =
  List(threeUtils, geometryModelJVM, geometryModelJS, geometryCore)
//List(threeUtils, geometryModelJVM, geometryModelJS, geometryCore, geometryWebapp, controller)

lazy val root = project
  .in(file("."))
  .aggregate(modules: _*)
  .settings(commonSettings: _*)
  .settings(noPublishSettings)

val threeVersion = "0.117.1" // https://www.npmjs.com/package/three
val circeVersion = "0.15.0-M1" // https://mvnrepository.com/artifact/io.circe/circe-core
val scalajsDomVersion = "1.2.0" // https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
//FIXME val scalajsLoggingVersion = "1.1.2-SNAPSHOT" //"1.1.2"
val akkaVersion = "2.6.15"
val akkaHttpVersion = "10.2.4"
val munitVersion = "0.7.26"

/* For munit https://scalameta.org/munit/docs/getting-started.html#scalajs-setup */
lazy val geometryModel = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/01-model"))
  .settings(name := "fmgp-geometry-model")
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(setupTestConfig: _*)
  .settings(
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion, //0.14.1 does not work with scala 3
      "io.circe" %%% "circe-parser" % circeVersion % Test,
    ),
    libraryDependencies += "dev.zio" %% "zio" % "1.0.9",
  )
  // /.settings(setupTestConfig, libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test)
  .settings(publishSettings)

lazy val threeUtils = project
  .in(file("modules/01-threejs-utils"))
  .settings(name := "fmgp-geometry-threejs-utils")
  .configure(scalaJSBundlerConfigure)
  .settings(
    scalaJSUseMainModuleInitializer := false,
    Compile / npmDependencies += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly(), //LibraryAndApplication
  )
  .settings(noPublishSettings)

lazy val geometryModelJS = geometryModel.js
lazy val geometryModelJVM = geometryModel.jvm

lazy val geometryCore = project
  .in(file("modules/02-core"))
  .settings(name := "fmgp-geometry-core")
  .configure(scalaJSBundlerConfigure)
  .settings(
    libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % scalajsDomVersion).cross(CrossVersion.for3Use2_13),
    //libraryDependencies += ("org.scala-js" %% "scalajs-logging" % scalajsLoggingVersion), //jsDependencies FIXME
    //  .cross(CrossVersion.for3Use2_13),
    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion, //0.14.1 does not work with scala 3
      "io.circe" %%% "circe-parser" % circeVersion,
    ),
    Compile / npmDependencies ++= Seq(
      "three" -> threeVersion,
      "stats.js" -> "0.17.0",
      "@types/stats.js" -> "0.17.0", //https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/stats.js
    ),
    scalaJSUseMainModuleInitializer := true,
    //mainClass := Some("fmgp.Main"),
    //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  )
  .dependsOn(threeUtils, geometryModelJS)
  .settings(publishSettings)

// lazy val controller = project
//   .in(file("modules/03-controller"))
//   //.settings(scalaVersion := "3.0.2-RC1-bin-20210706-6011847-NIGHTLY")
//   .settings(commonSettings: _*)
//   .settings(
//     libraryDependencies ++= Seq(
//       "io.circe" %%% "circe-core" % circeVersion,
//       "io.circe" %%% "circe-generic" % circeVersion, //0.14.1 does not work with scala 3
//       "io.circe" %%% "circe-parser" % circeVersion,
//     ),
//     libraryDependencies ++= Seq(
//       ("com.typesafe.akka" %% "akka-http" % akkaHttpVersion).cross(CrossVersion.for3Use2_13),
//       ("com.typesafe.akka" %% "akka-stream" % akkaVersion).cross(CrossVersion.for3Use2_13),
//       "ch.qos.logback" % "logback-classic" % "1.2.3",
//       "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
//       ("com.typesafe.akka" %% "akka-slf4j" % "2.6.15").cross(CrossVersion.for3Use2_13),
//     ),
//     libraryDependencies += "org.scalameta" %%% "munit" % munitVersion % Test,
//     console / initialCommands += """
//     import scala.math._
//     import scala.util.chaining._
//     app.fmgp.Main.start()
//     val myAkkaServer = app.fmgp.Main.server.get
//     import myAkkaServer.GeoSyntax._
//     import app.fmgp.geo._
//     """,
//     cleanupCommands += """
//     app.fmgp.Main.stop
//     """,
//   )
//   .dependsOn(geometryModelJVM)
//   .settings(noPublishSettings)

// lazy val geometryWebapp = project
//   .in(file("modules/03-webapp"))
//   .settings(name := "fmgp-geometry-webapp")
//   .configure(scalaJSBundlerConfigure)
//   // .settings(commonSettings: _*)
//   // .enablePlugins(ScalaJSPlugin)
//   // .enablePlugins(ScalaJSBundlerPlugin)
//   // .settings(setupTestConfig: _*)
//   .settings(
//     libraryDependencies += ("org.scala-js" %%% "scalajs-dom" % scalajsDomVersion).cross(CrossVersion.for3Use2_13),
//     libraryDependencies += "com.raquo" %%% "laminar" % "0.13.1",
//     libraryDependencies += "com.raquo" %%% "waypoint" % "0.4.1",
//     libraryDependencies += "com.lihaoyi" %%% "upickle" % "1.3.13",
//     libraryDependencies ++= Seq(
//       "io.circe" %%% "circe-core" % circeVersion,
//       "io.circe" %%% "circe-generic" % circeVersion, //0.14.1 does not work with scala 3
//       "io.circe" %%% "circe-parser" % circeVersion,
//     ),
//     Compile / npmDependencies ++= Seq(
//       "three" -> threeVersion,
//       "stats.js" -> "0.17.0",
//       "@types/stats.js" -> "0.17.0", //https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/stats.js
//     ),
//   )
//   .settings(
//     scalaJSUseMainModuleInitializer := true,
//     webpackBundlingMode := BundlingMode.LibraryAndApplication(),
//   )
//   .dependsOn(threeUtils, geometryModelJS, geometryCore)
//   .settings(noPublishSettings)

// lazy val demo = project
//   .in(file("modules/03-demo"))
//   .configure(baseSettings)
//   .settings(
//     name := "fmgp-generative-design-demo",
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
