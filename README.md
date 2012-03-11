# Overview

This project contains JSON and XML parser and parser generator.
You can generate a parser for JSON or XML with incredible speed.

It is licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

-----
# How to Use
## Generate JSON or XML Parser source code
1. Build jaxy jar package like jaxy-xxx.jar from pom.xml and put jar into your classpath.
2. Run sample code as follows.
3. The parser for JSON service source code is automatically generated.
4. Copy the parser code in the console and PASTE it into your IDE.
(If you use Eclipse, just past source code text on the source folder like 'src/main/java' or 'src')

```java
package org.example;

import org.riversun.jaxy.JaxyClient;
import org.riversun.jaxy.JaxyClientNetworkException;
import org.riversun.jaxy.JsonNode;

public class ParserGenSample {

	public static void main(String[] args) throws JaxyClientNetworkException {

		// Thank you, open weather map.
		String url = "http://api.openweathermap.org/data/2.5/weather?lat=40.7&lon=-74.0&mode=json";

		// Create the main class of jaxy
		JaxyClient jaxy = new JaxyClient();

		jaxy.setEndpointUrl(url);

		// Retrieve JSON data from the server
		JsonNode node = jaxy.getJson();

		StringBuilder sb = new StringBuilder();

		// generate parser source code into StringBuilder
		// you can set the packageName of parser source code if you want.
		jaxy.gen().setPackageName("com.example.sample").generateParserCodeTo(node, sb);

		// Output parser source codes to the console
		System.out.println(sb.toString());
	}
}
```

## Tips
In the sample of the above ,JAXY download JSON to access the Internet.(Yes,JAXY has REST client.)
If you want to generate parser from JSON text, see followings.

```java
StringBuilder sb = new StringBuilder();
JsonNode node = jaxy.getJson("[JSON_STRING]");
jaxy.gen().setPackageName("com.example.sample").generateParserCodeTo(node, sb);
```

## How to handle JSON/XML
- Here is auto generated parser source code from 'ParserGenSample' above.You can easily understand how it works.
If you want to access the value of "coord"/"lon", write 'node.child("json_parent").child("coord").child("lon").toString();'

- However, you do not need to write it manually yourself, it's jaxy's job.

- target JSON 
```JSON
{"coord":{"lon":-74,"lat":40.7},"sys":{"message":0.0173,"country":"US","sunrise":1430215067,"sunset":1430264944},"weather":[{"id":801,"main":"Clouds","description":"few clouds","icon":"02n"}],"base":"stations","main":{"temp":282.992,"temp_min":282.992,"temp_max":282.992,"pressure":1016.68,"sea_level":1020.29,"grnd_level":1016.68,"humidity":67},"wind":{"speed":4.42,"deg":333.504},"clouds":{"all":20},"dt":1430203575,"id":5128581,"name":"New York","cod":200}
```

- auto generated source code (only shows main class)
```java
package com.example.json_sample_01;

import org.riversun.jaxy.*;

public class AppMain_Json {
	public static void main(String[] args) throws JaxyClientNetworkException {
		JaxyClient jaxy = new JaxyClient();
		jaxy.setEndpointUrl("http://api.openweathermap.org/data/2.5/weather?lat=40.7&lon=-74.0&mode=json");
		jaxy.setHttpMethod("GET");
		jaxy.setUseCDATA(true);
		jaxy.setUseBasicAuth(false);
		jaxy.setBasicAuthUserNameAndPassword(null, null);
		JsonNode node = jaxy.getJson();

		Json_parent json_parent = new Json_parent();
		json_parent.dt = node.child("json_parent").child("dt").toString();
		json_parent.coord = new Coord();
		json_parent.coord.lon = node.child("json_parent").child("coord").child("lon").toString();
		json_parent.coord.lat = node.child("json_parent").child("coord").child("lat").toString();
		json_parent.weather = new Weather();
		json_parent.weather.icon = node.child("json_parent").child("weather").child("icon").toString();
		json_parent.weather.description = node.child("json_parent").child("weather").child("description").toString();
		json_parent.weather.main = node.child("json_parent").child("weather").child("main").toString();
		json_parent.weather.id = node.child("json_parent").child("weather").child("id").toString();
		json_parent.name = node.child("json_parent").child("name").toString();
		json_parent.cod = node.child("json_parent").child("cod").toString();
		json_parent.main = new Main();
		json_parent.main.temp = node.child("json_parent").child("main").child("temp").toString();
		json_parent.main.temp_min = node.child("json_parent").child("main").child("temp_min").toString();
		json_parent.main.grnd_level = node.child("json_parent").child("main").child("grnd_level").toString();
		json_parent.main.humidity = node.child("json_parent").child("main").child("humidity").toString();
		json_parent.main.pressure = node.child("json_parent").child("main").child("pressure").toString();
		json_parent.main.sea_level = node.child("json_parent").child("main").child("sea_level").toString();
		json_parent.main.temp_max = node.child("json_parent").child("main").child("temp_max").toString();
		json_parent.clouds = new Clouds();
		json_parent.clouds.all = node.child("json_parent").child("clouds").child("all").toString();
		json_parent.id = node.child("json_parent").child("id").toString();
		json_parent.sys = new Sys();
		json_parent.sys.country = node.child("json_parent").child("sys").child("country").toString();
		json_parent.sys.sunrise = node.child("json_parent").child("sys").child("sunrise").toString();
		json_parent.sys.sunset = node.child("json_parent").child("sys").child("sunset").toString();
		json_parent.sys.message = node.child("json_parent").child("sys").child("message").toString();
		json_parent.base = node.child("json_parent").child("base").toString();
		json_parent.wind = new Wind();
		json_parent.wind.deg = node.child("json_parent").child("wind").child("deg").toString();
		json_parent.wind.speed = node.child("json_parent").child("wind").child("speed").toString();

		// TODO
		// Write down your code here
		System.out.println("json_parent=" + json_parent);
	}// main()
}
```

##Downloads
You can download latest jaxy jar from here.

[jaxy-0.8.0.jar](http://riversun.org/downloads/jaxy-0.8.0.zip)

project moved from bitbucket:)
