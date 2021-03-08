package curves.example

import curves.draw.SDL._

object Main{

    def main(args: Array[String]): Unit = {
        val points = args.lift(1).getOrElse("100000").toInt
        val order = args.lift(2).getOrElse("9").toInt//9=512x512 ,10=1024x1024... WindowSize= power(2,order);
        val dotColor = args.lift(3).getOrElse("true") match { 
            case w if w == "0" | w == "white" | w == "false"  => false ;
            case b if b == "1" | b == "black" | b == "true"  => true
            case _ => true } 
        val size = 1 << order
        val dot = if(dotColor) "black" else "white"
   
        if(args.nonEmpty){
             args(0) match{
                case "hashCode"    =>  draw(points,order,dotColor,(c: Int) => (c.toString).hashCode)
                case "djb2"        =>  draw(points,order,dotColor,djb2 _)
                case "djb2_xor"    =>  draw(points,order,dotColor,djb2_xor _)
                case "murmur3"     =>  draw(points,order,dotColor,mumur3 _)
                case "primes"      =>  draw(points,order,dotColor,primes _)
                case "random"      =>  draw(points,order,dotColor,random _)
                case "--help"      =>  println(pritnHelp("Help Menu.",points,order,size,dot))
                case "-h"          =>  println(pritnHelp("Help Menu.",points,order,size,dot))
                case isFile(path)  =>  drawFile(path,order,dotColor)
                case _             =>  println(pritnHelp("Bad input. Enterd: " + args(0),points,order,size,dot))
                }

        } else println(pritnHelp("Empty input. ",points,order,size,dot))
    }

    private val targetBin = "./example/hashdraw/native/target/scala-2.13/hashdraw-out" 

    def pritnHelp(firstMessage: String,points: Int,order: Int,size: Int,dot: String) = 
    s"${firstMessage}Supported hash functions: arg(0)=hashCode|djb2|djb2_xor|murmur3|primes|random\n" +
    s"Other possible args:\n"+
    s"arg(1)=(default=$points), Number of points to create.\n"  +
    s"arg(2)=(default=$order) , Order of hilbert curve. This makes WindowSize(default=${size}x${size},size is:  2**arg(2))\n" +
    s"arg(3)=(default=$dot), Color of the pixles. Can be arg(3)=black, or arg(3)=white \n\n"+
    s"""Exmaple(prime, 200000 points, order 10 (WindowSize(1024x1024), white data points):\n $targetBin primes 200000 10 white \n\n""" +
    s"""Exmaple(mumur3, 100000 points, order 9 (WindowSize(512x512), black data points):\n $targetBin murmur3"\n"""

    object isFile{
    def unapply(path: String): Option[String] = {
      val file = new java.io.File(path)
        if(file.exists && file.isFile) Some(path) else None
     }
    }

    def random(i: Int): Int = util.Random.nextInt(10000000)

    def mumur3(i: Int): Int = scala.util.hashing.MurmurHash3.stringHash(i.toString)

    def primes(i: Int): Int =  5839 * i ^ 12143 * 89;


    def djb2_xor(int : Int): Int = {
        val str = int.toString
        var hash = 5381
        var c = 0
        while (c < str.length){
        hash = hash * 33 ^ str(c)
         c+=1
        }
        hash
    }

    def djb2(int : Int): Int = {
        val str = int.toString
        var hash = 5381
        var c = 0
        while (c < str.length){
        hash = ((hash << 5) + hash) + str(c); /* hash * 33 + c */
         c+=1
        }
        hash
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
