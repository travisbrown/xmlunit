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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Adapts the marked-up content in a source Reader to specify that it
 * conforms to a different DTD.
 * Combines Reader semantics with the ability to specify a target doctype
 * for a character stream containing XML markup.
 * Used by Validator class to wrap a Reader when performing validation of a
 * document against a DTD.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DoctypeReader extends Reader {
    private static final String DOCTYPE_OPEN_DECL = "<!";
    private static final int DECL_LENGTH = DOCTYPE_OPEN_DECL.length();
    private static final String DOCTYPE_CLOSE_DECL = ">";
    private static final String DOCTYPE = "DOCTYPE ";
    private static final String SYSTEM = " SYSTEM \"";
    private final Reader originalSource;
    private final StringBuffer sourceBuffer = new StringBuffer(1024);
    private final String doctypeName;
    private final String systemId;

    private Reader replacementReader;

    /**
     * Create a Reader whose XML content is provided by the originalSource with
     * the exception of the DOCTYPE which is provided by the doctypeName
     * and systemID.
     * @param originalSource
     * @param doctypeName
     * @param systemID
     */
    public DoctypeReader(Reader originalSource, String doctypeName,
    String systemID) {
        this.originalSource = originalSource;
        this.doctypeName = doctypeName;
        this.systemId = systemID;
    }

    /**
     * @return the content of the original source, without amendments or
     *  substitutions. Safe to call multiple times.
     * @throws IOException if thrown while reading from the original source
     */
    protected String getContent() throws IOException {
        return getContent(originalSource).toString();
    }

    /**
     * @param originalSource
     * @return the contents of the originalSource within a StringBuffer
     * @throws IOException if thrown while reading from the original source
     */
    private StringBuffer getContent(Reader originalSource) throws IOException {
        if (sourceBuffer.length() == 0) {
            BufferedReader bufferedReader;
            if (originalSource instanceof BufferedReader) {
                bufferedReader = (BufferedReader) originalSource;
            } else {
                bufferedReader = new BufferedReader(originalSource);
            }
            String newline = System.getProperty("line.separator");
            String source;
            boolean atFirstLine = true;
            while ((source = bufferedReader.readLine()) != null) {
                if (atFirstLine) {
                    atFirstLine = false;
                } else {
                    sourceBuffer.append(newline);
                }
                sourceBuffer.append(source);
            }

            bufferedReader.close();
        }

        return sourceBuffer;
    }

    /**
     * Determine where to place the DOCTYPE declaration within some marked-up
     * content
     * @param withinContent
     * @return
     */
    private int findStartDoctype(StringBuffer withinContent) {
        int startAt = -1;
        char curChar;
        boolean canInsert = true;
        for (int i = 0; startAt == -1; ++i) {
            curChar = withinContent.charAt(i);
            if (curChar == '<') {
                switch (withinContent.charAt(i + 1)) {
                    case '?':
                    case '!':
                    case '-':
                        canInsert = false;
                        break;
                    default:
                        startAt = i;
                }
            } else if (curChar == '>') {
                canInsert = true;
            } else if (canInsert) {
                startAt = i;
            }
        }
        return startAt;
    }

    /**
     * Perform DOCTYPE amendment / addition within some marked-up content
     * @param withinContent
     * @param doctypeName
     * @param systemId
     * @return the content, after DOCTYPE amendment / addition
     */
    public String replaceDoctype(StringBuffer withinContent,
    String doctypeName, String systemId) {
        String content = withinContent.toString();
        int startDoctype = content.indexOf(DOCTYPE);
        boolean noCurrentDoctype = false;
        if (startDoctype == -1) {
            startDoctype = findStartDoctype(withinContent);
            noCurrentDoctype = true;
        }

        int endDoctype = startDoctype + DOCTYPE.length();

        if (noCurrentDoctype) {
            withinContent.insert(startDoctype, DOCTYPE_OPEN_DECL);
            withinContent.insert(startDoctype + DECL_LENGTH, DOCTYPE);
            endDoctype += DECL_LENGTH;
        } else {
            int startInternalDecl = content.indexOf('[', endDoctype);
            if (startInternalDecl > 0) {
                int endInternalDecl = content.indexOf(']', startInternalDecl);
                withinContent.delete(endDoctype, endInternalDecl + 1);
            } else {
                int endDoctypeTag = content.indexOf('>', endDoctype);
                withinContent.delete(endDoctype, endDoctypeTag);
            }
        }

        int atPos = endDoctype;
        withinContent.insert(atPos, doctypeName);
        atPos += doctypeName.length();
        withinContent.insert(atPos, SYSTEM);
        atPos += SYSTEM.length();
        withinContent.insert(atPos, systemId);
        atPos += systemId.length();
        withinContent.insert(atPos, '"');

        if (noCurrentDoctype) {
            withinContent.insert(++atPos, DOCTYPE_CLOSE_DECL);
        }
        return withinContent.toString();
    }

    /**
     * Wrap the DOCTYPE-replaced content in a StringReader
     * @return a StringReader from which the DOCTYPE-replaced content can be read
     * @throws IOException
     */
    private Reader getReplacementReader() throws IOException {
        StringBuffer originalContent = getContent(originalSource);
        String replacedContent = replaceDoctype(originalContent,
            doctypeName, systemId);
        return new StringReader(replacedContent);
    }

    /**
     * Read DOCTYPE-replaced content from the wrapped Reader
     * @param cbuf
     * @param off
     * @param len
     * @return The number of characters read, or -1 if the end of the
     *  stream has been reached
     * @throws IOException
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        if (replacementReader == null) {
            replacementReader = getReplacementReader();
        }
        return replacementReader.read(cbuf, off, len);
    }

    /**
     * Close the wrapped Reader
     * @throws IOException
     */
    public void close() throws IOException {
        replacementReader.close();
    }
}
