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
using System;
using System.IO;
using System.Xml;
using net.sf.xmlunit.exceptions;
using net.sf.xmlunit.input;

namespace net.sf.xmlunit.builder {
    /// <summary>
    /// Fluent API to create ISource instances.
    /// </summary>
    public sealed class Input {
        public interface IBuilder {
            /// <summary>
            /// build the actual ISource instance.
            /// </summary>
            ISource Build();
        }

        internal class DOMBuilder : IBuilder {
            private readonly ISource source;
            internal DOMBuilder(XmlNode d) {
                source = new DOMSource(d);
            }
            public ISource Build() {
                return source;
            }
        }

        /// <summary>
        /// Build an ISource from a DOM Document.
        /// </summary>
        public static IBuilder FromDocument(XmlDocument d) {
            return new DOMBuilder(d);
        }

        /// <summary>
        /// Build an ISource from a DOM Node.
        /// </summary>
        public static IBuilder FromNode(XmlNode n) {
            return new DOMBuilder(n);
        }

        internal class StreamBuilder : IBuilder {
            private readonly ISource source;
            internal StreamBuilder(string s) {
                source = new StreamSource(s);
            }
            internal StreamBuilder(Stream s) {
                source = new StreamSource(s);
            }
            internal StreamBuilder(TextReader r) {
                source = new StreamSource(r);
            }
            internal string SystemId {
                set {
                    source.SystemId = value ?? string.Empty;
                }
            }
            public ISource Build() {
                return source;
            }
        }

        /// <summary>
        /// Build an ISource from a named file.
        /// </summary>
        public static IBuilder FromFile(string name) {
            return new StreamBuilder(name);
        }

        /// <summary>
        /// Build an ISource from a stream.
        /// </summary>
        public static IBuilder FromStream(Stream s) {
            StreamBuilder b = new StreamBuilder(s);
            if (s is FileStream) {
                b.SystemId = new Uri(Path.GetFullPath((s as FileStream).Name))
                    .ToString();
            }
            return b;
        }

        /// <summary>
        /// Build an ISource from a reader.
        /// </summary>
        public static IBuilder FromReader(TextReader r) {
            StreamBuilder b = new StreamBuilder(r);
            StreamReader s = r as StreamReader;
            if (s != null && s.BaseStream is FileStream) {
                b.SystemId =
                    new Uri(Path.GetFullPath((s.BaseStream as FileStream).Name))
                    .ToString();
            }
            return b;
        }

        /// <summary>
        /// Build an ISource from a string.
        /// </summary>
        public static IBuilder FromMemory(string s) {
            return FromReader(new StringReader(s));
        }

        /// <summary>
        /// Build an ISource from an array of bytes.
        /// </summary>
        public static IBuilder FromMemory(byte[] b) {
            return FromStream(new MemoryStream(b));
        }

        /// <summary>
        /// Build an ISource from an URI.
        /// <param name="uri">must represent a valid URL</param>
        /// </summary>
        public static IBuilder FromURI(string uri) {
            return new StreamBuilder(uri);
        }

        /// <summary>
        /// Build an ISource from an URI.
        /// <param name="uri">must represent a valid URL</param>
        /// </summary>
        public static IBuilder FromURI(System.Uri uri) {
            return new StreamBuilder(uri.AbsoluteUri);
        }

        public interface ITransformationBuilder
            : ITransformationBuilderBase<ITransformationBuilder>, IBuilder {
            /// <summary>
            /// Sets the stylesheet to use.
            /// </summary>
            ITransformationBuilder WithStylesheet(IBuilder b);
        }

        internal class Transformation
            : AbstractTransformationBuilder<ITransformationBuilder>,
              ITransformationBuilder {

            internal Transformation(ISource s) : base(s) {
            }
            public ITransformationBuilder WithStylesheet(IBuilder b) {
                return WithStylesheet(b.Build());
            }
            public ISource Build() {
                return new DOMSource(Helper.TransformToDocument());
            }
        }

        /// <summary>
        /// Build an ISource by XSLT transforming a different ISource.
        /// </summary>
        public static ITransformationBuilder ByTransforming(ISource s) {
            return new Transformation(s);
        }
        /// <summary>
        /// Build an ISource by XSLT transforming a different ISource.
        /// </summary>
        public static ITransformationBuilder ByTransforming(IBuilder b) {
            return ByTransforming(b.Build());
        }
    }
}
