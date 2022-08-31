import akka.actor.typed.ActorSystem

object Boot {
  def main(args: Array[String]): Unit = {
    val system: ActorSystem[Server.Message] =
      ActorSystem(Server("localhost", 8080), "BuildServer")
  }
}
