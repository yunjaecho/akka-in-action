package aia.testdriven

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by USER on 2018-03-12.
  */
class FilteringActorTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with MustMatchers with StopSystemAfterAll {
  import FilteringActor._

  "A Filtering Actor" must {
    "filter out particular messages" in {
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filtter-1")

      filter ! Event(1)
      filter ! Event(2)
      filter ! Event(1)
      filter ! Event(3)
      filter ! Event(1)
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      filter ! Event(6)

      val eventIds = receiveWhile() {
        case Event(id) if id <=5 => id
      }

      eventIds must be (List(1,2,3,4,5))
      expectMsg(Event(6))
    }

    "filter out particular messages using expectNoMsg" in {
      import FilteringActor._
      val props = FilteringActor.props(testActor, 5)
      val filter = system.actorOf(props, "filter-2")
      filter ! Event(1)
      filter ! Event(2)
      expectMsg(Event(1))
      expectMsg(Event(2))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(3)
      expectMsg(Event(3))
      filter ! Event(1)
      expectNoMsg
      filter ! Event(4)
      filter ! Event(5)
      filter ! Event(5)
      expectMsg(Event(4))
      expectMsg(Event(5))
      expectNoMsg()
    }
  }
}

object FilteringActor {
  case class Event(id: Long)

  def props(nextActor: ActorRef, bufferSize: Int) = Props(new FilteringActor(nextActor, bufferSize))
}

class FilteringActor(nextActor: ActorRef, bufferSize: Int) extends Actor {
  import FilteringActor._
  var lastMessages = Vector[Event]()

  override def receive: Receive = {

    case msg: Event =>
    if (!lastMessages.contains(msg)) {
      lastMessages = lastMessages :+ msg
      nextActor ! msg
      if (lastMessages.size > bufferSize) {
        lastMessages = lastMessages.tail
      }
    }
  }
}
