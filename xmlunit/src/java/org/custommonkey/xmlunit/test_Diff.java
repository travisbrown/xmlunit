package org.custommonkey.xmlunit;

import junit.framework.*;
import org.jdom.*;

/**
 * Test a Diff
 */
public class test_Diff extends TestCase{

    /**
     * Construct a test
     * @param name Test name
     */
    public test_Diff(String name){
        super(name);
    }

    /**
     * Test the diff.
     */
    public void testDiff(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.diff(new Element("test"), new Element("test"));
        assert(!diff.similar());
    }

    /**
     * Return the test suite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_Diff.class);
    }
}
