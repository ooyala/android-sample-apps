package com.ooyala.sample.utils;

import java.util.Set;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSeekBar;

public class CuePointsSeekBar extends AppCompatSeekBar {

  private Set<Integer> _cuePoints;
  private Paint _paint;

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

  public void setCuePoints(Set<Integer> cuePoints) {
    _cuePoints = cuePoints;
    this.invalidate();
  }

  private void init() {
    _paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    _paint.setStyle(Paint.Style.FILL);
    _paint.setColor(CUE_POINT_COLOR);
  }

  @Override
  protected synchronized void onDraw(final Canvas canvas) {
    super.onDraw(canvas);

    if (_cuePoints != null) {
      float width = getWidth() - getThumbOffset() * 2;
      float height = getHeight();
      float step = width / getMax();

      for (Integer i : _cuePoints) {
        canvas.drawCircle(i * step + getThumbOffset(), height / 2, height
            * CUE_POINT_RADIUS_FACTOR,
            _paint);
      }
    }
  }
}
