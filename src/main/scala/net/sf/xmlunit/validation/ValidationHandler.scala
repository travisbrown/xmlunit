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

import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import scala.collection.mutable.Buffer

/**
 * ErrorHandler collecting parser exceptions as ValidationProblems
 */
final class ValidationHandler extends ErrorHandler {
  private val problems = Buffer.empty[ValidationProblem]
  private var valid = true

  // fatal errors are re-thrown by the parser
  private var lastFatalError: SAXParseException = _

  def error(e: SAXParseException) {
    if (e != this.lastFatalError) {
      this.valid = false
      this.problems += ValidationProblem.fromException(e, false)
    }
  }

  def fatalError(e: SAXParseException) {
    this.valid = false
    this.lastFatalError = e
    this.problems += ValidationProblem.fromException(e, false)
  }

  def warning(e: SAXParseException) {
    this.problems += ValidationProblem.fromException(e, true)
  }

  def getResult = new ValidationResult(this.valid, this.problems)
}

