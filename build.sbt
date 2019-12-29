val baseSettings: Seq[Setting[_]] = Seq(
  organization := "app.fmgp",
  version := "0.1",
  scalaVersion := "2.13.1",
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.8"
)

val threeVersion = "0.108.0" // https://www.npmjs.com/package/three

lazy val three = (project in file("three"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin)
  .settings(baseSettings)
  .settings(
    name := "scala-threejs",
    npmDependencies in Compile += "three" -> threeVersion,
    webpackBundlingMode := BundlingMode.LibraryOnly()
  )

lazy val demo = (project in file("demo"))
  .enablePlugins(ScalaJSPlugin)
  .enablePlugins(ScalaJSBundlerPlugin) //, ScalaJSJUnitPlugin)
  .settings(
    baseSettings,
    scalaJSUseMainModuleInitializer := true,
    npmDependencies in Compile += "three" -> threeVersion,
    mainClass := Some("fmgp.Main"),
    //scalaJSMainModuleInitializer := Some(mainMethod("fmgp.Main", "main"))
    webpackBundlingMode := BundlingMode
      .LibraryAndApplication() //This is needed for the index.html to avoid calling webpack all the time
  )
  .dependsOn(three)
