package autocomplete.db

import autocomplete.TernaryTree.AutocompleteTernaryTree
import cats.effect.IO

trait AutoCompleteTreeDb {
  def insertTree(newTree: AutocompleteTernaryTree): IO[Unit]
  def getTree: IO[Option[AutocompleteTernaryTree]]

}

class PlaceholderTreeDb extends AutoCompleteTreeDb {
  private var tree: Option[AutocompleteTernaryTree] = None

  def insertTree(newTree: AutocompleteTernaryTree): IO[Unit] = {
    IO.delay{tree = Some(newTree)}

  }
  def getTree: IO[Option[AutocompleteTernaryTree]] = {
    IO.delay{tree}
  }
}
