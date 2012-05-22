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
package net.sf.xmlunit.builder

import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import net.sf.xmlunit.transform.Transformation
import scala.reflect.BeanProperty

/**
 * Holds the common builder methods for XSLT related builders.
 *
 * <p><code>B</code> is the derived builder interface.</p>
 */
trait TransformationBuilderBase[B <: TransformationBuilderBase[B]] {
  /**
   * sets the TraX factory to use.
   */
  def usingFactory(factory: TransformerFactory): B

  /**
   * Adds an output property.
   */
  def withOutputProperty(name: String, value: String): B

  /**
   * Adds a parameter.
   */
  def withParameter(name: String, value: Object): B

  /**
   * Sets the stylesheet to use.
   */
  def withStylesheet(source: Source): B

  /**
   * Sets the resolver to use for the document() function and
   * xsi:import/include.
   */
  def withURIResolver(resolver: URIResolver): B
}

/**
 * Base class providing the common logic of the XSLT related builders.
 *
 * <p>Not intended to be used outside of this package.</p>
 *
 * <p>I wish there was a way to say <code>implements B</code> (TB: fixed with
 * self type).</p>
 */
abstract class TransformationBuilder[B <: TransformationBuilderBase[B]](
  source: Source
) extends TransformationBuilderBase[B] { this: B =>
  @BeanProperty protected val helper = new Transformation(source)

  def usingFactory(factory: TransformerFactory) = {
    this.helper.setFactory(factory)
    this
  }

  def withOutputProperty(name: String, value: String) = {
    this.helper.addOutputProperty(name, value)
    this
  }

  def withParameter(name: String, value: Object) = {
    this.helper.addParameter(name, value)
    this
  }

  def withStylesheet(source: Source) = {
    this.helper.setStylesheet(source)
    this
  }

  def withURIResolver(resolver: URIResolver) = {
    this.helper.setURIResolver(resolver)
    this
  }
}

