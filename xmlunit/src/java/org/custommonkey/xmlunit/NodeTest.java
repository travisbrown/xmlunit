package org.custommonkey.xmlunit;

import java.io.IOException;
import java.io.Reader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Encapsulation of the Node-by-Node testing of a DOM Document
 * Uses a nodetype-specific <code>NodeFilter</code> to pass the DOM Nodes
 * to a NodeTester instance that performs the acual Node validation.
 * @see NodeTester
 */
public class NodeTest {
    private final DocumentTraversal documentTraversal;
    private final Node rootNode;

    /**
     * Construct a NodeTest for the DOM built using the Reader and JAXP, for
     * multiple node types
     */
    public NodeTest(Reader reader) throws SAXException,
    ParserConfigurationException, IOException {
        this(DocumentBuilderFactory.newInstance()
            .newDocumentBuilder().parse(new InputSource(reader))
        );
    }

    /**
     * Construct a NodeTest for the specified Document, for
     * multiple node types.
     * @exception IllegalArgumentException if the Document does not support the DOM
     * DocumentTraversal interface (most DOM implementations should provide this
     * support)
     */
    public NodeTest(Document document) {
        this(getDocumentTraversal(document),
            document.getDocumentElement());
    }

    /**
     * Try to cast a Document into a DocumentTraversal
     * @param document
     * @return DocumentTraversal interface if the DOM implementation supports it
     */
    private static DocumentTraversal getDocumentTraversal(Document document) {
        try {
            return (DocumentTraversal) document;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("DOM Traversal not supported by "
                + document.getImplementation().getClass().getName()
                + ". To use this class you will need to switch to a DOM implementation that supports Traversal.");
        }
    }

    /**
     * Construct a NodeTest using the specified DocumentTraversal, starting at
     * the specified root node, for multiple node types
     */
    public NodeTest(DocumentTraversal documentTraversal, Node rootNode) {
        this.documentTraversal = documentTraversal;
        this.rootNode = rootNode;
    }

    /**
     * Does this NodeTest pass using the specified NodeTester instance?
     * @param tester
     * @param singleNodeType
     * @exception NodeTestException if test fails
     */
    public void performTest(NodeTester tester, short singleNodeType)
    throws NodeTestException {
        performTest(tester, new short[] {singleNodeType});
    }

    /**
     * Does this NodeTest pass using the specified NodeTester instance?
     * @param tester
     * @param nodeTypes
     * @exception NodeTestException if test fails
     */
    public void performTest(NodeTester tester, short[] nodeTypes)
    throws NodeTestException {
        NodeIterator iter = documentTraversal.createNodeIterator(rootNode,
            NodeFilter.SHOW_ALL, new NodeTypeNodeFilter(nodeTypes), true);

        for (Node nextNode = iter.nextNode(); nextNode != null;
        nextNode = iter.nextNode()) {
            tester.testNode(nextNode, this);
        }
        tester.noMoreNodes(this);
    }

    /**
     * Node type specific Node Filter: accepts Nodes of those types specified
     * in constructor, rejects all others
     */
    private class NodeTypeNodeFilter implements NodeFilter {
        private final short[] nodeTypes;

        /**
         * Construct filter for specific node types
         * @param nodeTypes
         */
        public NodeTypeNodeFilter(short[] nodeTypes) {
            this.nodeTypes = nodeTypes;
        }

        /**
         * NodeFilter method.
         * @param aNode
         * @return
         */
        public short acceptNode(Node aNode) {
            if (acceptNodeType(aNode.getNodeType())) {
                return NodeFilter.FILTER_ACCEPT;
            }
            return NodeFilter.FILTER_REJECT;
        }

        /**
         * Does this instance accept nodes with the node type value
         * @param shortVal
         * @return
         */
        private boolean acceptNodeType(short shortVal) {
            for (int i=0; i < nodeTypes.length; ++i) {
                if (nodeTypes[i] == shortVal) {
                    return true;
                }
            }
            return false;
        }
    }
}
