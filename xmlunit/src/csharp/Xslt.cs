namespace XmlUnit {
    using System.IO;
    using System.Security.Policy;
    using System.Xml;
    using System.Xml.XPath;
    using System.Xml.Xsl;
    
    public class Xslt {
        private readonly XmlInput _xsltInput;
        public Xslt(XmlInput xsltInput) {
            _xsltInput = xsltInput;
        }
        
        public Xslt(string xslt) 
            : this(new XmlInput(xslt)) {
        }
        
        public string Transform(string someXml) {
            XslTransform transform = new XslTransform();
            XmlReader xsltReader = _xsltInput.CreateDefaultXmlReader();
            XmlResolver xsltResolver = null;
            Evidence evidence = null;
            transform.Load(xsltReader, xsltResolver, evidence);
            
            XmlReader inputReader = new XmlTextReader(new StringReader(someXml));
            XmlSpace space = XmlSpace.Default;
            XPathDocument document = new XPathDocument(inputReader, space);
            XPathNavigator navigator = document.CreateNavigator();
            
            XsltArgumentList args = null;
            XmlResolver inputResolver = null;
            TextWriter writer = new StringWriter();
            transform.Transform(navigator, args, writer, inputResolver);
            
            inputReader.Close();
            xsltReader.Close();
            return writer.ToString();
        }
    }
}
