package com.ooyala.sample.adapters

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.ooyala.sample.R
import com.ooyala.sample.utils.VideoData
import com.ooyala.sample.utils.VideoItemType
import kotlinx.android.synthetic.main.view_holder_item.view.*

class VideoRecyclerAdapter(private val dataList: ArrayList<VideoData>, private val listener: (VideoData) -> Unit) : Adapter<VideoRecyclerAdapter.ItemViewHolder>() {

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bindItem(dataList[position], listener)

  override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ItemViewHolder =
          ItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_holder_item, parent, false))

  override fun getItemCount(): Int = dataList.size

  class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindItem(data: VideoData, listener: (VideoData) -> Unit) {
      itemView.videoTitleTextView.visibility = GONE
      if (data.type == VideoItemType.VIDEO) {
        itemView.sectionTitleTextView.visibility = GONE
        itemView.videoTitleTextView.visibility = VISIBLE
        itemView.videoTitleTextView.text = data.title
      } else {
        itemView.videoTitleTextView.visibility = GONE
        itemView.sectionTitleTextView.visibility = VISIBLE
        itemView.sectionTitleTextView.text = data.title
      }

      itemView.setOnClickListener { listener(data) }
    }
  }
}