package org.custommonkey.xmlunit;

import junit.framework.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.util.*;
import java.io.*;

/**
 * JUnit TestCase subclass.
 * Allows method-level access to Diff class - for the lazy only?
 */
public class XMLTestCase extends TestCase{

    /**
     * Construct a new test case.
     * Subclasses must implement their own constructor which
     * pass the name parameter to it's super class e.g.
     * <pre>
     * public MyXMLTestCase(String name){
     *     super(name);
     * }
     * </pre>
     * @param name Name of test
     */
    public XMLTestCase(String name){
        super(name);
    }

    /**
     * Whether to ignore whitespace in attributes and elements
     * @param ignore
     */
    public void setIgnoreWhitespace(boolean ignore){
        XMLUnit.setIgnoreWhitespace(ignore);
    }

    /**
     * Overide default sax parser used to parser documents
     */
    public void setControlParser(String parser){
        XMLUnit.setControlParser(parser);
    }

    /**
     * Overide default sax parser used to parser documents
     */
    public void setTestParser(String parser){
        XMLUnit.setTestParser(parser);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        return new Diff(control, test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(String control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        return new Diff(new StringReader(control), test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(Reader control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        return new Diff(control, new StringReader(test));
    }

    /**
     * Compare two XML documents provided as strings
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        return new Diff(control, test);
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.similar());
    }

    /**
     * Assert that two XML documents are similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.similar());
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(String err, String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(err + ", " + diff.toString(), true, diff.similar());
    }

    /**
     * Assert that two XML documents are similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(String err, Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(err + ", " + diff.toString(), true, diff.similar());
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), false, diff.similar());
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(String err, String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(err + ", " + diff.toString(), false, diff.similar());
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), false, diff.similar());
    }

    /**
     * Assert that two XML documents are NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(String err, Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        Diff diff = new Diff(control, test);
        assertEquals(err + ", " + diff.toString(), false, diff.similar());
    }
}
