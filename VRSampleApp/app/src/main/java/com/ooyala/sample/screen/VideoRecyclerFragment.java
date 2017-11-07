package com.ooyala.sample.screen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ooyala.sample.R;
import com.ooyala.sample.adapters.VideoRecyclerAdapter;
import com.ooyala.sample.interfaces.ItemClickedInterface;
import com.ooyala.sample.utils.AdList;

public class VideoRecyclerFragment extends Fragment {

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

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(new VideoRecyclerAdapter(AdList.videoList, itemClickedInterface));
    return inflated;
  }
}
