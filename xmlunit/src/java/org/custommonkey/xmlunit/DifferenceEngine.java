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

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

/**
 * Class that has responsibility for comparing Nodes and notifying a
 * DifferenceListener of any differences or dissimilarities that are found.
 * Knows how to compare namespaces and nested child nodes, but currently
 * only compares nodes of type ELEMENT_NODE, CDATA_SECTION_NODE,
 * COMMENT_NODE, DOCUMENT_TYPE_NODE, PROCESSING_INSTRUCTION_NODE and TEXT_NODE.
 * Nodes of other types (eg ENTITY_NODE) will be skipped.
 * @see DifferenceListener
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DifferenceEngine implements DifferenceConstants {
    private static final String NULL_NODE = "null";
    private static final String NOT_NULL_NODE = "not null";
    private final ComparisonController controller;
    private final XpathNodeTracker controlTracker = new XpathNodeTracker();
    private final XpathNodeTracker testTracker = new XpathNodeTracker();
	private ElementQualifier elementQualifier;
    
    public DifferenceEngine(ComparisonController controller) {
    	this(controller, new ElementNameQualifier());
    }
    
    public DifferenceEngine(ComparisonController controller, ElementQualifier elementQualifier) {
    	this.controller = controller;
    	this.elementQualifier = elementQualifier;
    }
    
    /**
     * Entry point for Node comparison testing
     * @param control
     * @param test
     * @param listener
     * @param elementQualifier
     */
    public void compare(Node control, Node test, DifferenceListener listener, ElementQualifier elementQualifier) {
    	ElementQualifier oldElementQualifier = this.elementQualifier;
    	if (elementQualifier != null) {
    		this.elementQualifier = elementQualifier;
    	}
    	controlTracker.reset();
    	testTracker.reset();
        try {
            compare(getNullOrNotNull(control), getNullOrNotNull(test),
                control, test, listener, NODE_TYPE);
            if (control!=null) {
                compareNode(control, test, listener);
            }
        } catch (DifferenceFoundException e) {
            // thrown by the protected compare() method to terminate the
            // comparison and unwind the call stack back to here
        }
    	this.elementQualifier = oldElementQualifier;
    }
	
    private String getNullOrNotNull(Node aNode) {
        return aNode==null ? NULL_NODE : NOT_NULL_NODE;
    }

    /**
     * First point of call: if nodes are comparable it compares node values then
     *  recurses to compare node children.
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareNode(Node control, Node test,
    DifferenceListener listener) throws DifferenceFoundException {
        boolean comparable = compareNodeBasics(control, test, listener);
        boolean isDocumentNode = false;

		if (comparable) {
	        switch (control.getNodeType()) {
	            case Node.ELEMENT_NODE:
	                compareElement((Element)control, (Element)test, listener);
	                break;
	            case Node.CDATA_SECTION_NODE:
	                compareCDataSection((CDATASection)control,
	                    (CDATASection)test, listener);
	                break;
	            case Node.COMMENT_NODE:
	                compareComment((Comment)control, (Comment)test, listener);
	                break;
	            case Node.DOCUMENT_TYPE_NODE:
	                compareDocumentType((DocumentType)control,
	                    (DocumentType)test, listener);
	                break;
	            case Node.PROCESSING_INSTRUCTION_NODE:
	                compareProcessingInstruction((ProcessingInstruction)control,
	                    (ProcessingInstruction)test, listener);
	                break;
	            case Node.TEXT_NODE:
	                compareText((Text)control, (Text)test, listener);
	                break;
	            case Node.DOCUMENT_NODE:
	                isDocumentNode = true;
	                compareDocument((Document)control, (Document) test, listener);
	                break;
	            default:
	                listener.skippedComparison(control, test);
	        }
		} 

        compareHasChildNodes(control, test, listener);
        if (isDocumentNode) {
            Element controlElement = ((Document)control).getDocumentElement();
            Element testElement = ((Document)test).getDocumentElement();
            if (controlElement!=null && testElement!=null) {
                compareNode(controlElement, testElement, listener);
            }
        } else {
        	controlTracker.indent();
        	testTracker.indent();
            compareNodeChildren(control, test, listener);
            controlTracker.outdent();
            testTracker.outdent();
        }
    }
    
    /**
     * Compare two Documents for doctype and then element differences
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareDocument(Document control, Document test, 
    DifferenceListener listener) throws DifferenceFoundException {
        DocumentType controlDoctype = control.getDoctype();
        DocumentType testDoctype = test.getDoctype();
        compare(getNullOrNotNull(controlDoctype), 
            getNullOrNotNull(testDoctype), 
            controlDoctype, testDoctype, listener, 
            HAS_DOCTYPE_DECLARATION);
        if (controlDoctype!=null && testDoctype!=null) {
            compareNode(controlDoctype, testDoctype, listener);
        }
    }

    /**
     * Compares node type and node namespace characteristics: basically
     * determines if nodes are comparable further
     * @param control
     * @param test
     * @param listener
     * @return true if the nodes are comparable further, false otherwise
     * @throws DifferenceFoundException
     */
    protected boolean compareNodeBasics(Node control, Node test,
    DifferenceListener listener) throws DifferenceFoundException {
		controlTracker.visited(control);
		testTracker.visited(test);

        Short controlType = new Short(control.getNodeType());
        Short testType = new Short(test.getNodeType());

        compare(controlType, testType, control, test, listener,
            NODE_TYPE);
        compare(control.getNamespaceURI(), test.getNamespaceURI(),
            control, test, listener, NAMESPACE_URI);
        compare(control.getPrefix(), test.getPrefix(),
            control, test, listener, NAMESPACE_PREFIX);
            
        return controlType.equals(testType);
    }

    /**
     * Compare the number of children, and if the same, compare the actual
     *  children via their NodeLists.
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareHasChildNodes(Node control, Node test,
    DifferenceListener listener) throws DifferenceFoundException {
        Boolean controlHasChildren = control.hasChildNodes()
            ? Boolean.TRUE : Boolean.FALSE;
        Boolean testHasChildren = test.hasChildNodes()
            ? Boolean.TRUE : Boolean.FALSE;
        compare(controlHasChildren, testHasChildren, control, test,
            listener, HAS_CHILD_NODES);
    }

    /**
     * Compare the number of children, and if the same, compare the actual
     *  children via their NodeLists.
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareNodeChildren(Node control, Node test,
    DifferenceListener listener) throws DifferenceFoundException {
        if (control.hasChildNodes() && test.hasChildNodes()) {
            NodeList controlChildren = control.getChildNodes();
            NodeList testChildren = test.getChildNodes();

            Integer controlLength = new Integer(controlChildren.getLength());
            Integer testLength = new Integer(testChildren.getLength());
            compare(controlLength, testLength, control, test, listener,
                CHILD_NODELIST_LENGTH);
            compareNodeList(controlChildren, testChildren,
                controlLength.intValue(), listener);
        }
    }

    /**
     * Compare the contents of two node list one by one, assuming that order
     * of children is NOT important: matching begins at same position in test
     * list as control list.
     * An {@link ElementQualifier ElementQualifier} instance is used to
     * determine which of the child elements in the test NodeList should be
     * compared to the current child element in the control NodeList.
     * @param control
     * @param test
     * @param numNodes convenience parameter because the calling method should
     *  know the value already
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareNodeList(NodeList control, NodeList test,
    int numNodes, DifferenceListener listener) throws DifferenceFoundException {
        Node nextControl, nextTest = null;
        int j = 0;
        int lastTestNode = test.getLength() - 1;
        boolean matchOnElement, matchFound;
        short findNodeType;
        testTracker.preloadNodeList(test);

        for (int i=0; i < numNodes; ++i) {
            nextControl = control.item(i);
            if (nextControl instanceof Element) {
                matchOnElement = true;
            } else {
                matchOnElement = false;
            }
            findNodeType = nextControl.getNodeType();
            int startAt = ( i > lastTestNode ? lastTestNode : i);
            j = startAt;
            
            matchFound = false;

            while (!matchFound) {
                if (matchOnElement && test.item(j) instanceof Element
                && elementQualifier.qualifyForComparison((Element)nextControl, (Element)test.item(j))) {
                    matchFound = true;
                } else if (!matchOnElement
                && findNodeType == test.item(j).getNodeType()) {
                    matchFound = true;
                } else {
                    ++j;
                    if (j > lastTestNode) {
                        j = 0;
                    }
                    if (j == startAt) {
                        // been through all children
                        break;
                    }
                }
            }
            nextTest = test.item(j);
            compare(new Integer(i), new Integer(j),
                nextControl, nextTest, listener, CHILD_NODELIST_SEQUENCE);
            compareNode(nextControl, nextTest, listener);
        }
    }

    /**
     * @param aNode
     * @return true if the node has a namespace
     */
    private boolean isNamespaced(Node aNode) {
        String namespace = aNode.getNamespaceURI();
        return namespace != null && namespace.length() > 0;
    }

    /**
     * Compare 2 elements and their attributes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareElement(Element control, Element test,
    DifferenceListener listener) throws DifferenceFoundException {
        if (isNamespaced(control)) {
            compare(control.getLocalName(), test.getLocalName(), control, test,
                listener,ELEMENT_TAG_NAME);
        } else {
            compare(control.getTagName(), test.getTagName(), control, test,
                listener,ELEMENT_TAG_NAME);
        }

        NamedNodeMap controlAttr = control.getAttributes();
        NamedNodeMap testAttr = test.getAttributes();
        compare(new Integer(controlAttr.getLength()),
            new Integer(testAttr.getLength()),
            control, test, listener, ELEMENT_NUM_ATTRIBUTES);

        compareElementAttributes(control, test, controlAttr, testAttr,
            listener);
    }

    private void compareElementAttributes(Element control, Element test,
    NamedNodeMap controlAttr, NamedNodeMap testAttr,
    DifferenceListener listener) throws DifferenceFoundException {
        for (int i=0; i < controlAttr.getLength(); ++i) {
            Attr nextAttr = (Attr) controlAttr.item(i);
            Attr compareTo = null;
            String attrName = nextAttr.getName();
            if (isXMLNSAttribute(nextAttr)) {
                // xml namespacing is handled in compareNodeBasics
            } else if (testAttr.getNamedItem(attrName) != null) {
                compareTo = (Attr) testAttr.getNamedItem(attrName);
                compareAttribute(nextAttr, compareTo, listener);
                Attr attributeItem = (Attr) testAttr.item(i);

                String testAttrName;
                if (attributeItem == null) {
                    testAttrName = "[attribute absent]";
                } else {
                    testAttrName = testAttr.item(i).getNodeName();
                }
                compare(attrName, testAttrName,
                    nextAttr, compareTo, listener, ATTR_SEQUENCE);
            } else {
                compare(attrName, null, control, test, listener,
                    ATTR_NAME_NOT_FOUND);
            }
        }
    }

    /**
     * @param attribute
     * @return true if the attribute represents a namespace declaration
     */
    private boolean isXMLNSAttribute(Attr attribute) {
        return XMLConstants.XMLNS_PREFIX.equals(attribute.getPrefix()) ||
            XMLConstants.XMLNS_PREFIX.equals(attribute.getName());
    }

    /**
     * Compare two attributes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareAttribute(Attr control, Attr test,
    DifferenceListener listener) throws DifferenceFoundException {
    	controlTracker.visited(control);
    	testTracker.visited(test);
        compare(control.getValue(), test.getValue(), control, test,
            listener,ATTR_VALUE);

        compare(control.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
            test.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
            control, test, listener, ATTR_VALUE_EXPLICITLY_SPECIFIED);
    }

    /**
     * Compare two CDATA sections
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareCDataSection(CDATASection control, CDATASection test,
    DifferenceListener listener) throws DifferenceFoundException {
        compareCharacterData(control, test, listener, CDATA_VALUE);
    }

    /**
     * Compare two comments
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareComment(Comment control, Comment test,
    DifferenceListener listener) throws DifferenceFoundException {
        compareCharacterData(control, test, listener, COMMENT_VALUE);
    }

    /**
     * Compare two DocumentType nodes
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareDocumentType(DocumentType control, DocumentType test,
    DifferenceListener listener) throws DifferenceFoundException {
        compare(control.getName(), test.getName(), control, test, listener,
            DOCTYPE_NAME);
        compare(control.getPublicId(), test.getPublicId(), control, test, listener,
            DOCTYPE_PUBLIC_ID);

        compare(control.getSystemId(), test.getSystemId(),
            control, test, listener, DOCTYPE_SYSTEM_ID);
    }

    /**
     * Compare two processing instructions
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareProcessingInstruction(ProcessingInstruction control,
    ProcessingInstruction test, DifferenceListener listener)
    throws DifferenceFoundException {
        compare(control.getTarget(), test.getTarget(), control, test, listener,
            PROCESSING_INSTRUCTION_TARGET);
        compare(control.getData(), test.getData(), control, test, listener,
            PROCESSING_INSTRUCTION_DATA);
    }

    /**
     * Compare text
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareText(Text control, Text test,
    DifferenceListener listener)
    throws DifferenceFoundException {
        compareCharacterData(control, test, listener, TEXT_VALUE);
    }

    /**
     * Character comparison method used by comments, text and CDATA sections
     * @param control
     * @param test
     * @param listener
     * @param differenceType
     * @throws DifferenceFoundException
     */
    private void compareCharacterData(CharacterData control, CharacterData test,
    DifferenceListener listener, Difference difference)
    throws DifferenceFoundException {
        compare(control.getData(), test.getData(), control, test, listener,
            difference);
    }

    /**
     * If the expected and actual values are unequal then inform the listener of
     *  a difference and throw a DifferenceFoundException.
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param listener
     * @param differenceType
     * @throws DifferenceFoundException
     */
    protected void compare(Object expected, Object actual,
    Node control, Node test, DifferenceListener listener, Difference difference)
    throws DifferenceFoundException {
        if (unequal(expected, actual)) {
        	NodeDetail controlDetail = new NodeDetail(String.valueOf(expected),
        		control, controlTracker.toXpathString());
        	NodeDetail testDetail = new NodeDetail(String.valueOf(actual),
        		test, testTracker.toXpathString());
        	Difference differenceInstance = new Difference(difference, 
        		controlDetail, testDetail);
            listener.differenceFound(differenceInstance);
            if (controller.haltComparison(differenceInstance)) {
                throw flowControlException;
            }
        }
    }

    /**
     * Test two possibly null values for inequality
     * @param expected
     * @param actual
     * @return TRUE if the values are neither both null, nor equals() equal
     */
    private boolean unequal(Object expected, Object actual) {
        return (expected==null ? actual!=null : unequalNotNull(expected, actual));
    }

    /**
     * Test two non-null values for inequality
     * @param expected
     * @param actual
     * @return TRUE if the values are not equals() equal (taking whitespace
     *  into account if necessary)
     */
    private boolean unequalNotNull(Object expected, Object actual) {
        if (XMLUnit.getIgnoreWhitespace()
        && expected instanceof String && actual instanceof String) {
            return !(((String)expected).trim().equals(((String)actual).trim()));
        }
        return !(expected.equals(actual));
    }

    /**
     * Marker exception thrown by the protected compare() method and passed
     * upwards through the call stack to the public compare() method.
     */
    protected static final class DifferenceFoundException extends Exception {
        private DifferenceFoundException() {
            super("This exception is used to control flow");
        }
    }

    /**
     * Exception instance used internally to control flow
     * when a difference is found
     */
    private static final DifferenceFoundException flowControlException =
        new DifferenceFoundException();
}