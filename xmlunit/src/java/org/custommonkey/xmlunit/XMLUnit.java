package org.custommonkey.xmlunit;

import junit.framework.*;
import javax.xml.parsers.*;
import java.util.*;
import java.io.*;
import org.xml.sax.*;

/**
 * Allows access to project control parameters such as which Parser to use
 */
public final class XMLUnit {
    private static DocumentBuilderFactory controlBuilderFactory
        = DocumentBuilderFactory.newInstance();
    private static DocumentBuilderFactory testBuilderFactory
        = DocumentBuilderFactory.newInstance();
    private static boolean ignoreWhitespace = false;

    /**
     * Private constructor
     * Makes class non-instantiable
     */
    private XMLUnit() {
        // access via static methods please
    }

    /**
     * Overide the parser to use to parse control documents.
     * This is useful when comparing the output of two different
     * parsers.
     */
    public static void setControlParser(String parser)
    throws FactoryConfigurationError {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", parser);
        controlBuilderFactory = DocumentBuilderFactory.newInstance();
    }
    /**
     * Used by Diff class
     * @return parser for control values
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder getControlParser()
    throws ParserConfigurationException {
        return controlBuilderFactory.newDocumentBuilder() ;
    }

    /**
     * Overide the parser to use to parser test documents.
     * This is useful when comparing the output of two different
     * parsers.
     */
    public static void setTestParser(String parser)
    throws FactoryConfigurationError {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", parser);
        testBuilderFactory = DocumentBuilderFactory.newInstance();
    }
    /**
     * Used by Diff class
     * @return parser for test values
     * @throws ParserConfigurationException
     */
    public static DocumentBuilder getTestParser()
    throws ParserConfigurationException {
        return testBuilderFactory.newDocumentBuilder();
    }

    /**
     * Ignore whitespace when comparing nodes.
     */
    public static void setIgnoreWhitespace(boolean ignore){
        ignoreWhitespace = ignore;
    }
    public static boolean getIgnoreWhitespace(){
        return ignoreWhitespace;
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @throws Exception Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @decremented use Diff constructor directly
     */
    public static Diff compare(Reader control, Reader test) throws SAXException,
    IOException, ParserConfigurationException {
        return new Diff(control, test);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws Exception Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @decremented use Diff constructor directly
     */
    public static Diff compare(String control, Reader test) throws SAXException,
    IOException, ParserConfigurationException {
        return new Diff(new StringReader(control), test);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws Exception Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @decremented use Diff constructor directly
     */
    public static Diff compare(Reader control, String test) throws SAXException,
    IOException, ParserConfigurationException {
        return new Diff(control, new StringReader(test));
    }

    /**
     * Compare two XML documents provided as strings
     * @throws Exception Error thrown in response to an error passing xml doc
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @decremented use Diff constructor directly
     */
    public static Diff compare(String control, String test) throws SAXException,
    IOException, ParserConfigurationException {
        return new Diff(control, test);
    }

    /**
     * Place holder for version info
     * @return current version
     */
    public static String getVersion() {
        return "0.4";
    }
}
