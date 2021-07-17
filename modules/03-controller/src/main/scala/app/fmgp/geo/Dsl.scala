package app.fmgp.geo

import scala.annotation.implicitNotFound
import scala.util.chaining._

object Dsl {
  final case class WorldBox(var v: Seq[Shape]) {
    //def add(s: Shape) = v = v :+ s
    def add(s: Seq[Shape]) = v = v ++ s
  }

  final case class Warp(
      var v: Seq[Shape] = Seq.empty,
      history: scala.collection.mutable.Stack[Seq[Shape]] = new scala.collection.mutable.Stack[Seq[Shape]]()
  ) extends ContestSpecificDslImp {
    def add(s: Shape) = v = v :+ s
    def push = { history.push(v); v = Seq.empty }
    def pop = v = history.pop
  }

  final case class Dummy() extends ContestSpecificDslImp
}

trait Dsl extends ContestSpecificDsl {
  import Dsl.{WorldBox, Warp, Dummy}
  def sideEffect(w: WorldBox): Unit

  //World
  def world3d(init: Warp ?=> Unit): WorldBox =
    given worldBox: WorldBox = WorldBox(Seq.empty)
    given warpWorld: Warp = Warp(Seq.empty)
    import warpWorld._
    init
    worldBox.add(warpWorld.v)
    sideEffect(worldBox)
    worldBox

  def warp(init: Warp ?=> Unit)(using
      @implicitNotFound("sphere needed to be defined in a World") t: WorldBox
  ): Warp =
    given w: Warp = Warp(Seq.empty)
    init
    t.add(w.v)
    w

  //...
  def xyz(x: Double = 0, y: Double = 0, z: Double = 0): XYZ = XYZ(x, y, z)
  def vxyz(x: Double = 0, y: Double = 0, z: Double = 0): Vec = Vec(x, y, z)

  // OPS
  def dummy[S <: Shape](init: Dummy ?=> S)(using
      @implicitNotFound("dummy needed to be defined in a World") ctx: Warp
  ) =
    given d: Dummy = Dummy()
    import d._
    init

  def add(obj: Dummy ?=> Shape)(using @implicitNotFound("add call only be called in a World") ctx: Warp) =
    given d: Dummy = Dummy()
    import d._
    ctx.add(obj)
    (): Unit

  override def box(width: Double, height: Double, depth: Double)(using
      @implicitNotFound("box needed to be defined in a World") ctx: Warp | Dummy
  ) = ctx.box(width: Double, height: Double, depth: Double)

  override def sphere(center: => XYZ, radius: => Double)(using
      @implicitNotFound("sphere needed to be defined in a World") ctx: Warp | Dummy
  ): Sphere = ctx.sphere(center, radius)

  override def shapes(init: (Warp | Dummy) ?=> Shape | Unit)(using
      @implicitNotFound("shapes needed to be defined in a World") ctx: Warp | Dummy
  ) = ctx.shapes(init)

}

trait ContestSpecificDsl {
  import Dsl.{WorldBox, Warp, Dummy}

  protected def ms[S <: Shape, W <: Warp | Dummy](s: S)(using w: W): S = w match
    case e: Warp  => e.add(s); s //ShapeNull()
    case e: Dummy => s

  def box(width: Double, height: Double, depth: Double)(using
      @implicitNotFound("box needed to be defined in a World") ctx: Warp | Dummy
  ): Box

  def sphere(center: => XYZ, radius: => Double)(using
      @implicitNotFound("sphere needed to be defined in a World") ctx: Warp | Dummy
  ): Sphere

  def shapes(init: Warp | Dummy ?=> Shape | Unit)(using
      @implicitNotFound("shapes needed to be defined in a World") ctx: Warp | Dummy
  ): ShapeSeq
}

trait ContestSpecificDslImp extends ContestSpecificDsl {
  import Dsl.{WorldBox, Warp, Dummy}

  def box(width: Double, height: Double, depth: Double)(using
      @implicitNotFound("box needed to be defined in a World") ctx: Warp | Dummy
  ): Box = ms(Box(width, height, depth))

  def sphere(center: => XYZ, radius: => Double)(using
      @implicitNotFound("sphere needed to be defined in a World") ctx: Warp | Dummy
  ): Sphere = ms(Sphere(radius, center))

  def shapes(init: (Warp | Dummy) ?=> Shape | Unit)(using
      @implicitNotFound("sphere needed to be defined in a World") ctx: Warp | Dummy
  ) =
    ctx match
      case ctxW: Warp =>
        ctxW.push
        val extra = init match {
          case s: Shape => println("1111111111111"); Seq(s)
          case _: Unit  => println("222222222222"); Seq.empty
          // case s: Seq[Shape] => println("3333333333333"); s
        }
        println(s"extra: $extra               ----    ctxW.v: ${ctxW.v} ")
        val seq = ShapeSeq(ctxW.v)
        val fixme = ctxW.pop
        println(s"fixme: $fixme")
        ctxW.add(seq)
        seq
      case e: Dummy =>
        val extra = init match {
          case s: Shape => println("3333333333"); Seq(s)
          case _: Unit  => println("4444444444"); Seq.empty
          // case s: Seq[Shape] => println("3333333333333"); s
        }
        val seq = ShapeSeq(extra)
        seq

}

// [error]    |None of the overloaded alternatives of method shapes in trait ContestSpecificDslImp with types
// (init: (app.fmgp.geo.Dsl.Warp | app.fmgp.geo.Dsl.Dummy) ?=> app.fmgp.geo.Shape | Unit)(using ctx: app.fmgp.geo.Dsl.Warp | app.fmgp.geo.Dsl.Dummy): Matchable
// (init: (app.fmgp.geo.Dsl.Warp) ?=> app.fmgp.geo.Shape | Unit)  (using ctx: app.fmgp.geo.Dsl.Warp | app.fmgp.geo.Dsl.Dummy): app.fmgp.geo.ShapeSeq match arguments (app.fmgp.geo.Shape | Unit)
