package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import java.io.*;

/**
 * JUnit test for NodeTest (Nice comment tim :-)
 */
public class test_NodeTest extends TestCase {
    private NodeTest nodeTest;

    private class NodeTypeTester implements NodeTester {
        private short type;
        public NodeTypeTester(short type) {
            this.type = type;
        }
        public void testNode(Node aNode, NodeTest forTest) {
            assertEquals(type, aNode.getNodeType());
        }
        public void noMoreNodes(NodeTest forTest) {
        }
    }

    private class RejectingNodeTester implements NodeTester {
        public boolean completed;
        public void testNode(Node aNode, NodeTest forTest)
        throws NodeTestException {
            throw new NodeTestException("Reject all nodes", aNode);
        }
        public void noMoreNodes(NodeTest forTest) throws NodeTestException {
            completed = true;
            throw new NodeTestException("Rejection");
        }
    }

    public void testFiltering() throws Exception {
        nodeTest = new NodeTest(
            new StringReader("<message><hello>folks</hello></message>"));
        short nodeType = Node.ELEMENT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        nodeType = Node.TEXT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        nodeType = Node.COMMENT_NODE;
        nodeTest.performTest(new NodeTypeTester(nodeType), nodeType);

        short[] nodeTypes = new short[] {Node.TEXT_NODE, Node.COMMENT_NODE};
        nodeTest.performTest(new NodeTypeTester(Node.TEXT_NODE), nodeTypes);
    }

    public void testNodeTesting() throws Exception {
        nodeTest = new NodeTest(
            new StringReader("<keyboard><qwerty>standard</qwerty></keyboard>"));
        RejectingNodeTester tester = new RejectingNodeTester();

        try {
            nodeTest.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            assertEquals("not completed", false, tester.completed);
        }

        try {
            nodeTest.performTest(tester, Node.CDATA_SECTION_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            assertEquals("completed", true, tester.completed);
        }
    }

    public test_NodeTest(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_NodeTest.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

