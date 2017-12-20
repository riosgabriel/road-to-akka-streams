package com.rios.study.akka.stream

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, FlowShape }
import akka.stream.scaladsl.{ Broadcast, Flow, GraphDSL, Sink, Source, Zip }

object FlowShapeGraph extends App {

  implicit val system = ActorSystem("PartialGraph")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val pairWithToString = Flow.fromGraph(GraphDSL.create() { implicit builder =>
    import GraphDSL.Implicits._

    // fan-out
    val broadcast = builder.add(Broadcast[Int](2))

    // fan-in
    val zip = builder.add(Zip[Int, String]())

    broadcast.out(0).map(identity) ~> zip.in0
    broadcast.out(1).map(_.toString) ~> zip.in1

    FlowShape(broadcast.in, zip.out)

    Source.combine()
  })

  Source(List(1, 2, 3))
      .via(pairWithToString)
      .runWith(Sink.foreach(println)).foreach(_ => system.terminate())

}
