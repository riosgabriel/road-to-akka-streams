package com.rios.study.akka.stream

import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ GraphDSL, RunnableGraph, Sink, Source, ZipWith }
import akka.stream.{ ActorMaterializer, ClosedShape, UniformFanInShape }

object PartialGraph extends App {

  implicit val system = ActorSystem("PartialGraph")
  implicit val materializer = ActorMaterializer()

  val pickMaxOfThree = GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    val zip1 = builder.add(ZipWith[Int, Int, Int](math.max))
    val zip2 = builder.add(ZipWith[Int, Int, Int](math.max))

    zip1.out ~> zip2.in0

    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val rg = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder =>
    sink =>
    import GraphDSL.Implicits._

    val pm3: UniformFanInShape[Int, Int] = builder.add(pickMaxOfThree)

    Source.single(1) ~> pm3.in(0)
    Source.single(2) ~> pm3.in(1)
    Source.single(3) ~> pm3.in(2)

    pm3.out ~> sink.in

    ClosedShape
  })

  val max: Future[Int] = rg.run()

  println(Await.result(max, 300 millis) == 3)

  system.terminate()
}
