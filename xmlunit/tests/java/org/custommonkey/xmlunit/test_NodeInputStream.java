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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import org.w3c.dom.Document;

import junit.framework.*;
import junit.textui.TestRunner;

/**
 * JUnit test for NodeInputStream
 */
public class test_NodeInputStream extends TestCase {
    private NodeInputStream nodeStream;
    private final String frog = "";
    private final String ribbit =  "<frog><!-- eats slugs and snails -->"
        + "<frogspawn>fertilised egg</frogspawn>"
        + "<tadpole juvenile=\"true\"/></frog>";

    public void setUp() throws Exception {
        Document document = XMLUnit.buildControlDocument(ribbit);
        nodeStream = new NodeInputStream(document);
    }

    public void testRead() throws Exception {
        InputStreamReader reader = new InputStreamReader(nodeStream);

        Diff diff = new Diff(new StringReader(ribbit), reader);
        assertEquals(diff.toString(), true, diff.identical());
    }

    public void testAvailable() throws Exception {
        int available = nodeStream.available();
        assertEquals("available="+available, true, available > 0);

        nodeStream.read();
        assertEquals(available - 1, nodeStream.available());
    }

    public test_NodeInputStream(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_NodeInputStream.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

