package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;

/**
 * JUnit test for Validator
 */
public class test_Validator extends TestCase {
    private Validator validator;
    private File tempDTDFile;

    public void testIsValidGood() throws Exception {
        String toonXML = test_Constants.XML_DECLARATION
             + test_Constants.CHUCK_JONES_RIP_DTD_DECL
             + test_Constants.CHUCK_JONES_RIP_XML;
        validator = new Validator(new StringReader(toonXML));
        assertEquals("toonXML " + validator.toString(),
            true, validator.isValid());

        String noXMLDeclaration = test_Constants.CHUCK_JONES_RIP_DTD_DECL
             + test_Constants.CHUCK_JONES_RIP_XML;
        validator = new Validator(new StringReader(noXMLDeclaration));
        assertEquals("noXMLDeclaration " + validator.toString(),
            true, validator.isValid());
    }

    public void testIsValidExternalSystemId() throws Exception {
        writeTempDTDFile();
        assertEquals(tempDTDFile.getAbsolutePath(), true, tempDTDFile.exists());

        String externalDTD = test_Constants.XML_DECLARATION
             + test_Constants.DOCUMENT_WITH_GOOD_EXTERNAL_DTD;
        validator = new Validator(new StringReader(externalDTD),
            tempDTDFile.toURL().toExternalForm());

        assertEquals("externalDTD " + validator.toString(),
            true, validator.isValid());

        String noDTD = test_Constants.XML_DECLARATION
             + test_Constants.DOCUMENT_WITH_NO_EXTERNAL_DTD;
        validator = new Validator(new StringReader(noDTD),
            tempDTDFile.toURL().toExternalForm());

        assertEquals("noDTD " + validator.toString(),
            false, validator.isValid());
    }

    public void testIsValidNoDTD() throws Exception {
        writeTempDTDFile();
        assertEquals(tempDTDFile.getAbsolutePath(), true, tempDTDFile.exists());

        String noDTD = test_Constants.CHUCK_JONES_RIP_XML;
        validator = new Validator(new StringReader(noDTD),
            tempDTDFile.toURL().toExternalForm(), "cartoons");
        assertEquals(validator.toString(), true, validator.isValid());

        validator = new Validator(new StringReader(noDTD),
            tempDTDFile.toURL().toExternalForm(), "anima");
        assertEquals(validator.toString(), false, validator.isValid());
    }

    public void testIsValidBad() throws Exception {
        String noDTD = test_Constants.XML_DECLARATION
             + test_Constants.CHUCK_JONES_RIP_XML;
        validator = new Validator(new StringReader(noDTD));
        assertEquals("noDTD " + validator.toString(),
            false, validator.isValid());

        String dtdTwice = test_Constants.XML_DECLARATION
             + test_Constants.CHUCK_JONES_RIP_DTD_DECL
             + test_Constants.CHUCK_JONES_RIP_DTD_DECL
             + test_Constants.CHUCK_JONES_RIP_XML;
        validator = new Validator(new StringReader(dtdTwice));
        assertEquals("dtdTwice " + validator.toString(),
            false, validator.isValid());

        String invalidXML = test_Constants.XML_DECLARATION
             + test_Constants.CHUCK_JONES_RIP_DTD_DECL
             + test_Constants.CHUCK_JONES_SPINNING_IN_HIS_GRAVE_XML;
        validator = new Validator(new StringReader(invalidXML));
        assertEquals("invalidXML " + validator.toString(),
            false, validator.isValid());
    }

    private void removeTempDTDFile() throws Exception {
        if (tempDTDFile.exists()) {
            tempDTDFile.delete();
        }
    }

    private void writeTempDTDFile() throws Exception {
        FileWriter writer = new FileWriter(tempDTDFile);
        writer.write(test_Constants.CHUCK_JONES_RIP_DTD);
        writer.close();
    }

    public void setUp() throws Exception {
        tempDTDFile = new File(test_Constants.EXTERNAL_DTD);
        removeTempDTDFile();
    }

    public void tearDown() throws Exception {
        removeTempDTDFile();
    }

    public test_Validator(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_Validator.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

