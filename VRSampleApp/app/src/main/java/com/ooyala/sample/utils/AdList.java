package com.ooyala.sample.utils;


import java.util.Arrays;
import java.util.List;

public class AdList {

  public static List<VideoData> videoList = Arrays.asList(
      new VideoData(VideoItemType.SECTION, "No ADS", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[No ADS] 2D Video No ADS", false, "VzZHd2YzE612sYDbk2UyuurOXrLgsVx9"),
      new VideoData(VideoItemType.VIDEO, "[No ADS] Video 360", false, "ZwdTE5YzE69c3U3cXy2CCzfnCkzMMqUP"),
      new VideoData(VideoItemType.VIDEO, "[No ADS] Video 360 long description", false, "syazl0YzE6sGVc6vA5sPUZK6RWp5aplu"),
      new VideoData(VideoItemType.VIDEO, "[No ADS] Video 360 watermark", false, "tvaTluYzE6gfZg5nhqlqxPV7YbEukBCj"),
      new VideoData(VideoItemType.VIDEO, "[No ADS] Video 360 High quality", false, "N5N2c2ZDE6DCsvCvSrgPHE5zerJ0oJYe", "15OGsyOs7wTVvirAtV0F611vIpKH"),

      new VideoData(VideoItemType.SECTION, "IMA ADS Video 360", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 360] Pre-roll", true, "Izbm1rYzE6Hr19rd1wK74qeraVA7xSLx"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 360] Mid-roll skippable", true, "Q0dWFtYzE6RFRGuFP0WzuPE5dvBzJ8_R"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 360] Post-roll", true, "N4bmNtYzE63wuc3QizkmmkA0HDZou83_"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 360] Pre-Mid-Post skippable", true, "Z4Y2UyZDE6bi5ZhPJE860W8GcE3z6WkE"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 360] Podded 3Pre-3Mid-3Post", true, "UyZGUyZDE6ht1KgaWXgoWhw2P2Kp8_Nb"),

      new VideoData(VideoItemType.SECTION, "Ooyala ADS Video 360", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[Ooyala ADS 360] Pre-roll", false, "NibG1rYzE6B7m54kL380ZXwEsUUy4bIe"),

      new VideoData(VideoItemType.SECTION, "VAST ADS Video 360", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 360] Pre-roll", false, "o5bm1rYzE6Iv00WKa3Wd67QUuulRGtTb"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 360] Mid-roll skippable", false, "F3bW1rYzE6gd0C5kJ8ETeB-0yeawf2Cd"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 360] Post roll", false, "h4dGFtYzE6pjfgMHC5ioFOiaq5BywAL6"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 360] Pre-Mid-Post", false, "g4YmNsYzE6zLuWf3eCAtcOdi0--i081X"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 360] Podded 2Pre-2Mid-2Post", false, "ZpZGxoYzE6oThg4Hapb2gwUC-HBDJy8T"),

      new VideoData(VideoItemType.SECTION, "IMA ADS Video 2D", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 2D] Pre-roll", true, "tqd282ZDE6bX2evvxui_wI5HezO6oLZ1"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 2D] Mid-roll skippable", true, "dyeW82ZDE6cFE2fW4sWqaOSmRHVWj8yp"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 2D] Post-roll skippable", true, "tod282ZDE6wCNaEqZYcSaYPHVNX4_mSX"),
      new VideoData(VideoItemType.VIDEO, "[IMA ADS 2D] Pre-Mid-Post, skippable", true, "tsd282ZDE6ntnpHkMcWP4MnuBQXR2PAw"),

      new VideoData(VideoItemType.SECTION, "Ooyala ADS Video 2D", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[Ooyala ADS 2D] Pre-roll", false, "dmNXA2ZDE636pnqwBZiB0askbt9JHYwQ"),

      new VideoData(VideoItemType.SECTION, "VAST ADS Video 2D", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 2D] Pre-roll skippable", false, "ltNHA2ZDE6utNFZtF3-nH9-3otMkG_Eq"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 2D] Mid-roll skippable", false, "wwNnA2ZDE6SIhAkXssgiR6svHXAGBq-P"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 2D] Post-roll skippable", false, "lxNHA2ZDE6uRDirY6LZsJRRGh2aBC6ZR"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 2D] Pre-Mid-Post skippable", false, "lvNHA2ZDE6Zc-4j2o0-CmHAj6lJOKnSN"),
      new VideoData(VideoItemType.VIDEO, "[VAST ADS 2D] Podded 2Pre-2Mid-2Post skippable", false, "lzNHA2ZDE6_oDV04VtY7sg4Pr7jGErfG"),

      new VideoData(VideoItemType.SECTION, "Discovery", false, null, null, null),
      new VideoData(VideoItemType.VIDEO, "[Discovery] Video 360", false, "hwZnUxZDE6CyfgVaLAOw4HlCzVUZGPnB"),
      new VideoData(VideoItemType.VIDEO, "[Discovery] Video 2D", false, "N2eG42ZDE6Zq5lNIARlre7Lp0wzglOvA")
  );
}
