package com.goticks

import scala.concurrent.duration._
import akka.actor.{Actor, ActorIdentity, ActorLogging, ActorRef, Identify, ReceiveTimeout, Terminated}

class RemoteLookupProxy(path: String) extends Actor with ActorLogging{
  context.setReceiveTimeout(3 seconds)  // 3초 동안 아무 메시지도 도착하지 않으면 ReceiveTimeout 메세지를 보낸다
  sendIdentifyRequest() // 액터를 즉시 실별하기 위한 요청을 시작한다

  def sendIdentifyRequest(): Unit = {
    val selection = context.actorSelection(path)  // 경로로 액터를 선택한다
    selection ! Identify(path)  // actorSelection Identify 메시지를 보낸다
  }


  override def receive: Receive = identify

  def identify: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>  //액터를 식별하고 ActorRef를 반환한다
      // 이제 활성 상태이므로 액터에게 메시지가 도착하지 않을때 ReceiveTimeout 메세지를 보내지 않는다
      context.setReceiveTimeout(Duration.Undefined)
      log.info("switching to active state")
      context.become(active(actor)) // 액터상태를 활성(active)수신 상태로 만든다
      context.watch(actor)
    case ActorIdentity(`path`, None) =>
      log.error(s"Remote actor with path $path is not available")

    case ReceiveTimeout =>
      sendIdentifyRequest()

    case msg: Any =>
      log.error(s"Ignoring message $msg, not ready yet.")
  }

  def active(actor: ActorRef): Receive = {
    case Terminated(actorRef) =>
      log.info(s"Actor $actorRef terminated")
      log.info("switching to identify state")
      context.become(identify)
      context.setReceiveTimeout(3 seconds)
      sendIdentifyRequest()
    case msg: Any =>
      actor forward msg
  }

}
