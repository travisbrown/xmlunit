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
package net.sf.xmlunit.validation

import javax.xml.transform.Source
import scala.collection.mutable.Buffer
import scala.reflect.BeanProperty

/**
 * Validates a piece of XML against a schema given in a supported
 * language or the defintion of such a schema itself.
 */
abstract class Validator {
  /**
   * The URI (or for example the System ID in case of a DTD) that
   * identifies the schema to validate or use during validation.
   */
  @BeanProperty var schemaURI = null

  private val sources = Buffer.empty[Source] 

  /**
   * Where to find the schema.
   */
  def setSchemaSources(sources: Array[Source]) {
    this.sources.clear()
    this.sources ++= Option(sources).flatten
  }

  /**
   * Where to find the schema.
   */
  def setSchemaSource(source: Source) {
    this.sources.clear()
    this.sources += source
  }

  protected def getSchemaSources: Array[Source] = this.sources.toArray

  /**
   * Validates a schema.
   *
   * @throws UnsupportedOperationException if the language's
   * implementation doesn't support schema validation
   */
  def validateSchema: ValidationResult

  /**
   * Validates an instance against the schema.
   */
  def validateInstance(instance: Source): ValidationResult
}

object Validator {
  /**
   * Factory that obtains a Validator instance based on the schema language.
   *
   * @see Languages
   */
  def forLanguage(language: String) = language match {
    case Languages.XML_DTD_NS_URI =>
      new ParsingValidator(Languages.XML_DTD_NS_URI)
    case language => new JAXPValidator(language)
  }
}

