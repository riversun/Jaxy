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

import org.riversun.jaxy.JaxyClient.SourceGenTriggerMethod;
import org.riversun.jaxy.JaxyClientTriggerCodeGen.TriggerType;

/**
 * Parser source code generator for JsonNode<b>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxySrcGen4JsonNode extends JaxySrcGenBase<JsonNode> {

	private JaxySrcGenSourceCode mCurrentClassSourceCode = null;

	/**
	 * Construct
	 * 
	 * @param targetNode
	 *            Target JsonNode to generate parser code.
	 */
	public JaxySrcGen4JsonNode(JsonNode targetNode) {
		super(targetNode);
	}

	protected void buildSourceCodeClasses(JsonNode parentElement, boolean printWithHints, JaxySrcGenSourceCode parentSourceCode) {
		String pkgName = getSrcCodePackageName();

		boolean isNowScanningTopElement = false;

		if (parentElement == null) {

			// null parent means I'm at the top.
			parentElement = mTargetNode;
			isNowScanningTopElement = true;

		}

		String dot = ".";

		if (mParserMainClassSourceCode == null) {
			mParserMainClassSourceCode = new JaxySrcGenSourceCode(getSrcCodeClassNamePrefix() + JaxyDef.JS_PARSER_MAIN_CLASS_NAME);
		}

		if (isNowScanningTopElement) {

			dot = "";

			StringBuilder header = new StringBuilder();

			if (isNotBlank(pkgName)) {
				header.append("\npackage " + pkgName + ";\n");
			}
			header.append("import org.riversun.jaxy.*;");
			mParserMainClassSourceCode.setHeaderCode(header.toString());
			mParserMainClassSourceCode.addString("public static void main(String[] args) throws JaxyClientNetworkException {");

			if (SourceGenTriggerMethod.UNKNOWN == mSourceGenTrigger.triggerMethod) {

				mParserMainClassSourceCode.addString("JaxyClient client=new JaxyClient();");
				mParserMainClassSourceCode.addString("JsonNode node=new JsonNode();");

			} else {

				String triggerCode = new JaxyClientTriggerCodeGen(TriggerType.JSON, mSourceGenTrigger).generate();
				mParserMainClassSourceCode.addString(triggerCode);
			}

		}

		final String[] elementNames = parentElement.getChildElementNames();

		for (int elementIdx = 0; elementIdx < elementNames.length; elementIdx++) {

			JsonNode[] childElements = parentElement.children(elementNames[elementIdx]);

			if (isNowScanningTopElement) {
				parentElement.mMyElementAccessHint = JaxyDef.DEFAULT_NODE_NAME;
			}

			boolean arrayFlag = false;

			if (childElements.length > 1) {

				arrayFlag = true;

			} else if (childElements.length > 0) {

				arrayFlag = false;

			}

			for (int childIdx = 0; childIdx < childElements.length; childIdx++) {

				JsonNode childElement = childElements[childIdx];

				final String childElementName = childElement.elementName;
				String childElementNameForProp = childElement.elementName;

				if (isStartedWithInt(childElementNameForProp)) {
					// --If the property starts with number.
					childElementNameForProp = JaxyDef.SOURCE_GEN_NUMBER_ELEMENT_PREFIX + childElementNameForProp;
				}

				if (isNowScanningTopElement) {
					String topObjectModelName = getColonSeparatedString(childElementName);
					mParserMainClassSourceCode.addString(getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementNameForProp)) + " " + topObjectModelName + //
							"= new " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");
					mParserMainClassSourceCode.setTopObjectModelName(topObjectModelName);
				}

				childElement.mIndentText += parentElement.mIndentText + JsonNode.TAG_PRETTY_SPACE;

				if (childElements.length > 1) {

					childElement.mMyElementAccessHint = parentElement.mMyElementAccessHint + ".children(\"" + childElementName + "\")[" + JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX
							+ getColonSeparatedString(childElementNameForProp) + "]";

					childElement.mMyClassName = parentElement.mMyClassName + dot + childElementNameForProp + "[" + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX)
							+ getColonSeparatedString(childElementNameForProp) + "]";// Array

					childElement.mMyClassNamePrimitive = (parentElement.mMyClassName + dot + childElementNameForProp);

				} else {

					childElement.mMyElementAccessHint = parentElement.mMyElementAccessHint + ".child(\"" + childElementName + "\")";
					childElement.mMyClassName = parentElement.mMyClassName + dot + childElementNameForProp;

				}

				if (childElement.elementText != null) {
					// --If the child element is TEXT value.

					if (!arrayFlag) {
						// --If the child element is TEXT value AND is not ARRAY

						final String modelPropertyName = getColonSeparatedString(childElementNameForProp);
						String defString = "public String " + modelPropertyName + ";";

						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + //
								"=" + childElement.mMyElementAccessHint + ".toString();");

					}

					else if (arrayFlag) {
						// --If the child element is TEXT value AND is ARRAY

						String modelPropertyName = getColonSeparatedString(childElementNameForProp);
						String defString = "public String" + "[] " + modelPropertyName + ";";
						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						String arraySizeStr = (parentElement.mMyElementAccessHint) + ".children(\"" + childElementName + "\").length";

						mParserMainClassSourceCode.addString("// make " + childElementNameForProp + " array object");
						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementNameForProp) + //
								"= new " + "String" + "[" + arraySizeStr + "];");

						mParserMainClassSourceCode.addString("for (int " + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX + childElementNameForProp)
								+ //
								"=0;" + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX + childElementNameForProp) + "<" + arraySizeStr + ";" + JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX
								+ getColonSeparatedString(childElementNameForProp) + "++){");

						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementNameForProp) + "["
								+ getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX) + getColonSeparatedString(childElementNameForProp) + "]"//
								+ "=" + childElement.mMyElementAccessHint + ".toString();");

					}

				} else {
					// -If the child element is CLASS or ARRAY

					if (!arrayFlag) {

						if (isNowScanningTopElement) {
							// --If current is top element
							// No need to generate CLASS source code.
						} else {

							String modelPropertyName = getColonSeparatedString(childElementNameForProp);
							String defString = "public " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementNameForProp)) + " " + modelPropertyName + ";";

							mCurrentClassSourceCode.addString(defString);
							mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

							mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + //
									"= new " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");

						}
					} else if (arrayFlag) {

						String modelPropertyName = childElementNameForProp;
						String defString = "public " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementNameForProp)) + "[] " + modelPropertyName + ";";

						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						String arraySizeStr = parentElement.mMyElementAccessHint + ".children(\"" + childElementName + "\").length";

						mParserMainClassSourceCode.addString("// make " + childElementNameForProp + " to array object");
						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementNameForProp) + //
								"= new " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "[" + arraySizeStr + "];");

						mParserMainClassSourceCode.addString("for (int " + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX + childElementNameForProp)
								+ //
								"=0;" + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX + childElementNameForProp) + "<" + arraySizeStr + ";" + JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX
								+ getColonSeparatedString(childElementNameForProp) + "++){");

						mParserMainClassSourceCode.addString(//
								getColonSeparatedString(parentElement.mMyClassName) + dot//
										+ getColonSeparatedString(childElementNameForProp) //
										+ "[" + getColonSeparatedString(JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX) + getColonSeparatedString(childElementNameForProp) + "]"//
										+ "= new " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");

					}

					JaxySrcGenSourceCode classFile = mModelClassSourceCodeList.get(childElementNameForProp);

					if (classFile == null) {

						mCurrentClassSourceCode = new JaxySrcGenSourceCode(getPrefixAddedInitialClassNameString(childElementNameForProp));

						if (isNotBlank(pkgName)) {
							mCurrentClassSourceCode.setHeaderCode("\npackage " + pkgName + ";\n");
						}
						//
						mModelClassSourceCodeList.put(childElementNameForProp, mCurrentClassSourceCode);

					} else {

						mCurrentClassSourceCode = classFile;

					}

				}

				if (childElement.elementText != null) {

					if (arrayFlag) {
						mParserMainClassSourceCode.addString("} //" + childElementNameForProp);
					}

				} else {

					this.buildSourceCodeClasses(childElement, printWithHints, mCurrentClassSourceCode);

					if (arrayFlag) {
						mParserMainClassSourceCode.addString("} //" + childElementNameForProp);
					}

					if (isNowScanningTopElement) {
						mParserMainClassSourceCode.addString("\n");
						mParserMainClassSourceCode.addString("//TODO");
						mParserMainClassSourceCode.addString("//Write down your code here");
						String topObjectModelName = mParserMainClassSourceCode.getTopObjectModelName();
						mParserMainClassSourceCode.addString("System.out.println(" + JaxyDef.DQUOT + topObjectModelName + "=" + JaxyDef.DQUOT + "+" + topObjectModelName + ");");

						mParserMainClassSourceCode.addString("}//main()");
					}

					mCurrentClassSourceCode = parentSourceCode;

				}
			}
		}
	}

}
