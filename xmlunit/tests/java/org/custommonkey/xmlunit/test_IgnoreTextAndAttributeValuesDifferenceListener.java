package org.custommonkey.xmlunit;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;
import org.w3c.dom.Node;

/**
 * @author TimBacon
 */
public class test_IgnoreTextAndAttributeValuesDifferenceListener
extends TestCase {
    private DifferenceListener listener;
    public void testDifferenceFound() {
        assertCorrectInterpretation(
            DifferenceConstants.ATTR_NAME_NOT_FOUND,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.ATTR_SEQUENCE,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.ATTR_VALUE,
            DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
        assertCorrectInterpretation(
            DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED,
            DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
        assertCorrectInterpretation(
            DifferenceConstants.CDATA_VALUE,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.CHILD_NODELIST_LENGTH,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.CHILD_NODELIST_SEQUENCE,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.COMMENT_VALUE,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.DOCTYPE_NAME,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.DOCTYPE_PUBLIC_ID,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.DOCTYPE_SYSTEM_ID,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.ELEMENT_NUM_ATTRIBUTES,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.ELEMENT_TAG_NAME,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.HAS_CHILD_NODES,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.HAS_DOCTYPE_DECLARATION,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.NAMESPACE_PREFIX,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.NAMESPACE_URI,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.NODE_TYPE,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.PROCESSING_INSTRUCTION_DATA,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.PROCESSING_INSTRUCTION_TARGET,
            DifferenceListener.RETURN_ACCEPT_DIFFERENCE);
        assertCorrectInterpretation(
            DifferenceConstants.TEXT_VALUE,
            DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR);
    }
    
    private void assertCorrectInterpretation(
    Difference difference, int returnValue) {
        assertEquals(difference.toString(),
            returnValue,
            listener.differenceFound(difference));
    }
    
    public void testClassInUse() throws Exception {
        String control = "<clouds><cloud name=\"cumulus\" rain=\"maybe\">fluffy</cloud></clouds>";
        String similarTest = "<clouds><cloud name=\"cirrus\" rain=\"no\">wispy</cloud></clouds>";
        
        Diff diff = new Diff(control, similarTest);
        diff.overrideDifferenceListener(listener);
        assertTrue("similar " + diff.toString(), 
            diff.similar());
        assertTrue("but not identical " + diff.toString(), 
            !diff.identical());

        DetailedDiff detailedDiff = new DetailedDiff(
            new Diff(control, similarTest));
        assertEquals("2 attribute and 1 text values", 
            3, detailedDiff.getAllDifferences().size());

        String dissimilarTest = "<clouds><cloud name=\"nimbus\"/></clouds>";
        Diff dissimilarDiff = new Diff(control, dissimilarTest);
        dissimilarDiff.overrideDifferenceListener(listener);
        assertTrue("not similar " + dissimilarDiff.toString(),
            !dissimilarDiff.similar()); 
            
        DetailedDiff dissimilarDetailedDiff = new DetailedDiff(
            new Diff(control, dissimilarTest));
        dissimilarDetailedDiff.overrideDifferenceListener(listener);
        List differences = dissimilarDetailedDiff.getAllDifferences();
        assertEquals("wrong number of attributes, missing attribute, different attribute value, and missing text node. "
            + dissimilarDetailedDiff.toString(), 
            4, differences.size());
        int recoverable = 0;
        for (Iterator iter = differences.iterator(); iter.hasNext(); ) {
            Difference aDifference = (Difference) iter.next();
            if (aDifference.isRecoverable()) {
                recoverable++;
            }
        }
        assertEquals("attribute value difference has been overridden as similar",
            1, recoverable);
    }

    public void setUp() {
        listener = 
            new IgnoreTextAndAttributeValuesDifferenceListener();    
    }
    
    /**
     * Constructor for test_IgnoreTextAndAttributeValuesDifferenceListener.
     * @param name
     */
    public test_IgnoreTextAndAttributeValuesDifferenceListener(String name) {
        super(name);
    }
}
