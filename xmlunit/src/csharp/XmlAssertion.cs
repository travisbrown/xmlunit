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
            AssertXmlEquals(xmlDiff, true);
        }
        
        public static void AssertXmlNotEquals(XmlDiff xmlDiff) {
            AssertXmlEquals(xmlDiff, false);
        }

        public static void AssertXmlEquals(XmlDiff xmlDiff, bool equalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            Assertion.AssertEquals(xmlDiff.OptionalDescription, equalOrNot, diffResult.Equal);
        }
        
        public static void AssertXmlIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, true);
        }
        
        public static void AssertXmlNotIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, false);
        }
        
        public static void AssertXmlIdentical(XmlDiff xmlDiff, bool identicalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            AssertEquals(xmlDiff.OptionalDescription, identicalOrNot, diffResult.Identical);
        }
        
        public static void AssertXmlValid(TextReader reader, string baseURI) {
            AssertXmlValid(new Validator(reader, baseURI));
        }
        
        public static void AssertXmlValid(Validator validator) {
            AssertEquals(validator.ValidationMessage, true, validator.IsValid);
        }
        
        public static void AssertXpathExists(string anXPathExpression, string inXml) {
            XPath xpath = new XPath(anXPathExpression);
            AssertEquals(true, xpath.XPathExists(inXml));
        }
        
        public static void AssertXPathEvaluatesTo(string anXPathExpression, string inXml, 
                                                  string expectedValue) {
            XPath xpath = new XPath(anXPathExpression);
            AssertEquals(expectedValue, xpath.EvaluateXPath(inXml));
        }
    }
}
