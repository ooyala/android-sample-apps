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
import com.adobe.adobepass.accessenabler.api.AccessEnablerException;

import java.security.Signature;

public class SignatureGenerator {
    private static final String LOG_TAG = "SignatureGenerator";

	protected IKeyInfo mSignatureKey = null;

	public SignatureGenerator (SigningCredential inCreds) {
		mSignatureKey = inCreds;
	}

	public String generateSignature(String inData) throws AccessEnablerException {
		try {
			Signature rsaSigner = Signature.getInstance(CryptoHelper.getSignatureAlgorithm());

            rsaSigner.initSign(mSignatureKey.getPrivateKey());

			rsaSigner.update(inData.getBytes());
			byte[] signature = rsaSigner.sign();

			return CryptoHelper.base64Encode(signature);
		}
		catch(Exception e) {
			Log.e(LOG_TAG, e.toString());
            throw new AccessEnablerException();
		}
	}

}
