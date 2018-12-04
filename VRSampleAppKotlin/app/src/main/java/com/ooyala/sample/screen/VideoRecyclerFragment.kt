package com.ooyala.sample.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ooyala.sample.R
import com.ooyala.sample.interfaces.VideoChooseInterface
import com.ooyala.sample.adapters.VideoRecyclerAdapter
import com.ooyala.sample.utils.AdList
import com.ooyala.sample.utils.VideoData
import kotlinx.android.synthetic.main.video_recycler_fragment.*

class VideoRecyclerFragment : Fragment() {

  companion object {
    val TAG = VideoRecyclerFragment::class.java.canonicalName
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
      inflater?.inflate(R.layout.video_recycler_fragment, container, false)

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    videoRecyclerView.layoutManager = LinearLayoutManager(context)
    videoRecyclerView.adapter = context?.let { AdList.instance.getVideoList(it) }?.let {
      VideoRecyclerAdapter(it, {
        handleItemChose(data = it)
      })
    }
  }

  private fun handleItemChose(data: VideoData) {
    val currentActivity = activity
    if (currentActivity is VideoChooseInterface) {
      currentActivity.onVideoChoose(data)
    }
  }
}