package com.example

import java.util.Date

import com.twitter.conversions.time._
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.service._
import com.twitter.finagle.stats.StatsReceiver
import com.twitter.finagle.util.DefaultTimer
import com.twitter.server.TwitterServer
import com.twitter.util._

class UsingRetryBudget(statsReceiver: StatsReceiver) {

  var count = 0

  val shouldRetry: PartialFunction[(Request, Try[Response]), Boolean] = {
    case (r, Return(rep)) if rep.statusCode != 200 =>
      println(s"${new Date().getSeconds} \t ${count} \t ${retryBudget.balance}")
      count += 1
      true
  }

  val retryBudget = RetryBudget()
//  println(retryBudget.balance)

//  val retryPolicy = RetryPolicy.tries(1000, shouldRetry)
  val backoff = Backoff(100.millisecond)(identity)
  val retryPolicy = RetryPolicy.backoff(backoff)(shouldRetry)

  val retryFilter: RetryFilter[Request, Response] = new RetryFilter(
    retryPolicy = retryPolicy,
    timer = DefaultTimer.twitter,
    statsReceiver = statsReceiver,
    retryBudget = retryBudget
  )

  val host = "localhost:8888"

  val client: Service[Request, Response] = Http.client
    .withStatsReceiver(statsReceiver)
    .withRequestTimeout(10.seconds)
    .newService(host)

  val service = retryFilter andThen client

//  val fs = for (i <- 1 to 1) yield {
//    val id = scala.util.Random.alphanumeric.take(8).mkString
//    service(Request(s"/?id=${id}"))
//  }
//
//  val rs = Await.result(Future.collect(fs))


}

object UsingRetryBudgetServer extends TwitterServer {

  def main(): Unit = {
    val client = new UsingRetryBudget(statsReceiver)
    val service = client.service

    val fs = for (i <- 1 to 1) yield {
      val id = scala.util.Random.alphanumeric.take(8).mkString
      println(client.retryBudget.balance)
      val f = service(Request(s"/?id=${id}"))
      f
    }

    Await.result(Future.collect(fs))

//    println("end")
//    Thread.sleep(1000 * 100)
  }

}


