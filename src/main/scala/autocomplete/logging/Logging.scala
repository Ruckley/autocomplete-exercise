package autocomplete.logging


import cats.effect.IO
import org.slf4j.{Logger, LoggerFactory}
object Logging {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  def spanLog[A](message: String)(ioa: IO[A]): IO[A] =
    for {
      start <- IO(System.currentTimeMillis())
      result <- ioa.attempt
      end <- IO(System.currentTimeMillis())
      _ = result match {
        case Left(e) =>
          logger.error(s"[$message] - Error: ${e.getMessage}, Elapsed Time: ${end - start} ms")
        case Right(_) =>
          logger.info(s"[$message] - Success, Elapsed Time: ${end - start} ms")
      }
    } yield result.getOrElse(throw new RuntimeException("Unexpected error"))
}