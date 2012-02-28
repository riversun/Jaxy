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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.w3c.dom.Document;

/**
 * This class can give you <br>
 * Easy access to JSON and XML by using JaxyNode(JsonNode and XmlNode)<br>
 * <br>
 * - JSON to JsonNode<br>
 * - XML to XmlNode<br>
 * <br>
 * 
 * Tom Misawa <riversun.org@gmail.com>
 *
 *
 */
public class JaxyClient {

	public static final String HTTP_METHOD_GET_METHOD = "GET";
	public static final String HTTP_METHOD_POST_METHOD = "POST";

	public static final String HTTPS_ALG_SSL = "SSL";
	public static final String HTTPS_ALG_TLS = "TLS";

	private static final String SUFFIX_HTTP = "http://";
	private static final String SUFFIX_HTTPS = "https://";

	private final JaxyConvXmlFromText xmlNodeTransform = new JaxyConvXmlFromText();
	private final JaxyConvJsonFromText mJaxyString2JsonNode = new JaxyConvJsonFromText();

	private String mProxyHost;
	private String mProxyPort;
	private String mEncoding;

	private String mEndpointUrl;

	private boolean mIsUseCDATA = false;

	private Map<String, String[]> mParameterMap;

	private Set<String> mNeedNotEncodeParameterNameSet;

	private String mHttpRequestMethod = HTTP_METHOD_GET_METHOD;
	private String mLatestRequestUrl;

	private int mTimeoutMillis = 5 * 1000;

	private boolean mIsUseBasicAuth = false;
	private String mBasicAuthUserName;
	private String mBasicAuthUsePassword;

	private String mSslAlgorithm = HTTPS_ALG_TLS;

	private Map<String, List<String>> mRequestPropertyMap = null;

	public JaxyClient setSSLAlgorithm(String sslAlgorithm) {
		this.mSslAlgorithm = sslAlgorithm;
		return JaxyClient.this;
	}

	// Contains parameter names even if the number of the element is one ,and
	// force to add the subscript.
	public Set<String> mForceIndexedParameterNameSet = new HashSet<String>();

	public JaxyClient() {

		this.mEncoding = "UTF-8";

		this.mProxyPort = "0";
		this.mParameterMap = new LinkedHashMap();
		this.mNeedNotEncodeParameterNameSet = new HashSet();

	}

	/**
	 * retrieve json formatted data from network and get as JsonNode
	 * 
	 * @return JsonNode
	 * @throws JaxyClientNetworkException
	 */
	public JsonNode getJson() throws JaxyClientNetworkException {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_NET);

		String jsonText;
		try {
			jsonText = this.getTextFormUrl(new URL(this.mEndpointUrl));
		} catch (MalformedURLException e) {
			throw new JaxyClientNetworkException(e);
		}

		JsonNode jsonNode = mJaxyString2JsonNode.toJsonNode(jsonText);

		return jsonNode;
	}

	/**
	 * Get JsonNode from json formatted file
	 * 
	 * @param jsonText
	 * @return
	 */
	public JsonNode getJson(String jsonText) {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_TEXT);

		trg.text = jsonText;

		return mJaxyString2JsonNode.toJsonNode(jsonText);

	}

	/**
	 * retrieve xml formatted data from network and get as XmlNode
	 * 
	 * @return XmlNode
	 * @throws JaxyClientNetworkException
	 */
	public XmlNode getXml() throws JaxyClientNetworkException {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_NET);

		XmlNode xmlNode = null;

		if (this.mEndpointUrl != null) {

			String xmlText;
			try {
				xmlText = this.getTextFormUrl(new URL(this.mEndpointUrl));
			} catch (MalformedURLException e) {
				throw new JaxyClientNetworkException(e);
			}

			xmlNode = xmlNodeTransform.toXmlNode(xmlText);
		}

		return xmlNode;
	}

	/**
	 * Get XmlNode from w3c document
	 * 
	 * @param doc
	 * @return
	 */
	@Deprecated
	public XmlNode getXml(Document doc) {
		return xmlNodeTransform.toXmlNode(doc);

	}

	/**
	 * Get XmlNode from xml formatted file
	 * 
	 * @param file
	 * @return
	 */
	public XmlNode getXml(File file) {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_FILE);
		trg.file = file;

		if (File.separator.equals("\\")) {
			// String info = xxx.replaceAll("\\\\", "\\\\\\\\");
		}

		return xmlNodeTransform.toXmlNode(file);

	}

	/**
	 * Retrieve xml formatted data from url and get as XmlNode
	 * 
	 * @param url
	 * @return
	 * @throws JaxyClientNetworkException
	 */
	public XmlNode getXml(URL url) throws JaxyClientNetworkException {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_NET);
		trg.endpointUrl = url.toString();

		String xmlText = getTextFormUrl(url);

		return xmlNodeTransform.toXmlNode(xmlText);

	}

	/**
	 * Transform Xml formatted data into XmlNode
	 * 
	 * @param url
	 * @return
	 * @throws JaxyClientNetworkException
	 */
	public XmlNode getXml(String xmlFormattedText) {

		SourceGenTrigger trg = populateCurrentStatToTrigger(SourceGenTriggerMethod.FROM_TEXT);
		trg.text = xmlFormattedText;

		return xmlNodeTransform.toXmlNode(xmlFormattedText);
	}

	/**
	 * Get as plain text from network
	 * 
	 * @return
	 * @throws JaxyClientNetworkException
	 */
	public String getText() throws JaxyClientNetworkException {
		try {
			return getTextFormUrl(new URL(mEndpointUrl));
		} catch (MalformedURLException e) {
			throw new JaxyClientNetworkException(e);
		}
	}

	private SourceGenTrigger populateCurrentStatToTrigger(SourceGenTriggerMethod triggerMethod) {
		// setup method trigger
		mSourceGenTrigger.clear();
		mSourceGenTrigger.triggerMethod = triggerMethod;
		mSourceGenTrigger.httpMethod = this.mHttpRequestMethod;
		mSourceGenTrigger.endpointUrl = this.mEndpointUrl;
		mSourceGenTrigger.useCDATA = this.mIsUseCDATA;
		mSourceGenTrigger.parameterMap = this.mParameterMap;

		// proxy
		mSourceGenTrigger.useProxy = this.mProxyHost != null;
		if (mSourceGenTrigger.useProxy) {
			mSourceGenTrigger.proxyHost = this.mProxyHost;
			mSourceGenTrigger.proxyPort = Integer.parseInt(this.mProxyPort);
		}

		// basic auth
		mSourceGenTrigger.useBasicAuth = this.mIsUseBasicAuth;
		mSourceGenTrigger.basicAuthUser = this.mBasicAuthUserName;
		mSourceGenTrigger.basicAuthPassword = this.mBasicAuthUsePassword;

		//
		mSourceGenTrigger.file = null;
		mSourceGenTrigger.text = null;
		return mSourceGenTrigger;
	}

	/**
	 * Set userName and password for BasicAuth<br>
	 * <br>
	 * This method is available after calling {@link #setUseBasicAuth(boolean)}
	 * with 'true'<br>
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public JaxyClient setBasicAuthUserNameAndPassword(String userName, String password) {
		this.mBasicAuthUserName = userName;
		this.mBasicAuthUsePassword = password;
		return JaxyClient.this;
	}

	/**
	 * Set use http basic auth
	 * 
	 * @param useBasicAuth
	 * @return
	 */
	public JaxyClient setUseBasicAuth(boolean useBasicAuth) {
		this.mIsUseBasicAuth = useBasicAuth;
		return JaxyClient.this;
	}

	public JaxyClient setTimeoutMillis(int millis) {
		mTimeoutMillis = millis;
		return JaxyClient.this;
	}

	/**
	 * Force add the array index into the selected parameterName
	 * 
	 * @param parameterName
	 */
	public void setForceIndexedParameterName(String parameterName) {
		if (!mForceIndexedParameterNameSet.contains(parameterName)) {
			mForceIndexedParameterNameSet.add(parameterName);
		}
	}

	protected Map getParameterMap() {
		return mParameterMap;
	}

	public void setHttpMethod(String requestMethod) {
		this.mHttpRequestMethod = requestMethod;
	}

	public void addParameter(Map<String, String[]> parameterMap) {
		this.mParameterMap = parameterMap;
	}

	public void addParameter(String parameterName, String parameterValue) {
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	public void addParameter(String parameterName, String parameterValue, boolean isNeedUrlEncodeParameterValue) {
		if (isNeedUrlEncodeParameterValue == false) {
			this.mNeedNotEncodeParameterNameSet.add(parameterName);
		}
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	public void addParameter(String parameterName, String[] parameterValue) {
		this.mParameterMap.put(parameterName, parameterValue);
	}

	public void addParameter(String parameterName, int intParameterValue) {
		String parameterValue = String.valueOf(intParameterValue);
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	public void addParameter(String parameterName, long longParameterValue) {
		String parameterValue = String.valueOf(longParameterValue);
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	public void addParameter(String parameterName, float floatParameterValue) {
		String parameterValue = String.valueOf(floatParameterValue);
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	public void addParameter(String parameterName, double doubleParameterValue) {
		String parameterValue = String.valueOf(doubleParameterValue);
		this.mParameterMap.put(parameterName, new String[] { parameterValue });
	}

	/**
	 * retrieve text from net
	 * 
	 * @return
	 * @throws JaxyClientNetworkException
	 */
	public String getTextFormUrl(URL url) throws JaxyClientNetworkException {

		String endpointUrl = url.toString();

		String responseText = "";

		try {

			Set<String> keyset = mParameterMap.keySet();

			Iterator<String> iterator = keyset.iterator();

			StringBuffer queryParamSB = new StringBuffer();

			while (iterator.hasNext()) {

				String objKey = iterator.next();
				String[] objValues = (String[]) mParameterMap.get(objKey);

				// Request only registered key

				if (objValues != null && objValues.length > 0) {
					for (int i = 0; i < objValues.length; i++) {
						queryParamSB.append(objKey);
						queryParamSB.append("=");

						if (mNeedNotEncodeParameterNameSet.contains(objKey)) {
							queryParamSB.append(objValues[i]);
						} else {
							queryParamSB.append(URLEncoder.encode(objValues[i], mEncoding));
						}
						queryParamSB.append("&");
					}
				}

			}
			String queryParamString = queryParamSB.toString();

			String requestUrl = null;
			if (queryParamString.length() > 0) {

				// remove the '&' at the tail
				queryParamString = queryParamString.substring(0, queryParamString.length() - 1);

				requestUrl = endpointUrl + "?" + queryParamString;
			} else {

				requestUrl = endpointUrl;
			}

			if (requestUrl.startsWith(SUFFIX_HTTP)) {
				responseText = this.doHttpRequest(requestUrl);
			} else if (requestUrl.startsWith(SUFFIX_HTTPS)) {
				responseText = this.doHttpsRequest(requestUrl);
			}
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return responseText;
	}

	private String doHttpsRequest(String requestUrl) throws JaxyClientNetworkException {

		initHTTPS();

		String proxyHost = this.mProxyHost;
		String proxyPort = this.mProxyPort;
		String encoding = this.mEncoding;

		if (this.mIsUseBasicAuth) {
			Authenticator.setDefault(new BasicAuthAuthenticator(this.mBasicAuthUserName, this.mBasicAuthUsePassword));
		}
		int iProxyPort = Integer.parseInt(proxyPort);

		HttpsURLConnection httpURLConnection = null;

		BufferedReader br = null;
		InputStream is = null;
		InputStreamReader isr = null;

		StringBuffer sb = new StringBuffer("");
		try {

			if (proxyHost == null) {

				String queryStringForPOSTMethod = "";
				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					String[] requestUrlSplit = requestUrl.split("\\?");
					if (requestUrlSplit.length == 2) {
						String urlPart = requestUrlSplit[0];
						requestUrl = urlPart;
						queryStringForPOSTMethod = requestUrlSplit[1];

					}
				}

				JaxyClientURLWrapper conn = new JaxyClientURLWrapper(requestUrl);
				httpURLConnection = (HttpsURLConnection) conn.openConnectionNoProxy();
				httpURLConnection.setConnectTimeout(mTimeoutMillis);
				httpURLConnection.setReadTimeout(mTimeoutMillis);
				setHeaderPropertiesToHttpUrlConnection(httpURLConnection);

				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					httpURLConnection.setDoOutput(true);
					PrintWriter writer;

					writer = new PrintWriter(httpURLConnection.getOutputStream());
					writer.print(queryStringForPOSTMethod);
					writer.close();
				} else {
					httpURLConnection.setRequestMethod(HTTP_METHOD_GET_METHOD);
				}

			} else {

				String queryStringForPOSTMethod = "";
				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					String[] requestUrlSplit = requestUrl.split("\\?");
					if (requestUrlSplit.length == 2) {
						String urlPart = requestUrlSplit[0];
						requestUrl = urlPart;
						queryStringForPOSTMethod = requestUrlSplit[1];

					}
				}

				JaxyClientURLWrapper conn = new JaxyClientURLWrapper(requestUrl);
				httpURLConnection = (HttpsURLConnection) conn.openConnectionWithProxy(proxyHost, iProxyPort);
				httpURLConnection.setConnectTimeout(mTimeoutMillis);
				httpURLConnection.setReadTimeout(mTimeoutMillis);
				setHeaderPropertiesToHttpUrlConnection(httpURLConnection);

				// Request header addition processing
				if (mRequestPropertyMap != null) {

					Set<String> headerNamesSet = mRequestPropertyMap.keySet();
					for (String headerName : headerNamesSet) {
						List<String> headerValueList = mRequestPropertyMap.get(headerName);

						// Allow multi header values with the same name.
						for (String headervalue : headerValueList) {

							// add header name and value
							httpURLConnection.addRequestProperty(headerName, headervalue);

						}
					}
				}

				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					httpURLConnection.setDoOutput(true);
					PrintWriter writer;

					writer = new PrintWriter(httpURLConnection.getOutputStream());
					writer.print(queryStringForPOSTMethod);
					writer.close();

				} else {
					httpURLConnection.setRequestMethod(HTTP_METHOD_GET_METHOD);
				}

			}

			is = httpURLConnection.getInputStream();
			isr = new InputStreamReader(is, encoding);
			br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (ProtocolException e) {
			throw new JaxyClientNetworkException(e);
		} catch (UnsupportedEncodingException e) {
			throw new JaxyClientNetworkException(e);
		} catch (java.net.UnknownHostException e) {
			throw new JaxyClientNetworkException(e);
		} catch (IOException e) {

			InputStreamReader isr2 = null;
			InputStream errorStream = null;
			BufferedReader br2 = null;

			try {

				errorStream = httpURLConnection.getErrorStream();
				isr2 = new InputStreamReader(errorStream, encoding);

				// Also get error stream
				br2 = new BufferedReader(isr2);

				String line = null;
				while ((line = br2.readLine()) != null) {
					sb.append(line);
				}

				String serverSideErrorMessage = e.getMessage();

			} catch (UnsupportedEncodingException e1) {

				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if (br2 != null) {
					try {
						br2.close();
					} catch (IOException e2) {
						e.printStackTrace();
					}
				}
				if (isr2 != null) {
					try {
						isr2.close();
					} catch (IOException e2) {
						e.printStackTrace();
					}
				}
				if (errorStream != null) {
					try {
						errorStream.close();
					} catch (IOException e2) {
						e.printStackTrace();
					}
				}
			}
			throw new JaxyClientNetworkException(e);
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			httpURLConnection.disconnect();

		}

		return sb.toString();
	}

	/**
	 * Add header properties into the HttpUrlConnection
	 * 
	 * @param httpURLConnection
	 */
	private void setHeaderPropertiesToHttpUrlConnection(HttpURLConnection httpURLConnection) {
		// Request header addition processing
		if (mRequestPropertyMap != null) {

			Set<String> headerNamesSet = mRequestPropertyMap.keySet();
			for (String headerName : headerNamesSet) {
				List<String> headerValueList = mRequestPropertyMap.get(headerName);

				// In the same header name, and a loop to allow multiple values
				for (String headervalue : headerValueList) {

					httpURLConnection.addRequestProperty(headerName, headervalue);

				}
			}
		}

	}

	private void initHTTPS() {

		if (false) {
			// for debug
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession sslSession) {

					return true;

				}
			});
		}

		KeyManager[] km = null;

		TrustManager[] tm = { new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				for (X509Certificate cert : chain) {

					Principal issuerDN = cert.getIssuerDN();
					// TODO to handle cert properly
				}
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		} };

		SSLContext sslcontext;

		try {
			sslcontext = SSLContext.getInstance(mSslAlgorithm);

			sslcontext.init(km, tm, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	public String doHttpRequest() throws JaxyClientNetworkException {

		String responseText = "";
		String queryParamString = getQueryParamString();

		String requestUrl = null;
		if (queryParamString.length() > 0) {

			// remove '&' from tail
			queryParamString = queryParamString.substring(0, queryParamString.length() - 1);

			// add request query to the endpoint url.
			requestUrl = mEndpointUrl + "?" + queryParamString;

		} else {
			// Here it means there's no parameter.
			requestUrl = mEndpointUrl;
		}

		mLatestRequestUrl = requestUrl;

		// HTTPとHTTPSをみわける
		if (requestUrl.startsWith(SUFFIX_HTTP)) {
			responseText = this.doHttpRequest(requestUrl);

		} else if (requestUrl.startsWith(SUFFIX_HTTPS)) {
			responseText = this.doHttpsRequest(requestUrl);
		}

		return responseText;
	}

	private String doHttpRequest(String requestUrl) throws JaxyClientNetworkException {

		String proxyHost = this.mProxyHost;
		String proxyPort = this.mProxyPort;
		String encoding = this.mEncoding;

		if (this.mIsUseBasicAuth) {
			Authenticator.setDefault(new BasicAuthAuthenticator(this.mBasicAuthUserName, this.mBasicAuthUsePassword));
		}

		int iProxyPort = Integer.parseInt(proxyPort);

		HttpURLConnection httpURLConnection = null;

		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		StringBuffer sb = new StringBuffer("");

		try {

			// create url object

			if (proxyHost == null) {

				String queryStringForPOSTMethod = "";

				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					String[] requestUrlSplit = requestUrl.split("\\?");
					if (requestUrlSplit.length == 2) {
						String urlPart = requestUrlSplit[0];
						requestUrl = urlPart;
						queryStringForPOSTMethod = requestUrlSplit[1];

					}
				}

				JaxyClientURLWrapper conn = new JaxyClientURLWrapper(requestUrl);

				httpURLConnection = (HttpURLConnection) conn.openConnectionNoProxy();

				httpURLConnection.setConnectTimeout(mTimeoutMillis);
				httpURLConnection.setReadTimeout(mTimeoutMillis);
				setHeaderPropertiesToHttpUrlConnection(httpURLConnection);

				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					httpURLConnection.setDoOutput(true);
					PrintWriter writer;

					writer = new PrintWriter(httpURLConnection.getOutputStream());
					writer.print(queryStringForPOSTMethod);
					writer.close();
				} else {
					httpURLConnection.setRequestMethod(HTTP_METHOD_GET_METHOD);
				}

			} else {

				String queryStringForPOSTMethod = "";
				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					String[] requestUrlSplit = requestUrl.split("\\?");
					if (requestUrlSplit.length == 2) {
						String urlPart = requestUrlSplit[0];
						requestUrl = urlPart;
						queryStringForPOSTMethod = requestUrlSplit[1];

					}
				}

				JaxyClientURLWrapper conn = new JaxyClientURLWrapper(requestUrl);

				httpURLConnection = (HttpURLConnection) conn.openConnectionWithProxy(proxyHost, iProxyPort);
				httpURLConnection.setConnectTimeout(mTimeoutMillis);
				httpURLConnection.setReadTimeout(mTimeoutMillis);
				setHeaderPropertiesToHttpUrlConnection(httpURLConnection);

				if (this.mHttpRequestMethod.equals(HTTP_METHOD_POST_METHOD)) {
					httpURLConnection.setDoOutput(true);
					PrintWriter writer;

					writer = new PrintWriter(httpURLConnection.getOutputStream());
					writer.print(queryStringForPOSTMethod);
					writer.close();
				} else {
					httpURLConnection.setRequestMethod(HTTP_METHOD_GET_METHOD);
				}

			}

			is = httpURLConnection.getInputStream();
			isr = new InputStreamReader(is, encoding);
			br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (ProtocolException e) {
			throw new JaxyClientNetworkException(e);
		} catch (UnsupportedEncodingException e) {

			throw new JaxyClientNetworkException(e);
		} catch (IOException serverSideException) {
			BufferedReader br2 = null;
			InputStreamReader isr2 = null;
			InputStream errorStream = null;
			try {
				errorStream = httpURLConnection.getErrorStream();
				if (errorStream != null) {

					isr2 = new InputStreamReader(errorStream, encoding);
					br2 = new BufferedReader(isr2);

					String line = null;
					while ((line = br2.readLine()) != null) {
						sb.append(line);
					}

					String serverSideErrorMessage = serverSideException.getMessage();
				}
			} catch (UnsupportedEncodingException e1) {

				e1.printStackTrace();
			} catch (IOException e1) {

				e1.printStackTrace();
			} finally {
				if (br2 != null) {
					try {
						br2.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (isr2 != null) {
					try {
						isr2.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (errorStream != null) {
					try {
						errorStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			throw new JaxyClientNetworkException(serverSideException);
		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			httpURLConnection.disconnect();

		}

		return sb.toString();
	}

	/**
	 * returns raw RequestUrl of latest request
	 * 
	 * @return
	 */
	public String getLatestRequestRawUrl() {
		String requestUrl = null;
		try {

			// TODO fix older approach:-) like this
			Set keyset = mParameterMap.keySet();

			Iterator iterator = keyset.iterator();

			StringBuffer queryParamSB = new StringBuffer();

			while (iterator.hasNext()) {

				String objKey = (String) iterator.next();
				String[] objValues = (String[]) mParameterMap.get(objKey);

				// Only request key names that have been registered
				if (objValues != null && objValues.length > 0) {

					for (int i = 0; i < objValues.length; i++) {
						queryParamSB.append(objKey);
						queryParamSB.append("=");

						if (mNeedNotEncodeParameterNameSet.contains(objKey)) {
							queryParamSB.append(objValues[i]);
						} else {
							queryParamSB.append(URLEncoder.encode(objValues[i], mEncoding));
						}
						queryParamSB.append("&");
					}
				}

			}
			String queryParamString = queryParamSB.toString();
			if (queryParamString.length() > 0) {
				// Remove '&' on the tail
				queryParamString = queryParamString.substring(0, queryParamString.length() - 1);

				requestUrl = mEndpointUrl + "?" + queryParamString;
			} else {

				requestUrl = mEndpointUrl;
			}

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		return requestUrl;
	}

	public String getEncoding() {
		return mEncoding;
	}

	public void setEncoding(String encoding) {
		this.mEncoding = encoding;
	}

	public String getProxyHost() {
		return mProxyHost;
	}

	public void setProxy(String proxyHost, int proxyPort) {
		this.mProxyHost = proxyHost;
		this.mProxyPort = String.valueOf(proxyPort);
	}

	public String getEndpointUrl() {
		return mEndpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.mEndpointUrl = endpointUrl;
	}

	public void setUseCDATA(boolean useCDATA) {
		mIsUseCDATA = useCDATA;
		xmlNodeTransform.setUseCDATA(useCDATA);
	}

	public String getQueryParamString() {

		Set keyset = mParameterMap.keySet();

		Iterator iterator = keyset.iterator();

		StringBuffer queryParamSB = new StringBuffer();

		while (iterator.hasNext()) {

			String srcObjKey = (String) iterator.next();
			String[] objValues = (String[]) mParameterMap.get(srcObjKey);

			// Only request the registered key name
			boolean isThisObjKeyNeedforceIndexed = mForceIndexedParameterNameSet.contains(srcObjKey);

			if (objValues != null && objValues.length > 0) {
				for (int idx = 0; idx < objValues.length; idx++) {

					// If the one the number of string brute value to the key,
					// the key as it is

					// If the number of the value related to the key is 1,the
					// key's value keeps as it is.
					String objKey = srcObjKey;

					// If you want to add the subscript as 'param [0]'
					// to the key that is only one element (there's no key with
					// the
					// same name) key,plese set 1.
					// Only when the number of elements is 2 or more, please set
					// 2
					final int NEED_BRACKET_MINIMUM_INDEX = 2;

					if (objValues.length >= NEED_BRACKET_MINIMUM_INDEX || isThisObjKeyNeedforceIndexed) {

						// If the size of the array is two or more,Add a
						// subscript to the key.
						objKey = srcObjKey + "[" + idx + "]";
					}

					try {
						String encodedKey = URLEncoder.encode(objKey, mEncoding);
						queryParamSB.append(encodedKey);
					} catch (UnsupportedEncodingException e1) {
						e1.printStackTrace();
					}

					queryParamSB.append("=");

					if (mNeedNotEncodeParameterNameSet.contains(srcObjKey)) {
						queryParamSB.append(objValues[idx]);
					} else {

						try {

							String encodedValue = URLEncoder.encode(objValues[idx], mEncoding);
							queryParamSB.append(encodedValue);

						} catch (UnsupportedEncodingException e) {

							e.printStackTrace();
						}
					}
					queryParamSB.append("&");
				}
			}

		}

		String queryParamString = queryParamSB.toString();
		return queryParamString;
	}

	public String getLatestRequestUrl() {
		return mLatestRequestUrl;
	}

	/**
	 * Basic auth handler
	 * 
	 * Tom Misawa <riversun.org@gmail.com>
	 *
	 */
	static final class BasicAuthAuthenticator extends Authenticator {

		private String mUsername;
		private String mPassword;

		public BasicAuthAuthenticator() {

		}

		public BasicAuthAuthenticator(String username, String password) {
			super();
			this.mUsername = username;
			this.mPassword = password;
		}

		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(mUsername, mPassword.toCharArray());
		}

		public String getAuthRealm() {
			return super.getRequestingPrompt();
		}

		public String getUsername() {
			return mUsername;
		}

		public void setUsername(String username) {
			this.mUsername = username;
		}

		public String getPassword() {
			return mPassword;
		}

		public void setPassword(String password) {
			this.mPassword = password;
		}

	}

	/**
	 * URL wrapper
	 * 
	 * Tom Misawa <riversun.org@gmail.com>
	 *
	 */
	static final class JaxyClientURLWrapper {

		private URL _url;

		public JaxyClientURLWrapper(String requsetUrl) {
			try {
				_url = new URL(requsetUrl);
			} catch (MalformedURLException e) {

				e.printStackTrace();
			}
		}

		public URLConnection openConnectionNoProxy() {
			URLConnection conn = null;
			try {
				conn = _url.openConnection();
			} catch (IOException e) {

				e.printStackTrace();
			}
			return conn;
		}

		public URLConnection openConnectionWithProxy(String proxyHost, int proxyPort) {
			URLConnection conn = null;
			try {
				InetSocketAddress addr = new InetSocketAddress(proxyHost, proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);

				conn = _url.openConnection(proxy);
			} catch (IOException e) {

				e.printStackTrace();
			}
			return conn;
		}
	}

	// parser source cod generation methods below
	private SourceCodeGenerator mSourceCodeGenerator;

	public SourceCodeGenerator gen() {
		if (mSourceCodeGenerator == null) {
			mSourceCodeGenerator = new SourceCodeGenerator();
		}
		return mSourceCodeGenerator;
	}

	public class SourceCodeGenerator {

		private String mSrcCodePackageName = "";
		private String mSrcCodeClassNamePrefix = "";

		public SourceCodeGenerator setPackageName(String srcCodePackageName) {
			this.mSrcCodePackageName = srcCodePackageName;
			return SourceCodeGenerator.this;
		}

		public SourceCodeGenerator setClassNamePrefix(String srcCodeClassNamePrefix) {
			this.mSrcCodeClassNamePrefix = srcCodeClassNamePrefix;
			return SourceCodeGenerator.this;
		}

		public void generateParserCodeTo(JsonNode node, File dir) {
			getJsonGen(node).generateParserCodeTo(dir);
		}

		public void generateParserCodeToConsole(JsonNode node) {
			getJsonGen(node).generateParserCodeToConsole();
		}

		public void generateParserCodeTo(JsonNode node, StringBuilder sb) {
			getJsonGen(node).generateParserCodeTo(sb);
		}

		private JaxySrcGen4JsonNode getJsonGen(JsonNode node) {
			if (node == null) {
				throw new RuntimeException("Node is null.");
			}
			JaxySrcGen4JsonNode jsonGen = new JaxySrcGen4JsonNode(node);
			jsonGen.setSourceGenTrigger(mSourceGenTrigger);
			jsonGen.setSrcCodeClassNamePrefix(mSrcCodeClassNamePrefix);
			jsonGen.setSrcCodePackageName(mSrcCodePackageName);
			return jsonGen;
		}

		public void generateParserCodeTo(XmlNode node, File dir) {
			getXmlGen(node).generateParserCodeTo(dir);
		}

		public void generateParserCodeToConsole(XmlNode node) {
			getXmlGen(node).generateParserCodeToConsole();
		}

		public void generateParserCodeTo(XmlNode node, StringBuilder sb) {
			getXmlGen(node).generateParserCodeTo(sb);
		}

		private JaxySrcGen4XmlNode getXmlGen(XmlNode node) {
			if (node == null) {
				throw new RuntimeException("Node is null.");
			}
			JaxySrcGen4XmlNode xmlGen = new JaxySrcGen4XmlNode(node);
			xmlGen.setSourceGenTrigger(mSourceGenTrigger);
			xmlGen.setSrcCodeClassNamePrefix(mSrcCodeClassNamePrefix);
			xmlGen.setSrcCodePackageName(mSrcCodePackageName);
			return xmlGen;
		}
	}

	// Trigger bellow

	enum SourceGenTriggerMethod {
		FROM_NET, //
		FROM_FILE, //
		FROM_TEXT, //
		UNKNOWN, //
	}

	private final SourceGenTrigger mSourceGenTrigger = new SourceGenTrigger();

	class SourceGenTrigger {

		public SourceGenTriggerMethod triggerMethod = SourceGenTriggerMethod.UNKNOWN;

		// From Net
		public String httpMethod;
		public String endpointUrl = "";
		public boolean useProxy = false;
		public String proxyHost;
		public int proxyPort;
		public boolean useCDATA = false;
		public boolean useBasicAuth = false;
		public String basicAuthUser;
		public String basicAuthPassword;
		public Map<String, String[]> parameterMap;

		// From file
		public File file;

		// From text
		public String text;

		public void clear() {

			triggerMethod = SourceGenTriggerMethod.UNKNOWN;

			httpMethod = null;
			endpointUrl = "";
			parameterMap = null;

			// proxy
			useProxy = false;
			proxyHost = null;
			proxyPort = 0;

			// basic auth
			basicAuthUser = null;
			basicAuthPassword = null;

			useCDATA = false;

			//
			file = null;
			text = null;
		}

	}

}
