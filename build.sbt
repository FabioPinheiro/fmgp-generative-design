ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1-M1"
ThisBuild / organization := "app.fmgp"
ThisBuild / organizationHomepage := Some(url("https://fmgp.app/"))

// format: off
ThisBuild / sonatypeProfileName := "app.fmgp"
ThisBuild / publishMavenStyle := true
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")) //TODO
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
    name := "scala-threejs",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    npmDependencies in Compile += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly()
  )

lazy val demo = (project in file("demo"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin) //, ScalaJSJUnitPlugin)
  .settings(
    publishArtifact := false,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8",
    scalaJSUseMainModuleInitializer := true,
    npmDependencies in Compile += "three" -> threeVersion,
    mainClass := Some("fmgp.Main"),
    //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
    webpackBundlingMode := BundlingMode
      .LibraryAndApplication() //This is needed for the index.html to avoid calling webpack all the time
  )
  .dependsOn(three)
