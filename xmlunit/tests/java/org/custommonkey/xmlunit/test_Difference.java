package org.custommonkey.xmlunit;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author TimBacon
 */
public class test_Difference extends TestCase {
    private final Difference ORIGINAL = 
        DifferenceConstants.ATTR_NAME_NOT_FOUND;

    public void testCopyConstructor() {
        Difference copy = new Difference(ORIGINAL, null, null);
        assertEquals("id", ORIGINAL.getId(), copy.getId());
        assertEquals("description", 
            ORIGINAL.getDescription(), copy.getDescription());
        assertEquals("recoverable", 
            ORIGINAL.isRecoverable(), copy.isRecoverable());
        
        assertEquals("precondition", false, ORIGINAL.isRecoverable());
        copy.setRecoverable(true);
        assertEquals("recoverable again", 
            !ORIGINAL.isRecoverable(), copy.isRecoverable());
    }
    
    public void testEquals() {
        assertTrue("not equal to null", !ORIGINAL.equals(null));
        assertTrue("not equal to other class", !ORIGINAL.equals("aString"));
        assertEquals("equal to self", ORIGINAL, ORIGINAL);
        
        Difference copy = new Difference(ORIGINAL, null, null);
        assertEquals("equal to copy", ORIGINAL, copy);        
    }
    
    public void testToString() throws Exception {
    	String originalAsString = "Difference (#" + ORIGINAL.getId()
    		+ ") " + ORIGINAL.getDescription();
    	assertEquals("Original", originalAsString, ORIGINAL.toString());
    	
    	Document document = XMLUnit.getControlParser().newDocument();
    	
    	Node controlNode = document.createComment("control");
    	NodeDetail controlNodeDetail = new NodeDetail(controlNode.getNodeValue(),
    		controlNode, "/testToString/comment()");
    		
    	Node testNode = document.createComment("test");
    	NodeDetail testNodeDetail = new NodeDetail(testNode.getNodeValue(),
    		testNode, "/testToString/comment()");
    		
    	Difference difference = new Difference(DifferenceConstants.COMMENT_VALUE, 
    		controlNodeDetail, testNodeDetail);
    	StringBuffer buf = new StringBuffer("Expected ")
			.append(DifferenceConstants.COMMENT_VALUE.getDescription())
    		.append(" 'control' but was 'test' - comparing ");
    	NodeDescriptor.appendNodeDetail(buf, controlNodeDetail);
    	buf.append(" to ");
    	NodeDescriptor.appendNodeDetail(buf, testNodeDetail);
    	assertEquals("detail", buf.toString(), difference.toString());
    }
    
    /**
     * Constructor for test_Difference.
     * @param name
     */
    public test_Difference(String name) {
        super(name);
    }

}
