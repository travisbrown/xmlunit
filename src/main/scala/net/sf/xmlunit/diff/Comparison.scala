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
package net.sf.xmlunit.diff

import scala.reflect.BeanProperty

/**
 * Details of a single comparison XMLUnit has performed.
 */
class Comparison(
  /**
   * The kind of comparison performed.
   */
  @BeanProperty val `type`: ComparisonType,
  controlTarget: Object,
  controlXPath: String,
  controlValue: Object,
  testTarget: Object,
  testXPath: String,
  testValue: Object
) {
    /**
     * The details of a target (usually some representation of an XML
     * Node) that took part in the comparison.
     */
    case class Detail(
        /**
         * The actual target.
         */
        @BeanProperty target: Object,
        /**
         * XPath leading to the target.
         */
        @BeanProperty xPath: String,
        /**
         * The value for comparison found at the current target.
         */
        @BeanProperty value: Object
    )

    /**
     * Details of the control target.
     */
    @BeanProperty val controlDetails = Detail(controlTarget, controlXPath, controlValue)
    /**
     * Details of the test target.
     */
    @BeanProperty val testDetails = Detail(testTarget, testXPath, testValue)
}

