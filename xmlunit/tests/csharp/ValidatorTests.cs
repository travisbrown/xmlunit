namespace XmlUnit.Tests {
    using System;
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class ValidatorTests {
        public static readonly string VALID_FILE = ".\\..\\tests\\etc\\BookXsdGenerated.xml";
        public static readonly string INVALID_FILE = ".\\..\\tests\\etc\\invalidBook.xml";
        public static readonly string BASE_URI = DiffConfiguration.DEFAULT_BASE_URI;
                
        [Test] public void XsdValidFileIsValid() {
            PerformAssertion(VALID_FILE, true);
        } 
                
        private Validator PerformAssertion(string file, bool expected) {
            FileStream input = File.Open(file, FileMode.Open, FileAccess.Read);
            try {
                Validator validator = new Validator(new StreamReader(input), BASE_URI);
                Assertion.AssertEquals(expected, validator.IsValid);
                return validator;
            } finally {
                input.Close();
            }
        }
        
        [Test] public void XsdInvalidFileIsNotValid() {
            Validator validator = PerformAssertion(INVALID_FILE, false);
            string expected = "The element 'http://www.publishing.org:Book' has incomplete content";
            Assertion.AssertEquals(true, 
                                   validator.ValidationMessage.StartsWith(expected));
        }
    }
}
