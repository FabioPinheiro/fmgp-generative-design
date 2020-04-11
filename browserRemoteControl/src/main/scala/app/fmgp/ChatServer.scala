// package fabio.akka

// //import fabio.User
// case class User(name: String)

// case class AkkaChatMessage(msg: String, oUser: Option[User])

// object ChatServer {

//   trait ChatServerEvent

//   case class Message(value: String) extends ChatServerEvent

//   case class AddClient(ref: akka.actor.ActorRef) extends ChatServerEvent

//   case class RemoveClient(ref: akka.actor.ActorRef) extends ChatServerEvent

//   import akka.actor._

//   implicit lazy val system = ActorSystem("AkkaChatServer")

//   lazy val manager =
//     system.actorOf(Props(Manager()), "manager")

//   case class Manager() extends Actor {

//     def receive = operative()

//     def operative(clients: List[ActorRef] = List()): Receive = {
//       case AddClient(ref) =>
//         context.become(operative(clients :+ ref))
//       case RemoveClient(ref) =>
//         context.become(operative(clients.filterNot(_ == ref)))
//       case msg: Message =>
//         clients.foreach { c => c ! msg }
//     }
//   }

// }
