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
package net.sf.xmlunit.xpath

import java.util.Map
import javax.xml.transform.Source
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory
import net.sf.xmlunit.exceptions.ConfigurationException
import net.sf.xmlunit.exceptions.XMLUnitException
import net.sf.xmlunit.util.Convert
import net.sf.xmlunit.util.IterableNodeList
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import scala.collection.JavaConversions._

/**
 * Simplified access to JAXP's XPath API.
 */
class JAXPXPathEngine(factory: XPathFactory) extends XPathEngine {
  /**
   * Create an XPathEngine that uses JAXP's default XPathFactory
   * under the covers.
   */
  def this() = this(XPathFactory.newInstance)

  private val xpath = try factory.newXPath catch {
    case e: Exception => throw new ConfigurationException(e)
  }

  /**
   * {@inheritDoc}
   */
  def selectNodes(path: String, source: Source): java.lang.Iterable[Node] = try {
    new IterableNodeList(this.xpath.evaluate(
      path, Convert.toInputSource(source), XPathConstants.NODESET
    ).asInstanceOf[NodeList])
  } catch {
    case e: XPathExpressionException => throw new XMLUnitException(e)
  }

  /**
   * {@inheritDoc}
   */
  def evaluate(path: String, source: Source): String = try {
    this.xpath.evaluate(path, Convert.toInputSource(source))
  } catch {
    case e: XPathExpressionException => throw new XMLUnitException(e)
  }

  /**
   * {@inheritDoc}
   */
  def setNamespaceContext(prefix2Uri: Map[String, String]) {
    this.xpath.setNamespaceContext(
      Convert.toNamespaceContext(prefix2Uri.toMap)
    )
  }
}

