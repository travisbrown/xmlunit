package org.custommonkey.xmlunit;

import junit.framework.*;

/**
 * Not actually a test container, but conforms to the semantics
 */
public class test_Constants extends TestCase {
    public static final String BASEDIR =
        (System.getProperty("basedir")==null ? "." : System.getProperty("basedir"));
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    public static final String CHUCK_JONES_RIP_DTD =
        "<!ELEMENT name (#PCDATA)> \n"
        + "<!ELEMENT toon (name)> \n"
        + "<!ELEMENT cartoons (toon*)> \n";

    public static final String CHUCK_JONES_RIP_DTD_DECL =
        "<!DOCTYPE cartoons ["
        + CHUCK_JONES_RIP_DTD + "\n]>";

    private static final String DOCUMENT_START = "<cartoons>";
    private static final String DOCUMENT_END = "</cartoons>";

    public static final String CHUCK_JONES_RIP_XML = DOCUMENT_START
        + "<toon><name>bugs bunny</name></toon>"
        + "<!--meep meep--><toon><name>roadrunnner</name></toon>"
        + DOCUMENT_END;

    public static final String EXTERNAL_DTD = System.getProperty("user.home")
            +  "/cartoons.dtd";
    public static final String DOCUMENT_WITH_GOOD_EXTERNAL_DTD =
        "<!DOCTYPE cartoons SYSTEM \"yak.dtd\">"
        + CHUCK_JONES_RIP_XML;
    public static final String DOCUMENT_WITH_NO_EXTERNAL_DTD =
         CHUCK_JONES_RIP_XML;

    public static final String CHUCK_JONES_SPINNING_IN_HIS_GRAVE_XML =
        DOCUMENT_START + "<kidsTv><toon><name>Yo ho ahoy</name></toon></kidsTV>"
        + DOCUMENT_END;

    public static final String XSLT_START =
        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
            + "version=\"1.0\">";
    public static final String XSLT_XML_OUTPUT_NOINDENT =
        "<xsl:output method=\"xml\" indent=\"no\"/>";
    public static final String XSLT_END = "</xsl:stylesheet>";

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
