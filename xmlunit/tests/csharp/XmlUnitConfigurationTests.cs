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
        
        [Test] public void DefaultConfiguredWhitespaceHandlingAll() {
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, false);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, false);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, false);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, false);
        }
        
        private void PerformAssertion(string control, string test, bool assertion) {
            XmlDiff diff = new XmlDiff(control, test);
            PerformAssertion(diff, assertion);
        }
        private void PerformAssertion(XmlDiff diff, bool assertion) {
            Assertion.AssertEquals(assertion, diff.Compare().Equal);            
            Assertion.AssertEquals(assertion, diff.Compare().Identical);            
        }

        [Test] public void CanConfigureWhitespaceHandlingSignificant() {
            XmlUnitConfiguration xmlUnitConfiguration = 
                new XmlUnitConfiguration (WhitespaceHandling.Significant);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, 
                             true, xmlUnitConfiguration);
        }
        
        [Test] public void CanConfigureWhitespaceHandlingNone() {
            XmlUnitConfiguration xmlUnitConfiguration = 
                new XmlUnitConfiguration(WhitespaceHandling.None);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespaceElement, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespace, xmlWithoutWhitespaceElement, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespace, xmlWithWhitespace, 
                             true, xmlUnitConfiguration);
            PerformAssertion(xmlWithoutWhitespaceElement, xmlWithWhitespaceElement, 
                             true, xmlUnitConfiguration);
        }
        
        private void PerformAssertion(string control, string test, bool assertion, 
                                      XmlUnitConfiguration xmlUnitConfiguration) {
            XmlDiff diff = new XmlDiff(control, test, xmlUnitConfiguration);
            PerformAssertion(diff, assertion);
        }        
    }
}
