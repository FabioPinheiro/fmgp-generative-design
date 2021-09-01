package app.fmgp.geo

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

import app.fmgp.threejs._
import app.fmgp.threejs.extras.{FirstPersonControls, FlyControls, OrbitControls}
import app.fmgp.{Log, Websocket, Utils}

import _root_.app.fmgp.Websocket
case class InteractiveMesh(mesh: typings.three.meshMod.Mesh[_, _], onSelected: () => Unit = () => ()) {
  def id = mesh.id
}

class WebGLHelper(topPadding: Int) {

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
    aux.setSize(width, height)
    aux.autoClear = false //For multi scene
    aux
  }

  def init = {
    Log.info(s"### INIT ###")
    WebGLGlobal.sceneUI = new Scene()
    WebGLGlobal.scene = new Scene()

    val dimensions: Dimensions = Dimensions.D3

    WebGLGlobal.camera = Some(
      Utils
        .newCamera(width, height)
        .tap(_.position.set(0, 0, 10))
        .tap(_.lookAt(new Vector3(0, 0, 0)))
    )
    WebGLGlobal.controls = WebGLGlobal.camera.map { c =>
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
          WebGLGlobal.modelToAnimate = if (WebGLGlobal.masterWorld.dimensions.isD3) { () =>
            Some(WebGLGlobal.masterWorld.generateObj3D)
          } else () => None
      }
      fly //orbit //firstPerson
    }

    WebGLGlobal.scene.add(WebGLGlobal.modelToAnimate().getOrElse(WebGLGlobal.masterWorld.generateObj3D))

    WebGLGlobal.cameraUI = {

      val proportions /*aspect*/ = height / width
      val ui =
        new OrthographicCamera(left = 0, right = 1, top = 0, bottom = -proportions, near = 0, far = 3)
          .asInstanceOf[Camera]

      if (WebGLGlobal.debugUI) WebGLGlobal.scene.add((new CameraHelper(ui)))

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
      WebGLGlobal.websocket.onStateChange = updateColor _

      WebGLGlobal.addUiElement(InteractiveMesh(mesh, () => material.color = new typings.three.colorMod.Color("blue")))
    }
    { //text WS URL
      val textParameters = js.Dynamic.literal().asInstanceOf[typings.three.textGeometryMod.TextGeometryParameters]
      textParameters.font = WebGLGlobal.textFont
      textParameters.size = 0.02
      textParameters.height = 0
      textParameters.curveSegments = 12

      val geometry = new TextBufferGeometry(WebGLGlobal.websocket.wsUrl, textParameters).translate(0.01, -0.02, 0)
      val basicMarerial = new typings.three.meshBasicMaterialMod.MeshBasicMaterial()
      basicMarerial.color = new typings.three.colorMod.Color(0x444444)
      val mesh = new typings.three.mod.Mesh(geometry, basicMarerial)
      WebGLGlobal.addUiElement(InteractiveMesh(mesh))
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
          case 0 => WebGLGlobal.masterWorld.update(World.w3DEmpty)
          case 1 => WebGLGlobal.masterWorld.update(WorldWarp(GeometryExamples.atomiumWorld).world)
          case 2 => WebGLGlobal.masterWorld.update(WorldWarp(GeometryExamples.shapesDemo2D).world)
          case 3 => WebGLGlobal.masterWorld.update(World.w3D(Seq(TestShape())))
          case 4 => WebGLGlobal.masterWorld.update(World.w3D(Seq(Wireframe(TestShape()))))
        }
      }
      aux
        .map { case (mesh, material, index) => InteractiveMesh(mesh, update(mesh.id.toInt, index) _) }
        .map(WebGLGlobal.addUiElement _)
    }

    WebGLGlobal.scene.add(Utils.computeStaticThreeObjects)
    if (WebGLGlobal.animateFrameId.isEmpty) animate(1)

  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    WebGLGlobal.stats.begin()

    WebGLGlobal.uiEvent.foreach { event =>
      val intersects = WebGLGlobal.raycaster
        .tap(_.setFromCamera(event, WebGLGlobal.cameraUI.get))
        .intersectObjects(WebGLGlobal.sceneUI.children, true)
      WebGLGlobal.scene.raycast(WebGLGlobal.raycaster, intersects)
      intersects
        .map(_.`object`.id.toInt)
        .toSet
        .tap(e => println(s"click on: $e"))
        .map { (id: Int) => WebGLGlobal.uiElements.get(id).map(_.onSelected()) }
    }
    WebGLGlobal.uiEvent = None

    //WebGLGlobal.modelToAnimate().foreach(Utils.updateFunction _)
    WebGLGlobal.animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    WebGLGlobal.controls.foreach(
      _.update(1)
    ) // required if controls.enableDamping or controls.autoRotate are set to true
    WebGLGlobal.camera.foreach(renderer.render(WebGLGlobal.scene, _))
    renderer.clearDepth()
    WebGLGlobal.cameraUI.foreach(renderer.render(WebGLGlobal.sceneUI, _))

    WebGLGlobal.stats.end()
  }
}
