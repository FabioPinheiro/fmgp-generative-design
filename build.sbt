import org.scalajs.linker.interface.{ModuleInitializer, ModuleSplitStyle}
import scala.sys.process._

inThisBuild(
  Seq(
    organization := "app.fmgp",
    // ScalablyTyped's sbt-converter currently supports Scala 3.3.x, not Scala 3.9.
    scalaVersion := "3.3.7",
    updateOptions := updateOptions.value.withLatestSnapshots(false),
  )
)

/** Versions */
lazy val V = new {

  val munit = "1.3.6"

  // https://mvnrepository.com/artifact/io.circe/circe-core
  val circe = "0.14.16"

  // https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
  val scalajsDom = "2.8.1"
  // val scalajsLogging = "1.1.2-SNAPSHOT" //"1.1.2"

  // https://mvnrepository.com/artifact/dev.zio/zio
  val zio = "2.1.26"

  // https://mvnrepository.com/artifact/io.github.cquiroz/scala-java-time
  val scalaJavaTime = "2.7.0"

  val grpc = "1.84.0"

  val akka = "2.6.21"
  val akkaHttp = "10.2.10"
  val akkaSlf4j = "2.6.21"
  val logbackClassic = "1.6.3"
  val scalaLogging = "3.9.6"

  val sttpClient = "3.11.0"

  val laminar = "0.14.2"
  val waypoint = "0.5.0"
  val upickle = "1.5.0"
  // https://www.npmjs.com/package/material-components-web
  val materialComponents = "12.0.0"
}

/** Dependencies */
lazy val D = new {
  val dom = Def.setting("org.scala-js" %%% "scalajs-dom" % V.scalajsDom)

  val circeCore = Def.setting("io.circe" %%% "circe-core" % V.circe)
  val circeGeneric = Def.setting("io.circe" %%% "circe-generic" % V.circe)
  val circeParser = Def.setting("io.circe" %%% "circe-parser" % V.circe)

  val zio = Def.setting("dev.zio" %%% "zio" % V.zio)
  val zioStreams = Def.setting("dev.zio" %%% "zio-streams" % V.zio)

  // Needed for ZIO
  val scalaJavaT = Def.setting("io.github.cquiroz" %%% "scala-java-time" % V.scalaJavaTime)
  val scalaJavaTZ = Def.setting("io.github.cquiroz" %%% "scala-java-time-tzdb" % V.scalaJavaTime)

  // For munit https://scalameta.org/munit/docs/getting-started.html#scalajs-setup
  val munit = Def.setting("org.scalameta" %%% "munit" % V.munit % Test)

  // For controller
  val akkaHttp = Def.setting(("com.typesafe.akka" %% "akka-http" % V.akkaHttp).cross(CrossVersion.for3Use2_13))
  val akkaStream = Def.setting(("com.typesafe.akka" %% "akka-stream" % V.akka).cross(CrossVersion.for3Use2_13))
  val akkaSlf4j = Def.setting(("com.typesafe.akka" %% "akka-slf4j" % V.akkaSlf4j).cross(CrossVersion.for3Use2_13))
  val logbackClassic = Def.setting("ch.qos.logback" % "logback-classic" % V.logbackClassic)
  val scalaLogging = Def.setting("com.typesafe.scala-logging" %% "scala-logging" % V.scalaLogging)

  // For WEBAPP
  val laminar = Def.setting("com.raquo" %%% "laminar" % V.laminar)
  val waypoint = Def.setting("com.raquo" %%% "waypoint" % V.waypoint)
  val upickle = Def.setting("com.lihaoyi" %%% "upickle" % V.upickle)

  // For
  val sttpClient = Def.setting("com.softwaremill.sttp.client3" %% "core" % V.sttpClient)
}

/** NPM Dependencies */
lazy val NPM = new {
  // https://www.npmjs.com/package/three and https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/three
  val three = Seq("three", "@types/three").map(_ -> "0.134.0")

  // https://www.npmjs.com/package/stats and https://github.com/DefinitelyTyped/DefinitelyTyped/tree/master/types/stats.js
  val stats = Seq("stats.js" -> "0.17.0", "@types/stats.js" -> "0.17.4")

  // https://www.npmjs.com/package/@types/d3
  // val d3NpmDependencies = Seq("d3", "@types/d3").map(_ -> "7.1.0")

  val mermaid = Seq("mermaid" -> "8.14.0", "@types/mermaid" -> "8.2.9")

  val grpcWeb = Seq(
    "grpc-web" -> "2.1.1"
  ) // "1.3.0", // https://github.com/scalapb/scalapb-grpcweb/blob/master/build.sbt#L93

  val materialDesign = Seq(
    "material-components-web" -> V.materialComponents,
    // "@material/ripple" -> materialComponentsVersion, // https://material.io/develop/web/supporting/ripple
    // "@material/checkbox" -> materialComponentsVersion,
    // "@material/drawer" -> materialComponentsVersion,
    // "@material/form-field" -> materialComponentsVersion,
    // "@material/top-app-bar" -> materialComponentsVersion,
    // "@material/switch"
  )
}

lazy val noPublishSettings = skip / publish := true
lazy val publishSettings = {
  val repo = "https://github.com/FabioPinheiro/fmgp-generative-design"
  val contact = Developer("FabioPinheiro", "Fabio Pinheiro", "fabiomgpinheiro@gmail.com", url("http://fmgp.app"))
  Seq(
    Test / publishArtifact := false,
    pomIncludeRepository := (_ => false),
    homepage := Some(url(repo)),
    licenses := Seq("MIT License" -> url(repo + "/blob/master/LICENSE")),
    scmInfo := Some(ScmInfo(url(repo), "scm:git:git@github.com:FabioPinheiro/fmgp-generative-design.git")),
    developers := List(contact)
  )
}
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
    "-Werror",
    // "-Yexplicit-nulls",
    // TODO "-Ysafe-init",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    // "-Xsource:3", //https://scalacenter.github.io/scala-3-migration-guide/docs/tooling/migration-tools.html
    // "-Ytasty-reader",
    "-Xprint-diff-del", // "-Xprint-diff",
    "-Xprint-inline",
  ) // ++ Seq("-rewrite", "-indent", "-source", "future-migration") //++ Seq("-source", "future")
)

lazy val setupTestConfig: Seq[sbt.Def.SettingsDefinition] = Seq(
  libraryDependencies += D.munit.value,
)
lazy val setupTestConfigJS: Seq[sbt.Def.SettingsDefinition] = Seq(
  Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
)

lazy val commonSettings: Seq[sbt.Def.SettingsDefinition] = settingsFlags ++ Seq(
  Compile / doc / sources := Nil,
)

lazy val jsHeader =
  """/* FMGP generative design (a geometric liberty)
    | * https://github.com/FabioPinheiro/fmgp-generative-design
    | * Copyright: Fabio Pinheiro - fabiomgpinheiro@gmail.com
    | */""".stripMargin.trim() + "\n"

lazy val scalaJSLibConfigure: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalablyTypedConverterGenSourcePlugin)
    .settings(
      stShortModuleNames := true, // ShortModuleNames
      stOutputPackage := "fmgp.typings", // shade into another package
      // useYarn := true,
    )

lazy val scalaJSViteConfigure: Project => Project =
  _.enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
    .settings(
      /* Configure Scala.js to emit modules in the optimal way to
       * connect to Vite's incremental reload.
       * - emit ECMAScript modules
       * - emit as many small modules as possible for classes in the "livechart" package
       * - emit as few (large) modules as possible for all other classes
       *   (in particular, for the standard library)
       */
      scalaJSLinkerConfig ~= {
        _.withModuleKind(ModuleKind.ESModule)
          .withJSHeader(jsHeader)
        // .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("livechart")))
      },
      // .withSourceMap(false) // disabled because it somehow triggers warnings and errors

      // Tell ScalablyTyped that we manage `npm install` ourselves
      externalNpm := rootPaths.value.apply("BASE").toFile(),
      // ShortModuleNames
      stShortModuleNames := true,
      stOutputPackage := "fmgp.typings", // shade into another package
      // TODO REMOVE webpackBundlingMode := BundlingMode.LibraryAndApplication(), // BundlingMode.Application,
      // TODO useYarn := true
    )

lazy val buildInfoConfigure: Project => Project = _.enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoPackage := "fmgp.geo",
    // buildInfoObject := "BuildInfo",
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      BuildInfoKey.action("buildTime") { System.currentTimeMillis }, // re-computed each time at compile
      "serverPort" -> 8888,
      "grpcPort" -> 8889,
      "grpcWebPort" -> 8890, // DOCKER: `docker run --rm -ti --net=host -v $PWD/envoy.yaml:/etc/envoy/envoy.yaml envoyproxy/envoy:v1.17.0`
    ),
  )

lazy val modules: List[ProjectReference] =
  List(
    // REMOVE threeUtils,
    model.jvm,
    model.js,
    controller,
    geometryCoreJS,
    syntax.jvm,
    syntax.js,
    prebuilt.js,
    prebuilt.jvm,
    webapp,
    repl,
    protos.jvm,
    protos.js,
  )

addCommandAlias("testJVM", ";modelJVM/test;syntaxJVM/test")
addCommandAlias("testJS", ";modelJS/test;syntaxJS/test")
addCommandAlias("testAll", ";testJVM;testJS")

lazy val root = project
  .in(file("."))
  .aggregate(modules: _*)
  .settings(commonSettings: _*)
  .settings(noPublishSettings)

// #####################################################################################################################

lazy val model = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/01-model"))
  .settings(name := "fmgp-geometry-model")
  // .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(setupTestConfig: _*)
  .jsSettings(setupTestConfigJS: _*)
  .settings(libraryDependencies ++= Seq(D.circeCore.value, D.circeGeneric.value, D.circeParser.value % Test))
  .settings(libraryDependencies += D.zio.value)
  .jsSettings(libraryDependencies ++= Seq(D.scalaJavaT.value, D.scalaJavaTZ.value)) // Needed for ZIO
  .settings(publishSettings)

//REMOVE
// lazy val threeUtils = project
//   .in(file("modules/01-threejs-utils"))
//   .settings(name := "fmgp-threejs-utils")
//   .configure(scalaJSBundlerConfigure)
//   .settings(
//     Compile / npmDependencies ++= threeNpmDependencies,
//     webpackBundlingMode := BundlingMode.LibraryOnly(),
//   )
//   .settings(noPublishSettings)

lazy val geometryCoreJS = project
  .in(file("modules/02-core"))
  .settings(name := "fmgp-geometry-core")
  .configure(scalaJSLibConfigure)
  .settings(jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv())
  .settings(
    libraryDependencies ++= Seq(D.dom.value, D.zioStreams.value),
    // libraryDependencies += ("org.scala-js" %% "scalajs-logging" % scalajsLoggingVersion), //jsDependencies FIXME
    //  .cross(CrossVersion.for3Use2_13),
    libraryDependencies ++= Seq(D.circeCore.value, D.circeGeneric.value, D.circeParser.value),
    Compile / npmDependencies ++= NPM.three ++ NPM.stats,
  )
  .dependsOn(model.js)
  .settings(publishSettings)

lazy val syntax = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/02-syntax"))
  .settings(name := "fmgp-geometry-syntax")
  .settings(libraryDependencies += D.zioStreams.value)
  // .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(setupTestConfig: _*)
  .jsSettings(setupTestConfigJS: _*)
  .dependsOn(model)
  .settings(publishSettings)

lazy val prebuilt = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/03-prebuilt"))
  .settings(name := "fmgp-geometry-prebuilt")
  // .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(setupTestConfig: _*)
  .jsSettings(setupTestConfigJS: _*)
  .dependsOn(syntax)
  .settings(noPublishSettings)

// ### controller ###
lazy val controller = project //or crossProject(JVMPlatform).crossType(CrossType.Pure)
  .in(file("modules/02-controller"))
  .configure(buildInfoConfigure)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(D.circeCore.value, D.circeGeneric.value, D.circeParser.value),
    libraryDependencies ++= Seq(D.akkaHttp.value, D.akkaStream.value),
    libraryDependencies ++= Seq(D.akkaSlf4j.value, D.logbackClassic.value, D.scalaLogging.value),
    libraryDependencies += D.munit.value,
  )
  .settings( // compile and merge webapp as a resource
    // FIXME
    // Compile / unmanagedResources := ((Compile / unmanagedResources) dependsOn (webapp / Compile / fastOptJS / webpack)).value,
    // Compile / unmanagedResources += (webapp / target).value /
    //   ("scala-" + scalaVersion.value) /
    //   ("scalajs-bundler/main/" + (webapp / name).value + "-fastopt-bundle.js"),
    // Compile / unmanagedResources += (webapp / target).value /
    //   ("scala-" + scalaVersion.value) /
    //   ("scalajs-bundler/main/" + (webapp / name).value + "-fullopt-bundle.js"),
    // Compile / unmanagedResources += (webapp / target).value /
    //   ("scala-" + scalaVersion.value) /
    //   ("scalajs-bundler/main/node_modules/material-components-web/dist/material-components-web.min.css"),
    // Compile / unmanagedResources :=
    // ((Compile / unmanagedResources) dependsOn (webapp / Compile / fastLinkJS)).value,
  )
  .settings(
    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-netty" % V.grpc, // GRPC
      "io.grpc" % "grpc-services" % V.grpc // GRPC reflection api
    ),
    javaOptions += "-Dio.netty.tryReflectionSetAccessible=true", // For netty
    javaOptions += "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", // For netty
  )
  .dependsOn(model.jvm, syntax.jvm, protos.jvm)
  .settings(publishSettings)

lazy val repl = project //or crossProject(JVMPlatform).crossType(CrossType.Pure)
  .in(file("modules/04-repl"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies += D.sttpClient.value)
  .settings(
    // console / initialCommands += """
    // import scala.math._
    // import scala.util.chaining._
    // fmgp.experiments.Main.startLocal //(interface = "127.0.0.1", port = 8888)
    // val myAkkaServer = fmgp.experiments.Main.server.get
    // val geoSyntax = fmgp.GeoSyntax(myAkkaServer)
    // import geoSyntax._
    // import fmgp.geo._
    // """,
    // cleanupCommands += """
    // fmgp.experiments.Main.stop
    // """,
  )
  .dependsOn(model.jvm, syntax.jvm, prebuilt.jvm, controller)
  .settings(noPublishSettings)

lazy val webapp = project
  .in(file("modules/04-webapp"))
  .settings(name := "fmgp-geometry-webapp")
  .settings(publish / skip := true)
  .configure(scalaJSViteConfigure)
  .configure(buildInfoConfigure)
  .settings(
    libraryDependencies ++= Seq(D.laminar.value, D.waypoint.value, D.upickle.value),
    // Compile / npmDependencies ++= NPM.three ++ NPM.stats ++ NPM.mermaid ++ NPM.grpcWeb ++ NPM.materialDesign,
  )
  .settings(
    Compile / scalaJSModuleInitializers += {
      ModuleInitializer.mainMethod("fmgp.geo.webapp.App", "main").withModuleID("geoapp")
    },
  )
  .dependsOn(model.js, prebuilt.js, geometryCoreJS, protos.js)
  .settings(noPublishSettings)

// ##############
// ###  GRPC  ###
// ##############

lazy val protos =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/01-protos"))
    .settings(name := "fmgp-geometry-protos")
    // .configure(scalaJSLibConfigure)
    .settings(
      Compile / PB.protoSources := Seq( // show protosJVM/protocSources
        (ThisBuild / baseDirectory).value / "modules" / "01-protos" / "src" / "main" / "protobuf"
      ),

      // Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"),
      libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion,
      libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf", // Only needed if you include scalapb/scalapb.proto
    )
    .jvmSettings(
      // ZIO https://scalapb.github.io/zio-grpc/docs/installation
      libraryDependencies += "io.grpc" % "grpc-netty" % V.grpc, // https://mvnrepository.com/artifact/io.grpc/grpc-netty
      libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      libraryDependencies += "io.netty" % "netty-handler" % "4.2.17.Final", // This is to forces a update in from "io.grpc" % "grpc-netty" % "1.73.0" -> https://mvnrepository.com/artifact/io.netty/netty-handler/4.1.110.Final
      // Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value),
      Compile / PB.targets := Seq(
        scalapb.gen(grpc = true) -> (Compile / sourceManaged).value / "scalapb",
        scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "scalapb",
      ),
    )
    .jsSettings(
      libraryDependencies += "com.thesamet.scalapb.grpcweb" %%% "scalapb-grpcweb" % scalapb.grpcweb.BuildInfo.version,
      Compile / PB.targets := Seq(
        scalapb.gen(grpc = false) -> (Compile / sourceManaged).value / "scalapb",
        scalapb.grpcweb.GrpcWebCodeGenerator -> (Compile / sourceManaged).value / "scalapb",
      )
    )
