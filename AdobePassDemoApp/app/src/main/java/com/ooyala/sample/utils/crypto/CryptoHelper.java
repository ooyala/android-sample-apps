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
package com.ooyala.sample.utils.crypto;

import android.util.Base64;
import android.util.Log;

public class CryptoHelper {
    private static final String LOG_TAG = "CryptoHelper";

    public static String base64Encode(byte[] inData) {
        if (inData == null)
            return null;

        try {
            return new String(Base64.encode(inData, Base64.DEFAULT));
        }
        catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;
    }

    public static byte[] base64Decode(String inData) {
        if (inData == null)
            return null;

        try {
            return Base64.decode(inData.getBytes(), Base64.DEFAULT);
        }
        catch(Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;

    }

    public static String getSignatureAlgorithm() {
        return "SHA256WithRSA";
    }

    public static String getSymmetricEncryptionAlgorithm() {
        return "AES/CBC/PKCS5Padding";
    }
}
