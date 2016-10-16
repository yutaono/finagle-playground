package com.example

import java.net.{InetAddress, InetSocketAddress}

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, Response, Status}
import com.twitter.finagle.Service
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

import scala.collection.mutable


object TestServer extends TwitterServer {

  val service = new Service[Request, Response] {
    val counterMap = mutable.Map.empty[String, Int]

    def apply(request: Request) = {
      val response = Response(request.version, Status.BadRequest)
      val id = request.getParam("id")
      counterMap += id -> (counterMap.getOrElse(id, 0) + 1)

      log.info(s"request received - id: ${id}, counter: ${counterMap(id)}")

      Thread.sleep(10 * 1000)
      counter.incr()
      Future.value(response)
    }
  }

  val counter = statsReceiver.counter("requests_counter")


  def main() {
    val server = ServerBuilder()
      .codec(new Http().enableTracing(true))
      .bindTo(new InetSocketAddress(InetAddress.getLoopbackAddress, 8888))
      .name("test server")
      .reportTo(statsReceiver)
      .build(service)

    onExit { server.close() }
    Await.ready(server)
  }
}
