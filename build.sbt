ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1-M2-SNAPSHOT" //SNAPSHOT
ThisBuild / organization := "app.fmgp"
ThisBuild / organizationHomepage := Some(url("https://fmgp.app/"))

// format: off
ThisBuild / sonatypeProfileName := "app.fmgp"
ThisBuild / publishMavenStyle := true
ThisBuild / licenses := Seq("MIT" -> url("https://github.com/FabioPinheiro/fmgp-threejs/blob/master/LICENSE"))
ThisBuild / homepage := Some(url("http://threejs.fmgp.app/"))
ThisBuild / scmInfo := Some(ScmInfo(url("https://github.com/FabioPinheiro/fmgp-threejs"), "scm:git@github.com:FabioPinheiro/fmgp-threejs.git"))
ThisBuild / developers := List(Developer(id = "FabioPinheiro", name = "Fabio Pinheiro", email = "fabiomgpinheiro@gmail.com", url = url("https://fmgp.app")))
usePgpKeyHex("E1FC5E4D458BB2DB0B99B285F1CBAB1E3F257949")
// format: on
ThisBuild / publishTo := sonatypePublishToBundle.value //FIXME
// ThisBuild / publishTo := {
//   val nexus = "https://oss.sonatype.org/"
//   if (isSnapshot.value)
//     Some("snapshots" at nexus + "content/repositories/snapshots")
//   else Some("releases" at nexus + "service/local/staging/deploy/maven2")
//}

val threeVersion = "0.108.0" // https://www.npmjs.com/package/three

lazy val three = (project in file("three"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "fmgp-threejs",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    npmDependencies in Compile += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly()
  )

lazy val demo = (project in file("demo"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin) //, ScalaJSJUnitPlugin)
  .settings(
    name := "fmgp-threejs-demo",
    publishArtifact := false,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    scalaJSUseMainModuleInitializer := true,
    npmDependencies in Compile += "three" -> threeVersion,
    mainClass := Some("fmgp.threejs.Demo"),
    //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
    //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
    webpackBundlingMode := BundlingMode.LibraryAndApplication()
  )
  .dependsOn(three)

lazy val geometryModel = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("geometryModel"))
  .settings(
    name := "fmgp-geometry-model"
  )

lazy val geometryModelJs = geometryModel.js
lazy val geometryModelJvm = geometryModel.jvm

lazy val geometryCore = (project in file("geometryCore"))
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .settings(
    name := "fmgp-geometry-core",
    publishArtifact := false,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    scalaJSUseMainModuleInitializer := true,
    npmDependencies in Compile += "three" -> threeVersion,
    mainClass := Some("fmgp.Main"),
    //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
    //LibraryAndApplication is needed for the index-dev.html to avoid calling webpack all the time
    webpackBundlingMode := BundlingMode.LibraryAndApplication()
  )
  .dependsOn(three, geometryModelJs)
