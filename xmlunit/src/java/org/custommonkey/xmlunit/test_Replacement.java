package org.custommonkey.xmlunit;


import junit.framework.*;
import junit.textui.TestRunner;


/**
 * JUnit test for Replacement
 */
public class test_Replacement extends TestCase {
    private Replacement replacement;

    public void testCharReplacement() {
        char[] ch = {'h','o','a','g'};
        replacement = new Replacement(new char[] {'o','a'}, new char[] {'u'});
        assertEquals("hug", new String(replacement.replace(ch)));

        replacement = new Replacement(new char[] {'g'}, new char[] {'r', 's', 'e'});
        assertEquals("hoarse", new String(replacement.replace(ch)));
    }

    public void testSimpleString() {
        replacement = new Replacement("x", "y");
        // 1st char
        assertEquals("yen", replacement.replace("xen"));
        // last char
        assertEquals("boy", replacement.replace("box"));
        // not 1st or last
        assertEquals("aye", replacement.replace("axe"));
        // no replacement
        assertEquals("bag", replacement.replace("bag"));
        // multiple replacements
        assertEquals("yoyo", replacement.replace("xoxo"));
        // multiple concurrent replacements
        assertEquals("yyjykyy", replacement.replace("xxjxkxx"));
    }

    public void testComplexString() {
        replacement = new Replacement(" a whole bunch of words",
            "some other words altogether");
        assertEquals("Here aresome other words altogether...",
            replacement.replace("Here are a whole bunch of words..."));
    }

    public test_Replacement(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_Replacement.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

