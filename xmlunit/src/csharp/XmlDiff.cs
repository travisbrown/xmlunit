namespace XmlUnit {
    using System;
    using System.IO;
    using System.Xml;
    
    public class XmlDiff {
        private XmlReader _controlReader; 
        private XmlReader _testReader;
        private DiffResult _diffResult;
        
        public XmlDiff(TextReader control, TextReader test) {
            _controlReader = CreateXmlReader(control);
            if (control.Equals(test)) {
                _testReader = _controlReader;
            } else {
                _testReader = CreateXmlReader(test);
            }
        }
        
        private XmlReader CreateXmlReader(TextReader forTextReader) {
            XmlTextReader xmlReader = new XmlTextReader(forTextReader);
            xmlReader.WhitespaceHandling = XmlUnitConfiguration.WhitespaceHandling;
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
            bool controlHasNext, testHasNext;
            bool compareMore = true;
            do {
                controlHasNext = ReadIgnoringEndElement(_controlReader);
                testHasNext = ReadIgnoringEndElement(_testReader);
                
                if (controlHasNext != testHasNext) {
                    compareMore = DifferenceFound(Differences.CHILD_NODELIST_LENGTH, result);
                } else {
                    XmlNodeType controlNodeType = _controlReader.NodeType;
                    XmlNodeType testNodeType = _testReader.NodeType;
                    if (!controlNodeType.Equals(testNodeType)) {
                        compareMore = DifferenceFound(Differences.NODE_TYPE, controlNodeType,
                                                      testNodeType, result);
                    } else if (controlNodeType == XmlNodeType.Element) {
                        string controlTagName = _controlReader.Name;
                        string testTagName = _testReader.Name;
                        if (!String.Equals(controlTagName, testTagName)) {
                            compareMore = DifferenceFound(Differences.ELEMENT_TAG_NAME, result);
                        } else {
                            int controlAttributeCount = _controlReader.AttributeCount;
                            int testAttributeCount = _testReader.AttributeCount;
                            if (controlAttributeCount != testAttributeCount) {
                                compareMore = DifferenceFound(Differences.ELEMENT_NUM_ATTRIBUTES, result);
                            } else {
                                string controlAttrValue, controlAttrName;
                                string testAttrValue, testAttrName;
                                
                                _controlReader.MoveToFirstAttribute();
                                _testReader.MoveToFirstAttribute();
                                for (int i=0; compareMore && i < controlAttributeCount; ++i) {
                                    
                                    controlAttrName = _controlReader.Name;
                                    testAttrName = _testReader.Name;
                                    
                                    controlAttrValue = _controlReader.Value;
                                    testAttrValue = _testReader.Value;
                                    
                                    if (!String.Equals(controlAttrName, testAttrName)) {
                                        compareMore = DifferenceFound(Differences.ATTR_SEQUENCE, result);
                                    
                                        if (compareMore) {
                                            if (!_testReader.MoveToAttribute(controlAttrName)) {
                                                compareMore = DifferenceFound(Differences.ATTR_NAME_NOT_FOUND, result);
                                            }                                        
                                        }
                                        if (compareMore) {
                                            testAttrValue = _testReader.Value;
                                        }
                                    }
                                    
                                    if (compareMore && !String.Equals(controlAttrValue, testAttrValue)) {
                                        compareMore = DifferenceFound(Differences.ATTR_VALUE, result);
                                    }
                                    
                                    _controlReader.MoveToNextAttribute();
                                    _testReader.MoveToNextAttribute();
                                }
                            }
                        }
                        
                    }
                }
            } while (compareMore && controlHasNext && testHasNext) ;
        }
        
        private bool ReadIgnoringEndElement(XmlReader reader) {
            bool read = reader.Read();
            while (read && reader.NodeType == XmlNodeType.EndElement) {
                read = reader.Read();
            }
            return read;
        }
        
        private bool DifferenceFound(Difference difference, DiffResult result) {
            result.DifferenceFound(difference);
            return ContinueComparison(difference);
        }
        
        private bool DifferenceFound(Difference difference, XmlNodeType controlNodeType, 
                                     XmlNodeType testNodeType, DiffResult result) {
            return DifferenceFound(new Difference(difference, controlNodeType, testNodeType),
                            result);
        }
        
        public bool ContinueComparison(Difference afterDifference) {
            return !afterDifference.MajorDifference;
        }
    }
}
