package com.test

import akka.actor._

/**
  * Created by USER on 2018-03-22.
  */
class Watcher extends Actor {
  var counterRef: ActorRef = _

  val selection = context.actorSelection("/user/counter")

  selection ! Identify("1")
  selection ! Identify("2")

  override def receive: Receive =  {
    case ActorIdentity(id, Some(ref)) =>
      println(s"correlationId : $id")
      println(s"Actor Reference for counter is ${ref}")
    case ActorIdentity(_, None) =>
      println("Actor selection for actor doesn't live")
  }
}

object Watch extends App {
  val system = ActorSystem("watsh-actor-selection")

  val counter = system.actorOf(Props[Counter], "counter")
  val watcher = system.actorOf(Props[Watcher], "watcher")

  Thread.sleep(1000)
  system.terminate()
}
