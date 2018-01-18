package com.ooyala.android.item;

import java.util.Set;

/**
 * Stores the info and metatdata for the specified movie.
 */
public interface PlayableItem {
  public Set<Stream> getStreams();
}
