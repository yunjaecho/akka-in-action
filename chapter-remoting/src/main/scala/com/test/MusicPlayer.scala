package com.test

import akka.actor.{Actor, ActorSystem, Props}
import akka.actor.Actor.Receive
import com.test.MusicPlayer.{StartMusic, StopMusic}


object MusicController {
  sealed trait ControllerMsg
  case object Play extends ControllerMsg
  case object Stop extends ControllerMsg
}

class MusicController extends Actor {
  import MusicController._

  override def receive: Receive = {
    case Play =>
      println("Music Started.....")
    case Stop =>
      println("Music Stopped.....")
  }
}

object MusicPlayer {
  sealed trait PlayMsg
  case object StopMusic extends PlayMsg
  case object StartMusic extends PlayMsg
}


class MusicPlayer extends Actor {
  import MusicController._

  val controller = context.actorOf(Props[MusicController], "controller")

  override def receive: Receive = {
    case StopMusic =>
      println("I don't want to stop music")
    case StartMusic =>

      println(s"controller : $controller")
      controller ! Play
    case _ =>
      println("Unknown msg")
  }
}

object MusicMain extends App {
  val system = ActorSystem("MusicMain")

  val musicPlayer = system.actorOf(Props[MusicPlayer], "musicPlayer")

  musicPlayer ! StopMusic

  musicPlayer ! StartMusic

  musicPlayer ! StartMusic

  Thread.sleep(1000)
  system.terminate()

  //musicPlayer ! StartMusic
}
