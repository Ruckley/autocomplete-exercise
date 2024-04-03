package autocomplete.service

import autocomplete.TernaryTree.AutocompleteTernaryTree
import autocomplete.db.AutoCompleteTreeDb
import cats.effect.IO

class AutocompleteService(treeDB: AutoCompleteTreeDb, formatter: AutocompleteJsonFormatter) {

  def createTree(words: Seq[String]): Unit = {
    val newTree = AutocompleteTernaryTree(words)
    treeDB.insertTree(newTree)
  }

  def getSuggestionsResponse(prefix: String): IO[Option[String]] =
    treeDB.getTree.map{_.map { t =>
      val suggestions = t.generateSuggestions(prefix)
      formatter.formatSuggestions(suggestions, t.corpusSize, suggestions.head)
    }}

}
