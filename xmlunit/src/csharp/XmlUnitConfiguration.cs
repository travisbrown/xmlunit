namespace XmlUnit {
    using System.Xml;
    
    public class XmlUnitConfiguration {
        private readonly WhitespaceHandling _whitespaceHandling;
        
        public XmlUnitConfiguration(WhitespaceHandling whitespaceHandling) {
            _whitespaceHandling = whitespaceHandling;
        }
        
        public XmlUnitConfiguration() : this(WhitespaceHandling.All) {}
        
        public WhitespaceHandling WhitespaceHandling {
            get {
                return _whitespaceHandling;
            }
        }
    }
}
