package org.custommonkey.xmlunit;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;

import junit.framework.*;
import junit.textui.TestRunner;

/**
 * JUnit test for NodeInputStream
 */
public class test_NodeInputStream extends TestCase {
    private NodeInputStream nodeStream;
    private final String frog = "";
    private final String ribbit =  "<frog><!-- eats slugs and snails -->"
        + "<frogspawn>fertilised egg</frogspawn>"
        + "<tadpole juvenile=\"true\"/></frog>";

    public void setUp() throws Exception {
        Document document = XMLUnit.buildControlDocument(ribbit);
        nodeStream = new NodeInputStream(document);
    }

    public void testRead() throws Exception {
        InputStreamReader reader = new InputStreamReader(nodeStream);

        Diff diff = new Diff(new StringReader(ribbit), reader);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testAvailable() throws Exception {
        int available = nodeStream.available();
        assertEquals("available="+available, true, available > 0);

        nodeStream.read();
        assertEquals(available - 1, nodeStream.available());
    }

    public test_NodeInputStream(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_NodeInputStream.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

