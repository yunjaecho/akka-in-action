package sample

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive

import sample.MusicPlayer.{StartMusic, StopMusic}

/**
  * Created by USER on 2018-03-12.
  */
object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg

  def props = Props[MusicController]
}

class MusicController extends Actor{
  import MusicController._

  override def receive: Receive = {
    case Play =>
      println("Music Started .....")
    case Stop =>
      println("Music Stop ........")

  }
}


object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object StartMusic extends PlayMsg
}

class MusicPlayer extends Actor {
  import sample.MusicController._

  val controller = context.actorOf(props, "controller")

  override def receive: Receive = {
    case StopMusic =>
      controller ! Stop
    case StartMusic =>
      controller ! Play
    case _ =>
      println("Unknown Message")

  }
}


object ActorCreation extends App {
  val system = ActorSystem("creation")
  val player = system.actorOf(Props[MusicPlayer], "play")

  player ! StartMusic
  player ! StopMusic
}
