package com.ooyala.android.item;

import com.ooyala.android.util.OrderedMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Channel extends ContentItem implements PaginatedParentItem {
  protected OrderedMap<String, Video> _videos = new OrderedMap<String, Video>();
  protected ChannelSet _parent = null;
  protected String _nextChildren = null;
  protected boolean _isFetchingMoreChildren = false;

  Channel() {}

  Channel(JSONObject data, String embedCode) {
    this(data, embedCode, null);
  }

  Channel(JSONObject data, String embedCode, ChannelSet parent) {
    _embedCode = embedCode;
    _parent = parent;
    update(data);
  }

  @Override
  public synchronized ReturnState update(JSONObject data) {
    switch (super.update(data)) {
      case STATE_FAIL:
        return ReturnState.STATE_FAIL;
      case STATE_UNMATCHED:
        //If we didn't match, try updating all other videos in channel
        for (Video video : _videos) {
          video.update(data);
        }
        return ReturnState.STATE_UNMATCHED;
      default:
        break;
    }

    try {
      //If authorization, check all of the children's authorization.
      JSONObject myData = data.getJSONObject(_embedCode);
      if (!myData.isNull(ContentItem.KEY_AUTHORIZED) && myData.getBoolean(ContentItem.KEY_AUTHORIZED)) {
        for (Video video : _videos) {
          video.update(data);
        }
        return ReturnState.STATE_MATCHED;
      }

      //If metadata, then update children if possible and break out.
      if (!myData.isNull(ContentItem.KEY_METADATA_BASE)) {
        for (Video video : _videos) {
          video.update(data);
        }
        return ReturnState.STATE_MATCHED;
      }

      //Handle content_tree
      if (!myData.isNull(ContentItem.KEY_CONTENT_TYPE)
          && !myData.getString(ContentItem.KEY_CONTENT_TYPE).equals(ContentItem.CONTENT_TYPE_CHANNEL)) {
        System.out.println("ERROR: Attempted to initialize Channel with content_type: "
            + myData.getString(ContentItem.KEY_CONTENT_TYPE));
        return ReturnState.STATE_FAIL;
      }

      _nextChildren = myData.isNull(ContentItem.KEY_NEXT_CHILDREN) ? null : myData
          .getString(ContentItem.KEY_NEXT_CHILDREN);

      if (myData.isNull(ContentItem.KEY_CHILDREN)) {
        if (_nextChildren == null) {
          System.out
              .println("ERROR: Attempted to initialize Channel with children == nil and next_children == nil: "
                  + _embedCode);
          return ReturnState.STATE_FAIL;
        }
        return ReturnState.STATE_MATCHED;
      }

      JSONArray children = myData.getJSONArray(ContentItem.KEY_CHILDREN);
      if (children.length() > 0) {
        for (int i = 0; i < children.length(); i++) {
          JSONObject child = children.getJSONObject(i);
          if (!child.isNull(ContentItem.KEY_CONTENT_TYPE)
              && child.getString(ContentItem.KEY_CONTENT_TYPE).equals(ContentItem.CONTENT_TYPE_VIDEO)) {
            HashMap<String, JSONObject> childMap = new HashMap<String, JSONObject>();
            String childEmbedCode = child.getString(ContentItem.KEY_EMBED_CODE);
            childMap.put(childEmbedCode, child);
            JSONObject childData = new JSONObject(childMap);
            Video existingChild = _videos.get(childEmbedCode);
            if (existingChild == null) {
              addVideo(new Video(childData, childEmbedCode, this));
            } else {
              existingChild.update(childData);
            }
          } else {
            System.out.println("ERROR: Invalid Video content_type: "
                + child.getString(ContentItem.KEY_CONTENT_TYPE));
          }
        }
      }
    } catch (JSONException exception) {
      System.out.println("JSONException: " + exception);
      return ReturnState.STATE_FAIL;
    }

    return ReturnState.STATE_MATCHED;
  }

  /**
   * Get the first Video for this Channel
   *
   * @return the first Video this Channel represents
   */
  @Override
  public Video firstVideo() {
    if (_videos == null || _videos.size() == 0) { return null; }
    return _videos.get(0);
  }

  /**
   * Get the last Video for this Channel
   *
   * @return the last Video this Channel represents
   */
  public Video lastVideo() {
    if (_videos == null || _videos.size() == 0) { return null; }
    return _videos.get(_videos.size() - 1);
  }

  /**
   * Get the next Video for this Channel
   *
   * @param currentItem the current Video
   * @return the next Video from Channel
   */
  public Video nextVideo(Video currentItem) {
    int index = _videos.indexForValue(currentItem);
    if (index < 0 || ++index >= _videos.size()) { return _parent == null ? null : _parent.nextVideo(this); }
    return _videos.get(index);
  }

  /**
   * Get the previous Video for this Channel
   *
   * @param currentItem the current Video
   * @return the previous Video from Channel
   */
  public Video previousVideo(Video currentItem) {
    int index = _videos.indexForValue(currentItem);
    if (index < 0 || --index < 0) { return _parent == null ? null : _parent.previousVideo(this); }
    return _videos.get(index);
  }

  protected void addVideo(Video video) {
    _videos.put(video.getEmbedCode(), video);
  }

  @Override
  public int childrenCount() {
    return _videos.size();
  }

  @Override
  public OrderedMap<String, Video> getAllAvailableChildren() {
    return _videos;
  }

  public OrderedMap<String, Video> getVideos() {
    return getAllAvailableChildren();
  }

  /**
   * The total duration (not including Ads) of this Channel
   *
   * @return an int with the total duration in seconds
   */
  @Override
  public int getDuration() {
    int totalDuration = 0;
    for (Video video : _videos) {
      totalDuration += video.getDuration();
    }
    return totalDuration;
  }

  @Override
  public boolean hasMoreChildren() {
    return _nextChildren != null;
  }

  @Override
  public String getNextChildren() {
    return _nextChildren;
  }

  @Override
  public List<String> embedCodesToAuthorize() {
    List<String> embedCodes = new ArrayList<String>();
    embedCodes.add(_embedCode);
    embedCodes.addAll(_videos.keySet());
    return embedCodes;
  }

  @Override
  public Video videoFromEmbedCode(String embedCode, Video currentItem) {
    return _videos.get(embedCode);
  }

  public ChannelSet getParent() {
    return _parent;
  }
}
