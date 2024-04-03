package autocomplete.TernaryTree
import autocomplete.TernaryTree

import scala.annotation.tailrec

case class AutocompleteTernaryTree(root: Tree, corpusSize: Int) {
  def generateSuggestions(prefix: String): List[String] = {
    val suggestions =
      if (prefix.isEmpty) extractStringsWithFreqFromRoot
      else {
        val prefixTree = getPrefixSubtree(root, prefix)
        prefixTree match {
          case EmptyNode => List.empty
          case n: Node => n.string match {
            case None => extractStringsWithFreqFromNode(n.middle)
            case Some(s) => extractStringsWithFreqFromNode(n.middle) :+ (s, n.freq)
          }
        }
      }

    orderStrings(suggestions)

  }

  // should be made private
  def getPrefixSubtree(root: Tree, prefix: String): Tree = {
    @tailrec
    def _getPrefixSubtree(root: Tree, chars: Seq[Char]): Tree = {
      root match {
        case EmptyNode => EmptyNode
        case n: Node =>
          chars match {
            //catch emptyString
            case Nil => root

            //Termination Conditions
            case c :: Nil if c == n.char => n

            case c :: tail if c == n.char =>
              _getPrefixSubtree(n.middle, tail)
            case c :: tail if c < n.char =>
              _getPrefixSubtree(n.left, c :: tail)
            case c :: tail =>
              _getPrefixSubtree(n.right, c :: tail)

          }
      }
    }

    _getPrefixSubtree(root, prefix.toCharArray.toList)
  }

  private def extractStringsWithFreqFromNode(node: Tree): List[(String, Int)] = {
    _extractStrings(List(node), List.empty).collect {
      case (Some(str), value) => (str, value)
    }
  }

  // should be made private
  def extractStringsWithFreqFromRoot: List[(String, Int)] = {
    _extractStrings(List(root), List.empty).collect {
      case (Some(str), value) => (str, value)
    }
  }

  @tailrec
  private def _extractStrings(nodes: List[Tree], strings: List[(Option[String], Int)]): List[(Option[String], Int)] = {
    nodes match {
      case Nil => strings
      case EmptyNode :: remainingNodes => _extractStrings(remainingNodes, strings)
      case Node(_, freq, stringO, left, middle, right) :: remainingNodes =>
        _extractStrings(left :: middle :: right :: remainingNodes, strings :+ stringO -> freq)

    }
  }

  private def orderStrings(stringsWithFrq: List[(String, Int)]): List[String] = {
    stringsWithFrq
      .sortBy(_._1)
      .sortWith((a, b) => a._2 > b._2)
      .map(_._1)
  }
}

case object AutocompleteTernaryTree {

  def apply(corpus: Seq[String]): AutocompleteTernaryTree = {

    // decided to remove empty strings from word count
    val cleanCorpus = corpus.filter(_.nonEmpty)
    val tree = cleanCorpus.foldLeft[Tree](EmptyNode) { (root, word) =>
      insertString(root, word)
    }
    TernaryTree.AutocompleteTernaryTree(tree, cleanCorpus.size)
  }

  // NOT TAIL RECURSIVE
  private def insertString(root: Tree, word: String): Tree = {
    def _insertString(currentNode: Tree, remaining: List[Char]): Tree = {
      (currentNode, remaining) match {
        //Empty case
        case (EmptyNode, Nil) => EmptyNode
        case (n: Node, Nil) => n
        //Termination conditions
        case (EmptyNode, c :: Nil) => Node(c, 1, Some(word))
        case (EmptyNode, c :: tail) =>
          val middleChild = _insertString(EmptyNode, tail)
          Node(c, 0, middle = middleChild)
        case (n: Node, c :: Nil) if c == n.char =>
          n.copy(string = Some(word), freq = n.freq + 1)

        case (n: Node, c :: tail) if c == n.char =>
          val middleChild = _insertString(n.middle, tail)
          n.copy(middle = middleChild)
        case (n: Node, c :: tail) if c > n.char =>
          val rightChild = _insertString(n.right, c :: tail)
          n.copy(right = rightChild)
        case (n: Node, c :: tail) => // if c < n.char
          val leftChild = _insertString(n.left, c :: tail)
          n.copy(left = leftChild)
      }
    }

    _insertString(root, word.toCharArray.toList)
  }
}