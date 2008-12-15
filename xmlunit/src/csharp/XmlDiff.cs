namespace XmlUnit {
    using System;
    using System.Collections;
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    
    public class XmlDiff {
        private const string XMLNS_PREFIX = "xmlns";

        private readonly XmlInput controlInput;
        private readonly XmlInput testInput;
        private readonly DiffConfiguration _diffConfiguration;
        private DiffResult _diffResult;
                
        public XmlDiff(XmlInput control, XmlInput test, 
                       DiffConfiguration diffConfiguration) {
            _diffConfiguration =  diffConfiguration;
            controlInput = control;
            testInput = test;
        }
        
        public XmlDiff(XmlInput control, XmlInput test)
            : this(control, test, new DiffConfiguration()) {
        }

        public XmlDiff(TextReader control, TextReader test)
            : this(new XmlInput(control), new XmlInput(test)) {
        }
        
        public XmlDiff(string control, string test) 
            : this(new XmlInput(control), new XmlInput(test)) {
        }
        
        private XmlReader CreateXmlReader(XmlInput forInput) {
            XmlReader xmlReader = forInput.CreateXmlReader();
        	
        	if (xmlReader is XmlTextReader) {
        		((XmlTextReader) xmlReader ).WhitespaceHandling = _diffConfiguration.WhitespaceHandling;
        	}
            
            if (_diffConfiguration.UseValidatingParser) {
	            XmlValidatingReader validatingReader = new XmlValidatingReader(xmlReader);
	            return validatingReader;
            }
            
            return xmlReader;
        }
        
        public DiffResult Compare() {
            if (_diffResult == null) {
                _diffResult = new DiffResult();
                using (XmlReader controlReader = CreateXmlReader(controlInput))
                using (XmlReader testReader = CreateXmlReader(testInput)) {
                    if (!controlInput.Equals(testInput)) {
                        Compare(_diffResult, controlReader, testReader);
                    }
                }
            }
            return _diffResult;
        }
        
        private void Compare(DiffResult result, XmlReader controlReader,
                             XmlReader testReader) {
            bool controlRead, testRead;
            try {
                do {
                    controlRead = controlReader.Read();
                    testRead = testReader.Read();
                    Compare(result, controlReader, ref controlRead,
                            testReader, ref testRead);
                } while (controlRead && testRead) ;
            } catch (FlowControlException e) {       
                Console.Out.WriteLine(e.Message);
            }
        }        
        
        private void Compare(DiffResult result,
                             XmlReader controlReader, ref bool controlRead,
                             XmlReader testReader, ref bool testRead) {        	
            if (controlRead) {
                if (testRead) {
                    CompareNodes(result, controlReader, testReader);
                    CheckEmptyOrAtEndElement(result,
                                             controlReader, ref controlRead,
                                             testReader, ref testRead);
                } else {
                    DifferenceFound(DifferenceType.CHILD_NODELIST_LENGTH_ID, result);
                } 
            }
        }
                
        private void CompareNodes(DiffResult result, XmlReader controlReader,
                                  XmlReader testReader) {
            XmlNodeType controlNodeType = controlReader.NodeType;
            XmlNodeType testNodeType = testReader.NodeType;
            if (!controlNodeType.Equals(testNodeType)) {
                CheckNodeTypes(controlNodeType, testNodeType, result,
                               controlReader, testReader);
            } else if (controlNodeType == XmlNodeType.Element) {
                CompareElements(result, controlReader, testReader);
            } else if (controlNodeType == XmlNodeType.Text) {
                CompareText(result, controlReader, testReader);
            }
        }
        
        private void CheckNodeTypes(XmlNodeType controlNodeType,
                                    XmlNodeType testNodeType,
                                    DiffResult result,
                                    XmlReader controlReader,
                                    XmlReader testReader) {
            XmlReader readerToAdvance = null;
            if (controlNodeType.Equals(XmlNodeType.XmlDeclaration)) {
                readerToAdvance = controlReader;
            } else if (testNodeType.Equals(XmlNodeType.XmlDeclaration)) {        			
                readerToAdvance = testReader;
            }
        	
            if (readerToAdvance != null) {
            	DifferenceFound(DifferenceType.HAS_XML_DECLARATION_PREFIX_ID, 
            	                controlNodeType, testNodeType, result);
                readerToAdvance.Read();
                CompareNodes(result, controlReader, testReader);
            } else {
            	DifferenceFound(DifferenceType.NODE_TYPE_ID, controlNodeType, 
             	                testNodeType, result);
            }       
        }
        
        private void CompareElements(DiffResult result, XmlReader controlReader,
                                     XmlReader testReader) {
            string controlTagName = controlReader.Name;
            string testTagName = testReader.Name;
            if (!String.Equals(controlTagName, testTagName)) {
                DifferenceFound(DifferenceType.ELEMENT_TAG_NAME_ID, result);
            } else {
                XmlAttribute[] controlAttributes =
                    GetNonSpecialAttributes(controlReader);
                XmlAttribute[] testAttributes =
                    GetNonSpecialAttributes(testReader);
                if (controlAttributes.Length != testAttributes.Length) {
                    DifferenceFound(DifferenceType.ELEMENT_NUM_ATTRIBUTES_ID, result);
                } else {
                    CompareAttributes(result, controlAttributes, testAttributes);
                }
            }
        }
        
        private void CompareAttributes(DiffResult result,
                                       XmlAttribute[] controlAttributes,
                                       XmlAttribute[] testAttributes) {
            ArrayList unmatchedTestAttributes = new ArrayList();
            unmatchedTestAttributes.AddRange(testAttributes);
            for (int i=0; i < controlAttributes.Length; ++i) {
                
                bool controlIsInNs = IsNamespaced(controlAttributes[i]);
                string controlAttrName =
                    GetUnNamespacedNodeName(controlAttributes[i]);
                XmlAttribute testAttr = null;
                if (!controlIsInNs) {
                    testAttr = FindAttributeByName(testAttributes,
                                                   controlAttrName);
                } else {
                    testAttr = FindAttributeByNameAndNs(testAttributes,
                                                        controlAttrName,
                                                        controlAttributes[i]
                                                        .NamespaceURI);
                }

                if (testAttr != null) {
                    unmatchedTestAttributes.Remove(testAttr);
                    if (!_diffConfiguration.IgnoreAttributeOrder
                        && testAttr != testAttributes[i]) {
                        DifferenceFound(DifferenceType.ATTR_SEQUENCE_ID,
                                        result);
                    }

                    if (controlAttributes[i].Value != testAttr.Value) {
                    Console.Error.WriteLine("control: {0}, expected {1}, was {2}",
                                            controlAttrName,
                    controlAttributes[i].Value, testAttr.Value);
                        DifferenceFound(DifferenceType.ATTR_VALUE_ID, result);
                    }

                } else {
                    DifferenceFound(DifferenceType.ATTR_NAME_NOT_FOUND_ID,
                                    result);
                }
            }
            foreach (XmlAttribute a in unmatchedTestAttributes) {
                DifferenceFound(DifferenceType.ATTR_NAME_NOT_FOUND_ID, result);
            }
        }
        
      private void CompareText(DiffResult result, XmlReader controlReader,
                               XmlReader testReader) {
            string controlText = controlReader.Value;
            string testText = testReader.Value;
            if (!String.Equals(controlText, testText)) {
                DifferenceFound(DifferenceType.TEXT_VALUE_ID, result);
            }
        }
        
        private void DifferenceFound(DifferenceType differenceType, DiffResult result) {
            DifferenceFound(new Difference(differenceType), result);
        }
        
        private void DifferenceFound(Difference difference, DiffResult result) {
            result.DifferenceFound(this, difference);
            if (!ContinueComparison(difference)) {
                throw new FlowControlException(difference);
            }
        }
        
        private void DifferenceFound(DifferenceType differenceType, 
                                     XmlNodeType controlNodeType,
                                     XmlNodeType testNodeType, 
                                     DiffResult result) {
            DifferenceFound(new Difference(differenceType, controlNodeType, testNodeType),
                            result);
        }
        
        private bool ContinueComparison(Difference afterDifference) {
            return !afterDifference.MajorDifference;
        }
        
        private void CheckEmptyOrAtEndElement(DiffResult result, 
                                              XmlReader controlReader,
                                              ref bool controlRead,
                                              XmlReader testReader,
                                              ref bool testRead) {
            if (controlReader.IsEmptyElement) {
                if (!testReader.IsEmptyElement) {
                    CheckEndElement(testReader, ref testRead, result);
                }
            } else {
                if (testReader.IsEmptyElement) {
                    CheckEndElement(controlReader, ref controlRead, result);
                }
            }
        }
        
        private XmlAttribute[] GetNonSpecialAttributes(XmlReader r) {
            ArrayList l = new ArrayList();
            int length = r.AttributeCount;
            if (length > 0) {
                XmlDocument doc = new XmlDocument();
                r.MoveToFirstAttribute();
                for (int i = 0; i < length; i++) {
                    XmlAttribute a = doc.CreateAttribute(r.Name, r.NamespaceURI);
                    if (!IsXMLNSAttribute(a)) {
                        l.Add(a);
                    }
                    a.Value = r.Value;
                    r.MoveToNextAttribute();
                }
            }
            return (XmlAttribute[]) l.ToArray(typeof(XmlAttribute));
        }

        private bool IsXMLNSAttribute(XmlAttribute attribute) {
            return XMLNS_PREFIX == attribute.Prefix ||
                XMLNS_PREFIX == attribute.Name;
        }

        private XmlAttribute FindAttributeByName(XmlAttribute[] attrs,
                                                 string name) {
            foreach (XmlAttribute a in attrs) {
                if (GetUnNamespacedNodeName(a) == name) {
                    return a;
                }
            }
            return null;
        }

        private XmlAttribute FindAttributeByNameAndNs(XmlAttribute[] attrs,
                                                      string name,
                                                      string nsUri) {
            foreach (XmlAttribute a in attrs) {
                if (GetUnNamespacedNodeName(a) == name
                    && a.NamespaceURI == nsUri) {
                    return a;
                }
            }
            return null;
        }

        private string GetUnNamespacedNodeName(XmlNode aNode) {
            return GetUnNamespacedNodeName(aNode, IsNamespaced(aNode));
        }
    
        private string GetUnNamespacedNodeName(XmlNode aNode,
                                               bool isNamespacedNode) {
            if (isNamespacedNode) {
                return aNode.LocalName;
            }
            return aNode.Name;
        }

        private bool IsNamespaced(XmlNode aNode) {
            string ns = aNode.NamespaceURI;
            return ns != null && ns.Length > 0;
        }

        private void CheckEndElement(XmlReader reader, ref bool readResult, DiffResult result) {            
            readResult = reader.Read();
            if (!readResult || reader.NodeType != XmlNodeType.EndElement) {
                DifferenceFound(DifferenceType.CHILD_NODELIST_LENGTH_ID, result);
            }        
        }
        
        private class FlowControlException : ApplicationException {
            public FlowControlException(Difference cause) : base(cause.ToString()) {
            }
        }
        
        public string OptionalDescription {
            get {
                return _diffConfiguration.Description;
            }
        }
    }
}
