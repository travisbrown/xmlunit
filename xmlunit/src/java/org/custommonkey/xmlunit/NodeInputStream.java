package org.custommonkey.xmlunit;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

/**
 * Adapter class to present the content of a DOM Node (e.g. a Document) as an
 * InputStream using a DOM to Stream transformation.
  * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
*/
public class NodeInputStream extends InputStream {
    private final Node rootNode;
    private final ByteArrayOutputStream nodeContentBytes;
    private final Properties outputProperties;
    private int atPos = 0;

    /**
     * Simple constructor
     * @param rootNode the node to be presented as an input stream
     */
    public NodeInputStream(Node rootNode) {
        this(rootNode, null);
    }

    /**
     * Simple constructor
     * @param rootNode the node to be presented as an input stream
     */
    public NodeInputStream(Node rootNode, Properties outputProperties) {
        this.rootNode = rootNode;
        nodeContentBytes = new ByteArrayOutputStream();
        this.outputProperties = outputProperties;
    }

    /**
     * Do the actual work of serializing the node to bytes
     * @throws IOException if serialization goes awry
     */
    private void ensureContentAvailable() throws IOException {
        if (nodeContentBytes.size() > 0) {
            return;
        }
        try {
            Transform serializeTransform = new Transform(
                new DOMSource(rootNode));
            if (outputProperties!=null) {
                serializeTransform.setOutputProperties(outputProperties);
            }
            StreamResult byteResult = new StreamResult(nodeContentBytes);
            serializeTransform.transformTo(byteResult);
        } catch (Exception e) {
            throw new IOException("Unable to serialize document to outputstream: "
                + e.toString());
        }
    }

    /**
     * InputStream method
     * @return byte as read
     * @throws IOException
     */
    public int read() throws IOException {
        ensureContentAvailable();
        if (reallyAvailable()==0) {
            return -1;
        }
        int contentByte = nodeContentBytes.toByteArray()[atPos];
        atPos++;
        return contentByte;
    }

    /**
     * InputStream method
     * Note that calling close allows a repeated read of the content
     * @throws IOException
     */
    public void close() throws IOException {
        atPos = 0;
    }

    /**
     * InputStream method
     * @return number of bytes available
     */
    public int available() throws IOException {
        ensureContentAvailable();
        return reallyAvailable();
    }

    /**
     * @return really available
     */
    private int reallyAvailable() {
        return nodeContentBytes.size() - atPos;
    }
}

