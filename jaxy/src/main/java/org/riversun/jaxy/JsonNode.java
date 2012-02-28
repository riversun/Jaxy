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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * JSON Node
 * 
 * Tom Misawa <riversun.org@gmail.com>
 */
public class JsonNode extends JaxyNode {

	static final String TAG_PRETTY_SPACE = "  ";

	protected Map mLocalDataMap = new LinkedHashMap();

	boolean mHasNoSingleParent = false;

	public JsonNode mParenNode;

	public JsonNode child(String key) {

		List list = (ArrayList) this.mLocalDataMap.get(key);

		JsonNode retVal = null;

		if (list != null) {
			JsonNode[] elementNodeArray = (JsonNode[]) list.toArray(new JsonNode[0]);
			retVal = elementNodeArray[0];
		} else {

			retVal = new JsonNode();
		}
		return retVal;
	}

	public JsonNode[] children(String key) {

		List list = (List) this.mLocalDataMap.get(key);

		JsonNode[] retVal = null;

		if (list != null) {
			JsonNode[] elementNodeArray = (JsonNode[]) list.toArray(new JsonNode[0]);
			retVal = elementNodeArray;
		} else {

			retVal = new JsonNode[] {};
		}
		return retVal;
	}

	public String[] getChildElementNames() {
		Set childElementNameSet = this.mLocalDataMap.keySet();
		String[] retVal = (String[]) childElementNameSet.toArray(new String[] {});
		return retVal;

	}

	public void removeFirstChildNode(String key, JsonNode node) {
		List elementNodeList = (List) this.mLocalDataMap.get(key);
		if (elementNodeList != null) {
			if (elementNodeList.size() > 0) {
				elementNodeList.remove(0);
			}
		}

	}

	public void addChildNode(String key, JsonNode node) {
		node.mParenNode = this;

		List elementNodeList = (List) this.mLocalDataMap.get(key);
		if (elementNodeList == null) {
			elementNodeList = new ArrayList();
			this.mLocalDataMap.put(key, elementNodeList);
		}

		elementNodeList.add(node);
	}

	/**
	 * Set one parent element
	 * 
	 * @param val
	 */
	public void setHasNoSingleParent(boolean val) {
		mHasNoSingleParent = val;
	}

	@Override
	public String toString() {
		return elementText;
	}

}
