package org.custommonkey.xmlunit;

import junit.framework.TestCase;

/**
 * @author TimBacon
 */
public class test_Difference extends TestCase {
    private final Difference ORIGINAL = 
        DifferenceConstants.ATTR_NAME_NOT_FOUND;

    public void testPrototypeConstructor() {
        Difference copy = new Difference(ORIGINAL);
        assertEquals("id", ORIGINAL.getId(), copy.getId());
        assertEquals("description", 
            ORIGINAL.getDescription(), copy.getDescription());
        assertEquals("recoverable", 
            ORIGINAL.isRecoverable(), copy.isRecoverable());
        
        assertEquals("precondition", false, ORIGINAL.isRecoverable());
        copy = new Difference(ORIGINAL, true);
        assertEquals("id again", ORIGINAL.getId(), copy.getId());
        assertEquals("description again", 
            ORIGINAL.getDescription(), copy.getDescription());
        assertEquals("recoverable again", 
            !ORIGINAL.isRecoverable(), copy.isRecoverable());
    }
    
    public void testEquals() {
        assertTrue("not equal to null", !ORIGINAL.equals(null));
        assertTrue("not equal to other class", !ORIGINAL.equals("aString"));
        assertEquals("equal to self", ORIGINAL, ORIGINAL);
        
        Difference copy = new Difference(ORIGINAL);
        assertEquals("equal to copy", ORIGINAL, copy);
        
        copy = new Difference(ORIGINAL, true);
        assertEquals("equal to copy again", ORIGINAL, copy);
    }
    
    /**
     * Constructor for test_Difference.
     * @param name
     */
    public test_Difference(String name) {
        super(name);
    }

}
