namespace XmlUnit {
    public class Differences {
        private Differences() { }
        
        public static Difference ATTR_SEQUENCE = new Difference(DifferenceType.ATTR_SEQUENCE_ID, false);
        
        public static Difference CHILD_NODELIST_LENGTH = new Difference(DifferenceType.CHILD_NODELIST_LENGTH_ID, true);
        public static Difference NODE_TYPE = new Difference(DifferenceType.NODE_TYPE_ID, true);
        public static Difference ELEMENT_TAG_NAME = new Difference(DifferenceType.ELEMENT_TAG_NAME_ID, true);
        public static Difference ELEMENT_NUM_ATTRIBUTES = new Difference(DifferenceType.ELEMENT_NUM_ATTRIBUTES_ID, true);
        public static Difference ATTR_VALUE = new Difference(DifferenceType.ATTR_VALUE_ID, true);
        public static Difference ATTR_NAME_NOT_FOUND = new Difference(DifferenceType.ATTR_NAME_NOT_FOUND_ID, true);
        public static Difference TEXT_VALUE = new Difference(DifferenceType.TEXT_VALUE_ID, true);
        
    }
}
