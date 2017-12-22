package com.ooyala.android.item;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.util.IMatchObjectPredicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the info and metatdata for the specified movie.
 */
public class Video extends ContentItem implements PlayableItem {
  private static final String TAG = Video.class.getName();
  protected List<OoyalaManagedAdSpot> _ads = new ArrayList<OoyalaManagedAdSpot>();
  protected Set<Stream> _streams = new HashSet<Stream>();
  protected Channel _parent = null;
  protected int _duration = 0;
  protected boolean _live = false;
  protected ClosedCaptions _closedCaptions = null;
  protected VTTClosedCaptions vttClosedCaptions = null;
  protected File folder; /* local folder for offline video */

  Video() {
  }

  /**
   * Convert an UnbundledVideo into an object our playback stack understands.
   * This is mostly only for internal use.
   *
   * @param unbundledVideo non-null.
   * @see UnbundledVideo
   */
  public Video(UnbundledVideo unbundledVideo) {
    _embedCode = UnbundledVideo.UNBUNDLED_EMBED_CODE;
    _authorized = true;
    _authCode = AuthCode.AUTHORIZED;
    _streams.addAll(unbundledVideo.getStreams());
    _ads.addAll(unbundledVideo.getAds());

    if (unbundledVideo instanceof OfflineVideo) {
      folder = ((OfflineVideo) unbundledVideo).getFolder();
    }
  }

  public Video(JSONObject data, String embedCode) {
    this(data, embedCode, null);
  }

  Video(JSONObject data, String embedCode, Channel parent) {
    _embedCode = embedCode;
    _parent = parent;
    update(data);
  }

  @Override
  public ReturnState update(JSONObject data) {
    switch (super.update(data)) {
      case STATE_FAIL:
        return ReturnState.STATE_FAIL;
      case STATE_UNMATCHED:
        return ReturnState.STATE_UNMATCHED;
      default:
        break;
    }
    try {
      JSONObject myData = data.getJSONObject(_embedCode);
      if (!myData.isNull(ContentItem.KEY_DURATION)) {
        _duration = myData.getInt(ContentItem.KEY_DURATION);
      }
      if (!myData.isNull(ContentItem.KEY_CONTENT_TYPE)) {
        _live = myData.getString(ContentItem.KEY_CONTENT_TYPE).equals(ContentItem.CONTENT_TYPE_LIVE_STREAM);
      }
      if (!myData.isNull(ContentItem.KEY_METADATA_BASE)) {
        JSONObject baseData = myData.getJSONObject(ContentItem.KEY_METADATA_BASE);
      }

      if (!myData.isNull(ContentItem.KEY_AUTHORIZED) && myData.getBoolean(ContentItem.KEY_AUTHORIZED)
          && !myData.isNull(ContentItem.KEY_STREAMS)) {
        JSONArray streams = myData.getJSONArray(ContentItem.KEY_STREAMS);
        if (streams.length() > 0) {
          _streams.clear();
          for (int i = 0; i < streams.length(); i++) {
            Stream stream = new Stream(streams.getJSONObject(i));
            _live = _live || stream.isLiveStream();  // live stream means the video is live
            if (stream != null) {
              _streams.add(stream);
            }
          }
        }
        return ReturnState.STATE_MATCHED;
      }

      if (!myData.isNull(ContentItem.KEY_ADS)) {
        JSONArray ads = myData.getJSONArray(ContentItem.KEY_ADS);
        if (ads.length() > 0) {
          _ads.clear();
          for (int i = 0; i < ads.length(); i++) {
            OoyalaManagedAdSpot ad = OoyalaManagedAdSpot.create(ads.getJSONObject(i), _duration);
            ad.setPriority(i);
            if (ad != null) {
              _ads.add(ad);
            } else {
              DebugMode.logE(this.getClass().getName(), "Unable to create ad.");
            }
          }
        }
      }

      if (!myData.isNull(ContentItem.KEY_CLOSED_CAPTIONS)) {
        _closedCaptions = null;
        JSONArray array = myData.getJSONArray(ContentItem.KEY_CLOSED_CAPTIONS);
        if (array.length() > 0) {
          /*
           * NOTE [jigish]: here we only select the first closed caption returned. according to rui it is
           * guaranteed by the ingestion API that only one closed caption file will exist per movie. we are
           * not doing this restriction server side in the content tree api because the DB does not have this
           * restriction in case we want to support having multiple closed caption files per movie. if that
           * ever happens, we will have to change this to support multiple closed captions.
           */
          JSONObject o = (JSONObject) array.get(0);
          _closedCaptions = new ClosedCaptions(o);
        }
      }

      if (!myData.isNull(KEY_CLOSED_CAPTIONS_VTT)) {
        vttClosedCaptions = VTTClosedCaptions.build(myData.getJSONObject(KEY_CLOSED_CAPTIONS_VTT));
      }

    } catch (JSONException exception) {
      DebugMode.logE(this.getClass().getName(), "JSONException: " + exception);
      return ReturnState.STATE_FAIL;
    }

    return ReturnState.STATE_MATCHED;
  }

  public List<OoyalaManagedAdSpot> getAds() {
    return _ads;
  }

  /**
   * Insert an AdSpot to play during this video
   *
   * @param ad the AdSpot to play during this video
   */
  public void insertAd(OoyalaManagedAdSpot ad) {
    DebugMode.assertCondition(_ads != null, TAG, "ads is null");
    _ads.add(ad);
    Collections.sort(_ads);
  }

  public void filterAds(IMatchObjectPredicate<OoyalaManagedAdSpot> keeper) {
    ArrayList<OoyalaManagedAdSpot> kept = new ArrayList<OoyalaManagedAdSpot>();
    for (OoyalaManagedAdSpot ad : _ads) {
      if (keeper.matches(ad)) {
        kept.add(ad);
      }
    }
    _ads = kept;
  }

  public Channel getParent() {
    return _parent;
  }

  @Override
  public int getDuration() {
    return _duration;
  }

  @Override
  public Video firstVideo() {
    return this;
  }

  public Video nextVideo() {
    return _parent == null ? null : _parent.nextVideo(this);
  }

  public Video previousVideo() {
    return _parent == null ? null : _parent.previousVideo(this);
  }

  /**
   * Returns whether this movie has ads
   *
   * @return isAd
   */
  public boolean hasAds() {
    return (_ads != null && _ads.size() > 0);
  }

  public boolean isLive() {
    return _live;
  }

  public ClosedCaptions getClosedCaptions() {
    return _closedCaptions;
  }

  public VTTClosedCaptions getVTTClosedCaptions() {
    return vttClosedCaptions;
  }

  public void setClosedCaptions(ClosedCaptions closedCaptions) {
    this._closedCaptions = closedCaptions;
  }

  public boolean hasClosedCaptions() {
    return _closedCaptions != null && _closedCaptions.getLanguages().size() > 0;
  }

  @Override
  public Video videoFromEmbedCode(String embedCode, Video currentItem) {
    if (_embedCode.equals(embedCode)) {
      return this;
    }
    return null;
  }

  @Override
  public Set<Stream> getStreams() {
    return _streams;
  }

  public Stream getStream() {
    return Stream.bestStream(_streams, false);
  }

  public boolean needsFetchInfo() {
    if (hasAds()) {
      return true;
    }
    if (hasClosedCaptions()) {
      return true;
    }
    return false;
  }

  public boolean hasTokenExpired() {
    long currentTime = System.currentTimeMillis();
    for (Stream stream : _streams) {
      if (stream.hasTokenExpired(currentTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the offline video folder.
   */
  public File getFolder() {
    return folder;
  }
}
