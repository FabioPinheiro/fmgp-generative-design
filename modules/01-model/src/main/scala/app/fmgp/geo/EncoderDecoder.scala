package app.fmgp.geo

import io.circe._, io.circe.syntax._, io.circe.generic.semiauto._
import cats.syntax.functor._

object EncoderDecoder {

  given Encoder[Dimensions] = new Encoder[Dimensions] {
    override def apply(a: Dimensions): io.circe.Json = Json.fromString(a.toString)
  }
  given Decoder[Dimensions] = new Decoder[Dimensions] {
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Dimensions] =
      c.as[String].map(Dimensions.valueOf(_))
  }

  given Encoder[WorldAddition] = deriveEncoder[WorldAddition]
  given Decoder[WorldAddition] = deriveDecoder[WorldAddition]
  given Encoder[WorldState] = deriveEncoder[WorldState]
  given Decoder[WorldState] = deriveDecoder[WorldState]

  given Encoder[World] = Encoder.instance {
    case e: WorldState    => JsonObject(("WorldState", e.asJson)).asJson
    case e: WorldAddition => JsonObject(("WorldAddition", e.asJson)).asJson
  }
  //given Decoder[World] = List[Decoder[World]](Decoder[WorldAddition].widen, Decoder[WorldState].widen).reduceLeft(_ or _)
  given Decoder[World] = new Decoder[World]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[World] = {
      c.keys.map(_.toSeq) match {
        case Some("WorldState" :: Nil)    => c.downField("WorldState").as[WorldState]
        case Some("WorldAddition" :: Nil) => c.downField("WorldAddition").as[WorldAddition]
        case Some(e)                      => Left(DecodingFailure(s"'$e' is not a World type", c.history))
        case e => Left(DecodingFailure(s"Attempt to decode a World on failed missing type $e", c.history))
      }
    }

  given Encoder[Vec] = deriveEncoder[Vec]
  given Decoder[Vec] = deriveDecoder[Vec]
  given Encoder[Matrix] = new Encoder[Matrix]:
    override def apply(a: Matrix): io.circe.Json = summon[Encoder[Array[Double]]](
      Array(
        a.m00,
        a.m01,
        a.m02,
        a.m03,
        a.m10,
        a.m11,
        a.m12,
        a.m13,
        a.m20,
        a.m21,
        a.m22,
        a.m23,
        a.m30,
        a.m31,
        a.m32,
        a.m33
      )
    )
  given Decoder[Matrix] = new Decoder[Matrix]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Matrix] =
      summon[Decoder[Array[scala.Double]]](c).map {
        case Array(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33) =>
          Matrix(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33)
      }

  given Decoder[XYZ] = deriveDecoder[XYZ]
  given Encoder[XYZ] = deriveEncoder[XYZ]
  given Decoder[Polar] = deriveDecoder[Polar]
  given Encoder[Polar] = deriveEncoder[Polar]
  given Decoder[Cylindrical] = deriveDecoder[Cylindrical]
  given Encoder[Cylindrical] = deriveEncoder[Cylindrical]
  given Decoder[Spherical] = deriveDecoder[Spherical]
  given Encoder[Spherical] = deriveEncoder[Spherical]

  given Encoder[Coordinate] = Encoder.instance {
    case e: Vec         => JsonObject(("Vec", e.asJson)).asJson
    case e: Polar       => JsonObject(("Polar", e.asJson)).asJson
    case e: XYZ         => JsonObject(("XYZ", e.asJson)).asJson
    case e: Cylindrical => JsonObject(("Cylindrical", e.asJson)).asJson
    case e: Spherical   => JsonObject(("Spherical", e.asJson)).asJson
  }

  given Decoder[Coordinate] = new Decoder[Coordinate]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Coordinate] = {
      //c.downField("Vec").downField("aa").root
      c.keys.map(_.toSeq) match {
        case Some("Vec" :: Nil)         => c.downField("Vec").as[Vec]
        case Some("Polar" :: Nil)       => c.downField("Polar").as[Polar]
        case Some("XYZ" :: Nil)         => c.downField("XYZ").as[XYZ]
        case Some("Cylindrical" :: Nil) => c.downField("Cylindrical").as[Cylindrical]
        case Some("Spherical" :: Nil)   => c.downField("Spherical").as[Spherical]
        case Some(e)                    => Left(DecodingFailure(s"'$e' is not a Coordinate type", c.history))
        case e => Left(DecodingFailure(s"Attempt to decode a Coordinate on failed missing type $e", c.history))
      }
    }

  given Encoder[Coordinate3D] = new Encoder[Coordinate3D]:
    override def apply(e: Coordinate3D): io.circe.Json = summon[Encoder[Coordinate]](e)
  given Decoder[Coordinate3D] = new Decoder[Coordinate3D]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Coordinate3D] =
      summon[Decoder[Coordinate]](c).map(_.asInstanceOf[Coordinate3D])

  //### Transform ###
  given Encoder[TransformMatrix] = new Encoder[TransformMatrix]: //deriveEncoder[TransformMatrix]
    override def apply(a: TransformMatrix): io.circe.Json = summon[Encoder[Matrix]](a.matrix)
  given Decoder[TransformMatrix] = new Decoder[TransformMatrix]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[TransformMatrix] =
      summon[Decoder[Matrix]].map(e => TransformMatrix(e)).apply(c)

  given Encoder[Transformation] = Encoder.instance { case e: TransformMatrix => e.asJson }
  //given Decoder[Transformation] = List[Decoder[Transformation]](Decoder[TransformMatrix].widen).reduceLeft(_ or _)
  given Decoder[Transformation] = new Decoder[Transformation]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Transformation] =
      summon[Decoder[Matrix]].map(e => TransformMatrix(e)).apply(c)

//### Shapes ###
  given Encoder[TransformationShape] = deriveEncoder[TransformationShape]
  given Decoder[TransformationShape] = deriveDecoder[TransformationShape]
  given Encoder[Wireframe] = deriveEncoder[Wireframe]
  given Decoder[Wireframe] = deriveDecoder[Wireframe]
  given Encoder[ShapeSeq] = new Encoder[ShapeSeq]:
    override def apply(a: ShapeSeq): io.circe.Json = summon[Encoder[Seq[Shape]]](a.shapes)
  given Decoder[ShapeSeq] = new Decoder[ShapeSeq]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[ShapeSeq] =
      summon[Decoder[Seq[Shape]]].map(e => ShapeSeq(e)).apply(c)
  given Encoder[Points] = new Encoder[Points]:
    override def apply(a: Points): io.circe.Json = summon[Encoder[Seq[Coordinate3D]]](a.c)
  given Decoder[Points] = new Decoder[Points]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Points] =
      summon[Decoder[Seq[Coordinate3D]]].map(e => Points(e)).apply(c)
  given Encoder[Box] = deriveEncoder[Box]
  given Decoder[Box] = deriveDecoder[Box]
  given Encoder[Sphere] = deriveEncoder[Sphere]
  given Decoder[Sphere] = deriveDecoder[Sphere]
  given Encoder[Cylinder] = deriveEncoder[Cylinder]
  given Decoder[Cylinder] = deriveDecoder[Cylinder]
  given Encoder[Torus] = deriveEncoder[Torus]
  given Decoder[Torus] = deriveDecoder[Torus]
  given Encoder[Extrude] = deriveEncoder[Extrude]
  given Decoder[Extrude] = deriveDecoder[Extrude]
  given Encoder[PlaneShape] = deriveEncoder[PlaneShape]
  given Decoder[PlaneShape] = deriveDecoder[PlaneShape]
  given Encoder[SurfaceGridShape] = deriveEncoder[SurfaceGridShape]
  given Decoder[SurfaceGridShape] = deriveDecoder[SurfaceGridShape]
  given Encoder[LinePath] = deriveEncoder[LinePath]
  given Decoder[LinePath] = deriveDecoder[LinePath]
  given Encoder[CubicBezierPath] = deriveEncoder[CubicBezierPath]
  given Decoder[CubicBezierPath] = deriveDecoder[CubicBezierPath]
  // given Encoder[MultiPath] = deriveEncoder[MultiPath]
  // given Decoder[MultiPath] = deriveDecoder[MultiPath]

  given Encoder[MyPath] = Encoder.instance {
    case e: LinePath        => e.asJson
    case e: CubicBezierPath => e.asJson
    case e: MultiPath       => e.asJson
  }
  given Decoder[MyPath] = List[Decoder[MyPath]](
    Decoder[LinePath].widen,
    Decoder[CubicBezierPath].widen,
    Decoder[MultiPath].widen
  ).reduceLeft(_ or _)

  given Encoder[MultiPath] = new Encoder[MultiPath]:
    override def apply(a: MultiPath): io.circe.Json = summon[Encoder[Seq[MyPath]]](a.paths)
  given Decoder[MultiPath] = new Decoder[MultiPath]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[MultiPath] =
      summon[Decoder[Seq[MyPath]]].map(e => MultiPath(e)).apply(c)

  given Encoder[Circle] = deriveEncoder[Circle]
  given Decoder[Circle] = deriveDecoder[Circle]
  given Encoder[TriangleShape] = deriveEncoder[TriangleShape]
  given Decoder[TriangleShape] = deriveDecoder[TriangleShape]
  // given Encoder[Triangle[T]] = deriveEncoder[Triangle[T]]
  // given Decoder[Triangle[T]] = deriveDecoder[Triangle[T]]

  given Encoder[Arrow] = deriveEncoder[Arrow]
  given Decoder[Arrow] = deriveDecoder[Arrow]
  given Encoder[Axes] = deriveEncoder[Axes]
  given Decoder[Axes] = deriveDecoder[Axes]
  given Encoder[TextShape] = deriveEncoder[TextShape]
  given Decoder[TextShape] = deriveDecoder[TextShape]
  given Encoder[TestShape] = deriveEncoder[TestShape]
  given Decoder[TestShape] = deriveDecoder[TestShape]

  given Encoder[Shape] = Encoder.instance {
    case e: TransformationShape => JsonObject(("TransformationShape", e.asJson)).asJson
    case e: Wireframe           => JsonObject(("Wireframe", e.asJson)).asJson
    case e: ShapeSeq            => JsonObject(("ShapeSeq", e.asJson)).asJson
    case e: Points              => JsonObject(("Points", e.asJson)).asJson
    case e: Box                 => JsonObject(("Box", e.asJson)).asJson
    case e: Sphere              => JsonObject(("Sphere", e.asJson)).asJson
    case e: Cylinder            => JsonObject(("Cylinder", e.asJson)).asJson
    case e: Torus               => JsonObject(("Torus", e.asJson)).asJson
    case e: Extrude             => JsonObject(("Extrude", e.asJson)).asJson
    case e: PlaneShape          => JsonObject(("PlaneShape", e.asJson)).asJson
    case e: SurfaceGridShape    => JsonObject(("SurfaceGridShape", e.asJson)).asJson
    case e: LinePath            => JsonObject(("LinePath", e.asJson)).asJson
    case e: MultiPath           => JsonObject(("MultiPath", e.asJson)).asJson
    case e: CubicBezierPath     => JsonObject(("CubicBezierPath", e.asJson)).asJson
    case e: Circle              => JsonObject(("Circle", e.asJson)).asJson
    case e: TriangleShape       => JsonObject(("TriangleShape", e.asJson)).asJson
    case e: Arrow               => JsonObject(("Arrow", e.asJson)).asJson
    case e: Axes                => JsonObject(("Axes", e.asJson)).asJson
    case e: TestShape           => JsonObject(("TestShape", e.asJson)).asJson
    case e: TextShape           => JsonObject(("TextShape", e.asJson)).asJson
  }

  given Decoder[Shape] = new Decoder[Shape]:
    override def apply(c: io.circe.HCursor): io.circe.Decoder.Result[Shape] = {
      c.keys.map(_.toSeq) match {
        case Some("TransformationShape" :: Nil) => c.downField("TransformationShape").as[TransformationShape]
        case Some("Wireframe" :: Nil)           => c.downField("Wireframe").as[Wireframe]
        case Some("ShapeSeq" :: Nil)            => c.downField("ShapeSeq").as[ShapeSeq]
        case Some("Points" :: Nil)              => c.downField("Points").as[Points]
        case Some("Box" :: Nil)                 => c.downField("Box").as[Box]
        case Some("Sphere" :: Nil)              => c.downField("Sphere").as[Sphere]
        case Some("Cylinder" :: Nil)            => c.downField("Cylinder").as[Cylinder]
        case Some("Torus" :: Nil)               => c.downField("Torus").as[Torus]
        case Some("Extrude" :: Nil)             => c.downField("Extrude").as[Extrude]
        case Some("PlaneShape" :: Nil)          => c.downField("PlaneShape").as[PlaneShape]
        case Some("SurfaceGridShape" :: Nil)    => c.downField("SurfaceGridShape").as[SurfaceGridShape]
        case Some("LinePath" :: Nil)            => c.downField("LinePath").as[LinePath]
        case Some("MultiPath" :: Nil)           => c.downField("MultiPath").as[MultiPath]
        case Some("CubicBezierPath" :: Nil)     => c.downField("CubicBezierPath").as[CubicBezierPath]
        case Some("Circle" :: Nil)              => c.downField("Circle").as[Circle]
        case Some("TriangleShape" :: Nil)       => c.downField("TriangleShape").as[TriangleShape]

        case Some("Arrow" :: Nil)     => c.downField("Arrow").as[Arrow]
        case Some("Axes" :: Nil)      => c.downField("Axes").as[Axes]
        case Some("TestShape" :: Nil) => c.downField("TestShape").as[TestShape]
        case Some("TextShape" :: Nil) => c.downField("TextShape").as[TextShape]

        case Some(e) => Left(DecodingFailure(s"'$e' is not a Shape type", c.history))
        case e       => Left(DecodingFailure(s"Attempt to decode a Shape on failed missing type $e", c.history))
      }
    }

}
