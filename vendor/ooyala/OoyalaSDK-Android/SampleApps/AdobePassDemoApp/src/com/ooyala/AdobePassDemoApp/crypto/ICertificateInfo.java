/**
 * **********************************************************************
 * Copyright 2011, Adobe Systems Incorporated
 * All rights reserved.
 *  
 * Permission to copy, use, modify and distribute this software is granted provided
 * this copyright notice appears in all copies. This software is provided "as is"
 * without express or implied warranty, and with no claim as to its suitability for
 * any purpose.
 * ************************************************************************
 */
package com.ooyala.AdobePassDemoApp.crypto;

import java.security.cert.Certificate;

public interface ICertificateInfo {
    public static final String KEYSTORE_PKCS12 = "PKCS12";

    public Certificate getCertificate();

    public Certificate[] getCertificateChain();

    public boolean isValid();
}
