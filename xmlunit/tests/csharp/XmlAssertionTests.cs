namespace XmlUnit.Tests {
    using XmlUnit;
    using NUnit.Framework;
    using System.IO;
    
    [TestFixture]
    public class XmlAssertionTests {        
        [Test] public void AssertStringEqualAndIdenticalToSelf() {
            string control = "<assert>true</assert>";
            string test = "<assert>true</assert>";
            XmlAssertion.AssertXmlIdentical(control, test);
            XmlAssertion.AssertXmlEquals(control, test);
        }        
        
        [Test] public void AssertDifferentStringsNotEqualNorIdentical() {
            string control = "<assert>true</assert>";
            string test = "<assert>false</assert>";
            XmlDiff xmlDiff = new XmlDiff(control, test);
            XmlAssertion.AssertXmlNotIdentical(xmlDiff);
            XmlAssertion.AssertXmlNotEquals(xmlDiff);
        }        
        
        [Test] public void AssertXmlIdenticalUsesOptionalDescription() {
            string description = "An Optional Description";
            try {
                XmlDiff diff = new XmlDiff(new XmlInput("<a/>"), new XmlInput("<b/>"), 
                                           new DiffConfiguration(description));
                XmlAssertion.AssertXmlIdentical(diff);
            } catch (NUnit.Framework.AssertionException e) {
                Assertion.AssertEquals(true, e.Message.StartsWith(description));
            }
        }
        
        [Test] public void AssertXmlEqualsUsesOptionalDescription() {
            string description = "Another Optional Description";
            try {
                XmlDiff diff = new XmlDiff(new XmlInput("<a/>"), new XmlInput("<b/>"), 
                                           new DiffConfiguration(description));
                XmlAssertion.AssertXmlEquals(diff);
            } catch (NUnit.Framework.AssertionException e) {
                Assertion.AssertEquals(true, e.Message.StartsWith(description));
            }
        }
        
        [Test] public void AssertXmlValidTrueForValidFile() {
            StreamReader reader = GetStreamReader(ValidatorTests.VALID_FILE);
            try {
                XmlAssertion.AssertXmlValid(reader, ValidatorTests.BASE_URI);
            } finally {
                reader.Close();
            }
        }
        
        [Test] public void AssertXmlValidFalseForInvalidFile() {
            StreamReader reader = GetStreamReader(ValidatorTests.INVALID_FILE);
            try {
                XmlAssertion.AssertXmlValid(reader, ValidatorTests.BASE_URI);
                Assertion.Fail("Expected assertion failure");
            } catch(AssertionException e) {
                AvoidUnusedVariableCompilerWarning(e);
            } finally {
                reader.Close();
            }
        }
        
        private StreamReader GetStreamReader(string file) {
            FileStream input = File.Open(file, FileMode.Open, FileAccess.Read);
            return new StreamReader(input);
        }
        
        private static readonly string MY_SOLAR_SYSTEM = "<solar-system><planet name='Earth' position='3' supportsLife='yes'/><planet name='Venus' position='4'/></solar-system>";
        
        [Test] public void AssertXPathExistsWorksForExistentXPath() {
            XmlAssertion.AssertXPathExists("//planet[@name='Earth']", 
                                           MY_SOLAR_SYSTEM);
        }
        
        [Test] public void AssertXPathExistsFailsForNonExistentXPath() {
            try {
                XmlAssertion.AssertXPathExists("//star[@name='alpha centauri']", 
                                               MY_SOLAR_SYSTEM);
                Assertion.Fail("Expected assertion failure");
            } catch (AssertionException e) {
                AvoidUnusedVariableCompilerWarning(e);
            }
        }
        
        [Test] public void AssertXPathEvaluatesToWorksForMatchingExpression() {
            XmlAssertion.AssertXPathEvaluatesTo("//planet[@position='3']/@supportsLife", 
                                                MY_SOLAR_SYSTEM,
                                                "yes");
        }
        
        [Test] public void AssertXPathEvaluatesToWorksForNonMatchingExpression() {
            XmlAssertion.AssertXPathEvaluatesTo("//planet[@position='4']/@supportsLife", 
                                                MY_SOLAR_SYSTEM,
                                                "");
        }
        
        [Test] public void AssertXPathEvaluatesToWorksConstantExpression() {
            XmlAssertion.AssertXPathEvaluatesTo("true()", 
                                                MY_SOLAR_SYSTEM,
                                                "True");
            XmlAssertion.AssertXPathEvaluatesTo("false()", 
                                                MY_SOLAR_SYSTEM,
                                                "False");
        }

        private void AvoidUnusedVariableCompilerWarning(AssertionException e) {
            string msg = e.Message;
        }
    }
}
