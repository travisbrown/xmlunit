package org.custommonkey.xmlunit;

import junit.framework.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.io.*;

public class XMLUnit {
    private static final SAXBuilder builder = new SAXBuilder();
    private static final XMLUnit unit = new XMLUnit();

    /** 
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public static Diff compare(Reader control, Reader test) throws JDOMException {
        Document controlDoc = builder.build(control);
        Document testDoc = builder.build(test);
        return compare(controlDoc, testDoc);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public static Diff compare(String control, Reader test) throws JDOMException {
        Document controlDoc = builder.build(new StringReader(control));
        Document testDoc = builder.build(test);
        return compare(controlDoc, testDoc);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public static Diff compare(Reader control, String test) throws JDOMException {
        Document controlDoc = builder.build(control);
        Document testDoc = builder.build(new StringReader(test));
        return compare(controlDoc, testDoc);
    }

    /**
     * Compare two XML documents provided as strings
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public static Diff compare(String control, String test) throws JDOMException {
        return compare( new StringReader(control), new StringReader(test));
    }

    /**
     * compare XML documents
     */
    private static Diff compare(Document control, Document test){
        Diff diff = new Diff();
        unit.compare(control.getRootElement(), test.getRootElement(), diff);
        return diff;
    }

    /**
     * Compare XML elements.
     * Recuses thorugh the document tree comparing elements
     */
    private void compare(Element control, Element test, Diff diff){
        System.out.println("Compare: "+control+", "+test);

        if(!control.getText().equals(test.getText()))diff.diff(control, test);

        for(Iterator i = control.getAttributes().iterator();i.hasNext();){
            Attribute att = (Attribute)i.next();
            System.out.println("Compare: "+att+", "+test.getAttribute(att.getName()));
            if(test.getAttribute(att.getName())==null){
                diff.diff(control, test);
            }else{
                if(!test.getAttribute(att.getName()).getValue().equals(att.getName())){
                    diff.diff(control, test);
                }
            }
        }

        if( !control.getName().equals(test.getName())){
            diff.diff(control, test);
        }else{
            Iterator iTest = test.getChildren().iterator();
            for(Iterator iControl = control.getChildren().iterator();iControl.hasNext();){
                compare((Element)iControl.next(), (Element)iTest.next(), diff);
            }
        }
    }
}
