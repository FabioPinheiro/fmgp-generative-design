package fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, Shape => _, _}
import typings.three.webGLRendererMod.WebGLRendererParameters

import fmgp.threejs._
import fmgp.threejs.extras.OrbitControls
import fmgp.geo._
import fmgp.Utils
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation._
import java.awt.geom.Dimension2D
import fmgp.{Object3DWarp, NoneWarp, GeoWarp, WorldWarp}
import org.scalajs.dom.raw.{Event, Element}

import js.{undefined => ^}
import org.scalajs.logging.Logger

object Global {
  var scene: Scene = _
  var animateFrameId: Option[Int] = None
  var modelToAnimate: Option[Object3D] = None
  var camera: Option[Camera] = None
  var controls: Option[OrbitControls] = None

}

object Log extends Logger {
  def log(level: org.scalajs.logging.Level, message: => String): Unit = println(s"[$level] $message")
  def trace(t: => Throwable): Unit = log(org.scalajs.logging.Level.Debug, t.toString)
}

object Main {

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      WebGLRendererParameters(antialias = true, alpha = true)
    )
    aux.setSize(dom.window.innerWidth * 1, dom.window.innerHeight * 0.9)
    aux
  }

  def main(args: Array[String]): Unit = {
    fmgp.Fabio.version()

    //mount(dom.document.body, mainDiv)
    val select = dom.document.createElement("select")
    select.id = "modelSelectId"
    val option0 = dom.document.createElement("option")
    val option1 = dom.document.createElement("option")
    val option2 = dom.document.createElement("option")
    val option3 = dom.document.createElement("option")
    option0.asInstanceOf[js.Dynamic].value = "0"
    option1.asInstanceOf[js.Dynamic].value = "1"
    option2.asInstanceOf[js.Dynamic].value = "2"
    option3.asInstanceOf[js.Dynamic].value = "3"
    option0.textContent = "WebSocketText"
    option1.textContent = "Cylinder"
    option2.textContent = "shapesDemo2D"
    option3.textContent = "atomiumWorld"
    select.appendChild(option0)
    select.appendChild(option1)
    select.appendChild(option2)
    select.appendChild(option3)

    val message = dom.document.createElement("div")
    message.textContent = "Hi this is a dome of the threejs scalajs facede : "
    message.appendChild(select)
    dom.document.body.appendChild(message)
    val textarea: Element = dom.document.createElement("textarea")
    var node: Option[Element] = None

    Websocket.newWebSocket("ws://127.0.0.1:8080/browser", Log, textarea)
    select.addEventListener(
      `type` = "change",
      listener = (e0: dom.Event) => {
        val selected = js.Dynamic.global.modelSelectId.value.asInstanceOf[String].toInt
        if (selected == 0) {
          node.foreach(dom.document.body.removeChild)
          node = Some(textarea)
          dom.document.body.appendChild(textarea)
        } else {
          node.foreach(dom.document.body.removeChild)
          node = Some(renderer.domElement)
          dom.document.body.appendChild(renderer.domElement)
        }
        init(selected)
      }
    )

    ()
  }

  def init(scenario: Int) = {
    println(s"### INIT scenario:'$scenario' ###")
    Global.scene = new Scene()
    val model: GeoWarp = scenario match {
      case 0 => NoneWarp()
      case 1 =>
        val boxGeom = new BoxGeometry(1, 1, 1, ^, ^, ^) //c.width, c.height c.depth
        val cylinderGeom = new CylinderGeometry(1.0, 1.0, 1.0, 32, ^, ^, ^, ^)
        val sphereGeom = new SphereGeometry(1.0, 32, 32, ^, ^, ^, ^)
        Object3DWarp(Dimensions.D3)
          .add(new Mesh(cylinderGeom, new MeshPhongMaterial()).asInstanceOf[Object3D])
      // obj.scale.set(c.width, c.height, c.depth)
      case 2 => WorldWarp(GeometryExamples.shapesDemo2D)
      case 3 => WorldWarp(GeometryExamples.atomiumWorld)
    }

    val dimensions: Dimensions.D = Dimensions.D3

    Global.camera = Some(
      Utils.newCamera(dom.window.innerWidth, dom.window.innerHeight)
    )
    Global.camera.foreach { c =>
      c.position.set(0, 0, 10)
      c.lookAt(new Vector3(0, 0, 0))
    }
    Global.controls = Global.camera.map { c =>
      val aux = new OrbitControls(c, renderer.domElement)
      dimensions match {
        case Dimensions.D2 =>
          aux.enableRotate = false
          aux.screenSpacePanning = true
        case Dimensions.D3 =>
          Global.modelToAnimate = if (model.dimensions.isD3) model.generateObj3D else None
      }
      aux.keyPanSpeed = 30 //pixes
      aux.panSpeed = 3
      aux
    }

    model.generateObj3D.foreach { modelObj3D =>
      Global.scene.add(Global.modelToAnimate.getOrElse(modelObj3D))
      Global.scene.add(Utils.computeStaticThreeObjects)
    }

    if (Global.animateFrameId.isEmpty) animate(1)

  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    Global.modelToAnimate.foreach(Utils.updateFunction _)
    Global.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    Global.controls.foreach(_.update) // required if controls.enableDamping or controls.autoRotate are set to true
    Global.camera.foreach(renderer.render(Global.scene, _))
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
  var test2: Element = _

  /** (new $c_Lfmgp_Fabio$).f() */
  @JSExport
  def f() = {
    println("F:")
    test
  }

  @JSExport
  def version(): Unit = {
    val version = "FIXME Three.REVISION"
    dom.console.log(s"The threejs version is $version")
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
