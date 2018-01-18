package com.ooyala.android.item;

import org.w3c.dom.Element;

public class Caption {
  protected double _begin = 0;
  protected double _end = 0;
  protected String _text = null;

  private static final String TAG = Caption.class.getName();

  /**
   * Initialize a Caption with the given parameters
   *
   * @param begin the begin timestamp
   * @param end the end timestamp
   * @param text the subtitle text
   *
   */
  public Caption(double begin, double end, String text) {
    _begin = begin;
    _end = end;
    _text = text;
  }

  Caption() {}

  /**
   * Initialize a Caption with the given parameters
   *
   * @param element the dom element that contains caption info.
   *
   */
  Caption(Element element) {
    if (!element.getTagName().equals(ClosedCaptions.ELEMENT_P)) { return; }

    String beginStr = element.getAttribute(ClosedCaptions.ATTRIBUTE_BEGIN);
    String durationStr = element.getAttribute(ClosedCaptions.ATTRIBUTE_DUR);
    String endStr = element.getAttribute(ClosedCaptions.ATTRIBUTE_END);

    if (ItemUtils.isNullOrEmpty(beginStr)) { return; }
    _begin = ItemUtils.secondsFromTimeString(beginStr);

    if (!ItemUtils.isNullOrEmpty(endStr)) {
      _end = ItemUtils.secondsFromTimeString(endStr);
    } else if (!ItemUtils.isNullOrEmpty(durationStr)) {
      _end = _begin + ItemUtils.secondsFromTimeString(durationStr);
    } else {
      return;
    }

    _text = "";
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      for (String t : element.getChildNodes().item(i).getTextContent().split("[\r\n]")) {
        _text += t.trim();
      }
      if (element.getChildNodes().item(i).getNodeName().equals("br")) _text += "\n";
    }
  }

  /**
   * Fetch the time to begin showing this Caption
   * @return the time to begin showing this Caption (seconds)
   */
  public double getBegin() {
    return _begin;
  }

  /**
   * Fetch the time to stop showing this Caption
   * @return the time to stop showing this Caption (seconds)
   */
  public double getEnd() {
    return _end;
  }

  /**
   * Fetch the text of this Caption
   * @return the text of this Caption
   */
  public String getText() {
    return _text;
  }

  /**
   * append caption
   * 
   * @param the
   *          caption to append
   */
  void append(final Caption caption) {
    _text += "\n";
    _text += caption.getText();
    _end = Math.max(_end, caption.getEnd());
  }

  @Override
  public boolean equals(Object o) { // generated via IntelliJ.
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Caption caption = (Caption) o;
    if (Double.compare(caption._begin, _begin) != 0) return false;
    if (Double.compare(caption._end, _end) != 0) return false;
    return !(_text != null ? !_text.equals(caption._text) : caption._text != null);
  }

  @Override
  public int hashCode() { // generated via IntelliJ.
    int result;
    long temp;
    temp = Double.doubleToLongBits(_begin);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_end);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (_text != null ? _text.hashCode() : 0);
    return result;
  }
}
