namespace XmlUnit.Tests {
    using XmlUnit;
    using NUnit.Framework;
    using System.IO;
    using System.Xml;
    
    [TestFixture]
    public class XmlUnitConfigurationTests {
        private static string xmlWithWhitespace = "<elemA>as if<elemB> \r\n </elemB>\t</elemA>";
        private static string xmlWithoutWhitespaceElement = "<elemA>as if<elemB/>\r\n</elemA>";
        private static string xmlWithWhitespaceElement = "<elemA>as if<elemB> </elemB></elemA>";
        private static string xmlWithoutWhitespace = "<elemA>as if<elemB/></elemA>";
        
        private WhitespaceHandling _whitespaceHandling;
        
        [SetUp] public void CacheWhitespaceHandling() {
            _whitespaceHandling = XmlUnitConfiguration.WhitespaceHandling;
        }
        
        [TearDown] public void RestoreWhitespaceHandling() {
            XmlUnitConfiguration.WhitespaceHandling = _whitespaceHandling;
        }
        
        private void PerformAssertion(string control, string test, bool assertion) {
            XmlDiff diff = new XmlDiff(control, test);
            Assertion.AssertEquals(assertion, diff.Compare().Equal);            
            Assertion.AssertEquals(assertion, diff.Compare().Identical);            
        }
        
        [Test] public void DefaultConfiguredWhitespaceHandlingAll() {
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, false);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, false);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, false);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, false);
        }
        
        [Test] public void CanConfigureWhitespaceHandlingSignificant() {
            XmlUnitConfiguration.WhitespaceHandling = WhitespaceHandling.Significant;
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, true);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, true);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, true);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, true);
        }
        
        [Test] public void CanConfigureWhitespaceHandlingNone() {
            XmlUnitConfiguration.WhitespaceHandling = WhitespaceHandling.None;
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, true);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, true);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, true);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, true);
        }
    }
}
