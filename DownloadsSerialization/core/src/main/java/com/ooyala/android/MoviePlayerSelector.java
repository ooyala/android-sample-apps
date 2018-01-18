package com.ooyala.android;

import com.ooyala.android.item.Stream;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.BaseStreamPlayer;
import com.ooyala.android.player.MoviePlayer;
import com.ooyala.android.player.PlayerFactory;
import com.ooyala.android.player.StreamPlayer;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Select the proper movie and stream player for a video/stream
 */
public class MoviePlayerSelector {
  private static final String TAG = "MoviePlayerSelector";

  private class PlayerFactoryComparator implements Comparator<PlayerFactory> {
    @Override
    public int compare(PlayerFactory f1, PlayerFactory f2) {
      return f1.priority() - f2.priority();
    }
  }

  private SortedSet<PlayerFactory> factories;

  public MoviePlayerSelector() {
    factories = new TreeSet<PlayerFactory>(new PlayerFactoryComparator());
  }

  public void registerPlayerFactory(PlayerFactory factory) {
    factories.add(factory);
  }

  /**
   * Create a movie player for a video
   * @param video the video to be played
   * @return a movie player
   * @throws OoyalaException
   */
  public MoviePlayer selectMoviePlayer(Video video) throws OoyalaException {
    MoviePlayer player = null;
    if (video == null) {
      throw new OoyalaException(OoyalaException.OoyalaErrorCode.ERROR_PLAYBACK_FAILED, "the video is null");
    }

    Iterator it = factories.iterator();
    while (it.hasNext()) {
      PlayerFactory pf = (PlayerFactory)it.next();
      for (String format : pf.getSupportedFormats()) {
        if (Stream.getStreamWithDeliveryType(video.getStreams(), format) != null) {
          try {
            return pf.createPlayer();
          } catch (OoyalaException e) {
            throw e;
          }
        }
      }
    }

    return new MoviePlayer();
  }

  /**
   * Get all supported formats for all registered PlayerFactories
   * @return the list of all supported formats for all registered PlayerFactories
   */
  public Set<String> getSupportedFormats() {
    Set<String> supportedFormats = new HashSet<>();

    Iterator it = factories.iterator();
    while (it.hasNext()) {
      PlayerFactory pf = (PlayerFactory)it.next();
      supportedFormats.addAll(pf.getSupportedFormats());
    }

    return supportedFormats;
  }
  /**
   * Create a movie player for a set of streams
   * @param streams the streams to be played
   * @return a stream player
   */
  public StreamPlayer selectStreamPlayer(Set<Stream> streams) {
    StreamPlayer player = null;
    Iterator it = factories.iterator();
    while (it.hasNext()) {
      PlayerFactory pf = (PlayerFactory)it.next();
      for (String format : pf.getSupportedFormats()) {
        if (Stream.getStreamWithDeliveryType(streams, format) != null) {
          player = pf.createStreamPlayer();
          if (player != null) {
            return player;
          }
        }
      }
    }
    return new BaseStreamPlayer();
  }
}
