namespace XmlUnit {
    using System.Xml;
    
    public class XmlUnitConfiguration {
        private XmlUnitConfiguration() {}
        private static WhitespaceHandling _whitespaceHandling = WhitespaceHandling.All;
        
        public static WhitespaceHandling WhitespaceHandling {
            get {
                return _whitespaceHandling;
            }
            set {
                _whitespaceHandling = value;
            }
        }
    }
}
