package org.custommonkey.xmlunit;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.SAXException;

/**
 * Compares and describes the differences between XML documents.
 * Two documents are either:
 * <br /><ul>
 * <li><i>identical</i>: the content and sequence of the nodes in the documents
 *  are exactly the same.</li>
 * <li><i>similar</i>: the content of the nodes in the documents are the same,
 *  but the sequencing of sibling elements, values of namespace prefixes,
 *  use of implied attributes or other minor differences may exist.</li>
 * <li><i>different</i>: the contents of the documents are fundamentally
 *  different</li>
 * </ul>
 * <br />The differences between compared documents are contained in a
 *  message buffer held in this class, accessible either through the
 *  <code>appendMessage</code> or <code>toString</code> methods.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Diff implements DifferenceListener, DifferenceConstants {
    private final Document controlDoc;
    private final Document testDoc;
    private boolean similar = true;
    private boolean identical = true;
    private boolean compared = false;
    private StringBuffer messages;
    private DifferenceEngine differenceEngine;

    /**
     * Construct a Diff that compares the XML in two Strings
     */
    public Diff(String control, String test) throws SAXException, IOException,
    ParserConfigurationException {
        this(new StringReader(control), new StringReader(test));
    }

    /**
     * Construct a Diff that compares the XML read from two Readers
     */
    public Diff(Reader control, Reader test) throws SAXException, IOException,
    ParserConfigurationException {
        this(XMLUnit.buildDocument(XMLUnit.getControlParser(), control),
            XMLUnit.buildDocument(XMLUnit.getTestParser(), test));
    }

    /**
     * Construct a Diff that compares the XML in two Documents
     */
    public Diff(Document controlDoc, Document testDoc) {
        this(controlDoc, testDoc, new DifferenceEngine());
    }

    /**
     * Construct a Diff that compares the XML in two Documents using a specific
     * DifferenceEngine
     */
    public Diff(Document controlDoc, Document testDoc,
    DifferenceEngine comparator) {
        this.controlDoc = controlDoc;
        this.testDoc = testDoc;
        this.differenceEngine = comparator;
        this.messages = new StringBuffer();
    }

    /**
     * Construct a Diff that compares the XML in a control Document against the
     * result of a transformation
     */
    public Diff(String control, Transform testTransform) throws IOException,
    TransformerException, ParserConfigurationException, SAXException {
        this(XMLUnit.buildControlDocument(control),
            testTransform.getResultDocument());
    }

    /**
     * Top of the recursive comparison code tree
     */
    private void compare() {
        if (compared) {
            return;
        }
        differenceEngine.compare(controlDoc.getDocumentElement(),
            testDoc.getDocumentElement(), this);
        compared = true;
    }

    /**
     * Return the result of a comparison. Two documents are considered
     * to be "similar" if they contain the same elements and attributes
     * regardless of order.
     */
    public boolean similar(){
        compare();
        return similar;
    }

    /**
     * Return the result of a comparison. Two documents are considered
     * to be "identical" if they contain the same elements and attributes
     * in the same order.
     */
    public boolean identical(){
        compare();
        return identical;
    }

    /**
     * Append Node comparison details to message buffer
     * @param buf
     * @param control
     * @param test
     */
    private void appendComparingWhat(StringBuffer buf, Node control, Node test) {
        buf.append(": comparing ");
        appendNodeDetail(buf, control, true);
        buf.append(" to ");
        appendNodeDetail(buf, test, true);
    }

    /**
     * Convert a Node into a simple String representation and append to message
     *  buffer
     * @param buf
     * @param aNode
     * @param notRecursing
     */
    private void appendNodeDetail(StringBuffer buf, Node aNode,
    boolean notRecursing) {
        if (aNode==null) {
            return;
        }
        if (notRecursing) {
            buf.append('<');
        }
        switch (aNode.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
                appendNodeDetail(buf,
                    ((Attr)aNode).getOwnerElement(), false);
                buf.append(' ')
                    .append(aNode.getNodeName()).append("=\"")
                    .append(aNode.getNodeValue()).append("\"...");
                break;
            case Node.ELEMENT_NODE:
                    buf.append(aNode.getNodeName());
                    if (notRecursing) {
                        buf.append("...");
                    }
                break;
            case Node.TEXT_NODE:
                appendNodeDetail(buf, aNode.getParentNode(), false);
                buf.append(" ...>")
                    .append(aNode.getNodeValue()).append("</");
                appendNodeDetail(buf, aNode.getParentNode(), false);
                break;
            case Node.CDATA_SECTION_NODE:
                buf.append("![CDATA[").append(aNode.getNodeValue())
                    .append("]]");
                break;
            case Node.COMMENT_NODE:
                buf.append("!--").append(aNode.getNodeValue())
                    .append("--");
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                ProcessingInstruction instr = (ProcessingInstruction) aNode;
                buf.append('?').append(instr.getTarget())
                    .append(' ').append(instr.getData())
                    .append('?');
                break;
            case Node.DOCUMENT_TYPE_NODE:
                DocumentType type = (DocumentType) aNode;
                buf.append("!DOCTYPE ").append(type.getName());
                if (type.getPublicId()!=null
                && type.getPublicId().length() > 0) {
                    buf.append(" PUBLIC \"").append(type.getPublicId())
                        .append('"');
                }
                if (type.getSystemId()!=null
                && type.getSystemId().length() > 0) {
                    buf.append(" SYSTEM \"").append(type.getSystemId())
                        .append('"');
                }
                break;
            default:
                buf.append("!--NodeType ").append(aNode.getNodeType())
                    .append(' ').append(aNode.getNodeName())
                    .append('/').append(aNode.getNodeValue())
                    .append("--");

        }
        if (notRecursing) {
            buf.append('>');
        }
    }

    /**
     * Append a meaningful message to the buffer of messages
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param difference
     */
    private void appendDifference(String expected, String actual, Node control,
    Node test, Difference difference) {
        messages.append("\n Expected ")
            .append(difference.getDescription())
            .append(" ").append(expected)
            .append(" but was ").append(actual);
        appendComparingWhat(messages, control, test);
    }

    /**
     * DifferenceListener implementation.
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param comparingWhat
     */
    public final void differenceFound(String expected, String actual,
    Node control, Node test, Difference difference) {
        identical = false;
        if (difference.isRecoverable()) {
            messages.append("[similar]");
        } else {
            similar = false;
            messages.append("[different]");
        }
        appendDifference(expected, actual, control, test, difference);
    }

    /**
     * DifferenceListener implementation.
     * @param control
     * @param test
     */
    public final void skippedComparison(Node control, Node test) {
        System.err.println("DifferenceListener.skippedComparison: "
            + "unhandled control node type=" + control.getNodeType()
            + ", unhandled test node type=" + test.getNodeType());
    }

    /**
     * Append the message from the result of this Diff instance to a specified
     *  StringBuffer
     * @param toAppendTo
     * @return specified StringBuffer with message appended
     */
    public StringBuffer appendMessage(StringBuffer toAppendTo) {
        compare();
        if (messages.length()==0) {
            messages.append("[identical]");
        }
        return toAppendTo.append(messages);
    }

    /**
     * Get the result of this Diff instance as a String
     * @return result of this Diff
     */
    public String toString(){
        StringBuffer buf = new StringBuffer(getClass().getName());
        appendMessage(buf);
        return buf.toString();
    }

}

