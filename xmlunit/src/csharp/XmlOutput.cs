namespace XmlUnit {
    using System.IO;
    using System.Xml;
    using System.Xml.XPath;
    using System.Xml.Xsl;
    
    public class XmlOutput {

    	private readonly XslTransform _transform;
    	private readonly XsltArgumentList _xsltArgs;
    	private readonly XPathNavigator _navigator;
    	private readonly XmlResolver _resolverForXmlTransformed;
    	private readonly XmlReader[] _readersToClose;
    	
    	internal XmlOutput(XslTransform transform, XsltArgumentList xsltArgs, 
						   XPathNavigator navigator, XmlResolver resolverForXmlTransformed,
						   XmlReader[] readersToClose) {
			_transform = transform;
			_xsltArgs = xsltArgs;
			_navigator = navigator;
			_resolverForXmlTransformed = resolverForXmlTransformed;
			_readersToClose = readersToClose;
		}
		
		private void CleanUp() {
			for (int i = 0; i < _readersToClose.Length; ++i) {
	            _readersToClose[i].Close();
			}
		}
    	                   	 	                   
		public string AsString() {
			TextWriter writer = new StringWriter();
	        _transform.Transform(_navigator, _xsltArgs, writer, _resolverForXmlTransformed);
			CleanUp();
			return writer.ToString();
		}
		
		public XmlReader AsXml() {
	        XmlReader xml = _transform.Transform(_navigator, _xsltArgs, _resolverForXmlTransformed);
			CleanUp();
			return xml;
		}
		
		public void WriteXml(XmlWriter viaXmlWriter) {			
	        _transform.Transform(_navigator, _xsltArgs, viaXmlWriter, _resolverForXmlTransformed);
			CleanUp();
		}
		
		public void WriteStream(Stream viaStream) {			
	        _transform.Transform(_navigator, _xsltArgs, viaStream, _resolverForXmlTransformed);
			CleanUp();
		}
    }
}
