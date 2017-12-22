package com.ooyala.android.item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents video that is not tied to the Ooyala CMS e.g. a URL instead of an embed_code.
 */
public class UnbundledVideo {

  public static final String UNBUNDLED_EMBED_CODE = "UNBUNDLED";

  private final Set<Stream> streams;
  private final List<OoyalaManagedAdSpot> ads;

  /**
   * @param stream must be non-null.
   */
  public UnbundledVideo( Stream stream ) {
    Set<Stream> set = new HashSet<Stream>();
    set.add( stream );
    this.streams = set;
    this.ads = new ArrayList<OoyalaManagedAdSpot>();
  }

  /**
   * @param streams must be non-null.
   */
  public UnbundledVideo( Set<Stream> streams ) {
    this( streams, new ArrayList<OoyalaManagedAdSpot>() );
  }

  /**
   * @param streams must be non-null.
   * @param ads must be non-null.
   */
  public UnbundledVideo( Set<Stream> streams, List<OoyalaManagedAdSpot> ads ) {
    this.streams = streams;
    this.ads = ads;
  }

  public Set<Stream> getStreams() {
    return streams;
  }

  public List<OoyalaManagedAdSpot> getAds() {
    return ads;
  }
}
