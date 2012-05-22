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
package net.sf.xmlunit.util

import org.w3c.dom.Node
import org.w3c.dom.NodeList
import scala.collection.JavaConversions._

/**
 * Provides an iterable view to a NodeList, the Iterator that can be
 * obtained from this Iterable will be read-only.
 */
final class IterableNodeList(
  private val nodes: NodeList
) extends java.lang.Iterable[Node] {
  private val length = this.nodes.getLength

  def iterator: java.util.Iterator[Node] = new NodeListIterator

  private class NodeListIterator extends Iterator[Node] {
    private var current = 0
    def hasNext = this.current < IterableNodeList.this.length
    def next = {
      val node = IterableNodeList.this.nodes.item(this.current)
      this.current += 1
      node
    }
  }
}

object IterableNodeList {
  /**
   * Turns the NodeList into a list.
   */
  def asList(nodes: NodeList): java.util.List[Node] =
    new IterableNodeList(nodes).toSeq
}

