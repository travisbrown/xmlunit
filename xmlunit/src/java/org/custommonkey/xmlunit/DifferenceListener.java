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

import org.w3c.dom.Node;

/**
 * Listener for callbacks from a
 * {@link DifferenceEngine#compare DifferenceEngine comparison}.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public interface DifferenceListener {
    /** 
     * Standard return value for the <code>differenceFound</code> method.
     * Indicates that the <code>Difference</code> is interpreted as defined 
     * in {@link DifferenceConstants DifferenceConstants}.
     */
    public final int RETURN_ACCEPT_DIFFERENCE = 0;
    /** 
     * Override return value for the <code>differenceFound</code> method.
     * Indicates that the nodes identified as being different should be 
     * interpreted as being identical.
     */
    public final int RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL = 1;
    /** 
     * Override return value for the <code>differenceFound</code> method.
     * Indicates that the nodes identified as being different should be 
     * interpreted as being similar.
     */
    public final int RETURN_IGNORE_DIFFERENCE_NODES_SIMILAR = 2;
    /**
     * Receive notification that 2 nodes are different.
     * @param expected the control node value being compared
     * @param actual the test node value being compared
     * @param control the control node being compared
     * @param test the test node being compared
     * @param difference one of the constant Differfence instances defined in
     *  {@link DifferenceConstants DifferenceConstants} describing the
     *  cause of the difference
     * @return int one of the RETURN_... constants describing how this difference
     *  was interpreted
     */
    public int differenceFound(String expected, String actual,
        Node control, Node test, Difference difference);

    /**
     * Receive notification that a comparison between 2 nodes has been skipped
     *  because the node types are not comparable by the DifferenceEngine
     * @param control the control node being compared
     * @param test the test node being compared
     * @see DifferenceEngine
     */
    public void skippedComparison(Node control, Node test);

    /**
     * Determine whether a Difference that this listener has been notified of
     *  should halt further XML comparison. Default behaviour for a Diff
     *  instance is to halt if the Difference is not recoverable.
     * @see Difference#isRecoverable
     * @param afterDifference the last Difference passed to <code>differenceFound</code>
     * @return true to halt further comparison, false otherwise
     */
    public boolean haltComparison(Difference afterDifference);
}