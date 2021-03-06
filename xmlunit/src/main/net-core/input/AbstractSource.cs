/*
  This file is licensed to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
using System.Xml;

namespace net.sf.xmlunit.input {
    /// <summary>
    /// Provides a base implementation for the different concrete ISource
    /// implementations.
    /// </summary>
    public abstract class AbstractSource : ISource {
        private string systemId;
        private readonly XmlReader reader;
        protected AbstractSource(XmlReader r) {
            reader = r;
            systemId = r.BaseURI;
        }
        public XmlReader Reader {
            get {
                return reader;
            }
        }
        public string SystemId {
            get {
                return systemId;
            }
            set {
                systemId = value;
            }
        }
        public override string ToString() {
            return string.Format("{0} with systemId {1}", GetType().Name,
                                 SystemId);
        }
    }
}
