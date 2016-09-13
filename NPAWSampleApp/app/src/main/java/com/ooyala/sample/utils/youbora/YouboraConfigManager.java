package com.ooyala.sample.utils.youbora;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YouboraConfigManager maintains a configuration that can be passed into Youbora
 *
 * The youboraConfig is a multi-level Map that has many options for configuration.
 */
public class YouboraConfigManager {

    private static Map<String, Object> youboraConfig = null;

    private static final Map<String, Object> defaultYouboraConfig;

    static {
        // We use linkedhashmap only to preserve the order so it prints nicely in our UI, but this
        // could be a normal HashMap
        LinkedHashMap<String, Object> youboraConfig = new LinkedHashMap<>(20);

        youboraConfig.put("enableAnalytics", true);
        youboraConfig.put("parseHLS", false);
        youboraConfig.put("parseCDNNodeHost", false);
        youboraConfig.put("hashTitle", true);
        youboraConfig.put("httpSecure", false);
        youboraConfig.put("enableNiceBuffer", true);
        youboraConfig.put("enableNiceSeek", true);
        youboraConfig.put("accountCode", "nicetest");
        youboraConfig.put("service", "nqs.nice264.com");
        youboraConfig.put("username", "");
        youboraConfig.put("transactionCode", "");
        youboraConfig.put("isBalanced", "0");
        youboraConfig.put("isResumed", "0");
        youboraConfig.put("haltOnError", true);

        Map<String, Object> network = new HashMap<>(2);
        network.put("ip", "48.15.16.23");
        network.put("isp", "Verizon");
        youboraConfig.put("network", network);

        Map<String, Object> device = new HashMap<>(1);
        device.put("id", null);
        youboraConfig.put("device", device);

        Map<String, Object> media = new HashMap<>(5);
        media.put("isLive", false);
        media.put("resource", null);
        media.put("title", "Title");
        media.put("duration", null);
        media.put("cdn", null);
        youboraConfig.put("media", media);

        Map<String, Object> ads = new HashMap<>(6);
        ads.put("adsExpected", false);
        ads.put("resource", null);
        ads.put("campaign", "Ad Campaign 1");
        ads.put("title", null);
        ads.put("position", null);
        ads.put("duration", null);
        youboraConfig.put("ads", ads);

        Map<String, Object> properties = new HashMap<>(16);
        properties.put("contentId", null);
        properties.put("type", "video");
        properties.put("transaction_type", null);
        properties.put("genre", "Action");
        properties.put("language", null);
        properties.put("year", "2021");
        properties.put("cast", null);
        properties.put("director", null);
        properties.put("owner", null);
        properties.put("parental", null);
        properties.put("price", null);
        properties.put("rating", null);
        properties.put("audioType", null);
        properties.put("audioChannels", null);
        properties.put("device", null);
        properties.put("quality", null);
        youboraConfig.put("properties", properties);

        Map<String, Object> extraParams = new HashMap<>(10);
        extraParams.put("param1", "Param 1 value");
        extraParams.put("param2", "Param 2 value");
        extraParams.put("param3", "Param 3 value");
        extraParams.put("param4", "Param 4 value");
        extraParams.put("param5", "Param 5 value");
        extraParams.put("param6", "Param 6 value");
        extraParams.put("param7", "Param 7 value");
        extraParams.put("param8", "Param 8 value");
        extraParams.put("param9", "Param 9 value");
        extraParams.put("param10", "Param 10 value");
        youboraConfig.put("extraParams", extraParams);

        defaultYouboraConfig = Collections.unmodifiableMap(youboraConfig);
    }

    private YouboraConfigManager() {
    }

    public static void showConfig(Context context) {
        Intent i = new Intent(context, YouboraConfigActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void saveYouboraConfig(Context context, Map<String, Object> config){
        youboraConfig = config;
        // save to file
        File file = new File(context.getFilesDir(), "youbora_config_map");

        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(youboraConfig);
            outputStream.flush();
            outputStream.close();
            Log.i("YouboraConfigManager", "saved youbora config to file");
        } catch (Exception e) {
            Log.wtf("YouboraConfigManager", "exception when saving config to file: " + e.toString());
        }
    }

    public static Map<String, Object> getYouboraConfig(Context context) {
        if (youboraConfig == null) {
            // load from file
            File file = new File(context.getFilesDir(), "youbora_config_map");
            if (file.exists()) {
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                    youboraConfig = (Map) inputStream.readObject();
                    inputStream.close();
                    Log.i("YouboraConfigManager", "loaded youbora config from file");
                } catch (Exception e) {
                    Log.wtf("YouboraConfigManager", "exception when loading config to file: " + e.toString());
                }
            }
            // if not stored yet, reset
            if (youboraConfig == null) {
                resetPreferences(context);
            }
        }
        return youboraConfig;
    }

    public static Map<String, Object> resetPreferences(Context context) {
        youboraConfig = new LinkedHashMap<>(defaultYouboraConfig);
        saveYouboraConfig(context, youboraConfig);
        return youboraConfig;
    }

    public static Map<String, Object> getDefaultPreferences() {
        // default config
        return defaultYouboraConfig;
    }

}
