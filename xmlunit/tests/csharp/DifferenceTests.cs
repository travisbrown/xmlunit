namespace XmlUnit.Tests {
    using System;
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class DifferenceTests {
        [Test] public void ToStringContainsId() {
            string commentDifference = Differences.ATTR_SEQUENCE.ToString();
            string idValue = "type: " + (int)DifferenceType.ATTR_SEQUENCE_ID;
            Assertion.AssertEquals("contains " + idValue, 
                                   true, 
                                   commentDifference.IndexOfAny(idValue.ToCharArray()) > 0);
        }
    }
}
