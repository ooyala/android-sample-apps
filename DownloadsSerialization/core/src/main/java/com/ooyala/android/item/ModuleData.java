package com.ooyala.android.item;

import java.util.Map;

public class ModuleData {
  protected String _name;
  protected String _type;
  protected Map<String, String> _metadata;

  public ModuleData(String name, String type, Map<String, String> metadata) {
    _name = name;
    _type = type;
    _metadata = metadata;
  }

  public String getName() {
    return _name;
  }

  public String getType() {
    return _type;
  }

  public Map<String, String> getMetadata() {
    return _metadata;
  }
}
