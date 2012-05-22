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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.xml.XMLConstants
import javax.xml.namespace.NamespaceContext
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXSource
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource
import net.sf.xmlunit.exceptions.ConfigurationException
import net.sf.xmlunit.exceptions.XMLUnitException
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException

/**
 * Conversion methods.
 */
object Convert {
  /**
   * Creates a SAX InputSource from a TraX Source.
   *
   * <p>May use an XSLT identity transformation if SAXSource cannot
   * convert it directly.</p>
   */
  def toInputSource(source: Source) = try {
    Option(SAXSource.sourceToInputSource(source)).getOrElse {
      val stream = new ByteArrayOutputStream
      TransformerFactory.newInstance.newTransformer.transform(
        source, new StreamResult(stream)
      )
      SAXSource.sourceToInputSource(
        new StreamSource(new ByteArrayInputStream(stream.toByteArray))
      )
    }
  } catch {
    case e: TransformerConfigurationException => throw new ConfigurationException(e)
    case e: TransformerException => throw new XMLUnitException(e)
  }

  /**
   * Creates a DOM Document from a TraX Source.
   *
   * <p>If the source is a {@link DOMSource} holding a Document
   * Node, this one will be returned.  Otherwise {@link
   * #toInputSource} and a namespace aware DocumentBuilder (created
   * by the default DocumentBuilderFactory) will be used to read the
   * source.  This may involve an XSLT identity transform in
   * toInputSource.</p>
   */
  def toDocument(source: Source): Document = this.extract(source).getOrElse(
    this.toDocument(source, DocumentBuilderFactory.newInstance)
  )

  /**
   * Creates a DOM Document from a TraX Source.
   *
   * <p>If the source is a {@link DOMSource} holding a Document
   * Node, this one will be returned.  Otherwise {@link
   * #toInputSource} and a namespace aware DocumentBuilder (created
   * by given DocumentBuilderFactory) will be used to read the
   * source.  This may involve an XSLT identity transform in
   * toInputSource.</p>
   */
  def toDocument(source: Source, factory: DocumentBuilderFactory): Document =
    this.extract(source).getOrElse {
      // yes, there is a race condition but it is so unlikely to
      // happen that I currently don't care enough
      val aware = factory.isNamespaceAware
      val builder = try {
        if (!aware) factory.setNamespaceAware(true)
        factory.newDocumentBuilder
      } catch {
        case e: ParserConfigurationException => throw new ConfigurationException(e)
      } finally if (!aware) factory.setNamespaceAware(false)

      try builder.parse(this.toInputSource(source)) catch {
        case e: SAXException => throw new XMLUnitException(e)
        case e: IOException => throw new XMLUnitException(e)
      }
    }

  private def extractNode(source: Source) = source match {
    case d: DOMSource => Some(d.getNode)
    case _ => None
  }

  private def extract(source: Source) = this.extractNode(source).flatMap {
    case d: Document => Some(d)
    case _ => None 
  }

  /**
   * Creates a DOM Node from a TraX Source.
   *
   * <p>If the source is a {@link DOMSource} its Node will be
   * returned, otherwise this delegates to {@link #toDocument}.</p>
   */
  def toNode(source: Source) = this.extractNode(source).getOrElse(
    this.toDocument(source, DocumentBuilderFactory.newInstance)
  )

  /**
   * Creates a DOM Node from a TraX Source.
   *
   * <p>If the source is a {@link DOMSource} its Node will be
   * returned, otherwise this delegates to {@link #toDocument}.</p>
   */
  def toNode(source: Source, factory: DocumentBuilderFactory) =
    this.extractNode(source).getOrElse(this.toDocument(source, factory))

  /**
   * Creates a JAXP NamespaceContext from a Map prefix =&gt; Namespace URI.
   */
  def toNamespaceContext(prefix2Uri: Map[String, String]) = {
    val mapping = prefix2Uri.withDefaultValue(XMLConstants.NULL_NS_URI) + (
      XMLConstants.XML_NS_PREFIX -> XMLConstants.XML_NS_URI,
      XMLConstants.XMLNS_ATTRIBUTE -> XMLConstants.XMLNS_ATTRIBUTE_NS_URI
    )

    new NamespaceContext() {
      import scala.collection.JavaConversions._

      def getNamespaceURI(prefix: String) = {
        require(Option(prefix).isDefined, "prefix must not be null")
        mapping(prefix)
      }

      def getPrefix(uri: String) = this.prefixes(uri).headOption.orNull

      private def prefixes(uri: String) = { 
        require(Option(uri).isDefined, "prefix must not be null")
        mapping.filter(_._2 == uri).keys
      }

      def getPrefixes(uri: String): java.util.Iterator[_]
        = this.prefixes(uri).iterator
    }
  }
}

