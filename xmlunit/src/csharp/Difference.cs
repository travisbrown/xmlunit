namespace XmlUnit {
    using System.Xml;    
    
    public class Difference {
        private DifferenceType _id;
        private bool _majorDifference;
        private XmlNodeType _controlNodeType;
        private XmlNodeType _testNodeType;
        
        internal Difference(DifferenceType id, bool isMajorDifference) {
            Init(id, isMajorDifference);
        }
        
        private void Init(DifferenceType id, bool isMajorDifference) {
            _id = id;
            _majorDifference = isMajorDifference;
        }
        
        public Difference(Difference prototype, XmlNodeType controlNodeType,
                          XmlNodeType testNodeType) {
            Init(prototype.Id, prototype.MajorDifference);
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
