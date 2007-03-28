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

/**
 * Contains some common code for DoctypeReader and DoctypeInputStream.
 *
 * <p>When used with DoctypeInputStream it assumes that the whole
 * DOCTYPE declaration consists of US-ASCII characters.</p>
 */
final class DoctypeSupport implements DoctypeConstants {

    private final String decl;
    private int offset = 0;

    /**
     * Encapsulates a DOCTYPE declaration for the given name and system id.
     */
    DoctypeSupport(String name, String systemId) {
        StringBuffer sb = new StringBuffer(DOCTYPE_OPEN_DECL);
        sb.append(DOCTYPE).append(name).append(SYSTEM)
            .append(systemId).append("\"").append(DOCTYPE_CLOSE_DECL);
        decl = sb.toString();
    }

    /**
     * Whether anybody has started to read the declaration.
     */
    boolean hasBeenRead() {
        return offset != 0;
    }

    /**
     * Reads the next character from the declaration.
     * @return -1 if the end of the declaration has been reached.
     */
    int read() {
        return offset >= decl.length() ? -1 : decl.charAt(offset++);
    }
}