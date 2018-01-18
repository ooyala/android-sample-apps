package com.ooyala.android.item;

import org.json.JSONObject;

import com.ooyala.android.util.OrderedMap;

public interface PaginatedParentItem extends JSONUpdatableItem {

  public String getEmbedCode();

  /**
   * For internal use only. Update the PaginatedParentItem using the specified data (subclasses should
   * override and call this)
   * @param data the data to use to update this PaginatedParentItem
   * @return a ReturnState based on if the data matched or not (or parsing failed)
   */
  public ReturnState update(JSONObject data);

  /**
   * Find out it this PaginatedParentItem has more children
   *
   * @return true if it does, false if it doesn't
   */
  public boolean hasMoreChildren();

  /**
   * Returns all of the children that were already retrieved from the server
   * @return an ordered map that contains all retrieved content items
   */
  public OrderedMap<String, ? extends ContentItem> getAllAvailableChildren();
  /**
   * The number of children this PaginatedParentItem has.
   *
   * @return an int with the number of children
   */
  public int childrenCount();

  /**
   * For Internal Use Only.
   * @return the next children token for this PaginatedParentItem
   */
  public String getNextChildren();
}
