package org.custommonkey.xmlunit;

import junit.framework.*;

/**
 * Not actually a test container, but conforms to the semantics
 */
public class test_Constants extends TestCase {
    public static final String BASEDIR =
        (System.getProperty("basedir")==null ? "." : System.getProperty("basedir"));

    public test_Constants(String name) {
        super(name);
    }

    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new junit.textui.TestRunner().run(suite());
    }

    /**
     * Return the test suite containing this test
     */
    public static TestSuite suite() {
        return new TestSuite();
    }
}
