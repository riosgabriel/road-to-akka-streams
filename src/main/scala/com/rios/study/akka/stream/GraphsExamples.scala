package com.rios.study.akka.stream

import akka.actor.Cancellable
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, UnzipWith}

import scala.concurrent.{Future, Promise}

object GraphsExamples {

  val source = Source(1 to 10)

  val sink = Sink.fold[Int, Int](0)(_ + _)

  val runnable: RunnableGraph[Future[Int]] = source.toMat(sink)(Keep.right)

  val otherSink = Flow[Int].alsoTo(Sink.foreach(println(_))).to(Sink.ignore)

  val maybeSource: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]

  val cancellableFlow: Flow[Int, Int, Cancellable] = ???

  val futureSink: Sink[Int, Future[Int]] = Sink.head[Int]

  val runnableGraph1: RunnableGraph[Promise[Option[Int]]] = maybeSource.via(cancellableFlow).to(futureSink)

  val runnableGraph2: RunnableGraph[Cancellable] = maybeSource.viaMat(cancellableFlow)(Keep.right).to(futureSink)

  val runnableGraph3: RunnableGraph[Future[Int]] =
    maybeSource
      .via(cancellableFlow)
      .toMat(futureSink)(Keep.right)

  val f1, f2, f3, f4 = Flow[Int].map(_ + 10)



}
