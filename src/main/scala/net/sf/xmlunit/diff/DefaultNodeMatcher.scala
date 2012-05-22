/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package net.sf.xmlunit.diff

import org.w3c.dom.Element
import org.w3c.dom.Node
import scala.collection.JavaConversions._

/**
 * Strategy that matches control and tests nodes for comparison.
 */
trait NodeMatcher {
  /**
   * Matches control and test nodes against each other, returns the
   * matching pairs.
   */
  def getMatches(
    controlNodes: java.lang.Iterable[Node],
    testNodes: java.lang.Iterable[Node]
  ): java.lang.Iterable[(Node, Node)]
}

trait NodeTypeMatcher {
  def canBeCompared(controlType: Short, testType: Short): Boolean
}

object DefaultNodeTypeMatcher extends NodeTypeMatcher {
  def canBeCompared(controlType: Short, testType: Short) =
    controlType == testType || (
      controlType == Node.TEXT_NODE && testType == Node.CDATA_SECTION_NODE
    ) || (
      controlType == Node.CDATA_SECTION_NODE && testType == Node.TEXT_NODE
    )
}

/**
 * Strategy that matches control and tests nodes for comparison.
 */
class DefaultNodeMatcher(
  private val elementSelector: ElementSelector,
  private val nodeTypeMatcher: NodeTypeMatcher
) extends NodeMatcher {
  def this(elementSelector: ElementSelector) =
    this(elementSelector, DefaultNodeTypeMatcher)
  def this() = this(ElementSelectors.Default)

  def getMatches(
    controlNodes: java.lang.Iterable[Node],
    testNodes: java.lang.Iterable[Node]
  ): java.lang.Iterable[(Node, Node)] = controlNodes.foldLeft(
    List.empty[(Node, Node)],
    Set(0 until testNodes.size: _*),
    None: Option[Int]
  ) {
    case ((matches, unmatched, last), control) =>
      this.findMatchingNode(control, testNodes, last, unmatched).map {
        case (m, i) => ((control, m) :: matches, unmatched - i, last)
      }.getOrElse((matches, unmatched, last))
  }._1.reverse

  private def findMatchingNode(
    searchFor: Node,
    searchIn: Iterable[Node],
    indexOfLastMatch: Option[Int],
    availableIndices: Set[Int]
  ) = {
    indexOfLastMatch.map { i =>
      val (before, after) = searchIn.zipWithIndex.splitAt(i)
      (after.tail ++ before)
    }.getOrElse(searchIn.zipWithIndex)
      .filter(p => availableIndices(p._2))
      .find(p => nodesMatch(searchFor, p._1))
  }

  private def nodesMatch(n1: Node, n2: Node): Boolean = (n1, n2) match {
    case (e1: Element, e2: Element) =>
      this.elementSelector.canBeCompared(e1, e2)
    case (n1, n2) =>
      this.nodeTypeMatcher.canBeCompared(n1.getNodeType, n2.getNodeType)
  }
}

