package com.ooyala.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import com.ooyala.demo.R;

public class AddWatchButton extends CheckBox {
    public AddWatchButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.add_watch);
    }

}
