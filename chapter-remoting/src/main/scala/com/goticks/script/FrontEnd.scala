package com.goticks.script

/**
  * Created by USER on 2018-03-19.
  */
object FrontEnd extends App {
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
      port = 2552
    }
  }
}
  """

  import com.typesafe.config._
  import akka.actor._

  val config = ConfigFactory.parseString(conf)
  val frontend = ActorSystem("frontend", config)

  val path = "akka.tcp://backend@0.0.0.0:2551/user/simple"

  val simple = frontend.actorSelection(path)

  simple ! "Hello Remote World!"


}
