package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.StringReader;

/**
 * JUnit test for CountingNodeTester
 */
public class test_CountingNodeTester extends TestCase {
    private NodeTest test;
    private CountingNodeTester tester;

    public void testPositivePath() throws Exception {
        test = new NodeTest(new StringReader("<a><b>c</b></a>"));
        tester = new CountingNodeTester(2);
        test.performTest(tester, Node.ELEMENT_NODE);

        tester = new CountingNodeTester(1);
        test.performTest(tester, Node.TEXT_NODE);

        tester = new CountingNodeTester(3);
        test.performTest(tester,
            new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE});

        tester = new CountingNodeTester(0);
        test.performTest(tester, Node.COMMENT_NODE);
    }

    public void testNegativePath() throws Exception {
        test = new NodeTest(new StringReader("<a><b>c</b></a>"));
        try {
            tester = new CountingNodeTester(2);
            test.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(1);
            test.performTest(tester, Node.ELEMENT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(2);
            test.performTest(tester,
                new short[] {Node.TEXT_NODE, Node.ELEMENT_NODE});
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(1);
            test.performTest(tester, Node.COMMENT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }

        try {
            tester = new CountingNodeTester(0);
            test.performTest(tester, Node.TEXT_NODE);
            fail("Expected NodeTestException");
        } catch (NodeTestException e) {
            // failure, as expected
        }
    }

    public test_CountingNodeTester(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_CountingNodeTester.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

