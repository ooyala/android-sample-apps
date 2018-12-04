package com.ooyala.sample.utils;


import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ooyala.sample.R;

import java.util.List;

public class OfflineAnalyticsAdapter extends RecyclerView.Adapter<OfflineAnalyticsAdapter.ViewHolder> {
  private List<String> offlineFileNames;

  public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView textView;
    public ViewHolder(View view) {
      super(view);
      this.textView = view.findViewById(R.id.textViewOfflineFile);
      view.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      Context context = view.getContext();
      TextView textView = view.findViewById(R.id.textViewOfflineFile);
      String fileName = textView.getText().toString();
      String fileContent = Utils.fileToString(context, fileName);

      CharSequence text = "Logging";
      int duration = Toast.LENGTH_SHORT;
      Toast toast = Toast.makeText(context, text, duration);
      toast.show();

      Utils.logLongString(fileContent, "DTOA");
    }
  }

  public OfflineAnalyticsAdapter(List<String> offlineFileNames) {
    this.offlineFileNames = offlineFileNames;
  }

  @Override
  public OfflineAnalyticsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
    View listItem = layoutInflater.inflate(R.layout.offline_analytics_item, parent, false);
    ViewHolder viewHolder = new ViewHolder(listItem);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    String fileName = offlineFileNames.get(position);
    holder.textView.setText(fileName);
  }

  @Override
  public int getItemCount() {
    return offlineFileNames.size();
  }
}