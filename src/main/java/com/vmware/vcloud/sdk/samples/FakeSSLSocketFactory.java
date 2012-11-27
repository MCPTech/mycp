/*
 * *******************************************************
 * Copyright VMware, Inc. 2010-2011.  All Rights Reserved.
 * *******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */
package com.vmware.vcloud.sdk.samples;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

/**
 * Helper class to accept the self-signed certificates.
 */

public class FakeSSLSocketFactory {

	private FakeSSLSocketFactory() {
	}

	public static SSLSocketFactory getInstance() throws KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException,
			KeyStoreException {
		return new SSLSocketFactory(new TrustStrategy() {
			public boolean isTrusted(final X509Certificate[] chain,
					final String authType) throws CertificateException {
				return true;
			}

		}, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	}
}
