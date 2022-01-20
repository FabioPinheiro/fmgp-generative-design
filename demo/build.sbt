lazy val root = project
  .in(file("."))
  .settings(
    name := "demo",
    description := "Example fmgp project that compiles using Scala 3",
    version := "0.1.0",
    scalaVersion := "3.1.1",
    libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.3.18",
    libraryDependencies += "app.fmgp" %% "fmgp-geometry-syntax" % "0.0.0+140-6ddceb07-SNAPSHOT",
    // libraryDependencies += "app.fmgp" %% "controller" % "0.0.0+140-6ddceb07-SNAPSHOT",
  )
