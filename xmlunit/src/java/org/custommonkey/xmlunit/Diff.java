/*
******************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the following
      disclaimer in the documentation and/or other materials provided
      with the distribution.
    * Neither the name of the xmlunit.sourceforge.net nor the names
      of its contributors may be used to endorse or promote products
      derived from this software without specific prior written
      permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
*/

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
 * Compares and describes any difference between XML documents.
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
 * <br />
 *  The difference between compared documents is contained in a
 *  message buffer held in this class, accessible either through the
 *  <code>appendMessage</code> or <code>toString</code> methods.
 *  NB: When comparing documents, the comparison is halted as soon as the
 *  status (identical / similar / different) is known with certainty. For a
 *  list of all differences between the documents an instance of
 *  (@link DetailedDiff the DetailedDiff class} can be used instead.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Diff implements DifferenceListener, DifferenceConstants {
    private final Document controlDoc;
    private final Document testDoc;
    private boolean similar = true;
    private boolean identical = true;
    private boolean compared = false;
    private boolean haltComparison = false;
    private StringBuffer messages;
    private DifferenceEngine differenceEngine;
    private DifferenceListener differenceListenerDelegate;

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
     * Construct a Diff that compares the XML in a control Document against the
     * result of a transformation
     */
    public Diff(String control, Transform testTransform) throws IOException,
    TransformerException, ParserConfigurationException, SAXException {
        this(XMLUnit.buildControlDocument(control),
            testTransform.getResultDocument());
    }

    /**
     * Construct a Diff that compares the XML in two Documents using a specific
     * DifferenceEngine
     */
    public Diff(Document controlDoc, Document testDoc,
    DifferenceEngine comparator) {
        this.controlDoc = getWhitespaceManipulatedDocument(controlDoc);
        this.testDoc = getWhitespaceManipulatedDocument(testDoc);
        this.differenceEngine = comparator;
        this.messages = new StringBuffer();
    }

    /**
     * Construct a Diff from a prototypical instance.
     * Used by extension subclasses
     * @param prototype a prototypical instance
     */
    protected Diff(Diff prototype) {
        this.controlDoc = prototype.controlDoc;
        this.testDoc = prototype.testDoc;
        this.differenceEngine = prototype.differenceEngine;
        this.messages = new StringBuffer();
    }

    /**
     * If {@link XMLUnit#getIgnoreWhitespace whitespace is ignored} in
     *  differences then manipulate the content to strip the redundant whitespace
     * @param originalDoc a document making up one half of this difference
     * @return the original document with redundant whitespace removed if
     *  differences ignore whitespace
     */
    private Document getWhitespaceManipulatedDocument(Document originalDoc) {
        if (!XMLUnit.getIgnoreWhitespace()) {
            return originalDoc;
        }
        try {
            Transform whitespaceStripper = XMLUnit.getStripWhitespaceTransform(
                originalDoc);
            return whitespaceStripper.getResultDocument();
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessageAndLocation() + "\n" + e.getCause());
        }
    }

    /**
     * Top of the recursive comparison execution tree
     */
    protected final void compare() {
        if (compared) {
            return;
        }
        differenceEngine.compare(controlDoc,
            testDoc, this);
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
        buf.append(" - comparing ");
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
            buf.append(XMLConstants.OPEN_START_NODE);
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
                buf.append(" ...").append(XMLConstants.CLOSE_NODE)
                    .append(aNode.getNodeValue())
                    .append(XMLConstants.OPEN_END_NODE);
                appendNodeDetail(buf, aNode.getParentNode(), false);
                break;
            case Node.CDATA_SECTION_NODE:
                buf.append(XMLConstants.START_CDATA)
                    .append(aNode.getNodeValue())
                    .append(XMLConstants.END_CDATA);
                break;
            case Node.COMMENT_NODE:
                buf.append(XMLConstants.START_COMMENT)
                    .append(aNode.getNodeValue())
                    .append(XMLConstants.END_COMMENT);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                ProcessingInstruction instr = (ProcessingInstruction) aNode;
                buf.append(XMLConstants.START_PROCESSING_INSTRUCTION)
                    .append(instr.getTarget())
                    .append(' ').append(instr.getData())
                    .append(XMLConstants.END_CDATA);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                DocumentType type = (DocumentType) aNode;
                buf.append(XMLConstants.START_DOCTYPE).append(type.getName());
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
            case Node.DOCUMENT_NODE:
                buf.append("Document Node ")
                .append(XMLConstants.OPEN_START_NODE)
                .append("...")
                .append(XMLConstants.CLOSE_NODE);
                break;
            default:
                buf.append("!--NodeType ").append(aNode.getNodeType())
                    .append(' ').append(aNode.getNodeName())
                    .append('/').append(aNode.getNodeValue())
                    .append("--");

        }
        if (notRecursing) {
            buf.append(XMLConstants.CLOSE_NODE);
        }
    }

    /**
     * Append a meaningful message to the buffer of messages
     * @param appendTo the messages buffer
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param difference
     */
    private void appendDifference(StringBuffer appendTo, 
    String expected, String actual, Node control,
    Node test, Difference difference) {
        appendTo.append(" Expected ")
            .append(difference.getDescription())
            .append(" '").append(expected)
            .append("' but was '").append(actual).append("'");
        appendComparingWhat(appendTo, control, test);
    }

    /**
     * DifferenceListener implementation.
     * If the {@link Diff#overrideDifferenceListener overrideDifferenceListener} 
     * method has been called then the interpretation of the difference
     * will be delegated.
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param comparingWhat
     * @return a DifferenceListener.RETURN_... constant indicating how the
     *    difference was interpreted. 
     * Always RETURN_ACCEPT_DIFFERENCE if the call is not delegated.
     */
    public int differenceFound(String expected, String actual,
    Node control, Node test, Difference difference) {
        int returnValue = RETURN_ACCEPT_DIFFERENCE;    
        if (differenceListenerDelegate != null) {
            returnValue = differenceListenerDelegate.differenceFound(
                expected, actual, control, test, difference);
        } 

        switch (returnValue) {
            case RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL:
                return returnValue;
            case RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR:
                identical = false;
                haltComparison = false;
                break;
            case RETURN_ACCEPT_DIFFERENCE:
                identical = false;
                if (difference.isRecoverable()) {
                    haltComparison = false;
                } else {
                    similar = false;
                    haltComparison = true;
                }
                break;
            default:
                throw new IllegalArgumentException(returnValue
                    + " is not a defined DifferenceListener.RETURN_... value");
        }
        if (haltComparison) {
            messages.append("\n[different]");
        } else {
            messages.append("\n[dissimilar]");
        }
        appendDifference(messages, expected, actual, 
            control, test, difference);
        return returnValue;
    }

    /**
     * DifferenceListener implementation.
     * If the {@link Diff#overrideDifferenceListener  overrideDifferenceListener} 
     * method has been called then the call will be delegated 
     * otherwise a message is printed to <code>System.err</code>.
     * @param control
     * @param test
     */
    public void skippedComparison(Node control, Node test) {
        if (differenceListenerDelegate != null) {
            differenceListenerDelegate.skippedComparison(control, test);
        } else {
            System.err.println("DifferenceListener.skippedComparison: "
                + "unhandled control node type=" + control
                + ", unhandled test node type=" + test);
        }
    }

    /**
     * DifferenceListener implementation.
     * If the {@link Diff#overrideDifferenceListener  overrideDifferenceListener} 
     * method has been called then the call will be delegated 
     * otherwise the return value is true if the difference is 
     * not recoverable, or false if the difference is recoverable.
     * @param afterDifference
     * @return true if the comparison should be halted
     */
    public boolean haltComparison(Difference afterDifference) {
        if (differenceListenerDelegate != null) {
            return differenceListenerDelegate.haltComparison(afterDifference);
        } else {
            return haltComparison;
        }
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
    /**
     * Override the <code>DifferenceListener</code> used to determine how 
     * to handle differences that are found.
     * @param delegate the DifferenceListener instance to delegate handling to.
     */
    public void overrideDifferenceListener(DifferenceListener delegate) {
        this.differenceListenerDelegate = delegate;
    }

}
