namespace XmlUnit {
    using NUnit.Framework;
    using System.IO;
    
    public class XmlAssertion : Assertion {
        public static void AssertXmlEquals(TextReader controlTextReader, TextReader testTextReader) {
            AssertXmlEquals(new XmlDiff(controlTextReader, testTextReader));
        }

        public static void AssertXmlEquals(string controlText, string testText) {
           AssertXmlEquals(new XmlDiff(controlText, testText));
        }

        public static void AssertXmlIdentical(TextReader controlTextReader, TextReader testTextReader) {
            AssertXmlIdentical(new XmlDiff(controlTextReader, testTextReader));
        }        

        public static void AssertXmlIdentical(string controlText, string testText) {
            AssertXmlIdentical(new XmlDiff(controlText, testText));
        }        
        
        public static void AssertXmlEquals(XmlDiff xmlDiff) {
            DiffResult diffResult = xmlDiff.Compare();
            AssertEquals(true, diffResult.Equal);
        }
        
        public static void AssertXmlIdentical(XmlDiff xmlDiff) {
            DiffResult diffResult = xmlDiff.Compare();
            AssertEquals(true, diffResult.Identical);
        }
    }
}
