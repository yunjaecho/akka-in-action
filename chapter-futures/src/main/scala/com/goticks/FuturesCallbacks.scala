package com.goticks

import scala.concurrent._
import ExecutionContext.Implicits.global  //import scala.concurrent.ExecutionContext.Implicits.global( 위에서 지정한 import 영향)
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success}

object FuturesCallbacks extends App {

  def getUrlSpec(): Future[Seq[String]] = Future {
    println(Thread.currentThread().getName)
    throw new Exception("error")
    val f = Source.fromURL("http://www.w3.org/Addressing/URL/url-spec.txt.aaaa")
    try f.getLines.toList finally f.close
  }

  def find(lines: Seq[String], word: String): Future[String] = Future {
    println(Thread.currentThread().getName)
    throw new Exception("error")
    lines.zipWithIndex collect {
      case (line, n) if line.contains(word) => (n, line)
    } mkString("\n")
  }

  val urlSpec: Future[Seq[String]] = getUrlSpec()

  val result: Future[String] = urlSpec.flatMap { lines =>
    find(lines, "use").recover {
      case _ : Exception => "abcdefg"
    }
  }.recover {
    case e => "qwertt"
  }

  result.onComplete {
    case Success(lines) => print(Thread.currentThread().getName);println(lines)
    case Failure(e) => println("error : " + e)
  }

  /*val futureFail = Future {throw new Exception("error")}
  futureFail.onComplete {
    case Success(value) => println(value)
    case Failure(e) => println(e.printStackTrace())
  }*/


  Thread.sleep(10000)





}
