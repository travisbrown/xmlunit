package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * JUnit test for HTMLDocumentBuilder
 */
public class test_HTMLDocumentBuilder extends XMLTestCase {
    private static final String xHtml =
            "<html><head><title>test</title></head>" +
            "<body><h1>hello</h1><p>world</p><hr/><div><img src=\"foo.bar\"/>" +
            "<ul><li>one</li><li>two</li></ul></div></body></html>";
    private Document xHtmlDocument;
    private HTMLDocumentBuilder parser;
    private TolerantSaxDocumentBuilder builder;

    public void testParseGoodHtml() throws Exception {
        assertParsedDocumentEqual(xHtmlDocument, xHtml);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    public void testParseOldHtml() throws Exception {
        String oldHTML=
            "<html><head><title>test</title></head>" +
            "<body><h1>hello</h1><p>world<hr><div><img src=\"foo.bar\">" +
            "<ul><li>one<li>two</ul></div></body></html>";
        assertParsedDocumentEqual(xHtmlDocument, oldHTML);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    public void testParsePoorHtml() throws Exception {
        String poorHTML=
            "<html><head><title>test</title></head>" +
            "<body><h1>hello</h1><p>world<hr><div><img src=\"foo.bar\">" +
            "<ul><li>one<li>two";
        assertParsedDocumentEqual(xHtmlDocument, poorHTML);
        assertEquals(parser.getTrace(),-1, parser.getTrace().indexOf("WARNING"));
    }

    private void assertParsedDocumentEqual(Document control, String test)
    throws Exception {
        assertXMLEqual(control, parser.parse(test));
    }

    public test_HTMLDocumentBuilder(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        xHtmlDocument = XMLUnit.buildControlDocument(xHtml);
        builder = new TolerantSaxDocumentBuilder(XMLUnit.getTestParser());
        parser = new HTMLDocumentBuilder(builder);

    }
    public static TestSuite suite() {
        return new TestSuite(test_HTMLDocumentBuilder.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

