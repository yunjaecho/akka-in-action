package aia.testdriven

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.WordSpecLike

/**
  * Created by USER on 2018-03-12.
  */
class EchoActorTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with ImplicitSender with StopSystemAfterAll {
  "An EchoActor" must {
    "Reply with the same message it receives" in {
      val echo = system.actorOf(Props[EchoActor], "echo2")
      echo ! "some message"
      expectMsg("some message")
    }
  }
}

class EchoActor extends Actor {
  override def receive: Receive = {
    case msg =>
      sender() ! msg
  }
}
