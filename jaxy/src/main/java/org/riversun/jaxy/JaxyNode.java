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
 * Base class provides common variables
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
class JaxyNode {

	static final String TAG_PRETTY_SPACE = "  ";

	// name of element
	String elementName;

	// text value of element
	String elementText;

	// String to put on the head of property class name when source code
	// generation
	// String m_SouceCodeClassNamePrefix = "";

	// Indent space when you print this element, is used in the recursive
	// processing of print method
	String mIndentText = " ";

	String mMyElementAccessHint = "";
	String mMyClassName = "";
	String mMyClassNamePrimitive = "";
}
