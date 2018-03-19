package com.goticks

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App
  with RequestTimeout {

  // 설정으로부터 호스트와 포트를 가져온다
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  // bindAndHandle은 비동기적이며, ExecutionContext를 암시적으로 사용해야 한다
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher

  val api = new RestApi(system, requestTimeout(config)).routes // RestApi는 HTTP 루트를 제공한다

  implicit val materializer = ActorMaterializer()
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(api, host, port) // RestApi 루트를 가지고 HTTP 서버를 시작한다

  val log =  Logging(system.eventStream, "go-ticks")
  bindingFuture.map { serverBinding =>
    log.info(s"RestApi bound to ${serverBinding.localAddress} ")
  }.onComplete {
    case Success(v) =>
    case Failure(ex) =>
      log.error(ex, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}
