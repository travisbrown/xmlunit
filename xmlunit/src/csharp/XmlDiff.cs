namespace XmlUnit {
    using System;
    using System.IO;
    using System.Xml;
    
    public class XmlDiff {
        private readonly XmlReader _controlReader; 
        private readonly XmlReader _testReader;
        private DiffResult _diffResult;
                
        public XmlDiff(TextReader control, TextReader test, 
                       XmlUnitConfiguration xmlUnitConfiguration) {
            _controlReader = CreateXmlReader(control, xmlUnitConfiguration);
            if (control.Equals(test)) {
                _testReader = _controlReader;
            } else {
                _testReader = CreateXmlReader(test, xmlUnitConfiguration);
            }
        }
        public XmlDiff(TextReader control, TextReader test)
            : this(control, test, new XmlUnitConfiguration()) {
        }
                
        public XmlDiff(string control, string test) 
            : this(new StringReader(control), new StringReader(test)) {
        }
        
        public XmlDiff(string control, string test, XmlUnitConfiguration xmlUnitConfiguration) 
            : this(new StringReader(control), new StringReader(test), xmlUnitConfiguration) {
        }
        
        public XmlDiff(string control, TextReader test) 
            : this(new StringReader(control), test) {
        }
        
        public XmlDiff(TextReader control, string test) 
            : this(control, new StringReader(test)) {
        }
        
        private XmlReader CreateXmlReader(TextReader forTextReader, 
                                          XmlUnitConfiguration xmlUnitConfiguration) {
            XmlTextReader xmlReader = new XmlTextReader(forTextReader);
            xmlReader.WhitespaceHandling = xmlUnitConfiguration.WhitespaceHandling;
            return xmlReader;
        }
        
        public DiffResult Compare() {
            if (_diffResult == null) {
                _diffResult = new DiffResult();
                if (!_controlReader.Equals(_testReader)) {
                    Compare(_diffResult);
                }
            }
            return _diffResult;
        }
        
        private void Compare(DiffResult result) {
            bool controlRead, testRead;
            try {
                do {
                    controlRead = _controlReader.Read();
                    testRead = _testReader.Read();
                    if (controlRead) {
                        if(testRead) {
                            CompareNodes(result);
                            CheckAndCloseNonEmptyElements(result, ref controlRead, ref testRead);
                        } else {
                            DifferenceFound(Differences.CHILD_NODELIST_LENGTH, result);
                        } 
                    }
                } while (controlRead && testRead) ;
            } catch (FlowControlException e) {       
                Console.Out.WriteLine(e.Message);
            }
        }        
                
        private void CompareNodes(DiffResult result) {
            XmlNodeType controlNodeType = _controlReader.NodeType;
            XmlNodeType testNodeType = _testReader.NodeType;
            if (!controlNodeType.Equals(testNodeType)) {
                DifferenceFound(Differences.NODE_TYPE, controlNodeType, testNodeType, result);
            } else if (controlNodeType == XmlNodeType.Element) {
                string controlTagName = _controlReader.Name;
                string testTagName = _testReader.Name;
                if (!String.Equals(controlTagName, testTagName)) {
                    DifferenceFound(Differences.ELEMENT_TAG_NAME, result);
                } else {
                    int controlAttributeCount = _controlReader.AttributeCount;
                    int testAttributeCount = _testReader.AttributeCount;
                    if (controlAttributeCount != testAttributeCount) {
                        DifferenceFound(Differences.ELEMENT_NUM_ATTRIBUTES, result);
                    } else {
                        CompareAttributes(result, controlAttributeCount);
                    }
                }
            } else if (controlNodeType == XmlNodeType.Text) {
                string controlText = _controlReader.Value;
                string testText = _testReader.Value;
                if (!String.Equals(controlText, testText)) {
                    DifferenceFound(Differences.TEXT_VALUE, result);
                }
            }
        }
        
        private void CompareAttributes(DiffResult result, int controlAttributeCount) {
            string controlAttrValue, controlAttrName;
            string testAttrValue, testAttrName;
            
            _controlReader.MoveToFirstAttribute();
            _testReader.MoveToFirstAttribute();
            for (int i=0; i < controlAttributeCount; ++i) {
                
                controlAttrName = _controlReader.Name;
                testAttrName = _testReader.Name;
                
                controlAttrValue = _controlReader.Value;
                testAttrValue = _testReader.Value;
                
                if (!String.Equals(controlAttrName, testAttrName)) {
                    DifferenceFound(Differences.ATTR_SEQUENCE, result);
                
                    if (!_testReader.MoveToAttribute(controlAttrName)) {
                        DifferenceFound(Differences.ATTR_NAME_NOT_FOUND, result);
                    }
                    testAttrValue = _testReader.Value;
                }
                
                if (!String.Equals(controlAttrValue, testAttrValue)) {
                    DifferenceFound(Differences.ATTR_VALUE, result);
                }
                
                _controlReader.MoveToNextAttribute();
                _testReader.MoveToNextAttribute();
            }
        }
        
        private void DifferenceFound(Difference difference, DiffResult result) {
            result.DifferenceFound(difference);
            if (!ContinueComparison(difference)) {
                throw new FlowControlException(difference);
            }
        }
        
        private void DifferenceFound(Difference difference, XmlNodeType controlNodeType, 
                                     XmlNodeType testNodeType, DiffResult result) {
            DifferenceFound(new Difference(difference, controlNodeType, testNodeType),
                            result);
        }
        
        public bool ContinueComparison(Difference afterDifference) {
            return !afterDifference.MajorDifference;
        }
        
        private void CheckAndCloseNonEmptyElements(DiffResult result, 
                                                   ref bool controlRead, ref bool testRead) {
            if (_controlReader.IsEmptyElement) {
                if (!_testReader.IsEmptyElement) {
                    testRead = _testReader.Read();
                    if (!testRead || _testReader.NodeType != XmlNodeType.EndElement) {
                        DifferenceFound(Differences.CHILD_NODELIST_LENGTH, result);
                    }
                }
            } else {
                if (_testReader.IsEmptyElement) {
                    controlRead = _controlReader.Read();
                    if (!controlRead || _controlReader.NodeType != XmlNodeType.EndElement) {
                        DifferenceFound(Differences.CHILD_NODELIST_LENGTH, result);
                    }
                }
            }
        }
        
        private class FlowControlException : ApplicationException {
            public FlowControlException(Difference cause) : base(cause.ToString()) {
            }
        }
    }
}
