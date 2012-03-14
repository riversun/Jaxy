package org.example.gen_json_parser_code;

import org.riversun.jaxy.JaxyClient;
import org.riversun.jaxy.JaxyClientNetworkException;
import org.riversun.jaxy.JsonNode;

/**
 * Sample Code for Jaxy<br>
 * <br>
 * Category:Generate SourceCode of JSON Parser by Accessing web services<br>
 * <br>
 * Automatically generate the XML/JSON parser source code,<br>
 * you get it surprisingly fast. <br>
 * <br>
 * <br>
 * [Sample Description]<br>
 * <br>
 * - Generate JSON parser source code for JSON based Web API no.01<br>
 * -- directly call the URL by GET method<br>
 * 
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class SampleForJSON_01_DirectCall {

	/**
	 * @param args
	 * @throws JaxyClientNetworkException
	 */
	public static void main(String[] args) throws JaxyClientNetworkException {

		// Thank you, open weather map.
		String url = "http://api.openweathermap.org/data/2.5/weather?lat=40.7&lon=-74.0&mode=json";

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

		// Retrieve JSON data from the server
		JsonNode node = jaxy.getJson();

		StringBuilder sb = new StringBuilder();

		// generate parser source code into StringBuilder
		// you can set the packageName of parser source code if you want.
		jaxy.gen().setPackageName("com.example.json_sample_01").generateParserCodeTo(node, sb);

		// Output to the console
		System.out.println(sb.toString());

		/**
		 * And then, you can see the source code for this WEB API in the
		 * console. If you use Eclipse , just COPY all the source code shown in
		 * the console and PASTE it on the appropriate src folder in the package
		 * explorer.<br>
		 * And run 'SampleJsonParser.java' to get the WEB API data as a java
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
