package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Listener for callbacks from a
 * {@link DifferenceEngine#compare DifferenceEngine comparison}.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public interface DifferenceListener {
    /**
     * Receive notification that 2 nodes are different.
     * If the difference is {@link Difference#isRecoverable recoverable} the
     *  DifferenceEngine will continue node comparisons, otherwise it will halt.
     * @param expected the control node value being compared
     * @param actual the test node value being compared
     * @param control the control node being compared
     * @param test the test node being compared
     * @param difference one of the constant Differfence instances defined in
     *  {@link DifferenceConstants DifferenceConstants} describing the
     *  cause of the difference
     */
    public void differenceFound(String expected, String actual,
        Node control, Node test, Difference difference);

    /**
     * Receive notification that a comparison between 2 nodes has been skipped
     *  because the node types are not comparable by the DifferenceEngine
     * @param control the control node being compared
     * @param test the test node being compared
     * @see DifferenceEngine
     */
    public void skippedComparison(Node control, Node test);

    /**
     * Determine whether a Difference that this listener has been notified of
     *  should halt further XML comparison. Default behaviour for a Diff
     *  instance is to halt if the Difference is not recoverable.
     * @see Difference#isRecoverable
     * @param afterDifference the last Difference passed to <code>differenceFound</code>
     * @return true to halt further comparison, false otherwise
     */
    public boolean haltComparison(Difference afterDifference);
}