package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

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

    private String getDocumentBuilderFactoryImplClass() {
        return DocumentBuilderFactory.newInstance().getClass().getName();
    }

    /**
     * Test overiding the SAX parser used to parse control documents
     */
    public void testSetControlParser() throws Exception {
        Object before = XMLUnit.getControlParser();
        XMLUnit.setControlParser(getDocumentBuilderFactoryImplClass());
        assertEquals("should be different", false,
            before == XMLUnit.getControlParser());
    }

    public void testIgnoreWhitespace() throws Exception {
        assertEquals("should not ignore whitespace by default",
            false, XMLUnit.getIgnoreWhitespace());
        XMLUnit.setIgnoreWhitespace(true);
        String test="<test>  monkey   </test>";
        String control="<test>monkey</test>";
        assertEquals("Should be similar", true,
            XMLUnit.compare(control, test).similar());
        try {
            XMLUnit.setIgnoreWhitespace(false);
            assertEquals("Should be different", false,
                XMLUnit.compare(control, test).similar());
        } finally {
            // restore default setting
            XMLUnit.setIgnoreWhitespace(false);
        }
    }

    /**
     * Test overiding the SAX parser used to parse test documents
     */
    public void testSetTestParser() throws Exception {
        Object before = XMLUnit.getTestParser();
        XMLUnit.setTestParser(getDocumentBuilderFactoryImplClass());
        assertEquals("should be different", false,
            before==XMLUnit.getTestParser());
    }

    public void testSetTransformerFactory() throws Exception {
        Object before = XMLUnit.getTransformerFactory();
        XMLUnit.setTransformerFactory(before.getClass().getName());
        assertEquals("should be different", false,
            before==XMLUnit.getTransformerFactory());
    }

    public void testStripWhitespaceTransform() throws Exception {
        Document doc = XMLUnit.buildTestDocument(
            test_Constants.XML_WITH_WHITESPACE);
        Transform transform = XMLUnit.getStripWhitespaceTransform(doc);
        Diff diff = new Diff(test_Constants.XML_WITHOUT_WHITESPACE, transform);
        assertTrue(diff.similar());
    }

    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new TestRunner().run(suite());
    }

    /**
     * Returns a TestSuite containing this test case.
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLUnit.class);
    }
}
