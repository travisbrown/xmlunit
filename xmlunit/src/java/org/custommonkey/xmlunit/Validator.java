package org.custommonkey.xmlunit;

import java.io.IOException;
import java.io.Reader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

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
 */
public class Validator extends DefaultHandler implements ErrorHandler {
    private final InputSource validationInputSource ;
    private final SAXParser parser;
    private final StringBuffer messages;
    private final boolean canTrace;

    private Boolean isValid;

    /**
     * Basic constructor.
     * Validates the contents of the Reader using the DTD or schema referenced
     *  by those contents.
     * @param readerForValidation
     * @throws ParserConfigurationException if unable to turn validation feature
     *  on in JAXP factory
     * @throws SAXException if unable to obtain new Sax parser via JAXP factory
     */
    public Validator(Reader readerForValidation)
    throws ParserConfigurationException, SAXException {
        isValid = null;
        messages = new StringBuffer();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(true);
        parser = factory.newSAXParser();
        validationInputSource = new InputSource(readerForValidation);
        canTrace = (readerForValidation instanceof DoctypeReader);
    }

    /**
     * Extended constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID. There must be DOCTYPE instruction in the markup that
     *  references the DTD or else the markup will be considered invalid: if
     *  there is no DOCTYPE in the markup use the 3-argument constructor
     * @param readerForValidation
     * @param systemID
     * @throws ParserConfigurationException if unable to turn validation feature
     *  on in JAXP factory
     * @throws SAXException if unable to obtain new Sax parser via JAXP factory
     */
    public Validator(Reader readerForValidation, String systemID)
    throws ParserConfigurationException, SAXException {
        this(readerForValidation);
        validationInputSource.setSystemId(systemID);
    }

    /**
     * Full constructor.
     * Validates the contents of the Reader using the DTD specified with the
     *  systemID and named with the doctype name.
     * @param readerForValidation
     * @param systemID
     * @param doctype
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Validator(Reader readerForValidation, String systemID, String doctype)
    throws ParserConfigurationException, SAXException {
        this(new DoctypeReader(readerForValidation, doctype, systemID));
        validationInputSource.setSystemId(systemID);
    }

    /**
     * Perform the validation of the source against DTD
     * @return true if the input supplied to the constructor passes validation,
     *  false otherwise
     */
    public boolean isValid() {
        validate();
        return isValid.booleanValue();
    }

    /**
     * Append any validation message(s) to the specified StringBuffer
     * @param buf
     * @return
     */
    public StringBuffer appendMessage(StringBuffer buf) {
        if (isValid()) {
            return buf.append("[valid]");
        }
        return buf.append(messages);
    }

    /**
     * @return class name appended with validation messages
     */
    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString()).append(':');
        return appendMessage(buf).toString();
    }

    /**
     * Actually perform validation
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
        } else if (canTrace) {
            try {
                messages.append("\nContent was: ").append( ((DoctypeReader)
                    validationInputSource.getCharacterStream()).getContent());
            } catch (IOException e) {
                // silent but deadly?
            }
        }
    }

    /**
     * Deal with any parser exceptions not handled by the ErrorHandler interface
     * @param e
     */
    private void parserException(Exception e) {
        invalidate(e.getMessage());
    }

    /**
     * ErrorHandler interface method
     * @param exception
     * @throws SAXException
     */
    public void warning (SAXParseException exception)
    throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * ErrorHandler interface method
     * @param exception
     * @throws SAXException
     */
    public void error (SAXParseException exception)
    throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * ErrorHandler interface method
     * @param exception
     * @throws SAXException
     */
    public void fatalError (SAXParseException exception)
    throws SAXException {
        errorHandlerException(exception);
    }

    /**
     * Entity Resolver method: allows us to override an existing systemID
     * referenced in the markup DOCTYPE instruction
     * @param publicId
     * @param systemId
     * @return
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
     * Deal with exceptions passed to the ErrorHandler interface by the parser
     * @param exception
     * @throws SAXException
     */
    private void errorHandlerException(Exception e) {
        invalidate(e.getMessage());
    }

    /**
     * Set the validation status flag to false and capture the message for
     *  use later
     * @param message
     */
    private void invalidate(String message) {
        isValid = Boolean.FALSE;
        messages.append(message).append(' ');
    }
}
