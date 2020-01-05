package fmgp.threejs

import fmgp.threejs._
import fmgp.threejs.extras.OrbitControls
import org.scalajs.dom
import scala.scalajs.js
import scala.scalajs.js.annotation._

object Demo { //extends JSApp {

  lazy val renderer: WebGLRenderer = {
    val aux = new WebGLRenderer(
      WebGLRendererParameters(antialias = true, alpha = true)
    )
    aux.setSize(dom.window.innerWidth * 0.9, dom.window.innerHeight * 0.9)
    aux
  }

  def main(args: Array[String]): Unit = {
    //mount(dom.document.body, mainDiv)
    val message = dom.document.createElement("div")
    message.textContent = "Hi this is a dome of the threejs scalajs facede"
    dom.document.body.appendChild(message)
    dom.document.body.appendChild(renderer.domElement)
    init(0)
    ()
  }

  var scene: Scene = _
  var animateFrameId: Option[Int] = None
  var modelToAnimate: Option[Object3D] = None
  var camera: Option[Camera] = None
  var controls: Option[OrbitControls] = None

  def computeStaticThreeObjects = {
    // create lights
    val sunLight = new DirectionalLight(0xffffca, 0.5)
    sunLight.position.set(0.2, 1, 0.3)
    val belowLight = new DirectionalLight(0xffffca, 0.1)
    belowLight.position.set(-0.1, -1, -0.4)
    val hemiLight = new HemisphereLight(0xffffbb, 0x080820, 0.4)

    // create global coordinates helpers
    val axisHelper = new AxesHelper(8)
    val gridHelper = new GridHelper(100, 100)
    (proto.Dimensions.D3: proto.Dimensions.D) match {
      case proto.Dimensions.D2 => gridHelper.rotateX(Math.PI / 2.0)
      case proto.Dimensions.D3 => //ok as it is
      case proto.Dimensions.Unrecognized(d) =>
        println(s"Dimensions $d Unrecognized in computeStaticThreeObjects")
    }

    val staticRoot = new Object3D()
    Seq(sunLight, belowLight, hemiLight, gridHelper, axisHelper).foreach(
      staticRoot.add
    )
    staticRoot
  }

  def newCamera(
      width: Double,
      height: Double,
      size: Double = 30,
      near: Double = 1,
      far: Double = 500
  ): Camera =
    (proto.Dimensions.D3: proto.Dimensions.D) match {
      case proto.Dimensions.D2 =>
        val proportions = width / height
        new OrthographicCamera(
          -size / 2 * proportions,
          size / 2 * proportions,
          size / 2,
          -size / 2,
          near,
          far
        )
      case proto.Dimensions.D3 =>
        new PerspectiveCamera(45, width / height, near, far)
      case proto.Dimensions.Unrecognized(d) =>
        throw new RuntimeException(s"Dimensions $d Unrecognized in newCamera")
    }

  def init(scenario: Int) = {
    scene = new Scene()
    println(s"Init scenario $scenario")
    val worldToRender: Option[Object3D] = scenario match {
      // case 0 => GeometryExamples.worldFromWS
      // case 1 => Some(GeometryExamples.shapesDemo2D)
      // case 2 => Some(GeometryExamples.shapesDemo)
      // case 3 => Some(GeometryExamples.atomiumModel)
      case 0 =>
        val boxGeom = new BoxGeometry(1, 1, 1) //c.width, c.height, c.depth)
        val cylinderGeom = new CylinderGeometry(1.0, 1.0, 1.0, 32)
        val sphereGeom = new SphereGeometry(1.0, 32, 32)
        val obj = new Mesh(boxGeom, new MeshPhongMaterial())
        // obj.scale.set(c.width, c.height, c.depth)
        val parent = new Object3D
        parent.add(obj)
        Some(parent)
      case _ => None
    }
    worldToRender.foreach { w =>
      val model = w //w.modelObject3D
      //modelToAnimate = true //if (w.dimensions.isD3) Some(model) else None
      camera = Some(newCamera(dom.window.innerWidth, dom.window.innerHeight))
      camera.foreach { c =>
        c.position.set(0, 0, 10)
        c.lookAt(new Vector3(0, 0, 0))
      }
      controls = camera.map { c =>
        val aux = new OrbitControls(c, renderer.domElement)
        //if (w.dimensions.isD2) {
        //  aux.enableRotate = false
        //  aux.screenSpacePanning = true
        //}
        aux.keyPanSpeed = 30 //pixes
        aux.panSpeed = 3
        aux
      }
      scene.add(model)
      scene.add(computeStaticThreeObjects)
    }
    animate(1)
  }

  def updateFunction = modelToAnimate.map { model =>
    //println(s"updateFunction ${matrix42String(model.matrix)}")
    model.rotation.x += 0.02
    model.rotation.y += 0.01
  }

  @JSExport
  val animate: js.Function1[Double, Unit] = (d: Double) => {
    updateFunction
    animateFrameId = Some(dom.window.requestAnimationFrame(animate))
    controls.foreach(_.update) // required if controls.enableDamping or controls.autoRotate are set to true

    camera.foreach(renderer.render(scene, _))
  }
}

object proto {
  object Dimensions {
    trait D
    object D2 extends D
    object D3 extends D
    case class Unrecognized(i: Int) extends D
  }
}
