package org.custommonkey.xmlunit;

import junit.framework.*;
import java.io.*;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
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

    public test_Transform(String name) {
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
    public static TestSuite suite(){
        return new TestSuite(test_Transform.class);
    }
}
