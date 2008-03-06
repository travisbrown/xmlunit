/*
******************************************************************
Copyright (c) 2008, Jeff Martin, Tim Bacon
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

package org.custommonkey.xmlunit.jaxp13;

import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validator class based of {@link javax.xml.validation javax.xml.validation}.
 *
 * <p>This class currently only provides support for validating schema
 * definitions.  It defaults to the W3C XML Schema 1.0 but can be used
 * to validate against any schema language supported by your
 * SchemaFactory implementation.</p>
 */
public class Validator {
    private final String schemaLanguage;
    private final ArrayList sources = new ArrayList();

    /**
     * validates using W3C XML Schema 1.0.
     */
    public Validator() {
        this(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    /**
     * validates using the specified schema language.
     *
     * @param schemaLanguage the schema language to use - see {@link
     * javax.xml.validation.SchemaFactory SchemaFactory}.
     */
    public Validator(String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    /**
     * Adds a source for the schema defintion.
     */
    public void addSchemaSource(Source s) {
        sources.add(s);
    }

    /**
     * Is the given schema definition valid?
     */
    public boolean isSchemaValid() {
        return getSchemaErrors().size() == 0;
    }

    /**
     * Obtain a list of all errors in the schema defintion.
     *
     * <p>The list contains {@link org.xml.sax.SAXParseException
     * SAXParseException}s.</p>
     */
    public List/*<SAXParseException>*/ getSchemaErrors() {
        final ArrayList l = new ArrayList();
        ErrorHandler h = new ErrorHandler() {
                public void error(SAXParseException e) {
                    l.add(e);
                }
                public void fatalError(SAXParseException e) {
                    l.add(e);
                }
                public void warning(SAXParseException e) {
                    l.add(e);
                }
            };
        try {
            parseSchema(h);
        } catch (SAXException e) {
            // error has been recorded in our ErrorHandler anyway
        }
        return l;
    }

    private Schema parseSchema(ErrorHandler h) throws SAXException {
        SchemaFactory fac = SchemaFactory.newInstance(schemaLanguage);
        fac.setErrorHandler(h);
        return fac.newSchema((Source[]) sources.toArray(new Source[0]));
    }
}