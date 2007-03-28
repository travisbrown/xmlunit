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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Adapts the marked-up content in a source InputStream to specify that it
 * conforms to a different DTD.
 * Combines InputStream semantics with the ability to specify a target doctype
 * for a byte stream containing XML markup.
 * Used by Validator class to wrap an InputStrea, when performing validation of a
 * document against a DTD.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class DoctypeInputStream extends InputStream {
    private final InputStream wrappedStream;

    private static final byte[] DOCTYPE_BYTES = {
        'D', 'O', 'C', 'T', 'Y', 'P', 'E', ' '
    };

    private byte[] readAheadBeforeDeclBuffer = null;
    private int readAheadBeforeDeclOffset = 0;
    private byte[] readAheadAfterDeclBuffer = null;
    private int readAheadAfterDeclOffset = 0;

    private final DoctypeSupport docType;
    private boolean writeDecl = false;


    /**
     * Create an InputStream whose XML content is provided by the
     * originalSource with the exception of the DOCTYPE which is
     * provided by the doctypeName and systemID.
     * @param originalSource
     * @param doctypeName
     * @param systemID
     */
    public DoctypeInputStream(InputStream originalSource, String doctypeName,
                              String systemID) {
        wrappedStream = originalSource instanceof BufferedInputStream
            ? originalSource : new BufferedInputStream(originalSource);
        docType = new DoctypeSupport(doctypeName, systemID);
    }

    /**
     * Read DOCTYPE-replaced content from the wrapped Reader
     */
    public int read() throws IOException {
        int nextByte = -1;

        if (writeDecl) {
            // currently writing our own DOCTYPE declaration
            nextByte = docType.read();
            if (nextByte == -1) {
                writeDecl = false;
            } else {
                return nextByte;
            }
        }

        if (readAheadBeforeDeclBuffer != null) {
            // in part of original document before our DOCTYPE - this
            // has already been read
            nextByte = readAheadBeforeDeclBuffer[readAheadBeforeDeclOffset++];
            if (readAheadBeforeDeclOffset >= readAheadBeforeDeclBuffer.length) {
                readAheadBeforeDeclBuffer = null;
                writeDecl = true;
            }
        } else if (!docType.hasBeenRead()) {
            // DOCTYPE not written, yet, need to see where it should go
            
            // read ahead until we find a good place to insert the doctype,
            // store bytes in readAheadBuffers
            ByteArrayOutputStream beforeDecl = new ByteArrayOutputStream();
            ByteArrayOutputStream afterDecl = new ByteArrayOutputStream();
            int current;
            boolean ready = false;
            while (!ready && (current = wrappedStream.read()) != -1) {
                byte c = (byte) current;
                if (c >= 0 && Character.isWhitespace((char) c)) {
                    beforeDecl.write(c);
                } else if (c == '<') {
                    // could be XML declaration, comment, PI, DOCTYPE
                    // or the first element
                    byte[] elementOrDeclOr = readUntilCloseCharacterIsReached();
                    if (elementOrDeclOr.length > 0) {
                        if (elementOrDeclOr[0] == '?') {
                            // XML declaration or PI
                            beforeDecl.write('<');
                            beforeDecl.write(elementOrDeclOr, 0,
                                             elementOrDeclOr.length);
                        } else if (elementOrDeclOr[0] != '!') {
                            // first element
                            afterDecl.write('<');
                            afterDecl.write(elementOrDeclOr, 0,
                                            elementOrDeclOr.length);
                            ready = true;
                        } else {
                            // comment or doctype
                            if (indexOfDoctype(elementOrDeclOr) == -1) {
                                afterDecl.write('<');
                                afterDecl.write(elementOrDeclOr, 0,
                                                elementOrDeclOr.length);
                            } // else swallow old declaration
                            ready = true;
                        }
                    }
                                
                } else {
                    afterDecl.write(c);
                    ready = true;
                }
            }
            readAheadBeforeDeclBuffer = beforeDecl.size() > 0
                ? beforeDecl.toByteArray() : null;
            readAheadAfterDeclBuffer = afterDecl.size() > 0
                ? afterDecl.toByteArray() : null;
            writeDecl = (readAheadBeforeDeclBuffer == null);
            return read();
        } else  if (readAheadAfterDeclBuffer != null) {
            // in part of original document read ahead after our DOCTYPE
            nextByte = readAheadAfterDeclBuffer[readAheadAfterDeclOffset++];
            if (readAheadAfterDeclOffset >= readAheadAfterDeclBuffer.length) {
                readAheadAfterDeclBuffer = null;
            }
        } else {
            nextByte = wrappedStream.read();
        }
        return nextByte;
    }

    private byte[] readUntilCloseCharacterIsReached() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int byteRead = -1;
        int openCount = 1;
        while (openCount > 0 && (byteRead = wrappedStream.read()) != -1) {
            byte c = (byte) byteRead;
            baos.write(c);
            if (c == '<') {
                openCount++;
            }
            if (c == '>') {
                openCount--;
            }
        }
        return baos.toByteArray();
    }
    
    public void close() throws IOException {
        wrappedStream.close();
    }
    
    /**
     * Could be faster when searching from the other end, but should do.
     */
    private static int indexOfDoctype(byte[] b) {
        int index = -1;
        for (int i = 0; i < b.length - DOCTYPE_BYTES.length + 1; i++) {
            if (b[i] == DOCTYPE_BYTES[0]) {
                boolean found = false;
                int j = 1;
                for (; !found && j < DOCTYPE_BYTES.length; j++) {
                    if (b[i + j] != DOCTYPE_BYTES[j]) {
                        found = true;
                    }
                }
                if (found) {
                    index = i;
                    break;
                } else {
                    i += j - 1;
                }
            }
        }
        return index;
    }
}