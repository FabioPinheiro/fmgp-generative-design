object DDD
case class AAA(a: DDD.type)

//case class AAA(a: Int)
/*
sbt:fmgp-geometry> compile
[info] compiling 2 Scala sources to /home/fabio/workspace/fmgp-threejs/target/scala-3.0.0/classes ...
[error] -- Error: /home/fabio/workspace/fmgp-threejs/src/main/scala/File2.scala:3:40 ---
[error] 3 |val test = summon[io.circe.Decoder[AAA]]
[error]   |                                        ^
[error]   |       cannot reduce summonFrom with
[error]   |        patterns :  case given decodeA @ _:io.circe.Decoder[DDD.type]
[error]   |                    case given evidence$2 @ _:deriving.Mirror.Of[DDD.type]
[error]   | This location contains code that was inlined from Derivation.scala:19
[error]   | This location contains code that was inlined from Derivation.scala:32
[error]   | This location contains code that was inlined from Derivation.scala:11
[error]   | This location contains code that was inlined from Derivation.scala:70
[error]   | This location contains code that was inlined from auto.scala:16
[error] one error found
[error] one error found
[error] (Compile / compileIncremental) Compilation failed
[error] Total time: 0 s, completed Jul 7, 2021 3:16:49 PM


### Change code to use `case class AAA(a: Int)`

sbt:fmgp-geometry> compile
[info] compiling 2 Scala sources to /home/fabio/workspace/fmgp-threejs/target/scala-3.0.0/classes ...
[success] Total time: 1 s, completed Jul 7, 2021 3:17:21 PM


### Change code back to `case class AAA(a: DDD.type)`

sbt:fmgp-geometry> compile
[info] compiling 1 Scala source to /home/fabio/workspace/fmgp-threejs/target/scala-3.0.0/classes ...
[success] Total time: 0 s, completed Jul 7, 2021 3:17:34 PM

### Note: Just File1 was compiled.
### The expectation was for File2 to be recompiled also
 */
