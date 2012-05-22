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

import javax.xml.XMLConstants
import javax.xml.namespace.QName
import org.w3c.dom.Attr
import org.w3c.dom.CDATASection
import org.w3c.dom.CharacterData
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.ProcessingInstruction
import org.w3c.dom.Text
import scala.collection.JavaConversions._

/**
 * Utility algorithms that work on DOM nodes.
 */
object Nodes {
  /**
   * Extracts a Node's name, namespace URI (if any) and prefix as a
   * QName.
   */
  def getQName(node: Node) = Option(node.getLocalName).map {
    name => new QName(
      node.getNamespaceURI,
      name,
      Option(node.getPrefix).getOrElse(XMLConstants.DEFAULT_NS_PREFIX)
    )
  }.getOrElse(new QName(node.getNodeName))

  /**
   * Tries to merge all direct Text and CDATA children of the given
   * Node and concatenates their value.
   *
   * @return an empty string if the Node has no Text or CDATA
   * children.
   */
  def getMergedNestedText(node: Node) =
    new IterableNodeList(node.getChildNodes).filter(child =>
      child.isInstanceOf[Text] || child.isInstanceOf[CDATASection]
    ).flatMap(child => Option(child.getNodeValue)).mkString

  /**
   * Obtains an element's attributes as Map.
   */
  def getAttributes(node: Node): java.util.Map[QName, String] =
    mapAsJavaMap(Option(node.getAttributes).map { attrs =>
      (0 until attrs.getLength).map(attrs.item(_).asInstanceOf[Attr])
    }.flatten.map(a => this.getQName(a) -> a.getValue).toMap)

  /**
   * Creates a new Node (of the same type as the original node) that
   * is similar to the orginal but doesn't contain any empty text or
   * CDATA nodes and where all textual content including attribute
   * values or comments are trimmed.
   */
  def stripWhitespace(original: Node) = {
    val cloned = original.cloneNode(true)
    cloned.normalize()
    this.handleWsRec(cloned, false)
    cloned
  }

  /**
   * Creates a new Node (of the same type as the original node) that
   * is similar to the orginal but doesn't contain any empty text or
   * CDATA nodes and where all textual content including attribute
   * values or comments are trimmed and normalized.
   *
   * <p>"normalized" in this context means all whitespace characters
   * are replaced by space characters and consecutive whitespace
   * characaters are collapsed.</p>
   */
  def normalizeWhitespace(original: Node) = {
    val cloned = original.cloneNode(true)
    cloned.normalize()
    this.handleWsRec(cloned, true)
    cloned
  }

  /**
   * Trims textual content of this node, removes empty text and
   * CDATA children, recurses into its child nodes.
   * @param normalize whether to normalize whitespace as well
   */
  def handleWsRec(node: Node, normalize: Boolean) {
    if (
      node.isInstanceOf[CharacterData] ||
      node.isInstanceOf[ProcessingInstruction]
    ) {
      val trimmed = node.getNodeValue.trim
      node.setNodeValue(if (normalize) this.normalize(trimmed) else trimmed)
    }

    new IterableNodeList(node.getChildNodes).filter { child =>
      this.handleWsRec(child, normalize)
      
      !node.isInstanceOf[Attr] &&
        (child.isInstanceOf[Text] || child.isInstanceOf[CDATASection]) &&
        child.getNodeValue.length == 0
    }.foreach(node.removeChild)

    Option(node.getAttributes).foreach { attrs =>
      (0 until attrs.getLength).foreach { i =>
        this.handleWsRec(attrs.item(i), normalize)
      }
    }
  }

  /**
   * Normalize a string.
   *
   * <p>"normalized" in this context means all whitespace characters
   * are replaced by space characters and consecutive whitespace
   * characaters are collapsed.</p>
   */
  def normalize(s: String) = {
    val cleaned = s.replaceAll("\\s+", " ")
    if (cleaned == s) s else cleaned
  }
}

