package fmgp.geo.prebuilt

import zio._
import fmgp.dsl._
import fmgp.geo.prebuilt.TreesExample
import scala.concurrent.Future

object runtime {

  private val dslZServiceBuilder = ZLayer.make[Dsl](
    DslLive.layer,
    ZLayer.Debug.mermaid
  )

  private val treeZServiceBuilder = ZLayer.makeSome[Dsl, TreesExample.Tree](
    TreesExample.TreeLive.layer,
    Random.live,
    ZLayer.Debug.mermaid,
  )

  private val allZServiceBuilder = ZLayer.make[TreesExample.Tree with Dsl](
    DslLive.layer,
    TreesExample.TreeLive.layer,
    Random.live,
    ZLayer.Debug.mermaid
  )

  def runToFuture(
      in: zio.ZIO[TreesExample.Tree with Dsl, Throwable, fmgp.geo.Shape]
  ): Future[fmgp.geo.Shape] = zio.Runtime.global.unsafeRunToFuture(in.provideLayer(runtime.allZServiceBuilder)).future

  def run(
      in: zio.ZIO[TreesExample.Tree with Dsl, Throwable, fmgp.geo.Shape]
  ): fmgp.geo.Shape = zio.Runtime.global.unsafeRun(in.provideLayer(runtime.allZServiceBuilder))
}
