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

using System.Collections.Generic;
using System.Xml;
using NUnit.Framework;
using net.sf.xmlunit.builder;
using net.sf.xmlunit.exceptions;

namespace net.sf.xmlunit.xpath {

    public abstract class AbstractXPathEngineTest {

        private ISource source;

        protected abstract IXPathEngine Engine { get; }

        [SetUp] public void ReadSource() {
            source = Input.FromFile(TestResources.BLAME_FILE).Build();
        }

        [Test] public void SelectNodesWithNoMatches() {
            IEnumerable<XmlNode> i = Engine.SelectNodes("foo",
                                                                   source);
            Assert.IsNotNull(i);
            Assert.IsFalse(i.GetEnumerator().MoveNext());
        }

        [Test] public void SelectNodesWithSingleMatch() {
            IEnumerable<XmlNode> i = Engine.SelectNodes("//ul", source);
            Assert.IsNotNull(i);
            IEnumerator<XmlNode> it = i.GetEnumerator();
            Assert.IsTrue(it.MoveNext());
            Assert.AreEqual("ul", it.Current.Name);
            Assert.IsFalse(it.MoveNext());
        }

        [Test] public void SelectNodesWithMultipleMatchs() {
            IEnumerable<XmlNode> i = Engine.SelectNodes("//li", source);
            Assert.IsNotNull(i);
            int count = 0;
            foreach (XmlNode n in i) {
                count++;
                Assert.AreEqual("li", n.Name);
            }
            Assert.AreEqual(4, count);
        }

        [Test]
        public void SelectNodesWithInvalidXPath() {
            try {
                Engine.SelectNodes("//li[", source);
                Assert.Fail("expected an exception");
            } catch (XMLUnitException) {
                // expected
            }
        }

        [Test] public void EvaluateWithNoMatches() {
            Assert.AreEqual(string.Empty, Engine.Evaluate("foo", source));
        }

        [Test] public void EvaluateWithSingleMatch() {
            Assert.AreEqual("Don't blame it on the...",
                            Engine.Evaluate("//title", source));
        }

        [Test] public void EvaluateWithSingleMatchTextSelector() {
            Assert.AreEqual("Don't blame it on the...",
                            Engine.Evaluate("//title/text()", source));
        }

        [Test] public void EvaluateWithMultipleMatches() {
            Assert.AreEqual("sunshine",
                            Engine.Evaluate("//li", source));
        }

        [Test]
        public void EvaluateWithInvalidXPath() {
            try {
                Engine.Evaluate("//li[", source);
                Assert.Fail("expected an exception");
            } catch (XMLUnitException) {
                // expected
            }
        }

        [Test] public void SelectNodesWithNS() {
            IXPathEngine e = Engine;
            source = Input.FromMemory("<n:d xmlns:n='urn:test:1'><n:e/></n:d>")
                .Build();
            Dictionary<string, string> m = new Dictionary<string, string>();
            m["x"] = "urn:test:1";
            e.NamespaceContext = m;
            IEnumerable<XmlNode> it = e.SelectNodes("/x:d/x:e", source);
            Assert.IsTrue(it.GetEnumerator().MoveNext());
        }

        [Test] public void SelectNodesWithDefaultNS() {
            IXPathEngine e = Engine;
            source = Input.FromMemory("<d xmlns='urn:test:1'><e/></d>")
                .Build();
            Dictionary<string, string> m = new Dictionary<string, string>();
            m["x"] = "urn:test:1";
            e.NamespaceContext = m;
            IEnumerable<XmlNode> it = e.SelectNodes("/x:d/x:e", source);
            Assert.IsTrue(it.GetEnumerator().MoveNext());
        }

        // throws an exception "'/:d/:e' has an invalid token."
        public void SelectNodesWithDefaultNSEmptyPrefix() {
            IXPathEngine e = Engine;
            source = Input.FromMemory("<d xmlns='urn:test:1'><e/></d>")
                .Build();
            Dictionary<string, string> m = new Dictionary<string, string>();
            m[string.Empty] = "urn:test:1";
            e.NamespaceContext = m;
            IEnumerable<XmlNode> it = e.SelectNodes("/:d/:e", source);
            Assert.IsTrue(it.GetEnumerator().MoveNext());
        }

        // doesn't match
        public void SelectNodesWithDefaultNSNoPrefix() {
            IXPathEngine e = Engine;
            source = Input.FromMemory("<d xmlns='urn:test:1'><e/></d>")
                .Build();
            Dictionary<string, string> m = new Dictionary<string, string>();
            m[string.Empty] = "urn:test:1";
            e.NamespaceContext = m;
            IEnumerable<XmlNode> it = e.SelectNodes("/d/e", source);
            Assert.IsTrue(it.GetEnumerator().MoveNext());
        }
    }
}
