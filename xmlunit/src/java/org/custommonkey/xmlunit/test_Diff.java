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
    public void testDiffElement(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.diffElement(new Element("test"), new Element("test"));
        assert(!diff.similar());
    }

    public void testToString(){
        Diff diff = new Diff();
        Element control = new Element("test");
        control.setText("Monkey");
        Element test = new Element("test");
        test.setText("Chicken");
        diff.diffElement(control, test);
        assertEquals("Expected: <test>Monkey</test>, but was: <test>Chicken</test>", diff.toString());
        diff = new Diff();
        control = new Element("test");
        control.setText("Monkey");
        test = new Element("test");
        diff.diffElement(control, test);
        assertEquals("Expected: <test>Monkey</test>, but was: <test/>", diff.toString());
    }

    /**
     * Test the diff.
     */
    public void testDiffNullElement(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.diffElement(null, new Element("test"));
        try{
            assert(diff.toString(), diff.similar());
            fail("Throwable not thrown");
        }catch(AssertionFailedError t){
            assertEquals("Expected: null, but was: <test/>", t.getMessage());
        }
        diff = new Diff();
        diff.diffElement(new Element("test"), null);
        try{
            assert(diff.toString(), diff.similar());
            fail("Throwable not thrown");
        }catch(AssertionFailedError t){
            assertEquals("Expected: <test/>, but was: null", t.getMessage());
        }
    }

    /**
     * Test the diff.
     */
    public void testDiffAttribute(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.diffAttribute(new Attribute("test", "test"), new Attribute("test", "test"));
        assert(!diff.similar());
    }

    /**
     * Return the test suite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_Diff.class);
    }
}
