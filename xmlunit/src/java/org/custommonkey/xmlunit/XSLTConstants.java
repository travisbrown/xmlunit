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
 * A convenient place to hang constants relating to XSL transformations
 */
public interface XSLTConstants extends XMLConstants {
    /**
     * &lt;xsl:stylesheet&gt;
     */
    public static final String XSLT_START =
        "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">";

    /**
     * &lt;xsl:output&gt; for XML with no indentation
     */
    public static final String XSLT_XML_OUTPUT_NOINDENT =
        "<xsl:output method=\"xml\" version=\"1.0\" indent=\"no\"/>";

    /**
     * &lt;xsl:strip-space&gt; for all elements
     */
    public static final String XSLT_STRIP_WHITESPACE =
        "<xsl:strip-space elements=\"*\"/>";

    /**
     * &lt;xsl:template&gt; to copy the current nodeset into the output tree
     */
    public static final String XSLT_IDENTITY_TEMPLATE =
        "<xsl:template match=\"/\"><xsl:copy-of select=\".\"/></xsl:template>";

    /**
     * &lt;/xsl:stylesheet&gt;
     */
    public static final String XSLT_END = "</xsl:stylesheet>";
}
