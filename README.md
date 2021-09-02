# fmgp-generative-design

Is a Scala/ScalaJS library for Generative Design.
The visualizer run on any Browser with JS.
Scala's RELP can be used to interact with the visualizer via WS to develop your designs.

The project is also a ScalaJs facade for [threejs](https://threejs.org/)

[![Build Status](https://travis-ci.com/FabioPinheiro/fmgp-threejs.svg?branch=master)](https://travis-ci.com/FabioPinheiro/fmgp-threejs)

## Doc & Demo

[README](https://fabiopinheiro.github.io/fmgp-generative-design/)

[Live Demo](docs/index.html)

## Use

If you want to try I recommend to checkout this repository and run it yourself with SBT.

In the future I will publish the Scala3 version and with a lot more stuff!

The old version in publish on:
https://repo1.maven.org/maven2/app/fmgp/scala-threejs_sjs1.0-RC2_2.13/

```scala
libraryDependencies += "app.fmgp" % "fmgp-threejs_2.13" % "0.1-M1"
```

## Run via sbt

```scala
fastOptJS::webpack
```

Open `file:///.../modules/02-core/index-dev.html` on your browser.

```scala
repl/console

> :load script.sc
```

## Copyright and License

This project is licensed under the MIT license, available at
[http://opensource.org/licenses/mit-license.php](http://opensource.org/licenses/mit-license.php)
and also in the [LICENSE](LICENSE) file.

Copyright the Fabio Pinheiro, 2021.

[cats-badge]: https://typelevel.org/cats/img/cats-badge-tiny.png
[cats-infographic]: https://github.com/tpolecat/cats-infographic
[underscore-scala-book]: https://underscore.io/books/advanced-scala
[sbt]: http://scala-sbt.org
[shapeless]: https://github.com/milessabin/shapeless
