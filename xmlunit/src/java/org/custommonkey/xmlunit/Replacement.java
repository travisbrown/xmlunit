package org.custommonkey.xmlunit;

/**
 * Performs replacement of one String by another String
 *  within one or more Strings.
 * This was required but a code refactoring made it redundant and I don't have
 *  the heart to kill it off...!
 */
public class Replacement {
    private final char[] ofChars;
    private final char[] byChars;

    public Replacement(String ofString, String byString) {
        this(ofString.toCharArray(), byString.toCharArray());
    }

    public Replacement(char[] ofChars, char[] byChars) {
        this.ofChars = ofChars;
        this.byChars = byChars;
    }

    public final String replace(String inString) {
        StringBuffer buf =  replaceAndAppend(inString.toCharArray(),
            new StringBuffer(inString.length()));

        return buf.toString();
    }

    public final char[] replace(char[] inChars) {
        StringBuffer buf = replaceAndAppend(inChars,
            new StringBuffer(inChars.length));

        char[] replacement = new char[buf.length()];
        buf.getChars(0, buf.length(), replacement, 0);

        return replacement;
    }

    public final StringBuffer replaceAndAppend(char[] inChars,
    StringBuffer toBuffer) {
        int ofPos = 0;
        int falseStartPos = -1;
        for (int i=0; i < inChars.length; ++i) {
            if (inChars[i] == ofChars[ofPos]) {
                if (falseStartPos == -1) {
                    falseStartPos = i;
                }
                ++ofPos;
            } else {
                ofPos = 0;
                if (falseStartPos != -1) {
                    for (; falseStartPos < i; ++falseStartPos) {
                        toBuffer.append(inChars[falseStartPos]);
                    }
                    falseStartPos = -1;
                }
                toBuffer.append(inChars[i]);
            }

            if (ofPos == ofChars.length) {
                toBuffer.append(byChars);
                ofPos = 0;
                falseStartPos = -1;
            }
        }

        return toBuffer;
    }
}
