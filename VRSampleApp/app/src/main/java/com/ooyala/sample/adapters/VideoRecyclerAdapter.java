package com.ooyala.sample.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ooyala.sample.R;
import com.ooyala.sample.interfaces.ItemClickedInterface;
import com.ooyala.sample.utils.VideoData;
import com.ooyala.sample.utils.VideoItemType;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.VideoItemViewHolder> {

  private List<VideoData> datas;
  private ItemClickedInterface clickedInterface;

  public VideoRecyclerAdapter(List<VideoData> datas, ItemClickedInterface clickedInterface) {
    this.datas = datas;
    this.clickedInterface = clickedInterface;
  }

  @Override
  public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_item, parent, false);
    VideoItemViewHolder videoItemViewHolder = new VideoItemViewHolder(itemView);
    return videoItemViewHolder;
  }

  @Override
  public void onBindViewHolder(VideoItemViewHolder holder, int position) {
    holder.bind(datas.get(position), clickedInterface);
}

  @Override
  public int getItemCount() {
    return datas.size();
  }

  static class VideoItemViewHolder extends RecyclerView.ViewHolder {

    private TextView mSectionTextView;
    private TextView mVideoTitleTextView;

    VideoItemViewHolder(View itemView) {
      super(itemView);
      mSectionTextView = (TextView) itemView.findViewById(R.id.sectionTitleTextView);
      mVideoTitleTextView = (TextView) itemView.findViewById(R.id.videoTitleTextView);
    }

    private void bind(final VideoData data, final ItemClickedInterface clickInterface) {
      if (data.getItemType() == VideoItemType.SECTION) {
        mVideoTitleTextView.setVisibility(View.GONE);
        mSectionTextView.setVisibility(View.VISIBLE);
        mSectionTextView.setText(data.getTitle());
      } else {
        mSectionTextView.setVisibility(View.GONE);
        mVideoTitleTextView.setVisibility(View.VISIBLE);
        mVideoTitleTextView.setText(data.getTitle());
        mVideoTitleTextView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            clickInterface.onItemClicked(data);
          }
        });
      }
    }
  }
}
