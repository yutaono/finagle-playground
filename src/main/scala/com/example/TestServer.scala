package com.example

import java.net.{InetAddress, InetSocketAddress}

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Request, Response, Status}
import com.twitter.finagle.tracing.Trace
import com.twitter.finagle.Service
//import com.twitter.finagle.stats.DefaultStatsReceiver
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
//import zipkin.finagle.http.HttpZipkinTracer

import scala.collection.mutable


object TestServer extends TwitterServer {

  val service = new Service[Request, Response] {
    val counterMap = mutable.Map.empty[String, Int]

    def apply(request: Request) = {
      val response = Response(request.version, Status.BadRequest)
      val id = request.getParam("id")
      counterMap += id -> (counterMap.getOrElse(id, 0) + 1)

      Trace.record("starting that extremely expensive computation")

      Thread.sleep(2000L)
      log.info(s"request received - id: ${id}, counter: ${counterMap(id)}")

      counter.incr()
      Future.value(response)
    }
  }

  val counter = statsReceiver.counter("requests_counter")


  def main() {
//    System.setProperty("zipkin.http.host", "localhost:9411")
//    val tracer = ZipkinTracer.mk(host = "localhost", port = 9411, statsReceiver = DefaultStatsReceiver, sampleRate = 1.0f)
//    val tracer = new HttpZipkinTracer()

    val server = ServerBuilder()
      .codec(new Http().enableTracing(true))
      .bindTo(new InetSocketAddress(InetAddress.getLoopbackAddress, 8888))
      .name("test server")
      .reportTo(statsReceiver)
      .build(service)

//      .tracer(tracer)
//      .reportTo(DefaultStatsReceiver)

    onExit { server.close() }
    Await.ready(server)
  }
}
