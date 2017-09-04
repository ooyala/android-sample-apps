package com.skin.ooyalaskinsampleapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    PlayListener myListener;
    private List<PlayerSelectionOption> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlayerAdapter(List<PlayerSelectionOption> myDataset, PlayListener listener) {
        mDataset = myDataset;
        this.myListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // final String name = mDataset.get(position);
        holder.txtHeader.setText("" + (position));
        if (mDataset.get(position).isAutoPlay()) {
            holder.skinLayout.setVisibility(View.VISIBLE);
            myListener.playVideo(mDataset.get(position), ((ViewHolder) holder).skinLayout);
        } else {
            holder.skinLayout.setVisibility(View.INVISIBLE);
        }

        //holder.skinLayout
        /*holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(name);
            }
        });

        holder.txtFooter.setText("Footer: " + mDataset.get(position));*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public OoyalaSkinLayout skinLayout;
        private OoyalaSkinLayoutController playerController;
        //public TextView txtFooter;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            skinLayout = (OoyalaSkinLayout) v.findViewById(R.id.ooyala_player_skin);
            //txtFooter = (TextView) v.findViewById(R.id.secondLine);
        }
    }

}