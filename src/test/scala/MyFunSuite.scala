import munit._

import scala.collection.mutable
import scala.concurrent.Future
import scala.util.control.NonFatal
import munit.internal.PlatformCompat
import scala.concurrent.duration.Duration
import scala.concurrent.duration.FiniteDuration
import java.util.concurrent.TimeUnit

abstract class MyFunSuite
    extends Suite
    with Assertions
    with MyFunFixtures
    with TestOptionsConversions
    with MyTestTransforms
    with MySuiteTransforms
    with MyValueTransforms { self =>

  final type TestValue = Future[Any]

  final val munitTestsBuffer: mutable.ListBuffer[Test] =
    mutable.ListBuffer.empty[Test]
  def munitTests(): Seq[Test] = {
    munitSuiteTransform(munitTestsBuffer.toList)
  }

  def test(name: String)(body: => Any)(implicit loc: Location): Unit = {
    test(new TestOptions(name))(body)
  }
  def test(options: TestOptions)(body: => Any)(implicit loc: Location): Unit = {
    munitTestsBuffer += munitTestTransform(
      new Test(
        options.name, { () =>
          try {
            waitForCompletion(munitValueTransform(body))
          } catch {
            case NonFatal(e) =>
              Future.failed(e)
          }
        },
        options.tags.toSet,
        loc
      )
    )
  }

  def munitTimeout: Duration = new FiniteDuration(30, TimeUnit.SECONDS)
  private final def waitForCompletion[T](f: Future[T]) =
    PlatformCompat.waitAtMost(f, munitTimeout)

}

import munit.internal.FutureCompat._

import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure

trait MyFunFixtures { self: MyFunSuite =>

  class MyFunFixtures[T] private (
      val setup: TestOptions => Future[T],
      val teardown: T => Future[Unit]
  )(implicit dummy: DummyImplicit) { fixture =>
    @deprecated("Use `MyFunFixtures(...)` without `new` instead", "0.7.2")
    def this(setup: TestOptions => T, teardown: T => Unit) =
      this(
        (options: TestOptions) => Future(setup(options))(munitExecutionContext),
        (argument: T) => Future(teardown(argument))(munitExecutionContext)
      )

    def test(name: String)(
        body: T => Any
    )(implicit loc: Location): Unit = {
      fixture.test(TestOptions(name))(body)
    }
    def test(options: TestOptions)(
        body: T => Any
    )(implicit loc: Location): Unit = {
      self.test(options) {
        implicit val ec = munitExecutionContext
        // the setup, test and teardown need to keep the happens-before execution order
        setup(options).flatMap { argument =>
          munitValueTransform(body(argument))
            .transformWithCompat(testValue =>
              teardown(argument).transformCompat {
                case Success(_) => testValue
                case teardownFailure @ Failure(teardownException) =>
                  testValue match {
                    case testFailure @ Failure(testException) =>
                      testException.addSuppressed(teardownException)
                      testFailure
                    case _ =>
                      teardownFailure
                  }
              }
            )
        }
      }(loc)
    }
  }

  object MyFunFixtures {
    def apply[T](
        setup: TestOptions => T,
        teardown: T => Unit
    ): MyFunFixtures[T] = {
      implicit val ec = munitExecutionContext
      async[T](
        options => Future { setup(options) },
        argument => Future { teardown(argument) }
      )
    }
    def async[T](setup: TestOptions => Future[T], teardown: T => Future[Unit]) =
      new MyFunFixtures(setup, teardown)

    def map2[A, B](a: MyFunFixtures[A], b: MyFunFixtures[B]): MyFunFixtures[(A, B)] =
      MyFunFixtures.async[(A, B)](
        setup = { options =>
          implicit val ec = munitExecutionContext
          val setupA = a.setup(options)
          val setupB = b.setup(options)
          for {
            argumentA <- setupA
            argumentB <- setupB
          } yield (argumentA, argumentB)
        },
        teardown = {
          case (argumentA, argumentB) =>
            implicit val ec = munitExecutionContext
            Future
              .sequence(List(a.teardown(argumentA), b.teardown(argumentB)))
              .map(_ => ())
        }
      )
    def map3[A, B, C](
        a: MyFunFixtures[A],
        b: MyFunFixtures[B],
        c: MyFunFixtures[C]
    ): MyFunFixtures[(A, B, C)] =
      MyFunFixtures.async[(A, B, C)](
        setup = { options =>
          implicit val ec = munitExecutionContext
          val setupA = a.setup(options)
          val setupB = b.setup(options)
          val setupC = c.setup(options)
          for {
            argumentA <- setupA
            argumentB <- setupB
            argumentC <- setupC
          } yield (argumentA, argumentB, argumentC)
        },
        teardown = {
          case (argumentA, argumentB, argumentC) =>
            implicit val ec = munitExecutionContext
            Future
              .sequence(
                List(
                  a.teardown(argumentA),
                  b.teardown(argumentB),
                  c.teardown(argumentC)
                )
              )
              .map(_ => ())
        }
      )
  }

}

import munit.internal.FutureCompat._
import scala.util.Success
import scala.util.Failure
import scala.concurrent.Future
import scala.util.control.NonFatal

trait MyTestTransforms { this: MyFunSuite =>

  final class TestTransform(val name: String, fn: Test => Test) extends Function1[Test, Test] {
    def apply(v1: Test): Test = fn(v1)
  }

  def munitTestTransforms: List[TestTransform] =
    List(
      munitFailTransform,
      munitFlakyTransform
    )

  final def munitTestTransform(test: Test): Test = {
    try {
      munitTestTransforms.foldLeft(test) {
        case (t, fn) =>
          fn(t)
      }
    } catch {
      case NonFatal(e) =>
        test.withBody[TestValue](() => Future.failed(e))
    }
  }

  final def munitFailTransform: TestTransform =
    new TestTransform(
      "fail", { t =>
        if (t.tags(Fail)) {
          t.withBodyMap[TestValue](
            _.transformCompat {
              case Success(value) =>
                Failure(
                  throw new FailException(
                    munitLines.formatLine(
                      t.location,
                      "expected failure but test passed"
                    ),
                    t.location
                  )
                )
              case Failure(exception) =>
                Success(())
            }(munitExecutionContext)
          )
        } else {
          t
        }
      }
    )

  def munitFlakyOK: Boolean = "true" == System.getenv("MUNIT_FLAKY_OK")
  final def munitFlakyTransform: TestTransform =
    new TestTransform(
      "flaky", { t =>
        if (t.tags(Flaky)) {
          t.withBodyMap(_.transformCompat {
            case Success(value) => Success(value)
            case Failure(exception) =>
              if (munitFlakyOK) {
                Success(new TestValues.FlakyFailure(exception))
              } else {
                throw exception
              }
          }(munitExecutionContext))
        } else {
          t
        }
      }
    )

}

import scala.concurrent.Future
import scala.util.control.NonFatal

trait MySuiteTransforms { this: MyFunSuite =>

  final class MySuiteTransform(val name: String, fn: List[Test] => List[Test])
      extends Function1[List[Test], List[Test]] {
    def apply(v1: List[Test]): List[Test] = fn(v1)
  }

  def munitSuiteTransforms: List[MySuiteTransform] =
    List(
      munitIgnoreSuiteTransform,
      munitOnlySuiteTransform
    )

  final def munitSuiteTransform(tests: List[Test]): List[Test] = {
    try {
      munitSuiteTransforms.foldLeft(tests) {
        case (ts, fn) =>
          fn(ts)
      }
    } catch {
      case NonFatal(e) =>
        List(
          new Test(
            "munitSuiteTransform",
            () => Future.failed(e)
          )(Location.empty)
        )
    }
  }

  def munitIgnore: Boolean = false
  final def munitIgnoreSuiteTransform: MySuiteTransform =
    new MySuiteTransform(
      "munitIgnore", { tests =>
        if (munitIgnore) Nil
        else tests
      }
    )

  def isCI: Boolean = "true" == System.getenv("CI")
  final def munitOnlySuiteTransform: MySuiteTransform =
    new MySuiteTransform(
      "only", { tests =>
        val onlySuite = tests.filter(_.tags(Only))
        if (onlySuite.nonEmpty) {
          if (!isCI) {
            onlySuite
          } else {
            onlySuite.map(t =>
              if (t.tags(Only)) {
                t.withBody[TestValue](() => fail("'Only' tag is not allowed when `isCI=true`")(t.location))
              } else {
                t
              }
            )
          }
        } else {
          tests
        }
      }
    )
}

import scala.concurrent.Future
import munit.internal.FutureCompat._
import scala.util.Try
import munit.internal.console.StackTraces

trait MyValueTransforms { this: MyFunSuite =>

  final class MyValueTransform(
      val name: String,
      fn: PartialFunction[Any, Future[Any]]
  ) extends Function1[Any, Option[Future[Any]]] {
    def apply(v1: Any): Option[Future[Any]] = fn.lift(v1)
  }

  def munitValueTransforms: List[MyValueTransform] =
    List(
      munitFutureTransform
    )

  final def munitValueTransform(testValue: => Any): Future[Any] = {
    // Takes an arbitrarily nested future `Future[Future[Future[...]]]` and
    // returns a `Future[T]` where `T` is not a `Future`.
    def flattenFuture(future: Future[_]): Future[_] = {
      val nested: Future[Future[Any]] = future.map { value =>
        val transformed = munitValueTransforms.iterator
          .map(fn => fn(value))
          .collectFirst { case Some(future) => future }
        transformed match {
          case Some(f) => flattenFuture(f)
          case None    => Future.successful(value)
        }
      }(munitExecutionContext)
      nested.flattenCompat(munitExecutionContext)
    }
    val wrappedFuture = Future.fromTry(Try(StackTraces.dropOutside(testValue)))
    flattenFuture(wrappedFuture)
  }

  final def munitFutureTransform: MyValueTransform =
    new MyValueTransform("Future", { case e: Future[_] => e })
}
