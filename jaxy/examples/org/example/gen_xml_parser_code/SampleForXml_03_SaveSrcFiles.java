package org.example.gen_xml_parser_code;

import java.io.File;

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
 * - Generate XML parser source code for XML base Web API no.03<br>
 * -- save source code to the files<br>
 * 
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class SampleForXml_03_SaveSrcFiles {

	/**
	 * @param args
	 * @throws JaxyClientNetworkException
	 */
	public static void main(String[] args) throws JaxyClientNetworkException {

		// Thank you, open weather map.
		String url = "http://api.openweathermap.org/data/2.5/weather?lat=40.7&lon=-74.0&mode=xml";

		// Create the main class of jaxy
		JaxyClient jaxy = new JaxyClient();

		jaxy.setEndpointUrl(url);

		// Set HTTP method you can choose from "GET" / "POST"
		jaxy.setHttpMethod("GET");

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

		// [**************WATCH_HERE************]
		// generate parser source code into the file
		jaxy.gen().setPackageName("com.example.xml_sample_03").generateParserCodeTo(node, new File("c:/temp"));

		// Output to the console
		System.out.println(sb.toString());

		/**
		 * And then, you can see the source code for this WEB API in the directy
		 * you specified.<br>
		 * Run 'SampleXmlParser.java' to get the WEB API data as a java object
		 * model.<br>
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
