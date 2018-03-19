package com.goticks

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import com.typesafe.config.ConfigFactory

object SingleNodeMain extends App with StartUp {

  val config = ConfigFactory.load("singlenode")
  implicit val system = ActorSystem("singlenode", config)

  val api = new RestApi() {
    val log = Logging(system.eventStream, "go-ticks")
    implicit val requestTimeout = configuredRequestTimeout(config)
    implicit def executionContext = system.dispatcher

    def createBoxOffice: ActorRef = system.actorOf(BoxOffice.props, BoxOffice.name)
  }

  startup(api.routes)

}
