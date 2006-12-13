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
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.
 * sourceforge.net</a>
 * @see DifferenceListener#differenceFound(Difference)
 */
public class DifferenceEngine implements DifferenceConstants {
    private static final String NULL_NODE = "null";
    private static final String NOT_NULL_NODE = "not null";
    private final ComparisonController controller;
    private final XpathNodeTracker controlTracker;
    private final XpathNodeTracker testTracker;
    
    /**
     * Simple constructor
     * @param controller the instance used to determine whether a Difference
     * detected by this class should halt further comparison or not
     * @see ComparisonController#haltComparison(Difference)
     */
    public DifferenceEngine(ComparisonController controller) {
    	this.controller = controller;
    	this.controlTracker = new XpathNodeTracker();
    	this.testTracker = new XpathNodeTracker();
    }
        
    /**
     * Entry point for Node comparison testing.
     * @param control Control XML to compare
     * @param test Test XML to compare
     * @param listener Notified of any {@link Difference differences} detected
     * during node comparison testing
     * @param elementQualifier Used to determine which elements qualify for
     * comparison e.g. when a node has repeated child elements that may occur
     * in any sequence and that sequence is not considered important. 
     */
    public void compare(Node control, Node test, DifferenceListener listener, 
    ElementQualifier elementQualifier) {
    	controlTracker.reset();
    	testTracker.reset();
        try {
            compare(getNullOrNotNull(control), getNullOrNotNull(test),
                control, test, listener, NODE_TYPE);
            if (control!=null) {
                compareNode(control, test, listener, elementQualifier);
            }
        } catch (DifferenceFoundException e) {
            // thrown by the protected compare() method to terminate the
            // comparison and unwind the call stack back to here
        }    	
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
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareNode(Node control, Node test,
    DifferenceListener listener, ElementQualifier elementQualifier) 
    throws DifferenceFoundException {
        boolean comparable = compareNodeBasics(control, test, listener);
        boolean isDocumentNode = false;

		if (comparable) {
	        switch (control.getNodeType()) {
	            case Node.ELEMENT_NODE:
	                compareElement((Element)control, (Element)test, listener);
	                break;
	            case Node.CDATA_SECTION_NODE:
	            case Node.TEXT_NODE:
	                compareText((CharacterData) control,
                                    (CharacterData) test, listener);
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
	            case Node.DOCUMENT_NODE:
	                isDocumentNode = true;
	                compareDocument((Document)control, (Document) test, 
	                	listener, elementQualifier);
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
                compareNode(controlElement, testElement, listener, elementQualifier);
            }
        } else {
        	controlTracker.indent();
        	testTracker.indent();
            compareNodeChildren(control, test, listener, elementQualifier);
            controlTracker.outdent();
            testTracker.outdent();
        }
    }
    
    /**
     * Compare two Documents for doctype and then element differences
     * @param control
     * @param test
     * @param listener
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareDocument(Document control, Document test, 
    DifferenceListener listener, ElementQualifier elementQualifier) 
    throws DifferenceFoundException {
        DocumentType controlDoctype = control.getDoctype();
        DocumentType testDoctype = test.getDoctype();
        compare(getNullOrNotNull(controlDoctype), 
            getNullOrNotNull(testDoctype), 
            controlDoctype, testDoctype, listener, 
            HAS_DOCTYPE_DECLARATION);
        if (controlDoctype!=null && testDoctype!=null) {
            compareNode(controlDoctype, testDoctype, listener, elementQualifier);
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

        boolean textAndCDATA = comparingTextAndCDATA(control.getNodeType(),
                                                     test.getNodeType());
        if (!textAndCDATA) {
            compare(controlType, testType, control, test, listener,
                    NODE_TYPE);
        }
        compare(control.getNamespaceURI(), test.getNamespaceURI(),
            control, test, listener, NAMESPACE_URI);
        compare(control.getPrefix(), test.getPrefix(),
            control, test, listener, NAMESPACE_PREFIX);
            
        return textAndCDATA || controlType.equals(testType);
    }

    private boolean comparingTextAndCDATA(short controlType, short testType) {
        return
            controlType == Node.TEXT_NODE && testType == Node.CDATA_SECTION_NODE
            ||
            testType == Node.TEXT_NODE && controlType == Node.CDATA_SECTION_NODE;
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
     * @param elementQualifier
     * @throws DifferenceFoundException
     */
    protected void compareNodeChildren(Node control, Node test,
    DifferenceListener listener, ElementQualifier elementQualifier) 
    throws DifferenceFoundException {
        if (control.hasChildNodes() && test.hasChildNodes()) {
            NodeList controlChildren = control.getChildNodes();
            NodeList testChildren = test.getChildNodes();

            Integer controlLength = new Integer(controlChildren.getLength());
            Integer testLength = new Integer(testChildren.getLength());
            compare(controlLength, testLength, control, test, listener,
                CHILD_NODELIST_LENGTH);
            compareNodeList(controlChildren, testChildren,
                controlLength.intValue(), listener, elementQualifier);
        }
    }

    /**
     * Compare the contents of two node list one by one, assuming that order
     * of children is NOT important: matching begins at same position in test
     * list as control list.
     * @param control
     * @param test
     * @param numNodes convenience parameter because the calling method should
     *  know the value already
     * @param listener
     * @param elementQualifier used to determine which of the child elements in
     * the test NodeList should be compared to the current child element in the
     * control NodeList.
     * @throws DifferenceFoundException
     */
    protected void compareNodeList(NodeList control, NodeList test,
    int numNodes, DifferenceListener listener, ElementQualifier elementQualifier) 
    throws DifferenceFoundException {
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
        	compareNode(nextControl, nextTest, listener, elementQualifier);
            compare(new Integer(i), new Integer(j),
                nextControl, nextTest, listener, CHILD_NODELIST_SEQUENCE);
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
    	compare(getUnNamespacedNodeName(control), getUnNamespacedNodeName(test), 
    		control, test, listener, ELEMENT_TAG_NAME);

        NamedNodeMap controlAttr = control.getAttributes();
    	Integer controlNonXmlnsAttrLength = getNonXmlnsAttrLength(controlAttr);
        NamedNodeMap testAttr = test.getAttributes();
        Integer testNonXmlnsAttrLength = getNonXmlnsAttrLength(testAttr);
        compare(controlNonXmlnsAttrLength, testNonXmlnsAttrLength,
            control, test, listener, ELEMENT_NUM_ATTRIBUTES);

        compareElementAttributes(control, test, controlAttr, testAttr,
            listener);
    }

	private Integer getNonXmlnsAttrLength(NamedNodeMap attributes) {
		int length = 0, maxLength = attributes.getLength();
		for (int i = 0; i < maxLength; ++i) {
			if (!isXMLNSAttribute((Attr) attributes.item(i))) {
				++length;
			}
		}
		return new Integer(length);
	}

    private void compareElementAttributes(Element control, Element test,
    NamedNodeMap controlAttr, NamedNodeMap testAttr,
    DifferenceListener listener) throws DifferenceFoundException {
        for (int i=0; i < controlAttr.getLength(); ++i) {
            Attr nextAttr = (Attr) controlAttr.item(i);
        	if (isXMLNSAttribute(nextAttr)) {
        		// xml namespacing is handled in compareNodeBasics
        	} else {
            	boolean isNamespacedAttr = isNamespaced(nextAttr);
            	String attrName = getUnNamespacedNodeName(nextAttr, isNamespacedAttr);
            	Attr compareTo = null;
            	
            	if (isNamespacedAttr) {
            		compareTo = (Attr) testAttr.getNamedItemNS(
            			nextAttr.getNamespaceURI(), attrName);
            	} else {
            		compareTo = (Attr) testAttr.getNamedItem(attrName);
            	}
            	            
	            if (compareTo != null) {
	                compareAttribute(nextAttr, compareTo, listener);
	                
	                Attr attributeItem = (Attr) testAttr.item(i);
	                String testAttrName = "[attribute absent]";
	                if (attributeItem != null) {
	                    testAttrName = getUnNamespacedNodeName(attributeItem);
	                }
	                compare(attrName, testAttrName,
	                    nextAttr, compareTo, listener, ATTR_SEQUENCE);
	            } else {
	                compare(attrName, null, control, test, listener,
	                    ATTR_NAME_NOT_FOUND);
	            }
        	}
        }
    }
    
    private String getUnNamespacedNodeName(Node aNode) {
    	return getUnNamespacedNodeName(aNode, isNamespaced(aNode));
    }
    
	private String getUnNamespacedNodeName(Node aNode, boolean isNamespacedNode) {
		if (isNamespacedNode) {
			return aNode.getLocalName();
		}
		return aNode.getNodeName();
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
    	
    	compare(control.getPrefix(), test.getPrefix(), control, test, 
    		listener, NAMESPACE_PREFIX);
    		
        compare(control.getValue(), test.getValue(), control, test,
            listener, ATTR_VALUE);

        compare(control.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
            test.getSpecified() ? Boolean.TRUE : Boolean.FALSE,
            control, test, listener, ATTR_VALUE_EXPLICITLY_SPECIFIED);
    }

    /**
     * Compare two CDATA sections - unused, kept for backwards compatibility
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareCDataSection(CDATASection control, CDATASection test,
    DifferenceListener listener) throws DifferenceFoundException {
        compareText(control, test, listener);
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
     * Compare text - unused, kept for backwards compatibility
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareText(Text control, Text test,
                               DifferenceListener listener)
        throws DifferenceFoundException {
        compareText((CharacterData) control, (CharacterData) test, listener);
    }

    /**
     * Compare text
     * @param control
     * @param test
     * @param listener
     * @throws DifferenceFoundException
     */
    protected void compareText(CharacterData control, CharacterData test,
                               DifferenceListener listener)
        throws DifferenceFoundException {
        compareCharacterData(control, test, listener,
                             control instanceof CDATASection ? CDATA_VALUE : TEXT_VALUE);
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