package curves
package core

object value{

//  pack convention is - name(num)(type)(bit size)       //type[Int,Chat,Long,..] e.g.  pack2I32= two 32 bit ints
//unpack convention is - name(num)(type)(bit size)(type) //type[Int,Chat,Long,..] e.g.unpackI32I= get 32 bit int as Int
                                                         //                           unpackI32L= get 32 bit int as Long
 private final def pack2I32(hi: Int,lo: Int): Long = ((lo: Long) << 32) | (hi & 0xffffffffL)
 private final def unpackI32I(packed: Long,index: Int): Int = (packed >> (32 * index)).toInt
 private final def unpackI32L(packed: Long,index: Int): Long = (packed & (0xFFFFFFFFL << 32 * index)) >> (32 * index)
 private final val _2 = 0xFFFFFFFF00000000L //second need >> 32
 private final val _1 = 0xFFFFFFFFL

 final def Point(x: Int,y: Int): Point = pack2I32(x,y)
 final def Point(x: Long,y: Long): Point = pack2I32(x.toInt,y.toInt)

  implicit class Point(private val point: Long) extends AnyVal{
    final def asPoint: Point = point
    final def asLong: Long = point
    final def x: Long = unpackI32L(point,0)
    final def y: Long = unpackI32L(point,1)
    final def xInt: Int = unpackI32I(point,0)
    final def yInt: Int = unpackI32I(point,1)

    final def zeroX: Point = point & _2
    final def zeroY: Point = point & _1

    final def swap : Point = pack2I32(y.toInt,x.toInt)
    final def tuple: (Int,Int) = (x.toInt,y.toInt)

    //can we support x_+=,x_*=,x_/=,x_%=,x_^=,x_-= ...
    final def x_=(newX: Long):   Point =  (point & _2) | (newX & _1)
    final def plusX(add: Long):  Point = (point & _2) | (point.x + add)
    final def minusX(add: Long): Point = (point & _2) | (point.x - add)
    final def mulX(add: Long):   Point = (point & _2) | (point.x * add)
    final def divX(add: Long):   Point = (point & _2) | (point.x / add)

    //can we support y_+=,y_*=,y_/=,y_%=,y_^=,y_-= ...
    final def y_=(newY: Long):   Point = (point & _1) | ((newY & _1) << 32)
    final def plusY(add: Long):  Point = (point & _1) | (point.y + add)
    final def minusY(add: Long): Point = (point & _1) | (point.y - add)
    final def mulY(add: Long):   Point = (point & _1) | (point.y * add)
    final def divY(add: Long):   Point = (point & _1) | (point.y / add)

    final def hilbert1D(order: Int): Int = HilbertCurve.pointToIndex(point,order)
  }

  implicit class PointOpsInt(private val XorY: Int) extends AnyVal{
    final def asX: Point =  XorY | 0L
    final def asY: Point = (XorY | 0L) << 32
    final def withY(newY: Int): Point = Point(XorY,newY)
    final def withX(newX: Int): Point = Point(newX,XorY)
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
       val rx = if((point.xInt & s) >0) 1 else 0
       val ry = if((point.yInt & s) >0) 1 else 0
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

}

