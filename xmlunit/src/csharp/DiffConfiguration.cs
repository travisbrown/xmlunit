namespace XmlUnit {
    using System.Xml;
    
    public class DiffConfiguration {
        private readonly WhitespaceHandling _whitespaceHandling;
        
        public DiffConfiguration(WhitespaceHandling whitespaceHandling) {
            _whitespaceHandling = whitespaceHandling;
        }
        
        public DiffConfiguration() : this(WhitespaceHandling.All) {}
        
        public WhitespaceHandling WhitespaceHandling {
            get {
                return _whitespaceHandling;
            }
        }
    }
}
