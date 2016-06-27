package com.kkanojia.example.services

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Random

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}

//TODO Clean database
class UserServiceSpec extends PlaySpec with OneAppPerTest with ScalaFutures {

  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(50, Millis))

  "A User Service" must {

    "be able to create a user" in {
      //Arrange
      val email = s"john@${Random.nextInt(999999)}"
      val userService = app.injector.instanceOf[UserService]

      //Act
      val userPromise = userService.createUser(email)

      //Assert
      whenReady(userPromise) {
        case Some(user) =>
          user.email mustBe email
        case None => fail
      }
    }

    "be able to retrieve a user" in {
      //Arrange
      val email = s"jane@${Random.nextInt(999999)}"
      val userService = app.injector.instanceOf[UserService]
      Await.result(userService.createUser(email), 5 seconds)

      //Act
      val retrievalPromise = userService.retrieveUser(email)

      //Assert
      whenReady(retrievalPromise){
        case Some(retrievedUser) =>
          retrievedUser.email mustBe email

        case None => fail
      }

    }

  }

}
