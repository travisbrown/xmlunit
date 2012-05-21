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

import java.util.LinkedHashMap
import java.util.List
import java.util.Map
import java.util.Set
import java.util.TreeSet
import net.sf.xmlunit.util.Linqy
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Strategy that matches control and tests nodes for comparison.
 */
class DefaultNodeMatcher(
  private val elementSelector: ElementSelector,
  private val nodeTypeMatcher: NodeTypeMatcher
) extends NodeMatcher {
  def this(elementSelector: ElementSelector) = this(elementSelector, new DefaultNodeTypeMatcher())
  def this() = this(ElementSelectors.Default)

  def Iterable<Map.Entry<Node, Node>> match(Iterable<Node> controlNodes,
                                                 Iterable<Node> testNodes) {
        Map<Node, Node> matches = new LinkedHashMap<Node, Node>();
        List<Node> controlList = Linqy.asList(controlNodes);
        List<Node> testList = Linqy.asList(testNodes);
        final int testSize = testList.size();
        Set<Integer> unmatchedTestIndexes = new TreeSet<Integer>();
        for (int i = 0; i < testSize; i++) {
            unmatchedTestIndexes.add(Integer.valueOf(i));
        }
        final int controlSize = controlList.size();
        Match lastMatch = new Match(null, -1);
        for (int i = 0; i < controlSize; i++) {
            Node control = controlList.get(i);
            Match testMatch = findMatchingNode(control, testList,
                                               lastMatch.index,
                                               unmatchedTestIndexes);
            if (testMatch != null) {
                unmatchedTestIndexes.remove(testMatch.index);
                matches.put(control, testMatch.node);
            }
        }
        return matches.entrySet();
    }

    private Match findMatchingNode(final Node searchFor,
                                   final List<Node> searchIn,
                                   final int indexOfLastMatch,
                                   final Set<Integer> availableIndexes) {
        final int searchSize = searchIn.size();
        for (int i = indexOfLastMatch + 1; i < searchSize; i++) {
            if (!availableIndexes.contains(Integer.valueOf(i))) {
                continue;
            }
            if (nodesMatch(searchFor, searchIn.get(i))) {
                return new Match(searchIn.get(i), i);
            }
        }
        for (int i = 0; i < indexOfLastMatch; i++) {
            if (!availableIndexes.contains(Integer.valueOf(i))) {
                continue;
            }
            if (nodesMatch(searchFor, searchIn.get(i))) {
                return new Match(searchIn.get(i), i);
            }
        }
        return null;
    }

  def nodesMatch(n1: Node, n2: Node) = (n1, n2) match {
    case (e1: Element, e2: Element) =>
      this.elementSelector.canBeCompared(e1, e2)
    case (n1, n2) =>
      this.nodeTypeMatcher.canBeCompared(n1.getNodeType, n2.getNodeType)
  }

  case class Match(node: Node, index: Int)

  trait NodeTypeMatcher {
    def canBeCompared(controlType: Short, testType: Short)
  }

  object DefaultNodeTypeMatcher extends NodeTypeMatcher {
    def canBeCompared(controlType: Short, testType: Short) =
      controlType == testType || (
        controlType == Node.TEXT_NODE && testType == Node.CDATA_SECTION_NODE
      ) || (
        controlType == Node.CDATA_SECTION_NODE && testType == Node.TEXT_NODE
      )
  }
}

