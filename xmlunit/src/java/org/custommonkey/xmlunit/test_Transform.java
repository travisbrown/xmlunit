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

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.File;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

/**
 * Test a Transform
 */
public class test_Transform extends TestCase{
    private static final String FLEABALL = "<fleaball><animal><shaggy>dog</shaggy></animal></fleaball>";

    private static final String DOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + test_Constants.LINE_SEPARATOR + "<dog/>" ;

    private Transform transform;

    public void testGetResultString() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        assertEquals(DOG, transform.getResultString());
    }

    public void testGetResultDocument() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        Diff diff = new Diff(DOG, transform);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testIdentityTransform() throws Exception {
        Document control = XMLUnit.buildControlDocument(FLEABALL);
        DOMSource source = new DOMSource(control);
        transform = new Transform(source);
        Document test = transform.getResultDocument();
        Diff diff = new Diff(control, test);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testOutputProperty() throws Exception {
        transform = new Transform(FLEABALL,
            new File(test_Constants.BASEDIR + "/tests/etc/animal.xsl"));
        transform.setOutputProperty(OutputKeys.METHOD, "html");
        assertNotEquals(DOG, transform.getResultString());
    }

    public void assertNotEquals(Object expected, Object actual) {
        if (expected.equals(actual)) {
            fail("Expected " + expected + " different to actual!");
        }
    }

    public test_Transform(String name) {
        super(name);
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
        return new TestSuite(test_Transform.class);
    }
}
