package com.ooyala.sample.adapters;

import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ooyala.sample.R;
import com.ooyala.sample.interfaces.VideoChooseInterface;
import com.ooyala.sample.utils.VideoData;
import com.ooyala.sample.utils.VideoItemType;

import java.util.List;

public class VideoRecyclerAdapter extends RecyclerView.Adapter<VideoRecyclerAdapter.VideoItemViewHolder> {

  private List<VideoData> datas;
  private VideoChooseInterface clickedInterface;

  public VideoRecyclerAdapter(List<VideoData> datas, VideoChooseInterface clickedInterface) {
    this.datas = datas;
    this.clickedInterface = clickedInterface;
  }

  @Override
  public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_item, parent, false);
    return new VideoItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(VideoItemViewHolder holder, int position) {
    holder.bind(datas.get(position), clickedInterface);
  }

  @Override
  public int getItemCount() {
    return datas.size();
  }

  class VideoItemViewHolder extends RecyclerView.ViewHolder {

    private TextView sectionTextView;
    private TextView videoTitleTextView;

    VideoItemViewHolder(final View itemView) {
      super(itemView);
      sectionTextView = (TextView) itemView.findViewById(R.id.sectionTitleTextView);
      videoTitleTextView = (TextView) itemView.findViewById(R.id.videoTitleTextView);
      itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          videoTitleTextView.performClick();
        }
      });

      itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
         if(hasFocus){
           videoTitleTextView.setTextColor(Color.RED);
         } else {
           videoTitleTextView.setTextColor(Color.WHITE);
         }
        }
      });
    }

    private void bind(final VideoData data, final VideoChooseInterface clickInterface) {
      if (data.getItemType() == VideoItemType.SECTION) {
        videoTitleTextView.setVisibility(View.GONE);
        sectionTextView.setVisibility(View.VISIBLE);
        sectionTextView.setText(data.getTitle());
      } else {
        sectionTextView.setVisibility(View.GONE);
        videoTitleTextView.setVisibility(View.VISIBLE);
        videoTitleTextView.setText(data.getTitle());
        videoTitleTextView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            clickInterface.onVideoChoose(data);
          }
        });
      }
    }
  }
}
