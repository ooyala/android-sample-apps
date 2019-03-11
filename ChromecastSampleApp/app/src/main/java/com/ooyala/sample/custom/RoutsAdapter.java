package com.ooyala.sample.custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ooyala.android.item.CastMediaRoute;
import com.ooyala.sample.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

public class RoutsAdapter extends RecyclerView.Adapter<RoutsAdapter.CastMediaRouteViewHolder> {
  private List<CastMediaRoute> castMediaRouteList;
  private Consumer<CastMediaRoute> consumer;

  class CastMediaRouteViewHolder extends RecyclerView.ViewHolder {
    TextView textView;
    CastMediaRoute castMediaRoute;

    CastMediaRouteViewHolder(View view) {
      super(view);
      textView = view.findViewById(R.id.title);
      textView.setOnClickListener(v -> consumer.accept(castMediaRoute));
    }

    void setCastMediaRout(CastMediaRoute castMediaRoute) {
      this.castMediaRoute = castMediaRoute;
      textView.setText(castMediaRoute.getName());
    }
  }

  RoutsAdapter(List<CastMediaRoute> castMediaRouteList, Consumer<CastMediaRoute> consumer) {
    this.castMediaRouteList = castMediaRouteList;
    this.consumer = consumer;
  }

  @NonNull
  @Override
  public CastMediaRouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_rout_item, parent, false);
    return new CastMediaRouteViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull CastMediaRouteViewHolder holder, int position) {
    holder.setCastMediaRout(castMediaRouteList.get(position));
  }

  @Override
  public int getItemCount() {
    return castMediaRouteList.size();
  }
}