package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Perform Node-by-Node validation of a DOM Document.
 * Nodes are supplied to <code>testNode</code> method by a NodeTest instance,
 * and after all the nodes in the NodeTest have been supplied the
 * <code>noMoreNodes</code> method is called.
 * @see NodeTest
 */
public interface NodeTester {
    /**
     * Validate a single Node
     * @param aNode
     * @param forTest
     * @exception NodeTestException if the node fails the test
     */
    public void testNode(Node aNode, NodeTest forTest) throws NodeTestException ;

    /**
     * Validate that the Nodes passed one-by-one to the <code>testNode</code>
     * method were all the Nodes expected.
     * @param forTest
     * @exception NodeTestException if this instance was expecting more nodes
     */
    public void noMoreNodes(NodeTest forTest) throws NodeTestException ;
}
