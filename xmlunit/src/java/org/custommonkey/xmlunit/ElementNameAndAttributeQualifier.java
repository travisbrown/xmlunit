/*
******************************************************************
Copyright (c) 2001, Jeff Martin, Tim Bacon
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

import org.w3c.dom.Element;

/**
 * More complex interface implementation that tests two elements for tag name
 * and attribute name comparability. 
 * <br />Examples and more at 
 * <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 * @see DifferenceEngine#compareNodeList(NodeList, NodeList, int, DifferenceListener)
 * @see Diff#overrideElementQualifier(ElementQualifier)
 */
public class ElementNameAndAttributeQualifier extends ElementNameQualifier {
	public static final String ALL_ATTRIBUTES = "*";
	
	private final String[] qualifyingAttrNames;
	
	/**
	 * No-args constructor: use all attributes from all elements to determine
	 * whether elements qualify for comparability
	public ElementNameAndAttributeQualifier() {
		this(ALL_ATTRIBUTES);
	}
	 */
	
	/**
	 * Simple constructor for a single qualifying attribute name
	 * @param attrName the value to use to qualify whether two elements can be
	 * compared further for differences
	 */
	public ElementNameAndAttributeQualifier(String attrName) {
		this(new String[] {attrName});
	}

	/**
	 * Extended constructor for multiple qualifying attribute names
	 * @param attrNames the array of values to use to qualify whether two
	 * elements can be compared further for differences
	 */
	public ElementNameAndAttributeQualifier(String[] attrNames) {
		this.qualifyingAttrNames = attrNames;
	}

	/**
	 * Determine whether two elements qualify for further Difference comparison.
	 * @param differenceEngine the DifferenceEngine instance wanting to
	 * determine if the elements are comparable
	 * @param control
	 * @param test
	 * @return true if the two elements qualify for further comparison based on
	 * both the superclass qualification (namespace URI and non- namespaced tag
	 * name), and the presence of qualifying attributes with the same values;
	 * false otherwise
	 */
	public boolean qualifyForComparison(Element control, Element test) {
		if (super.qualifyForComparison(control, test)) {
			return areAttributesComparable(control, test);
		}
		return false;
	}
	
	/**
	 * Determine whether the qualifying attributes are present in both elements
	 * and if so whether their values are the same
	 * @param control
	 * @param test
	 * @return true if all qualifying attributes are present with the same
	 * values, false otherwise
	 */
	protected boolean areAttributesComparable(Element control, Element test) {
		String controlValue, testValue;
		for (int i=0; i < qualifyingAttrNames.length; ++i) {
			controlValue = control.getAttribute(qualifyingAttrNames[i]);
			testValue = test.getAttribute(qualifyingAttrNames[i]);
			if (controlValue == null) {
				if (testValue != null) {
					return false;
				}
			} else {
				if (!controlValue.equals(testValue)) {
					return false;
				}
			}
		}
		return true;
	}

}
