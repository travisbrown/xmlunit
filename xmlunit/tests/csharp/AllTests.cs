namespace XmlUnit.Tests.All {
    using NUnit.Framework;
    using XmlUnit.Tests;


    [TestFixture]
    public class AllTests : Assertion {
        static DiffResultTests diffTest(){
            DiffResultTests test = new DiffResultTests();
            test.CreateDiffResult();
            return test;
        }
        public static void Main (string[]args) {
            diffTest().NewDiffResultIsEqualAndIdentical();
            diffTest().NotEqualOrIdenticalAfterMajorDifferenceFound();
            diffTest().NotIdenticalButEqualAfterMinorDifferenceFound();

            new XmlDiffTests().EqualResultForSameReader();
            new XmlDiffTests().SameResultForTwoInvocations();
            new XmlDiffTests().EqualResultForSameEmptyElements();
            new XmlDiffTests().NotEqualResultForEmptyVsNotEmptyElements();
            new XmlDiffTests().NotEqualResultForDifferentElements();
            new XmlDiffTests().NotEqualResultForDifferentNumberOfAttributes();
            new XmlDiffTests().NotEqualResultForDifferentAttributeValues();
            new XmlDiffTests().NotEqualResultForDifferentAttributeNames();
            new XmlDiffTests().EqualResultForDifferentAttributeSequences();
            new XmlDiffTests().NotEqualResultForDifferentAttributeValuesAndSequences();            
            new XmlDiffTests().NotEqualResultForDifferentTextElements();
            new XmlDiffTests().CanDistinguishElementClosureAndEmptyElement();
            new XmlDiffTests().NotEqualResultForDifferentLengthElements();

            new DifferenceTests().ToStringContainsId();
            
            new XmlUnitConfigurationTests().DefaultConfiguredWhitespaceHandlingAll();
            new XmlUnitConfigurationTests().CanConfigureWhitespaceHandlingSignificant();
            new XmlUnitConfigurationTests().CanConfigureWhitespaceHandlingNone();
        }
    }
}
