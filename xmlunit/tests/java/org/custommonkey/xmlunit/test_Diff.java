/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Test a Diff
 */
public class test_Diff extends TestCase{
    private static final String[] control = new String[]{
        "<test/>",
        "<test></test>",
        "<test>test</test>",
        "<test test=\"test\">test</test>",
        "<test/>",
        "<test>test</test>",
        "<test test=\"test\"/>",
        "<test><test><test></test></test></test>",
        "<test test=\"test\"><test>test<test>test</test></test></test>",
        "<test test=\"test\"><test>test<test>test</test></test></test>",
        "<html>Yo this is a test!</html>",
        "<java></java>"
        };
    private static final String[] test = new String[]{
        "<fail/>",
        "<fail/>",
        "<fail>test</fail>",
        "<test>test</test>",
        "<fail/>",
        "<test>fail</test>",
        "<test test=\"fail\"/>",
        "<test><test><test>test</test></test></test>",
        "<test test=\"test\"><test>fail<test>test</test></test></test>",
        "<test test=\"fail\"><test>test<test>test</test></test></test>",
        "<html>Yo this isn't a test!</html>",
        "<java><package-def><ident>org</ident><dot/><ident>apache</ident><dot/><ident>test</ident></package-def></java>"
        };
    private Document aDocument;

    public void setUp() throws Exception {
        aDocument = XMLUnit.getControlParser().newDocument();
    }

    public void testToString(){
        Diff diff = buildDiff(aDocument, aDocument);
        String[] animals = {"Monkey", "Chicken"};
        String tag = "tag";
        Element elemA = aDocument.createElement(tag);
        Text textA = aDocument.createTextNode(animals[0]);
        elemA.appendChild(textA);

        Element elemB = aDocument.createElement(tag);
        Difference difference = new Difference(DifferenceConstants.HAS_CHILD_NODES,
        	new NodeDetail(Boolean.TRUE.toString(), elemA, "/tag"),
        	new NodeDetail(Boolean.FALSE.toString(),elemB, "/tag"));
        diff.differenceFound(difference);

        assertEquals(diff.getClass().getName() +"\n[different] Expected "
            + DifferenceConstants.HAS_CHILD_NODES.getDescription()
            + " 'true' but was 'false' - comparing <tag...> at /tag to <tag...> at /tag\n",
            diff.toString());

        diff = buildDiff(aDocument, aDocument);
        Text textB = aDocument.createTextNode(animals[1]);
        elemB.appendChild(textB);
        difference = new Difference(DifferenceConstants.TEXT_VALUE, 
        	new NodeDetail(animals[0], textA, "/tag/text()"),
        	new NodeDetail(animals[1], textB, "/tag/text()"));
        diff.differenceFound(difference);

        assertEquals(diff.getClass().getName() +"\n[different] Expected "
            + DifferenceConstants.TEXT_VALUE.getDescription()
            + " 'Monkey' but was 'Chicken' - comparing <tag ...>Monkey</tag> "
            + "at /tag/text() to <tag ...>Chicken</tag> at /tag/text()\n",
            diff.toString());

    }

    /**
     * Tests the compare method
     */
    public void testSimilar() throws Exception {
        for(int i=0;i<control.length;i++){
            assertEquals("XMLUnit.compare().similar() test case "+i+" failed",
                true, buildDiff(control[i], control[i]).similar());
            assertEquals("!XMLUnit.compare().similar() test case "+i+" failed",
                false, (buildDiff(control[i], test[i])).similar());
        }
    }

    public void testIdentical() throws Exception {
        String control="<control><test>test1</test><test>test2</test></control>";
        String test="<control><test>test2</test><test>test1</test></control>";

        assertEquals("Documents are identical, when they are not", false,
            buildDiff(control, test).identical());
    }

    public void testFiles() throws Exception {
        FileReader control = new FileReader(test_Constants.BASEDIR
            + "/tests/etc/test.blame.html");
        FileReader test = new FileReader(test_Constants.BASEDIR
            + "/tests/etc/test.blame.html");
        Diff diff = buildDiff(control, test);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testSameTwoStrings() throws Exception {
        Diff diff = buildDiff("<same>pass</same>", "<same>pass</same>");
        assertEquals("same should be identical", true, diff.identical());
        assertEquals("same should be similar", true, diff.similar());
    }

    public void testMissingElement() throws Exception {
        Diff diff = buildDiff("<root></root>", "<root><node/></root>");
        assertEquals("should not be identical", false, diff.identical());
        assertEquals("and should not be similar", false, diff.similar());
    }

    public void testExtraElement() throws Exception {
        Diff diff = buildDiff("<root><node/></root>", "<root></root>");
        assertEquals("should not be identical", false, diff.identical());
        assertEquals("and should not be similar", false, diff.similar());
    }

    public void testElementsInReverseOrder() throws Exception {
        Diff diff = buildDiff("<root><same/><pass/></root>", "<root><pass/><same/></root>");
        assertEquals("should not be identical", false, diff.identical());
        assertEquals("but should be similar", true, diff.similar());
    }

    public void testMissingAttribute() throws Exception {
        Diff diff = buildDiff("<same>pass</same>", "<same except=\"this\">pass</same>");
        assertEquals("should not be identical", false, diff.identical());
        assertEquals("and should not be similar", false, diff.similar());
    }

    public void testExtraAttribute() throws Exception {
        Diff diff = buildDiff("<same except=\"this\">pass</same>", "<same>pass</same>");
        assertEquals("should not be identical", false, diff.identical());
        assertEquals("and should not be similar", false, diff.similar());
    }    

    public void testAttributesInReverseOrder() throws Exception {
        Diff diff = buildDiff("<same zzz=\"qwerty\" aaa=\"uiop\">pass</same>",
            "<same aaa=\"uiop\" zzz=\"qwerty\">pass</same>" );
        if (diff.identical()) {
            System.out.println(getName() + " - should not ideally be identical "
                + "but JAXP implementations can reorder attributes inside NamedNodeMap");
        }
        assertEquals(diff.toString() + ": but should be similar",
            true, diff.similar());
    }
    
    public void testRepeatedElementNamesWithAttributeQualification1() throws Exception {
    	Diff diff = buildDiff("<root><node id=\"1\"/><node id=\"2\"/></root>", 
    		"<root><node id=\"2\"/><node id=\"1\"/></root>");
    	diff.overrideElementQualifier(new ElementNameAndAttributeQualifier("id"));
    	assertFalse("should not be identical: " + diff.toString(), diff.identical());
    	assertTrue("should be similar: " + diff.toString(), diff.similar());
    }

	public void testRepeatedElementNamesWithAttributeQualification2() throws Exception {
		Diff diff = buildDiff("<root><node id=\"1\" val=\"4\"/><node id=\"2\" val=\"3\"/></root>",
			"<root><node id=\"2\" val=\"4\"/><node id=\"1\" val=\"3\"/></root>");
		diff.overrideElementQualifier(new ElementNameAndAttributeQualifier("id"));
		assertFalse("should not be identical: " + diff.toString(), diff.identical());
		assertFalse("should not be similar: " + diff.toString(), diff.similar());
	}

	public void testRepeatedElementNamesWithAttributeQualification3() throws Exception {
		Diff diff = buildDiff("<root><node id=\"1\" val=\"4\"/><node id=\"2\" val=\"3\"/></root>",
			"<root><node id=\"2\" val=\"3\"/><node id=\"1\" val=\"4\"/></root>");
		diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
		assertFalse("should not be identical: " + diff.toString(), diff.identical());
		assertTrue("should be similar: " + diff.toString(), diff.similar());
	}
    
	public void testRepeatedElementNamesWithAttributeQualification4() throws Exception {
		Diff diff = buildDiff("<root><node id=\"1\" val=\"4\"/><node id=\"2\" val=\"3\"/></root>",
			"<root><node id=\"2\" val=\"4\"/><node id=\"1\" val=\"3\"/></root>");
		diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
		assertFalse("should not be identical: " + diff.toString(), diff.identical());
		assertFalse("should not be similar: " + diff.toString(), diff.similar());
	}
	
	public void testRepeatedElementNamesWithNamespacedAttributeQualification() throws Exception {
		Diff diff = buildDiff("<root xmlns:a=\"http://a.com\" xmlns:b=\"http://b.com\">"
				+ "<node id=\"1\" a:val=\"a\" b:val=\"b\"/><node id=\"2\" a:val=\"a2\" b:val=\"b2\"/></root>",
			"<root xmlns:c=\"http://a.com\" xmlns:d=\"http://b.com\">"
				+ "<node id=\"2\" c:val=\"a2\" d:val=\"b2\"/><node id=\"1\" c:val=\"a\" d:val=\"b\"/></root>");
		diff.overrideElementQualifier(new ElementNameAndAttributeQualifier());
		diff.overrideDifferenceListener(new ExpectedDifferenceListener(
			new int[] {DifferenceConstants.NAMESPACE_PREFIX_ID, DifferenceConstants.CHILD_NODELIST_SEQUENCE_ID}));
		assertFalse("should not be identical: " + diff.toString(), diff.identical());
		assertTrue("should be similar: " + diff.toString(), diff.similar());
	}

	public void testRepeatedElementNamesWithTextQualification() throws Exception {
			Diff diff = buildDiff("<root><node>1</node><node>2</node></root>",
				"<root><node>2</node><node>1</node></root>");
			diff.overrideElementQualifier(new ElementNameAndTextQualifier());
			assertFalse("should not be identical: " + diff.toString(), diff.identical());
			assertTrue("should be similar: " + diff.toString(), diff.similar());
	}
    public void testDiffStringWithAttributes() throws Exception {
        final String fruitBat = "<bat type=\"fruit\"/>",
            longEaredBat = "<bat type=\"longeared\"/>";
        Diff diff = buildDiff(fruitBat, longEaredBat);
        assertEquals(diff.getClass().getName() +"\n[different] Expected "
            + DifferenceConstants.ATTR_VALUE.getDescription()
            + " 'fruit' but was 'longeared' - comparing "
            + "<bat type=\"fruit\"...> at /bat[1]/@type to <bat type=\"longeared\"...> at /bat[1]/@type\n",
            diff.toString());
    }
    
    public void testXMLWithDTD() throws Exception {
        String aDTDpart = "<!DOCTYPE test ["
            + "<!ELEMENT assertion EMPTY>"
            + "<!ATTLIST assertion result (pass|fail) \"fail\">"
            + "<!ELEMENT test (assertion)*>";
        String aDTD = aDTDpart + "]>";
        String xmlWithoutDTD = "<test>"
            + "<assertion result=\"pass\"/>"
            + "<assertion result=\"fail\"/>"
            + "</test>";
        String xmlWithDTD = aDTD + xmlWithoutDTD;
        Diff diff = buildDiff(xmlWithDTD, xmlWithoutDTD);
        assertTrue("similar. " + diff.toString(), diff.similar());
        assertTrue("not identical. " + diff.toString(), !diff.identical());
        
        File tempDtdFile = File.createTempFile(getName(), "dtd");
        tempDtdFile.deleteOnExit();
        FileWriter dtdWriter = new FileWriter(tempDtdFile);
        dtdWriter.write(aDTD);
        try {
            String xmlWithExternalDTD = "<!DOCTYPE test SYSTEM \"" 
                + tempDtdFile.toURL().toExternalForm() + "\">"
                + xmlWithoutDTD;
            diff = buildDiff(xmlWithDTD, xmlWithExternalDTD);
            assertTrue("similar again. " + diff.toString(), diff.similar());
            assertTrue("not identical again. " + diff.toString(), !diff.identical());             
        } finally {
            tempDtdFile.delete();
        }

        String anotherDTD = aDTDpart 
            + "<!ELEMENT comment (ANY)>" + "]>";            
        String xmlWithAnotherDTD = anotherDTD + xmlWithoutDTD;
        diff = buildDiff(xmlWithDTD, xmlWithAnotherDTD);
        assertTrue("similar. " + diff.toString(), diff.similar());
        assertTrue("amd identical as DTD content is not compared. " + diff.toString(), diff.identical());        
    }

    /**
     * Raised by aakture 25.04.2002
     * Despite the name under which this defect was raised the issue is really
     * about managing redundant whitespace
     */
    public void testXMLUnitDoesNotWorkWellWithFiles() throws Exception {
        // to avoid test sequencing issues we need to restore whitespace setting
        boolean startValueIgnoreWhitespace = XMLUnit.getIgnoreWhitespace();
        try {
            XMLUnit.setIgnoreWhitespace(false);
            Diff whitespaceAwareDiff = buildDiff(test_Constants.XML_WITHOUT_WHITESPACE,
                test_Constants.XML_WITH_WHITESPACE);
            assertTrue(whitespaceAwareDiff.toString(),
                !whitespaceAwareDiff.similar());

            XMLUnit.setIgnoreWhitespace(true);
            Diff whitespaceIgnoredDiff = buildDiff(test_Constants.XML_WITHOUT_WHITESPACE,
                test_Constants.XML_WITH_WHITESPACE);
            assertTrue(whitespaceIgnoredDiff.toString(),
                whitespaceIgnoredDiff.similar());
        } finally {
            XMLUnit.setIgnoreWhitespace(startValueIgnoreWhitespace);
        }
    }

    /**
     * Raised 15.05.2002
     */
    public void testNamespaceIssues() throws Exception {
        String control = "<control:abc xmlns:control=\"http://yada.com\">"
            + "<control:xyz>text</control:xyz></control:abc>";
        Replacement replace = new Replacement("control", "test");
        String test = replace.replace(control);

        Diff diff = buildDiff(control, test);
        assertTrue("a-"+diff.toString(), diff.similar());
        assertTrue("b-"+diff.toString(), !diff.identical());

        Diff reverseDiff = buildDiff(test, control);
        assertTrue("c-"+reverseDiff.toString(), reverseDiff.similar());
        assertTrue("d-"+reverseDiff.toString(), !reverseDiff.identical());
    }

    /**
     * Raised 16.05.2002
     */
    public void testDefaultNamespace() throws Exception {
        String control = "<control:abc xmlns:control=\"http://yada.com\">"
            + "<control:xyz>text</control:xyz></control:abc>";
        Replacement replace = new Replacement("control:", "");
        Replacement trim = new Replacement("xmlns:control", "xmlns");
        String test = trim.replace(replace.replace(control));

        Diff diff = buildDiff(control, test);
        assertTrue("a-"+diff.toString(), diff.similar());
        assertTrue("b-"+diff.toString(), !diff.identical());

        Diff reverseDiff = buildDiff(test, control);
        assertTrue("c-"+reverseDiff.toString(), reverseDiff.similar());
        assertTrue("d-"+reverseDiff.toString(), !reverseDiff.identical());
    }

    public void testSameNameDifferentQName() throws Exception {
        String control = "<ns1:root xmlns:ns1=\"http://example.org/ns1\" xmlns:ns2=\"http://example.org/ns2\">"
            + "<ns1:branch>In namespace 1</ns1:branch>"
            + "<ns2:branch>In namespace 2</ns2:branch>"
            + "</ns1:root>";

        String test = "<ns1:root xmlns:ns1=\"http://example.org/ns1\" xmlns:ns2=\"http://example.org/ns2\">"
            + "<ns2:branch>In namespace 2</ns2:branch>"
            + "<ns1:branch>In namespace 1</ns1:branch>"
            + "</ns1:root>";

        Diff diff = buildDiff(control, test);
        assertTrue("a-"+diff.toString(), diff.similar());
        assertTrue("b-"+diff.toString(), !diff.identical());

        Diff reverseDiff = buildDiff(test, control);
        assertTrue("c-"+reverseDiff.toString(), reverseDiff.similar());
        assertTrue("d-"+reverseDiff.toString(), !reverseDiff.identical());
    }
    
    public void testNamespacedAttributes() throws Exception {
    	FileReader control = new FileReader(test_Constants.BASEDIR
    		+ "/tests/etc/controlNamespaces.xml");
    	FileReader test = new FileReader(test_Constants.BASEDIR
    		+ "/tests/etc/testNamespaces.xml");
    	Diff diff = buildDiff(control, test);
    	diff.overrideDifferenceListener(
    		new ExpectedDifferenceListener(DifferenceConstants.NAMESPACE_PREFIX_ID));
    	assertEquals(diff.toString(), false, diff.identical());
    	assertEquals(diff.toString(), true, diff.similar());
    }
    
    public void testOverrideDifferenceListener() throws Exception {
        String control = "<vehicles><car colour=\"white\">ford fiesta</car>"
            +"<car colour=\"red\">citroen xsara</car></vehicles>";
        String test = "<vehicles><car colour=\"white\">nissan primera</car>"
            +"<car colour=\"blue\">peugot 206</car></vehicles>";
        Diff diff = buildDiff(control, test);
        assertTrue("initially " + diff.toString(), 
            !diff.similar());
        
        Diff diffWithIdenticalOverride = buildDiff(control, test);
        diffWithIdenticalOverride.overrideDifferenceListener(
            new OverrideDifferenceListener(
                DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL));
        assertTrue("now identical" 
            + diffWithIdenticalOverride.toString(),
            diffWithIdenticalOverride.identical());
        
        Diff diffWithSimilarOverride = buildDiff(control, test);
        diffWithSimilarOverride.overrideDifferenceListener(
            new OverrideDifferenceListener(
                DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR));
        assertTrue("no longer identical" 
            + diffWithSimilarOverride.toString(),
            !diffWithSimilarOverride.identical());
        assertTrue("but still similar" 
            + diffWithSimilarOverride.toString(),
            diffWithSimilarOverride.similar());
        
        Diff diffWithOverride = buildDiff(control, test);
        diffWithOverride.overrideDifferenceListener(
            new OverrideDifferenceListener(
                DifferenceListener.RETURN_ACCEPT_DIFFERENCE));
        assertTrue("default behaviour" 
            + diffWithOverride.toString(),
            !diffWithOverride.similar());
    }
    
    public void testDifferentStructure() throws Exception {
    	String control = "<root><node>text</node></root>";
    	String test = "<root><node><inner-node>text</inner-node></node></root>";
    	Diff myDiff = buildDiff(control, test);
    	assertEquals(myDiff.toString(), false, myDiff.similar());
    }
    	
    protected Diff buildDiff(Document control, Document test) {
        return new Diff(control, test);
    }

    protected Diff buildDiff(String control, String test) throws Exception {
        return new Diff(control, test);
    }

    protected Diff buildDiff(Reader control, Reader test) throws Exception {
        return new Diff(control, test);
    }

    /**
     * Construct a test
     * @param name Test name
     */
    public test_Diff(String name){
        super(name);
    }

    private class OverrideDifferenceListener implements DifferenceListener {
        private final int overrideValue;
        private OverrideDifferenceListener(int overrideValue) {
            this.overrideValue = overrideValue;
        }
        public int differenceFound(Difference difference) {
            return overrideValue;
        }
        public void skippedComparison(Node control, Node test) {
        }
    }
    
    private class ExpectedDifferenceListener implements DifferenceListener {
	private final Set expectedIds;
	private ExpectedDifferenceListener(int expectedIdValue) {
		this(new int[] {expectedIdValue});
	}
	private ExpectedDifferenceListener(int[] expectedIdValues) {
		this.expectedIds = new HashSet(expectedIdValues.length);
		for (int i=0; i < expectedIdValues.length; ++i) {
			expectedIds.add(new Integer(expectedIdValues[i]));
		}
	}
	public int differenceFound(Difference difference) {
		assertTrue(difference.toString(), expectedIds.contains(new Integer(difference.getId())));
		return RETURN_ACCEPT_DIFFERENCE;
	}
	public void skippedComparison(Node control, Node test) {
	}
}
}
