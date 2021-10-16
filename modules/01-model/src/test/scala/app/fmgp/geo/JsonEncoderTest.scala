package app.fmgp.geo

import app.fmgp.geo.TestUtils._
import app.fmgp.geo.EncoderDecoder.{given}

//geometryModelJVM/testOnly app.fmgp.geo.JsonEncoderDecodeTest
class JsonEncoderDecodeTest extends munit.FunSuite {
  test("Dimensions") {
    assertEncoder(Dimensions.D2, "\"D2\"")
    assertDecode[Dimensions]("\"D3\"", Dimensions.D3)
  }

  test("circe for Matrix") {
    val obj = Matrix(00d, 01d, 02d, 03d, 10d, 11d, 12d, 13d, 20d, 21d, 22d, 23d, 30d, 31d, 32d, 33d)
    //val str = """[0.0,1.0,2.0,3.0,10.0,11.0,12.0,13.0,20.0,21.0,22.0,23.0,30.0,31.0,32.0,33.0]"""
    val str = """[0,1,2,3,10,11,12,13,20,21,22,23,30,31,32,33]"""

    assertEncoder(obj, str)
    assertDecode(str, obj)
  }

  test("XYZ") {
    //assertEncoder(XYZ(1, 2, 3), """{"x":1.0,"y":2.0,"z":3.0}""")
    assertEncoder(XYZ(1, 2, 3), """{"x":1,"y":2,"z":3}""")
  }

  test("Generic-Coordinate") {
    val objCoordinateXYZ: Coordinate = XYZ(1, 2, 3)
    //val strCoordinateXYZ = """{"XYZ":{"x":1.0,"y":2.0,"z":3.0}}"""
    val strCoordinateXYZ = """{"XYZ":{"x":1,"y":2,"z":3}}"""
    val objCoordinates = Seq[Coordinate](XYZ(1, 2, 3), Polar(1, 2))
    //val strCoordinates = """[{"XYZ":{"x":1.0,"y":2.0,"z":3.0}},{"Polar":{"rho":1.0,"phi":2.0}}]"""
    val strCoordinates = """[{"XYZ":{"x":1,"y":2,"z":3}},{"Polar":{"rho":1,"phi":2}}]"""

    val objCoordinates3D: Coordinate3D = (XYZ(1, 2, 3))
    //val strCoordinates3D = """{"XYZ":{"x":1.0,"y":2.0,"z":3.0}}"""
    val strCoordinates3D = """{"XYZ":{"x":1,"y":2,"z":3}}"""

    assertEncoder(objCoordinateXYZ, strCoordinateXYZ)
    assertDecode(strCoordinateXYZ, objCoordinateXYZ)
    assertDecode[Coordinate](strCoordinateXYZ, XYZ(1, 2, 3))

    assertEncoder(objCoordinates3D, strCoordinates3D)
    assertDecode(strCoordinates3D, objCoordinates3D)

    assertEncoder(objCoordinates, strCoordinates)
    assertDecode(strCoordinates, objCoordinates)
  }

  // Transformation
  test("Transformation") {
    val obj1: Transformation = TransformMatrix(Matrix.translate(1, 2, 3))
    val obj2 = TransformMatrix(Matrix.translate(1, 2, 3))
    //val str = """[1.0,0.0,0.0,1.0,0.0,1.0,0.0,2.0,0.0,0.0,1.0,3.0,0.0,0.0,0.0,1.0]"""
    val str = """[1,0,0,1,0,1,0,2,0,0,1,3,0,0,0,1]"""
    assertEquals(obj1, obj2)
    assertEncoder(obj1, str)
    assertDecode(str, obj1)
  }

  // Shape

  test("Shape") {
    val objPoints: Points = Points(Seq(XYZ(1, 2, 3)))
    //val strPoints = """[{"XYZ":{"x":1.0,"y":2.0,"z":3.0}}]""" // """{"c":[{"XYZ":{"x":1.0,"y":2.0,"z":3.0}}]}"""
    val strPoints = """[{"XYZ":{"x":1,"y":2,"z":3}}]"""

    assertEncoder(objPoints, strPoints)
    assertDecode(strPoints, objPoints)
  }
  test("TransformationShape") {
    val obj: TransformationShape = Box(1, 2, 3).transformWith(Matrix.translate(Vec(10, 20, 30)))
    val str =
      //"""{"TransformationShape":{"shape":{"Box":{"width":1.0,"height":2.0,"depth":3.0}},"transformation":[1.0,0.0,0.0,10.0,0.0,1.0,0.0,20.0,0.0,0.0,1.0,30.0,0.0,0.0,0.0,1.0]}}"""
      """{"TransformationShape":{"shape":{"Box":{"width":1,"height":2,"depth":3}},"transformation":[1,0,0,10,0,1,0,20,0,0,1,30,0,0,0,1]}}"""
    assertEncoder(obj: Shape, str)
    assertDecode(str, obj: Shape)
  }

  // World
  test("Generic-Shape") {
    val obj = WorldAddition(Seq(Box(1, 2, 3)))
    val obj2: World = obj
    //val str = """{"shapes":[{"Box":{"width":1.0,"height":2.0,"depth":3.0}}]}"""
    val str = """{"shapes":[{"Box":{"width":1,"height":2,"depth":3}}]}"""
    val str2 = s"""{"WorldAddition":$str}"""

    import app.fmgp.geo.EncoderDecoder.worldEncoderDecoderAux.{given}

    assertEncoder(obj, str)
    assertDecode(str, obj)

    assertEncoder(obj2, str2)
    assertDecode(str2, obj2)
  }

  // import io.circe._, io.circe.syntax._, io.circe.parser._
  // val aux =
  //   Box(1, 2, 3).transformWith(Matrix.translate(Vec(1, 1, 1))).transformWith(Matrix.translate(Vec(1, 1, 1)))
  // println(WorldAddition(Seq(aux)).asJson.noSpaces)
}
