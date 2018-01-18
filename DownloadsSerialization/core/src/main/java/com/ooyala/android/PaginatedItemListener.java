package com.ooyala.android;

public interface PaginatedItemListener {
  public void onItemsFetched(int firstIndex, int count, OoyalaException error);
}
