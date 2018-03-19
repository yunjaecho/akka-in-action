package com.goticks

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait StartUp extends RequestTimeout{

  def startup(api: Route)(implicit system: ActorSystem): Unit = {
    val host = system.settings.config.getString("http.host")
    val port = system.settings.config.getInt("http.port")
    startHttpServer(api, host, port)
  }

  def startHttpServer(api: Route, host: String, port: Int)(implicit system: ActorSystem) = {
    implicit val ec = system.dispatcher
    // bindAndHandle에서 암시적인 ExecutionContext가 필요한다
    implicit val materialize = ActorMaterializer()
    // Http 서버를 시작한다
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(api, host, port)

    val log = Logging(system.eventStream, "go-ticks")
    bindingFuture.map { serverBinding =>
      log.info(s"RestApi bound to ${serverBinding.localAddress}")
    }.onComplete {
      case Success(v) =>
      case Failure(ex) =>
        log.error(ex, "Failed to bind to {}:{}!", host, port)
        system.terminate()
    }
  }
}
