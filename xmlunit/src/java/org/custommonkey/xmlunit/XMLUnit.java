package org.custommonkey.xmlunit;

import junit.framework.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import java.io.*;

public class XMLUnit {
    private SAXBuilder controlBuilder = new SAXBuilder();
    private SAXBuilder testBuilder = new SAXBuilder();
    private static final XMLUnit unit = new XMLUnit();
    private static boolean ignoreWhitespace = false;

    /**
     * Overide the parser to use to parser control documents.
     * This is useful when comparing the output of two different 
     * parsers.
     */
    public static void setControlParser(String parser){
        unit.controlBuilder = new SAXBuilder(parser);
    }

    /**
     * Overide the parser to use to parser test documents.
     * This is useful when comparing the output of two different 
     * parsers.
     */
    public static void setTestParser(String parser){
        unit.controlBuilder = new SAXBuilder(parser);
    }

    /**
     * Ignore whitespace when comparing nodes.
     */
    public static void setIgnoreWhitespace(boolean ignore){
        ignoreWhitespace = ignore;
    }

    /** 
     * Compare XML documents provided by two Reader classes
     * @throws JDOMException Error thrown in response to an error passing xml doc
     * @param control Control document 
     * @param test Document to test 
     * @return Diff object describing differences in documents
     */
    public static Diff compare(Reader control, Reader test) throws JDOMException {
        Document controlDoc = unit.controlBuilder.build(control);
        Document testDoc = unit.testBuilder.build(test);
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
        Document controlDoc = unit.controlBuilder.build(new StringReader(control));
        Document testDoc = unit.testBuilder.build(test);
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
        Document controlDoc = unit.controlBuilder.build(control);
        Document testDoc = unit.testBuilder.build(new StringReader(test));
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
        if(!ignoreWhitespace){
            if(!control.getText().equals(test.getText()))
                diff.diffElement(control, test);
        }else{
            if(!control.getText().trim().equals(test.getText().trim()))
                diff.diffElement(control, test);
        }

        for(Iterator i = control.getAttributes().iterator();i.hasNext();){
            Attribute att = (Attribute)i.next();
            if(test.getAttribute(att.getName())==null){
                diff.diffAttribute(att, test.getAttribute(att.getName()));
            }else{
                if(!test.getAttribute(att.getName()).getValue().equals(att.getValue())){
                    diff.diffAttribute(att, test.getAttribute(att.getName()));
                }
            }
        }

        if( !control.getName().equals(test.getName())){
            diff.diffElement(control, test);
        }else{
            Iterator iTest = test.getChildren().iterator();
            for(Iterator iControl = control.getChildren().iterator();iControl.hasNext();){
                if(iTest.hasNext()){
                    compare((Element)iControl.next(), (Element)iTest.next(), diff);
                }else{
                    diff.diffElement((Element)iControl.next(), (Element)null);
                    return;
                }
            }
            Iterator iControl = control.getChildren().iterator();
            for(iTest = test.getChildren().iterator();iTest.hasNext();){
                if(iControl.hasNext()){
                    compare((Element)iTest.next(), (Element)iControl.next(), diff);
                }else{
                    diff.diffElement((Element)null, (Element)iTest.next());
                    return;
                }
            }
        }
    }
}
