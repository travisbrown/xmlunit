package org.custommonkey.xmlunit;

import junit.framework.*;
import junit.textui.TestRunner;

import java.io.StringReader;
import javax.xml.transform.OutputKeys;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * JUnit test for SimpleSerializer
 */
public class test_SimpleSerializer extends TestCase {
    private SimpleSerializer serializer ;

    public void testNode() throws Exception {
        String simpleXML = "<season><spring id=\"1\"><eg>daffodils</eg></spring></season>";
        Document doc = XMLUnit.buildControlDocument(simpleXML);

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        assertEquals(false, simpleXML.equals(serializer.serialize(doc)));

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        assertEquals(simpleXML, serializer.serialize(doc));

        Element testElem = doc.createElement("eg");
        Text lamb = doc.createTextNode("lamb");
        testElem.appendChild(lamb);

        assertEquals("<eg>lamb</eg>", serializer.serialize(testElem));
    }

    public void setUp() {
        serializer = new SimpleSerializer();
    }

    public test_SimpleSerializer(String name) {
        super(name);
    }

    public static TestSuite suite() {
        return new TestSuite(test_SimpleSerializer.class);
    }

    public static void main(String[] args) {
        new TestRunner().run(suite());
    }
}

