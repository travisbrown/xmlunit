package org.custommonkey.xmlunit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author tbacon
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class test_ElementNameAndAttributeQualifier extends TestCase {
	private Document document;
	private ElementNameAndAttributeQualifier elementNameAndAttributeQualifier;
	private static final String TAG_NAME = "qwerty";
	
	public void testSingleQualifyingAttribute() throws Exception {
		String attrName = "id";
		elementNameAndAttributeQualifier = new ElementNameAndAttributeQualifier(attrName); 
		
		Element control = document.createElement(TAG_NAME);
		control.setAttribute(attrName, "1");

		Element test = document.createElement(TAG_NAME);
		assertFalse("qwerty id 1 not comparable to qwerty with no attributes",
			elementNameAndAttributeQualifier.areComparable(control, test));
		
		test.setAttribute(attrName, "1");
		assertTrue("qwerty id 1 comparable to qwerty id 1", 
			elementNameAndAttributeQualifier.areComparable(control, test));
			
		control.setAttribute("uiop","true");
		assertTrue("qwerty id 1 && uiop comparable to qwerty id 1",
			elementNameAndAttributeQualifier.areComparable(control, test));
					
		test.setAttribute("uiop", "false");
		assertTrue("qwerty id 1 && uiop comparable to qwerty id 1 && !uiop",
			elementNameAndAttributeQualifier.areComparable(control, test));

		test.setAttribute(attrName, "2");
		assertFalse("qwerty id 1 && uiop NOT comparable to qwerty id 2 && !uiop",
			elementNameAndAttributeQualifier.areComparable(control, test));	
	}
	
	public void testMultipleQualifyingAttributes() throws Exception {
		String[] attrNames = {"id", "uid"};
		elementNameAndAttributeQualifier = new ElementNameAndAttributeQualifier(attrNames);

		Element control = document.createElement(TAG_NAME);
		for (int i=0; i < attrNames.length; ++i) {
			control.setAttribute(attrNames[i], "1");
		}

		Element test = document.createElement(TAG_NAME);
		assertFalse("qwerty id/uid 1 not comparable to qwerty with no attributes",
			elementNameAndAttributeQualifier.areComparable(control, test));
			
		for (int i=0; i < attrNames.length; ++i) {
			test.setAttribute(attrNames[i], "1");
		}

		assertTrue("qwerty id/uid 1 comparable to qwerty id/uid 1",
			elementNameAndAttributeQualifier.areComparable(control, test));
		
		test.setAttribute("oid", "0x2394b3456df");
		assertTrue("qwerty id/uid 1 comparable to qwerty id/uid 1 with oid",
			elementNameAndAttributeQualifier.areComparable(control, test));

		control.setAttribute("oid", "0xfd6543b4932");
		assertTrue("qwerty id/uid 1 with oid comparable to qwerty id/uid 1 with different oid",
			elementNameAndAttributeQualifier.areComparable(control, test));
		
		test.setAttribute(attrNames[0], "2");
		assertFalse("qwerty id/uid 1 not comparable to qwerty id 2 /uid 1",
			elementNameAndAttributeQualifier.areComparable(control, test));

		test.setAttribute(attrNames[0], "1");
		test.setAttribute(attrNames[1], "2");
		assertFalse("qwerty id/uid 1 not comparable to qwerty id 1 /uid 2",
			elementNameAndAttributeQualifier.areComparable(control, test));

		test.setAttribute(attrNames[0], "2");
		assertFalse("qwerty id/uid 1 not comparable to qwerty id/uid 2",
			elementNameAndAttributeQualifier.areComparable(control, test));
	}	

	public void setUp() throws Exception {
		document = XMLUnit.getControlParser().newDocument();
	}
	
	public static TestSuite suite() {
		return new TestSuite(test_ElementNameAndAttributeQualifier.class);
	}
	
	/**
	 * Constructor for test_ElementNameAndAttributeQualifier.
	 * @param name
	 */
	public test_ElementNameAndAttributeQualifier(String name) {
		super(name);
	}

}
