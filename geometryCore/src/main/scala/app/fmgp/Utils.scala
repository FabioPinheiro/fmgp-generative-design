package app.fmgp

import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, Shape => _, _}
import typings.three.webGLRendererMod.WebGLRendererParameters

import app.fmgp.threejs._
import app.fmgp.threejs.extras.OrbitControls
import app.fmgp.geo._

object Utils {
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
    (Dimensions.D3: Dimensions.D) match {
      case Dimensions.D2 => gridHelper.rotateX(Math.PI / 2.0)
      case Dimensions.D3 => //ok as it is
      // case Dimensions.Unrecognized(d) =>
      //   println(s"Dimensions $d Unrecognized in computeStaticThreeObjects")
    }

    val staticRoot = new Object3D()
    Seq(sunLight, belowLight, hemiLight, gridHelper, axisHelper).foreach(e => staticRoot.add(e))
    staticRoot
  }

  def newCamera(
      width: Double,
      height: Double,
      dimensions: Dimensions.D = Dimensions.D3,
      size: Double = 30,
      near: Double = 1,
      far: Double = 500
  ): Camera =
    (dimensions) match {
      case Dimensions.D2 =>
        val proportions = width / height
        val aux = size / 2 * proportions
        new OrthographicCamera(-aux, aux, size / 2, -size / 2, near, far).asInstanceOf[Camera]
      case Dimensions.D3 => new PerspectiveCamera(45, width / height, near, far).asInstanceOf[Camera]
      // case Dimensions.Unrecognized(d) =>
      //   throw new RuntimeException(s"Dimensions $d Unrecognized in newCamera")
    }

  // def updateFunction(model: Object3D): Object3D = {
  //   // println(
  //   //   s"updateFunction (${model.rotation.x}, ${model.rotation.y}, ${model.rotation.z})"
  //   // )
  //   // model.rotation.x += 0.02
  //   // model.rotation.y += 0.01
  //   model
  // }
}
