package com.ooyala.android;

public class PaginatedItemResponse {
  /** The index at which the first new child was inserted */
  public int firstIndex = -1;
  /** The number of additional children fetched */
  public int count = 0;

  public PaginatedItemResponse(int firstIndex, int count) {
    this.firstIndex = firstIndex;
    this.count = count;
  }
}
