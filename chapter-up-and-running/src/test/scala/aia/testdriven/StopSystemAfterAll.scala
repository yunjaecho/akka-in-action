package aia.testdriven

import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

/**
  * Created by USER on 2018-03-12.
  */
trait StopSystemAfterAll extends BeforeAndAfterAll{
  this: TestKit with Suite =>
  override protected def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }

}
