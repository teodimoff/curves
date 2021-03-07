package curves
package core


final class Point(val x: Int,val y: Int){
    def swap: Point = new Point(y,x)
    override def toString: String = s"Point($x,$y)"  
  }

object Point{
  def apply(x: Int,y: Int): Point = new Point(x,y)
}

//https://en.wikipedia.org/wiki/Hilbert_curve
object HilbertCurve{
  //power of 2
  @scala.annotation.tailrec
  final def pow2(power: Int,res: Int = 1): Int = if(power == 0) res else pow2(power - 1,res * 2)

  final def rot(n: Int, rx: Int, ry: Int,point: Point): Point =
    if (ry == 0)
      if (rx == 1) Point((n - 1) - point.y,(n - 1) - point.x) else point.swap
    else  point

  //Map a index from 1D to point in 2D space preserving locality behavior (x,y)
  final def indexToPoint(index: Int,order: Int): Point = {
    @scala.annotation.tailrec
    def indexToPoint_(n: Int,s: Int,t: Int,point: Point): Point = {
      if (s < n) {
        val  rx = (t & 2) >> 1
        val  ry = ((t ^ rx) & 1)
        val  nextPoint =  rot(s, rx, ry,point)
        indexToPoint_(n,s << 1, t >>> 2,Point(nextPoint.x + (s*rx),nextPoint.y + (s*ry)))
      }
      else point
    }
    indexToPoint_(pow2(order),1,index,Point(0,0))
  }
  //Map a point to 1 dimention.
  final def pointToIndex(point: Point,order: Int): Int = {
    val n = pow2(order)
    @scala.annotation.tailrec
    def indexToPoint_(s: Int,index: Int,point: Point): Int = {
      if (s > 0) {
        val rx = if((point.x & s) >0) 1 else 0
        val ry = if((point.y & s) >0) 1 else 0
        indexToPoint_(s >>> 1, index + s * s * ((3 * rx) ^ ry), rot(n, rx, ry, point))
      }
      else index
    }
    indexToPoint_(n >>> 1,0,point)
  }

  def apply(index: Int,order: Int): Point = indexToPoint(index,order)
  def unapply(point: Point,order: Int): Option[Int] = Some(pointToIndex(point,order))

  object Mapping{
    implicit class HilbertIndexOps(private val index: Int) extends AnyVal{
      final def hilbert2D(order: Int): Point = indexToPoint(index,order)
    }
  }

}

/*
object Main{

    def main(args: Array[String]): Unit = {

        val upper = args.lift(1).getOrElse("1").toInt
        val range = 1 to upper
        val order = args.lift(2).getOrElse("9").toInt
        
        args(0) match {
            case "custom" =>
            println(time(HilbertCurve(upper,order).tuple))
            println(time(HilbertCurve2(upper,order)))

            case "hilbert1" =>  
            println(s"Range: (1 to $upper), Test(10000,$order)=${HilbertCurve(10000,order).tuple}, order: $order")
            time(range.foreach(i => HilbertCurve(i,order)))

            case "hilbert2" => 
            println(s"Range: (1 to $upper), Test(10000,$order)=${HilbertCurve2(10000,order)}, order: $order")
            time(range.foreach(i => HilbertCurve2(i,order)))
        }


  def time[A](a: => A) = {
    val now = System.nanoTime()
    val result = a
    val nano = System.nanoTime() - now
    val micros = nano / 1000
    val seconds = micros.toFloat / 1000 / 1000
    println("%d nanoseconds".format(nano), "%d microseconds".format(micros), "%.3f seconds".format(seconds))
    result
  }

    }
}
*/

