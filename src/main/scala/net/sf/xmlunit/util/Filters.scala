package net.sf.xmlunit.util

import org.w3c.dom.Node

object Filters {
  val NotDocumentType: Function1[Node, Boolean] = {
    case n: Node => n.getNodeType != Node.DOCUMENT_TYPE_NODE
  }
}

