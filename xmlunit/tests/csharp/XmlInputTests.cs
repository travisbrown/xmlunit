namespace XmlUnit.Tests {
    using System;
    using System.IO;
    using System.Text;
    using System.Xml;
    using NUnit.Framework;
    using XmlUnit;
    
    [TestFixture]
    public class XmlInputTests {     
        private static readonly string INPUT = "<abc><q>werty</q><u>iop</u></abc> ";
        private string _expected; 
        
        [SetUp] public void SetExpected() {
            _expected = ReadOuterXml(new XmlTextReader(new StringReader(INPUT)));
        }
        
        [Test] public void StringInputTranslatesToXmlReader() {
            XmlInput input = new XmlInput(INPUT);
            string actual = ReadOuterXml(input.CreateXmlReader());
            Assertion.AssertEquals(_expected, actual);
        }
        
        [Test] public void StreamInputTranslatesToXmlReader() {
            MemoryStream stream = new MemoryStream();
            StreamWriter writer = new StreamWriter(stream, Encoding.Default);
            writer.WriteLine(INPUT);
            writer.Flush();
            stream.Seek(0, SeekOrigin.Begin);
            XmlInput input = new XmlInput(stream);
            string actual = ReadOuterXml(input.CreateXmlReader());
            try {
                Assertion.AssertEquals(_expected, actual);
            } finally {
                writer.Close();
            }
        }
        
        private string ReadOuterXml(XmlReader forReader) {
            try {
                forReader.MoveToContent();
                return forReader.ReadOuterXml();
            } finally {
                forReader.Close();
            }
        }
    }
}
