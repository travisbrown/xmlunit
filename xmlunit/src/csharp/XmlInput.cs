namespace XmlUnit {
    using System.IO;
    using System.Xml;
    
    public class XmlInput {
        public delegate XmlReader XmlInputTranslator();
        
        private object _originalInput;
        private XmlInputTranslator _translateInput;
        
        public XmlInput(string someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(TranslateString);
        }
        
        private XmlReader TranslateString() {
            return new XmlTextReader(new StringReader((string) _originalInput));
        }
        
        public XmlInput(Stream someXml) {
            _originalInput = someXml;
            _translateInput = new XmlInputTranslator(TranslateStream);
        }
                
        private XmlReader TranslateStream() {
            return new XmlTextReader(new StreamReader((Stream) _originalInput));
        }
        
        public XmlReader CreateXmlReader() {
            return _translateInput();
        }
        
    }
}
