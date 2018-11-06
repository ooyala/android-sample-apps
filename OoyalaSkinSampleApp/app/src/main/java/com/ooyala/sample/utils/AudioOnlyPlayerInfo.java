package com.ooyala.sample.utils;

import java.util.HashMap;
import java.util.Map;

public class AudioOnlyPlayerInfo extends BaseCustomPlayerInfo {

  private final Map<String, String> ADDITIONAL_PARAMS;

  {
    ADDITIONAL_PARAMS = new HashMap<>();
    ADDITIONAL_PARAMS.put("player_type", "audio_only");
  }

  @Override
  public Map<String, String> getAdditionalParams() {
    return ADDITIONAL_PARAMS;
  }
}
