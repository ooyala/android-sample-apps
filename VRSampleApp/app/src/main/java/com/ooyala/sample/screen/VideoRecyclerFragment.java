package com.ooyala.sample.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooyala.sample.R;
import com.ooyala.sample.adapters.VideoRecyclerAdapter;
import com.ooyala.sample.interfaces.ItemClickedInterface;
import com.ooyala.sample.interfaces.TvControllerInterface;
import com.ooyala.sample.utils.AdList;
import com.ooyala.sample.utils.VideoData;

import java.util.List;

import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

public class VideoRecyclerFragment extends Fragment implements TvControllerInterface {

  public static final String TAG = VideoRecyclerFragment.class.getCanonicalName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View inflated = inflater.inflate(R.layout.video_recycler_fragment, container, false);
    RecyclerView recyclerView = (RecyclerView) inflated.findViewById(R.id.videoRecyclerView);

    ItemClickedInterface itemClickedInterface = null;

    if (getActivity() instanceof ItemClickedInterface) {
      itemClickedInterface = (ItemClickedInterface) getActivity();
    }

    LinearLayoutManager layout = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layout);
    List<VideoData> videoList = AdList.getInstance().getVideoList(getContext());
    adapter = new VideoRecyclerAdapter(videoList, itemClickedInterface);
    recyclerView.setAdapter(adapter);
    return inflated;
  }

  VideoRecyclerAdapter adapter;

  @Override
  public void onKeyUp(int keyCode, KeyEvent event) {
    switch (keyCode) {
      case KEYCODE_DPAD_UP:
        adapter.selectPrevious();
        break;
      case KEYCODE_DPAD_DOWN:
        adapter.selectNext();
        break;
      case KEYCODE_DPAD_CENTER:
        adapter.chooseCurrent();
        break;
    }
  }

  @Override
  public void onKeyDown(int keyCode, KeyEvent event) {

  }
}
