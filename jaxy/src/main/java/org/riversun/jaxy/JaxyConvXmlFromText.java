/*  Jaxy JSON/XML-processor
 *
 *  Copyright (c) 2006- Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package org.riversun.jaxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Transform from Text into XmlNode<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxyConvXmlFromText {

	private boolean mEnableAttributeOnlyElement = true;
	private boolean mIsUseCDATA;

	public XmlNode toXmlNode(String xmlString) {
		Document xmlDocument = getDocumentFromString(xmlString);
		return toXmlNode(xmlDocument);
	}

	public XmlNode toXmlNode(File xmlFile) {
		Document xmlDocument = getDocumentFromFile(xmlFile);
		return toXmlNode(xmlDocument);
	}

	public XmlNode toXmlNode(Document xmlDocument) {
		XmlNode xmlNode = new XmlNode();

		if (xmlDocument != null) {

			Node rootNode = xmlDocument.getDocumentElement();
			this.searchNodes(rootNode, null, xmlNode, null);

		} else {

			xmlNode = null;
		}
		return xmlNode;
	}

	private void searchNodes(Node parentNode, Node grandParentNode, XmlNode parentXmlElement, XmlNode grandParentXmlElement) {

		if (parentNode.hasChildNodes() || parentNode.hasAttributes()) {

			XmlNode currentElement = new XmlNode();
			currentElement.elementName = parentNode.getNodeName();

			NamedNodeMap attrs = parentNode.getAttributes();

			int attrLeng = attrs.getLength();
			if (attrLeng > 0) {

				// Parse attribute only element
				if (mEnableAttributeOnlyElement) {

					// -- Some attributes, but do not have a child element

					if (parentNode.hasChildNodes() == false) {
						Document document = parentNode.getOwnerDocument();

						// Add the name that is considered to be non-overlapping
						// into the node as child TEXTNODE element for the sake
						// of convenience.
						parentNode.appendChild(document.createTextNode(JaxyDef.HAVING_ATTRIBUTE_SIGN));
					}
				}

				for (int i = 0; i < attrLeng; i++) {
					Node node = attrs.item(i);
					currentElement.addAttribute(node.getNodeName(), node.getNodeValue());
				}
			}

			if (hasChildElementNodes(parentNode)) {

				parentXmlElement.addChildXmlElement(parentNode.getNodeName(), currentElement);

			}

			NodeList nodeList = parentNode.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				searchNodes(node, parentNode, currentElement, parentXmlElement);
			}
		} else {

			if (parentNode.getNodeType() == Node.TEXT_NODE) {
				String plainedNodeValue = parentNode.getNodeValue();

				char charCR = 13;// 0x0D
				char charLF = 10;// 0x0A
				char charTab = 9;// 0x09 Horizontal Tab

				String strCR = String.valueOf(charCR);
				String strLF = String.valueOf(charLF);
				String strTAB = String.valueOf(charTab);

				// For faulty xml:remove the space
				String spaceDeletedNodeValue = plainedNodeValue.replace(" ", "");

				// For faulty xml:remove LF
				String lfDeletedNodeValue = spaceDeletedNodeValue.replace(strLF, "");

				// For faulty xml:remove CR
				String crDeletedNodeValue = lfDeletedNodeValue.replace(strCR, "");

				// For faulty xml:remove TAB
				String tabDeletedNodeValue = crDeletedNodeValue.replace(strTAB, "");

				String allDeletedNodeValue = tabDeletedNodeValue;

				int nodeValueLength = allDeletedNodeValue.length();

				if (nodeValueLength > 0) {

					parentXmlElement.elementText = parentNode.getNodeValue();

					if (mEnableAttributeOnlyElement) {

						if (parentXmlElement.elementText.equals(JaxyDef.HAVING_ATTRIBUTE_SIGN)) {
							parentXmlElement.elementText = "";
						}
					}

					grandParentXmlElement.addChildXmlElement(grandParentNode.getNodeName(), parentXmlElement);
				}

			}
		}
	}

	public void setEnableAttributeOnlyElement(boolean enableAttributeOnlyElement) {
		mEnableAttributeOnlyElement = enableAttributeOnlyElement;
	}

	public void setUseCDATA(boolean useCDATA) {
		this.mIsUseCDATA = useCDATA;
	}

	private boolean hasChildElementNodes(Node targetNode) {

		boolean retValue = false;

		for (int i = 0; i < targetNode.getChildNodes().getLength(); i++) {
			Node node = targetNode.getChildNodes().item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				retValue = true;
				break;
			}
		}
		return retValue;
	}

	private Document getDocumentFromString(String str) {
		Document doc = null;

		try {
			DocumentBuilderFactory docbuilderFactory = DocumentBuilderFactory.newInstance();
			docbuilderFactory.setCoalescing(this.mIsUseCDATA);
			DocumentBuilder docBuilder = docbuilderFactory.newDocumentBuilder();

			doc = docBuilder.parse(new InputSource(new StringReader(str)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	private Document getDocumentFromFile(File file) {
		Document doc = null;
		try {
			DocumentBuilderFactory docbuilderFactory = DocumentBuilderFactory.newInstance();
			docbuilderFactory.setCoalescing(this.mIsUseCDATA);
			DocumentBuilder docBuilder = docbuilderFactory.newDocumentBuilder();

			InputStream inStream = new FileInputStream(file);

			doc = docBuilder.parse(inStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

}
