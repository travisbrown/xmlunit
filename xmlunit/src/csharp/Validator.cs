namespace XmlUnit {
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    
    public class Validator {
        private bool hasValidated = false;
        private bool isValid = true;
        private string validationMessage;
        private readonly XmlValidatingReader validatingReader;
        
        public Validator(TextReader reader, string baseURI) 
            :this(new XmlInput(reader), baseURI) {
        }
            
        public Validator(XmlInput input, string baseURI) 
            : this(input, baseURI, WhitespaceHandling.All) {
        }
        
        public Validator(XmlInput input, string baseURI, 
                         WhitespaceHandling whitespaceHandling) {
            XmlReader xmlReader = input.CreateXmlReader(baseURI, WhitespaceHandling.All);
            validatingReader = new XmlValidatingReader(xmlReader);          
            AddValidationEventHandler(new ValidationEventHandler(ValidationFailed));            
        }
                        
        public void AddValidationEventHandler(ValidationEventHandler handler) {
            validatingReader.ValidationEventHandler += handler;
        }
        
        public void ValidationFailed(object sender, ValidationEventArgs e) {
            isValid = false;
            validationMessage = e.Message;
        }
        
        private void Validate() {
            if (!hasValidated) {
                hasValidated = true;
                while (validatingReader.Read()) {
                    // only interested in ValidationFailed callbacks
                }
            }
        }
        
        public bool IsValid {
            get {
                Validate();
                return isValid;
            }
        }
        
        public string ValidationMessage {
            get {
                Validate();
                return validationMessage;
            }
            
        }
    }
}
