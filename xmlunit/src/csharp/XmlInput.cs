namespace XmlUnit {
    using System.IO;
    using System.Xml;
    
    public class XmlInput {
        private delegate XmlReader XmlInputTranslator(string baseURI, WhitespaceHandling whitespaceHandling);
        
        private object _originalInput;
        private XmlInputTranslator _translateInput;
        
        public XmlInput(string someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(TranslateString);
        }
        
        public XmlReader WhitespaceAdjusted(XmlTextReader xmlTextReader, WhitespaceHandling whitespaceHandling) {
            xmlTextReader.WhitespaceHandling = whitespaceHandling;
            return xmlTextReader;
        }
        
        private XmlReader TranslateString(string baseURI, WhitespaceHandling whitespaceHandling) {
            return WhitespaceAdjusted(
                new XmlTextReader(baseURI, new StringReader((string) _originalInput)),
                whitespaceHandling);
        }
        
        public XmlInput(Stream someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(TranslateStream);
        }
                
        private XmlReader TranslateStream(string baseURI, WhitespaceHandling whitespaceHandling) {
            return WhitespaceAdjusted(
                new XmlTextReader(baseURI, new StreamReader((Stream) _originalInput)),
                whitespaceHandling);
        }
        
        public XmlInput(TextReader someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(TranslateReader);
        }
                
        private XmlReader TranslateReader(string baseURI, WhitespaceHandling whitespaceHandling) {
            return WhitespaceAdjusted(
                new XmlTextReader(baseURI, (TextReader) _originalInput),
                whitespaceHandling);
        }
        
        public XmlInput(XmlReader someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(NullTranslator);
        }
                
        private XmlReader NullTranslator(string baseURI, WhitespaceHandling whitespaceHandling) {
            return (XmlReader) _originalInput;
        }
        
        public XmlReader CreateXmlReader(string baseURI, WhitespaceHandling whitespaceHandling) {
            return _translateInput(baseURI, whitespaceHandling);
        }
        
        internal XmlReader CreateDefaultXmlReader() {
            return CreateXmlReader(".", WhitespaceHandling.All); 
        }
        
        public override bool Equals(object other) {
            if (other != null && other is XmlInput) {
                return _originalInput.Equals(((XmlInput)other)._originalInput);
            }
            return false;
        }
        
        public override int GetHashCode() {
            return _originalInput.GetHashCode();
        }
    }
}
