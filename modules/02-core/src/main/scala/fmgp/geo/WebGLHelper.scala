package fmgp.geo

import scala.util.chaining._
// /import scala.math.Ordered
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{Y => AnonY}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.statsJs.mod.{^ => Stats}

import fmgp.{Log, Websocket, Utils}

import _root_.fmgp.Websocket
import fmgp.WebsocketJSLive
import typings.three.textGeometryMod.TextGeometry
import typings.three.eventDispatcherMod.Event
import typings.three.raycasterMod.Intersection
import typings.three.flyControlsMod.FlyControls
import scala.util.Random
import typings.std.KeyboardEvent

import zio._
import zio.stream._
import zio.Console._

case class InteractiveMesh(mesh: typings.three.meshMod.Mesh[_, _], onSelected: () => Unit = () => ()) {
  def id = mesh.id
}

@JSExportTopLevel("WebGLHelper")
object WebGLHelper {
  // WebGLHelper.test[2].object.material.color.set(0x00ff00)
  // @JSExport var test: js.Any = _
  // @JSExport var test2: js.Any = _
}
class WebGLHelper(topPadding: Int, modelToAnimate: Group = new Group) {

  val webGLGlobal = new WebGLGlobal
  var uiEvent: Option[AnonY] = None

  def height = -5 + dom.window.innerHeight - topPadding
  def width = dom.window.innerWidth

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      js.Dynamic.literal().asInstanceOf[WebGLRendererParameters].pipe { o =>
        o.antialias = true
        o.alpha = true
        // o.context = org.scalajs.dom.raw.WebGLRenderingContext.WEBGL2
        o
      }
    )
    aux.domElement.addEventListener("click", ev => onClickEvent(ev))
    aux.domElement.addEventListener("ontouch", ev => onTouchEvent(ev))
    aux.setSize(width, height)
    aux.autoClear = false // For multi scene
    aux
  }

  def onClickEvent(event: dom.MouseEvent) = {
    // calculate mouse position in normalized device coordinates (-1 to +1) for both components
    val x = (event.clientX / width) * 2 - 1
    val y = -((event.clientY - topPadding) / height) * 2 + 1
    this.uiEvent = Some(AnonY(x, y))
  }
  def onTouchEvent(event: dom.TouchEvent) = {
    val x = (event.touches(0).screenX / width) * 2 - 1
    val y = -((event.touches(0).screenY - topPadding) / height) * 2 + 1
    this.uiEvent = Some(AnonY(x, y))
  }

  /** Must be call after init */
  def append(parent: org.scalajs.dom.raw.HTMLElement) =
    parent.appendChild(renderer.domElement)

  def init = {
    Log.info(s"### INIT ###")
    KeyboardUtils.app
    webGLGlobal.sceneUI = new Scene()
    webGLGlobal.scene = new Scene()
    WebGLTextGlobal.scene = webGLGlobal.scene // TODO REMOVE

    val dimensions: Dimensions = Dimensions.D3

    webGLGlobal.controls = Some {
      val fly = new FlyControls(KeyboardUtils.hack.camera2, renderer.domElement)
      fly.dragToLook = true
      fly.rollSpeed = 0.015
      fly.movementSpeed = 0.3
      fly
    }

    val cameraHelper = new CameraHelper(KeyboardUtils.hack.camera1)
    webGLGlobal.scene.add(cameraHelper)
    webGLGlobal.scene.add(modelToAnimate)
    webGLGlobal.cameraUI = {

      val proportions /*aspect*/ = height / width
      val ui =
        new OrthographicCamera(left = 0, right = 1, top = 0, bottom = -proportions, near = 0, far = 3)
          .asInstanceOf[Camera]

      if (webGLGlobal.debugUI) webGLGlobal.scene.add((new CameraHelper(ui)))

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
      WebsocketJSLive.onStateChange = updateColor _

      webGLGlobal.addUiElement(InteractiveMesh(mesh, () => material.color = new typings.three.colorMod.Color("blue")))
    }
    { // text WS URL
      val textParameters = js.Dynamic.literal().asInstanceOf[typings.three.textGeometryMod.TextGeometryParameters]
      textParameters.font = WebGLTextGlobal.textFont
      textParameters.size = 0.02
      textParameters.height = 0
      textParameters.curveSegments = 12

      val geometry = new TextGeometry(WebsocketJSLive.wsUrl, textParameters).translate(0.01, -0.02, 0)
      val basicMarerial = new typings.three.meshBasicMaterialMod.MeshBasicMaterial()
      basicMarerial.color = new typings.three.colorMod.Color(0x444444)
      val mesh = new typings.three.mod.Mesh(geometry, basicMarerial)
      webGLGlobal.addUiElement(InteractiveMesh(mesh))
    }

    /*
    { //Examples //TODO REMOVE
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
          case 0 => webGLGlobal.masterWorld.update(World.w3DEmpty)
          case 1 => webGLGlobal.masterWorld.update(WorldWarp(GeometryExamples.atomiumWorld).world)
          case 2 => webGLGlobal.masterWorld.update(WorldWarp(GeometryExamples.shapesDemo2D).world)
          case 3 => webGLGlobal.masterWorld.update(World.w3D(Seq(TestShape())))
          case 4 => webGLGlobal.masterWorld.update(World.w3D(Seq(Wireframe(TestShape()))))
        }
      }
      aux
        .map { case (mesh, material, index) => InteractiveMesh(mesh, update(mesh.id.toInt, index) _) }
        .map(webGLGlobal.addUiElement _)
    }
     */

    webGLGlobal.scene.add(Utils.computeStaticThreeObjects)
    if (webGLGlobal.animateFrameId.isEmpty) animate(1)

  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    StatsComponent.stats.begin()
    KeyboardUtils.appCamera

    this.uiEvent.foreach { mouseEvent =>
      val ray = webGLGlobal.raycaster.ray
      val dir = ray.direction.tap(_.normalize())
      val arrowHelper = new ArrowHelper(dir, ray.origin, 10, 0x550055, headLength = 0.5, headWidth = 0.05);
      webGLGlobal.scene.add(arrowHelper);

      val intersects: js.Array[Intersection[typings.three.object3DMod.Object3D[Event]]] =
        webGLGlobal.raycaster
          // update the picking ray with the camera and mouse position
          .tap(_.setFromCamera(mouseEvent, KeyboardUtils.hack.camera))
          // calculate objects intersecting the picking ray
          .intersectObjects(webGLGlobal.scene.children, true)

      webGLGlobal.scene.raycast(webGLGlobal.raycaster, intersects)

      // REMOVE WebGLHelper.test = intersects
      // TODO val color = Random.between(0, 0xffffff)
      // TODO intersects.foreach(_.`object`.asInstanceOf[js.Dynamic].material.color.set(color))
      val ids = intersects.map(o => s"${o.`object`.id}").mkString("[", ", ", "]")
      println(s"UI EVENT! intersects (${intersects.size}) : $ids")
    }
    this.uiEvent = None

    // webGLGlobal.modelToAnimate().foreach(Utils.updateFunction _)
    webGLGlobal.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    webGLGlobal.controls.foreach(_.update(1))
    // required if controls.enableDamping or controls.autoRotate are set to true

    renderer.render(webGLGlobal.scene, KeyboardUtils.hack.camera)
    renderer.clearDepth()
    webGLGlobal.cameraUI.foreach(renderer.render(webGLGlobal.sceneUI, _))

    StatsComponent.stats.end()
  }
}
