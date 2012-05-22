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
 * Useful base-implementation of some parts of the DifferenceEngine
 * interface.
 */
abstract class AbstractDifferenceEngine extends DifferenceEngine {
  private val listeners = new ComparisonListenerSupport
  @BeanProperty var nodeMatcher: NodeMatcher = new DefaultNodeMatcher
  @BeanProperty var differenceEvaluator = DifferenceEvaluators.Default
  @BeanProperty var namespaceContext = Map.empty[String, String] 

  def addComparisonListener(listener: ComparisonListener) {
    require(Option(listener).isDefined, "listener must not be null")
    this.listeners.addComparisonListener(listener)
  }

  def addMatchListener(listener: ComparisonListener) {
    require(Option(listener).isDefined, "listener must not be null")
    this.listeners.addMatchListener(listener)
  }

  def addDifferenceListener(listener: ComparisonListener) {
    require(Option(listener).isDefined, "listener must not be null")
    this.listeners.addDifferenceListener(listener)
  }

  /**
   * Compares the detail values for object equality, lets the
   * difference evaluator evaluate the result, notifies all
   * listeners and returns the outcome.
   */
  protected def compare[A, B](comparison: Comparison[A, B]) = {
    val initial = if (
      Option(comparison.controlDetails.value) ==
      Option(comparison.testDetails.value)
    ) ComparisonResult.EQUAL else ComparisonResult.DIFFERENT
    val altered = this.differenceEvaluator.evaluate(comparison, initial)
    this.listeners.fireComparisonPerformed(comparison, altered)
    altered
  }

  protected def getXPath(context: XPathContext) =
    Option(context).map(_.getXPath).orNull
}

