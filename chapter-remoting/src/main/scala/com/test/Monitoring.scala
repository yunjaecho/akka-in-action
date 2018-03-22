package com.test

import akka.actor.Actor.Receive
import akka.actor._

class Area(athena: ActorRef) extends Actor {

  override def preStart(): Unit = {
    context.watch(athena)
  }

  override def postStop(): Unit = {
    println("Ares postStop....")
  }

  override def receive: Receive = {
    case Terminated =>
      println("Terminated")
      //context.stop(self)
    case _ =>
      println("Unknown message")
  }
}

class Athena extends Actor {
  override def receive: Receive = {
    case msg =>
      println(s"Athena received ${msg}")
      context.stop(self)
  }
}


object Monitoring extends App {
  val system =  ActorSystem("Monitoring")

  val athena = system.actorOf(Props[Athena], "athena")

  val area = system.actorOf(Props(classOf[Area], athena), "area")

  athena ! "Hi"

  athena ! "Hi"

  area ! "Hi"

  Thread.sleep(500)

  //system.terminate()


}
