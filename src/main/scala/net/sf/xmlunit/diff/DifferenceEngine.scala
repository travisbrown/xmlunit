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

import javax.xml.transform.Source

/**
 * XMLUnit's difference engine.
 */
trait DifferenceEngine {
  /**
   * Registers a listener that is notified of each comparison.
   */
  def addComparisonListener(listener: ComparisonListener): Unit

  /**
   * Registers a listener that is notified of each comparison with
   * outcome {@link ComparisonResult#EQUAL}.
   */
  def addMatchListener(listener: ComparisonListener): Unit

  /**
   * Registers a listener that is notified of each comparison with
   * outcome other than {@link ComparisonResult#EQUAL}.
   */
  def addDifferenceListener(listener: ComparisonListener): Unit

  /**
   * Sets the strategy for selecting nodes to compare.
   */
  def setNodeMatcher(matcher: NodeMatcher): Unit

  /**
   * Determines whether the comparison should stop after given
   * difference has been found.
   */
  def setDifferenceEvaluator(evaluator: DifferenceEvaluator): Unit

  /**
   * Establish a namespace context that will be used in {@link
   * Comparison.Detail#getXPath Comparison.Detail#getXPath}.
   *
   * <p>Without a namespace context (or with an empty context) the
   * XPath expressions will only use local names for elements and
   * attributes.</p>
   *
   * @param uri2Prefix maps from namespace URI to prefix.
   */
  def setNamespaceContext(uri2Prefix: Map[String, String])

  /**
   * Compares two pieces of XML and invokes the registered listeners.
   */
  def compare(control: Source, test: Source): Unit
}

