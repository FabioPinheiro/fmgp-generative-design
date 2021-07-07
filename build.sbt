name := "fmgp-geometry"

lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := "3.0.0", //Same on "3.0.2-RC1-bin-20210706-6011847-NIGHTLY"
    libraryDependencies += "io.circe" %% "circe-generic" % "0.14.1"
  )
