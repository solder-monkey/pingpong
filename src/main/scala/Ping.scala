import akka.actor._

case object PingMessage
case object IncrementMessage
case object PongMessage
case object StartMessage
case object StopMessage

/**
  * An Akka Actor example written by Alvin Alexander of
  * http://alvinalexander.com
  *
  * Shared here under the terms of the Creative Commons
  * Attribution Share-Alike License: http://creativecommons.org/licenses/by-sa/2.5/
  *
  * more akka info: http://doc.akka.io/docs/akka/snapshot/scala/actors.html
  */
class Ping(pong: ActorRef) extends Actor {
  var count = 0
  def incrementAndPrint { count += 1 }
  def receive = {
    case StartMessage =>
      incrementAndPrint
      pong ! PingMessage
    case IncrementMessage =>
      incrementAndPrint
    case PongMessage =>
      incrementAndPrint
      if (count > 9999999) {
        sender ! StopMessage
        println("ping stopped")
        context.stop(self)
      } else {
        sender ! PingMessage
      }
  }
}

class Pong extends Actor {
  var startTime = System.currentTimeMillis()

  def receive = {
    case PingMessage =>
      sender ! PongMessage
      sender ! IncrementMessage
      sender ! IncrementMessage
      sender ! IncrementMessage
    case StopMessage =>
      println("pong stopped")
      println("Rate = " + ((10000000/(System.currentTimeMillis() - startTime))*1000).toString + " / second")
      context.stop(self)
  }
}

object PingPongTest extends App {
  val system = ActorSystem("PingPongSystem")
  val pong = system.actorOf(Props[Pong], name = "pong")
  val ping = system.actorOf(Props(new Ping(pong)), name = "ping")
  // start them going
  ping ! StartMessage
}

//This is a useless comment...