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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * XmlNode<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class XmlNode extends JaxyNode {

	private Map<String, List<XmlNode>> mLocalDataMap = new LinkedHashMap<String, List<XmlNode>>();

	private Map<String, List<String>> mLocalAttrMap = new HashMap<String, List<String>>();

	public XmlNode child(String key) {
		List<XmlNode> list = (ArrayList<XmlNode>) this.mLocalDataMap.get(key);

		XmlNode retVal = null;

		if (list != null) {
			XmlNode[] xmlElementArray = (XmlNode[]) list.toArray(new XmlNode[0]);
			retVal = xmlElementArray[0];
		} else {

			retVal = new XmlNode();
		}
		return retVal;
	}

	public XmlNode[] children(String key) {
		List<XmlNode> list = (List<XmlNode>) this.mLocalDataMap.get(key);
		XmlNode[] retVal = null;

		if (list != null) {
			XmlNode[] xmlElementArray = (XmlNode[]) list.toArray(new XmlNode[0]);
			retVal = xmlElementArray;
		} else {

			retVal = new XmlNode[] {};
		}
		return retVal;
	}

	public String[] getChildElementNames() {
		Set<String> childElementNameSet = this.mLocalDataMap.keySet();
		String[] retVal = (String[]) childElementNameSet.toArray(new String[] {});
		return retVal;

	}

	@Override
	public String toString() {
		return elementText;
	}

	public String attr(String attributeName) {

		String retValue = null;

		ArrayList attrElementList = (ArrayList) this.mLocalAttrMap.get(attributeName);
		if (attrElementList == null) {
			retValue = null;
		} else {

			retValue = (String) attrElementList.get(0);
		}
		return retValue;
	}

	public String[] attrValues(String attributeName) {

		String[] retValue = null;

		ArrayList attrElementList = (ArrayList) this.mLocalAttrMap.get(attributeName);
		if (attrElementList == null) {
			retValue = new String[] {};
		} else {

			retValue = (String[]) attrElementList.toArray(new String[] {});
		}
		return retValue;

	}

	/**
	 * Add child node to this node
	 * 
	 * @param key
	 * @param xmlElement
	 */
	public void addChildXmlElement(String key, XmlNode xmlElement) {

		List<XmlNode> xmlElementList = (List<XmlNode>) this.mLocalDataMap.get(key);

		if (xmlElementList == null) {
			xmlElementList = new ArrayList<XmlNode>();
			this.mLocalDataMap.put(key, xmlElementList);
		}

		xmlElementList.add(xmlElement);
	}

	/**
	 * Add attribute to this node
	 * 
	 * @param key
	 * @param value
	 */
	public void addAttribute(String key, String value) {

		// I use the 'List' in order to cope when there are multiple attributes
		// with the same name.
		List<String> attrElementList = (ArrayList<String>) this.mLocalAttrMap.get(key);

		if (attrElementList == null) {
			attrElementList = new ArrayList<String>();
			this.mLocalAttrMap.put(key, attrElementList);
		}
		attrElementList.add(value);

	}

	public String stringValue() {
		return elementText;
	}

	public int intValue() {
		return Integer.parseInt(elementText);
	}

	public long longValue() {
		return Long.parseLong(elementText);
	}

	public float floatValue() {
		return Float.parseFloat(elementText);
	}

	public double doubleValue() {
		return Double.parseDouble(elementText);
	}

	public Map<String, List<String>> getLocalAttrMap() {
		return this.mLocalAttrMap;
	}

}
