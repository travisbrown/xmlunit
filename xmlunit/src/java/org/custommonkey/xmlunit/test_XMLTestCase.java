package org.custommonkey.xmlunit;

import junit.framework.*;

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
