package autocomplete.service

import autocomplete.AutocompleteConfig
import autocomplete.db.PlaceholderTreeDb
import cats.effect._
import cats.effect.unsafe.implicits.global
import fs2.concurrent.SignallingRef
import org.http4s._
import org.http4s.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
class AutocompleteAPISpec extends AnyWordSpec with Matchers{

  // Define a test configuration
  val testConfig = AutocompleteConfig(port = 8080, host = "localhost")

  val db = new PlaceholderTreeDb()
  val formatter = ManualJsonFormatter
  val testAutocompleteService = new AutocompleteService(db, formatter)
  val shutdownSignal = SignallingRef[IO, Boolean](false).unsafeRunSync()

  val autocompleteAPI = new AutocompleteAPI(testConfig, testAutocompleteService, shutdownSignal)

  // Define an HTTP client to send requests to the server
  val client = org.http4s.client.blaze.BlazeClientBuilder[IO](scala.concurrent.ExecutionContext.global).resource.unsafeRunSync

  // Define a test case for the health check endpoint
  "Health check endpoint" should {
    "return 200 OK" in {
      val request = Request[IO](Method.GET, uri"/healthcheck")
      val response = autocompleteAPI.serviceRoutes.run(request).unsafeRunSync()

      response.status shouldBe (Status.Ok)
      response.as[String].unsafeRunSync() shouldBe ("Service is up and running")
    }
  }

}
