import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import scala.io.StdIn
import akka.http.scaladsl.server.Directives

object WebServer extends Directives {
  def main(args: Array[String]) {
    val slaService = SlaService
    val throttlingService: ThrottlingService = new Throttling(10, slaService) // todo take grace from YML

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    // TODO add exception handler
    val route =
      concat(
        get {
          pathPrefix("request") {
            // TODO get token from authorization header
            if (throttlingService.isRequestAllowed(Some("6N2peswmp7IEFwiXxFWk"))) {
              complete(StatusCodes.OK)
            }
            complete(StatusCodes.Unauthorized)
          }
        }
      )
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
