package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.File;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

/**
 * Test a Transform
 */
public class test_Transform extends TestCase{
    private static final String FLEABALL = "<fleaball><animal><shaggy>dog</shaggy></animal></fleaball>";

    private static final String DOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + test_Constants.LINE_SEPARATOR + "<dog/>" ;

    private Transform transform;

    public void testGetResultString() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        assertEquals(DOG, transform.getResultString());
    }

    public void testGetResultDocument() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        Diff diff = new Diff(DOG, transform);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testIdentityTransform() throws Exception {
        Document control = XMLUnit.buildControlDocument(FLEABALL);
        DOMSource source = new DOMSource(control);
        transform = new Transform(source);
        Document test = transform.getResultDocument();
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testOutputProperty() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        transform.setOutputProperty(OutputKeys.METHOD, "html");
        assertNotEquals(DOG, transform.getResultString());
    }

    public void assertNotEquals(Object expected, Object actual) {
        if (expected.equals(actual)) {
            fail("Expected " + expected + " different to actual!");
        }
    }

    public test_Transform(String name) {
        super(name);
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
