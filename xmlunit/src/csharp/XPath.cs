namespace XmlUnit {
    using System.IO;
    using System.Text;
    using System.Xml.XPath;
    
    public class XPath {
        private readonly string _xPathExpression;
        
        public XPath(string anXPathExpression) {
            _xPathExpression = anXPathExpression;
        }
        
        public bool XPathExists(string forSomeXml) {
            return XPathExists(new StringReader(forSomeXml));
        }
        
        public bool XPathExists(TextReader forReader) {
            XPathNodeIterator iterator = GetNodeIterator(forReader);
            return (iterator.Count > 0);
        }
        
        private XPathNodeIterator GetNodeIterator(TextReader forReader) {
            XPathNavigator xpathNavigator = GetNavigator(forReader);
            return xpathNavigator.Select(_xPathExpression);            
        }
                
        private XPathNavigator GetNavigator(TextReader forReader) {            
            XPathDocument xpathDocument = new XPathDocument(forReader);
            return xpathDocument.CreateNavigator();
        }
                
        public string EvaluateXPath(string forSomeXml) {
            return EvaluateXPath(new StringReader(forSomeXml));
        }
        
        public string EvaluateXPath(TextReader forReader) {
            XPathNavigator xpathNavigator = GetNavigator(forReader);
            XPathExpression xPathExpression = xpathNavigator.Compile(_xPathExpression);
            if (xPathExpression.ReturnType == XPathResultType.NodeSet) {
                return EvaluateXPath(xpathNavigator);
            } else {
                return xpathNavigator.Evaluate(xPathExpression).ToString();
            }
        }
        
        private string EvaluateXPath(XPathNavigator forXPathNavigator) {
            XPathNodeIterator iterator = forXPathNavigator.Select(_xPathExpression);
            
            StringBuilder stringBuilder = new StringBuilder();
            XPathNavigator xpathNavigator;
            
            while (iterator.MoveNext()) {
                xpathNavigator = iterator.Current;
                stringBuilder.Insert(stringBuilder.Length, xpathNavigator.Value);
            }
            return stringBuilder.ToString();
        }
    }
}
