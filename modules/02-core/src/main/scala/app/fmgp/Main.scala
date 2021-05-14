package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
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

import scala.util.chaining._

object Global {
  val masterWorld = DynamicWorldWarp()
  var websocket = Websocket.AutoReconnect("ws://127.0.0.1:8888/browser", Log, masterWorld)
  val debugUI = false
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

  val uiElements: scala.collection.mutable.HashMap[Int, InteractiveMesh] = scala.collection.mutable.HashMap.empty
  def addUiElement(o: InteractiveMesh) = {
    uiElements.put(o.id.toInt, o)
    sceneUI.add(o.mesh); if (debugUI) scene.add(o.mesh.clone(true))
  }

  val raycaster = new Raycaster()
  var uiEvent: Option[AnonX] = None

  // Text
  var textFont: typings.three.fontMod.Font = _
  val loader = new FontLoader()
  def init = Log.info(s"### Global.init ###") //FIXME
  loader.load(
    "https://raw.githubusercontent.com/mrdoob/three.js/dev/examples/fonts/gentilis_regular.typeface.json", //"fonts/helvetiker_bold.typeface.json",
    (f: typings.three.fontMod.Font) => textFont = f
  )

}

object Log extends Logger {
  def log(level: org.scalajs.logging.Level, message: => String): Unit = println(s"[$level] $message")
  def trace(t: => Throwable): Unit = log(org.scalajs.logging.Level.Debug, t.toString)
}

case class InteractiveMesh(mesh: typings.three.meshMod.Mesh[_, _], onSelected: () => Unit = () => ()) {
  def id = mesh.id
}

object Main {
  def onClickEvent(event: dom.MouseEvent) = {
    // calculate mouse position in normalized device coordinates (-1 to +1) for both components
    val x = (event.clientX / dom.window.innerWidth) * 2 - 1
    val y = -(event.clientY / dom.window.innerHeight) * 2 + 1
    Global.uiEvent = Some(AnonX(x, y))
  }
  def onTouchEvent(event: dom.TouchEvent) = {
    val x = (event.touches(0).screenX / dom.window.innerWidth) * 2 - 1
    val y = -(event.touches(0).screenY / dom.window.innerHeight) * 2 + 1
    Global.uiEvent = Some(AnonX(x, y))
  }
  dom.window.addEventListener("click", onClickEvent, false)
  dom.window.addEventListener("ontouch", onTouchEvent, false) //TODO need to test this

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      js.Dynamic.literal().asInstanceOf[WebGLRendererParameters].pipe { o =>
        o.antialias = true
        o.alpha = true
        //o.context = org.scalajs.dom.raw.WebGLRenderingContext.WEBGL2
        o
      }
    )
    aux.setSize(dom.window.innerWidth, dom.window.innerHeight)
    aux.autoClear = false //For multi scene
    aux
  }

  def main(args: Array[String]): Unit = {
    Global.init
    dom.document.body.appendChild(renderer.domElement)
    renderer.domElement.style = "position: fixed; top: 0px; left: 0px;"
    js.timers.setTimeout(1000)(init) //milliseconds FIXME
    ()
  }

  def init = {
    Log.info(s"### INIT ###")
    Global.sceneUI = new Scene()
    Global.scene = new Scene()

    val dimensions: Dimensions.D = Dimensions.D3

    Global.camera = Some(
      Utils
        .newCamera(dom.window.innerWidth, dom.window.innerHeight)
        .tap(_.position.set(0, 0, 10))
        .tap(_.lookAt(new Vector3(0, 0, 0)))
    )
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
          Global.modelToAnimate = if (Global.masterWorld.dimensions.isD3) { () =>
            Some(Global.masterWorld.generateObj3D)
          } else () => None
      }
      fly //orbit //firstPerson
    }

    Global.scene.add(Global.modelToAnimate().getOrElse(Global.masterWorld.generateObj3D))

    Global.cameraUI = {

      val proportions /*aspect*/ = dom.window.innerHeight / dom.window.innerWidth
      val ui =
        new OrthographicCamera(left = 0, right = 1, top = 0, bottom = -proportions, near = 0, far = 3)
          .asInstanceOf[Camera]

      if (Global.debugUI) Global.scene.add((new CameraHelper(ui)))

      ui.position.set(0, 0, 0)
      ui.lookAt(new Vector3(0, 0, -1))
      Some(ui)
    }

    { // WS state
      val circle = new CircleBufferGeometry(0.05, 32)
      val material = new MeshBasicMaterial(typings.three.meshBasicMaterialMod.MeshBasicMaterialParameters())
      val mesh = new typings.three.mod.Mesh(circle, material).translateZ(-1)
      def updateColor(s: Websocket.State.State) = s match {
        case Websocket.State.CONNECTING => material.color = new typings.three.colorMod.Color("blue")
        case Websocket.State.OPEN       => material.color = new typings.three.colorMod.Color("green")
        case Websocket.State.CLOSING    => material.color = new typings.three.colorMod.Color("yellow")
        case Websocket.State.CLOSED     => material.color = new typings.three.colorMod.Color("red")
      }
      Global.websocket.onStateChange = updateColor _

      Global.addUiElement(InteractiveMesh(mesh, () => material.color = new typings.three.colorMod.Color("blue")))
    }
    { //text WS URL
      val textParameters = js.Dynamic.literal().asInstanceOf[typings.three.textGeometryMod.TextGeometryParameters]
      textParameters.font = Global.textFont
      textParameters.size = 0.02
      textParameters.height = 0
      textParameters.curveSegments = 12

      val geometry = new TextBufferGeometry(Global.websocket.wsUrl, textParameters).translate(0.01, -0.02, 0)
      val basicMarerial = new typings.three.meshBasicMaterialMod.MeshBasicMaterial()
      basicMarerial.color = new typings.three.colorMod.Color(0x444444)
      val mesh = new typings.three.mod.Mesh(geometry, basicMarerial)
      Global.addUiElement(InteractiveMesh(mesh))
    }
    { //Examples
      val bottonGeometry = new PlaneBufferGeometry(0.05, 0.05).translate(0.025, -0.025, 0)
      val materialParameters = typings.three.meshBasicMaterialMod.MeshBasicMaterialParameters()
      materialParameters.color_=(new typings.three.colorMod.Color(0x666666))
      val aux = (0 to 4)
        .map { i =>
          val material = new MeshBasicMaterial(materialParameters)
          val mesh = new typings.three.mod.Mesh(bottonGeometry, material)
            .translateZ(-1.1)
            .translateY(-0.07 - i * 0.05)
          (mesh, material, i)
        }
      def update(meshId: Int, scenario: Int)() = {
        aux.foreach { e =>
          if (meshId == e._1.id.toInt) e._2.color = new typings.three.colorMod.Color("blue")
          else e._2.color = new typings.three.colorMod.Color(0x666666)
        }
        scenario match {
          case 0 => Global.masterWorld.update(World.w3DEmpty)
          case 1 => Global.masterWorld.update(WorldWarp(GeometryExamples.atomiumWorld).world)
          case 2 => Global.masterWorld.update(WorldWarp(GeometryExamples.shapesDemo2D).world)
          case 3 => Global.masterWorld.update(World.w3D(Seq(TestShape())))
          case 4 => Global.masterWorld.update(World.w3D(Seq(Wireframe(TestShape()))))
        }
      }
      aux
        .map { case (mesh, material, index) => InteractiveMesh(mesh, update(mesh.id.toInt, index) _) }
        .map(Global.addUiElement _)
    }

    Global.scene.add(Utils.computeStaticThreeObjects)
    if (Global.animateFrameId.isEmpty) animate(1)

  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    Global.stats.begin()

    Global.uiEvent.foreach { event =>
      val intersects = Global.raycaster
        .tap(_.setFromCamera(event, Global.cameraUI.get))
        .intersectObjects(Global.sceneUI.children, true)
      Global.scene.raycast(Global.raycaster, intersects)
      intersects
        .map(_.`object`.id.toInt)
        .toSet
        .tap(e => println(s"click on: $e"))
        .map { id: Int => Global.uiElements.get(id).map(_.onSelected()) }
    }
    Global.uiEvent = None

    //Global.modelToAnimate().foreach(Utils.updateFunction _)
    Global.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    Global.controls.foreach(_.update(1)) // required if controls.enableDamping or controls.autoRotate are set to true
    Global.camera.foreach(renderer.render(Global.scene, _))
    renderer.clearDepth()
    Global.cameraUI.foreach(renderer.render(Global.sceneUI, _))

    Global.stats.end()
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
  def foo(options: FooOptions): String = {
    val a = options.a
    val b = options.b
    val c = options.c.getOrElse(false)
    // do something with a, b, c
    s"$a $b $c"
  }
}
