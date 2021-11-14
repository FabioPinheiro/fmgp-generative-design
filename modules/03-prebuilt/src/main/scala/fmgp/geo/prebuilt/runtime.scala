package fmgp.geo.prebuilt

import zio._
import fmgp.dsl._
import fmgp.geo.prebuilt.TreesExample
import scala.concurrent.Future

object runtime {

  private val dslZServiceBuilder = ZServiceBuilder.wire[zio.Has[Dsl] with zio.Has[Dsl]](
    DslLive.layer,
    ZServiceBuilder.Debug.mermaid
  )

  private val treeZServiceBuilder = ZServiceBuilder.wireSome[zio.Has[Dsl], zio.Has[TreesExample.Tree]](
    TreesExample.TreeLive.layer,
    Random.live,
    ZServiceBuilder.Debug.mermaid,
  )

  private val allZServiceBuilder = ZServiceBuilder.wire[zio.Has[TreesExample.Tree] with zio.Has[Dsl]](
    DslLive.layer,
    TreesExample.TreeLive.layer,
    Random.live,
    ZServiceBuilder.Debug.mermaid
  )

  def runToFuture(
      in: zio.ZIO[zio.Has[TreesExample.Tree] with zio.Has[Dsl], Throwable, fmgp.geo.Shape]
  ): Future[fmgp.geo.Shape] = zio.Runtime.global.unsafeRunToFuture(in.inject(runtime.allZServiceBuilder)).future

  def run(
      in: zio.ZIO[zio.Has[TreesExample.Tree] with zio.Has[Dsl], Throwable, fmgp.geo.Shape]
  ): fmgp.geo.Shape = zio.Runtime.global.unsafeRun(in.inject(runtime.allZServiceBuilder))
}
