package com.example

import java.util.UUID

import com.twitter.finagle.Http
import com.twitter.conversions.time._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.param.HighResTimer
import com.twitter.finagle.service.{Backoff, RetryBudget, RetryFilter, RetryPolicy}
import com.twitter.finagle.stats.ClientStatsReceiver
import com.twitter.logging.Logging
import com.twitter.util._

object TestClient extends App {

  implicit val t = HighResTimer.Default

  val retryBudget = RetryBudget.Infinite // (ttl = 10.seconds, minRetriesPerSec = 1, percentCanRetry = 0.2)
  val backoff = Backoff(Duration.fromMilliseconds(1L))(a => a)

  val shouldRetry: PartialFunction[(Request, Try[Response]), Boolean] = {
    case (r, Return(rep)) => true //  rep.statusCode != 200
  }

//  val retryPolicy = RetryPolicy.tries(3, shouldRetry)
  val retryFilter = RetryFilter(backoff)(shouldRetry)

  private val service =
//    retryFilter andThen
      Http.client
//        .withRetryBudget(retryBudget)
//        .withRetryBackoff(backoff)
        .newService("10.4.94.198:80")

  val responsesF = for (i <- 1 to 1) yield {
    val uuid = UUID.randomUUID().toString
    val req = Request(s"/?id=${uuid}")
    service(req)
  }

  val res = Await.result(Future.collect(responsesF))

  println(res.mkString("\n"))

}
