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
package net.sf.xmlunit.transform

import java.io.StringWriter
import java.util.Properties
import javax.xml.transform.ErrorListener
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.dom.DOMResult
import javax.xml.transform.stream.StreamResult
import net.sf.xmlunit.exceptions.ConfigurationException
import net.sf.xmlunit.exceptions.XMLUnitException
import org.w3c.dom.Document
import scala.collection.mutable.Map
import scala.reflect.BeanProperty

/**
 * Provides a convenience layer over TraX.
 *
 * <p>Apart from IllegalArgumentExceptions if you try to pass in null
 * values only the transform methods will ever throw exceptions and
 * these will be XMLUnit's runtime exceptions.</p>
 *
 * <p>Each invocation of a transform method will use a fresh
 * Transformer instance, the transform methods are thread-safe.</p>
 */
final class Transformation(private val source: Source) {
  private var stylesheet: Option[Source] = None
  private var factory: Option[TransformerFactory] = None
  private var uriResolver: Option[URIResolver] = None
  private var errorListener: Option[ErrorListener] = None
  private val output = new Properties
  private val parameters = Map.empty[String, Object] 

  require(Option(source).isDefined, "source must not be null")

  /**
   * Set the stylesheet to use.
   * @param s the stylesheet to use - may be null in which case an
   * identity transformation will be performed.
   */
  def setStylesheet(source: Source) {
    this.stylesheet = Option(source)
  }

  /**
   * Set the TraX factory to use.
   *
   * @param factory the factory to use - may be null in which case the
   * default factory will be used.
   */
  def setFactory(factory: TransformerFactory) {
    this.factory = Option(factory)
  }

  /**
   * Set the resolver to use for document() and xsl:include/import
   *
   * <p>The resolver will <b>not</b> be attached to the factory.</p>
   *
   * @param uriResolver the resolver - may be null in which case no explicit
   * resolver will be used
   */
  def setURIResolver(uriResolver: URIResolver) {
    this.uriResolver = Option(uriResolver)
  }

  /**
   * Set the error listener for the transformation.
   *
   * <p>The listener will <b>not</b> be attached to the factory.</p>
   *
   * @param errorListener the listener - may be null in which case no listener
   * will be used
   */
  def setErrorListener(errorListener: ErrorListener) {
    this.errorListener = Option(errorListener)
  }

  /**
   * Add a named output property.
   *
   * @param name name of the property - must not be null
   * @param value value of the property - must not be null
   */
  def addOutputProperty(name: String, value: String) {
    require(Option(name).isDefined, "name must not be null")
    require(Option(value).isDefined, "value must not be null")
    this.output.setProperty(name, value)
  }

  /**
   * Clear all output properties.
   */
  def clearOutputProperties() {
    this.output.clear()
  }

  /**
   * Add a named parameter.
   *
   * @param name name of the parameter - must not be null
   * @param value value of the parameter - may be null
   */
  def addParameter(name: String, value: Object) {
    require(Option(name).isDefined, "name must not be null")
    this.parameters.update(name, value)
  }

  /**
   * Clear all output parameters.
   */
  def clearParameters() {
    this.parameters.clear()
  }

  /**
   * Perform the transformation.
   *
   * @param r where to send the transformation result - must not be null
   * @exception IllegalArgumentException if source or result are null
   * @exception ConfigurationException if the TraX system isn't
   * configured properly
   * @exception XMLUnitException if the transformation throws an
   * exception
   */
  def transformTo(result: Result) {
    require(Option(result).isDefined, "result must not be null")
    try {
      val factory = this.factory.getOrElse(TransformerFactory.newInstance)
      val transformer = this.stylesheet.map(
        factory.newTransformer(_)
      ).getOrElse(factory.newTransformer)
      this.uriResolver.foreach(transformer.setURIResolver(_))
      this.errorListener.foreach(transformer.setErrorListener(_))
      transformer.setOutputProperties(this.output)
      this.parameters.foreach {
        case (k, v) => transformer.setParameter(k, v)
      }
      transformer.transform(source, result)
    } catch {
      case e: TransformerConfigurationException => throw new ConfigurationException(e)
      case e: TransformerException => throw new XMLUnitException(e)
    }
  }

  /**
   * Convenience method that returns the result of the
   * transformation as a String.
   *
   * @exception IllegalArgumentException if source is null
   * @exception ConfigurationException if the TraX system isn't
   * configured properly
   * @exception XMLUnitException if the transformation throws an
   * exception
   */
  def transformToString = {
    val writer = new StringWriter
    this.transformTo(new StreamResult(writer))
    writer.toString
  }

  /**
   * Convenience method that returns the result of the
   * transformation as a Document.
   *
   * @exception IllegalArgumentException if source is null
   * @exception ConfigurationException if the TraX system isn't
   * configured properly
   * @exception XMLUnitException if the transformation throws an
   * exception
   */
  def transformToDocument = {
    val result = new DOMResult
    this.transformTo(result)
    result.getNode.asInstanceOf[Document]
  }
}

