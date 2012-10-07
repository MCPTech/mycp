//
// typica - A client library for Amazon Web Services
// Copyright (C) 2007 Xerox Corporation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.xerox.amazonws.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import com.xerox.amazonws.typica.jaxb.Response;
import com.xerox.amazonws.typica.sqs2.jaxb.Error;
import com.xerox.amazonws.typica.sqs2.jaxb.ErrorResponse;

/**
 * This class provides an interface with the Amazon SQS service. It provides high level
 * methods for listing and creating message queues.
 *
 * @author D. Kavanagh
 * @author developer@dotech.com
 */
public class AWSQueryConnection extends AWSConnection {
	private static final Log log = LogFactory.getLog(AWSQueryConnection.class);
	private static String userAgent = "typica/";

	// this is the number of automatic retries
	private int maxRetries = 5;
	private HttpClient hc = null;
	private int maxConnections = 100;
	private String proxyHost = null;
	private int proxyPort;
	private String proxyUser;
	private String proxyPassword;
	private String proxyDomain;	// for ntlm authentication
	private int connectionManagerTimeout = 0;
	private int soTimeout = 0;
	private int connectionTimeout = 0;
	private TimeZone serverTimeZone = TimeZone.getTimeZone("GMT");

	static {
		String version = "?";
		try {
			Properties props = new Properties();
			InputStream verStream = ClassLoader.getSystemResourceAsStream("version.properties");
			try {
				props.load(verStream);
			} finally {
				verStream.close();

			}
			version = props.getProperty("version");
		} catch (Exception ex) { }
		userAgent = userAgent + version + " ("+ System.getProperty("os.arch") + "; " + System.getProperty("os.name") + ")";
	}

    /**
	 * Initializes the queue service with your AWS login information.
	 *
     * @param awsAccessId The your user key into AWS
     * @param awsSecretKey The secret string used to generate signatures for authentication.
     * @param isSecure True if the data should be encrypted on the wire on the way to or from SQS.
     * @param server Which host to connect to.
     * @param port Which port to use.
     */
    public AWSQueryConnection(String awsAccessId, String awsSecretKey, boolean isSecure,
                             String server, int port) {
		super(awsAccessId, awsSecretKey, isSecure, server, port);
    }

	/**
	 * This method returns the number of connections that can be open at once.
	 *
	 * @return the number of connections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * This method sets the number of connections that can be open at once.
	 *
	 * @param connections the number of connections
	 */
	public void setMaxConnections(int connections) {
		maxConnections = connections;
		hc = null;
	}

	/**
	 * This method returns the number of times to retry when a recoverable error occurs.
	 *
	 * @return the number of times to retry on recoverable error
	 */
	public int getMaxRetries() {
		return maxRetries;
	}

	/**
	 * This method sets the number of times to retry when a recoverable error occurs.
	 *
	 * @param retries the number of times to retry on recoverable error
	 */
	public void setMaxRetries(int retries) {
		maxRetries = retries;
	}

	/**
	 * This method sets the proxy host and port
	 *
	 * @param host the proxy host
	 * @param port the proxy port
	 */
	public void setProxyValues(String host, int port) {
		this.proxyHost = host;
		this.proxyPort = port;
		hc = null;
	}

	/**
	 * This method sets the proxy host, port, user and password (for authenticating proxies)
	 *
	 * @param host the proxy host
	 * @param port the proxy port
	 * @param user the proxy user
	 * @param password the proxy password
	 */
	public void setProxyValues(String host, int port, String user, String password) {
		this.proxyHost = host;
		this.proxyPort = port;
		this.proxyUser = user;
		this.proxyPassword = password;
		hc = null;
	}

	/**
	 * This method sets the proxy host, port, user, password and domain (for NTLM authentication)
	 *
	 * @param host the proxy host
	 * @param port the proxy port
	 * @param user the proxy user
	 * @param password the proxy password
	 * @param domain the proxy domain
	 */
	public void setProxyValues(String host, int port, String user, String password, String domain) {
		this.proxyHost = host;
		this.proxyPort = port;
		this.proxyUser = user;
		this.proxyPassword = password;
		this.proxyDomain = domain;
		hc = null;
	}

	/**
	 * This method indicates the system properties should be used for proxy settings. These
	 * properties are http.proxyHost, http.proxyPort, http.proxyUser and http.proxyPassword
	 */
	public void useSystemProxy() {
		this.proxyHost = System.getProperty("http.proxyHost");
		if (this.proxyHost != null && this.proxyHost.trim().equals("")) {
			proxyHost = null;
		}
		this.proxyPort = getPort();
		try {
			this.proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
		} catch (NumberFormatException ex) {
			/* use default */
		}
		this.proxyUser = System.getProperty("http.proxyUser");
		this.proxyPassword = System.getProperty("http.proxyPassword");
		this.proxyDomain = System.getProperty("http.proxyDomain");
		hc = null;
	}

 	/**
	 * @see org.apache.http.params.HttpClientParams.getConnectionManagerTimeout()
	 * @return connection manager timeout in milliseconds
	 */
	public int getConnectionManagerTimeout()
	{
		return connectionManagerTimeout;
	}

	/**
	 * @see org.apache.http.params.HttpClientParams.getConnectionManagerTimeout()
	 * @param connection manager timeout in milliseconds
	 */
	public void setConnectionManagerTimeout(int timeout)
	{
		connectionManagerTimeout = timeout;
		hc = null;
	}

	/**
	 * @see org.apache.http.params.HttpConnectionParams.getSoTimeout()
	 * @see org.apache.http.params.HttpMethodParams.getSoTimeout()
	 * @return socket timeout in milliseconds
	 */
	public int getSoTimeout()
	{
		return soTimeout;
	}

	/**
	 * @see org.apache.http.params.HttpConnectionParams.getSoTimeout()
	 * @see org.apache.http.params.HttpMethodParams.getSoTimeout()
	 * @param socket timeout in milliseconds
	 */
	public void setSoTimeout(int timeout)
	{
		soTimeout = timeout;
		hc = null;
	}

	/**
	 * @see org.apache.http.params.HttpConnectionParams.getConnectionTimeout()
	 * @return connection timeout in milliseconds
	 */
	public int getConnectionTimeout()
	{
		return connectionTimeout;
	}

	/**
	 * @see org.apache.http.params.HttpConnectionParams.getConnectionTimeout()
	 * @param connection timeout in milliseconds
	 */
	public void setConnectionTimeout(int timeout)
	{
		connectionTimeout = timeout;
		hc = null;
	}

	/**
	 * This method returns the map of headers for this connection
	 *
	 * @return map of headers (modifiable) 
	 */
	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	/**
	 * Returns timezone used when creating requests. This is helpful when talking to servers
	 * running in different timezones. Specifically when typica talks with a private Eucalyptus
	 * cluster.
	 *
	 * @return server timezone setting
	 */
	public TimeZone getServerTimeZone() {
		return serverTimeZone;
	}

	/**
	 * Allows setting non-standard server timezone.	(see getter comments)
	 *
	 * @param serverTimeZone new timezone of server
	 */
	public void setServerTimeZone(TimeZone serverTimeZone) {
		this.serverTimeZone = serverTimeZone;
	}

	protected HttpClient getHttpClient() {
		if (hc == null) {
			configureHttpClient();
		}
		return hc;
	}

	public void setHttpClient(HttpClient hc) {
		this.hc = hc;
	}

    /**
     * Make a http request and process the response. This method also performs automatic retries.
	 *
     * @param method The HTTP method to use (GET, POST, DELETE, etc)
     * @param action the name of the action for this query request
     * @param params map of request params
     * @param respType the class that represents the desired/expected return type
     */
	public <T> T makeRequest(HttpRequestBase method, String action, Map<String, String> params, Class<T> respType)
		throws HttpException, IOException, JAXBException, AWSException, SAXException {

		// add auth params, and protocol specific headers
        Map<String, String> qParams;
        if (params != null) {
            qParams = new HashMap<String, String>(params);
        } else {
            qParams = new HashMap<String, String>();
        }
        qParams.put("Action", action);
		qParams.put("AWSAccessKeyId", getAwsAccessKeyId());
		qParams.put("SignatureVersion", ""+getSignatureVersion());
		qParams.put("Timestamp", httpDate(serverTimeZone));
		if (getSignatureVersion() == 2) {
			qParams.put("SignatureMethod", getAlgorithm());
		}
        if (headers != null) {
            for (Iterator<String> i = headers.keySet().iterator(); i.hasNext(); ) {
                String key = i.next();
                for (Iterator<String> j = headers.get(key).iterator(); j.hasNext(); ) {
					qParams.put(key, j.next());
                }
            }
        }
		// sort params by key
		ArrayList<String> keys = new ArrayList<String>(qParams.keySet());
		if (getSignatureVersion() == 2) {
			Collections.sort(keys);
		}
		else {
			Collator stringCollator = Collator.getInstance();
			stringCollator.setStrength(Collator.PRIMARY);
			Collections.sort(keys, stringCollator);
		}

		// build param string
		StringBuilder resource = new StringBuilder();
		if (getSignatureVersion() == 0) {	// ensure Action, Timestamp come first!
			resource.append(qParams.get("Action"));
			resource.append(qParams.get("Timestamp"));
		}
		else if (getSignatureVersion() == 2) {
			resource.append(method.getMethod());
			resource.append("\n");
			resource.append(getServer().toLowerCase());
			resource.append("\n/");
			String reqURL = makeURL("").toString();
			// see if there is something after the host:port/ in the URL
			if (reqURL.lastIndexOf('/') < (reqURL.length()-1)) {
				// if so, put that here in the string to sign
				// make sure we slice and dice at the right '/'
				int idx = reqURL.lastIndexOf(':');
				resource.append(reqURL.substring(reqURL.indexOf('/', idx)+1));
			}
			resource.append("\n");
			boolean first = true;
			for (String key : keys) {
				if (!first) {
					resource.append("&");
				}
				else { first = false; }
				resource.append(key);
				resource.append("=");
				resource.append(urlencode(qParams.get(key)));
//				System.err.println("encoded params "+key+" :"+(urlencode(qParams.get(key))));
			}
		}
		else {
			for (String key : keys) {
				resource.append(key);
				resource.append(qParams.get(key));
			}
		}
		//System.err.println("String to sign :"+resource.toString());

		// calculate signature
       	String unencoded = encode(getSecretAccessKey(), resource.toString(), false);
       	String encoded = urlencode(unencoded);
		//System.err.println("sig = "+encoded);
		

		// build param string, encoding values and adding request signature
		resource = new StringBuilder();
		if (method.getMethod().equals("POST")) {
			ArrayList<BasicNameValuePair> postParams = new ArrayList<BasicNameValuePair>();
			for (String key : keys) {
				postParams.add(new BasicNameValuePair(key, qParams.get(key)));
			}
			postParams.add(new BasicNameValuePair("Signature", unencoded));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams, "UTF-8");
			method.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
			((HttpPost)method).setEntity(entity);
		}
		else {
			for (String key : keys) {
				resource.append("&");
				resource.append(key);
				resource.append("=");
				resource.append(urlencode(qParams.get(key)));
			}
			resource.setCharAt(0, '?');	// set first param delimeter
			resource.append("&Signature=");
			resource.append(encoded);
		}

		// finally, build request object
        URL url = makeURL(resource.toString());
		try {
			method.setURI(new java.net.URI(url.toString()));
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}
		method.setHeader(new BasicHeader("User-Agent", userAgent));
		if (getSignatureVersion() == 0) {
			method.setHeader(new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"));
		}
		Object response = null;
		boolean done = false;
		int retries = 0;
		boolean doRetry = false;
		AWSException error = null;
		HttpResponse httpResponse = null;
		do {
			int responseCode = 600;	// default to high value, so we don't think it is valid
			try {
				httpResponse = getHttpClient().execute(method);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				
			} catch (SocketException ex) {
				// these can generally be retried. Treat it like a 500 error
				doRetry = true;
				error = new AWSException(ex.getMessage(), ex);
			}
			// 100's are these are handled by httpclient
			if (responseCode < 300) {
				// 200's : parse normal response into requested object
				if (respType != null) {
					InputStream iStr = httpResponse.getEntity().getContent();
					//Charu - print out webservice response here 
					/*BufferedReader in = new BufferedReader(new InputStreamReader(iStr));
					String line = null;
					while((line = in.readLine()) != null) {
					  System.out.println(line);
					}*/
					
					response = JAXBuddy.deserializeXMLStream(respType, iStr);
					
				}
				done = true;
			}
			else if (responseCode < 400) {
				// 300's : what to do?
				throw new HttpException("redirect error : "+responseCode);
			}
			else if (responseCode < 500) {
				// 400's : parse client error message
				String body = getString(httpResponse.getEntity());
				System.out.println(" = "+body);
				throw createException(body, "Client error : ");
			}
			else if (responseCode < 600) {
				// 500's : retry...
				doRetry = true;
				String body = getString(httpResponse.getEntity());
				error = createException(body, "");
			}
			if (doRetry) {
				retries++;
				if (retries > maxRetries) {
					throw new HttpException("Number of retries exceeded : "+action, error);
				}
				doRetry = false;
				try {
					Thread.sleep((long)(Math.random() * (Math.pow(4, (retries-1))*100L)));
				} catch (InterruptedException ex) {}
			}
		} while (!done);
		
		//System.out.println(response.toString());
		
		return (T)response;
	}

	private void configureHttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, soTimeout);

		params.setParameter(AllClientPNames.MAX_TOTAL_CONNECTIONS, new Integer(maxConnections));
		params.setParameter(AllClientPNames.VIRTUAL_HOST, getServer());
//		params.setParameter(AllClientPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(maxConnections));

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

		ThreadSafeClientConnManager connMgr = new ThreadSafeClientConnManager(params, registry);
		//SingleClientConnManager connMgr = new SingleClientConnManager(params, registry);

		hc = new TypicaHttpClient(connMgr, params);
		//hc = new DefaultHttpClient(connMgr, params);
		
		if (proxyHost != null) {
			DefaultHttpClient defaultHC = (DefaultHttpClient) hc;
 			log.info("Proxy Host set to "+proxyHost+":"+proxyPort);

	        HttpHost proxy = new HttpHost(proxyHost, proxyPort); 

	        defaultHC.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

 			if (proxyUser != null && !proxyUser.trim().equals("")) {
				AuthScope scope = new AuthScope(proxyHost, proxyPort);
				Credentials creds = null;

 				if (proxyDomain != null) {
					creds = new NTCredentials(proxyUser, proxyPassword, proxyHost, proxyDomain);
 				}
 				else {
					creds =	new UsernamePasswordCredentials(proxyUser, proxyPassword);
 				}
				defaultHC.getCredentialsProvider().setCredentials(scope, creds);
			}
		}
	}

	protected String getString(HttpEntity entity) {
		if (entity == null) {
			return null;
		}
		else {
			try {
				return EntityUtils.toString(entity);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void close(HttpEntity entity) {
		if (entity != null) {
			try {
				entity.consumeContent();
			}
			catch (Exception ignore) {
				// ignored
			}
		}
	}

	protected void close(InputStream istream) {
		if (istream != null) {
			try {
				istream.close();
			}
			catch (Exception ignored) {
				// ignored
			}
		}
	}

	/**
	 * This method creates a detail packed exception to pass up
	 */
	private AWSException createException(String errorResponse, String msgPrefix) throws IOException, JAXBException, SAXException {
		String errorMsg;
		String requestId;
		List<AWSError> errors = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(errorResponse.getBytes());
		if (errorResponse.indexOf("<ErrorResponse") > -1) {
			try {
				// this comes from the SQS2 schema, and is the standard new response
				ErrorResponse resp = JAXBuddy.deserializeXMLStream(ErrorResponse.class, bais);
				List<Error> errs = resp.getErrors();
				errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
				requestId = resp.getRequestId();
				errors = new ArrayList<AWSError>();
				for (Error e : errs) {
					errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
											e.getCode(), e.getMessage()));
				}
			} catch (UnmarshalException ex) {
				try {
					// this comes from the DevpayLS schema, duplicated because of the different namespace
					bais = new ByteArrayInputStream(errorResponse.getBytes());
					com.xerox.amazonws.typica.jaxb.ErrorResponse resp = JAXBuddy.deserializeXMLStream(com.xerox.amazonws.typica.jaxb.ErrorResponse.class, bais);
					List<com.xerox.amazonws.typica.jaxb.Error> errs = resp.getErrors();
					errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
					requestId = resp.getRequestID();
					errors = new ArrayList<AWSError>();
					for (com.xerox.amazonws.typica.jaxb.Error e : errs) {
						errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
												e.getCode(), e.getMessage()));
					}
				} catch (UnmarshalException ex2) {
					try {
						// this comes from the Monitoring schema, duplicated because of the different namespace
						bais = new ByteArrayInputStream(errorResponse.getBytes());
						com.xerox.amazonws.typica.monitor.jaxb.ErrorResponse resp = JAXBuddy.deserializeXMLStream(com.xerox.amazonws.typica.monitor.jaxb.ErrorResponse.class, bais);
						List<com.xerox.amazonws.typica.monitor.jaxb.Error> errs = resp.getErrors();
						errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
						requestId = resp.getRequestId();
						errors = new ArrayList<AWSError>();
						for (com.xerox.amazonws.typica.monitor.jaxb.Error e : errs) {
							errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
													e.getCode(), e.getMessage()));
						}
					} catch (UnmarshalException ex3) {
						try {
							// this comes from the ELB schema, duplicated because of the different namespace
							bais = new ByteArrayInputStream(errorResponse.getBytes());
							com.xerox.amazonws.typica.loadbalance.jaxb.ErrorResponse resp = JAXBuddy.deserializeXMLStream(com.xerox.amazonws.typica.loadbalance.jaxb.ErrorResponse.class, bais);
							List<com.xerox.amazonws.typica.loadbalance.jaxb.Error> errs = resp.getErrors();
							errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
							requestId = resp.getRequestId();
							errors = new ArrayList<AWSError>();
							for (com.xerox.amazonws.typica.loadbalance.jaxb.Error e : errs) {
								errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
														e.getCode(), e.getMessage()));
							}
						} catch (UnmarshalException ex4) {
							try {
								// this comes from the scaling schema, duplicated because of the different namespace
								bais = new ByteArrayInputStream(errorResponse.getBytes());
								com.xerox.amazonws.typica.autoscale.jaxb.ErrorResponse resp = JAXBuddy.deserializeXMLStream(com.xerox.amazonws.typica.autoscale.jaxb.ErrorResponse.class, bais);
								List<com.xerox.amazonws.typica.autoscale.jaxb.Error> errs = resp.getErrors();
								errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
								requestId = resp.getRequestId();
								errors = new ArrayList<AWSError>();
								for (com.xerox.amazonws.typica.autoscale.jaxb.Error e : errs) {
									errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
															e.getCode(), e.getMessage()));
								}
							} catch (UnmarshalException ex5) {
								try {
									// this comes from the notification schema, duplicated because of the different namespace
									bais = new ByteArrayInputStream(errorResponse.getBytes());
									com.xerox.amazonws.typica.sns.jaxb.ErrorResponse resp = JAXBuddy.deserializeXMLStream(com.xerox.amazonws.typica.sns.jaxb.ErrorResponse.class, bais);
									List<com.xerox.amazonws.typica.sns.jaxb.Error> errs = resp.getErrors();
									errorMsg = "("+errs.get(0).getCode()+") "+errs.get(0).getMessage();
									requestId = resp.getRequestId();
									errors = new ArrayList<AWSError>();
									for (com.xerox.amazonws.typica.sns.jaxb.Error e : errs) {
										errors.add(new AWSError(AWSError.ErrorType.getTypeFromString(e.getType()),
																e.getCode(), e.getMessage()));
									}
								} catch (UnmarshalException ex6) {
									errorMsg = "Couldn't parse error response!";
									requestId = "???";
									log.error(errorMsg, ex6);
									log.info("response = "+errorResponse);
								}
							}
						}
					}
				}
			}
		}
		else {
			// this clause to parse Eucalyptus errors, until they get with the program!
			if (errorResponse.indexOf("<soapenv:Reason") > -1) {
				int idx = errorResponse.indexOf("Text xml:lang=\"en-US\">");
				errorMsg = errorResponse.substring(idx+22);	// this number tied to string in line above
				int idx2 = errorMsg.indexOf("<");
				errorMsg = errorMsg.substring(0, idx2);
				requestId = "NA";
				errors = new ArrayList<AWSError>();
				errors.add(new AWSError(AWSError.ErrorType.SENDER, "unknown", errorMsg));
			}
			else {
				try {
					Response resp = JAXBuddy.deserializeXMLStream(Response.class, bais);
					String errorCode = resp.getErrors().getError().getCode();
					errorMsg = resp.getErrors().getError().getMessage();
					requestId = resp.getRequestID();
					if (errorCode != null && !errorCode.trim().equals("")) {
						errors = new ArrayList<AWSError>();
						errors.add(new AWSError(AWSError.ErrorType.SENDER, errorCode, errorMsg));
					}
				} catch (SAXException ex) {
					errorMsg = "Couldn't parse error response!";
					requestId = "???";
					log.error(errorMsg, ex);
					log.info("response = "+errorResponse);
				} catch (UnmarshalException ex2) {
					errorMsg = "Couldn't parse error response!";
					requestId = "???";
					log.error(errorMsg, ex2);
					log.info("response = "+errorResponse);
				}
			}
		}
		return new AWSException(msgPrefix + errorMsg, requestId, errors);
	}

    /**
     * Generate an rfc822 date for use in the Date HTTP header.
     */
    private static String httpDate(TimeZone serverTimeZone) {
        //final String DateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        final String DateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat format = new SimpleDateFormat( DateFormat, Locale.US );
        format.setTimeZone(serverTimeZone);
        return format.format( new Date() );
	}

	protected String httpDate(Calendar date) {
		final String DateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat format = new SimpleDateFormat(DateFormat, Locale.US);
		format.setTimeZone(serverTimeZone);
		return format.format(date.getTime());
	}
}
