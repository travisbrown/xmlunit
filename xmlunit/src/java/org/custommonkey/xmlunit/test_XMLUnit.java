package org.custommonkey.xmlunit;

import junit.framework.*;
import java.io.*;
import javax.xml.parsers.*;

/**
 * Test case for XMLUnit
 */
public class test_XMLUnit extends TestCase{
    /**
     * Contructs a new test case.
     */
    public test_XMLUnit(String name){
        super(name);
    }

    /**
     * Test overiding the SAX parser used to parse control documents
     */
    public void testSetControlParser() throws Exception {
        Object before = XMLUnit.getControlParser();
        XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        assert("should be different", before!=XMLUnit.getControlParser());
    }

    public void testIgnoreWhitespace() throws Exception {
        assertEquals("should not ignore whitespace by default",
            false, XMLUnit.getIgnoreWhitespace());
        XMLUnit.setIgnoreWhitespace(true);
        String test="<test>  monkey   </test>";
        String control="<test>monkey</test>";
        assert("Should be similar", XMLUnit.compare(control, test).similar());
        XMLUnit.setIgnoreWhitespace(false);
        assert("Should be different", !XMLUnit.compare(control, test).similar());
    }

    /**
     * Test overiding the SAX parser used to parse test documents
     */
    public void testSetTestParser() throws Exception {
        Object before = XMLUnit.getTestParser();
        XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        assert("should be different", before!=XMLUnit.getTestParser());
    }
    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new junit.textui.TestRunner().run(suite());
    }

    /**
     * Returns a TestSuite containing this test case.
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLUnit.class);
    }
}
