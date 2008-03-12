/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
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
package org.custommonkey.xmlunit.examples;

import java.util.Locale;
import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class test_CaseInsensitiveDifferenceListener extends TestCase {

    private static final String ATTR = "aTtr";
    private static final String CDATA = "C Data";
    private static final String CMMT = "a Comment";
    private static final String TEXT = "some Text";

    public void testCaseInsensitive() throws Exception {
        String control = getDoc(ATTR, CDATA, CMMT, TEXT);
        String test = getDoc(ATTR.toUpperCase(Locale.US),
                             CDATA.toUpperCase(Locale.US),
                             CMMT.toUpperCase(Locale.US),
                             TEXT.toUpperCase(Locale.US));
        Diff d = new Diff(control, test);

        CaseInsensitiveDifferenceListener c =
            new CaseInsensitiveDifferenceListener(new DifferenceListener() {
                    public int differenceFound(Difference d) {
                        fail("differenceFound shouldn't get invoked, but"
                             + " was with type " + d.getId());
                        return -42;
                    }
                    public void skippedComparison(Node c, Node t) {
                        fail("skippedComparison shouldn't get invoked");
                    }
                });

        d.overrideDifferenceListener(c);
        assertTrue(d.identical());
    }

    private static String getDoc(String attr, String cdata, String comment,
                                 String text) {
        return "<root><first attr=\"" + attr + "\"/><!--" + comment + "-->"
            + "<second><![CDATA[" + cdata + "]]></second><third>" + text
            + "</third></root>";
    }
}