package org.custommonkey.xmlunit;

import org.w3c.dom.Node;

/**
 * Thrown by a NodeTest that fails
 * @see NodeTest
 */
public class NodeTestException extends Exception {
    private final Node node;

    /**
     * Constructor for specific node and message
     * @param message
     * @param node
     */
    public NodeTestException(String message, Node node) {
        super(message);
        this.node = node;
    }

    /**
     * Constructor for message only
     * @param message
     */
    public NodeTestException(String message) {
        this(message, null);
    }

    /**
     * @return true if a node was passed to constructor
     */
    public boolean hasNode() {
        return node != null;
    }

    /**
     * @return the node passed to constructor, or null if no node was passed
     */
    public Node getNode() {
        return node;
    }

}