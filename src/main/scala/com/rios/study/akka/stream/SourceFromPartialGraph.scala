package com.rios.study.akka.stream

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{ GraphDSL, Sink, Source, Zip }
import akka.stream.{ ActorMaterializer, SourceShape }

object SourceFromPartialGraph extends App {

  implicit val system = ActorSystem("sourceFromPartialGraph")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = ExecutionContext.global

  val sourcePairs = Source.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val zip = builder.add(Zip[Int, Int]())

    def ints = Source.fromIterator(() => Iterator.range(1, 100))

    ints.filter(_ % 2 != 0) ~> zip.in0
    ints.filter(_ % 2 == 0) ~> zip.in1

    SourceShape(zip.out)
  })

  val firstPair: Future[Done] = sourcePairs.runWith(Sink.foreach(println))

  firstPair.onComplete {
    case Success(_) =>
      print("Done")
      system.terminate()
    case Failure(ex) =>
      print(ex)
      system.terminate()
  }



}
