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

import java.util.Set;

import org.riversun.jaxy.JaxyClient.SourceGenTrigger;

class JaxyClientTriggerCodeGen {

	enum TriggerType {
		JSON, //
		XML,
	}

	private TriggerType mTriggerType;
	private SourceGenTrigger mSourceGenTrigger;

	public JaxyClientTriggerCodeGen(TriggerType triggerType, SourceGenTrigger sourceGenTrigger) {
		mTriggerType = triggerType;
		mSourceGenTrigger = sourceGenTrigger;
	}

	String generate() {

		if (TriggerType.JSON == mTriggerType) {

			switch (mSourceGenTrigger.triggerMethod) {

			case FROM_NET:
				return generateJsonTriggerForNet();
			case FROM_FILE:
				return generateJsonTriggerForFile();
			case FROM_TEXT:
				return generateJsonTriggerForText();
			default:
				throw new RuntimeException("Please set triggerMethod.");
			}

		} else if (TriggerType.XML == mTriggerType) {

			switch (mSourceGenTrigger.triggerMethod) {
			case FROM_NET:
				return generateXmlTriggerForNet();
			case FROM_TEXT:
				return generateXmlTriggerForText();
			case FROM_FILE:
				return generateXmlTriggerForFile();

			default:
				throw new RuntimeException("Please set triggerMethod.");

			}

		} else {
			throw new RuntimeException("Please set triggerType.");
		}
	}

	private String generateJsonTriggerForNet() {

		final StringBuilder sb = new StringBuilder();

		generateCommonNetworkBasedTriggerCode(sb);

		sb.append("JsonNode node=jaxy.getJson();");
		sb.append("\n");

		return sb.toString();
	}

	private String generateJsonTriggerForText() {
		// TODO
		return "UNDER CONSTRUCTION";
	}

	private String generateJsonTriggerForFile() {
		// TODO
		return "UNDER CONSTRUCTION";
	}

	private String generateXmlTriggerForNet() {

		final StringBuilder sb = new StringBuilder();

		generateCommonNetworkBasedTriggerCode(sb);

		sb.append("XmlNode node=jaxy.getXml();");
		sb.append("\n");

		return sb.toString();
	}

	private String generateXmlTriggerForText() {
		// TODO
		return "UNDER CONSTRUCTION";
	}

	private String generateXmlTriggerForFile() {
		// TODO
		return "UNDER CONSTRUCTION";
	}

	private void generateCommonNetworkBasedTriggerCode(final StringBuilder sb) {

		final String DQ = JaxyDef.DQUOT;

		sb.append("JaxyClient jaxy = new JaxyClient();");
		sb.append("\n");

		// setendpoint
		sb.append("jaxy.setEndpointUrl(");
		sb.append(DQ + mSourceGenTrigger.endpointUrl + DQ);
		sb.append(");");
		sb.append("\n");

		// sethttpmethod
		sb.append("jaxy.setHttpMethod(");
		sb.append(DQ + mSourceGenTrigger.httpMethod + DQ);
		sb.append(");");
		sb.append("\n");

		if (mSourceGenTrigger.parameterMap != null && mSourceGenTrigger.parameterMap.size() > 0) {

			Set<String> keySet = mSourceGenTrigger.parameterMap.keySet();

			for (String key : keySet) {

				String[] paramValues = mSourceGenTrigger.parameterMap.get(key);

				for (String paramValue : paramValues) {

					sb.append("jaxy.addParameter(");
					sb.append(DQ + key + DQ + "," + DQ + paramValue + DQ);
					sb.append(");");
					sb.append("\n");
				}

			}
		}

		// useCdata
		sb.append("jaxy.setUseCDATA(");
		sb.append(mSourceGenTrigger.useCDATA);
		sb.append(");");
		sb.append("\n");

		if (mSourceGenTrigger.useProxy) {
			// setproxy
			sb.append("jaxy.setProxy(");
			sb.append(DQ + mSourceGenTrigger.proxyHost + DQ + "," + mSourceGenTrigger.proxyPort);
			sb.append(");");
			sb.append("\n");

		}

		if (mSourceGenTrigger.useBasicAuth) {
			sb.append("jaxy.setUseBasicAuth(true);");
			sb.append("\n");
			sb.append("jaxy.setBasicAuthUserNameAndPassword(");
			sb.append(DQ + mSourceGenTrigger.basicAuthUser + DQ + "," + DQ + mSourceGenTrigger.basicAuthPassword + DQ);
			sb.append(");");
			sb.append("\n");

		} else {
			sb.append("jaxy.setUseBasicAuth(false);");
			sb.append("\n");
			sb.append("jaxy.setBasicAuthUserNameAndPassword(null,null);");
			sb.append("\n");
		}
	}

}
