package com.ooyala.android.item;

import com.ooyala.android.util.OrderedMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelSet extends ContentItem implements PaginatedParentItem {
  protected OrderedMap<String, Channel> _channels = new OrderedMap<String, Channel>();
  protected String _nextChildren = null;
  protected boolean _isFetchingMoreChildren = false;

  ChannelSet() {}

  ChannelSet(JSONObject data, String embedCode) {
    this(data, embedCode, null);
  }

  ChannelSet(JSONObject data, String embedCode, ChannelSet parent) {
    _embedCode = embedCode;
    update(data);
  }

  @Override
  public synchronized ReturnState update(JSONObject data) {
    switch (super.update(data)) {
      case STATE_FAIL:
        return ReturnState.STATE_FAIL;
      case STATE_UNMATCHED:
        //If we didn't match, try updating all subchannels
        for (Channel channel : _channels) {
          channel.update(data);
        }
        return ReturnState.STATE_UNMATCHED;
      default:
        break;
    }

    try {
      //If authorization, check all of the children's video authorization.
      JSONObject myData = data.getJSONObject(_embedCode);
      if (!myData.isNull(ContentItem.KEY_AUTHORIZED) && myData.getBoolean(ContentItem.KEY_AUTHORIZED)) {
        for (Channel channel : _channels) {
          channel.update(data);
        }
        return ReturnState.STATE_MATCHED;
      }

      //If metadata, then update children if possible and break out
      if (!myData.isNull(ContentItem.KEY_METADATA_BASE)) {
        for (Channel channel : _channels) {
          channel.update(data);
        }
        return ReturnState.STATE_MATCHED;
      }

      //Handle content tree
      if (!myData.isNull(ContentItem.KEY_CONTENT_TYPE)
          && !myData.getString(ContentItem.KEY_CONTENT_TYPE).equals(ContentItem.CONTENT_TYPE_CHANNEL_SET)) {
        System.out.println("ERROR: Attempted to initialize ChannelSet with content_type: "
            + myData.getString(ContentItem.KEY_CONTENT_TYPE));
        return ReturnState.STATE_FAIL;
      }

      _nextChildren = myData.isNull(ContentItem.KEY_NEXT_CHILDREN) ? null : myData
          .getString(ContentItem.KEY_NEXT_CHILDREN);

      if (myData.isNull(ContentItem.KEY_CHILDREN)) {
        if (_nextChildren == null) {
          System.out
              .println("ERROR: Attempted to initialize ChannelSet with children == nil and next_children == nil: "
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
              && child.getString(ContentItem.KEY_CONTENT_TYPE).equals(ContentItem.CONTENT_TYPE_CHANNEL)) {
            HashMap<String, JSONObject> childMap = new HashMap<String, JSONObject>();
            String childEmbedCode = child.getString(ContentItem.KEY_EMBED_CODE);
            childMap.put(childEmbedCode, child);
            JSONObject childData = new JSONObject(childMap);
            Channel existingChild = _channels.get(childEmbedCode);
            if (existingChild == null) {
              addChannel(new Channel(childData, childEmbedCode, this));
            } else {
              existingChild.update(childData);
            }
          } else {
            System.out.println("ERROR: Invalid Channel content_type: "
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
   * Get the first Video for this ChannelSet
   * @return the first Video this ChannelSet represents
   */
  @Override
  public Video firstVideo() {
    if (_channels == null || _channels.size() == 0) { return null; }
    return _channels.get(0).firstVideo();
  }

  /**
   * Get the next Video for this ChannelSet (this method should only be called at the end of a channel)
   * @param currentItem the current Channel
   * @return the next Video from ChannelSet
   */
  public Video nextVideo(Channel currentItem) {
    int idx = _channels.indexForValue(currentItem);
    if (idx < 0 || ++idx >= _channels.size()) { return null; }
    return _channels.get(idx).firstVideo();
  }

  /**
   * Get the previous Video for this ChannelSet (this method should only be called at the end of a channel)
   * @param currentItem the current Channel
   * @return the previous Video from ChannelSet
   */
  public Video previousVideo(Channel currentItem) {
    int idx = _channels.indexForValue(currentItem);
    if (idx < 0 || --idx < 0) { return null; }
    return _channels.get(idx).lastVideo();
  }

  protected void addChannel(Channel channel) {
    _channels.put(channel.getEmbedCode(), channel);
  }

  @Override
  public int childrenCount() {
    return _channels.size();
  }

  @Override
  public List<String> embedCodesToAuthorize() {
    List<String> embedCodes = new ArrayList<String>();
    embedCodes.add(_embedCode);
    embedCodes.addAll(_channels.keySet());
    return embedCodes;
  }

  @Override
  public OrderedMap<String, Channel> getAllAvailableChildren() {
    return _channels;
  }

  public OrderedMap<String, Channel> getChannels() {
    return getAllAvailableChildren();
  }
  /**
   * The total duration (not including Ads) of this ChannelSet. This only accounts for currently loaded channels.
   * @return an int with the total duration in seconds
   */
  @Override
  public int getDuration() {
    int totalDuration = 0;
    for (Channel channel : _channels) {
      totalDuration += channel.getDuration();
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

  /**
   * Get the Video in this ChannelSet with the specified embed code
   * @param embedCode the embed code to look up
   * @param currentItem the current Video
   * @return the video in this ChannelSet with the specified embed code
   */
  @Override
  public Video videoFromEmbedCode(String embedCode, Video currentItem) {
    // search through channelset starting with currentItem's channel
    // get first channels index
    int start = (currentItem == null) ? 0 : _channels.indexForValue(currentItem.getParent());
    int i = start;
    do {
      Video v = _channels.get(i).videoFromEmbedCode(embedCode, currentItem);
      if (v != null) { return v; }
      i = i >= _channels.size() ? 0 : i + 1;
    } while (i != start);
    return null;
  }
}
