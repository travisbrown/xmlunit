/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * JUnit test for DoctypeInputStream
 */
public class test_DoctypeInputStream extends TestCase {

    private static final String NEWLINE = System.getProperty("line.separator");
    private static final String NO_DTD = "<document><element>one</element></document>";
    private File testFile;

    public void tearDown() {
        if (testFile != null) {
            testFile.delete();
        }
    }

    private FileInputStream testDocument(String content)
        throws IOException {
        testFile = File.createTempFile("xmlunit_", ".xml");
        FileOutputStream fos = new FileOutputStream(testFile);
        OutputStreamWriter w = new OutputStreamWriter(fos, "ISO-8859-1");
        w.write(content);
        w.close();

        return new FileInputStream(testFile);
    }

    private static String readFully(DoctypeInputStream dis)
        throws IOException {
        StringBuffer buf = new StringBuffer();
        char[] ch = new char[1024];
        int numChars;
        InputStreamReader reader =
            new InputStreamReader(dis, "ISO-8859-1");
        while ((numChars = reader.read(ch))!=-1) {
            buf.append(ch, 0, numChars);
        }
        return buf.toString();
    }

    private void assertEquals(String expected, String input, String docType,
                              String systemId) throws IOException {
        FileInputStream fis = null;
        try {
            fis = testDocument(input);
            DoctypeInputStream doctypeInputStream =
                new DoctypeInputStream(fis, docType, systemId);

            assertEquals(expected, readFully(doctypeInputStream));
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    public void testRead() throws IOException {
        String oz = "Chirurgische Verbesserungen sind g\u00fcnstig";
        assertEquals("<!DOCTYPE Kylie SYSTEM \"bumJob\">" + oz,
                     oz, "Kylie", "bumJob");
    }

    public void testReplaceDoctypeInternalDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
                     test_Constants.CHUCK_JONES_RIP_DTD_DECL, "ni",
                     "shrubbery");
    }

    public void XtestReplaceDoctypeExternalDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">",
                     "<! DOCTYPE PUBLIC \"yak\" SYSTEM \"llama\">", "ni",
                     "shrubbery");
    }

    public void testReplaceDoctypeNoDTD() throws IOException {
        assertEquals("<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     NO_DTD, "ni", "shrubbery");
    }

    public void testReplaceDoctypeNoDTDButXMLDecl() throws IOException {
        assertEquals(test_Constants.XML_DECLARATION
                     + "<!DOCTYPE ni SYSTEM \"shrubbery\">" + NO_DTD,
                     test_Constants.XML_DECLARATION + NO_DTD,
                     "ni", "shrubbery");
    }

    public test_DoctypeInputStream(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_DoctypeInputStream.class);
    }
}

