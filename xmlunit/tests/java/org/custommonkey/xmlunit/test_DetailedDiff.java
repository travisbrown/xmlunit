package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;
import java.util.List;
import org.w3c.dom.Document;
import java.io.Reader;

/**
 * Test a DetailedDiff. Extend the test case class for Diff so we can rerun those
 * tests with a DetailedDiff and assert that behaviour has not changed.
 */
public class test_DetailedDiff extends test_Diff {
    private String firstForecast, secondForecast;

    public void testAllDifferencesFirstForecastControl() throws Exception {
        Diff multipleDifferences = new Diff(firstForecast, secondForecast);
        DetailedDiff detailedDiff = new DetailedDiff(multipleDifferences);

        List differences = detailedDiff.getAllDifferences();
        assertExpectedDifferencesFirstForecastControl(differences, detailedDiff);
    }

    private void assertExpectedDifferencesFirstForecastControl(List differences,
    DetailedDiff detailedDiff) {
        assertEquals("size: " + detailedDiff, 5, differences.size());
        assertEquals("first: " + detailedDiff,
            DifferenceConstants.ELEMENT_NUM_ATTRIBUTES, differences.get(0));
        assertEquals("second: " + detailedDiff,
            DifferenceConstants.ATTR_NAME_NOT_FOUND, differences.get(1));
        assertEquals("third: " + detailedDiff,
            DifferenceConstants.ATTR_VALUE, differences.get(2));
        assertEquals("fourth: " + detailedDiff,
            DifferenceConstants.ATTR_SEQUENCE, differences.get(3));
        assertEquals("fifth: " + detailedDiff,
            DifferenceConstants.HAS_CHILD_NODES, differences.get(4));
    }

    public void testAllDifferencesSecondForecastControl() throws Exception {
        Diff multipleDifferences = new Diff(secondForecast, firstForecast);
        DetailedDiff detailedDiff = new DetailedDiff(multipleDifferences);

        List differences = detailedDiff.getAllDifferences();

        assertEquals("size: " + detailedDiff, 4, differences.size());
        assertEquals("first: " + detailedDiff,
            DifferenceConstants.ELEMENT_NUM_ATTRIBUTES, differences.get(0));
        assertEquals("second: " + detailedDiff,
            DifferenceConstants.ATTR_VALUE, differences.get(1));
        assertEquals("third: " + detailedDiff,
            DifferenceConstants.ATTR_SEQUENCE, differences.get(2));
        assertEquals("fourth: " + detailedDiff,
            DifferenceConstants.HAS_CHILD_NODES, differences.get(3));
    }

    public void testPrototypeIsADetailedDiff() throws Exception {
        Diff multipleDifferences = new Diff(firstForecast, secondForecast);
        DetailedDiff detailedDiff = new DetailedDiff(
            new DetailedDiff(multipleDifferences));

        List differences = detailedDiff.getAllDifferences();
        assertExpectedDifferencesFirstForecastControl(differences, detailedDiff);
    }

    protected Diff buildDiff(Document control, Document test) {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    protected Diff buildDiff(String control, String test) throws Exception {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    protected Diff buildDiff(Reader control, Reader test) throws Exception {
        return new DetailedDiff(super.buildDiff(control, test));
    }

    public test_DetailedDiff(String name) {
        super(name);
        firstForecast = "<weather><today icon=\"clouds\" temp=\"17\">"
            + "<outlook>unsettled</outlook></today></weather>";
        secondForecast = "<weather><today temp=\"20\"/></weather>";
    }

    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new TestRunner().run(suite());
    }

    /**
     * Return the test suite containing this test
     */
    public static TestSuite suite(){
        return new TestSuite(test_DetailedDiff.class);
    }
}
