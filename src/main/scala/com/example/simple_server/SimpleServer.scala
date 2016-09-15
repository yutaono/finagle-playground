package com.example.simple_server

import java.net.InetSocketAddress

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}

object SimpleServer extends TwitterServer {

  val service = new Service[Request, Response] {
    def apply(request: Request): Future[Response] = {
      val response = Response(request.version, Status.Ok)
      response.contentString = "hi"
      Future.value(response)
    }
  }

  def main() {
    val server = ServerBuilder()
      .stack(Http.server)
      .bindTo(new InetSocketAddress(8888))
      .name("test server")
      .build(service)

    onExit { server.close() }
    Await.ready(server)
  }
}
