package com.ooyala.android.captions;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

public class ClosedCaptionsStyle implements Comparable {

  public static final int CC_FONT_SP = 16;

	public int textColor;
	public float textSize;
	public float textOpacity;
	public Typeface textFont;

	public int backgroundColor;
	public int backgroundOpacity;

	public int bottomMargin;

	public int edgeType;
	public int edgeColor;

  public ClosedCaptionsStyle(Context context) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
		  CaptioningManagerWrapper.updateClosedCaptionsStyleFromCaptioningManager(this, context);
		} else {
			this.textSize = CC_FONT_SP;
			this.textFont = Typeface.DEFAULT;
			this.textColor = Color.WHITE;

			this.backgroundColor = Color.BLACK;

			this.edgeType = 0;
			this.edgeColor = Color.TRANSPARENT;
		}
	}

	@Override
	public int compareTo(Object style) {
		ClosedCaptionsStyle closedCaptionsStyle = (ClosedCaptionsStyle) style;
		if (this.edgeColor == closedCaptionsStyle.edgeColor &&
				this.edgeType == closedCaptionsStyle.edgeType &&
				this.textSize == closedCaptionsStyle.textSize &&
				this.textFont == closedCaptionsStyle.textFont &&
				this.textColor == closedCaptionsStyle.textColor &&
				this.backgroundColor == closedCaptionsStyle.backgroundColor) {

			return 0;
		}
		return 1;
	}
}
