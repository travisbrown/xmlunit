package org.custommonkey.xmlunit;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Helper class.
 * Abstract interface implementation that performs Node-type checks and
 * delegates testNode() processing to subclass.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see NodeTest
 */
public abstract class AbstractNodeTester implements NodeTester {
    /**
     * Validate a single Node by delegating to node type specific methods.
     */
    public void testNode(Node aNode, NodeTest forTest) throws NodeTestException {
        switch (aNode.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
                testAttribute((Attr)aNode);
                break;
            case Node.CDATA_SECTION_NODE:
                testCDATASection((CDATASection)aNode);
                break;
            case Node.COMMENT_NODE:
                testComment((Comment)aNode);
                break;
            case Node.DOCUMENT_TYPE_NODE:
                testDocumentType((DocumentType)aNode);
                break;
            case Node.ELEMENT_NODE:
                testElement((Element)aNode);
                break;
            case Node.ENTITY_NODE:
                testEntity((Entity)aNode);
                break;
            case Node.ENTITY_REFERENCE_NODE:
                testEntityReference((EntityReference)aNode);
                break;
            case Node.NOTATION_NODE:
                testNotation((Notation)aNode);
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                testProcessingInstruction(
                    (ProcessingInstruction) aNode);
                break;
            case Node.TEXT_NODE:
                testText((Text)aNode);
                break;
            default:
                throw new NodeTestException("No delegate method for Node type",
                    aNode);
        }
    }

    /**
     * Template delegator for testNode() method.
     * @param attribute
     * @exception NodeTestException always: override if required in subclass
     */
    public void testAttribute(Attr attribute) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param cdata
     * @exception NodeTestException always: override if required in subclass
     */
    public void testCDATASection(CDATASection cdata) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param comment
     * @exception NodeTestException always: override if required in subclass
     */
    public void testComment(Comment comment) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param doctype
     * @exception NodeTestException always: override if required in subclass
     */
    public void testDocumentType(DocumentType doctype) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param element
     * @exception NodeTestException always: override if required in subclass
     */
    public void testElement(Element element) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param entity
     * @exception NodeTestException always: override if required in subclass
     */
    public void testEntity(Entity entity) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param reference
     * @exception NodeTestException always: override if required in subclass
     */
    public void testEntityReference(EntityReference reference) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param notation
     * @exception NodeTestException always: override if required in subclass
     */
    public void testNotation(Notation notation) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param instr
     * @exception NodeTestException always: override if required in subclass
     */
    public void testProcessingInstruction(ProcessingInstruction instr) throws NodeTestException  {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }
    /**
     * Template delegator for testNode() method.
     * @param text
     * @exception NodeTestException always: override if required in subclass
     */
    public void testText(Text text) throws NodeTestException {
        throw new NodeTestException("Test fails by default in AbstractNodeTester");
    }

    /**
     * Validate that the Nodes validated one-by-one in the <code>isValid</code>
     * method were all the Nodes expected.
     * @return true if no mode Nodes were expected, false otherwise
     */
    public abstract void noMoreNodes(NodeTest forTest) throws NodeTestException;
}