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
 * Value object that describes a difference between DOM Nodes.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Difference {
    /** Simple unique identifier */
    private final int id;
    /** Description of the difference */
    private final String description;
    /** TRUE if the difference represents a similarity, FALSE otherwise */
    private final boolean recoverable;

    /**
     * Constructor for non-similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description) {
        this(id, description, false);
    }

    /**
     * Constructor for similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description, boolean recoverable) {
        this.id = id;
        this.description = description;
        this.recoverable = recoverable;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return TRUE if the difference represents a similarity, FALSE otherwise
     */
    public boolean isRecoverable() {
        return recoverable;
    }

    /**
     * @return a basic representation of the object state and identity
     */
    public String toString() {
        return new StringBuffer(super.toString())
            .append(" (#").append(id)
            .append(") ").append(description)
            .toString();
    }
}