package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Counts the number of nodes in a document to allow assertions to be made
 *  using a NodeTest.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see NodeTest
 */
public class CountingNodeTester implements NodeTester {
    private final int expectedNumNodes;
    private int actualNumNodes;

    public CountingNodeTester(int expectedNumNodes) {
        this.expectedNumNodes = expectedNumNodes;
    }

    /**
     * A single Node is always valid
     * @param aNode
     * @param forTest
     */
    public void testNode(Node aNode, NodeTest forTest) {
        actualNumNodes++;
    }

    /**
     * Called by NodeTest when all nodes have been iterated over: time to see
     * if all the nodes that were expected were found.
     * Note that this method also invokes {@link #resetCounter resetCounter}
     * so that the instance can be reused.
     * @exception true if expected num nodes == actual num nodes,
     * false otherwise
     */
    public void noMoreNodes(NodeTest forTest) throws NodeTestException {
        int testedNodes = actualNumNodes;
        resetCounter();
        if (testedNodes != expectedNumNodes) {
            throw new NodeTestException("Counted " + testedNodes
                + " node(s) but expected " + expectedNumNodes);
        }
    }

    /**
     * Reset the counter so that an instance can be reused for another
     * <code>NodeTest</code>
     */
    public void resetCounter() {
        actualNumNodes = 0;
    }
}