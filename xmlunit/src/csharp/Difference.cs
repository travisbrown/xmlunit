namespace XmlUnit {
    using System.Xml;    
    
    public class Difference {
        private readonly DifferenceType _id;
        private readonly bool _majorDifference;
        private XmlNodeType _controlNodeType;
        private XmlNodeType _testNodeType;
        
        internal Difference(DifferenceType id, bool isMajorDifference) {
            _id = id;
            _majorDifference = isMajorDifference;
        }
        
        public Difference(Difference prototype, XmlNodeType controlNodeType, XmlNodeType testNodeType) 
        : this(prototype.Id, prototype.MajorDifference) {
            _controlNodeType = controlNodeType;
            _testNodeType = testNodeType;
        }
        
        public DifferenceType Id {
            get {
                return _id;
            }
        }
        
        public bool MajorDifference {
            get {
                return _majorDifference;
            }
        }
        
        public XmlNodeType ControlNodeType {
            get {
                return _controlNodeType;
            }
        }
        
        public XmlNodeType TestNodeType {
            get {
                return _testNodeType;
            }
        }
        
        public override string ToString() {
            string asString = base.ToString() + " type: " + (int) _id 
                + ", control Node: " + _controlNodeType.ToString()
                + ", test Node: " + _testNodeType.ToString();            
            return asString;
        }
    }
}
