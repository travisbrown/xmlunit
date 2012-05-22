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
package net.sf.xmlunit.input

import java.io.StringReader
import javax.xml.transform.Source
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource
import net.sf.xmlunit.transform.Transformation
import net.sf.xmlunit.util.Convert
import net.sf.xmlunit.util.Nodes

/**
 * A source that is obtained from a different source by stripping all
 * comments.
 */
class CommentLessSource(source: Source) extends DOMSource {
  require(Option(source).isDefined, "source must not be null")
  private val transformation = new Transformation(source)
  transformation.setStylesheet(CommentLessSource.stylesheet)
  this.setNode(transformation.transformToDocument)
}

object CommentLessSource {
  private val stylesheet = new StreamSource(new StringReader("""
    <stylesheet xmlns="http://www.w3.org/1999/XSL/Transform">
      <template match="node()[not(self::comment())]|@*">
        <copy>
          <apply-templates select="node()[not(self::comment())]|@*"/>
        </copy>
      </template>
    </stylesheet>"""
  ))
}

/**
 * A source that is obtained from a different source by removing all
 * empty text nodes and normalizing the non-empty ones.
 *
 * <p>"normalized" in this context means all whitespace characters
 * are replaced by space characters and consecutive whitespace
 * characaters are collapsed.</p>
 */
class WhitespaceNormalizedSource(source: Source) extends DOMSource(
  Nodes.normalizeWhitespace(Convert.toDocument(source))
) {
  this.setSystemId(source.getSystemId)
}

/**
 * A source that is obtained from a different source by removing all
 * empty text nodes and trimming the non-empty ones.
 */
class WhitespaceStrippedSource(source: Source) extends DOMSource(
  Nodes.stripWhitespace(Convert.toDocument(source))
) {
  this.setSystemId(source.getSystemId)
}

