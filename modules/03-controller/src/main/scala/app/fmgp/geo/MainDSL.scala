package app.fmgp.geo

@main def MainDSL() =
  //import app.fmgp.geo.DslConsole._
  import app.fmgp.geo.DslJson._
  //import DslConsole._
  world3d {
    val origin = xyz()
    val box9: Box = dummy(box(999, 999, 999))
    val box2 = dummy(box(2, 2, 2)) //it is also of the type Box!   =)
    box(1, 1, 1)
    add(box2)
    dummy(box(888, 888, 888)) // its not added! =)
    add(dummy(box(3, 3, 3)))
    add(box(4, 4, 4)) // is just added 1 time !!!! =)
    //dummy(add(box(5, 5, 5))) //this does not compile !!!! =)
    for i <- 5 to 8 do add(box(i, i, i))
    add(box(9, 9, 9))

    dummy(shapes(shapes(ShapeSeq()))) //is not added =)
    shapes {} //is added but can be filtered =)

    def highOrderWithContext = (ctx: Dsl.Warp | Dsl.Dummy) ?=> box(123, 123, 123)
    def highOrderWithOutContext = box(111, 111, 111)
    shapes {
      shapes {
        val aux = dummy(box(10, 10, 10))
        add(aux)
        val seq = dummy {
          highOrderWithOutContext //the context is not from here so it will be added  =(
          highOrderWithContext //the context is from here so it will be not added becouse is a dummy =)
          shapes {
            box(12, 12, 12)
          }
        }
        box(11, 11, 11)
        add(seq)
      }
    }

    box(1, 2, 3)
    shapes {
      sphere(origin, 2)
      box(1, 2, 3)
    }
    shapes(sphere(origin, 2))
    shapes {
      sphere(origin, 2)
      shapes {
        box(1, 2, 3)
      }
    }
    shapes(shapes(shapes(sphere(xyz(), 2))))
  }
