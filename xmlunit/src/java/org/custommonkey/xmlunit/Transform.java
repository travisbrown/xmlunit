package org.custommonkey.xmlunit;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*;
import java.io.*;

/**
 *
 */
public class Transform {

    private final Source inputSource;
    private final Transformer transformer;

    public Transform(String input, String stylesheet)
    throws TransformerConfigurationException {
        this(new StringReader(input), new StringReader(stylesheet));
    }
    public Transform(String input, File stylesheet)
    throws TransformerConfigurationException, FileNotFoundException {
        this(new StringReader(input), new FileReader(stylesheet));
    }
    private Transform(Reader inputReader, Reader stylesheetReader)
    throws TransformerConfigurationException {
        this.inputSource = new StreamSource(inputReader);
        this.transformer = getTransformer(new StreamSource(stylesheetReader));
    }

    private Transformer getTransformer(Source stylesheetSource)
    throws TransformerConfigurationException {
        return TransformerFactory.newInstance().newTransformer(stylesheetSource);
    }

    public String getResultString() throws TransformerException {
        StringWriter outputWriter = new StringWriter();
        StreamResult result = new StreamResult(outputWriter);
        transformer.transform(inputSource, result);

        return outputWriter.toString();
    }

    public Document getResultDocument() throws TransformerException {
        DOMResult result = new DOMResult();
        transformer.transform(inputSource, result);

        return (Document) result.getNode();
    }

}
