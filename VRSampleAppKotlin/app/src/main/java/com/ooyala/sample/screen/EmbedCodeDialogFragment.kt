package com.ooyala.sample.screen

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.ooyala.sample.R
import com.ooyala.sample.interfaces.VideoChooseInterface
import com.ooyala.sample.parser.AdType
import com.ooyala.sample.utils.VideoData
import com.ooyala.sample.utils.VideoItemType

class EmbedCodeDialogFragment : DialogFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.embed_code_dialog_fragment, container)

    val embedCodeEditText = view.findViewById<EditText>(R.id.embed_edit_text)
    val pCodeEditText = view.findViewById<EditText>(R.id.pcode_edit_text)
    val imaCheckBox = view.findViewById<CheckBox>(R.id.ima_ad_check_box)

    initOpenVideoButton(view, embedCodeEditText, pCodeEditText, imaCheckBox)
    initCancelButton(view)

    return view
  }

  private fun initCancelButton(view: View) {
    view.findViewById<Button>(R.id.cancel_button).setOnClickListener({ dismiss() })
  }

  private fun initOpenVideoButton(view: View, embedCodeEditText: EditText, pCodeEditText: EditText, imaCheckBox: CheckBox) {
    view.findViewById<Button>(R.id.open_button).setOnClickListener({
      var adType = if (imaCheckBox.isChecked) AdType.IMA else AdType.VAST
      var embedCode = embedCodeEditText.text.toString()
      var pCode: String? = pCodeEditText.text.toString()

      if (embedCode.isEmpty()) {
        Toast.makeText(context, "Embed code can't be empty!", Toast.LENGTH_LONG).show()
        dismiss()
        return@setOnClickListener
      }
      if (pCode!!.isEmpty()) {
        pCode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj"
      }

      val data = VideoData(VideoItemType.VIDEO, "Custom video", adType, embedCode, pCode)
      if (activity is VideoChooseInterface) {
        (activity as VideoChooseInterface).onVideoChoose(data)
      }
      dismiss()
    })
  }
}
