package org.custommonkey.xmlunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Properties;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 * Handy wrapper for an XSLT transformation performed using JAXP/Trax.
 * Note that transformation is not actually performed until a call to
 * <code>getResultXXX</code> method, and Templates are not used.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Transform {

    private final Source inputSource;
    private final Transformer transformer;

    /**
     * Create a transformation using String input XML and String stylesheet
     * @param input
     * @param stylesheet
     * @throws TransformerConfigurationException
     */
    public Transform(String input, String stylesheet)
    throws TransformerConfigurationException {
        this(new StringReader(input), new StringReader(stylesheet));
    }

    /**
     * Create a transformation using String input XML and stylesheet in a File
     * @param input
     * @param stylesheet
     * @throws TransformerConfigurationException
     * @throws FileNotFoundException
     */
    public Transform(String input, File stylesheet)
    throws TransformerConfigurationException, FileNotFoundException {
        this(new StringReader(input), new FileReader(stylesheet));
    }

    /**
     * Create a transformation that allows us to serialize a DOM
     * @param source
     * @throws TransformerConfigurationException
     */
    public Transform(DOMSource source)
    throws TransformerConfigurationException {
        this.inputSource = source;
        this.transformer = getIdentityTransform();
    }

    /**
     * Create a transformation using Reader input XML and Reader stylesheet
     * @param inputReader
     * @param stylesheetReader
     * @throws TransformerConfigurationException
     */
    private Transform(Reader inputReader, Reader stylesheetReader)
    throws TransformerConfigurationException {
        this.inputSource = new StreamSource(inputReader);
        this.transformer = getTransformer(new StreamSource(stylesheetReader));
    }

    /**
     * @return the identity transform
     * @throws TransformerConfigurationException
     */
    private Transformer getIdentityTransform()
    throws TransformerConfigurationException {
        return getTransformer(null);
    }

    /**
     * Factory method
     * @param stylesheetSource
     * @return
     * @throws TransformerConfigurationException
     */
    private Transformer getTransformer(Source stylesheetSource)
    throws TransformerConfigurationException {
        TransformerFactory factory = XMLUnit.getTransformerFactory();
        if (stylesheetSource == null) {
            return factory.newTransformer();
        }
        return factory.newTransformer(stylesheetSource);
    }

    /**
     * Perform the actual transformation
     * @param result
     * @throws TransformerException
     */
    protected void transformTo(Result result) throws TransformerException {
        transformer.transform(inputSource, result);
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a String
     * @throws TransformerException
     */
    public String getResultString() throws TransformerException {
        StringWriter outputWriter = new StringWriter();
        transformTo(new StreamResult(outputWriter));

        return outputWriter.toString();
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a DOM Document
     * @throws TransformerException
     */
    public Document getResultDocument() throws TransformerException {
        DOMResult result = new DOMResult();
        transformTo(result);

        return (Document) result.getNode();
    }

    /**
     * Override an output property specified in the transformation stylesheet
     * @param name
     * @param value
     */
    public void setOutputProperty(String name, String value) {
        Properties properties = new Properties();
        properties.setProperty(name, value);
        setOutputProperties(properties);
    }

    /**
     * Override output properties specified in the transformation stylesheet
     * @param outputProperties
     */
    public void setOutputProperties(Properties outputProperties) {
        transformer.setOutputProperties(outputProperties);
    }
}


