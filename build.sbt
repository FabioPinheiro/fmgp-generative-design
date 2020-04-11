ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1-M2-SNAPSHOT" //SNAPSHOT
ThisBuild / organization := "app.fmgp"
ThisBuild / organizationHomepage := Some(url("https://fmgp.app/"))

// ### PUBLISH ###
// must not have SNAPSHOT on the version
//> project three
//> publishSigned
//> sonatypePrepare
//> sonatypeBundleUpload

// format: off
ThisBuild / sonatypeProfileName := "app.fmgp"
ThisBuild / publishMavenStyle := true
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/FabioPinheiro/fmgp-threejs/blob/master/LICENSE"))
ThisBuild / homepage := Some(url("http://threejs.fmgp.app/"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/FabioPinheiro/fmgp-threejs"), "scm:git@github.com:FabioPinheiro/fmgp-threejs.git"))
ThisBuild / developers := List(Developer(id = "FabioPinheiro", name = "Fabio Pinheiro", email = "fabiomgpinheiro@gmail.com", url = url("https://fmgp.app")))
usePgpKeyHex("E1FC5E4D458BB2DB0B99B285F1CBAB1E3F257949") //This is just a reference of the key
// format: on
ThisBuild / publishTo := sonatypePublishToBundle.value //FIXME
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value)
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
//}

name := "fmgp-threejs-root"
publishArtifact := false
val threeVersion = "0.108.0" // https://www.npmjs.com/package/three
//TODO update version 0.113

lazy val baseSettings: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      scalaVersion := "2.13.1",
      version := "0.1-SNAPSHOT",
      //scalacOptions ++= ScalacOptions.flags,
      //scalaJSUseMainModuleInitializer := true,
      scalaJSLinkerConfig ~= (_
      /* disabled because it somehow triggers many warnings */
        .withSourceMap(false)
        .withModuleKind(ModuleKind.CommonJSModule))
    )

lazy val bundlerSettings: Project => Project =
  _.enablePlugins(ScalablyTypedConverterPlugin)
    .settings(
      Compile / fastOptJS / webpackExtraArgs += "--mode=development",
      Compile / fullOptJS / webpackExtraArgs += "--mode=production",
      Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",
      Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production",
      useYarn := true
    )

lazy val three = (project in file("three"))
  .configure(baseSettings, bundlerSettings)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(
    useYarn := true,
    scalaJSUseMainModuleInitializer := false,
    name := "fmgp-threejs",
    sonatypeProfileName := "app.fmgp",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    npmDependencies in Compile += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly()
  )

// lazy val demo = (project in file("demo"))
//   .configure(baseSettings, bundlerSettings)
//   .settings(
//     name := "fmgp-threejs-demo",
//     publishArtifact := false,
//     libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
//     npmDependencies in Compile += "three" -> threeVersion,
//     scalaJSUseMainModuleInitializer := true,
//     mainClass := Some("fmgp.threejs.Demo"),
//     //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
//     //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
//     webpackBundlingMode := BundlingMode.LibraryAndApplication()
//   )
//   .dependsOn(three)

lazy val geometryModel = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("geometryModel"))
  .settings(
    name := "fmgp-geometry-model",
    publishArtifact := false
  )

lazy val geometryModelJs = geometryModel.js
lazy val geometryModelJvm = geometryModel.jvm

lazy val geometryCore = (project in file("geometryCore"))
  .configure(baseSettings, bundlerSettings) //.enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "fmgp-geometry-core",
    //publishArtifact := false,
    //libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    //libraryDependencies ++= Seq(ScalablyTyped.T.three),
    libraryDependencies += "org.scala-js" %%% "scalajs-logging" % "1.0.1",
    npmDependencies in Compile += "three" -> threeVersion,
    scalaJSUseMainModuleInitializer := true,
    mainClass := Some("fmgp.Main"),
    //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
    //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
    webpackBundlingMode := BundlingMode.LibraryAndApplication()
  )
  .dependsOn(three, geometryModelJs)

val circeVersion = "0.13.0"
lazy val browserRemoteControl = (project in file("browserRemoteControl"))
  .settings(
    name := "browserRemoteControl",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.1.11",
      "com.typesafe.akka" %% "akka-stream" % "2.6.4",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
    )
  )
  .dependsOn(geometryModelJvm)
