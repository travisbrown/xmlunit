/*
******************************************************************
Copyright (c) 200, Jeff Martin, Tim Bacon
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


