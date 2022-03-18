inThisBuild(
  Seq(
    organization := "app.fmgp",
    scalaVersion := "3.1.0", // Also update docs/publishWebsite.sh and any ref to scala-3.1.0
    updateOptions := updateOptions.value.withLatestSnapshots(false),
  )
)

/** Versions */
lazy val V = new {

  val munit = "0.7.29"

  // https://mvnrepository.com/artifact/io.circe/circe-core
  val circe = "0.15.0-M1"

  // https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
  val scalajsDom = "2.1.0" // scalajsDom 2.0.0 need to update sbt-converter to 37?
  // val scalajsLogging = "1.1.2-SNAPSHOT" //"1.1.2"

  // https://mvnrepository.com/artifact/dev.zio/zio
  val zio = "2.0.0-RC3"

  // https://mvnrepository.com/artifact/io.github.cquiroz/scala-java-time
  val scalaJavaTime = "2.3.0"

  val akka = "2.6.18"
  val akkaHttp = "10.2.7"
  val akkaSlf4j = "2.6.18"
  val logbackClassic = "1.2.10"
  val scalaLogging = "3.9.4"

  val sttpClient = "3.3.14"

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
  val circeGeneric = Def.setting("io.circe" %%% "circe-generic" % V.circe) // 0.14.1 does not work with scala 3
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
  val stats = Seq("stats.js", "@types/stats.js").map(_ -> "0.17.0")

  // https://www.npmjs.com/package/@types/d3
  // val d3NpmDependencies = Seq("d3", "@types/d3").map(_ -> "7.1.0")

  val mermaid = Seq("mermaid" -> "8.13.3", "@types/mermaid" -> "8.2.7")

  val grpcWeb = Seq(
    "grpc-web" -> "1.2.1"
  ) // "1.3.0", //https://github.com/scalapb/scalapb-grpcweb/blob/master/build.sbt#L93

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
    "-Xfatal-warnings",
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

lazy val scalaJSBundlerConfigure: Project => Project =
  _.settings(commonSettings: _*)
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings((setupTestConfig ++ setupTestConfigJS): _*)
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
    modelJVM,
    modelJS,
    controller,
    geometryCoreJS,
    syntaxJVM,
    syntaxJS,
    prebuiltJS,
    prebuiltJVM,
    webapp,
    repl,
    protosJVM,
    protosJS,
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

lazy val modelJS = model.js
lazy val modelJVM = model.jvm

lazy val geometryCoreJS = project
  .in(file("modules/02-core"))
  .settings(name := "fmgp-geometry-core")
  .configure(scalaJSBundlerConfigure)
  .settings(jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv())
  .settings(
    libraryDependencies ++= Seq(D.dom.value, D.zioStreams.value),
    // libraryDependencies += ("org.scala-js" %% "scalajs-logging" % scalajsLoggingVersion), //jsDependencies FIXME
    //  .cross(CrossVersion.for3Use2_13),
    libraryDependencies ++= Seq(D.circeCore.value, D.circeGeneric.value, D.circeParser.value),
    Compile / npmDependencies ++= NPM.three ++ NPM.stats,
    scalaJSUseMainModuleInitializer := true,
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
  )
  .dependsOn(modelJS)
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

lazy val syntaxJS = syntax.js
lazy val syntaxJVM = syntax.jvm

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

lazy val prebuiltJS = prebuilt.js
lazy val prebuiltJVM = prebuilt.jvm

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
    Compile / unmanagedResources := ((Compile / unmanagedResources) dependsOn (webapp / Compile / fastOptJS / webpack)).value,
    Compile / unmanagedResources += (webapp / target).value /
      ("scala-" + scalaVersion.value) /
      ("scalajs-bundler/main/" + (webapp / name).value + "-fastopt-bundle.js"),
    Compile / unmanagedResources += (webapp / target).value /
      ("scala-" + scalaVersion.value) /
      ("scalajs-bundler/main/" + (webapp / name).value + "-fullopt-bundle.js"),
    Compile / unmanagedResources += (webapp / target).value /
      ("scala-" + scalaVersion.value) /
      ("scalajs-bundler/main/node_modules/material-components-web/dist/material-components-web.min.css"),
    // Compile / unmanagedResources :=
    // ((Compile / unmanagedResources) dependsOn (webapp / Compile / fastLinkJS)).value,
  )
  .settings(
    libraryDependencies ++= Seq(
      "io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion, // GRPC
      "io.grpc" % "grpc-services" % scalapb.compiler.Version.grpcJavaVersion // GRPC reflection api
    ),
    javaOptions += "-Dio.netty.tryReflectionSetAccessible=true", // For netty
    javaOptions += "--add-opens=java.base/jdk.internal.misc=ALL-UNNAMED", // For netty
  )
  .dependsOn(modelJVM, syntaxJVM, protosJVM)
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
  .settings(reStart / mainClass := Some("fmgp.SingleRequest"))
  .dependsOn(modelJVM, syntaxJVM, prebuiltJVM, controller)
  .settings(noPublishSettings)

lazy val webapp = project
  .in(file("modules/04-webapp"))
  .settings(name := "fmgp-geometry-webapp")
  .configure(scalaJSBundlerConfigure)
  .configure(buildInfoConfigure)
  .settings(
    libraryDependencies ++= Seq(D.laminar.value, D.waypoint.value, D.upickle.value),
    Compile / npmDependencies ++= NPM.three ++ NPM.stats ++ NPM.mermaid ++ NPM.grpcWeb ++ NPM.materialDesign,
  )
  .settings(
    webpackBundlingMode := BundlingMode.LibraryAndApplication(),
    // MODUELS
    // scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    // scalaJSLinkerConfig ~= (_.withModuleSplitStyle(org.scalajs.linker.interface.ModuleSplitStyle.SmallestModules)),
    Compile / scalaJSModuleInitializers += {
      org.scalajs.linker.interface.ModuleInitializer.mainMethod("fmgp.geo.webapp.App", "main")
      // .withModuleID("app_print")
      // .withModuleID("clientGRPC")
    },
  )
  .dependsOn(modelJS, prebuiltJS, geometryCoreJS, protosJS)
  .settings(noPublishSettings)

// ##############
// ###  GRPC  ###
// ##############

lazy val protos =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Pure)
    .in(file("modules/01-protos"))
    .settings(
      Compile / PB.protoSources := Seq( // show protosJVM/protocSources
        (ThisBuild / baseDirectory).value / "modules" / "01-protos" / "src" / "main" / "protobuf"
      ),
      libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    )
    .jvmSettings(
      libraryDependencies += "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      Compile / PB.targets := Seq(scalapb.gen() -> (Compile / sourceManaged).value),
    )
    .jsSettings(
      // publish locally and update the version for test
      libraryDependencies += "com.thesamet.scalapb.grpcweb" %%% "scalapb-grpcweb" % scalapb.grpcweb.BuildInfo.version,
      Compile / PB.targets := Seq(
        scalapb.gen(grpc = false) -> (Compile / sourceManaged).value,
        scalapb.grpcweb.GrpcWebCodeGenerator -> (Compile / sourceManaged).value
      )
    )

lazy val protosJS = protos.js
lazy val protosJVM = protos.jvm
