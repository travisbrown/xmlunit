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
package net.sf.xmlunit.exceptions

/**
 * Exception thrown when anything inside JAXP throws a
 * *ConfigurationException.
 */
class ConfigurationException(cause: Throwable) extends XMLUnitException(cause)

/**
 * Base class of any Exception thrown within XMLUnit.
 *
 * @param message the detail message
 * @param cause the root cause of the exception
 */
class XMLUnitException(
  message: String,
  cause: Throwable
) extends RuntimeException(message, cause) {
  /**
   * Inititializes an exception without cause.
   *
   * @param message the detail message
   */
  def this(message: String) = this(message, null)

  /**
   * Inititializes an exception using the wrapped exception's message.
   *
   * @param cause the root cause of the exception
   */
  def this(cause: Throwable) =
    this(Option(cause).map(_.getMessage).orNull, cause)
}

