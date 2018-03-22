package com.goticks

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by USER on 2018-03-20.
  */
object BackendRemoteDeployMain extends App {
  val config = ConfigFactory.load("backend")
  val system = ActorSystem("backend", config)
}
