package org.custommonkey.xmlunit;

import org.jdom.*;
import java.util.*;

/**
 * Describes differences between XML documents
 */
public class Diff{
    private boolean similar = true;
    private boolean identical = true;
    private Element control=null;
    private Element test=null;

    /**
     * Return the result of a comparison. Two documents are considered 
     * to be "similar" if the contain the same elements and attributes
     * regardless of order.
     */
    public boolean similar(){
        return similar;
    }

    /**
     * Return the result of a comparison. Two documents are considered 
     * to be "identical" if the contain the same elements and attributes
     * regardless of order.
     */
    public boolean identical(){
        return identical;
    }

    /**
     * Add a difference to the list difference between documents
     */
    protected void diff(Element control, Element test){
        similar = false;
        identical = false;
        this.control = control;
        this.test = test;
    }

    public String toString(){
        return "Expected: " + control + " Got: " + test;
    }
}
