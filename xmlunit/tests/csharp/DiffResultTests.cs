namespace XmlUnit.Tests {
    using NUnit.Framework;
    using System.Xml;
    using XmlUnit;
    
    [TestFixture]
    public class DiffResultTests {
        private DiffResult _result;
        private Difference _majorDifference, _minorDifference;
        
        [SetUp] public void CreateDiffResult() {
            _result = new DiffResult();
            _majorDifference = new Difference(Differences.ELEMENT_TAG_NAME, XmlNodeType.Element, XmlNodeType.Element);
            _minorDifference = new Difference(Differences.ATTR_SEQUENCE, XmlNodeType.Comment, XmlNodeType.Comment);
        }
        
        [Test] public void NewDiffResultIsEqualAndIdentical() {
            Assertion.AssertEquals(true, _result.Identical);
            Assertion.AssertEquals(true, _result.Equal);
        }
        
        [Test] public void NotEqualOrIdenticalAfterMajorDifferenceFound() {
            _result.DifferenceFound(_majorDifference);
            Assertion.AssertEquals(false, _result.Identical);
            Assertion.AssertEquals(false, _result.Equal);
        }
        
        [Test] public void NotIdenticalButEqualAfterMinorDifferenceFound() {
            _result.DifferenceFound(_minorDifference);
            Assertion.AssertEquals(false, _result.Identical);
            Assertion.AssertEquals(true, _result.Equal);
        }
    }
}
