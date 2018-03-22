package com.test

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, PoisonPill, Props}

/**
  * Created by USER on 2018-03-22.
  */
object ActorPath extends App {
  val system = ActorSystem("Actor-Paths")

  val counter1 = system.actorOf(Props[Counter], "counter")

  println(s"Actor Reference for counter1 : ${counter1}")

  val counterSelection1 = system.actorSelection("counter")

  println(s"Actor Selection for couner1: ${counterSelection1}")

  counter1 ! PoisonPill

  Thread.sleep(1000)

  val counter2 = system.actorOf(Props[Counter], "counter")

  println(s"Actor Reference for counter2 : ${counter2}")

  val counterSelection2 = system.actorSelection("counter")

  println(s"Actor Selection for couner2: ${counterSelection2}")

  counter2 ! PoisonPill

  Thread.sleep(1000)

  system.terminate()
}


class Counter extends Actor {

  import Counter._

  var count = 0

  override def receive: Receive = {
    case Inc(x) =>
      count += x
    case Dec(x) =>
      count -= x
  }
}


object Counter {
  final case class Inc(num: Int)
  final case class Dec(num: Int)
}