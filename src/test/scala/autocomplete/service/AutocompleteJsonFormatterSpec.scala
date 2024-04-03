package autocomplete.service

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec._

class AutocompleteJsonFormatterSpec extends AnyWordSpec with Matchers {

  "formatSuggestions should create the correct Json" when {
    "create " in {
      formatSuggestions(List("format", "these", "words"), 5) shouldBe
        s"""
           |{
           |   "suggestions": ["format", "these", "words"],
           |   "corpusSize": 5
           |}
           |""".stripMargin
    }
  }

}

