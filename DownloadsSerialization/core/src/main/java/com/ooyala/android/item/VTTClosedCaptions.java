package com.ooyala.android.item;

import com.ooyala.android.util.DebugMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VTTClosedCaptions {
    private static final String TAG = "VTTClosedCaptions";

    public static class VTTCaption {
        private String languageCode;
        private String languageName;
        private String url;

        public String getLanguageCode() {
            return languageCode;
        }

        public String getLanguageName() {
            return languageName;
        }

        public String getUrl() {
            return url;
        }
    }

    static final String KEY_LANGUAGES = "languages";
    static final String KEY_CAPTIONS = "captions";
    static final String KEY_CAPTION_NAME = "name";
    static final String KEY_CAPTION_URL = "url";
    static final String KEY_DEFAULT_LANGUAGE = "default_language";

    protected Set<String> languages = new HashSet<>();
    protected List<VTTCaption> captions = new ArrayList<>();
    protected String defaultLanguage;

    private VTTClosedCaptions() {}

    public static VTTClosedCaptions build(JSONObject data) {
        if (null == data) return null;

        VTTClosedCaptions build = new VTTClosedCaptions();
        try {
            if (data.isNull(KEY_LANGUAGES)) {
                DebugMode.logD(TAG, "ERROR: Failed to get vtt languages!");
                return null;
            }

            JSONArray jsonLanguages = data.getJSONArray(KEY_LANGUAGES);
            for (int i = 0; i < jsonLanguages.length(); i++) {
                String language = jsonLanguages.getString(i);
                build.languages.add(language);

                if (data.isNull(KEY_CAPTIONS)) {
                    DebugMode.logD(TAG, "ERROR: Failed to get vtt captions!");
                    return null;
                }

                JSONObject caption = data.getJSONObject(KEY_CAPTIONS);
                VTTCaption vttCaption = new VTTCaption();

                if (null != caption && !caption.isNull(language)) {
                    caption = caption.getJSONObject(language);
                    vttCaption.languageCode = vttCaption.languageName = language;
                }

                if (null != caption && !caption.isNull(KEY_CAPTION_NAME)) {
                    vttCaption.languageName = caption.getString(KEY_CAPTION_NAME);
                }

                if (null != caption && !caption.isNull(KEY_CAPTION_URL)) {
                    vttCaption.url = caption.getString(KEY_CAPTION_URL);
                }
                build.captions.add(vttCaption);

                if (!data.isNull(KEY_DEFAULT_LANGUAGE)) {
                    build.defaultLanguage = data.getString(KEY_DEFAULT_LANGUAGE);
                }
            }

        } catch (JSONException e) {
            DebugMode.logD(TAG, "JSON Exception: " + e);
            return null;
        }

        return build;
    }

    public Set<String> getLanguages() {
        return languages;
    }

    public List<VTTCaption> getCaptions() {
        return captions;
    }

    private VTTCaption getCaptionForLanguageCode(String languageCode) {
        for (VTTCaption caption : captions) {
            if (caption.languageCode.equals(languageCode)) return caption;
        }

        return null;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    /**
     * Get the language full name given a language code
     * @param languageCode, for example 'en'
     * @return language full name, for example 'English'. Returns null if no match found.
     */
    public String getLanguageFullName(String languageCode) {
        for (String code : languages) {
            if (code.equals(languageCode)) {
                VTTCaption caption = getCaptionForLanguageCode(languageCode);
                if (null != caption) return caption.languageName;
            }
        }

        return null;
    }

    /**
     * Get the language code given a language full name
     * @param languageFullName, for example 'English'
     * @return language code, for example 'en'. Returns null if no match found.
     */
    public String getLanguageCode(String languageFullName) {
        for (VTTCaption caption : captions) {
            if (caption.getLanguageName().equals(languageFullName)) {
                return caption.languageCode;
            }
        }

        return null;
    }
}
