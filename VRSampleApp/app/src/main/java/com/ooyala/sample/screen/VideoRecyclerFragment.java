package com.ooyala.sample.screen;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooyala.sample.R;
import com.ooyala.sample.adapters.VideoRecyclerAdapter;
import com.ooyala.sample.interfaces.VideoChooseInterface;
import com.ooyala.sample.utils.AdList;
import com.ooyala.sample.utils.VideoData;

import java.util.List;

public class VideoRecyclerFragment extends Fragment {

  public static final String TAG = VideoRecyclerFragment.class.getCanonicalName();

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View inflated = inflater.inflate(R.layout.video_recycler_fragment, container, false);
    RecyclerView recyclerView = (RecyclerView) inflated.findViewById(R.id.videoRecyclerView);

    VideoChooseInterface videoChooseInterface = null;

    if (getActivity() instanceof VideoChooseInterface) {
      videoChooseInterface = (VideoChooseInterface) getActivity();
    }

    LinearLayoutManager layout = new LinearLayoutManager(getContext());
    recyclerView.setLayoutManager(layout);
    List<VideoData> videoList = AdList.getInstance().getVideoList(getContext());
    adapter = new VideoRecyclerAdapter(videoList, videoChooseInterface);
    recyclerView.setAdapter(adapter);
    return inflated;
  }

  VideoRecyclerAdapter adapter;
}
