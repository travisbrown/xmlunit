package org.custommonkey.xmlunit;

/**
 * A convenient place to hang constants relating to general XML usage
 */
public interface XMLConstants {

    /**
     * &lt;?xml&gt; declaration
     */
    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>";

    /**
     * xmlns attribute prefix
     */
    public static final String XMLNS_PREFIX = "xmlns";

    /**
     * "&lt;"
     */
    public static final String OPEN_START_NODE = "<";

    /**
     * "&lt;/"
     */
    public static final String OPEN_END_NODE = "</";

    /**
     * "&gt;"
     */
    public static final String CLOSE_NODE = ">";

    /**
     * "![CDATA["
     */
    public static final String START_CDATA = "![CDATA[";
    /**
     * "]]"
     */
    public static final String END_CDATA = "]]";

    /**
     * "!--"
     */
    public static final String START_COMMENT = "!--";
    /**
     * "--""
     */
    public static final String END_COMMENT = "--";

    /**
     * "?"
     */
    public static final String START_PROCESSING_INSTRUCTION = "?";
    /**
     * "?"
     */
    public static final String END_PROCESSING_INSTRUCTION = "?";

    /**
     * "!DOCTYPE"
     */
    public static final String START_DOCTYPE = "!DOCTYPE ";
}
