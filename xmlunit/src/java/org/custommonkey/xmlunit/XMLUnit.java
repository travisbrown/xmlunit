/*
*****************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

package org.custommonkey.xmlunit;

import org.custommonkey.xmlunit.exceptions.ConfigurationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.EntityResolver;

/**
 * Allows access to project control parameters such as which Parser to use and
 * provides some convenience methods for building Documents from Strings etc.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public final class XMLUnit {
    private static DocumentBuilderFactory controlBuilderFactory;
    private static DocumentBuilderFactory testBuilderFactory;
    private static TransformerFactory transformerFactory;
    private static boolean ignoreWhitespace = false;
    private static EntityResolver testEntityResolver = null;
    private static EntityResolver controlEntityResolver = null;


    private static final String STRIP_WHITESPACE_STYLESHEET
        = new StringBuffer(XMLConstants.XML_DECLARATION)
        .append(XSLTConstants.XSLT_START)
        .append(XSLTConstants.XSLT_XML_OUTPUT_NOINDENT)
        .append(XSLTConstants.XSLT_STRIP_WHITESPACE)
        .append(XSLTConstants.XSLT_IDENTITY_TEMPLATE)
        .append(XSLTConstants.XSLT_END)
        .toString();

    /**
     * Private constructor.
     * Makes class non-instantiable
     */
    private XMLUnit() {
        // access via static methods please
    }

    /**
     * Overide the DocumentBuilder to use to parse control documents.
     * This is useful when comparing the output of two different
     * parsers. Note: setting the control parser before any test cases
     * are run will affect the test parser as well.
     */
    public static void setControlParser(String className) {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", className);
        controlBuilderFactory = null;
        controlBuilderFactory = getControlDocumentBuilderFactory();
    }
    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the control
     * XML in an XMLTestCase.
     * @return parser for control values
     * @throws ConfigurationException
     */
    public static DocumentBuilder newControlParser()
        throws ConfigurationException {
        try {
            controlBuilderFactory = getControlDocumentBuilderFactory();
            DocumentBuilder builder =
                controlBuilderFactory.newDocumentBuilder();
            if (controlEntityResolver!=null) {
                builder.setEntityResolver(controlEntityResolver);
            }
            return builder;
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Sets an EntityResolver to be added to all new test parsers.
     * Setting to null will reset to the default EntityResolver
     */
    public static void setTestEntityResolver(EntityResolver resolver) {
        testEntityResolver = resolver;
    }

    /**
     * Sets an EntityResolver to be added to all new control parsers.
     * Setting to null will reset to the default EntityResolver
     */
    public static void setControlEntityResolver(EntityResolver resolver) {
        controlEntityResolver = resolver;
    }

    /**
     * Get the <code>DocumentBuilderFactory</code> instance used to instantiate
     * parsers for the control XML in an XMLTestCase.
     * @return factory for control parsers
     */
    public static DocumentBuilderFactory getControlDocumentBuilderFactory() {
        if (controlBuilderFactory == null) {
            controlBuilderFactory = DocumentBuilderFactory.newInstance();
            controlBuilderFactory.setNamespaceAware(true);
        }
        return controlBuilderFactory;
    }
    /**
     * Override the <code>DocumentBuilderFactory</code> used to instantiate
     * parsers for the control XML in an XMLTestCase.
     */
    public static void setControlDocumentBuilderFactory(DocumentBuilderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot set control DocumentBuilderFactory to null!");
        }
        controlBuilderFactory = factory;
    }

    /**
     * Overide the DocumentBuilder to use to parser test documents.
     * This is useful when comparing the output of two different
     * parsers. Note: setting the test parser before any test cases
     * are run will affect the control parser as well.
     */
    public static void setTestParser(String className) {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", className);
        testBuilderFactory = null;
        testBuilderFactory = getTestDocumentBuilderFactory();
    }
    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the test XML
     * in an XMLTestCase.
     * @return parser for test values
     * @throws ConfigurationException
     */
    public static DocumentBuilder newTestParser()
        throws ConfigurationException {
        try {
            testBuilderFactory = getTestDocumentBuilderFactory();
            DocumentBuilder builder = testBuilderFactory.newDocumentBuilder();
            if (testEntityResolver!=null) {
                builder.setEntityResolver(testEntityResolver);
            }
            return builder;
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationException(ex);
        }
    }

    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the test XML
     * in an XMLTestCase.
     * @return parser for test values
     * @throws ConfigurationException
     * @deprecated use newTestParser()
     */
    public static DocumentBuilder getTestParser()
        throws ConfigurationException {
        return newTestParser();
    }

    /**
     * Get the <code>DocumentBuilder</code> instance used to parse the test XML
     * in an XMLTestCase.
     * @return parser for control values
     * @deprecated use newControlParser()
     * @throws ConfigurationException
     */
    public static DocumentBuilder getControlParser()
        throws ConfigurationException {
        return newControlParser();
    }

    /**
     * Get the <code>DocumentBuilderFactory</code> instance used to instantiate
     * parsers for the test XML in an XMLTestCase.
     * @return factory for test parsers
     */
    public static DocumentBuilderFactory getTestDocumentBuilderFactory() {
        if (testBuilderFactory == null) {
            testBuilderFactory = DocumentBuilderFactory.newInstance();
            testBuilderFactory.setNamespaceAware(true);
        }
        return testBuilderFactory;
    }
    /**
     * Override the <code>DocumentBuilderFactory</code> used to instantiate
     * parsers for the test XML in an XMLTestCase.
     */
    public static void setTestDocumentBuilderFactory(DocumentBuilderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Cannot set test DocumentBuilderFactory to null!");
        }
        testBuilderFactory = factory;
    }

    /**
     * Whether to ignore whitespace when comparing node values.
     * This method also invokes <code>setIgnoringElementContentWhitespace()</code>
     *  on the underlying control AND test document builder factories.
     */
    public static void setIgnoreWhitespace(boolean ignore){
        ignoreWhitespace = ignore;
        getControlDocumentBuilderFactory().setIgnoringElementContentWhitespace(ignore);
        getTestDocumentBuilderFactory().setIgnoringElementContentWhitespace(ignore);
    }

    /**
     * Whether to ignore whitespace when comparing node values.
     * @return true if whitespace should be ignored when comparing nodes, false
     * otherwise
     */
    public static boolean getIgnoreWhitespace(){
        return ignoreWhitespace;
    }

    /**
     * Compare XML documents provided by two Reader classes.
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     * @deprecated use Diff constructor directly
     */
    public static Diff compare(Reader control, Reader test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare two XML documents provided by SAX <code>InputSources</code>
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     * @deprecated use Diff constructor directly
     */
    public static Diff compare(InputSource control, InputSource test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare two XML documents provided by a string and a Reader.
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     * @deprecated use Diff constructor directly
     */
    public static Diff compare(String control, Reader test) throws SAXException,
                                                                   IOException {
        return new Diff(new StringReader(control), test);
    }

    /**
     * Compare two XML documents provided by a Reader and a string.
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     * @deprecated use Diff constructor directly
     */
    public static Diff compare(Reader control, String test) throws SAXException,
                                                                   IOException {
        return new Diff(control, new StringReader(test));
    }

    /**
     * Compare two XML documents provided as strings.
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     * @deprecated use Diff constructor directly
     */
    public static Diff compare(String control, String test) throws SAXException,
                                                                   IOException {
        return new Diff(control, test);
    }

    /**
     * Utility method to build a Document using the control DocumentBuilder
     * to parse the specified String.
     * @param fromXML
     * @return Document representation of the String content
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildControlDocument(String fromXML)
        throws SAXException, IOException {
        return buildDocument(newControlParser(), new StringReader(fromXML));
    }

    /**
     * Utility method to build a Document using the control DocumentBuilder
     * and the specified InputSource
     * @param fromSource
     * @return Document representation of the String content
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildControlDocument(InputSource fromSource)
        throws IOException, SAXException {
        return buildDocument(newControlParser(), fromSource);
    }

    /**
     * Utility method to build a Document using the test DocumentBuilder
     * to parse the specified String.
     * @param fromXML
     * @return Document representation of the String content
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildTestDocument(String fromXML)
        throws SAXException, IOException {
        return buildDocument(newTestParser(), new StringReader(fromXML));
    }

    /**
     * Utility method to build a Document using the test DocumentBuilder
     * and the specified InputSource
     * @param fromSource
     * @return Document representation of the String content
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildTestDocument(InputSource fromSource)
        throws IOException, SAXException {
        return buildDocument(newTestParser(), fromSource);
    }

    /**
     * Utility method to build a Document using a specific DocumentBuilder
     * and reading characters from a specific Reader.
     * @param withBuilder
     * @param fromReader
     * @return Document built
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildDocument(DocumentBuilder withBuilder,
    Reader fromReader) throws SAXException, IOException {
        return buildDocument(withBuilder, new InputSource(fromReader));
    }
    /**
     * Utility method to build a Document using a specific DocumentBuilder
     * and a specific InputSource
     * @param withBuilder
     * @param fromSource
     * @return Document built
     * @throws SAXException
     * @throws IOException
     */
    public static Document buildDocument(DocumentBuilder withBuilder,
    InputSource fromSource) throws IOException, SAXException {
        return withBuilder.parse(fromSource);
    }

    /**
     * Overide the transformer to use for XSLT transformations (and by
     * implication serialization and XPaths).
     * This is useful when comparing transformer implementations.
     */
    public static void setTransformerFactory(String className) {
        System.setProperty("javax.xml.transform.TransformerFactory",
            className);
        transformerFactory = null;
        getTransformerFactory();
    }

    /**
     * Get the transformer to use for XSLT transformations (and by
     * implication serialization and XPaths).
     * @return the current transformer factory in use
     * a new instance of the default transformer factory
     */
    public static TransformerFactory getTransformerFactory() {
        if (transformerFactory == null) {
            transformerFactory = TransformerFactory.newInstance();
        }
        return transformerFactory;
    }


    /**
     * Override the SAX parser to use in tests.
     * Currently only used by {@link Validator Validator class}
     * @param className
     */
    public static void setSAXParserFactory(String className) {
        System.setProperty("javax.xml.parsers.SAXParserFactory", className);
        getSAXParserFactory();
    }

    /**
     * Get the SAX parser to use in tests.
     * @return the SAXParserFactory instance used by the {@link Validator Validator}
     * to perform DTD validation
     */
    public static SAXParserFactory getSAXParserFactory() {
        SAXParserFactory newFactory = SAXParserFactory.newInstance();
        newFactory.setNamespaceAware(true);
        return newFactory;
    }

    /**
     * Obtain the transformation that will strip whitespace from a DOM containing
     *  empty Text nodes
     * @param forDocument
     * @return a <code>Transform</code> to do the whitespace stripping
     */
    public static Transform getStripWhitespaceTransform(Document forDocument) {
        return new Transform(forDocument, STRIP_WHITESPACE_STYLESHEET);
    }

    /**
     * Place holder for current version info.
     * @return current version
     */
    public static String getVersion() {
        return "1.0"; 
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     */
    public static Diff compareXML(Reader control, Reader test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     */
    public static Diff compareXML(String control, Reader test)
        throws SAXException, IOException {
        return new Diff(new StringReader(control), test);
    }

    /**
     * Compare XML documents provided by two Reader classes
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     */
    public static Diff compareXML(Reader control, String test)
        throws SAXException, IOException {
        return new Diff(control, new StringReader(test));
    }

    /**
     * Compare two XML documents provided as strings
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     * @throws SAXException
     * @throws IOException
     */
    public static Diff compareXML(String control, String test)
        throws SAXException, IOException {
        return new Diff(control, test);
    }

    /**
     * Compare two XML documents provided as strings
     * @param control Control document
     * @param test Document to test
     * @return Diff object describing differences in documents
     */
    public static Diff compareXML(Document control, Document test) {
        return new Diff(control, test);
    }

}

