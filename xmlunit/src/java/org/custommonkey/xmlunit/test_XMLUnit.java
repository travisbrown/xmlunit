package org.custommonkey.xmlunit;

import junit.framework.*;
import java.io.*;
import javax.xml.parsers.*;

/** 
 * Test case for XMLUnit
 */
public class test_XMLUnit extends TestCase{
    private final String[] control;
    private final String[] test;

    /**
     * Contructs a new test case.
     */
    public test_XMLUnit(String name){
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

    /**
     * Test overiding the SAX parser used to parse control documents
     */
    public void testSetControlBuilder(){
        XMLUnit.setControlParser("org.apache.xerces.parsers.SAXParser");
    }

    public void testIgnoreWhitespace() throws Exception {
        XMLUnit.setIgnoreWhitespace(true);
        String test="<test>  monkey   </test>";
        String control="<test>monkey</test>";
        assert("Should be similar", XMLUnit.compare(control, test).similar());
        XMLUnit.setIgnoreWhitespace(false);
        assert("Should be different", !XMLUnit.compare(control, test).similar());
    }
    /**
     * Test overiding the SAX parser used to parse test documents
     */
    public void testSetTestBuilder(){
        XMLUnit.setTestParser("org.apache.xerces.parsers.SAXParser");
    }
    /**
     * Tests the compare method of the XMLUnit class
     */
    public void testSimilar() throws Exception {
        for(int i=0;i<control.length;i++){
            assert("XMLUnit.compare().similar() test case "+i+" failed",
                XMLUnit.compare(control[i], control[i]).similar());
            assert("!XMLUnit.compare().similar() test case "+i+" failed",
                !XMLUnit.compare(control[i], test[i]).similar());
        }
    }

    /**
     * 
     */
    public void testIdentical() throws Exception {
        String control="<control><test>test1</test><test>test2</test></control>";
        String test="<control><test>test2</test><test>test1</test></control>";

        assert("Documents are identical, when they are not",
            !XMLUnit.compare(control, test).identical());
    }

    public void testFiles() throws Exception {
        FileReader control = new FileReader("tests/test.blame.html");
        FileReader test = new FileReader("tests/test.blame.html");
        Diff diff = XMLUnit.compare(control, test);
        assert(diff.toString(),
            diff.identical());
    }

    /**
     * Returns a TestSuite containing this test case.
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLUnit.class);
    }
}
