package com.ooyala.sample.adapters;

import android.graphics.Color;
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
  private int selectedPosition = 0;
  private boolean isChosen = false;

  public VideoRecyclerAdapter(List<VideoData> datas, ItemClickedInterface clickedInterface) {
    this.datas = datas;
    this.clickedInterface = clickedInterface;
  }

  public void selectNext() {
    if (selectedPosition < datas.size() - 1) {
      selectedPosition++;
      notifyItemChanged(selectedPosition);

      int previousPosition = selectedPosition - 1;
      notifyItemChanged(previousPosition);
    }
  }

  public void selectPrevious() {
    if (selectedPosition > 1) {
      selectedPosition--;
      notifyItemChanged(selectedPosition);

      int previousPosition = selectedPosition + 1;
      notifyItemChanged(previousPosition);
    }
  }

  public void chooseCurrent() {
    isChosen = true;
    notifyDataSetChanged();
  }

  @Override
  public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_item, parent, false);
    return new VideoItemViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(VideoItemViewHolder holder, int position) {
    holder.bind(datas.get(position), clickedInterface, position);
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
    }

    private void bind(final VideoData data, final ItemClickedInterface clickInterface, int position) {
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
            clickInterface.onItemClicked(data);
          }
        });
      }

      keyPressProcessing(data, position);
    }

    private void keyPressProcessing(VideoData data, int position) {
      if (position == selectedPosition && data.getItemType() != VideoItemType.SECTION) {
        if (isChosen) {
          videoTitleTextView.performClick();
          return;
        }
        videoTitleTextView.setTextColor(Color.RED);
        sectionTextView.setTextColor(Color.RED);
      } else {
        videoTitleTextView.setTextColor(Color.WHITE);
        sectionTextView.setTextColor(Color.WHITE);
      }
    }
  }
}
