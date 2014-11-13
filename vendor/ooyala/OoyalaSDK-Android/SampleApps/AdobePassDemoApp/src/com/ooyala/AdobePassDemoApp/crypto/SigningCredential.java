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

import android.util.Log;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Enumeration;

public class SigningCredential implements ICertificateInfo, IKeyInfo {
    private static final String LOG_TAG = "SigningCredential";

    protected KeyStore.PrivateKeyEntry mKeyEntry = null;

    public SigningCredential (InputStream inPKCSFile, String inPassword) {
        mKeyEntry = extractPrivateKeyEntry(inPKCSFile, inPassword);
    }

    private KeyStore.PrivateKeyEntry extractPrivateKeyEntry(InputStream inPKCSFile, String inPassword) {
        if (inPKCSFile == null)
            return null;

        try {
            KeyStore ks  = KeyStore.getInstance("PKCS12");
            Log.d(LOG_TAG, "KS provider : " + ks.getProvider());

            ks.load(inPKCSFile, inPassword.toCharArray());

            String keyAlias = null;
            Enumeration<String> aliases = ks.aliases();
            while(aliases.hasMoreElements()) {
                keyAlias = aliases.nextElement();
                if (ks.isKeyEntry(keyAlias))
                    break;
            }

            if (keyAlias != null) {
                KeyStore.PrivateKeyEntry keyEntry =
                    (KeyStore.PrivateKeyEntry) ks.getEntry
                        (keyAlias, new KeyStore.PasswordProtection(inPassword.toCharArray()));

                return keyEntry;
            }
        }
        catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }


        return null;
    }

    public PrivateKey getPrivateKey() {
        if (mKeyEntry == null)
            return null;

        return mKeyEntry.getPrivateKey();
    }

    public Certificate getCertificate() {
        if (mKeyEntry == null)
            return null;

        return mKeyEntry.getCertificate();
    }

    public Certificate[] getCertificateChain() {
        if (mKeyEntry == null)
            return null;

        return mKeyEntry.getCertificateChain();

    }

    public boolean isValid() {
        return mKeyEntry != null;
    }
}
