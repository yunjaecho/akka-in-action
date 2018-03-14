package com.faulttolerance

import aia.testdriven.StopSystemAfterAll
import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.WordSpecLike


/**
  * Created by USER on 2018-03-14.
  *
  * start = preStart
  *
  * stop = postStop
  *
  * restart = preRestart =>
  *            postStop : preRestart hook 에서 super.preRestart(reason, message) 호출 부분이 있어야 호출 =>
  *            Constructor : Actor 생성 =>
  *            postRestart  =>
  *            preStart : postRestart hook 에서 super.postRestart(reason) 호출 부분이 있어야 호출
  *
  */
class LifeCycleHooksTest extends TestKit(ActorSystem("testsystem")) with WordSpecLike with StopSystemAfterAll {
  "The Child" must {
    "log lifecycle" in {
      val testActorRef = system.actorOf(Props[LifeCycleHooks], "LifeCycleHooks")
      testActorRef ! "restart"
      testActorRef.tell("msg", testActor)
      expectMsg("msg")
      system.stop(testActorRef)   // testActorRef postStop (해당 actor 종료)
      Thread.sleep(1000)
    }
  }

}
