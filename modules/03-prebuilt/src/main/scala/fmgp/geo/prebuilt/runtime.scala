package fmgp.geo.prebuilt

import zio._
import fmgp.dsl._
import fmgp.geo.prebuilt.TreesExample
import scala.concurrent.Future
import zio.Exit.Success
import zio.Exit.Failure

object runtime {

  private val dslZServiceBuilder = ZLayer.make[Dsl](
    DslLive.layer,
    // ZLayer.Debug.mermaid
  )

  private val treeZServiceBuilder = ZLayer.makeSome[Dsl, TreesExample.Tree](
    TreesExample.TreeLive.layer,
    ZLayer.succeed(Random.RandomLive),
    // ZLayer.Debug.mermaid,
  )

  private val allZServiceBuilder =
    ZLayer.make[TreesExample.Tree with Dsl](
      DslLive.layer,
      TreesExample.TreeLive.layer,
      ZLayer.succeed(Random.RandomLive),
      // ZLayer.Debug.mermaid,
    )

  def runToFuture(
      in: zio.ZIO[TreesExample.Tree with Dsl, Throwable, fmgp.geo.Shape]
  ): Future[fmgp.geo.Shape] =
    Unsafe.unsafe { implicit unsafe => // Run side effect
      Runtime.default.unsafe.runToFuture(
        in.provideLayer(runtime.allZServiceBuilder)
      )
    }

  def run(
      in: zio.ZIO[TreesExample.Tree with Dsl, Throwable, fmgp.geo.Shape]
  ): fmgp.geo.Shape =
    Unsafe.unsafe { implicit unsafe => // Run side effect
      Runtime.default.unsafe.run(
        in.provideLayer(runtime.allZServiceBuilder)
      ) match
        case Success(value) => value
        case Failure(cause) => ???
    }

}
