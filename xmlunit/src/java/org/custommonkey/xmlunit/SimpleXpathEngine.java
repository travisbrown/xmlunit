package org.custommonkey.xmlunit;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Simple class for accessing the Nodes matched by an Xpath expression, or
 * evaluating the String value of an Xpath expression.
 * Uses a <code>copy-of</code> or <code>value-of</code> XSL template (as
 * appropriate) to execute the Xpath.
 * This is not an efficient method for accessing XPaths but it is portable
 * across underlying transform implementations. (Yes I know Jaxen is too, but
 * this approach seemed to be the simplest thing that could possibly work...)
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class SimpleXpathEngine {

    /**
     * What every XSL transform needs
     * @return
     */
    private StringBuffer getXSLTBase() {
        return new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            .append("<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">")
            ;
    }

    /**
     * @param select an xpath syntax <code>select</code> expression
     * @return the <code>copy-of</code> transformation
     */
    private String getCopyTransformation(String select) {
        return getXSLTBase()
            .append("<xsl:preserve-space elements=\"*\"/>")
            .append("<xsl:output method=\"xml\" version=\"1.0\" encoding=\"UTF-8\"/>")
            .append("<xsl:template match=\"/\">")
            .append("<xpathResult>")
            .append("<xsl:apply-templates select=\"").append(select)
                .append("\" mode=\"result\"/>")
            .append("</xpathResult>")
            .append("</xsl:template>")
            .append("<xsl:template match=\"*\" mode=\"result\">")
            .append("  <xsl:copy-of select=\".\"/>")
            .append("</xsl:template>")
            .append("</xsl:stylesheet>")
            .toString();
    }

    /**
     * @param select an xpath syntax <code>select</code> expression
     * @return the <code>value-of</code> transformation
     */
    private String getValueTransformation(String select) {
        return getXSLTBase()
            .append("<xsl:output method=\"text\"/>")
            .append("<xsl:template match=\"/\">")
            .append("  <xsl:value-of select=\"").append(select).append("\"/>")
            .append("</xsl:template>")
            .append("</xsl:stylesheet>")
            .toString();
    }

    /**
     * Perform the actual transformation work required
     * @param xslt
     * @param document
     * @param result
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private void performTransform(String xslt, Document document, Result result)
    throws TransformerConfigurationException, TransformerException {
        StreamSource source = new StreamSource(new StringReader(xslt));
        Transformer transformer =
            XMLUnit.getTransformerFactory().newTransformer(source);
        transformer.transform(new DOMSource(document), result);
    }

    /**
     * Testable method to execute the copy-of transform and return the root
     * node of the resulting Document.
     * @param select
     * @param document
     * @return the root node of the Document created by the copy-of transform.
     * @throws TransformerException
     */
    protected Node getXPathResultNode(String select, Document document)
    throws TransformerException {
        return getXPathResultAsDocument(select, document).getDocumentElement();
    }

    /**
     * Execute the copy-of transform and return the resulting Document.
     * Used for XMLTestCase comparison
     * @param select
     * @param document
     * @return the Document created by the copy-of transform.
     * @throws TransformerException
     */
    protected Document getXPathResultAsDocument(String select, Document document)
    throws TransformerException {
        DOMResult result = new DOMResult();
        performTransform(getCopyTransformation(select), document, result);
        return (Document) result.getNode();
    }

    /**
     * Execute the specified xpath syntax <code>select</code> expression
     * on the specified document and return the list of nodes (could have
     * length zero) that match
     * @param select
     * @param document
     * @return list of matching nodes
     * @throws TransformerException
     * @throws TransformerConfigurationException
     */
    public NodeList getMatchingNodes(String select, Document document)
    throws TransformerException, TransformerConfigurationException {
        return getXPathResultNode(select, document).getChildNodes();
    }

    /**
     * Evaluate the result of executing the specified xpath syntax
     * <code>select</code> expression on the specified document
     * @param select
     * @param document
     * @return evaluated result
     * @throws TransformerException
     * @throws TransformerConfigurationException
     */
    public String evaluate(String select, Document document)
    throws TransformerException, TransformerConfigurationException {
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        performTransform(getValueTransformation(select), document, result);
        return writer.toString();
    }
}

