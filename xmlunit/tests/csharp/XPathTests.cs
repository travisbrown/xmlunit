namespace XmlUnit.Tests {
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class XpathTests {
        public static readonly string SOLAR_SYSTEM = "<solar-system><planet name='Earth' position='3' supportsLife='yes'/><planet name='Venus' position='4'/></solar-system>";
        private static readonly string SIMPLE_XML = "<a><b><c>one two</c></b></a>";
        [Test] public void XpathExistsTrueForXpathThatExists() {
            string anXPath = "/a/b/c";
            XPathEvaluator evaluator = new XPathEvaluator(anXPath);
            Assertion.AssertEquals(true, 
                                   evaluator.XPathExists(SIMPLE_XML));
        }
        
        [Test] public void XpathExistsFalseForXpathThatDoesntExist() {
            string anXPath = "/a/b/c/d";
            XPathEvaluator evaluator = new XPathEvaluator(anXPath);
            Assertion.AssertEquals(false, 
                                   evaluator.XPathExists(SIMPLE_XML));
        }
        
        [Test] public void XpathEvaluatesToTrueForSimpleString() {
            string anXPath = "/a/b/c";
            string expectedValue = "one two";
            XPathEvaluator evaluator = new XPathEvaluator(anXPath);
            Assertion.AssertEquals(expectedValue, 
                                   evaluator.EvaluateXPath(SIMPLE_XML));
        }
    }
}
