package org.custommonkey.xmlunit;


/**
 * Value object that describes a difference between DOM Nodes.
 * <br />Examples and more at <a href="http://xmlunit.sourceforge.net"/>xmlunit.sourceforge.net</a>
 */
public class Difference {
    /** Simple unique identifier */
    private final int id;
    /** Description of the difference */
    private final String description;
    /** TRUE if the difference represents a similarity, FALSE otherwise */
    private final boolean recoverable;

    /**
     * Constructor for non-similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description) {
        this(id, description, false);
    }

    /**
     * Constructor for similar Difference instances
     * @param id
     * @param description
     */
    protected Difference(int id, String description, boolean recoverable) {
        this.id = id;
        this.description = description;
        this.recoverable = recoverable;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return TRUE if the difference represents a similarity, FALSE otherwise
     */
    public boolean isRecoverable() {
        return recoverable;
    }

    /**
     * @return a basic representation of the object state and identity
     */
    public String toString() {
        return new StringBuffer(super.toString())
            .append(" (#").append(id)
            .append(") ").append(description)
            .toString();
    }
}