package com.faulttolerance

import akka.actor.{Actor, ActorLogging}

/**
  * Created by USER on 2018-03-14.
  */
class LifeCycleHooks extends Actor with ActorLogging{

  println("Constructor")

  override def preStart(): Unit = {
    println("preStart")
    //super.preStart()
  }

  override def postStop(): Unit = {
    println("postStop")
    //super.postStop()
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println(s"preRestart , message : $message" )
    /**
      * 해당 엑터의 모든 자식 엑터를 종료한 다음 postStop hook 호출 한다.
      */
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("postRestart")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case "restart" =>
      throw new IllegalStateException("force restart")
    case msg: AnyRef =>
      println("Receive")
      sender() ! msg
  }

}
