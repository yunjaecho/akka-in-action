package com.test

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import com.test.Aphrodite.{RestartException, ResumeException, StopException}

import scala.concurrent.duration._

class Aphrodite extends Actor {
  import Aphrodite._
  override def preStart() = {
    println("Aphrodite preStart hook.............")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("Aphrodite preRestart hook.............")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("Aphrodite postRestart hook.............")
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    println("Aphrodite postStop hook.............")
  }

  override def receive: Receive = {
    case "Resume" =>
      throw ResumeException
    case "Stop" =>
      throw StopException
    case "Restart" =>
      throw RestartException
    case _ =>
      throw new Exception
  }
}

object Aphrodite {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
}

class Hera extends Actor {

  var childRef: ActorRef = _

  override val supervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
      case ResumeException => Resume
      case RestartException => Restart
      case StopException => Stop
      case _: Exception => Escalate
    }
  }

  override def preStart(): Unit = {
    childRef = context.actorOf(Props[Aphrodite], "Aphrodite")
    Thread.sleep(100)
  }

  override def receive: Receive = {
    case msg =>
      println(s"Here received $msg")
      childRef ! msg
      Thread.sleep(100)
  }
}


object Supervision extends App {
  val system = ActorSystem("supervision")

  val hera = system.actorOf(Props[Hera], "hera")

//  hera ! "Resume"
//  Thread.sleep(1000)



  hera ! "Restart"
  Thread.sleep(1000)

  system.terminate()

}

