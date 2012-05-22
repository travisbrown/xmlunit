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

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.IOException
import java.io.Reader
import java.io.StringReader
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.URIResolver
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamSource
import net.sf.xmlunit.exceptions.XMLUnitException
import org.w3c.dom.Document
import org.w3c.dom.Node

/**
 * Fluent API to create Source instances.
 */
object Input {
  trait Builder {
    /**
     * build the actual Source instance.
     */
    def build: Source
  }

  private class DOMBuilder(node: Node) extends Builder {
    val build = new DOMSource(node)
  }

  /**
   * Build a Source from a DOM Document.
   */
  def fromDocument(document: Document): Builder = new DOMBuilder(document)

  /**
   * Build a Source from a DOM Node.
   */
  def fromNode(node: Node): Builder = new DOMBuilder(node)

  private case class StreamBuilder(build: Source) extends Builder {
    def this(file: File) = this(new StreamSource(file))
    def this(stream: InputStream) = this(new StreamSource(stream))
    def this(reader: Reader) = this(new StreamSource(reader))
    
    def setSystemId(id: String) {
      Option(id).foreach(this.build.setSystemId(_))
    }
  }

  /**
   * Build a Source from a file.
   */
  def fromFile(file: File): Builder = new StreamBuilder(file)

  /**
   * Build a Source from a named file.
   */
  def fromFile(path: String): Builder = new StreamBuilder(new File(path))

  /**
   * Build a Source from a stream.
   */
  def fromStream(stream: InputStream): Builder = new StreamBuilder(stream)

  /**
   * Build a Source from a reader.
   */
  def fromReader(reader: Reader): Builder = new StreamBuilder(reader)

  /**
   * Build a Source from a string.
   */
  def fromMemory(s: String): Builder = this.fromReader(new StringReader(s))

  /**
   * Build a Source from an array of bytes.
   */
  def fromMemory(bytes: Array[Byte]): Builder = this.streamFromMemory(bytes)

  private def streamFromMemory(bytes: Array[Byte]): StreamBuilder =
    new StreamBuilder(new ByteArrayInputStream(bytes))

  /**
   * Build a Source from an URL.
   */
  def fromURL(url: URL): Builder = {
    var in: InputStream = null
    try {
      in = url.openStream()
      val out = new ByteArrayOutputStream
      val buffer = Array.ofDim[Byte](4096)
      var read = in.read(buffer)
      while (read >= 0) {
        if (read > 0) out.write(buffer, 0, read)
        read = in.read(buffer)
      }
    
      val builder = this.streamFromMemory(out.toByteArray)
      try builder.setSystemId(url.toURI.toString) catch {
        // impossible - shouldn't have been an URL in the first place
        case e: URISyntaxException => builder.setSystemId(url.toString)
      }
      builder
    } catch {
      case e: IOException => throw new XMLUnitException(e)
    } finally {
      if (in != null) in.close()
    }
  }

  /**
   * Build a Source from an URI.
   * @param uri must represent a valid URL
   */
  def fromURI(uri: URI): Builder = try this.fromURL(uri.toURL) catch {
    case e: MalformedURLException =>
      throw new IllegalArgumentException("uri " + uri + " is not an URL", e)
  }

  /**
   * Build a Source from an URI.
   * @param uri must represent a valid URL
   */
  def fromURI(uri: String): Builder = try this.fromURI(new URI(uri)) catch {
    case e: URISyntaxException =>
      throw new IllegalArgumentException("uri " + uri + " is not an URI", e)
  }

  trait TransformationSourceBuilder
    extends TransformationBuilderBase[TransformationSourceBuilder]
    with Builder {
    /**
     * Sets the stylesheet to use.
     */
    def withStylesheet(builder: Builder): TransformationSourceBuilder
  }

  private class Transformation(source: Source)
    extends TransformationBuilder[TransformationSourceBuilder](source)
    with TransformationSourceBuilder {
    def withStylesheet(builder: Builder): TransformationSourceBuilder =
      this.withStylesheet(builder.build)
    def build = new DOMSource(this.helper.transformToDocument)
  }

  /**
   * Build a Source by XSLT transforming a different Source.
   */
  def byTransforming(source: Source): TransformationSourceBuilder =
    new Transformation(source)

  /**
   * Build a Source by XSLT transforming a different Source.
   */
  def byTransforming(builder: Builder): TransformationSourceBuilder =
    return this.byTransforming(builder.build)
}

