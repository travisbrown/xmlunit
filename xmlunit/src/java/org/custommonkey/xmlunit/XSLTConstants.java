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
