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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Transform from Text into JSON<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxyConvJsonFromText {

	private final JaxyJsonNodeParser mJaxyParser = new JaxyJsonNodeParser();

	/**
	 * transform JSON text into JsonNode
	 * 
	 * @param jsonText
	 * @return
	 */
	public JsonNode toJsonNode(String jsonText) {

		JsonObjectWrapper jsonObjEx = toJsonNodeInternally(jsonText);

		JsonNode jsonNode = mJaxyParser.buildJsonNode(jsonObjEx.jsonObj);

		jsonNode.setHasNoSingleParent(jsonObjEx.hasNoSingleParent);

		return jsonNode;
	}

	/**
	 * Handling jsonText to JsonNode after the determination of the parent
	 * element count whether one or multiple<br>
	 * 
	 * @param jsonText
	 * @return
	 */
	private JsonObjectWrapper toJsonNodeInternally(String jsonText) {

		JSONObject jsonObj = toJsonObject(jsonText);

		final int jsonChildrenCount = jsonObj.length();

		final boolean hasNoSingleParent;

		if (jsonChildrenCount > 1) {

			// If JSON has no single parent.
			jsonText = "{\"" + JaxyDef.JS_DEFAULT_PARENT_NODE_NAME + "\":" + jsonText + "}";

			jsonObj = toJsonObject(jsonText);
			hasNoSingleParent = true;

		} else {
			// If JSON has single parent.
			hasNoSingleParent = false;
		}

		return new JsonObjectWrapper(jsonObj, hasNoSingleParent);

	}

	/**
	 * JSON text to JSONOBject provided by json.org
	 * 
	 * @param jsonText
	 * @return
	 */
	private JSONObject toJsonObject(String jsonText) {

		JSONObject jsonObj;

		try {
			jsonObj = new JSONObject(jsonText);
		} catch (JSONException e) {
			e.printStackTrace();
			// if error occurred ,blank JSON object will be returned
			jsonObj = new JSONObject();

		}
		return jsonObj;
	}

	private static class JsonObjectWrapper {

		JSONObject jsonObj;

		boolean hasNoSingleParent = false;

		public JsonObjectWrapper(JSONObject jsonObj, boolean hasNoSingleParent) {
			super();
			this.jsonObj = jsonObj;
			this.hasNoSingleParent = hasNoSingleParent;
		}

	}
}
