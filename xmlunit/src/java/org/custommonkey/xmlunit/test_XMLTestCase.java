package org.custommonkey.xmlunit;

import junit.framework.*;
import java.io.*;

/**
 * Test case used to test the XMLTestCase
 */
public class test_XMLTestCase extends XMLTestCase{
    private final String[] control;
    private final String[] test;

    /**
     * Construct the test case
     */
    public test_XMLTestCase(String name){
        super(name);

        // Control XML docs
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
            "<test test=\"test\"><test>test<test>test</test></test></test>"
        };
        // Test XML docs
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
            "<test test=\"fail\"><test>test<test>test</test></test></test>"
        };
    }

    public void testSetParsers(){
        setControlParser("org.apache.xerces.parsers.SAXParser");
        setTestParser("org.apache.xerces.parsers.SAXParser");
    }

    public void testIgnoreWhitespace() throws Exception {
        setIgnoreWhitespace(true);
        String test = "<test>   asdc   </test>";
        String control = "<test>asdc</test>";

        assertXMLEqual(control,test);
        setIgnoreWhitespace(false);
        assertXMLNotEqual(control,test);
    }

    /**
     *  Test for the compareXML method.
     */
    public void testCompareXMLStrings() throws Exception {
        for(int i=0;i<control.length;i++){
            assert("compareXML case " + i + " failed",
                compareXML(control[i], control[i]).similar());
            assert("!compareXML case " + i + " failed",
                !compareXML(control[i], test[i]).similar());
        }
    }

    /**
     * Test the comparision of two files
     */
    public void testCompareFiles() throws Exception {
        assertXMLEqual(new FileReader("tests/test1.xml"),
            new FileReader("tests/test1.xml"));
        assertXMLNotEqual(new FileReader("tests/test1.xml"),
            new FileReader("tests/test2.xml"));

        try{
            assertXMLNotEqual(new FileReader("nofile.xml"),
                new FileReader("nofile.xml"));
            fail("Expecting FileNotFoundException");
        }catch(FileNotFoundException e){}
    }

    /**
     *  Test for the assertXMLEquals method.
     */
    public void testXMLEqualsStrings() throws Exception {
        for(int i=0;i<control.length;i++){
            assertXMLEqual("assertXMLEquals test case " + i + " failed",
                control[i], control[i]);
            assertXMLNotEqual("assertXMLNotEquals test case" + i + " failed",
                control[i], test[i]);
        }
    }

    /**
     * returns the TestSuite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLTestCase.class);
    }
}
