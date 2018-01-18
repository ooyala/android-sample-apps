package com.ooyala.android.ads.vast;

import com.ooyala.android.item.Stream;

import org.w3c.dom.Element;

class VASTStream extends Stream {
  /** if this stream is scalable */
  private boolean _scalable;
  /** if this stream must maintain the aspect ratio */
  private boolean _maintainAspectRatio;
  /** the vast delivery type of this stream */
  private String _vastDeliveryType;
  /** the apiFramework of this stream */
  private String _apiFramework;

  /**
   * Initialize a Stream using the specified VAST MediaFile XML (subclasses should override this)
   * @param data the Element containing the xml to use to initialize this Stream
   */
  VASTStream(Element data) {
    if (!data.getTagName().equals(Constants.ELEMENT_MEDIA_FILE)) { return; }
    this._vastDeliveryType = data.getAttribute(Constants.ATTRIBUTE_DELIVERY);
    this._apiFramework = data.getAttribute(Constants.ATTRIBUTE_API_FRAMEWORK);
    String scalableStr = data.getAttribute(Constants.ATTRIBUTE_SCALABLE);
    if (!VASTUtils.isNullOrEmpty(scalableStr)) {
      this._scalable = Boolean.getBoolean(scalableStr);
    }
    String maintainAspectRatioStr = data.getAttribute(Constants.ATTRIBUTE_MAINTAIN_ASPECT_RATIO);
    if (maintainAspectRatioStr != null) {
      this._maintainAspectRatio = Boolean.getBoolean(maintainAspectRatioStr);
    }
    String type = data.getAttribute(Constants.ATTRIBUTE_TYPE);
    if (type != null) {
      if (type.equals(Constants.MIME_TYPE_M3U8)) {
        this._deliveryType = Stream.DELIVERY_TYPE_HLS;
      }
      if (type.equals(Constants.MIME_TYPE_MP4)) {
        this._deliveryType = Stream.DELIVERY_TYPE_MP4;
      } else {
        this._deliveryType = type;
      }
    }
    String bitrate = data.getAttribute(Constants.ATTRIBUTE_BITRATE);
    if (!VASTUtils.isNullOrEmpty(bitrate)) {
      this._videoBitrate = Integer.parseInt(bitrate);
    }
    String theWidth = data.getAttribute(Constants.ATTRIBUTE_WIDTH);
    if (!VASTUtils.isNullOrEmpty(theWidth)) {
      this._width = Integer.parseInt(theWidth);
    }
    String theHeight = data.getAttribute(Constants.ATTRIBUTE_HEIGHT);
    if (!VASTUtils.isNullOrEmpty(theHeight)) {
      this._height = Integer.parseInt(theHeight);
    }
    this._urlFormat = "text";
    this._url = data.getTextContent();
  }

  /**
   * Check whether this VASTStream is scalable or not.
   * @return true if it is, false if not.
   */
  public boolean isScalable() {
    return _scalable;
  }

  /**
   * Check whether this VASTStream should maintian its aspect ratio when scaling or not.
   * @return true if yes, false if no.
   */
  public boolean isMaintainAspectRatio() {
    return _maintainAspectRatio;
  }

  /**
   * Get the delivery type (format) of this VASTStream.
   * @return a String denoting the delivery type.
   */
  public String getVastDeliveryType() {
    return _vastDeliveryType;
  }

  /**
   * Get the API Framework of this VASTStream.
   * @return the API Framework as a String.
   */
  public String getApiFramework() {
    return _apiFramework;
  }
}
