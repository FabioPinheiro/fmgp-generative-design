package fmgp.geo.prebuilt

import zio._
import fmgp.dsl._
import fmgp.geo.prebuilt.TreesExample
import scala.concurrent.Future

object runtime {

  val dslZLayer = ZLayer.wire[zio.Has[Dsl] with zio.Has[Dsl]](
    DslLive.layer,
    ZLayer.Debug.mermaid
  )

  val treeZLayer = ZLayer.wireSome[zio.Has[Dsl], zio.Has[TreesExample.Tree]](
    TreesExample.TreeLive.layer,
    Random.live,
    ZLayer.Debug.mermaid,
  )

  val allZLayer = ZLayer.wire[zio.Has[TreesExample.Tree] with zio.Has[Dsl]](
    DslLive.layer,
    TreesExample.TreeLive.layer,
    Random.live,
    ZLayer.Debug.mermaid
  )

  def allRT = zio.Runtime.unsafeFromLayer(allZLayer)
  def dslRT = zio.Runtime.unsafeFromLayer(dslZLayer)

  def runToFuture(
      in: zio.ZIO[zio.Has[TreesExample.Tree] with zio.Has[Dsl], Throwable, fmgp.geo.Shape]
  ): Future[fmgp.geo.Shape] = zio.Runtime.global.unsafeRunToFuture(in.inject(runtime.allZLayer)).future

  def run(
      in: zio.ZIO[zio.Has[TreesExample.Tree] with zio.Has[Dsl], Throwable, fmgp.geo.Shape]
  ): fmgp.geo.Shape = zio.Runtime.global.unsafeRun(in.inject(runtime.allZLayer))
}
