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


/**
 * Constants for describing differences between DOM Nodes.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public interface DifferenceConstants {
    /** Comparing an implied attribute value against an explicit value */
    public static final Difference ATTR_VALUE_EXPLICITLY_SPECIFIED =
        new Difference(1, "attribute value explicitly specified", true);

    /** Comparing 2 elements and one has an attribute the other does not */
    public static final Difference ATTR_NAME_NOT_FOUND =
        new Difference(2, "attribute name");

    /** Comparing 2 attributes with the same name but different values */
    public static final Difference ATTR_VALUE =
        new Difference(3, "attribute value");

    /** Comparing 2 attribute lists with the same attributes in different sequence */
    public static final Difference ATTR_SEQUENCE =
        new Difference(4, "sequence of attributes", true);

    /** Comparing 2 CDATA sections with different values */
    public static final Difference CDATA_VALUE =
        new Difference(5, "CDATA section value");

    /** Comparing 2 comments with different values */
    public static final Difference COMMENT_VALUE =
        new Difference(6, "comment value");

    /** Comparing 2 document types with different names */
    public static final Difference DOCTYPE_NAME =
        new Difference(7, "doctype name");

    /** Comparing 2 document types with different public identifiers */
    public static final Difference DOCTYPE_PUBLIC_ID =
        new Difference(8, "doctype public identifier");

    /** Comparing 2 document types with different system identifiers */
    public static final Difference DOCTYPE_SYSTEM_ID =
        new Difference(9, "doctype system identifier", true);

    /** Comparing 2 elements with different tag names */
    public static final Difference ELEMENT_TAG_NAME =
        new Difference(10, "element tag name");

    /** Comparing 2 elements with different number of attributes */
    public static final Difference ELEMENT_NUM_ATTRIBUTES =
        new Difference(11, "number of element attributes");

    /** Comparing 2 processing instructions with different targets */
    public static final Difference PROCESSING_INSTRUCTION_TARGET =
        new Difference(12, "processing instruction target");

    /** Comparing 2 processing instructions with different instructions */
    public static final Difference PROCESSING_INSTRUCTION_DATA =
        new Difference(13, "processing instruction data");

    /** Comparing 2 different text values */
    public static final Difference TEXT_VALUE =
        new Difference(14, "text value");

    /** Comparing 2 nodes with different namespace prefixes */
    public static final Difference NAMESPACE_PREFIX =
        new Difference(15, "namespace prefix", true);

    /** Comparing 2 nodes with different namespace URIs */
    public static final Difference NAMESPACE_URI =
        new Difference(16, "namespace URI");

    /** Comparing 2 nodes with different node types */
    public static final Difference NODE_TYPE =
        new Difference(17, "node type");

    /** Comparing 2 nodes but only one has any children*/
    public static final Difference HAS_CHILD_NODES =
        new Difference(18, "presence of child nodes to be");

    /** Comparing 2 nodes with different numbers of children */
    public static final Difference CHILD_NODELIST_LENGTH =
        new Difference(19, "number of child nodes");

    /** Comparing 2 nodes with children whose nodes are in different sequence*/
    public static final Difference CHILD_NODELIST_SEQUENCE =
        new Difference(20, "sequence of child nodes", true);
}

