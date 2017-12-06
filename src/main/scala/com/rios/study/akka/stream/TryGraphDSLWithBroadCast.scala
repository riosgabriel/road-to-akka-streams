package com.rios.study.akka.stream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, ClosedShape}

object TryGraphDSLWithBroadCast extends App {

  implicit val actorSystem = ActorSystem("stream-with-broadcast")
  implicit val materializer = ActorMaterializer()

  val sink1 = Sink.foreach[Int](n => println(s"Sink 1: $n"))
  val sink2 = Sink.foreach[Int](n => println(s"Sink 2: $n"))

  RunnableGraph.fromGraph(GraphDSL.create(sink1, sink2)((_, _)) { implicit builder =>
    (s1, s2) =>
    import GraphDSL.Implicits._

    val source = Source(1 to 10)

    val bcast = builder.add(Broadcast[Int](2))

    val addOne = Flow[Int].map(_ + 1)
    val minusOne = Flow[Int].map(_ - 1)

    source ~> bcast.in

    bcast.out(0) ~> addOne ~> s1.in
    bcast.out(1) ~> minusOne ~> s2.in

    ClosedShape
  }).run()
}
