package aia.testdriven

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by USER on 2018-03-12.
  */
class Creeter02Test extends TestKit(ActorSystem("testsystem")) with WordSpecLike with StopSystemAfterAll {
  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val props = Greeter02.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter02-1")
      greeter ! Greeting("World")
      expectMsg("Hello World!")
    }
  }
}

object Greeter02 {
  def props(listener: Option[ActorRef] = None) = Props(new Greeter02(listener))
}

class Greeter02(listener: Option[ActorRef]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(who) =>
      val message = "Hello " + who + "!"
      log.info(message)
      listener.foreach(_ ! message)
  }
}