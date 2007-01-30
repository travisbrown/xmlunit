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

import org.custommonkey.xmlunit.exceptions.ConfigurationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Validates XML against its internal or external DOCTYPE, or a completely
 *  different DOCTYPE.
 * Usage:
 * <ul>
 * <li><code>new Validator(readerForXML);</code> <br/>
 *   to validate some XML that contains or references an accessible DTD or
 *   schema
 * </li>
 * <li><code>new Validator(readerForXML, systemIdForValidation);</code> <br/>
 *   to validate some XML that references a DTD but using a local systemId
 *   to perform the validation
 * </li>
 * <li><code>new Validator(readerForXML, systemIdForValidation, doctypeName);</code> <br/>
 *   to validate some XML against a completely different DTD
 * </li>
 * </ul>
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Validator extends DefaultHandler implements ErrorHandler {
    private final InputSource validationInputSource;
    private final SAXParser parser;
    private final StringBuffer messages;
    private final boolean usingDoctypeReader;

    private Boolean isValid;

    /**
     * Baseline constructor: called by all others
     * 
     * @param inputSource
     * @param usingDoctypeReader
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     */
    protected Validator(InputSource inputSource, boolean usingDoctypeReader)
        throws SAXException, ConfigurationException {
        isValid = null;
        messages = new StringBuffer();
        SAXParserFactory factory = XMLUnit.getSAXParserFactory();
        factory.setValidating(true);
        try {
            parser = factory.newSAXParser();
        } catch (ParserConfigurationException ex) {
            throw new ConfigurationException(ex);
        }

        this.validationInputSource = inputSource;
        this.usingDoctypeReader = usingDoctypeReader;
    }

    /**
     * DOM-style constructor: allows Document validation post-manipulation
     * of the DOM tree's contents.
     * This takes a fairly tortuous route to validation as DOM level 2 does
     * not allow creation of Doctype nodes.
     * The supplied systemId and doctype name will replace any Doctype
     * settings in the Document.
     * 
     * @param document
     * @param systemID
     * @param doctype
     * @throws SAXException if unable to obtain new Sax parser via JAXP factory
     * @throws ConfigurationException if validation could not be turned on
     */
    public Validator(Document document, String systemID, String doctype)
        throws SAXException, ConfigurationException {
        this(new InputStreamReader(new NodeInputStream(document)),
            systemID, doctype);
    }

    /**
     * Basic constructor.
     * Validates the contents of the Reader using the DTD or schema referenced
     *  by those contents.
     *  
     * @param readerForValidation
     * @throws SAXException if unable to obtain new Sax parser via JAXP factory
     * @throws ConfigurationException if validation could not be turned on
     */
    public Validator(Reader readerForValidation)
        throws SAXException, ConfigurationException {
        this(new InputSource(readerForValidation),
            (readerForValidation instanceof DoctypeReader));
    }

    /**
     * Extended constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID. There must be DOCTYPE instruction in the markup that
     *  references the DTD or else the markup will be considered invalid: if
     *  there is no DOCTYPE in the markup use the 3-argument constructor
     *  
     * @param readerForValidation
     * @param systemID
     * @throws SAXException if unable to obtain new Sax parser via JAXP factory
     * @throws ConfigurationException if validation could not be turned on
     */
    public Validator(Reader readerForValidation, String systemID)
        throws SAXException, ConfigurationException {
        this(readerForValidation);
        validationInputSource.setSystemId(systemID);
    }

    /**
     * Full constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID and named with the doctype name.
     *  
     * @param readerForValidation
     * @param systemID
     * @param doctype
     * @throws SAXException
     * @throws ConfigurationException if validation could not be turned on
     */
    public Validator(Reader readerForValidation, String systemID, String doctype)
        throws SAXException, ConfigurationException {
        this(new DoctypeReader(readerForValidation, doctype, systemID));
        validationInputSource.setSystemId(systemID);
    }

    /**
     * Turn on XML Schema validation.
     *
     * <p><b>This feature should work with any XML parser that is JAXP
     * 1.2 compliant and supports XML Schema validation.</b></p>
     *
     * <p>For a fully JAXP 1.2 compliant parser the property {@link
     * JAXPConstants.Properties.SCHEMA_LANGUAGE
     * http://java.sun.com/xml/jaxp/properties/schemaLanguage} is set,
     * if this fails the method falls back to the features
     * http://apache.org/xml/features/validation/schema &amp;
     * http://apache.org/xml/features/validation/dynamic which should
     * cover early versions of Xerces 2 as well.</p>
     *
     * @param use indicate that XML Schema should be used to validate
     * documents.
     * @throws SAXException
     * @see #setJAXP12SchemaSource(Object)
     */
    public void useXMLSchema(boolean use) throws SAXException {
        boolean tryXercesProperties = false;
        try {
            if (use) {
                parser.setProperty(JAXPConstants.Properties.SCHEMA_LANGUAGE,
                                   XMLConstants.W3C_XML_SCHEMA_NS_URI);
            }
        } catch (SAXNotRecognizedException e) {
            tryXercesProperties = true;
        } catch (SAXNotSupportedException e) {
            tryXercesProperties = true;
        }

        if (tryXercesProperties) {
        parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/schema", use);
        parser.getXMLReader().setFeature("http://apache.org/xml/features/validation/dynamic", use);
        }
    }

    /**
     * Perform the validation of the source against DTD / Schema.
     * 
     * @return true if the input supplied to the constructor passes validation,
     *  false otherwise
     */
    public boolean isValid() {
        validate();
        return isValid.booleanValue();
    }

    /**
     * Assert that a document is valid.
     */
    public void assertIsValid(){
        if(!isValid()){
            junit.framework.Assert.fail(messages.toString());
        }
    }

    /**
     * Append any validation message(s) to the specified StringBuffer.
     * 
     * @param toAppendTo
     * @return specified StringBuffer with message(s) appended
     */
    private StringBuffer appendMessage(StringBuffer toAppendTo) {
        if (isValid()) {
            return toAppendTo.append("[valid]");
        }
        return toAppendTo.append(messages);
    }

    /**
     * @return class name appended with validation messages
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString()).append(':');
        return appendMessage(buf).toString();
    }

    /**
     * Actually perform validation.
     */
    private void validate() {
        if (isValid != null) {
            return;
        }

        try {
            parser.parse(validationInputSource, this);
        } catch (SAXException e) {
            parserException(e);
        } catch (IOException e) {
            parserException(e);
        }

        if (isValid == null) {
            isValid = Boolean.TRUE;
        } else if (usingDoctypeReader) {
            try {
                messages.append("\nContent was: ").append(((DoctypeReader)
                    validationInputSource.getCharacterStream()).getContent());
            } catch (IOException e) {
                // silent but deadly?
            }
        }
    }

    /**
     * Deal with any parser exceptions not handled by the ErrorHandler interface.
     * 
     * @param e
     */
    private void parserException(Exception e) {
        invalidate(e.getMessage());
    }

    /**
     * ErrorHandler interface method.
     * 
     * @param exception
     * @throws SAXException
     */
    public void warning(SAXParseException exception)
        throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * ErrorHandler interface method.
     * 
     * @param exception
     * @throws SAXException
     */
    public void error(SAXParseException exception)
        throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * ErrorHandler interface method.
     * 
     * @param exception
     * @throws SAXException
     */
    public void fatalError(SAXParseException exception)
        throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * Entity Resolver method: allows us to override an existing systemID
     * referenced in the markup DOCTYPE instruction.
     * 
     * @param publicId
     * @param systemId
     * @return the sax InputSource that points to the overridden systemID
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        if (validationInputSource.getSystemId() != null) {
            return new InputSource(validationInputSource.getSystemId());
        } else if (systemId != null) {
            return new InputSource(systemId);
        }
        return null;
    }

    /**
     * Deal with exceptions passed to the ErrorHandler interface by the parser.
     */
    private void errorHandlerException(Exception e) {
        invalidate(e.getMessage());
    }

    /**
     * Set the validation status flag to false and capture the message for use
     * later.
     * 
     * @param message
     */
    private void invalidate(String message) {
        isValid = Boolean.FALSE;
        messages.append(message).append(' ');
    }

    /**
     * As per JAXP 1.2 changes, which introduced a standard way for parsers to
     * support schema validation. Since only W3C Schema support was included in 
     * JAXP 1.2, this is the only mechanism currently supported by this method.
     * 
     * @param schemaSource
     *            This can be one of the following:
     * <ul>
     *   <li>String that points to the URI of the schema</li>
     *   <li>InputStream with the contents of the schema</li>
     *   <li>SAX InputSource</li>
     *   <li>File</li>
     *   <li>an array of Objects with the contents being one of the
     *       types defined above. An array of Objects can be used only when
     *       the schema language has the ability to assemble a schema at
     *       runtime. When an array of Objects is passed it is illegal to
     *       have two schemas that share the same namespace.</li>
     * </ul>
     * @throws SAXException if this method of validating  isn't supported.
     * @see http://java.sun.com/webservices/jaxp/change-requests-11.html
     */
    public void setJAXP12SchemaSource(Object schemaSource) throws SAXException {
        parser.setProperty(JAXPConstants.Properties.SCHEMA_SOURCE,
                        schemaSource);
    }
}
