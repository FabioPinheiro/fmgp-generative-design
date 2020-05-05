package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, Shape => _, _}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.statsJs.mod.{^ => Stats}

import app.fmgp.threejs._
import app.fmgp.threejs.extras.{FirstPersonControls, FlyControls, OrbitControls}
import app.fmgp.geo._
import app.fmgp.Utils
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation._
import java.awt.geom.Dimension2D
import app.fmgp.{Object3DWarp, GeoWarp, WorldWarp, DynamicWorldWarp}
import org.scalajs.dom.raw.{Event, Element}

import js.{undefined => ^}
import org.scalajs.logging.Logger

object Global {
  val debugUI = true
  val wsURL = "ws://127.0.0.1:8888/browser"
  var scene: Scene = _
  var sceneUI: Scene = _
  var animateFrameId: Option[Int] = None
  var modelToAnimate: () => Option[Object3D] = () => None
  var camera: Option[Camera] = None
  var cameraUI: Option[Camera] = None
  var controls: Option[FlyControls] = None
  var stats: Stats = new Stats()

  stats.showPanel(0); // 0: fps, 1: ms, 2: mb, 3+: custom
  stats.dom.style.right = "0px"
  stats.dom.style.left = null
  dom.document.body.appendChild(stats.dom)
}

object Log extends Logger {
  def log(level: org.scalajs.logging.Level, message: => String): Unit = println(s"[$level] $message")
  def trace(t: => Throwable): Unit = log(org.scalajs.logging.Level.Debug, t.toString)
}

object Main {

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      WebGLRendererParameters(
        antialias = true,
        alpha = true,
        //context = org.scalajs.dom.raw.WebGLRenderingContext.WEBGL2
      )
    )
    aux.setSize(dom.window.innerWidth, dom.window.innerHeight)
    aux.autoClear = false //For multi scene
    aux
  }

  val masterWorld = DynamicWorldWarp()

  def main(args: Array[String]): Unit = {
    app.fmgp.Fabio.version()

    //mount(dom.document.body, mainDiv)
    // val select = dom.document.createElement("select")
    // select.id = "modelSelectId"
    // val option0 = dom.document.createElement("option")
    // val option1 = dom.document.createElement("option")
    // val option2 = dom.document.createElement("option")
    // val option3 = dom.document.createElement("option")
    // val option4 = dom.document.createElement("option")
    // option0.asInstanceOf[js.Dynamic].value = "0"
    // option1.asInstanceOf[js.Dynamic].value = "1"
    // option2.asInstanceOf[js.Dynamic].value = "2"
    // option3.asInstanceOf[js.Dynamic].value = "3"
    // option4.asInstanceOf[js.Dynamic].value = "4"
    // option0.textContent = "WebSocketText"
    // option1.textContent = "Cylinder"
    // option2.textContent = "shapesDemo2D"
    // option3.textContent = "atomiumWorld"
    // option4.textContent = "WebSocket"
    // select.appendChild(option0)
    // select.appendChild(option4)
    // select.appendChild(option1)
    // select.appendChild(option2)
    // select.appendChild(option3)

    // val message = dom.document.createElement("div")
    // message.appendChild(select)
    // dom.document.body.appendChild(message)
    // val textarea: Element = dom.document.createElement("textarea")
    // //var node: Option[Element] = None
    //var node: Option[Element] = Some(renderer.domElement)
    dom.document.body.appendChild(renderer.domElement)
    renderer.domElement.style = "position: fixed; top: 0px; left: 0px;"

    Websocket.AutoReconnect(Global.wsURL, Log, masterWorld)
    // select.addEventListener(
    //   `type` = "change",
    //   listener = (e0: dom.Event) => {
    //     val selected = js.Dynamic.global.modelSelectId.value.asInstanceOf[String].toInt
    //     if (selected == 0) {
    //       node.foreach(dom.document.body.removeChild)
    //       node = Some(textarea)
    //       dom.document.body.appendChild(textarea)
    //     } else {
    //node.foreach(dom.document.body.removeChild)
    //node = Some(renderer.domElement)
    //dom.document.body.appendChild(renderer.domElement)
    //     }
    //     init(selected)
    //   }
    // )

    init(4) //WebSocket
    ()
  }

  def init(scenario: Int) = {
    Log.info(s"### INIT scenario:'$scenario' ###")
    if (scenario == 0) {
      Log.info(s"This scenario have no geometry model")
    } else {
      Global.sceneUI = new Scene()
      Global.scene = new Scene()

      val model: GeoWarp = masterWorld
      // scenario match {
      //   case 1 =>
      //     val boxGeom = new BoxGeometry(1, 1, 1, ^, ^, ^) //c.width, c.height c.depth
      //     val cylinderGeom = new CylinderGeometry(1.0, 1.0, 1.0, 32, ^, ^, ^, ^)
      //     val sphereGeom = new SphereGeometry(1.0, 32, 32, ^, ^, ^, ^)
      //     Object3DWarp(Dimensions.D3)
      //       .add(new Mesh(cylinderGeom, new MeshPhongMaterial()).asInstanceOf[Object3D])
      //   // obj.scale.set(c.width, c.height, c.depth)
      //   case 2 => WorldWarp(GeometryExamples.shapesDemo2D)
      //   case 3 => WorldWarp(GeometryExamples.atomiumWorld)
      //   case 4 => masterWorld
      // }

      val dimensions: Dimensions.D = Dimensions.D3

      Global.camera = Some(
        Utils.newCamera(dom.window.innerWidth, dom.window.innerHeight)
      )
      Global.camera.foreach { c =>
        c.position.set(0, 0, 10)
        c.lookAt(new Vector3(0, 0, 0))
      }
      Global.controls = Global.camera.map { c =>
        //val orbit = new OrbitControls(c, renderer.domElement)
        //orbit.keyPanSpeed = 30 //pixes
        //orbit.panSpeed = 3
        val fly = new FlyControls(c, renderer.domElement)
        fly.dragToLook = true
        fly.rollSpeed = 0.015
        fly.movementSpeed = 0.3
        //val firstPerson = new FirstPersonControls(c, renderer.domElement)
        //firstPerson.lookSpeed = 0.01
        //firstPerson.movementSpeed = 0.5
        dimensions match {
          case Dimensions.D2 =>
          //orbit.enableRotate = false
          //orbit.screenSpacePanning = true
          case Dimensions.D3 =>
            Global.modelToAnimate = if (model.dimensions.isD3) { () => Some(model.generateObj3D) }
            else () => None
        }
        fly //orbit //firstPerson
      }

      Global.scene.add(Global.modelToAnimate().getOrElse(model.generateObj3D))

      Global.cameraUI = {
        def addToScene(obj: typings.three.object3DMod.Object3D) = {
          if (Global.debugUI) Global.scene.add(obj.clone(true))
          Global.sceneUI.add(obj)
        }

        val proportions = dom.window.innerHeight / dom.window.innerWidth
        val ui =
          new OrthographicCamera(left = 0, right = 1, top = 0, bottom = -proportions, near = 0, far = 3)
            .asInstanceOf[Camera]

        Global.scene.add((new CameraHelper(ui)))

        ui.position.set(0, 0, 0)
        ui.lookAt(new Vector3(0, 0, -1))
        addToScene(new typings.three.mod.Mesh(new CircleGeometry(0.05, 32)).translateZ(-1))
        val loader = new FontLoader()
        loader.load(
          "https://raw.githubusercontent.com/mrdoob/three.js/dev/examples/fonts/gentilis_regular.typeface.json", //"fonts/helvetiker_bold.typeface.json",
          (f: typings.three.fontMod.Font) => {
            val textParameters = typings.three.textGeometryMod.TextGeometryParameters(
              font = f,
              size = 0.02,
              height = 0,
              curveSegments = 12,
            )
            val geometry = new TextBufferGeometry(Global.wsURL, textParameters).translate(0.01, -0.02, 0)
            val basicMarerial = new typings.three.meshBasicMaterialMod.MeshBasicMaterial()
            basicMarerial.color = new typings.three.colorMod.Color(0x000000)
            addToScene(new typings.three.mod.Mesh(geometry, basicMarerial))

          }
        )

        Some(ui)
      }
      Global.scene.add(Utils.computeStaticThreeObjects)

      if (Global.animateFrameId.isEmpty) animate(1)
    }
  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    Global.stats.begin();
    //Global.modelToAnimate().foreach(Utils.updateFunction _)
    Global.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    Global.controls.foreach(_.update(1)) // required if controls.enableDamping or controls.autoRotate are set to true
    Global.camera.foreach(renderer.render(Global.scene, _))
    Global.cameraUI.foreach(renderer.render(Global.sceneUI, _))
    Global.stats.end();
  }
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

trait FooOptions extends js.Object {
  val a: Int
  val b: String
  val c: js.UndefOr[Boolean]
}
@JSExportTopLevel("Fabio")
object Fabio {

  /** $m_Lfmgp_Fabio$().test */
  @JSExport
  var test: Event = _
  @JSExport
  var any: Any = _

  /** (new $c_Lfmgp_Fabio$).f() */
  @JSExport
  def f() = {
    println("F:")
    test
  }

  @JSExport
  def version(): Unit = {
    dom.console.log(s"The threejs version is 0.108.0")
  }

  @JSExport
  def foo(options: FooOptions): String = {
    val a = options.a
    val b = options.b
    val c = options.c.getOrElse(false)
    // do something with a, b, c
    s"$a $b $c"
  }
}
