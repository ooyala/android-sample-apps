package com.ooyala.sample.utils;

import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

public class CuePointsSeekBar extends AppCompatSeekBar {

  private Set<Float> cuePoints;
  private Paint paint;

  private static final int CUE_POINT_COLOR = 0xffffffff;
  private static final float CUE_POINT_RADIUS_FACTOR = 0.15f;

  public CuePointsSeekBar(Context context) {
    super(context);
    init();
  }

  public CuePointsSeekBar(final Context context, final AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public CuePointsSeekBar(final Context context, final AttributeSet attrs,
                          final int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  public void setCuePoints(Set<Float> cuePoints) {
    this.cuePoints = cuePoints;
    this.invalidate();
  }

  private void init() {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(CUE_POINT_COLOR);
  }

  @Override
  protected synchronized void onDraw(final Canvas canvas) {
    super.onDraw(canvas);

    if (cuePoints != null) {
      float offset = getThumbOffset() * 2;
      float width = getWidth() - offset * 2;
      float height = getHeight();

      for (Float cuePointPercentage : cuePoints) {
        float radius = height * CUE_POINT_RADIUS_FACTOR;
        float cx = cuePointPercentage * width * 0.01f + offset;
        float cy = height * 0.5f;
        canvas.drawCircle(cx, cy, radius, paint);
      }
    }
  }
}
