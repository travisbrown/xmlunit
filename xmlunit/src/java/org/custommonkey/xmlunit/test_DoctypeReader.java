package org.custommonkey.xmlunit;


import junit.framework.*;
import junit.textui.TestRunner;

import java.io.*;

/**
 * JUnit test for DoctypeReader
 */
public class test_DoctypeReader extends TestCase {
    private DoctypeReader doctypeReader;
    private StringReader sourceReader;
    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String NO_DTD = "<document><element>one</element></document>";

    public void testRead() throws Exception {
        String oz = "Surgical enhancement is cheap";
        StringReader reader = new StringReader(oz);
        doctypeReader = new DoctypeReader(reader, "Kylie", "bumJob");

        StringBuffer buf = new StringBuffer();
        String expected = "<!DOCTYPE Kylie SYSTEM \"bumJob\">" + oz;
        char[] ch = new char[expected.length()];
        int numChars;
        while ((numChars = doctypeReader.read(ch))!=-1) {
            buf.append(ch);
        }

        assertEquals(expected, buf.toString());
    }

    public void testGetContent() throws Exception {
        String source = "WooPDeDoO!" + NEWLINE + "GooRanga!"
            + NEWLINE + " plIng! ";
        sourceReader = new StringReader(source);
        doctypeReader = new DoctypeReader(sourceReader,
            "nonsense", "words");
        assertEquals(source, doctypeReader.getContent());
        // can get content indefinitely from this reader
        assertEquals(source, doctypeReader.getContent());
    }

    private void initDummyDoctypeReader() {
        sourceReader = new StringReader("yabba");
        doctypeReader = new DoctypeReader(sourceReader,
            "yabba", "don\'t");
    }

    public void testReplaceDoctypeInternalDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(test_Constants.CHUCK_JONES_RIP_DTD_DECL);
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
            doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeExternalDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(
            "<! DOCTYPE PUBLIC \"yak\" SYSTEM \"llama\">");
        assertEquals("<! DOCTYPE ni SYSTEM \"shrubbery\">",
            doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeNoDTD() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(NO_DTD);
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
            doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public void testReplaceDoctypeNoDTDButXMLDecl() {
        initDummyDoctypeReader();
        StringBuffer buf = new StringBuffer(test_Constants.XML_DECLARATION
             + NO_DTD);
        assertEquals(test_Constants.XML_DECLARATION +
            "<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
            doctypeReader.replaceDoctype(buf, "ni", "shrubbery"));
    }

    public test_DoctypeReader(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_DoctypeReader.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

