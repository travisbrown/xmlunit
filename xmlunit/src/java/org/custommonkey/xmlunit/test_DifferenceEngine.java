package org.custommonkey.xmlunit;

import java.io.File;
import java.io.FileWriter;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

import junit.framework.*;
import junit.textui.TestRunner;

/**
 * JUnit test for DifferenceEngine
 */
public class test_DifferenceEngine extends TestCase implements DifferenceConstants {
    private MockDifferenceListener listener;
    private DifferenceEngine engine;
    private Document document;
    private final static String TEXT_A = "the pack on my back is aching";
    private final static String TEXT_B = "the straps seem to cut me like a knife";
    private final static String COMMENT_A = "Im no clown I wont back down";
    private final static String COMMENT_B = "dont need you to tell me whats going down";
    private final static String[] PROC_A = {"down", "down down"};
    private final static String[] PROC_B = {"dadada", "down"};
    private final static String CDATA_A = "I'm standing alone, you're weighing the gold";
    private final static String CDATA_B = "I'm watching you sinking... Fools Gold";
    private final static String ATTR_A = "These boots were made for walking";
    private final static String ATTR_B = "The marquis de sade never wore no boots like these";

    private void assertDifferentText(Text control, Text test,
    Difference difference) {
        try {
            engine.compareText(control, test, listener);
        } catch (DifferenceEngine.DifferenceFoundException e) {
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(true, listener.different);
        resetListener();
    }

    public void testCompareText() throws Exception {
        String expected = TEXT_A;
        String actual = TEXT_B;
        Text control = document.createTextNode(expected);
        Text test = document.createTextNode(actual);

        assertDifferentText(control, test, TEXT_VALUE);
    }

    private void assertDifferentProcessingInstructions (
    ProcessingInstruction control, ProcessingInstruction test,
    Difference difference) {
        try {
            engine.compareProcessingInstruction(control, test, listener);
        } catch (DifferenceEngine.DifferenceFoundException e) {
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(true, listener.different);
        resetListener();
    }

    public void testCompareProcessingInstruction() throws Exception {
        String[] expected = PROC_A;
        String[] actual = PROC_B;
        ProcessingInstruction control = document.createProcessingInstruction(
            expected[0], expected[1]);
        ProcessingInstruction test = document.createProcessingInstruction(
            actual[0], actual[1]);

        assertDifferentProcessingInstructions(control, test,
            PROCESSING_INSTRUCTION_TARGET);

        ProcessingInstruction control2 = document.createProcessingInstruction(
            expected[0], expected[1]);
        ProcessingInstruction test2 = document.createProcessingInstruction(
            expected[0], actual[1]);
        assertDifferentProcessingInstructions(control2, test2,
            PROCESSING_INSTRUCTION_DATA);
    }

    private void assertDifferentComments(Comment control, Comment test,
    Difference difference) {
        try {
            engine.compareComment(control, test, listener);
        } catch (DifferenceEngine.DifferenceFoundException e) {
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(true, listener.different);
        resetListener();
    }

    public void testCompareComment() throws Exception {
        String expected = COMMENT_A;
        String actual = COMMENT_B;
        Comment control = document.createComment(expected);
        Comment test = document.createComment(actual);

        assertDifferentComments(control, test, COMMENT_VALUE);
    }

    private void assertDifferentCDATA(CDATASection control, CDATASection test,
    Difference difference) {
        try {
            engine.compareCDataSection(control, test, listener);
        } catch (DifferenceEngine.DifferenceFoundException e) {
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(true, listener.different);
        resetListener();
    }

    public void testCompareCDATA() throws Exception {
        String expected = CDATA_A ;
        String actual = CDATA_B ;
        CDATASection control = document.createCDATASection(expected);
        CDATASection test = document.createCDATASection(actual);

        assertDifferentCDATA(control, test, CDATA_VALUE);
    }

    private void assertDifferentDocumentTypes(DocumentType control,
    DocumentType test, Difference difference, boolean fatal) {
        try {
            engine.compareDocumentType(control, test, listener);
            if (fatal) {
                fail("Expected fatal difference!");
            }
        } catch (DifferenceEngine.DifferenceFoundException e) {
            if (!fatal) {
                fail("Expected similarity not fatal difference!");
            }
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(fatal, listener.different);
        resetListener();
    }

    public void testCompareDocumentType() throws Exception {
        File tmpFile = File.createTempFile("Roses","dtd");
        tmpFile.deleteOnExit();
        String tmpDTD = "<!ELEMENT leaf (#PCDATA)><!ELEMENT root (leaf)>";
        new FileWriter(tmpFile).write(tmpDTD);
        String rosesDTD = tmpFile.toURL().toExternalForm();

        File altTmpFile = File.createTempFile("TheCrows", "dtd");
        altTmpFile.deleteOnExit();
        new FileWriter(altTmpFile).write(tmpDTD);
        String theCrowsDTD = altTmpFile.toURL().toExternalForm();

        Document controlDoc = XMLUnit.buildControlDocument(
            "<!DOCTYPE root PUBLIC 'Stone' '" + rosesDTD + "'>"
            + "<root><leaf/></root>");
        Document testDoc = XMLUnit.buildTestDocument(
            "<!DOCTYPE tree PUBLIC 'Stone' '" + rosesDTD + "'>"
            + "<tree><leaf/></tree>");

        DocumentType control = controlDoc.getDoctype();
        DocumentType test = testDoc.getDoctype();

        assertDifferentDocumentTypes(control, test, DOCTYPE_NAME, true);

        test = XMLUnit.buildTestDocument("<!DOCTYPE root PUBLIC 'id' '" + rosesDTD + "'>"
            + "<root><leaf/></root>").getDoctype();
        assertDifferentDocumentTypes(control, test, DOCTYPE_PUBLIC_ID, true);

        test = XMLUnit.buildTestDocument("<!DOCTYPE root SYSTEM '" + rosesDTD + "'>"
            + "<root><leaf/></root>").getDoctype();
        assertDifferentDocumentTypes(control, test, DOCTYPE_PUBLIC_ID, true);

        test = XMLUnit.buildTestDocument("<!DOCTYPE root PUBLIC 'Stone' '" + theCrowsDTD + "'>"
            + "<root><leaf/></root>").getDoctype();
        assertDifferentDocumentTypes(control, test, DOCTYPE_SYSTEM_ID, false);

        test = XMLUnit.buildTestDocument("<!DOCTYPE root SYSTEM '" + theCrowsDTD + "'>"
            + "<root><leaf/></root>").getDoctype();
        assertDifferentDocumentTypes(control, test, DOCTYPE_PUBLIC_ID, true);

        control = XMLUnit.buildTestDocument("<!DOCTYPE root SYSTEM '" + rosesDTD + "'>"
            + "<root><leaf/></root>").getDoctype();
        assertDifferentDocumentTypes(control, test, DOCTYPE_SYSTEM_ID, false);
    }

    private void assertDifferentAttributes(Attr control, Attr test,
    Difference difference, boolean fatal) {
        try {
            engine.compareAttribute(control, test, listener);
            if (fatal) {
                fail("Expecting fatal difference!");
            }
        } catch (DifferenceEngine.DifferenceFoundException e) {
            if (!fatal) {
                fail("Expecting similarity not fatal difference!");
            }
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(fatal, listener.different);
        resetListener();
    }

    public void testCompareAttribute() throws Exception {
        String expected = ATTR_A;
        String actual = ATTR_B;
        Attr control = document.createAttribute(getName());
        control.setValue(expected);
        Attr test = document.createAttribute(getName());
        test.setValue(actual);

        assertDifferentAttributes(control, test, ATTR_VALUE, true);

        String doctypeDeclaration = "<!DOCTYPE manchester [" +
            "<!ELEMENT sound EMPTY><!ATTLIST sound sorted (true|false) \"true\">" +
            "<!ELEMENT manchester (sound)>]>";
        Document controlDoc = XMLUnit.buildControlDocument(doctypeDeclaration +
            "<manchester><sound sorted=\"true\"/></manchester>");
        control = (Attr) controlDoc.getDocumentElement().getFirstChild()
            .getAttributes().getNamedItem("sorted");

        Document testDoc = XMLUnit.buildTestDocument(doctypeDeclaration +
            "<manchester><sound/></manchester>");
        test = (Attr) testDoc.getDocumentElement().getFirstChild()
            .getAttributes().getNamedItem("sorted");

        assertDifferentAttributes(control, test,
            ATTR_VALUE_EXPLICITLY_SPECIFIED, false);
    }

    private void assertDifferentElements(Element control, Element test,
    Difference difference) {
        try {
            engine.compareElement(control, test, listener);
        } catch (DifferenceEngine.DifferenceFoundException e) {
        }
        assertEquals(difference.getId(), listener.comparingWhat);
        assertEquals(true, listener.different);
        resetListener();
    }

    public void testCompareElements() throws Exception {
        Document document = XMLUnit.buildControlDocument(
            "<down><im standing=\"alone\"/><im watching=\"you\" all=\"\"/>"
            + "<im watching=\"you all\"/><im watching=\"you sinking\"/></down>");
        Element control = (Element) document.getDocumentElement();
        Element test = (Element) control.getFirstChild();

        assertDifferentElements(control, test, ELEMENT_TAG_NAME);

        // compare im#1 to im#2
        control = test;
        test = (Element) control.getNextSibling();
        assertDifferentElements(control, test, ELEMENT_NUM_ATTRIBUTES);

        // compare im#1 to im#3
        test = (Element) test.getNextSibling();
        assertDifferentElements(control, test, ATTR_NAME_NOT_FOUND);

        // compare im#3 to im#4
        control = test;
        test = (Element) control.getNextSibling();
        assertDifferentElements(control, test, ATTR_VALUE);
    }

    public void testCompareNode() throws Exception {
        Document controlDocument = XMLUnit.buildControlDocument("<root>"
            + "<!-- " + COMMENT_A + " -->"
            + "<?" + PROC_A[0] + " "+ PROC_A[1] + " ?>"
            + "<elem attr=\"" + ATTR_A + "\">" + TEXT_A + "</elem></root>");
        Document testDocument = XMLUnit.buildTestDocument("<root>"
            + "<!-- " + COMMENT_B + " -->"
            + "<?" + PROC_B[0] + " "+ PROC_B[1] + " ?>"
            + "<elem attr=\"" + ATTR_B + "\">" + TEXT_B + "</elem></root>");

        engine.compare(controlDocument, testDocument, listener);
        assertEquals(true, listener.nodesSkipped);

        Node control = controlDocument.getDocumentElement().getFirstChild();
        Node test = testDocument.getDocumentElement().getFirstChild();

        do {
            resetListener();
            engine.compare(control, test, listener);
            assertEquals(true, -1 != listener.comparingWhat);
            assertEquals(false, listener.nodesSkipped);

            resetListener();
            engine.compare(control, control, listener);
            assertEquals(-1, listener.comparingWhat);

            control = control.getNextSibling();
            test = test.getNextSibling();
        } while (control != null);
    }

    private void assertDifferentNamespaceDetails(Node control, Node test,
    Difference expectedDifference, boolean fatal) {
        try {
            engine.compareNodeBasics(control, test, listener);
            if (fatal) {
                fail("Expected fatal difference");
            }
        } catch (DifferenceEngine.DifferenceFoundException e) {
            if (!fatal) {
                fail("Not expecting fatal difference!");
            }
        }
        assertEquals(expectedDifference.getId(), listener.comparingWhat);
        assertEquals(fatal, listener.different);
        resetListener();
    }

    public void testCompareNodeBasics() throws Exception {
        String namespaceA = "http://example.org/StoneRoses";
        String namespaceB = "http://example.org/Stone/Roses";
        String prefixA = "music";
        String prefixB = "cd";
        String elemName = "nowPlaying";
        Element control = document.createElementNS(namespaceA,
            prefixA + ':' + elemName);
        engine.compareNodeBasics(control, control, listener);

        Element test = document.createElementNS(namespaceB,
            prefixA + ':' + elemName);
        assertDifferentNamespaceDetails(control, test, NAMESPACE_URI,
            true);

        test = document.createElementNS(namespaceA,
            prefixB + ':' + elemName);
        assertDifferentNamespaceDetails(control, test,
            NAMESPACE_PREFIX, false);
    }

    private void assertDifferentChildren(Node control, Node test,
    Difference expectedDifference, boolean fatal) {
        try {
            engine.compareNodeChildren(control, test, listener);
            if (fatal) {
                fail("Expected fatal difference");
            }
        } catch (DifferenceEngine.DifferenceFoundException e) {
            if (!fatal) {
                fail("Not expecting fatal difference " +
                    listener.comparingWhat
                    + ": expected " + listener.expected
                    + " but was " + listener.actual);
            }
        }
        assertEquals(expectedDifference==null ? -1 : expectedDifference.getId(),
            listener.comparingWhat);
        assertEquals(fatal, listener.different);
        resetListener();
    }

    public void testCompareNodeChildren() throws Exception {
        document = XMLUnit.buildControlDocument(
            "<down><im standing=\"alone\"/><im><watching/>you all</im>"
            + "<im watching=\"you\">sinking</im></down>");
        // compare im #1 to itself
        Node control = document.getDocumentElement().getFirstChild();
        Node test = control;
        assertDifferentChildren(control, control, null, false);

        // compare im #1 to im #2
        test = control.getNextSibling();
        assertDifferentChildren(control, test, HAS_CHILD_NODES, true);

        // compare im #2 to im #3
        control = test;
        test = control.getNextSibling();
        assertDifferentChildren(control, test, CHILD_NODELIST_LENGTH,
            true);
    }

    private void assertDifferentNodeLists(Node control, Node test,
    Difference expectedDifference, boolean fatal) {
        try {
            engine.compareNodeList(control.getChildNodes(), test.getChildNodes(),
                control.getChildNodes().getLength(), listener);
            if (fatal) {
                fail("Expected fatal difference");
            }
        } catch (DifferenceEngine.DifferenceFoundException e) {
            if (!fatal) {
                fail("Not expecting fatal difference!");
            }
        }
        assertEquals(expectedDifference==null ? -1 : expectedDifference.getId(),
            listener.comparingWhat);
        assertEquals(fatal, listener.different);
        resetListener();
    }

    public void testCompareNodeList() throws Exception {
        document = XMLUnit.buildControlDocument(
            "<down><im><standing/>alone</im><im><watching/>you all</im>"
            + "<im><watching/>you sinking</im></down>");
        // compare im #1 to itself
        Node control = document.getDocumentElement().getFirstChild();
        Node test = control;
        assertDifferentNodeLists(control, test, null, false);

        // compare im #1 to im #2
        test = control.getNextSibling();
        assertDifferentChildren(control, test, ELEMENT_TAG_NAME, true);

        // compare im #2 to im #3
        control = test;
        test = control.getNextSibling();
        assertDifferentChildren(control, test, TEXT_VALUE, true);
    }

    public void testCompareNodeListElements() throws Exception {
        Element control = document.createElement("root");
        control.appendChild(document.createElement("leafElemA"));
        control.appendChild(document.createElement("leafElemB"));

        Element test = document.createElement("root");
        test.appendChild(document.createElement("leafElemB"));
        test.appendChild(document.createElement("leafElemA"));

        assertDifferentChildren(control, test, CHILD_NODELIST_SEQUENCE, false);
        assertDifferentChildren(test, control, CHILD_NODELIST_SEQUENCE, false);
    }

    public void testCompareNodeListMixedContent() throws Exception {
        Element control = document.createElement("root");
        control.appendChild(document.createTextNode("text leaf"));
        control.appendChild(document.createElement("leafElem"));

        Element test = document.createElement("root");
        test.appendChild(document.createElement("leafElem"));
        test.appendChild(document.createTextNode("text leaf"));

        assertDifferentChildren(control, test, CHILD_NODELIST_SEQUENCE, false);
        assertDifferentChildren(test, control, CHILD_NODELIST_SEQUENCE, false);
    }

    public void testBasicCompare() throws Exception {
        try {
            engine.compare("black", "white", null, null, listener,
                ATTR_NAME_NOT_FOUND);
            fail("Expected difference found exception");
        } catch (DifferenceEngine.DifferenceFoundException e) {
            assertEquals(true, listener.different);
            assertEquals(ATTR_NAME_NOT_FOUND.getId(), listener.comparingWhat);
        }
        resetListener();

        try {
            engine.compare("black", "white", null, null, listener,
                NAMESPACE_PREFIX);
            assertEquals(false, listener.different);
            assertEquals(NAMESPACE_PREFIX.getId(), listener.comparingWhat);
        } catch (Exception e) {
            fail("Not expecting difference found exception");
        }
    }

    private void resetListener() {
        listener = new MockDifferenceListener();
    }

    public void setUp() throws Exception {
        resetListener();
        engine = new DifferenceEngine();
        DocumentBuilder documentBuilder = XMLUnit.getControlParser();
        document = documentBuilder.newDocument();
    }

    public test_DifferenceEngine(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_DifferenceEngine.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
//        new TestRunner().run(new test_DifferenceEngine("testCompareNodeListElements"));
    }

    private class MockDifferenceListener implements DifferenceListener {
        public String expected;
        public String actual;
        public Node control;
        public Node test;
        public int comparingWhat = -1;
        public boolean different = false;
        public boolean nodesSkipped = false;
        public void differenceFound(String expected, String actual,
            Node control, Node test, Difference difference) {
                this.expected = expected;
                this.actual = actual;
                this.control = control;
                this.test = test;
                this.comparingWhat = difference.getId();
                this.different = !difference.isRecoverable();
        }
        public void skippedComparison(Node control, Node test) {
            nodesSkipped = true;
        }
    }
}

