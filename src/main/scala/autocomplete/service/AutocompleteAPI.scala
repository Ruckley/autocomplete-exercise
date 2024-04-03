package autocomplete.service

import autocomplete.AutocompleteConfig
import autocomplete.logging.Logging.spanLog
import cats.effect.IO
import fs2.concurrent.SignallingRef
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._
import org.http4s.{HttpRoutes, Response, Status}
class AutocompleteAPI(config: AutocompleteConfig, autocompleteService: AutocompleteService, shutdownSignal: SignallingRef[IO, Boolean]) {
  private val serviceRoutes = HttpRoutes.of[IO] {
    case GET -> Root / "healthcheck" =>
      spanLog("Health Check Endpoint") {
        Ok("Service is up and running")
      }

    case POST -> Root / "shutdown" =>
      spanLog("Shutdown Endpoint") {
        shutdownSignal.set(true) *> Ok("Shutdown signal sent")
      }

    case GET -> Root / "getSuggestions" / "v1" / prefix =>
      spanLog("getSuggestions endpoint") {
        autocompleteService.getSuggestionsResponse(prefix).flatMap {
          case Some(suggestionsResponse) =>
            Ok(suggestionsResponse.mkString(", "))
          case None =>
            IO.pure(Response[IO](Status.InternalServerError))
        }
      }

    case req @ POST -> Root / "v1" /  "createTree" =>
      for {
        words <- req.as[List[String]]
        _ <- autocompleteService.createTree(words)
        res <- Ok("Ternary tree built successfully")
      } yield res
  }.orNotFound

  def runServer(): IO[Unit] = {
    val server = BlazeServerBuilder[IO]
      .bindHttp(config.port, config.host)
      .withHttpApp(serviceRoutes)
      .resource

    server.use(_ => shutdownSignal.discrete.takeWhile(!_).compile.drain)
  }
}