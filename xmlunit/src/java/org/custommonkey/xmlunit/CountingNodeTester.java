package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Counts the number of nodes in a document
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
     * @exception true if expected num nodes == actual num nodes,
     * false otherwise
     */
    public void noMoreNodes(NodeTest forTest) throws NodeTestException {
        int testedNodes = actualNumNodes;
        actualNumNodes = 0;
        if (testedNodes != expectedNumNodes) {
            throw new NodeTestException("Counted " + testedNodes
                + " node(s) but expected " + expectedNumNodes);
        }
    }

}