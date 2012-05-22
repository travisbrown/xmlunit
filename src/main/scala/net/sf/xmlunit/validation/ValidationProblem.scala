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

import org.xml.sax.SAXParseException
import scala.reflect.BeanProperty

/**
 * A validation "problem" which may be an error or a warning.
 */
trait ValidationProblem {
  /**
   * The problem's message.
   */
  @BeanProperty def message: String

  def line: Option[Int]
  def column: Option[Int]

  /**
   * The line where the problem occured or {@link #UNKNOWN UNKNOWN}.
   */
  def getLine = this.line.getOrElse(ValidationProblem.Unknown)

  /**
   * The column where the problem occured or {@link #UNKNOWN UNKNOWN}.
   */
  def getColumn = this.column.getOrElse(ValidationProblem.Unknown)
}

object ValidationProblem {
  val Unknown = -1
  def fromException(e: SAXParseException, isWarning: Boolean) = {
    val line = e.getLineNumber match {
      case i if i > 0 => Some(i)
      case _ => None
    }

    val column = e.getColumnNumber match {
      case i if i > 0 => Some(i)
      case _ => None
    }

    if (isWarning)
      ValidationWarning(e.getMessage, line, column)
    else 
      ValidationError(e.getMessage, line, column)
  }
}

case class ValidationError(
  message: String,
  line: Option[Int],
  column: Option[Int]
) extends ValidationProblem

case class ValidationWarning(
  message: String,
  line: Option[Int],
  column: Option[Int]
) extends ValidationProblem

