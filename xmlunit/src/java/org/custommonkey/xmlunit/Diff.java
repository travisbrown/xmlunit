package org.custommonkey.xmlunit;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;

/**
 * Describes differences between XML documents
 */
public class Diff{
    private final Document controlDoc;
    private final Document testDoc;
    private boolean similar = true;
    private boolean identical = true;
    private boolean compared = false;
    private Element controlElement;
    private Element testElement;

    /**
     * Old style constructor used by XMLUnit class
     * @deprecated Not required since the addition of the new constructors
     */
    protected Diff() {
        this.controlDoc = null;
        this.testDoc = null;
    }

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
        this(XMLUnit.getControlParser().parse(new InputSource(control)),
            XMLUnit.getTestParser().parse(new InputSource(test)));
    }

    /**
     * Construct a Diff that compares the XML in two Documents
     */
    public Diff(Document controlDoc, Document testDoc) {
        this.controlDoc = controlDoc;
        this.testDoc = testDoc;
    }

    /**
     * Construct a Diff that compares the XML in a control Document against the
     * result of a transformation
     */
    public Diff(String control, Transform testTransform) throws IOException,
    TransformerException, ParserConfigurationException, SAXException {
        this(XMLUnit.getControlParser().parse(
            new InputSource(new StringReader(control))), testTransform.getResultDocument());
    }

    /**
     * Top of the recursive comparison code tree
     */
    private void compare() {
        if (compared) {
            return;
        }
        if (controlDoc==null && testDoc==null) {
            return;
        }
        compare(controlDoc.getDocumentElement(), testDoc.getDocumentElement());
        compared = true;
    }

    /**
     * compare XML elements recursively
     */
    private void compare(Element control, Element test){
        try {
            compareElementValues(control, test);
            compareAttributes(control, test);
            compareChildren(control, test);
        } catch (ComparisonFailedException e) {
            differenceFound(control, test);
        }
    }

    private void compareElementValues(Element control, Element test)
    throws ComparisonFailedException {
        if (!identicalNodeValues(control, test)) {
            throw new ComparisonFailedException();
        }
    }

    private String getText(Node aNode) {
        String text = "";
        switch(aNode.getNodeType()) {
            case Node.ELEMENT_NODE:
                NodeList children = aNode.getChildNodes();
                int child = 0;
                boolean noTextFound = true;
                Node childNode;
                while (child < children.getLength() && noTextFound ) {
                    childNode = children.item(child);
                    if (childNode.getNodeType()==Node.TEXT_NODE) {
                        noTextFound = false;
                        text = childNode.getNodeValue();
                    }
                    ++child;
                }
                break;
            case Node.ATTRIBUTE_NODE:
                text = aNode.getNodeValue();
                break;
            default:
                break;
        }
        return text;
    }

    private boolean identicalNodeValues(Node control, Node test) {
        if(control.getNodeName().equals(test.getNodeName())){
            if(XMLUnit.getIgnoreWhitespace()){
                if(getText(control).trim().equals(getText(test).trim())) {
                    return true;
                }
            }else if(getText(control).equals(getText(test))) {
                return true;
            }
        }
        return false;
    }

    private void compareAttributes(Element control, Element test)
    throws ComparisonFailedException {
        NamedNodeMap controlAttributes = control.getAttributes();
        NamedNodeMap testAttributes = test.getAttributes();
        int numAttributes = controlAttributes.getLength();
        if (numAttributes!=testAttributes.getLength()) {
            similar = false;
        }

        Node controlNextAttribute;
        Node testNextAttribute, testMatchingAttribute;
        String controlAttribName, controlAttribValue;
        int attributeNum = 0;

        while(similar && attributeNum < numAttributes){
            controlNextAttribute = controlAttributes.item(attributeNum);
            testNextAttribute = testAttributes.item(attributeNum);

            controlAttribName = controlNextAttribute.getNodeName();
            controlAttribValue = controlNextAttribute.getNodeValue();

            testMatchingAttribute = test.getAttributeNode(controlAttribName);

            if(testMatchingAttribute==null){
                similar = false;
            }else {
                similar = identicalNodeValues(controlNextAttribute, testNextAttribute);
            }
            ++attributeNum;
        }
        if (!similar) {
            throw new ComparisonFailedException();
        }
    }

    private void compareChildren(Element control, Element test)
    throws ComparisonFailedException {
        NodeList controlChildren = control.getChildNodes();
        NodeList testChildren = test.getChildNodes();
        int numNodes = controlChildren.getLength();
        if (numNodes!=testChildren.getLength()) {
            similar =false;
        }

        Node controlNextChild ;
        Element testMatchingChild ;
        int elementNum = 0;
        while (similar && elementNum < numNodes){
            controlNextChild = controlChildren.item(elementNum);
            if (controlNextChild.getNodeType()==Document.ELEMENT_NODE) {
                testMatchingChild = getMatchingElement((Element)controlNextChild,
                    testChildren, elementNum);
                if(testMatchingChild==null){
                    similar = false;
                }else{
                    compare((Element)controlNextChild, testMatchingChild);
                }
            }
            ++elementNum;
        }

        if (!similar) {
            throw new ComparisonFailedException();
        }
    }

    private Element getMatchingElement(Element elementToMatch, NodeList elementList,
    int startAt) {
        Element matching = null;
        int listIndex = startAt;
        int maxIndex = elementList.getLength();

        Node nextNode;
        do {
            nextNode = elementList.item(listIndex);
            if (nextNode.getNodeType()==Document.ELEMENT_NODE
            && identicalNodeValues((Element)nextNode, elementToMatch)) {
                matching = (Element) nextNode;
                if (listIndex!=startAt) {
                    identical = false;
                }
            } else {
                ++listIndex;
                if (listIndex==maxIndex) {
                    listIndex = 0;
                }
            }
        } while (matching==null && listIndex!=startAt);
        return matching;
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
     * Add a difference to the list difference between documents
     */
    protected final void differenceFound(Element control, Element test){
        if(controlElement!=null||testElement!=null) {
            return;
        }
        similar = false;
        identical = false;
        this.controlElement = control;
        this.testElement = test;
    }

    public String toString(){
        compare();
        StringBuffer buf = new StringBuffer(getClass().getName());
        if (identical) {
            return buf.append("[identical]").toString();
        } else if (similar) {
            buf.append("[similar]");
        }
        buf.append(" Expected: ");
        appendElement(controlElement, buf);
        buf.append(", but was: ");
        appendElement(testElement, buf);
        return buf.toString();
    }

    private StringBuffer appendElement(Element anElement,
    StringBuffer appendToBuf) {
        if(anElement==null){
            appendToBuf.append("null");
        }else{
            String elementText = getText(anElement);
            appendToBuf.append("<").append(anElement.getNodeName());

            appendAttributes(anElement.getAttributes(), appendToBuf);

            if(elementText==null || elementText.length()==0){
                appendToBuf.append("/>");
            }else{
                appendToBuf.append(">").append(elementText)
                    .append("</").append(anElement.getNodeName()).append(">");
            }
        }
        return appendToBuf;
    }

    private StringBuffer appendAttributes(NamedNodeMap attributeMap,
    StringBuffer appendToBuf) {
        Node item;
        for (int i=0; i < attributeMap.getLength(); ++i) {
            item = attributeMap.item(i);
            appendToBuf.append(" ").append(item.getNodeName())
                .append("=\"").append(item.getNodeValue()).append("\"");
        }
        return appendToBuf;
    }

    /**
     * Inner class used by internal compare() methods to flag that a comparison
     * found dissimilarities
     */
    private class ComparisonFailedException extends Exception {
        private ComparisonFailedException() {
            super();
        }
    }
}
