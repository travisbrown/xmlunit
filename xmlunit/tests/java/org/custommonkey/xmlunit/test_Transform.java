package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.File;
import java.io.FileReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 * Test a Transform
 */
public class test_Transform extends TestCase{
    private static final String FLEABALL = "<fleaball><animal><shaggy>dog</shaggy></animal></fleaball>";

    private static final String DOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + test_Constants.LINE_SEPARATOR + "<dog/>" ;

    private Transform transform;
    private File animal;

    public void testGetResultString() throws Exception {
        transform = new Transform(FLEABALL, animal);
        assertEquals(DOG, transform.getResultString());
    }

    public void testGetResultDocument() throws Exception {
        transform = new Transform(FLEABALL, animal);
        Diff diff = new Diff(DOG, transform);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testIdentityTransform() throws Exception {
        Document control = XMLUnit.buildControlDocument(FLEABALL);
        transform = new Transform(control);
        Document test = transform.getResultDocument();
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testOutputProperty() throws Exception {
        transform = new Transform(FLEABALL, animal);
        transform.setOutputProperty(OutputKeys.METHOD, "html");
        assertNotEquals(DOG, transform.getResultString());
    }

    public void testDOMSourceAndFile() throws Exception {
        transform = new Transform(XMLUnit.buildControlDocument(FLEABALL), animal);
        assertEquals(DOG, transform.getResultString());
    }

    public void testDOMSourceAndString() throws Exception {
        FileReader reader = new FileReader(animal);
        try {
            char[] animalXSL = new char[1024];
            int length = reader.read(animalXSL);
            transform = new Transform(XMLUnit.buildControlDocument(FLEABALL),
                new String(animalXSL, 0, length));
            assertEquals(DOG, transform.getResultString());
        } finally {
            reader.close();
        }
    }

    /**
     * Raised by Craig Strong 04.04.2002
     */
    public void testXSLIncludeWithoutSystemId() throws Exception {
        String input = "<bug><animal>creepycrawly</animal></bug>";
        String xslWithInclude = test_Constants.XML_DECLARATION
            + test_Constants.XSLT_START
            + test_Constants.XSLT_XML_OUTPUT_NOINDENT
            + "<xsl:template match=\"bug\"><xsl:apply-templates select=\"animal\"/></xsl:template>"
            + "<xsl:include href=\"" + test_Constants.BASEDIR + "/tests/etc/animal.xsl\"/>"
            + test_Constants.XSLT_END;
        Transform transform = new Transform(input, xslWithInclude);
        transform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        assertEquals("<creepycrawly/>", transform.getResultString());
    }

    private void assertNotEquals(Object expected, Object actual) {
        if (expected.equals(actual)) {
            fail("Expected " + expected + " different to actual!");
        }
    }

    public test_Transform(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        animal = new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl");
    }

    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new TestRunner().run(suite());
    }

    /**
     * Return the test suite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_Transform.class);
    }
}

