package app.fmgp.geo.prebuilt

import app.fmgp.geo._
import app.fmgp.dsl._

/** Isenberg School of Management Hub
  *
  * @see
  *   [[https://nbviewer.jupyter.org/github/RenataCB/Isenberg_Notebook/blob/master/Isenberg.ipynb]]
  *
  * @see
  *   [[https://github.com/RenataCB/Isenberg_Notebook]]
  *
  * @see
  *   [[http://web.ist.utl.pt/antonio.menezes.leitao/ADA/documents/publications_docs/2020_ProgramComprehensionForLiveAlgorithmicDesignInVirtualReality.pdf]]
  */
object IsenbergSchoolOfManagementHubExample {
  type XY = XYZ

  /** @param center
    *   center of the circle
    * @param r
    *   radius of the circle
    * @param alfa_init
    *   beginning angle for the circular distribution of points
    * @param alfa_end
    *   ending angle for the circular distribution of points
    * @param n
    *   number of points created
    * @return
    *   n+1 points in a circle
    */
  def pts_circle(center: XY, r: Double, alfa_init: Double, alfa_end: Double, n: Int): Seq[XY] = {
    (0 to n)
      .map(_ * (alfa_end - alfa_init) / n + alfa_init)
      .map(alfa => Polar(rho = r, phi = alfa))
      .map(_.toXY0 + center)
  }

  /** @param ri
    *   slab interior radius
    * @param re
    *   slab exterior radius
    * @param alfa_proj
    *   circle angle at which the building's columns start to tilt. At this point the slab deviates from the circular
    *   path to accompany their movement
    * @param thick
    *   slab thickness
    * @param is_first
    *   boolean value: is it the first slab? The base slab of the building is the only one whose shape changes at the
    *   alfa_proj angle to accompany the tilted columns
    */
  //def isenberg_slab(ri: Double, re: Double, alfa_proj: Double, thick: Double, is_first: Boolean): Seq[XY] = ???
  class Isenberg(
      center: XY,
      ri: Double,
      re: Double,
      alfa_init: Double,
      alfa_proj: Double,
      alfa_end: Double,
      n: Int,
      slabThickness: Double = 0.1,
  ) {
    //ps𝑖 = ps_circle (c , r𝑖 , 𝛼0 , 𝛼𝑒 , n )
    def psi(n: Int) = pts_circle(center, ri, alfa_init, alfa_end, n)
    //ps𝑒 = ps_circle (c , r𝑒 , 𝛼0 , 𝛼𝑒 , n )
    def pse(n: Int) = pts_circle(center, re, alfa_init, alfa_end, n)
    //ps𝑝 = ps_circle (c , r𝑒 , 𝛼0 , 𝛼𝑝 , n )
    def psp(n: Int) = pts_circle(center, re, alfa_init, alfa_proj, n)

    // p𝑡 = c + vcyl ( r𝑒 , 𝛼𝑝 , 0)
    // Δ𝛼 = 𝛼𝑒 - 𝛼𝑝
    // v = vpol (Δ𝛼 *2 r𝑒 /𝜋 , 𝛼𝑝 + Δ𝛼)
    val pr = center + Polar(rho = re, phi = alfa_proj).toXY0 //Cylindrical(rho = re, phi = alfa_proj, 0)
    val deltaAlfa = alfa_end - alfa_init
    val v = Polar(deltaAlfa * 2 * re * math.Pi, alfa_proj + deltaAlfa)

    // ps = base ?
    // [ ps𝑝 ... , p𝑡 + v , ps𝑒 [end] , reverse ( ps𝑖 ) ...]
    // [ ps𝑒 ... , reverse ( ps𝑖 ) ...]

    private val nQuality = n * 5
    def floorPoints = psp(nQuality)
      ++ Seq(pse(1).last + Vec(center, pse(1).last).perpendicularInXY2D, pse(1).last)
      ++ psi(nQuality).reverse
    def roofPoints = pse(nQuality) ++ psi(nQuality).reverse

    // slab ( closed_polygonal_path ( ps ) )
    // val rotated = ps.drop(1) ++ ps.take(1) //(ps ++ ps).slice(1, 1 + ps.length)
    // val aux = ps.zip(rotated).map((s, e) => LinePath(s, e))

    private def slabAux(points: Seq[XY]) = Extrude(
      path = MultiPath(LinePath(points)),
      holes = Seq(),
      options = Some(Extrude.Options(depth = Some(slabThickness), bevelEnabled = Some(false), steps = Some(1)))
    )

    def slabFloor = slabAux(floorPoints)
    def slabRoof(height: Double) = slabAux(roofPoints).transformWith(Matrix.translate(z = height))
    def beams(height: Double) = {
      val h = height + slabThickness
      Box(10 * slabThickness, slabThickness * 5, h)
        .transformWith(
          Matrix
            .translate(z = h / 2)
          //.postTranslate(x = re - 3 * slabThickness)
          //.preRotate(0.1 * math.Pi, Vec(z = 1))
        )
        .transformWith(Matrix.translate(re - 3 * slabThickness, 0, 0))
        .transformWith(Matrix.rotate(0.1 * math.Pi, Vec(z = 1)))
        .transformWith(Matrix.rotate(0.1 * math.Pi, Vec(z = 1)))

    }
  }

  // function slabs (c , r𝑖 , r𝑒 , 𝛼0 , 𝛼𝑝 , 𝛼𝑒 , fℎ , floors , n )
  // isenberg_slab (c , r𝑖 , r𝑒 , 𝛼0 , 𝛼𝑝 , 𝛼𝑒 , n , true )
  // for i in 1: floors
  // isenberg_slab ( c + vz ( fℎ * i ) , r𝑖 , r𝑒 , 𝛼0 , 𝛼𝑝 , 𝛼𝑒 , n , false )
  // end
  // end

}
