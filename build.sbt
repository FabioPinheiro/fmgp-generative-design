name := "fmgp-geometry"

lazy val root = project
  .in(file("."))
  .settings(
    //https://repo1.maven.org/maven2/org/scala-lang/scala3-compiler_3/maven-metadata.xml
    scalaVersion := "3.0.2-RC1-bin-20210713-cf6fa97-NIGHTLY", // Same on version 3.0.0
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.27" % Test,
  )
