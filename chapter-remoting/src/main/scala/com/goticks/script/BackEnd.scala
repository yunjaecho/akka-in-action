package com.goticks.script

/**
  * Created by USER on 2018-03-19.
  */
object BackEnd extends App {
  val conf = s"""
  akka {
  version = 2.5.0
  loglevel = DEBUG
  stdout-loglevel = WARNING
  loggers = ["akka.event.slf4j.Slf4jLogger"]

  actor {
    provider = "akka.remote.RemoteActorRefProvider"
  }

  remote {
    enabled-transports = ["akka.remote.netty.tcp"]
    netty.tcp {
      hostname = "0.0.0.0"
      port = 2551
    }
  }
}
  """
  import com.typesafe.config._
  import akka.actor._

  val config = ConfigFactory.parseString(conf)
  val backend = ActorSystem("backend", config)


  class Simple extends Actor {
    override def receive: Receive = {
      case m =>
        println(s"received $m!")
    }
  }

  backend.actorOf(Props[Simple], "simple")
}
