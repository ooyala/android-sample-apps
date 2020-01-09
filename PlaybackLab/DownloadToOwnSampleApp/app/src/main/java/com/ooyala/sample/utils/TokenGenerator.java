package com.ooyala.sample.utils;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.EmbeddedSecureURLGenerator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class TokenGenerator implements EmbedTokenGenerator {

    private PlayerSelectionOption playerSelectionOption;

    public TokenGenerator(PlayerSelectionOption playerSelectionOption) {
        this.playerSelectionOption = playerSelectionOption;
    }

    @Override
    public void getTokenForEmbedCodes(List<String> embedCodes, EmbedTokenGeneratorCallback callback) {
        String embedCodesString = "";
        for (String ec : embedCodes) {
            if (ec.equals("")) embedCodesString += ",";
            embedCodesString += ec;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("account_id", playerSelectionOption.getAccountId());


        String uri = "/sas/embed_token/" + playerSelectionOption.getPcode() + "/" + embedCodesString;

        EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(playerSelectionOption.getApiKey(),
            playerSelectionOption.getSecretKey());

        URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

        callback.setEmbedToken(tokenUrl.toString());
    }
}
