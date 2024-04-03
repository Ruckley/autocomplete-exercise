package autocomplete.service

trait AutocompleteJsonFormatter {
  // should use something like circe for this
  def formatSuggestions(suggestions: List[String], corpusSize: Int, suggestion: String): String
}
object ManualJsonFormatter extends AutocompleteJsonFormatter {
  override def formatSuggestions(suggestions: List[String], corpusSize: Int, suggestion: String): String = {
    val suggestionsWithQuotes = suggestions.map(s => "\"" + s + "\"")
    s"""
       |{
       |   "suggestions": [${suggestionsWithQuotes.mkString(", ")}],
       |   "corpusSize": $corpusSize,
       |   "autocomplete": "$suggestion"
       |}
       |""".stripMargin
  }
}
