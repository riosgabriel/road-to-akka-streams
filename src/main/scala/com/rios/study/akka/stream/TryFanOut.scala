package com.rios.study.akka.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source }
import akka.stream.{ ActorMaterializer, ClosedShape }

object TryFanOut extends App {

  implicit val actorSystem = ActorSystem("stream-with-broadcast")
  implicit val materializer = ActorMaterializer()

  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]
  val sharedDoubler = Flow[Int].map(_ * 2)

  RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) { implicit builder =>
    (topHS, bottomHS) =>
      import akka.stream.scaladsl.GraphDSL.Implicits._
      val broadcast = builder.add(Broadcast[Int](2))
      Source.single(1) ~> broadcast.in

      broadcast.out(0) ~> sharedDoubler ~> topHS.in
      broadcast.out(1) ~> sharedDoubler ~> bottomHS.in
      ClosedShape
  })
}
