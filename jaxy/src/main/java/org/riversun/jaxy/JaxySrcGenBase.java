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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.riversun.jaxy.JaxyClient.SourceGenTrigger;

/**
 * Base class of JaxySrcGen<br>
 * Aggregate common functions simply
 * 
 * Tom Misawa <riversun.org@gmail.com>
 *
 * @param <T>
 */
abstract class JaxySrcGenBase<T extends JaxyNode> {
	Map<String, JaxySrcGenSourceCode> mModelClassSourceCodeList = null;
	JaxySrcGenSourceCode mParserMainClassSourceCode;

	protected T mTargetNode;

	private String mSrcCodePackageName = "";
	private String mSrcCodeClassNamePrefix = "";

	protected SourceGenTrigger mSourceGenTrigger;

	public void setSourceGenTrigger(SourceGenTrigger sourceGenTrigger) {
		mSourceGenTrigger = sourceGenTrigger;
	}

	public JaxySrcGenBase(T targetNode) {
		mTargetNode = targetNode;
	}

	protected abstract void buildSourceCodeClasses(T parentElement, boolean printWithHints, JaxySrcGenSourceCode parentSourceCode);

	public final void generateParserCodeTo(File dir) {

		// When scanning the array elements, if the new element that belongs to
		// the
		// array elements suddenly appeared,
		// There is a case in which the parser is created incompletely.
		//
		// Current practical solution is here,
		// You need to take the code protruding from
		// the loop back to correct position manually.
		// Anyway, I'd like to improve.

		mModelClassSourceCodeList = new LinkedHashMap<String, JaxySrcGenSourceCode>();

		// start recursive
		buildSourceCodeClasses(null, false, null);

		String filePath = dir.getAbsolutePath();

		saveSourceCodeToFile(filePath + File.separator + this.mParserMainClassSourceCode.getClassName() + ".java", this.mParserMainClassSourceCode.getString(), "UTF-8");
		// System.out.println("SouceCodeGenerated->" + filePath + File.separator
		// + this.mParserMainClassSourceCode.getClassName() + ".java");

		for (JaxySrcGenSourceCode cff : this.mModelClassSourceCodeList.values()) {
			saveSourceCodeToFile(filePath + File.separator + cff.getClassName() + ".java", cff.getString(), "UTF-8");
			// System.out.println("SouceCodeGenerated->" + filePath +
			// File.separator + cff.getClassName() + ".java");

		}

	}

	public final void generateParserCodeToConsole() {

		mModelClassSourceCodeList = new LinkedHashMap<String, JaxySrcGenSourceCode>();

		final StringBuilder sb = new StringBuilder();
		generateParserCodeTo(sb);

		System.out.println(sb.toString());
	}

	public final void generateParserCodeTo(StringBuilder sb) {

		mModelClassSourceCodeList = new LinkedHashMap<String, JaxySrcGenSourceCode>();

		this.buildSourceCodeClasses(null, false, null);

		sb.append("\n");
		sb.append(this.mParserMainClassSourceCode.getString());
		sb.append("\n");
		sb.append("\n");

		// append Model classes
		for (JaxySrcGenSourceCode cff : this.mModelClassSourceCodeList.values()) {

			sb.append(cff.getString());
			sb.append("\n");
			sb.append("\n");
		}

	}

	/**
	 * replace colon(:) to the underscore(_)
	 * 
	 * @param str
	 * @return
	 */
	protected String getColonSeparatedString(String str) {
		// When the source code generation, since the colon (:) included name
		// cannot be the Java class name,
		// so converts it into underscore(_).
		return str.replaceAll(":", "_");

	}

	/**
	 * First char of the text to upper case
	 * 
	 * @param str
	 * @return
	 */
	protected String getPrefixAddedInitialClassNameString(String str) {
		String firstString = str.substring(0, 1).toUpperCase();
		String restingString = str.substring(1, str.length());
		return getSrcCodeClassNamePrefix() + (firstString + restingString);
	}

	protected boolean isStartedWithInt(String str) {

		boolean ret = true;
		try {
			String firstChar = str.substring(0, 1);

			Integer.parseInt(firstChar);
		} catch (Exception e) {
			ret = false;
		}
		return ret;

	}

	protected void saveSourceCodeToFile(String fileName, String fileContent, String encoding) {

		File f = new File(fileName);

		FileOutputStream fos = null;
		BufferedWriter bw = null;

		try {
			fos = new FileOutputStream(f);

			bw = new BufferedWriter(new OutputStreamWriter(fos, encoding));

			bw.write(fileContent);
			bw.newLine();

			bw.flush();
			bw.close();

		} catch (Exception e) {
			// TODO Change to properly handle
			e.printStackTrace();
		}

	}

	public boolean isBlank(String str) {
		if (str == null) {
			return true;
		}
		if (str.equals("")) {
			return true;
		}
		return false;
	}

	public boolean isNotBlank(String str) {
		if (str != null && !str.equals("")) {
			return true;
		}
		return false;
	}

	public String getSrcCodePackageName() {
		return mSrcCodePackageName;
	}

	public void setSrcCodePackageName(String srcCodePackageName) {
		this.mSrcCodePackageName = srcCodePackageName;
	}

	public String getSrcCodeClassNamePrefix() {
		return mSrcCodeClassNamePrefix;
	}

	public void setSrcCodeClassNamePrefix(String srcCodeClassNamePrefix) {
		this.mSrcCodeClassNamePrefix = srcCodeClassNamePrefix;
	}

}
