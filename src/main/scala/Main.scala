
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._
import spray.json.JsObject

import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("github-webhook")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  private val json = mapResponseEntity(_.withContentType(ContentTypes.`application/json`))
  implicit val exceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Throwable =>
        e.printStackTrace()
        throw e
    }
  implicit val rejectHandler: RejectionHandler =
    RejectionHandler
      .newBuilder()
      .result()
  val port = System.getProperty("http.port").toInt

  val route =
    path("") {
      get {
        complete("OK")
      } ~
      post {
        json {
          entity(as[JsObject]) { body =>
            extractRequest { request =>
              println(s"${request.method.value} ${request.uri}")
              request.headers.foreach { header =>
                println(s"  ${header.name()}: ${header.value()}")
              }
              println(body.prettyPrint)
              complete(body.prettyPrint)
            }
          }
        }
      }
    }

  val bindingFuture = Http()
    .bindAndHandle(route, "::0", port)
    .map { binding =>
      println(s"Server started: ${binding.localAddress} on $port")
      binding
    }
}
