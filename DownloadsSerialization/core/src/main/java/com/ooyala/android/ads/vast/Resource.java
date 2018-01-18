package com.ooyala.android.ads.vast;

/**
 * A representation of VAST resource.
 */
public class Resource {
  public enum Type {
    None,
    Static,
    IFrame,
    HTML;

    @Override
    public String toString() {
      switch (this) {
        case None:
          return "None";
        case Static:
          return "Static";
        case IFrame:
          return "IFrame";
        case HTML:
          return "HTML";
        default:
          return "Undefined";
      }
    }
  };

  /** the resource type */
  private final Type type;
  /** the uri to the resource */
  private final String uri;
  /** the mimeType for static resource */
  private final String mimeType;

  Resource(Type type, String mimeType, String uri) {
    this.type = type;
    this.mimeType = mimeType;
    this.uri = uri;
  }

  /**
   * @return the type
   */
  public Type getType() {
    return type;
  }

  /**
   * @return the mimeType for static resource
   */
  public String getMimeType() {
    return mimeType;
  }

  /**
   * @return the uri;
   */
  public String getUri() {
    return uri;
  }
}
