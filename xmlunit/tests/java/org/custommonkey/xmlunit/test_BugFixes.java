package org.custommonkey.xmlunit;

import java.io.File;
import javax.xml.transform.OutputKeys;
import junit.framework.*;
import junit.textui.TestRunner;

/**
 * Regression test class for various bug fixes
 */
public class test_BugFixes extends TestCase {
    public void setUp() throws Exception {
        XMLUnit.setControlParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit.setTestParser("org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        XMLUnit.setSAXParserFactory("org.apache.xerces.jaxp.SAXParserFactoryImpl");
        XMLUnit.setTransformerFactory("org.apache.xalan.processor.TransformerFactoryImpl");
    }

    public test_BugFixes(String name) {
        super(name);
    }

    /**
     * Handy dandy main method to run this suite with text-based TestRunner
     */
    public static void main(String[] args) {
        new TestRunner().run(suite());
    }

    /**
     * Return the test suite containing the bug fix tests
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new test_XMLUnit("testStripWhitespaceTransform"));
        suite.addTest(new test_Diff("testXMLUnitDoesNotWorkWellWithFiles"));
        suite.addTest(new test_Transform("testXSLIncludeWithoutSystemId"));
        suite.addTest(new test_Diff("testNamespaceIssues"));
        suite.addTest(new test_Diff("testDefaultNamespace"));
        return suite;
    }
}
