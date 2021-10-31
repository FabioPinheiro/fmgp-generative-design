# FMGP Algorithmic Design

Is a Scala/ScalaJS library for Algorithmic Design.
The visualizer run on any Browser with JS.
Scala's RELP can be used to interact with the visualizer via WS to develop your designs.

The webbapp visualizer uses the [threejs](https://threejs.org/) library on the background.

## Doc & Demo

[Live Demo](https://fabiopinheiro.github.io/fmgp-generative-design/)

## How to use?

(We will make a release the future)

If you want to try I recommend to clone this repository and run it yourself with SBT.

In the future I will publish the Scala3 version!

The old version in publish on:
https://repo1.maven.org/maven2/app/fmgp/scala-threejs_sjs1.0-RC2_2.13/

```scala
libraryDependencies += "app.fmgp" % "fmgp-threejs_2.13" % "0.1-M1"
```

## How to Run?

### **Run controller (Server)**

On sbt `controller/reStart "Revolver"` (this will run the server on the background)

Open `http://localhost:8888` on your browser.

### **Hot reload example**

We suggest to develop your geometric algorithm designs, in a separate main, and having the **controller (server)** running.

Your code must post the geometric model to the **controller** via http. See an example in the main class SingleRequest.scala.

On sbt: `~repl/run app.fmgp.SingleRequest`

*Note:* `~` will hot reload a your main every time a change is made to the code.

The role of the **controller** is to keep the websocket allways open to the browser. This way the geometric model is reload almost instantaneous.
Its also possible to open a websocket communicating and directly with the browser. But it takes time for the webapp to try to reconnect.

### **Run console via sbt**

Use the scala REPL (“Read-Evaluate-Print-Loop”) to develop your geometric algorithm designs.

```scala
repl/console //start REPL from the console module
//This will starting the controller server and import the DSL

> :load script.sc //load a file. Equivalent to copy-paste ever line from script.sc

> //your code...
```

### **Open app (core) on browser**

On sbt `core/fastOptJS::webpack`

Open `file:///.../modules/02-core/index-dev.html` on your browser.

### **Open webapp on browser**

On sbt `webapp/fastOptJS::webpack`

Open `file:///.../modules/04-webapp/index-fastopt.html` on your browser.

## GRPC

For the GRPC and protubuf you need to have envoy runing.
`docker run --rm -ti --net=host -v $PWD/envoy.yaml:/etc/envoy/envoy.yaml envoyproxy/envoy:v1.17.0`

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
