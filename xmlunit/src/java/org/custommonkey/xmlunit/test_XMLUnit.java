package org.custommonkey.xmlunit;

import junit.framework.*;

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
            "<test test=\"test\"><test>test<test>test</test></test></test>"
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
            "<test test=\"fail\"><test>test<test>test</test></test></test>"
        };
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

    /**
     * Returns a TestSuite containing this test case.
     */
    public static TestSuite suite(){
        return new TestSuite(test_XMLTestCase.class);
    }
}
