package org.custommonkey.xmlunit;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*;
import java.io.*;

/**
 * Handy wrapper for an XSLT transformation performed using JAXP/Trax.
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
     * Factory method
     * @param stylesheetSource
     * @return
     * @throws TransformerConfigurationException
     */
    private Transformer getTransformer(Source stylesheetSource)
    throws TransformerConfigurationException {
        return TransformerFactory.newInstance().newTransformer(stylesheetSource);
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a String
     * @throws TransformerException
     */
    public String getResultString() throws TransformerException {
        StringWriter outputWriter = new StringWriter();
        StreamResult result = new StreamResult(outputWriter);
        transformer.transform(inputSource, result);

        return outputWriter.toString();
    }

    /**
     * Perform the XSLT transformation specified in the constructor
     * @return the result as a DOM Document
     * @throws TransformerException
     */
    public Document getResultDocument() throws TransformerException {
        DOMResult result = new DOMResult();
        transformer.transform(inputSource, result);

        return (Document) result.getNode();
    }

}
