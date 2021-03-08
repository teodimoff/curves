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

def drawFile(fileName: CString, order: Int, height: Int, width: Int, color: Int, hilbertMapping: CFuncPtr2[Int,Int,Long]): Unit = extern

}

def drawFile(fileName: String,order: Int, height: Int, width: Int, blackDot: Boolean, hilbertMapping: CFuncPtr2[Int,Int,Long]): Unit =  Zone { implicit z =>
  SDL2.drawTest(toCString(fileName),order,height,width,if(blackDot) 0 else 1,hilbertMapping)
}
def drawFile(fileName: String, order: Int, blackDot: Boolean): Unit =  Zone { implicit z =>
  SDL2.drawTest(toCString(fileName),order,1 << order,1 << order,if(blackDot) 0 else 1,(points: Int,order: Int) => HilbertCurve(points,order).asLong)
}
def draw(points: Int,order: Int,blackDot: Boolean,hashFunction: CFuncPtr1[Int,Int]): Unit = 
        SDL2.drawGradientAndPoints(points,order,0,1 << order,1 << order,if(blackDot) 0 else 1 ,hashFunction,(points: Int,order: Int) => HilbertCurve(points,order).asLong)

def draw(points: Int,order: Int,blackDot: Boolean,hashFunction: CFuncPtr1[Int,Int], hilbertMapping: CFuncPtr2[Int,Int,Long]): Unit = 
        SDL2.drawGradientAndPoints(points,order,0,1 << order,1 << order,if(blackDot) 0 else 1 ,hashFunction,hilbertMapping)

}

