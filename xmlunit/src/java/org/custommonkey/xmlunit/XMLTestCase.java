package org.custommonkey.xmlunit;

import junit.framework.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.io.*;

public class XMLTestCase extends TestCase{
    /** SAX document builder */
    private static final SAXBuilder builder = new SAXBuilder();

    /**
     * Construct a new test case.
     * Test must implement there own constructor which must
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
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(Reader control, Reader test) throws JDOMException {
        return XMLUnit.compare(control, test);
    }

    /** 
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(String control, Reader test) throws JDOMException {
        return XMLUnit.compare(control, test);
    }

    /** 
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(Reader control, String test) throws JDOMException {
        return XMLUnit.compare(control, test);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public Diff compareXML(String control, String test) throws JDOMException {
        return XMLUnit.compare( control, test);
    }

    /**
     * Assert that two XML documents are the similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(String control, String test) throws JDOMException{
        assert(compareXML(control, test).similar());
    }

    /**
     * Assert that two XML documents are the similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLEqual(String err, String control, String test) throws JDOMException{
        assert(err, compareXML(control, test).similar());
    }

    /**
     * Assert that two XML documents are the NOT similar
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(String control, String test) throws JDOMException{
        assert(!compareXML(control, test).similar());
    }

    /**
     * Assert that two XML documents are the NOT similar
     * @param err Message to be displayed on assertion failure
     * @param control XML to be compared against
     * @param test XML to be tested
     */
    public void assertXMLNotEqual(String err, String control, String test) throws JDOMException{
        assert(err, !compareXML(control, test).similar());
    }
}
