The project is a scalajs facade for threejs
https://threejs.org/

[![Build Status](https://travis-ci.com/FabioPinheiro/fmgp-threejs.svg?branch=master)](https://travis-ci.com/FabioPinheiro/fmgp-threejs)


## Doc & Demo

[README](https://fabiopinheiro.github.io/fmgp-threejs/)

[Live Demo](docs/index.html)

## Use
https://repo1.maven.org/maven2/app/fmgp/scala-threejs_sjs1.0-RC2_2.13/

```
libraryDependencies += "app.fmgp" % "fmgp-threejs_2.13" % "0.1-M1"
```


## Run on sbt:
```
fastOptJS::webpack

browserRemoteControl/console

> :load script.sc
```

## TODO LIST / Roadmap / Ideas
- Add Shapes:
 - Point (XYZ that is a Shape) [1h]
 - Frustum (Camera Helper) [2h]
 - Axis Helper [2h]
 - Axis Path [2d]
 - Text [2w]
- Make/Get nice algorithmic design examples [3w]
 - Scala logo (university stairs) [WIP]
 - Bamboo house?
 - Transformer house? Single room multi configurations
- Fix Live Demo [8h]
- Threejs:
  - Support for Terrain[16h]
  - Draft initial support for light[16h]
  - Draft initial support for materials [1w]
- Interface improvements:
  - Support for scene resizing[8h]
    - [Example from webglfundamentals](https://webglfundamentals.org/webgl/lessons/webgl-resizing-the-canvas.html)
  - Write Interface with our API [?d]
  - Make interface with 3D obj (could be used for mobile and be very useful for VR)
- Publish version 0.1 to Maven
  - Fix build.sbt for publish [16h]
  - Make documentation [2w]
  - Cleanup code
  - Rename Github repository, project name in Maven and modelos in SBT
   - Chaos theory; Butterfly effect; FMGP; Scala; Geometry
  - Make a Docker Container with everything ready to use.
    - Serve the FE's javascript file via HTTP from the BE. [4h]
    - Draft DocketFile [16h]
  - Selling points ideas:
    - Scala has really nice BigData libraries but is lack of libraries for Machine Learning, unlike python.
      I don't think the language is difficult, quite the opposite! IMO the problem is the lack of tools and libs for Data Visualization.
      I think scalaJS could feel that hole. So the plan here is the make a simples API.
- 3 Dimensional Vector graphics
- Draft support for real-time collaborative work on algorithmic design.
  - The system has 3 Components:
    - FE - A browser here the models is viewed. Most of the mesh of the models are generated on the FE!
    - BE - Server that is connected with the FE via websocket and receive commands from a client.
    - Client - Simple API / lib where algorithmic design are specified on the scala RELP or Worksheet. Connects with the BE server
- HList for Coordinate (use shapeless or wait for dotty?)
  - Implemente a Coordinate system with a HList (Heterogeneous lists).
    I imagine HList having really nice properties to support an arbitrary number of dimensions.
    Implement specialized support for 2D and 3D. In top of HList.
- Try to integrate with KHEPRI again
- Support for Constructive Geometry - union; intersection; subtraction (This is not supported on threejs)
  - Constructive Solid Geometry - basic implementation (with Quadtrees)
    Have a look and get inspiration from:
    https://github.com/dzufferey/scadla
    https://github.com/dzufferey/scadla-oce-backend
    https://stemkoski.github.io/Three.js/CSG.html
    https://github.com/stemkoski/stemkoski.github.com/blob/master/Three.js/CSG.html
    https://github.com/chandlerprall/ThreeCSG
- Experiments:
  - Input controller: GamePad; VR; Ray Tracing Pixel-select
  - Game idia: make an empty city or a shopping mall.
    Give each player's a place to remodel.
    Would be like an Architecture (algorithmic design) hackathon.
  - Use Node.js as the JavaScript runtime system. (We can try also the [Electron](https://www.electronjs.org/)
  - Compile code on the backend as a module and dynamically load it on the FE.
  - Try WebGPU and try to implemente some machine learning algorithms!
  - FPV Drone Simulator! How hard can it be?
- Optimizations: 
  - Config circe entity encoder json to drop null values vs Encode text on binary vs Use protobuf
  - Experiment with webgl shaders for optimizations.
    - Learn [WebGL Shaders](
      https://developer.mozilla.org/en-US/docs/Web/API/WebGL_API/Tutorial/Getting_started_with_WebGL)

## Copyright and License

This project is licensed under the MIT license, available at
[http://opensource.org/licenses/mit-license.php](http://opensource.org/licenses/mit-license.php)
and also in the [LICENSE](LICENSE) file.

Copyright the Fabio Pinheiro, 2020.

[cats-badge]: https://typelevel.org/cats/img/cats-badge-tiny.png
[cats-infographic]: https://github.com/tpolecat/cats-infographic
[underscore-scala-book]: https://underscore.io/books/advanced-scala
[sbt]: http://scala-sbt.org
[shapeless]: https://github.com/milessabin/shapeless
