package org.custommonkey.xmlunit;

import org.w3c.dom.Node;
import java.util.List;
import java.util.ArrayList;

/**
 * Compares and describes all the differences between two XML documents.
 * The document comparison does not stop once the first unrecoverable difference
 * is found, unlike the Diff class.
 * Note that because the differences are described relative to some control XML
 * the list of all differences when <i>A</i> is compared to <i>B</i> will not
 * necessarily be the same as when <i>B</i> is compared to <i>A</i>.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DetailedDiff extends Diff {
    private final List allDifferences;

    /**
     * Create a new instance based on a prototypical Diff instance
     * @param prototype the Diff instance for which more detailed difference
     *  information is required
     */
    public DetailedDiff(Diff prototype) {
        super(prototype);
        allDifferences = new ArrayList();
    }

    /**
     * DifferenceListener implementation.
     * Add the difference to the list of all differences
     * @param expected
     * @param actual
     * @param control
     * @param test
     * @param comparingWhat
     */
    public void differenceFound(String expected, String actual,
    Node control, Node test, Difference difference) {
        allDifferences.add(difference);
        super.differenceFound(expected, actual, control, test, difference);
    }

    /**
     * DifferenceListener implementation.
     * @param afterDifference
     * @return false (always) as this class wants to see all differences
     */
    public boolean haltComparison(Difference afterDifference) {
        return false;
    }

    /**
     * Obtain all the differences found by this instance
     * @return a list of {@link Difference differences}
     */
    public List getAllDifferences() {
        compare();
        return allDifferences;
    }
}
