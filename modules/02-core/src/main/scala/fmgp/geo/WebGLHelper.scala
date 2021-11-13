package fmgp.geo

import scala.util.chaining._
// /import scala.math.Ordered
import scala.scalajs.js
import scala.scalajs.js.annotation._
import org.scalajs.dom

import typings.three.loaderMod.Loader
import typings.three.mod.{Shape => _, _}
import typings.three.anon.{X => AnonX}
import typings.three.webGLRendererMod.WebGLRendererParameters
import typings.statsJs.mod.{^ => Stats}

import fmgp.threejs._
import fmgp.threejs.extras.{FirstPersonControls, FlyControls, OrbitControls}
import fmgp.{Log, Websocket, Utils}

import _root_.fmgp.Websocket
import fmgp.WebsocketJSLive

case class InteractiveMesh(mesh: typings.three.meshMod.Mesh[_, _], onSelected: () => Unit = () => ()) {
  def id = mesh.id
}

class WebGLHelper(topPadding: Int, modelToAnimate: Object3D = new Object3D()) {

  val webGLGlobal = new WebGLGlobal
  var uiEvent: Option[AnonX] = None

  def height = -5 + dom.window.innerHeight - topPadding
  def width = dom.window.innerWidth

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      js.Dynamic.literal().asInstanceOf[WebGLRendererParameters].pipe { o =>
        o.antialias = true
        o.alpha = true
        //o.context = org.scalajs.dom.raw.WebGLRenderingContext.WEBGL2
        o
      }
    )
    aux.domElement.addEventListener("click", ev => onClickEvent(ev))
    aux.domElement.addEventListener("ontouch", ev => onTouchEvent(ev))
    aux.setSize(width, height)
    aux.autoClear = false //For multi scene
    aux
  }

  def onClickEvent(event: dom.MouseEvent) = {
    // calculate mouse position in normalized device coordinates (-1 to +1) for both components
    val x = (event.clientX / dom.window.innerWidth) * 2 - 1
    val y = -((event.clientY - topPadding) / dom.window.innerHeight) * 2 + 1
    this.uiEvent = Some(AnonX(x, y))
  }
  def onTouchEvent(event: dom.TouchEvent) = {
    val x = (event.touches(0).screenX / dom.window.innerWidth) * 2 - 1
    val y = -((event.touches(0).screenY - topPadding) / dom.window.innerHeight) * 2 + 1
    this.uiEvent = Some(AnonX(x, y))
  }

  /** Must be call after init */
  def append(parent: org.scalajs.dom.raw.HTMLElement) =
    parent.appendChild(renderer.domElement)

  def init = {
    Log.info(s"### INIT ###")
    webGLGlobal.sceneUI = new Scene()
    webGLGlobal.scene = new Scene()

    val dimensions: Dimensions = Dimensions.D3

    webGLGlobal.camera = Some(
      Utils
        .newCamera(width, height)
        .tap(_.position.set(0, 0, 10))
        .tap(_.lookAt(new Vector3(0, 0, 0)))
    )
    webGLGlobal.controls = webGLGlobal.camera.map { c =>
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
      // dimensions match {
      //   case Dimensions.D2 =>
      //   //orbit.enableRotate = false
      //   //orbit.screenSpacePanning = true
      //   case Dimensions.D3 =>
      //     //######################################################################
      //     if (webGLGlobal.masterWorld.dimensions.isD3) (() => Some(webGLGlobal.masterWorld.generateObj3D))
      //     else () => None
      // }
      fly //orbit //firstPerson
    }

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
    { //text WS URL
      val textParameters = js.Dynamic.literal().asInstanceOf[typings.three.textGeometryMod.TextGeometryParameters]
      textParameters.font = WebGLTextGlobal.textFont
      textParameters.size = 0.02
      textParameters.height = 0
      textParameters.curveSegments = 12

      val geometry = new TextBufferGeometry(WebsocketJSLive.wsUrl, textParameters).translate(0.01, -0.02, 0)
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

    this.uiEvent.foreach { event =>
      val intersects = webGLGlobal.raycaster
        .tap(_.setFromCamera(event, webGLGlobal.cameraUI.get))
        .intersectObjects(webGLGlobal.sceneUI.children, true)
      webGLGlobal.scene.raycast(webGLGlobal.raycaster, intersects)
      intersects
        .map(_.`object`.id.toInt)
        .toSet
        .tap(e => println(s"click on: $e"))
        .map { (id: Int) => webGLGlobal.uiElements.get(id).map(_.onSelected()) }
    }
    this.uiEvent = None

    //webGLGlobal.modelToAnimate().foreach(Utils.updateFunction _)
    webGLGlobal.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    webGLGlobal.controls.foreach(
      _.update(1)
    ) // required if controls.enableDamping or controls.autoRotate are set to true
    webGLGlobal.camera.foreach(renderer.render(webGLGlobal.scene, _))
    renderer.clearDepth()
    webGLGlobal.cameraUI.foreach(renderer.render(webGLGlobal.sceneUI, _))

    StatsComponent.stats.end()
  }
}
