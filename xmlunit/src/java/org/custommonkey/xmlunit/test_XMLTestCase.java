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
        setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
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
            assertEquals("compareXML case " + i + " failed", true,
                compareXML(control[i], control[i]).similar());
            assertEquals("!compareXML case " + i + " failed", false,
                compareXML(control[i], test[i]).similar());
        }
    }

    /**
     * Test the comparision of two files
     */
    public void testCompareFiles() throws Exception {
        assertXMLEqual(new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"),
            new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"));
        assertXMLNotEqual(new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test1.xml"),
            new FileReader(
                test_Constants.BASEDIR + "/tests/etc/test2.xml"));

        try{
            assertXMLNotEqual(new FileReader("nosuchfile.xml"),
                new FileReader("nosuchfile.xml"));
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
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new junit.textui.TestRunner().run(suite());
    }

    /**
     * returns the TestSuite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLTestCase.class);
    }
}
