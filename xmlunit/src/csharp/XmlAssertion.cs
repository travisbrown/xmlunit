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

        public static void AssertXmlEquals(XmlInput controlInput, XmlInput testInput) {
            AssertXmlEquals(new XmlDiff(controlInput, testInput));
        }        
        
        public static void AssertXmlIdentical(TextReader controlTextReader, TextReader testTextReader) {
            AssertXmlIdentical(new XmlDiff(controlTextReader, testTextReader));
        }        

        public static void AssertXmlIdentical(string controlText, string testText) {
            AssertXmlIdentical(new XmlDiff(controlText, testText));
        }        
        
        public static void AssertXmlIdentical(XmlInput controlInput, XmlInput testInput) {
            AssertXmlIdentical(new XmlDiff(controlInput, testInput));
        }        
        
        public static void AssertXmlEquals(XmlDiff xmlDiff) {
            AssertXmlEquals(xmlDiff, true);
        }
        
        public static void AssertXmlNotEquals(XmlDiff xmlDiff) {
            AssertXmlEquals(xmlDiff, false);
        }

        private static void AssertXmlEquals(XmlDiff xmlDiff, bool equalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            Assertion.AssertEquals(xmlDiff.OptionalDescription, equalOrNot, diffResult.Equal);
        }
        
        public static void AssertXmlIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, true);
        }
        
        public static void AssertXmlNotIdentical(XmlDiff xmlDiff) {
            AssertXmlIdentical(xmlDiff, false);
        }
        
        private static void AssertXmlIdentical(XmlDiff xmlDiff, bool identicalOrNot) {
            DiffResult diffResult = xmlDiff.Compare();
            AssertEquals(xmlDiff.OptionalDescription, identicalOrNot, diffResult.Identical);
        }
        
        public static void AssertXmlValid(string someXml, string baseURI) {
            TextReader reader = new StringReader(someXml);
            AssertXmlValid(reader, baseURI);
        }
        
        public static void AssertXmlValid(TextReader reader, string baseURI) {
            Validator validator = new Validator(reader, baseURI);
            AssertXmlValid(validator);
        }
        
        public static void AssertXmlValid(XmlInput xmlInput, string baseURI) {
            Validator validator = new Validator(xmlInput, baseURI);
            AssertXmlValid(validator);
        }
        
        public static void AssertXmlValid(Validator validator) {
            AssertEquals(validator.ValidationMessage, true, validator.IsValid);
        }
        
        public static void AssertXPathExists(string anXPathExpression, string inXml) {
            AssertXPathExists(anXPathExpression, new XmlInput(inXml));
        }
        
        public static void AssertXPathExists(string anXPathExpression, TextReader inXml) {
            AssertXPathExists(anXPathExpression, new XmlInput(inXml));
        }
        
        public static void AssertXPathExists(string anXPathExpression, XmlInput inXml) {
            XPath xpath = new XPath(anXPathExpression);
            AssertEquals(true, xpath.XPathExists(inXml));
        }
        
        public static void AssertXPathEvaluatesTo(string anXPathExpression, string inXml, 
                                                  string expectedValue) {
            AssertXPathEvaluatesTo(anXPathExpression, new XmlInput(inXml), expectedValue);
        }
        
        public static void AssertXPathEvaluatesTo(string anXPathExpression, TextReader inXml, 
                                                  string expectedValue) {
            AssertXPathEvaluatesTo(anXPathExpression, new XmlInput(inXml), expectedValue);
        }
                                                  
        public static void AssertXPathEvaluatesTo(string anXPathExpression, XmlInput inXml, 
                                                  string expectedValue) {
            XPath xpath = new XPath(anXPathExpression);
            AssertEquals(expectedValue, xpath.EvaluateXPath(inXml));
        }
    }
}
