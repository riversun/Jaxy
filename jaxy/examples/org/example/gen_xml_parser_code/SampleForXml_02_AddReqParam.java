package org.example.gen_xml_parser_code;

import org.riversun.jaxy.JaxyClient;
import org.riversun.jaxy.JaxyClientNetworkException;
import org.riversun.jaxy.XmlNode;

/**
 * Sample Code for Jaxy<br>
 * <br>
 * Category:Generate SourceCode of XML Parser by Accessing web services<br>
 * <br>
 * Automatically generate the XML/JSON parser source code,<br>
 * you get it surprisingly fast. <br>
 * <br>
 * <br>
 * [Sample Description]<br>
 * <br>
 * - Generate XML parser source code for XML base Web API no.02<br>
 * --you can add a request parameter by calling 'addParameter' method<br>
 * 
 * 
 * Tom Misawa <riversun.org@gmail.com>
 *
 */
public class SampleForXml_02_AddReqParam {

	/**
	 * @param args
	 * @throws JaxyClientNetworkException
	 */
	public static void main(String[] args) throws JaxyClientNetworkException {

		// Thank you, open weather map.
		String url = "http://api.openweathermap.org/data/2.5/weather";

		// Create the main class of jaxy
		JaxyClient jaxy = new JaxyClient();

		jaxy.setEndpointUrl(url);

		// Set HTTP method you can choose from "GET" / "POST"
		jaxy.setHttpMethod("GET");

		// [**************WATCH_HERE************]
		// Instead of putting the request parameters to the tail,
		// you can add a request parameter by calling 'addParameter' method.
		jaxy.addParameter("lat", "40.7");
		jaxy.addParameter("lon", "-74.0");
		jaxy.addParameter("mode", "xml");

		// If you would like to use CDATA section, set true
		jaxy.setUseCDATA(true);

		// If you would like to use proxy server, comment out and fix here
		// jaxy.setProxy("proxy.example.com", 8080);

		// If you would like to use basic auth, comment out and fix here
		// jaxy.setUseBasicAuth(false);
		// jaxy.setBasicAuthUserNameAndPassword("username", "password");

		// Retrieve XML data from the server
		XmlNode node = jaxy.getXml();

		StringBuilder sb = new StringBuilder();

		// generate parser source code into StringBuilder
		// you can set the packageName of parser source code if you want.
		jaxy.gen().setPackageName("com.example.xml_sample_02").generateParserCodeTo(node, sb);

		// Output to the console
		System.out.println(sb.toString());

		/**
		 * And then, you can see the source code for this WEB API in the
		 * console. If you use Eclipse , just COPY all the source code shown in
		 * the console and PASTE it on the appropriate src folder in the package
		 * explorer.<br>
		 * And run 'SampleXmlParser.java' to get the WEB API data as a java
		 * object model.<br>
		 * <br>
		 * Finally, you can freely modify that code and generated model code as
		 * you like.<br>
		 * <br>
		 * Have a good day.<br>
		 * <br>
		 * by riversun.org<br>
		 */

	}

}
