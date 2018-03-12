package aia.testdriven

import aia.testdriven.SendingActor.{SortEvent, SortedEvent}
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.util.Random

/**
  * Created by USER on 2018-03-12.
  */
class SendingActorTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with MustMatchers with StopSystemAfterAll {
  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      import SendingActor._

      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 100
      val maxInclusive = 100000

      def randomEvents() = (0 until(size)).map(_ =>
        Event(Random.nextInt(maxInclusive))
      ).toVector

      val unsorted = randomEvents()

      val sortEvent = SortEvent(unsorted)
      sendingActor ! sortEvent

      expectMsgPF() {
        case SortedEvent(events) =>
          events.size must be(size)
          unsorted.sortBy(_.id) must be(events)
      }
    }
  }
}

object SendingActor {
  case class Event(id: Long)
  case class SortEvent(unsorted: Vector[Event])
  case class SortedEvent(sorted: Vector[Event])

  def props(receiver: ActorRef) = Props(new SendingActor(receiver))
}

class SendingActor(receiver: ActorRef) extends Actor {

  override def receive: Receive = {
    case SortEvent(unsorted) =>
      receiver ! SortedEvent(unsorted.sortBy(_.id))
  }
}