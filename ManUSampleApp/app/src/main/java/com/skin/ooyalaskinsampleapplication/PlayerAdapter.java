package com.skin.ooyalaskinsampleapplication;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<TemplateVideoModal> {
    MediaClickListener myListener;
    private List<PlayerSelectionOption> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlayerAdapter(List<PlayerSelectionOption> myDataset, MediaClickListener listener) {
        mDataset = myDataset;
        this.myListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TemplateVideoModal onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new TemplateVideoModal(viewGroup, myListener, false);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(TemplateVideoModal holder, int position) {
        if (mDataset.get(position).getEmbedCode() != null)
            holder.updateData(position, mDataset.get(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}