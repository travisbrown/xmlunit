package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Class to use when performing a Diff that only compares the 
 * structure of 2 pieces of XML, i.e. where the values of text
 * and attribute nodes should be ignored.
 * @see Diff#overrideDifferenceListener
 */
public class IgnoreTextAndAttributeValuesDifferenceListener
implements DifferenceListener {
    private static final int[] IGNORE_VALUES = new int[] {
        DifferenceConstants.ATTR_VALUE.getId(),
        DifferenceConstants.ATTR_VALUE_EXPLICITLY_SPECIFIED.getId(),
        DifferenceConstants.TEXT_VALUE.getId()
    };
        
    private boolean isIgnoredDifference(Difference difference) {
        int differenceId = difference.getId();
        for (int i=0; i < IGNORE_VALUES.length; ++i) {
            if (differenceId == IGNORE_VALUES[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR to ignore 
     *  differences in values of TEXT or ATTRIBUTE nodes,
     *  and RETURN_ACCEPT_DIFFERENCE to accept all other 
     *  differences.
     * @see DifferenceListener#differenceFound(String, String, Node, Node, Difference)
     */
    public int differenceFound(String expected, String actual,
    Node control, Node test, Difference difference) {
        if (isIgnoredDifference(difference)) {
            return RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR;
        } else {
            return RETURN_ACCEPT_DIFFERENCE;
        }
    }
    
    /**
     * Do nothing
     * @see DifferenceListener#skippedComparison(Node, Node)
     */
    public void skippedComparison(Node control, Node test) {
    }

    /**
     * @return false if the difference is ignored or recoverable, 
     *  true otherwise
     * @see DifferenceListener#haltComparison(Difference)
     */
    public boolean haltComparison(Difference afterDifference) {
        if (isIgnoredDifference(afterDifference)
        || afterDifference.isRecoverable()) {
            return false;
        }
        return true;
    }

}
