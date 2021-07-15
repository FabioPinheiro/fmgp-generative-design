package app.fmgp

val avoidWarn2 = """
[warn] 1 |package app.fmgp
[warn]   |^^^^^^^^^^^^^^^^
[warn]   |No class, trait or object is defined in the compilation unit.
[warn]   |The incremental compiler cannot record the dependency information in such case.
[warn]   |Some errors like unused import referring to a non-existent class might not be reported.
"""

extension (i: Int) def extensionMethod: String = "hello"
