package com.ooyala.sample.adapters

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import com.ooyala.sample.R
import com.ooyala.sample.R.id.videoTitleTextView
import com.ooyala.sample.utils.VideoData
import com.ooyala.sample.utils.VideoItemType
import kotlinx.android.synthetic.main.view_holder_item.view.*
import java.util.*

class VideoRecyclerAdapter(private val dataList: List<VideoData>, private val listener: (VideoData) -> Unit) : RecyclerView.Adapter<VideoRecyclerAdapter.ItemViewHolder>() {

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bindItem(dataList[position], listener)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    return ItemViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.view_holder_item, parent, false))
  }

  override fun getItemCount(): Int = dataList.size

  class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    init {
      itemView.setOnClickListener { itemView.videoTitleTextView.performClick() }
      itemView.setOnFocusChangeListener { v, hasFocus ->
        if (hasFocus) {
          itemView.videoTitleTextView.videoTitleTextView.setTextColor(Color.RED)
        } else {
          itemView.videoTitleTextView.videoTitleTextView.setTextColor(Color.WHITE)
        }
      }
    }

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