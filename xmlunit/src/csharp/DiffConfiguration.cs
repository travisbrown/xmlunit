namespace XmlUnit {
    using System.Xml;
    
    public class DiffConfiguration {
        public static readonly WhitespaceHandling DEFAULT_WHITESPACE_HANDLING = WhitespaceHandling.All;
        public static readonly string DEFAULT_DESCRIPTION = "";
        public static readonly bool DEFAULT_USE_VALIDATING_PARSER = true;
        public static readonly string DEFAULT_BASE_URI = ".";
        
        private readonly string _description;
        private readonly bool _useValidatingParser;
        private readonly string _baseURI;
        private readonly WhitespaceHandling _whitespaceHandling;
        
        public DiffConfiguration(string description, 
                                 bool useValidatingParser, string baseURI, 
                                 WhitespaceHandling whitespaceHandling) {
            _description = description;
            _useValidatingParser = useValidatingParser;
            _baseURI = baseURI;
            _whitespaceHandling = whitespaceHandling;
        }
        
        public DiffConfiguration(string description, 
                                 WhitespaceHandling whitespaceHandling)
        : this (description, 
                DEFAULT_USE_VALIDATING_PARSER, DEFAULT_BASE_URI, 
                whitespaceHandling) {}
        
        public DiffConfiguration(WhitespaceHandling whitespaceHandling)
        : this(DEFAULT_DESCRIPTION, 
               DEFAULT_USE_VALIDATING_PARSER, DEFAULT_BASE_URI, 
               whitespaceHandling) {}
        
        public DiffConfiguration(string description) 
        : this(description, 
               DEFAULT_USE_VALIDATING_PARSER, DEFAULT_BASE_URI, 
               DEFAULT_WHITESPACE_HANDLING) {}
                
        public DiffConfiguration(bool useValidatingParser, string baseURI) 
        : this(DEFAULT_DESCRIPTION, 
               useValidatingParser, baseURI, 
               DEFAULT_WHITESPACE_HANDLING) {
        }
        
        public DiffConfiguration() 
        : this(DEFAULT_DESCRIPTION, 
               DEFAULT_USE_VALIDATING_PARSER, DEFAULT_BASE_URI, 
               DEFAULT_WHITESPACE_HANDLING) {}
        
        public string Description {
            get {
                return _description;
            }
        }
        
        public bool UseValidatingParser {
            get {
                return _useValidatingParser;
            }
        }
        
        public string BaseURI {
            get {
                return _baseURI;
            }
        }
        
        public WhitespaceHandling WhitespaceHandling {
            get {
                return _whitespaceHandling;
            }
        }
    }
}
