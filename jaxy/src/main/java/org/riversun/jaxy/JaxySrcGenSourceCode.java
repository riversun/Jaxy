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

/**
 * Class for Source Code
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxySrcGenSourceCode {

	private final List<String> m_ModelPropertyName = new ArrayList<String>();
	public String m_ClassName = "";
	public String m_HeaderCode = "";
	private Map<String, String> sourceCodeLineMap = new LinkedHashMap<String, String>();

	private String m_TopObjectModelName;

	public void setTopObjectModelName(String topObjectModelName) {
		m_TopObjectModelName = topObjectModelName;
	}

	public String getTopObjectModelName() {
		return m_TopObjectModelName;
	}

	public String getClassName() {
		return getColonSeparatedString(m_ClassName);
	}

	public JaxySrcGenSourceCode(String className) {
		m_ClassName = className;
	}

	public void addString(String str) {
		String sentense = str + "\n";

		if (!sourceCodeLineMap.containsKey(sentense)) {
			sourceCodeLineMap.put(sentense, sentense);
		}
	}

	public void addModelPropertyName(String propertyName) {
		if (m_ModelPropertyName.contains(propertyName) == false) {
			m_ModelPropertyName.add(propertyName);
		}
	}

	public void setHeaderCode(String header) {
		m_HeaderCode = header;
	}

	public String getString() {

		String sourceCodeLine = "";

		sourceCodeLine += m_HeaderCode + "\n";

		sourceCodeLine += "public class " + getColonSeparatedString(m_ClassName) + " {" + "\n";
		for (String sentense : sourceCodeLineMap.values()) {
			sourceCodeLine += sentense;
		}

		int size = m_ModelPropertyName.size();

		if (size > 0) {

			/*
			 * generate toString method
			 */
			sourceCodeLine += "\n";
			sourceCodeLine += "public String toString(){" + "\n" + "return ";

			final StringBuilder sb = new StringBuilder();

			sb.append(JaxyDef.DQUOT + m_ClassName + "[" + JaxyDef.DQUOT + "+");

			for (int i = 0; i < size; i++) {

				String propName = m_ModelPropertyName.get(i);
				String head = JaxyDef.DQUOT + propName + "=" + JaxyDef.DQUOT + "+" + propName;
				String tail = "+" + JaxyDef.DQUOT + ",\\n" + JaxyDef.DQUOT + "+";

				if (i < size - 1) {
					String line = head + tail;
					sb.append(line);
				} else {
					String line = head;
					sb.append(line);
				}

			}
			sb.append("+" + JaxyDef.DQUOT + "]\\n" + JaxyDef.DQUOT);
			sb.append(";");

			sourceCodeLine += sb.toString();

			sourceCodeLine += "}" + "\n";
		}

		sourceCodeLine += "}";

		return sourceCodeLine;
	}

	private String getColonSeparatedString(String str) {

		return str.replaceAll(":", "_");

	}

}