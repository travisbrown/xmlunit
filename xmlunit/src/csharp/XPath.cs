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
            XPathNodeIterator iterator = GetNodeIterator(forSomeXml);
            return (iterator.Count > 0);
        }
        
        private XPathNodeIterator GetNodeIterator(string forSomeXml) {
            XPathNavigator xpathNavigator = GetNavigator(forSomeXml);
            return xpathNavigator.Select(_xPathExpression);            
        }
        
        private XPathNavigator GetNavigator(string forSomeXml) {
            TextReader reader = new StringReader(forSomeXml);
            XPathDocument xpathDocument = new XPathDocument(reader);
            return xpathDocument.CreateNavigator();
        }
                
        public string EvaluateXPath(string forSomeXml) {
            XPathNavigator xpathNavigator = GetNavigator(forSomeXml);
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
