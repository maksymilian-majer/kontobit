package com.hackwaw

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[KontomierzServiceActor], "demo-service")

  val feedActor = system.actorOf(Props[FeedKontomierzActor], "feed-kontomierz")
  val queryActor = system.actorOf(Props(new QueryDbActor(feedActor)), "query-db")

  system.scheduler.schedule(0 seconds, 15 seconds, queryActor, Message())

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
