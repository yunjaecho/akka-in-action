package com.goticks

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object FrontendMain extends App with StartUp {
  val config = ConfigFactory.load("fronted")
  implicit val system = ActorSystem("fronted", config)

  val api = new RestApi() {
    val log = Logging(system.eventStream, "frontend")
    implicit val requestTimeout = configuredRequestTimeout(config)
    implicit def executionContext = system.dispatcher

    def createPath(): String = {
      val host = config.getString("host")
      val port = config.getInt("port")
      val protocol = config.getString("protocol")
      val systemName = config.getString("system")
      val actorName = config.getString("actor")
      s"$protocol://$systemName@$host:$port/$actorName"
    }

    def createBoxOffice: ActorRef = {
      val path = createPath()
      system.actorOf(Props(new RemoteLookupProxy(path)), "lookupBoxOffice")
    }
  }

  startup(api.routes)
}
