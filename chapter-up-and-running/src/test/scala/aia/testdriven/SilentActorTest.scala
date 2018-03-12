package aia.testdriven


import akka.actor._
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

package silentactor02 {

  class SilentActorTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with MustMatchers with StopSystemAfterAll {

    import SilentActor._

    "A Silent Actor" must {
      "change state when it receives a message, single threaded" in {
        val silentActor = TestActorRef[SilentActor]
        silentActor ! SilentMessage("whisper")
        silentActor.underlyingActor.state must (contain("whisper"))
      }
    }

  }

  class SilentActor extends Actor {

    import aia.testdriven.silentactor02.SilentActor._

    var internalState = Vector[String]()

    override def receive: Receive = {
      case SilentMessage(data) =>
        internalState = internalState :+ data
    }

    def state = internalState
  }


  object SilentActor {

    case class SilentMessage(data: String)

    case class GetState(receiver: ActorRef)

  }
}


package silentactor03 {

  class SilentActorTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with MustMatchers with StopSystemAfterAll {

    import SilentActor._

    "A Silent Actor" must {
      "change state when it receives as message, multi-treaded" in {
        val silentActor = system.actorOf(Props[SilentActor], "s3")
        silentActor ! SilentMessage("whisper1")
        silentActor ! SilentMessage("whisper2")
        silentActor ! GetState(testActor)
        expectMsg(Vector("whisper1", "whisper2"))
      }
    }

  }

  class SilentActor extends Actor {

    import aia.testdriven.silentactor03.SilentActor._

    var internalState = Vector[String]()

    override def receive: Receive = {
      case SilentMessage(data) =>
        internalState = internalState :+ data
        println(Thread.currentThread().getName)
      case GetState(receiver) =>
        receiver ! internalState
    }
  }


  object SilentActor {

    case class SilentMessage(data: String)

    case class GetState(receiver: ActorRef)

  }
}
