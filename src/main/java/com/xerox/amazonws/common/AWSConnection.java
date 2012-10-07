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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * This class provides common code to the query and rest connection classes
 *
 * @author D. Kavanagh
 * @author developer@dotech.com
 */
public abstract class AWSConnection {
    private String awsAccessId;
    private String awsSecretKey;
    private boolean isSecure;
    private String server;
    private int port;
	private String resourcePrefix = "/";
	private int sigVersion = 2;
	protected Map <String, List<String>> headers;
	// used for caching last used Mac obj.. to save time 99.99% of the time
	// no longer static. was causing bottleneck for multi-core systems.
	private Map<String, Mac> macMap = new HashMap<String, Mac>();
	private String lastSecretKey;
	private Object macSync = new Object();

    /**
	 * Initializes the queue service with your AWS login information.
	 *
     * @param awsAccessId The your user key into AWS
     * @param awsSecretKey The secret string used to generate signatures for authentication.
     * @param isSecure True if the data should be encrypted on the wire on the way to or from SQS.
     * @param server Which host to connect to.
     * @param port Which port to use.
     */
    public AWSConnection(String awsAccessId, String awsSecretKey, boolean isSecure,
                             String server, int port)
    {
        this.awsAccessId = awsAccessId;
        this.awsSecretKey = awsSecretKey;
        this.isSecure = isSecure;
        this.server = server;
        this.port = port;
		this.headers = new TreeMap<String, List<String>>();
    }

	/**
	 * This method provides the URL for the queue service based on initialization.
	 *
	 * @return generated queue service url
	 */
	public URL getUrl() {
		try {
			return makeURL("");
		} catch (MalformedURLException ex) {
			return null;
		}
	}

	public String getAwsAccessKeyId() {
		return this.awsAccessId;
	}

	public String getSecretAccessKey() {
		return this.awsSecretKey;
	}

	public boolean isSecure() {
		return this.isSecure;
	}

	public String getServer() {
		return this.server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return this.port;
	}

	public String getResourcePrefix() {
		return this.resourcePrefix;
	}

	public void setResourcePrefix(String prefix) {
		this.resourcePrefix = prefix;
	}

	/**
	 * This method returns the signature version
	 *
	 * @return the version
	 */
	public int getSignatureVersion() {
		return sigVersion;
	}

	/**
	 * This method sets the signature version used to sign requests (0, 1 or 2).
	 * NOTE: This value defaults to 2, so passing 1 is the most likely use case.
	 *
	 * @param version signature version
	 */
	public void setSignatureVersion(int version) {
		if (version != 0 && version != 1 && version != 2) {
			throw new IllegalArgumentException("Only signature versions 0, 1 and 2 supported");
		}
		sigVersion = version;
	}

    /**
     * Create a new URL object for a given resource.
     * @param resource The resource name (bucketName + "/" + key).
     */
    protected URL makeURL(String resource) throws MalformedURLException {
        String protocol = this.isSecure ? "https" : "http";
        return new URL(protocol, this.server, this.port, resourcePrefix+resource);
    }

    /**
     * Calculate the HMAC/SHA1 on a string.
     * @param awsSecretKey passcode to sign it with
     * @param canonicalString data to sign
     * @return signature
     * @throws NoSuchAlgorithmException If the algorithm does not exist.  Unlikely
     * @throws InvalidKeyException If the key is invalid.
     */
    protected String encode(String awsSecretKey, String canonicalString,
                                boolean urlencode) {
		return encode(awsSecretKey, canonicalString, urlencode, getAlgorithm());
	}

    protected String encode(String awsSecretKey, String canonicalString,
                                boolean urlencode, String algorithm) {

        // The following HMAC/SHA1 code for the signature is taken from the
        // AWS Platform's implementation of RFC2104 (amazon.webservices.common.Signature)
        //
        // Acquire an HMAC/SHA1 from the raw key bytes.
        SecretKeySpec signingKey =
            new SecretKeySpec(awsSecretKey.getBytes(), algorithm);

        // Acquire the MAC instance and initialize with the signing key.
		Mac mac = null;
		synchronized (macSync) {
			mac = macMap.get(algorithm);
			if (mac == null || !lastSecretKey.equals(awsSecretKey)) {
				try {
					mac = Mac.getInstance(algorithm);
				} catch (NoSuchAlgorithmException e) {
					// should not happen
					throw new RuntimeException("Could not find sha1 algorithm", e);
				}
				try {
					mac.init(signingKey);
					macMap.put(algorithm, mac);
				} catch (InvalidKeyException e) {
					// also should not happen
					mac = null;
					throw new RuntimeException("Could not initialize the MAC algorithm", e);
				}
				lastSecretKey = awsSecretKey;
			}
		}

        // Compute the HMAC on the digest, and set it.
		byte [] signedBytes = null;
		synchronized (mac) {
			try {
				signedBytes = mac.doFinal(canonicalString.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				signedBytes = mac.doFinal(canonicalString.getBytes());
			}
		}
        String b64 = new String(Base64.encodeBase64(signedBytes));

        if (urlencode) {
            return urlencode(b64);
        } else {
            return b64;
        }
    }

	protected String getAlgorithm() {
		return (sigVersion==2)?"HmacSHA256":"HmacSHA1";
	}

    protected String urlencode(String unencoded) {
		String encoded = unencoded;
        try {
			if (sigVersion == 2) {
				encoded = URLEncoder.encode(unencoded, "UTF-8").replace("+", "%20").replace("*", "%2A").replaceAll("%7E", "~");
			}
			else {
				//System.out.println("unencoded = "+unencoded);
            	encoded = URLEncoder.encode(unencoded, "UTF-8");
			}
        } catch (UnsupportedEncodingException e) {
            // should never happen
        	//e.printStackTrace();
        	System.out.println(e.getMessage());
            throw new RuntimeException("Could not url encode to UTF-8", e);
        }
		return encoded;
    }
}
