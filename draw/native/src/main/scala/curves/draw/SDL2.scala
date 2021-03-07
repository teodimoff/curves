package curves
package draw

import curves.core.value._
import scalanative.unsafe._
import scalanative.unsigned._

object SDL{

@extern
@link("SDL2")
object SDL2{

def drawGradientAndPoints(points: Int,
                          order: Int,
                          startFrom: Int,
                          height: Int,
                          width: Int,
                          dotColor: Int,
                          hashFunction: CFuncPtr1[Int,Int],
                          hilbertMapping: CFuncPtr2[Int,Int,Long]): Unit = extern
}


def draw(points: Int,order: Int,blackDot: Boolean,hashFunction: CFuncPtr1[Int,Int]): Unit = 
        SDL2.drawGradientAndPoints(points,order,0,1 << order,1 << order,if(blackDot) 0 else 1 ,hashFunction,(points: Int,order: Int) => HilbertCurve(points,order).asLong)

def draw(points: Int,order: Int,blackDot: Boolean,hashFunction: CFuncPtr1[Int,Int], hilbertMapping: CFuncPtr2[Int,Int,Long]): Unit = 
        SDL2.drawGradientAndPoints(points,order,0,1 << order,1 << order,if(blackDot) 0 else 1 ,hashFunction,hilbertMapping)

}

