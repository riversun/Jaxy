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

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parse the JSONObject to build a JsonNode tree<br>
 * <br>
 * 
 * The JSON data as follows,<br>
 * { "result": { "message": [ { "msgId": "01" ,"msgContent": "Message 01" } ,{
 * "msgId": "02" ,"msgContent": "Message 02" } ] ,"messageCount":"2" } } <br>
 * 
 * <br>
 * 
 * I will provide an intuitive access as follows like JavaScript associative
 * object tree/object model.
 * 
 * <br>
 * JsonNode.child("result").child("messageCount").toString();
 * JsonNode.child("result").children("message")[0].child("msgId").toString(); <br>
 * <br>
 * <br>
 * 
 * If the child node is array node (JsonNode[]), you can access it by using
 * children() like followings.<br>
 * JsonNode.children([KEYNAME])[0]<br>
 * <br>
 * 
 * You can access in the same way by using method chain even if elements become
 * deep.<br>
 * <br>
 * parentNode.child("TopLevelElement").child("SecondLevel").child("ThirdLevel").
 * toString(); <br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxyJsonNodeParser {

	/**
	 * Build the JsonNode tree<br>
	 * 
	 * @param jsonObject
	 * @return
	 */
	public JsonNode buildJsonNode(JSONObject jsonObject) {

		JsonNode resultNode = new JsonNode();

		try {
			parse(jsonObject, resultNode);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultNode;
	}

	/**
	 * Print the extended jsonObject hint to System.out
	 * 
	 * @param jsonObject
	 */
	public void printHints(JSONObject jsonObject) {

		JsonNode resultNode = new JsonNode();

		try {
			StringBuffer sb = new StringBuffer();
			showAllParamsValues(jsonObject, resultNode, 0, "", sb);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void parse(Object parent, JsonNode parentNode) throws JSONException {

		// If the caller parent was JSONObject
		if (parent instanceof JSONObject) {

			JSONObject parentJSONObject = (JSONObject) parent;

			Iterator itor = parentJSONObject.keys();

			while (itor.hasNext()) {
				String key = (String) itor.next();

				// create child node
				JsonNode childNode = new JsonNode();
				childNode.elementName = key;

				// Get the element one by one from the parent JSONObject
				Object childObj = parentJSONObject.get(key);

				if (!(childObj instanceof JSONObject) && !(childObj instanceof JSONArray)) {

					// If the child node of the parent JSONObject is neither
					// JSONArray even JSONObject,
					// It means that( the child node ) is the VALUE node that
					// takes value.
					childNode.elementText = String.valueOf(childObj);

					parentNode.addChildNode(key, childNode);

				} else if (childObj instanceof JSONObject) {

					// If the child element was JSONObject, to continue the
					// analysis by the recursive call.
					parentNode.addChildNode(key, childNode);
					parse(childObj, childNode);

				} else if (childObj instanceof JSONArray) {
					// If the child element was JSONArray, to continue the
					// analysis by the recursive call.
					parentNode.addChildNode(key, childNode);
					parse(childObj, childNode);
				}

			}

		} else if (parent instanceof JSONArray) {

			// --if the calling parent was JSONOArray
			JSONArray parentJSONArray = (JSONArray) parent;

			int leng = parentJSONArray.length();

			// start the loop of popping from array

			for (int i = 0; i < leng; i++) {

				Object childObj = parentJSONArray.get(i);

				JsonNode childNode = new JsonNode();
				childNode.elementName = parentNode.elementName;

				// [Design tips]
				// For this purpose, although in JsonNode have set a
				// reference to its parent,
				// Depending on how to make it is also possible to in the
				// recursion (parent, child) the design with arguments that.
				// GrandParent
				//
				// [Node building image]
				// __|-ParentNode (Added first)
				// __|-childNode (Adding on the currently scanned line)
				// __|-childNode (Adding on the currently scanned line)
				// __|-childNode (Adding on the currently scanned line)
				//
				parentNode.mParenNode.addChildNode(childNode.elementName, childNode);
				parse(childObj, childNode);

			}

			JsonNode grandParentNode = parentNode.mParenNode;
			String key = parentNode.elementName;

			// After you exit the loop, remove the parentNode (first element of
			// the child of GrandParentNode) from grandParentNode.
			// Here the array elements and number of the array fit and
			// reconcile.
			grandParentNode.removeFirstChildNode(key, parentNode);
		}

	}

	private void showAllParamsValues(Object parent, JsonNode parentNode, int level, String line, StringBuffer buffer) throws JSONException {

		if (parent instanceof JSONObject) {

			JSONObject parentJSONObject = (JSONObject) parent;
			Iterator itor = parentJSONObject.keys();

			while (itor.hasNext()) {
				String key = (String) itor.next();

				JsonNode childNode = new JsonNode();
				childNode.elementName = key;

				Object childObj = parentJSONObject.get(key);

				if (!(childObj instanceof JSONObject) && !(childObj instanceof JSONArray)) {

					parentNode.addChildNode(key, childNode);
					childNode.elementText = String.valueOf(childObj);
					buffer.append(line + "._(" + JaxyDef.DQUOT + key + JaxyDef.DQUOT + ")=" + JaxyDef.DQUOT + childNode.elementText + JaxyDef.DQUOT);
					buffer.append("\r\n");

				} else if (childObj instanceof JSONObject) {

					parentNode.addChildNode(key, childNode);

					showAllParamsValues(childObj, childNode, level + 1, line + "._(" + JaxyDef.DQUOT + key + JaxyDef.DQUOT + ")", buffer);

				} else if (childObj instanceof JSONArray) {

					parentNode.addChildNode(key, childNode);
					showAllParamsValues(childObj, childNode, level + 1, line, buffer);

				}
			}

		} else if (parent instanceof JSONArray) {
			JSONArray parentJSONArray = (JSONArray) parent;
			int leng = parentJSONArray.length();

			for (int i = 0; i < leng; i++) {
				Object childObj = parentJSONArray.get(i);

				JsonNode childNode = new JsonNode();
				childNode.elementName = parentNode.elementName;

				parentNode.mParenNode.addChildNode(childNode.elementName, childNode);

				showAllParamsValues(childObj, childNode, level + 1, line + ".__(" + JaxyDef.DQUOT + parentNode.elementName + JaxyDef.DQUOT + ")[" + String.valueOf(i) + "]", buffer);

			}

			JsonNode grandParentNode = parentNode.mParenNode;
			String key = parentNode.elementName;

			grandParentNode.removeFirstChildNode(key, parentNode);
		}

	}

}