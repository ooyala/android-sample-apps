package com.ooyala.sample.screen;

import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.ooyala.sample.R;
import com.ooyala.sample.interfaces.VideoChooseInterface;
import com.ooyala.sample.parser.AdType;
import com.ooyala.sample.utils.VideoData;
import com.ooyala.sample.utils.VideoItemType;

public class EmbedCodeDialogFragment extends DialogFragment {
  @Override
  public void setArguments(Bundle args) {
    super.setArguments(args);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.embed_code_dialog_fragment, container);

    final EditText embedCodeEditText = (EditText) view.findViewById(R.id.embed_edit_text);
    final EditText pCodeEditText = (EditText) view.findViewById(R.id.pcode_edit_text);
    final CheckBox imaCheckBox = (CheckBox) view.findViewById(R.id.ima_ad_check_box);

    initOpenVideoButton(view, embedCodeEditText, pCodeEditText, imaCheckBox);
    initCancelButton(view);

    return view;
  }

  private void initCancelButton(View view) {
    view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismiss();
      }
    });
  }

  private void initOpenVideoButton(View view, final EditText embedCodeEditText, final EditText pCodeEditText, final CheckBox imaCheckBox) {
    view.findViewById(R.id.open_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AdType adType = imaCheckBox.isChecked() ? AdType.IMA : AdType.VAST;
        String embedCode = embedCodeEditText.getText().toString();
        String pCode = pCodeEditText.getText().toString();

        if (embedCode.isEmpty()) {
          Toast.makeText(getContext(), "Embed code can't be empty!", Toast.LENGTH_LONG).show();
          dismiss();
          return;
        }
        if (pCode.isEmpty()) {
          pCode = "BzY2syOq6kIK6PTXN7mmrGVSJEFj";
        }

        VideoData data = new VideoData(VideoItemType.VIDEO, "Custom video", adType, embedCode, pCode);
        FragmentActivity activity = getActivity();
        if (activity instanceof VideoChooseInterface) {
          ((VideoChooseInterface) activity).onVideoChoose(data);
        }
        dismiss();
      }
    });
  }
}
