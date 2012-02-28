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

import java.util.List;
import java.util.Map;

import org.riversun.jaxy.JaxyClient.SourceGenTriggerMethod;
import org.riversun.jaxy.JaxyClientTriggerCodeGen.TriggerType;

/**
 * Parser source code generator for JsonNode<b>
 * 
 * Tom Misawa <riversun.org@gmail.com>
 *
 */
class JaxySrcGen4XmlNode extends JaxySrcGenBase<XmlNode> {

	// Whether to generate a stub for XML attributes (attribute) or not.
	private static final boolean PARSER_ATTRIBUTES_ENABLED = true;

	private JaxySrcGenSourceCode mCurrentClassSourceCode = null;

	/**
	 * Construct
	 * 
	 * @param targetNode
	 *            Target XmlNode to generate parser code.
	 */
	public JaxySrcGen4XmlNode(XmlNode targetNode) {
		super(targetNode);
	}

	/**
	 *
	 * Analyze and generate source code.<br>
	 * <br>
	 * If the element originally defined as array have been returned in an array
	 * of size 1, The parser will take a mistake.This process is still immature.
	 * I need improvement... <br>
	 * 
	 * @param parentElement
	 * @param printWithHints
	 * @param parentSourceCode
	 */
	protected void buildSourceCodeClasses(XmlNode parentElement, boolean printWithHints, JaxySrcGenSourceCode parentSourceCode) {

		String pkgName = getSrcCodePackageName();

		final String LOOP_INDEX_PREFIX = JaxyDef.SOURCE_GEN_LOOP_INDEX_PREFIX;

		boolean isNowScanningTopElement = false;

		if (parentElement == null) {
			parentElement = mTargetNode;
			isNowScanningTopElement = true;
		}

		String dot = ".";

		if (mParserMainClassSourceCode == null) {
			mParserMainClassSourceCode = new JaxySrcGenSourceCode(getSrcCodeClassNamePrefix() + JaxyDef.XML_PARSER_MAIN_CLASS_NAME);
		}

		if (isNowScanningTopElement) {

			dot = "";

			parentElement.mMyElementAccessHint = "node";

			StringBuilder header = new StringBuilder();

			if (isNotBlank(pkgName)) {
				header.append("\npackage " + pkgName + ";\n");
			}

			header.append("import org.riversun.jaxy.*;");

			mParserMainClassSourceCode.setHeaderCode(header.toString());

			mParserMainClassSourceCode.addString("public static void main(String[] args) throws JaxyClientNetworkException {");

			if (SourceGenTriggerMethod.UNKNOWN == mSourceGenTrigger.triggerMethod) {
				mParserMainClassSourceCode.addString("JaxyClient jaxy=new JaxyClient();");
				mParserMainClassSourceCode.addString("XmlNode node=new XmlNode();");

			} else {
				String triggerCode = new JaxyClientTriggerCodeGen(TriggerType.XML, mSourceGenTrigger).generate();
				mParserMainClassSourceCode.addString(triggerCode);
			}

		}
		String[] elementNames = parentElement.getChildElementNames();

		for (int elementIdx = 0; elementIdx < elementNames.length; elementIdx++) {

			XmlNode[] childElements = parentElement.children(elementNames[elementIdx]);
			if (isNowScanningTopElement) {

			}

			boolean arrayFlag = false;

			if (childElements.length > 1) {
				arrayFlag = true;
			} else if (childElements.length > 0) {
				arrayFlag = false;
			}

			for (int childIdx = 0; childIdx < childElements.length; childIdx++) {

				XmlNode childElement = childElements[childIdx];

				Map attrMap = childElement.getLocalAttrMap();

				String childElementName = childElement.elementName;
				if (isNowScanningTopElement) {
					// Top element
					String topObjectModelName = getColonSeparatedString(childElementName);
					mParserMainClassSourceCode.addString(getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + " " + topObjectModelName + "= new "
							+ getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");

					mParserMainClassSourceCode.setTopObjectModelName(topObjectModelName);
				}

				childElement.mIndentText += parentElement.mIndentText + XmlNode.TAG_PRETTY_SPACE;

				if (childElements.length > 1) {

					childElement.mMyElementAccessHint = parentElement.mMyElementAccessHint + ".children(\"" + childElementName + "\")[" + LOOP_INDEX_PREFIX
							+ getColonSeparatedString(childElementName) + "]";

					// Array
					childElement.mMyClassName = parentElement.mMyClassName + dot + childElementName + "[" + getColonSeparatedString(LOOP_INDEX_PREFIX) + getColonSeparatedString(childElementName)
							+ "]";

					// Also remain that are not array representation
					// (for String attribute array)
					childElement.mMyClassNamePrimitive = (parentElement.mMyClassName + dot + childElementName);

				} else {
					childElement.mMyElementAccessHint = parentElement.mMyElementAccessHint + ".child(\"" + childElementName + "\")";
					childElement.mMyClassName = parentElement.mMyClassName + dot + childElementName;
				}

				if (childElement.elementText != null) {
					// --If the child element is TEXT value.

					if (!arrayFlag) {
						// --If the child element is TEXT value AND is not ARRAY
						final String modelPropertyName = getColonSeparatedString(childElementName);
						String defString = "public String " + modelPropertyName + ";";

						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + "=" + childElement.mMyElementAccessHint + ".toString();");

						// Setting of attr,when the element is a String value
						if (PARSER_ATTRIBUTES_ENABLED) {
							Map<String, List<String>> stringElementAttrMap = childElement.getLocalAttrMap();
							for (String key : stringElementAttrMap.keySet()) {

								mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + "_attr_" + key + "=" + childElement.mMyElementAccessHint + ".attr(\"" + key
										+ "\"); ");

								String attrName = getColonSeparatedString(childElementName) + "_attr_" + key;

								String attrString = "public String " + attrName + ";";
								mCurrentClassSourceCode.addString(attrString);
								mCurrentClassSourceCode.addModelPropertyName(attrName);
							}
						}

					} else if (arrayFlag) {

						// --If the child element is TEXT value AND is ARRAY

						String modelPropertyName = getColonSeparatedString(childElementName);
						String defString = "public " + "String" + "[] " + modelPropertyName + ";";
						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						String arraySizeStr = (parentElement.mMyElementAccessHint) + ".children(\"" + childElementName + "\").length";

						mParserMainClassSourceCode.addString("// make " + childElementName + " array object");
						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementName) + "= new " + "String" + "["
								+ arraySizeStr + "];");

						// Make array for attributes
						if (PARSER_ATTRIBUTES_ENABLED) {

							// Make setters for attributes
							Map<String, List<String>> targetElementAttrMap = childElement.getLocalAttrMap();

							if (targetElementAttrMap.size() > 0) {

								// --If there are attributes,make setter method
								// for it.

								for (String key : targetElementAttrMap.keySet()) {

									mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassNamePrimitive) + "_attr_" + getColonSeparatedString(key) + "=" + "new "
											+ "String" + "[" + arraySizeStr + "];");
								}
							}
						}

						mParserMainClassSourceCode.addString("for (int " + getColonSeparatedString(LOOP_INDEX_PREFIX + childElementName) + "=0;"
								+ getColonSeparatedString(LOOP_INDEX_PREFIX + childElementName) + "<" + arraySizeStr + ";" + LOOP_INDEX_PREFIX + getColonSeparatedString(childElementName) + "++){");

						mParserMainClassSourceCode.addString("// make array element " + childElementName + " object");
						mParserMainClassSourceCode.addString("// if  childElement.name is innerclass, you should define as follows");

						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementName) + "["
								+ getColonSeparatedString(LOOP_INDEX_PREFIX) + getColonSeparatedString(childElementName) + "]=" + childElement.mMyElementAccessHint + ".toString();");

						if (PARSER_ATTRIBUTES_ENABLED) {

							// Make setters for attributes
							Map<String, List<String>> targetElementAttrMap = childElement.getLocalAttrMap();

							// --If there are attributes,make setter method
							// for it.
							if (targetElementAttrMap.size() > 0) {

								mParserMainClassSourceCode.addString("// set xml attribute values for " + childElementName);
								for (String key : targetElementAttrMap.keySet()) {

									mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassNamePrimitive) + "_attr_" + getColonSeparatedString(key) + "["
											+ LOOP_INDEX_PREFIX + getColonSeparatedString(childElementName) + "]" + "=" + getColonSeparatedString(childElement.mMyElementAccessHint) + ".attr(\""
											+ key + "\");");

									String attrName = getColonSeparatedString(childElementName) + "_attr_" + getColonSeparatedString(key);
									String attrString = "public String[] " + attrName + ";";
									mCurrentClassSourceCode.addString(attrString);

									mCurrentClassSourceCode.addModelPropertyName(attrName);

								}
							}
						}

					}

				} else {// if (childElement.elementText != null) {

					// -If the child element is CLASS or ARRAY

					if (!arrayFlag) {

						if (isNowScanningTopElement) {

							// --If current is top element
							// No need to generate CLASS source code.

						} else {

							String modelPropertyName = getColonSeparatedString(childElementName);

							String defString = "public " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + " " + modelPropertyName + ";";

							mCurrentClassSourceCode.addString(defString);
							mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

							mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + "= new "
									+ getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");

							if (PARSER_ATTRIBUTES_ENABLED) {

								// Make setters for attributes
								Map<String, List<String>> targetElementAttrMap = childElement.getLocalAttrMap();

								// --If there are attributes,make setter method
								// for it.
								if (targetElementAttrMap.size() > 0) {

									String attributeClassName = getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "_Attribute";
									mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + ".attr= new " + getColonSeparatedString(attributeClassName) + "();");

									mParserMainClassSourceCode.addString("// set xml attribute values for " + childElementName);
									for (String key : targetElementAttrMap.keySet()) {

										mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + ".attr." + getColonSeparatedString(key) + "="
												+ childElement.mMyElementAccessHint + ".attr(\"" + key + "\");");
									}
								}
							}

						}
					} else if (arrayFlag) {

						String modelPropertyName = childElementName;
						String defString = "public " + getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "[] " + modelPropertyName + ";";
						mCurrentClassSourceCode.addString(defString);
						mCurrentClassSourceCode.addModelPropertyName(modelPropertyName);

						String arraySizeStr = parentElement.mMyElementAccessHint + ".children(\"" + childElementName + "\").length";

						mParserMainClassSourceCode.addString("// make " + childElementName + " array object");
						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementName) + "= new "
								+ getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "[" + arraySizeStr + "];");

						mParserMainClassSourceCode.addString("for (int " + getColonSeparatedString(LOOP_INDEX_PREFIX + childElementName) + "=0;"
								+ getColonSeparatedString(LOOP_INDEX_PREFIX + childElementName) + "<" + arraySizeStr + ";" + LOOP_INDEX_PREFIX + getColonSeparatedString(childElementName) + "++){");

						mParserMainClassSourceCode.addString("// make array element " + childElementName + " object");
						mParserMainClassSourceCode.addString("// if  childElement.name is innerclass, you should define as follows");
						mParserMainClassSourceCode.addString("// " + parentElement.mMyClassName + dot + childElementName + "[i" + childElementName + "]= "
								+ getColonSeparatedString(parentElement.mMyClassName) + dot + "new " + (getPrefixAddedInitialClassNameString(childElementName)) + "();");
						mParserMainClassSourceCode.addString(getColonSeparatedString(parentElement.mMyClassName) + dot + getColonSeparatedString(childElementName) + "["
								+ getColonSeparatedString(LOOP_INDEX_PREFIX) + getColonSeparatedString(childElementName) + "]= new "
								+ getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "();");

						if (PARSER_ATTRIBUTES_ENABLED) {

							Map<String, List<String>> targetElementAttrMap = childElement.getLocalAttrMap();

							// Setter to the parsermain class
							if (targetElementAttrMap.size() > 0) {

								String attributeClassName = getColonSeparatedString(getPrefixAddedInitialClassNameString(childElementName)) + "_Attribute";
								mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + ".attr= new " + attributeClassName + "();");

								mParserMainClassSourceCode.addString("// set xml attribute values for " + childElementName);
								for (String key : targetElementAttrMap.keySet()) {

									mParserMainClassSourceCode.addString(getColonSeparatedString(childElement.mMyClassName) + ".attr." + getColonSeparatedString(key) + "="
											+ childElement.mMyElementAccessHint + ".attr(\"" + key + "\");");
								}
							}
						}

					}

					JaxySrcGenSourceCode classFile = mModelClassSourceCodeList.get(childElementName);

					if (classFile == null) {
						mCurrentClassSourceCode = new JaxySrcGenSourceCode(getPrefixAddedInitialClassNameString(childElementName));

						if (isNotBlank(pkgName)) {
							mCurrentClassSourceCode.setHeaderCode("\npackage " + pkgName + ";\n");
						}

						mModelClassSourceCodeList.put(childElementName, mCurrentClassSourceCode);
					} else {
						mCurrentClassSourceCode = classFile;
					}

					if (PARSER_ATTRIBUTES_ENABLED) {
						Map<String, List<String>> targetElementAttrMap = childElement.getLocalAttrMap();
						if (targetElementAttrMap.size() > 0) {

							String attributeClassName = getPrefixAddedInitialClassNameString(childElementName) + "_Attribute";

							mCurrentClassSourceCode.addString("public " + getColonSeparatedString(attributeClassName) + " attr;");

							JaxySrcGenSourceCode attributeClass = mModelClassSourceCodeList.get(attributeClassName);

							if (attributeClass == null) {
								attributeClass = new JaxySrcGenSourceCode(attributeClassName);
								mModelClassSourceCodeList.put(attributeClassName, attributeClass);
							}

							for (String key : targetElementAttrMap.keySet()) {
								attributeClass.addString("public String " + getColonSeparatedString(key) + ";");
							}
						}
					}

				}

				if (childElement.elementText != null) {

					// -- If the value is set to the element,childElement is the
					// lowest layer element.

					if (arrayFlag) {
						mParserMainClassSourceCode.addString("} //" + childElementName);
					}

				} else {

					// -- When no value is set to the element

					// Call recursive search because that would be to have the
					// child elements.
					this.buildSourceCodeClasses(childElement, printWithHints, mCurrentClassSourceCode);

					if (arrayFlag) {
						mParserMainClassSourceCode.addString("} //" + childElementName);
					}
					if (isNowScanningTopElement) {
						mParserMainClassSourceCode.addString("\n");
						mParserMainClassSourceCode.addString("//TODO");
						mParserMainClassSourceCode.addString("//Write down your code here");
						String topObjectModelName = mParserMainClassSourceCode.getTopObjectModelName();
						mParserMainClassSourceCode.addString("System.out.println(" + JaxyDef.DQUOT + topObjectModelName + "=" + JaxyDef.DQUOT + "+" + topObjectModelName + ");");
						mParserMainClassSourceCode.addString("}//main()");
					}

					// Put the class before dive
					mCurrentClassSourceCode = parentSourceCode;

				}
			}
		}
	}
}
