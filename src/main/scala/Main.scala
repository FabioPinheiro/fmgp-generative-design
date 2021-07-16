import TestUtils._ //If we copy the code to this file the bug does not happen!

class Test extends A {
  TestUtils.anInlineMethod
}

abstract class A extends B { self =>}
trait B { self: A => }

@main def hello() = 
  println("Hello, world")
  new Test
