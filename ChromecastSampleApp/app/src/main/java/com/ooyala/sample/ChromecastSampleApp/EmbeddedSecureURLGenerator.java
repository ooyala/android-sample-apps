package com.ooyala.sample.ChromecastSampleApp;

import com.ooyala.android.SecureURLGenerator;
import com.ooyala.android.SignatureGenerator;
import com.ooyala.android.Utils;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EmbeddedSecureURLGenerator implements SecureURLGenerator {
  static final String KEY_API_KEY = "api_key";
  static final String KEY_DEVICE = "device";

  static final String METHOD_GET = "GET";
  static final String METHOD_PUT = "PUT";
  static final String METHOD_POST = "POST";

  static final int RESPONSE_LIFE_SECONDS = 5 * 60;

  static final String KEY_EXPIRES = "expires";  //embedded, Vast, PAPI
  static final String KEY_SIGNATURE = "signature"; // embedded, VAST

  static final String SEPARATOR_SIGNATURE_PARAMS = ""; //Simple concat, no separator

  private String _apiKey = null;
  private SignatureGenerator _signatureGenerator = null;

  public EmbeddedSecureURLGenerator(String apiKey, String secretKey) {
    _apiKey = apiKey;
    _signatureGenerator = new EmbeddedSignatureGenerator(secretKey);
  }

  public EmbeddedSecureURLGenerator(String apiKey, SignatureGenerator signatureGenerator) {
    _apiKey = apiKey;
    _signatureGenerator = signatureGenerator;
  }

  @Override
  public URL secureURL(String host, String uri, Map<String, String> params) {
    Map<String, String> allParams = null;
    if (params == null) {
      allParams = new HashMap<String, String>();
      allParams.put(KEY_API_KEY, _apiKey);
      long secondsSince1970 = (new Date()).getTime() / 1000;
      allParams.put(KEY_EXPIRES, Long.toString(secondsSince1970 + RESPONSE_LIFE_SECONDS));
      allParams.put(KEY_SIGNATURE,
          _signatureGenerator.sign(genStringToSign(uri, allParams, METHOD_GET)));
    } else {
      allParams = new HashMap<String, String>(params);
      if (!params.containsKey(KEY_SIGNATURE)) {
        if (!params.containsKey(KEY_API_KEY)) {
          allParams.put(KEY_API_KEY, _apiKey);
        }
        if (!params.containsKey(KEY_EXPIRES)) {
          long secondsSince1970 = (new Date()).getTime() / 1000;
          allParams.put(KEY_EXPIRES,
              Long.toString(secondsSince1970 + RESPONSE_LIFE_SECONDS));
        }
        allParams.put(KEY_SIGNATURE,
            _signatureGenerator.sign(genStringToSign(uri, allParams, METHOD_GET)));
      }
    }
    return Utils.makeURL(host, uri, allParams);
  }

  private String genStringToSign(String uri, Map<String, String> params, String method) {
    String paramsString = Utils.getParamsString(params, SEPARATOR_SIGNATURE_PARAMS, false);
    return method + uri + paramsString;
  }

}
