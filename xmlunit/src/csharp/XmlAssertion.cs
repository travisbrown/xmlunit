namespace XmlUnit {
    using NUnit.Framework;
    using System.IO;
    
    public class XmlAssertion : Assertion {
        private static DiffResult PerformComparison(TextReader controlTextReader, TextReader testTextReader) {
            XmlDiff xmlDiff = new XmlDiff(controlTextReader, testTextReader);
            DiffResult diffResult = xmlDiff.Compare();
            return diffResult;
        }
        
        public static void AssertXmlEquals(TextReader controlTextReader, TextReader testTextReader) {
            DiffResult diffResult = PerformComparison(controlTextReader, testTextReader);
            AssertEquals(true, diffResult.Equal);
        }

        public static void AssertXmlIdentical(TextReader controlTextReader, TextReader testTextReader) {
            DiffResult diffResult = PerformComparison(controlTextReader, testTextReader);
            AssertEquals(true, diffResult.Identical);
        }        
    }
}
