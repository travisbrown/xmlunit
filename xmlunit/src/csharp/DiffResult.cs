namespace XmlUnit {
        
    public class DiffResult {
        private bool _identical = true;
        private bool _equal = true;
        private Difference _difference;
        
        public bool Identical {
            get {
                return _identical;
            }
        }
        
        public bool Equal {
            get {
                return _equal;
            }
        }
        
        public Difference Difference {
            get {
                return _difference;
            }
        }
     
        public void DifferenceFound(Difference difference) {
            _identical = false;
            if (difference.MajorDifference) {
                _equal = false;
            }       
            _difference = difference;
        }
    }
}
