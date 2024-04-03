package autocomplete

import autocomplete.db.PlaceholderTreeDb
import autocomplete.service.{AutocompleteAPI, AutocompleteService, ManualJsonFormatter}
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import fs2.concurrent.SignallingRef

object Main extends IOApp{


  def run(args: List[String]): IO[ExitCode] = {

    val config = ConfigFactory.load()
    val port = config.getInt("autocomplete.port")
    val host = config.getString("autocomplete.host")
    val aCConfig = AutocompleteConfig(port, host)

    val db = new PlaceholderTreeDb()
    val formatter = ManualJsonFormatter
    val autocompleteService = new AutocompleteService(db, formatter)


    for {
      shutdownSignal <- SignallingRef[IO, Boolean](false)
      autocompleteAPI = new AutocompleteAPI(aCConfig,autocompleteService, shutdownSignal)
      _ <- autocompleteAPI.runServer()
    } yield ExitCode.Success
  }

}
