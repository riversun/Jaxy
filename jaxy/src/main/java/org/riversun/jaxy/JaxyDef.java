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

/**
 * Constants for Jaxy<br>
 * 
 * Tom Misawa <riversun.org@gmail.com>
 *
 */
class JaxyDef {

	// Def of JSON Transform
	public static final String JS_PARSER_MAIN_CLASS_NAME = "AppMain_Json";
	public static final String JS_DEFAULT_PARENT_NODE_NAME = "json_parent";

	// Def of XML Transform
	public static final String XML_PARSER_MAIN_CLASS_NAME = "AppMain_Xml";
	public static final String HAVING_ATTRIBUTE_SIGN = "___________________________" + System.currentTimeMillis();

	// Def of Source Code Generation
	public static final String DEFAULT_NODE_NAME = "node";
	
	//
	public static final String DQUOT = "\"";
	
	//
	public static final String SOURCE_GEN_NUMBER_ELEMENT_PREFIX = "num_";
	public static final String SOURCE_GEN_LOOP_INDEX_PREFIX = "indexOf";

}
