namespace XmlUnit {
    using System.IO;
    using System.Xml;
    using System.Xml.Schema;
    
    public class Validator {
        private bool hasValidated = false;
        private bool isValid = true;
        private string validationMessage;
        private readonly XmlValidatingReader validatingReader;
        
        public Validator(TextReader reader, string baseURI) {
            XmlTextReader xmlReader = new XmlTextReader(baseURI, reader);
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
