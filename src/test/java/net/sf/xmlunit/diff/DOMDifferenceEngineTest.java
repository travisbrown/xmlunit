/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package net.sf.xmlunit.diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sf.xmlunit.NullNode;
import net.sf.xmlunit.TestResources;
import net.sf.xmlunit.builder.Input;
import net.sf.xmlunit.util.Convert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import static org.junit.Assert.*;

public class DOMDifferenceEngineTest extends AbstractDifferenceEngineTest {

    @Override protected AbstractDifferenceEngine getDifferenceEngine() {
        return new DOMDifferenceEngine();
    }

    private static class DiffExpecter implements ComparisonListener {
        private int invoked = 0;
        private final int expectedInvocations;
        private final ComparisonType type;
        private final boolean withXPath;
        private final String controlXPath;
        private final String testXPath;
        private DiffExpecter(ComparisonType type) {
            this(type, 1);
        }
        private DiffExpecter(ComparisonType type, int expected) {
            this(type, expected, false, null, null);
        }
        private DiffExpecter(ComparisonType type, String controlXPath,
                             String testXPath) {
            this(type, 1, true, controlXPath, testXPath);
        }
        private DiffExpecter(ComparisonType type, int expected,
                             boolean withXPath, String controlXPath,
                             String testXPath) {
            this.type = type;
            this.expectedInvocations = expected;
            this.withXPath = withXPath;
            this.controlXPath = controlXPath;
            this.testXPath = testXPath;
        }
        public void comparisonPerformed(Comparison comparison,
                                        ComparisonResult outcome) {
            assertTrue(invoked + " should be less than " + expectedInvocations,
                       invoked < expectedInvocations);
            invoked++;
            assertEquals(type, comparison.getType());
            assertEquals(ComparisonResult.CRITICAL, outcome);
            if (withXPath) {
                assertEquals("Control XPath", controlXPath,
                             comparison.getControlDetails().getXPath());
                assertEquals("Test XPath", testXPath,
                             comparison.getTestDetails().getXPath());
            }
        }
    }

    private Document doc;

    @Before public void createDoc() throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .newDocument();
    }

    @Test public void compareNodesOfDifferentType() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElement("x"), new XPathContext(),
                                    doc.createComment("x"), new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesWithoutNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NODE_TYPE, 0);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(doc.createElement("x"), new XPathContext(),
                                    doc.createElement("x"), new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test public void compareNodesDifferentNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_URI);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "y"),
                                    new XPathContext(),
                                    doc.createElementNS("z", "y"),
                                    new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentPrefix() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.NAMESPACE_PREFIX);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.NAMESPACE_PREFIX) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(doc.createElementNS("x", "x:y"),
                                    new XPathContext(),
                                    doc.createElementNS("x", "z:y"),
                                    new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareNodesDifferentNumberOfChildren() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex =
            new DiffExpecter(ComparisonType.CHILD_NODELIST_LENGTH, 2);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Element e1 = doc.createElement("x");
        Element e2 = doc.createElement("x");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        e1.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        e2.appendChild(doc.createElement("x"));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(2, ex.invoked);
    }

    @Test public void compareCharacterData() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.TEXT_VALUE, 9);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NODE_TYPE) {
                        if (outcome == ComparisonResult.EQUAL
                            || (
                                comparison.getControlDetails()
                                .getTarget() instanceof CharacterData
                                &&
                                comparison.getTestDetails()
                                .getTarget() instanceof CharacterData
                                )) {
                            return ComparisonResult.EQUAL;
                        }
                    }
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            });

        Comment fooComment = doc.createComment("foo");
        Comment barComment = doc.createComment("bar");
        Text fooText = doc.createTextNode("foo");
        Text barText = doc.createTextNode("bar");
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        CDATASection barCDATASection = doc.createCDATASection("bar");

        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barCDATASection, new XPathContext()));

        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooComment, new XPathContext(),
                                    barCDATASection, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    fooCDATASection, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooText, new XPathContext(),
                                    barCDATASection, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooText, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barText, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    fooComment, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(fooCDATASection, new XPathContext(),
                                    barComment, new XPathContext()));
        assertEquals(9, ex.invoked);
    }

    @Test public void compareProcessingInstructions() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_TARGET);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        ProcessingInstruction foo1 = doc.createProcessingInstruction("foo", "1");
        ProcessingInstruction bar1 = doc.createProcessingInstruction("bar", "1");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(foo1, new XPathContext(),
                                    foo1, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(foo1, new XPathContext(),
                                    bar1, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.PROCESSING_INSTRUCTION_DATA);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        ProcessingInstruction foo2 = doc.createProcessingInstruction("foo", "2");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(foo1, new XPathContext(),
                                    foo1, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(foo1, new XPathContext(),
                                    foo2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareDocuments() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.HAS_DOCTYPE_DECLARATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.HAS_DOCTYPE_DECLARATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals("Expected EQUAL for " + comparison.getType()
                                 + " comparison.",
                                 ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        Document d1 = Convert.toDocument(Input.fromMemory("<Book/>").build());
        Document d2 =
            Convert.toDocument(Input.fromMemory("<!DOCTYPE Book PUBLIC "
                                                + "\"XMLUNIT/TEST/PUB\" "
                                                + "\"src/test/resources" + TestResources.BOOK_DTD
                                                + "\">"
                                                + "<Book/>")
                               .build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_VERSION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.1\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_STANDALONE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " standalone=\"yes\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " standalone=\"no\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.XML_ENCODING);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.XML_ENCODING) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });

        d1 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-8\"?>"
                                                 + "<Book/>").build());
        d2 = Convert.toDocument(Input.fromMemory("<?xml version=\"1.0\""
                                                 + " encoding=\"UTF-16\"?>"
                                                 + "<Book/>").build());
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(d1, new XPathContext(),
                                    d2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    private static class DocType extends NullNode implements DocumentType {
        private final String name, publicId, systemId;
        private DocType(String name, String publicId, String systemId) {
            this.name = name;
            this.publicId = publicId;
            this.systemId = systemId;
        }
        @Override public short getNodeType() {
            return Node.DOCUMENT_TYPE_NODE;
        }
        public NamedNodeMap getEntities() {
            return null;
        }
        public String getInternalSubset() {
            return null;
        }
        public String getName() {
            return name;
        }
        public NamedNodeMap getNotations() {
            return null;
        }
        public String getPublicId() {
            return publicId;
        }
        public String getSystemId() {
            return systemId;
        }
    }

    @Test public void compareDocTypes() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.DOCTYPE_NAME);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        DocumentType dt1 = new DocType("name", "pub", "system");
        DocumentType dt2 = new DocType("name2", "pub", "system");
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_PUBLIC_ID);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        dt2 = new DocType("name", "pub2", "system");
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.DOCTYPE_SYSTEM_ID);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType()
                        == ComparisonType.DOCTYPE_SYSTEM_ID) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        dt2 = new DocType("name", "pub", "system2");
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(dt1, new XPathContext(),
                                    dt2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareElements() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e3, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ELEMENT_NUM_ATTRIBUTES);
        e1.setAttribute("attr1", "value1");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.ATTR_NAME_LOOKUP,
                              "/@attr1", "/");
        e2.setAttributeNS("urn:xmlunit:test", "attr1", "value1");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);


        d = new DOMDifferenceEngine();
        d.addDifferenceListener(new ComparisonListener() {
                public void comparisonPerformed(Comparison comparison,
                                                ComparisonResult outcome) {
                    fail("unexpected Comparison of type " + comparison.getType()
                         + " with outcome " + outcome + " and values '"
                         + comparison.getControlDetails().getValue()
                         + "' and '"
                         + comparison.getTestDetails().getValue() + "'");
                }
            });
        e1.setAttributeNS("urn:xmlunit:test", "attr1", "value1");
        e2.setAttributeNS(null, "attr1", "value1");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
    }

    @Test public void compareAttributes() {
        Attr a1 = doc.createAttribute("foo");
        Attr a2 = doc.createAttribute("foo");

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ATTR_VALUE_EXPLICITLY_SPECIFIED);
        /* Can't reset "explicitly set" state for Documents created via API
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.Accept);
        a2.setValue("");
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(a1, new XPathContext(),
                                    a2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        */
        ex = new DiffExpecter(ComparisonType.ATTR_VALUE);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        Attr a3 = doc.createAttribute("foo");
        a1.setValue("foo");
        a2.setValue("foo");
        a3.setValue("bar");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(a1, new XPathContext(),
                                    a2, new XPathContext()));
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(a1, new XPathContext(),
                                    a3, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void naiveRecursion() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element c1 = doc.createElement("bar");
        e1.appendChild(c1);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP,
                                           "/bar[1]", null);
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.CHILD_NODELIST_LENGTH) {
                        return ComparisonResult.EQUAL;
                    }
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            };
        d.setDifferenceEvaluator(ev);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        // symmetric?
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP, null, "/bar[1]");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(ev);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
        assertEquals(1, ex.invoked);

        Element c2 = doc.createElement("bar");
        e2.appendChild(c2);
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(ev);
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test public void textAndCDataMatchRecursively() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Text fooText = doc.createTextNode("foo");
        e1.appendChild(fooText);
        CDATASection fooCDATASection = doc.createCDATASection("foo");
        e2.appendChild(fooCDATASection);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e2, new XPathContext(),
                                    e1, new XPathContext()));
    }

    @Test public void recursionUsesElementSelector() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        e1.appendChild(e3);
        Element e4 = doc.createElement("baz");
        e2.appendChild(e4);
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME,
                                           "/bar[1]", "/baz[1]");
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        d = new DOMDifferenceEngine();
        d.setNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName));
        ex = new DiffExpecter(ComparisonType.CHILD_LOOKUP, "/bar[1]", null);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(DifferenceEvaluators.DefaultStopWhenDifferent);
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void schemaLocationDifferences() {
        Element e1 = doc.createElement("foo");
        Element e2 = doc.createElement("foo");
        e1.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "schemaLocation", "somewhere");
        e2.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "schemaLocation", "somewhere else");

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.SCHEMA_LOCATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.SCHEMA_LOCATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);

        e1 = doc.createElement("foo");
        e2 = doc.createElement("foo");
        e1.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "noNamespaceSchemaLocation", "somewhere");
        e2.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance",
                          "noNamespaceSchemaLocation", "somewhere else");
        d = new DOMDifferenceEngine();
        ex = new DiffExpecter(ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION);
        d.addDifferenceListener(ex);
        d.setDifferenceEvaluator(new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NO_NAMESPACE_SCHEMA_LOCATION) {
                        assertEquals(ComparisonResult.DIFFERENT, outcome);
                        return ComparisonResult.CRITICAL;
                    }
                    assertEquals(ComparisonResult.EQUAL, outcome);
                    return ComparisonResult.EQUAL;
                }
            });
        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

    @Test public void compareElementsNS() {
        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.ELEMENT_TAG_NAME);
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (comparison.getType() == ComparisonType.NAMESPACE_PREFIX) {
                        return ComparisonResult.EQUAL;
                    }
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            };
        d.setDifferenceEvaluator(ev);
        Element e1 = doc.createElementNS("urn:xmlunit:test", "foo");
        e1.setPrefix("p1");
        Element e2 = doc.createElementNS("urn:xmlunit:test", "foo");
        e2.setPrefix("p2");
        assertEquals(ComparisonResult.EQUAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(0, ex.invoked);
    }

    @Test public void childNodeListSequence() {
        Element e1 = doc.createElement("foo");
        Element e3 = doc.createElement("bar");
        Element e4 = doc.createElement("baz");
        e1.appendChild(e3);
        e1.appendChild(e4);

        Element e2 = doc.createElement("foo");
        Element e5 = doc.createElement("bar");
        Element e6 = doc.createElement("baz");
        e2.appendChild(e6);
        e2.appendChild(e5);

        DOMDifferenceEngine d = new DOMDifferenceEngine();
        DiffExpecter ex = new DiffExpecter(ComparisonType.CHILD_NODELIST_SEQUENCE,
                                           "/bar[1]", "/bar[1]");
        d.addDifferenceListener(ex);
        DifferenceEvaluator ev = new DifferenceEvaluator() {
                public ComparisonResult evaluate(Comparison comparison,
                                                 ComparisonResult outcome) {
                    if (outcome != ComparisonResult.EQUAL
                        && comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE) {
                        return ComparisonResult.CRITICAL;
                    }
                    return DifferenceEvaluators.DefaultStopWhenDifferent
                        .evaluate(comparison, outcome);
                }
            };
        d.setDifferenceEvaluator(ev);
        d.setNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName));

        assertEquals(ComparisonResult.CRITICAL,
                     d.compareNodes(e1, new XPathContext(),
                                    e2, new XPathContext()));
        assertEquals(1, ex.invoked);
    }

}
