package autocomplete.TernaryTree

sealed trait Tree
case class Node(char: Char, freq: Int, string: Option[String] = None, left: Tree = EmptyNode, middle: Tree = EmptyNode, right: Tree = EmptyNode) extends Tree
case object EmptyNode extends Tree
