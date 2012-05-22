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

import javax.xml.transform.Result
import javax.xml.transform.Source
import net.sf.xmlunit.transform.Transformation
import org.w3c.dom.Document

/**
 * Fluent API access to {@link Transformation}.
 */
object Transform {
  trait Builder extends TransformationBuilderBase[Builder] {
    /**
     * Create the result of the transformation.
     */
    def build: TransformationResult
  }

  trait TransformationResult {
    /**
     * Output the result to a TraX Result.
     */
    def to(result: Result): Unit

    /**
     * Output the result to a DOM Document.
     */
    def toDocument: Document
  }

  /**
   * Build a transformation for a source document.
   */
  def source(source: Source): Builder = new TransformBuilder(source)
  
  private class TransformBuilder(source: Source) extends TransformationBuilder[Builder](source) with Builder with TransformationResult {
    def build = this
    def to(result: Result) {
      this.helper.transformTo(result)
    }
    def toDocument = this.helper.transformToDocument
    override def toString = this.helper.transformToString
  }
}

