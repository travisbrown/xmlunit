package org.custommonkey.xmlunit;

import junit.framework.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * Test a Diff
 */
public class test_Diff extends TestCase{
    private final String[] control;
    private final String[] test;
    private Document aDocument;

    public void setUp() throws Exception {
        aDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    /**
     * Test the diff.
     */
    public void testDifferenceFoundElement(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.differenceFound(aDocument.createElement("test"), aDocument.createElement("test"));
        assert(!diff.similar());
    }

    public void testToString(){
        Diff diff = new Diff();
        Element control = aDocument.createElement("test");
        Text controlText = aDocument.createTextNode("Monkey");
        control.appendChild(controlText);
        Element test = aDocument.createElement("test");
        Text testText = aDocument.createTextNode("Chicken");
        test.appendChild(testText);
        diff.differenceFound(control, test);
        assertEquals("org.custommonkey.xmlunit.Diff Expected: <test>Monkey</test>, but was: <test>Chicken</test>", diff.toString());
        diff = new Diff();
        control = aDocument.createElement("test");
        control.appendChild(controlText);
        test = aDocument.createElement("test");
        diff.differenceFound(control, test);
        assertEquals("org.custommonkey.xmlunit.Diff Expected: <test>Monkey</test>, but was: <test/>", diff.toString());
    }

    /**
     * Test the diff.
     */
    public void testDifferenceFoundNullElement(){
        Diff diff = new Diff();
        assert(diff.similar());
        diff.differenceFound(null, aDocument.createElement("test"));
        try{
            assert(diff.toString(), diff.similar());
            fail("Throwable not thrown");
        }catch(AssertionFailedError t){
            assertEquals("org.custommonkey.xmlunit.Diff Expected: null, but was: <test/>", t.getMessage());
        }
        diff = new Diff();
        diff.differenceFound(aDocument.createElement("test"), null);
        try{
            assert(diff.toString(), diff.similar());
            fail("Throwable not thrown");
        }catch(AssertionFailedError t){
            assertEquals("org.custommonkey.xmlunit.Diff Expected: <test/>, but was: null", t.getMessage());
        }
    }


    /**
     * Tests the compare method
     */
    public void testSimilar() throws Exception {
        for(int i=0;i<control.length;i++){
            assert("XMLUnit.compare().similar() test case "+i+" failed",
                new Diff(control[i], control[i]).similar());
            assert("!XMLUnit.compare().similar() test case "+i+" failed",
                !(new Diff(control[i], test[i])).similar());
        }
    }

    /**
     * Tests the compare method
     */
    public void testIdentical() throws Exception {
        String control="<control><test>test1</test><test>test2</test></control>";
        String test="<control><test>test2</test><test>test1</test></control>";

        assert("Documents are identical, when they are not",
            !XMLUnit.compare(control, test).identical());
    }

    /**
     * Tests the compare method
     */
    public void testFiles() throws Exception {
        FileReader control = new FileReader(test_Constants.BASEDIR
            + "/tests/test.blame.html");
        FileReader test = new FileReader(test_Constants.BASEDIR
            + "/tests/test.blame.html");
        Diff diff = XMLUnit.compare(control, test);
        assert(diff.toString(), diff.identical());
    }

    public void testSameTwoStrings() throws Exception {
        Diff diff = new Diff("<same>pass</same>", "<same>pass</same>");
        assert("same should be identical", diff.identical());
        assert("same should be similar", diff.similar());
    }

    public void testMissingElement() throws Exception {
        Diff diff = new Diff("<root></root>", "<root><node/></root>");
        assert("should not be identical", !diff.identical());
        assert("and should not be similar", !diff.similar());
    }

    public void testExtraElement() throws Exception {
        Diff diff = new Diff("<root><node/></root>", "<root></root>");
        assert("should not be identical", !diff.identical());
        assert("and should not be similar", !diff.similar());
    }

    public void testElementsInReverseOrder() throws Exception {
        Diff diff = new Diff("<root><same/><pass/></root>", "<root><pass/><same/></root>");
        assert("should not be identical", !diff.identical());
        assert("but should be similar", diff.similar());
    }

    public void testMissingAttribute() throws Exception {
        Diff diff = new Diff("<same>pass</same>", "<same except=\"this\">pass</same>");
        assert("should not be identical", !diff.identical());
        assert("and should not be similar", !diff.similar());
    }

    public void testExtraAttribute() throws Exception {
        Diff diff = new Diff("<same except=\"this\">pass</same>", "<same>pass</same>");
        assert("should not be identical", !diff.identical());
        assert("and should not be similar", !diff.similar());
    }

    public void testAttributesInReverseOrder() throws Exception {
        Diff diff = new Diff("<same zzz=\"qwerty\" aaa=\"uiop\">pass</same>",
            "<same aaa=\"uiop\" zzz=\"qwerty\">pass</same>" );
        assert("should not ideally be identical but JAXP reorders attributes inside NamedNodeMap", diff.identical());
        assert("but should be similar", diff.similar());
    }

    /**
     * Construct a test
     * @param name Test name
     */
    public test_Diff(String name){
        super(name);

        control = new String[]{
            "<test/>",
            "<test></test>",
            "<test>test</test>",
            "<test test=\"test\">test</test>",
            "<test/>",
            "<test>test</test>",
            "<test test=\"test\"/>",
            "<test><test><test></test></test></test>",
            "<test test=\"test\"><test>test<test>test</test></test></test>",
            "<test test=\"test\"><test>test<test>test</test></test></test>",
            "<html>Yo this is a test!</html>",
            "<java></java>"
        };
        test = new String[]{
            "<fail/>",
            "<fail/>",
            "<fail>test</fail>",
            "<test>test</test>",
            "<fail/>",
            "<test>fail</test>",
            "<test test=\"fail\"/>",
            "<test><test><test>test</test></test></test>",
            "<test test=\"test\"><test>fail<test>test</test></test></test>",
            "<test test=\"fail\"><test>test<test>test</test></test></test>",
            "<html>Yo this isn't a test!</html>",
            "<java><package-def><ident>org</ident><dot/><ident>apache</ident><dot/><ident>test</ident></package-def></java>"

        };
    }

    public void testDiffStringWithAttributes() throws Exception {
        final String fruitBat = "<bat type=\"fruit\"/>",
            longEaredBat = "<bat type=\"longeared\"/>";
        Diff diff = new Diff(fruitBat, longEaredBat);
        assertEquals(diff.getClass().getName()+" Expected: " + fruitBat + ", but was: " + longEaredBat,
            diff.toString());
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
        return new TestSuite(test_Diff.class);
    }
}
