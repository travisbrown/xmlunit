package org.custommonkey.xmlunit;

import org.jdom.*;
import java.util.*;

/**
 * Describes differences between XML documents
 */
public class Diff{
    private boolean similar = true;
    private boolean identical = true;
    private Element controlElement=null;
    private Element testElement=null;
    private Attribute controlAttribute=null;
    private Attribute testAttribute=null;

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
    protected void diffElement(Element control, Element test){
        if(controlAttribute!=null||testAttribute!=null
            ||controlElement!=null||testElement!=null)return;
        similar = false;
        identical = false;
        this.controlElement = control;
        this.testElement = test;
    }

    /**
     * Add a difference to the list difference between documents
     */
    protected void diffAttribute(Attribute control, Attribute test){
        if(controlAttribute!=null||testAttribute!=null
            ||controlElement!=null||testElement!=null)return;
        similar = false;
        identical = false;
        this.controlAttribute = control;
        this.testAttribute = test;
    }

    public String toString(){
        StringBuffer buf = new StringBuffer();
        if(controlElement==null && testElement== null){
            return "Expected: " + controlAttribute + " Got: " + testAttribute;
        }else{
            buf.append("Expected: ");
            if(controlElement==null){
                buf.append("null");
            }else{
                if(controlElement.getText()!=null
                    &&!controlElement.getText().equals("")){
                    buf.append("<");
                    buf.append(controlElement.getName());
                    buf.append(">");
                    buf.append(controlElement.getText());
                    buf.append("</");
                    buf.append(controlElement.getName());
                    buf.append(">");
                }else{
                    buf.append("<");
                    buf.append(controlElement.getName());
                    buf.append("/>");
                }
            }
            buf.append(", but was: ");
            if(testElement==null){
                buf.append("null");
            }else{
                if(testElement.getText()!=null
                    &&!testElement.getText().equals("")){
                    buf.append("<");
                    buf.append(testElement.getName());
                    buf.append(">");
                    buf.append(testElement.getText());
                    buf.append("</");
                    buf.append(testElement.getName());
                    buf.append(">");
                }else{
                    buf.append("<");
                    buf.append(testElement.getName());
                    buf.append("/>");
                }
            }
        }
        return buf.toString();
    }
}
