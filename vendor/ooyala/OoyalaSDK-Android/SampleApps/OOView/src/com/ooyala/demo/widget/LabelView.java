package com.ooyala.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.ooyala.demo.R;
import com.ooyala.demo.utils.FontUtils;

/**
 * User: Aider
 * Date: Aug 1, 2010
 * Time: 10:07:01 AM
 */
public class LabelView extends TextView {
    public LabelView(Context context) {
        super(context);
        init(context);
    }

    public LabelView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.label);
        init(context);
    }

    public LabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTypeface(FontUtils.getFont(context.getAssets()));
    }
}
