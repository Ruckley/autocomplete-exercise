package optolookup

import cats.data.EitherT
import cats.effect.{ExitCode, IO, IOApp}

import java.net.URL
import scala.concurrent.duration._
import scala.io.Source
import scala.util.Using

object OptoLookup extends IOApp {

  def run(args: List[String]): IO[ExitCode] = {
    val defaultUrl: String = """https://gist.githubusercontent.com/akozumpl/61e98ab8aae99762a517656a61436e65/raw/23881f9513e31b7ca295b4da9f77028db1a91f91/corpus.txt""".stripMargin

    val argE: IO[Either[Throwable, (String, String)]] = IO.pure {
      args match {
        case "-c" :: url :: prefix :: Nil => Right(url, prefix)
        case prefix :: Nil => Right(defaultUrl, prefix)
        case _ => Left(new IllegalArgumentException("unrocognised input, please use the format: optolookup [-c <corpus-url>] <prefix>"))
      }
    }

    def fetchCorpus(url: String): IO[Either[Throwable, String]] = {
      IO.blocking {
        Using.resource(Source.fromURL(new URL(url))) { source =>
          source.getLines().mkString
        }
      }.timeout(5.seconds).attempt
    }

    trait PickSuggestionService {
      def pickSuggestion(suggestions: Seq[String]) : IO[String]
    }

    def pickSugestion(suggestions: Seq[String]) : IO[String] = {

        object PickSuggestion extends PickSuggestionService {
          def pickSuggestion(suggestions: Seq[String]) = IO.pure(suggestions(1))
        }
        PickSuggestion.pickSuggestion(suggestions)

    }

    val resultIO: EitherT[IO, Throwable, String] = for {
      x <- EitherT(argE)
      corpusTxt <- EitherT(fetchCorpus(x._1))
    } yield {
      val tree = AutocompleteTernaryTree(corpusTxt.split(" ").toList)
      val (suggestions, corpusSize) = (tree.generateSuggestions(x._2), tree.corpusSize)
      pickSugestion(suggestions).map{ s =>
        AutocompleteJsonFormatter.formatSuggestions(suggestions, corpusSize, s)
      }
    }

    resultIO.value.flatMap {
      case Right(result) =>
        IO(println(result)) *> IO.pure(ExitCode.Success)
      case Left(error) =>
        IO(println(error)) *> IO.pure(ExitCode.Error)
    }

  }

}



