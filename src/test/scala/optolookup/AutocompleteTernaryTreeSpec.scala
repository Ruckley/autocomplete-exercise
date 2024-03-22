package optolookup


import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec._

class AutocompleteTernaryTreeSpec extends AnyWordSpec with Matchers {

  private val catTree = Node('c', 0, middle = Node('a', 0, middle = Node('t', 1, Some("cat"))))
  //"car", "can", "can", "cat", "cart", "cans", "car", "cart", "car"
  private val complexTree = Node('c', 0, middle = Node('a', 0, middle = Node('r', 3, Some("car"), left = Node('n', 2, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 2, Some("cart")), right = Node('t', 1, Some("cat")))), right = Node('r', 0, middle = Node('a', 0, middle = Node('t', 1, Some("rat")))))
  // "!x" "!x&" "!x^" "-"
  private val specCharsTree = Node('!', 0, None, EmptyNode, Node('x', 1, Some("!x"), EmptyNode, Node('&', 1, Some("!x&"), EmptyNode, EmptyNode, Node('^', 1, Some("!x^"), EmptyNode, EmptyNode, EmptyNode)), EmptyNode), Node('-', 1, Some("-"), EmptyNode, EmptyNode, EmptyNode))


  "AutocompleteTernaryTree apply should correctly create a ternary table" when {
    "single string" in {
      AutocompleteTernaryTree(List("cat")) shouldBe AutocompleteTernaryTree(catTree, 1)
    }
    "Two strings that do not share a prefix" in {
      AutocompleteTernaryTree(List("cat", "rat")) shouldBe AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 0, middle = Node('t', 1, Some("cat"))), right = Node('r', 0, middle = Node('a', 0, middle = Node('t', 1, Some("rat"))))), 2)
    }
    "Multiple strings that share a prefix" in {
      AutocompleteTernaryTree(List("car", "can", "cat", "cart", "cans")) shouldBe AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 0, middle = Node('r', 1, Some("car"), left = Node('n', 1, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 1, Some("cart")), right = Node('t', 1, Some("cat"))))), 5)
    }
    "Multiple strings that share a prefix with repeats" in {
      AutocompleteTernaryTree(List("car", "can", "can", "cat", "cart", "cans", "car", "cart", "car", "rat")) shouldBe AutocompleteTernaryTree(complexTree, 10)
    }
    "Later added strings are prefixes to existing strings in tree" in {
      AutocompleteTernaryTree(List("car", "can", "cat", "cart", "ca", "cans")) shouldBe AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 1, Some("ca"), middle = Node('r', 1, Some("car"), left = Node('n', 1, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 1, Some("cart")), right = Node('t', 1, Some("cat"))))), 6)
    }
    "treat capitalised letters separately" in {
      AutocompleteTernaryTree(List("cat", "CAT", "caT")) shouldBe AutocompleteTernaryTree(Node('c', 0, None, Node('C', 0, None, EmptyNode, Node('A', 0, None, EmptyNode, Node('T', 1, Some("CAT"), EmptyNode, EmptyNode, EmptyNode), EmptyNode), EmptyNode), Node('a', 0, None, EmptyNode, Node('t', 1, Some("cat"), Node('T', 1, Some("caT"), EmptyNode, EmptyNode, EmptyNode), EmptyNode, EmptyNode), EmptyNode), EmptyNode), 3)
    }
    "corpus is empty" in {
      AutocompleteTernaryTree(List.empty) shouldBe AutocompleteTernaryTree(EmptyNode, 0)
    }
    "corpus contains empty strings" in {
      AutocompleteTernaryTree(List("", "cat", "", "rat", "")) shouldBe AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 0, middle = Node('t', 1, Some("cat"))), right = Node('r', 0, middle = Node('a', 0, middle = Node('t', 1, Some("rat"))))), 2)
    }
    "corpus contains special characters" in {
      AutocompleteTernaryTree(List("!x", "!x&", "!x^", "-")) shouldBe AutocompleteTernaryTree(Node('!', 0, None, EmptyNode, Node('x', 1, Some("!x"), EmptyNode, Node('&', 1, Some("!x&"), EmptyNode, EmptyNode, Node('^', 1, Some("!x^"), EmptyNode, EmptyNode, EmptyNode)), EmptyNode), Node('-', 1, Some("-"), EmptyNode, EmptyNode, EmptyNode)), 4)
    }
  }

  "getPrefixSubtree should get the correct subtree" when {
    "tree is empty" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(EmptyNode, "c") shouldBe EmptyNode
    }
    "prefix is tree root" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(catTree, "c") shouldBe catTree
    }
    "single letter prefix is not in tree" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(catTree, "x") shouldBe EmptyNode
    }
    "prefix is not in tree" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(catTree, "rat") shouldBe EmptyNode
    }
    "prefix is not in tree but prefix of prefix is in tree" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(catTree, "cats") shouldBe EmptyNode
    }
    "prefix exists in single branch tree" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(catTree, "ca") shouldBe Node('a', 0, middle = Node('t', 1, Some("cat")))
    }
    "prefix exists in complex tree" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(complexTree, "ca") shouldBe
        Node('a', 0, middle = Node('r', 3, Some("car"), left = Node('n', 2, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 2, Some("cart")), right = Node('t', 1, Some("cat"))))
    }

    "prefix exists in complex tree but does not match all words on branch" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(complexTree, "can") shouldBe
        Node('n', 2, Some("can"), EmptyNode, Node('s', 1, Some("cans"), EmptyNode, EmptyNode, EmptyNode), EmptyNode)
    }

    "prefix exists in complex tree and prefix itself is a word in corpus" in {
      AutocompleteTernaryTree(EmptyNode, 0).getPrefixSubtree(Node('c', 0, middle = Node('a', 1, Some("ca"), middle = Node('r', 3, Some("car"), left = Node('n', 2, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 2, Some("cart")), right = Node('t', 1, Some("cat"))))), "ca") shouldBe
        Node('a', 1, Some("ca"), middle = Node('r', 3, Some("car"), left = Node('n', 2, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 2, Some("cart")), right = Node('t', 1, Some("cat"))))
    }
  }

  "extractStringsFromMiddle should get the correct list of strings" when {
    "tree is empty" in {
      AutocompleteTernaryTree(EmptyNode, 0).extractStringsWithFreqFromRoot shouldBe List.empty
    }
    "tree has a single string" in {
      AutocompleteTernaryTree(catTree, 1).extractStringsWithFreqFromRoot shouldBe List("cat" -> 1)
    }
    "tree has a multiple strings which don't share a prefix" in {
      //"cat", "rat"
      AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 0, middle = Node('t', 1, Some("cat"))), right = Node('r', 0, middle = Node('a', 0, middle = Node('t', 1, Some("rat"))))), 2)
        .extractStringsWithFreqFromRoot.sortBy(_._1) shouldBe List("cat" -> 1, "rat" -> 1).sortBy(_._1)
    }
    "tree has multiple strings with shared prefix" in {
      // "car", "can", "cat", "cart", "cans"
      AutocompleteTernaryTree(Node('c', 0, middle = Node('a', 0, middle = Node('r', 1, Some("car"), left = Node('n', 1, Some("can"), middle = Node('s', 1, Some("cans"))), middle = Node('t', 1, Some("cart")), right = Node('t', 1, Some("cat"))))), 5)
        .extractStringsWithFreqFromRoot.sortBy(_._1) shouldBe List("can" -> 1, "cans" -> 1, "car" -> 1, "cart" -> 1, "cat" -> 1).sortBy(_._1)
    }

    "tree has multiple strings with shared prefix with repeated words" in {
      // "car", "can", "can", "cat", "cart", "cans", "car", "cart", "car"
      // words should come out ordered by frequency then alphabetically
      AutocompleteTernaryTree(complexTree, 10).extractStringsWithFreqFromRoot.sortBy(_._1) shouldBe
        List("car" -> 3, "can" -> 2, "cart" -> 2, "cans" -> 1, "cat" -> 1, "rat" -> 1).sortBy(_._1)
    }
  }

  "generateSuggestions should create a correct list of suggestions" when {
    "empty string prefix" in {
      AutocompleteTernaryTree(catTree, 1).generateSuggestions("") shouldBe List("cat")
      AutocompleteTernaryTree(complexTree, 10).generateSuggestions("") shouldBe List("car", "can", "cart", "cans", "cat", "rat")
    }
    "corpus is empty" in {
      AutocompleteTernaryTree(EmptyNode, 0).generateSuggestions("prefix") shouldBe List.empty
    }
    "prefix not in corpus" in {
      AutocompleteTernaryTree(catTree, 1).generateSuggestions("x") shouldBe List.empty
      AutocompleteTernaryTree(complexTree, 1).generateSuggestions("x") shouldBe List.empty
      AutocompleteTernaryTree(complexTree, 1).generateSuggestions("cartx") shouldBe List.empty
    }
    "one word in corpus" in {
      AutocompleteTernaryTree(catTree, 1).generateSuggestions("c") shouldBe List("cat")
      AutocompleteTernaryTree(catTree, 1).generateSuggestions("ca") shouldBe List("cat")
      AutocompleteTernaryTree(catTree, 1).generateSuggestions("cat") shouldBe List("cat")
    }
    "multiple words in corpus" in {
      AutocompleteTernaryTree(complexTree, 10)
        .generateSuggestions("c") shouldBe List("car", "can", "cart", "cans", "cat")
      AutocompleteTernaryTree(complexTree, 10)
        .generateSuggestions("ca") shouldBe List("car", "can", "cart", "cans", "cat")
      AutocompleteTernaryTree(complexTree, 10)
        .generateSuggestions("can") shouldBe List("can", "cans")
      AutocompleteTernaryTree(complexTree, 10)
        .generateSuggestions("cart") shouldBe List("cart")
    }
    "there are non alphabet characters in corpus" in {
      AutocompleteTernaryTree(specCharsTree, 4).generateSuggestions("") shouldBe List("!x", "!x&", "!x^", "-")
    }

  }
}