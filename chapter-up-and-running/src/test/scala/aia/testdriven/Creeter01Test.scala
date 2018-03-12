package aia.testdriven

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.{MustMatchers, WordSpecLike}
import Creeter01Test._

/**
  * Created by USER on 2018-03-12.
  */
class Creeter01Test extends TestKit(testSystem) with WordSpecLike with StopSystemAfterAll {
  "The Greeter" must {
    "say Hello World! when a Greeting('World') is sent to it" in {
      val dsipatcherId = CallingThreadDispatcher.Id
      val props = Props[Greeter].withDispatcher(dsipatcherId)
      val greeter = system.actorOf(props)

      EventFilter.info(message = "Hello World", occurrences = 1).intercept(greeter ! Greeting("World"))
    }
  }
}

object Creeter01Test {
  val testSystem = {
    val config = ConfigFactory.parseString(
      """
        akka.loggers = [akka.testkit.TestEventListener]
      """)
    ActorSystem("testsystem", config)
  }
}


class Greeter extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(message) =>
      log.info("Hello {}", message)
  }
}
