package com.test

import akka.actor.{Actor, ActorSystem, Props}

case class WhoToGreet(who: String)

class Greet extends Actor {
  println("create Greet")

  override def receive: Receive = {
    case WhoToGreet(who) =>
      println(s"Hello $who")
  }
}

object HelloAkkaScala extends App {
  val system = ActorSystem("hello-and-who")

  val greeter = system.actorOf(Props[Greet], "greet")

  greeter ! WhoToGreet("Akka")
}
