package com.ooyala.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;

import com.ooyala.android.configuration.FCCTVRatingConfiguration;

final class FCCTVRatingViewStampDimensions {

  private static final class Dimensions {
    public final int width;
    public final int height;
    public Dimensions( int width, int height ) {
      this.width = width;
      this.height = height;
    }
  }

  private static final int WHITE_BORDER_DP = 2;
  private static final int BLACK_BORDER_DP = 4;
  private static final int MINIMUM_SIZE_PT = 24; // different than iOS version, to work on small Android displays.

  // sizes are in pixels.
  private int miniHeight;
  // there's a white outer border
  // and then a space inside that,
  // called the black border,
  // so the text doesn't hit the white border.
  private int whiteBorderSize;
  private int blackBorderSize;
  private int totalBorderSize;
  private Dimensions textableDimensions;
  public int left;
  public int top;
  public Rect blackRect;
  public Rect whiteRect;
  public Rect tvRect;
  public Rect labelsRect;
  public Rect ratingRect;

  // fyi: there are relationships among values, so the order of calls is important in the code below.

  public FCCTVRatingViewStampDimensions( Context context, FCCTVRatingConfiguration tvRatingConfiguration, int measuredWidth, int measuredHeight, boolean hasLabels ) {
    updateBorder( context );
    updateDimensions( context, tvRatingConfiguration.scale, measuredWidth, measuredHeight );
    updateRects( measuredWidth, measuredHeight, hasLabels );
    updatePosition( tvRatingConfiguration.position, measuredWidth, measuredHeight );
  }

  public boolean contains( float x, float y ) {
    return whiteRect.contains( (int)x-left, (int)y-top );
  }

  private void updateBorder( Context context ) {
    // yet it really only needs to be done once, but the context requirement prevents it from going into the constructor.
    whiteBorderSize = (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, WHITE_BORDER_DP, context.getResources().getDisplayMetrics() );
    blackBorderSize = (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, BLACK_BORDER_DP, context.getResources().getDisplayMetrics() );
    totalBorderSize = whiteBorderSize + blackBorderSize;
  }

  private void updateDimensions( Context context, float scale, int measuredWidth, int measuredHeight ) {
    textableDimensions = calculateTextableDimensions( context, scale, measuredWidth, measuredHeight );
  }

  private Dimensions calculateTextableDimensions( Context context, float scale, int measuredWidth, int measuredHeight ) {
    return
        calculateFinalTextableDimensions( context,
            calculateBasicTextableDimentions( scale, measuredWidth, measuredHeight )
            );
  }

  private Dimensions calculateBasicTextableDimentions( float scale, int measuredWidth, int measuredHeight ) {
    // Base the square off the halved video
    float w = measuredWidth;
    float h = measuredHeight;
    if (measuredWidth > measuredHeight) {
      w = measuredWidth / 2;
    } else {
      h = measuredHeight / 2;
    }
    int textableWidth = Math.round( scale * w );
    int textableHeight = Math.round( scale * h );
    return new Dimensions( textableWidth, textableHeight );
  }

  private Dimensions calculateFinalTextableDimensions( Context context, Dimensions textableSoFar ) {
    int textableWidth = textableSoFar.width;
    int textableHeight = textableSoFar.height;
    // per iOS:
    //    // Ensure width and height are of minimum size
    //    +  int minimumSize = [self calculateMinimumSizeInPixels];
    //    +  width = MAX( width, minimumSize );
    //    +  height = MAX( height, minimumSize );
    int minimumSize = (int)TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_PT, MINIMUM_SIZE_PT, context.getResources().getDisplayMetrics() );
    textableWidth = Math.max( textableWidth, minimumSize );
    textableHeight = Math.max( textableHeight, minimumSize );
    //    +  //Square the stamp
    //    +  height = MIN( height, width );
    //    +  width = height;
    int min = Math.min( textableHeight, textableWidth );
    textableWidth = min;
    return new Dimensions( textableWidth, textableHeight );
  }

  private void updateRects( int measuredWidth, int measuredHeight, boolean hasLabels ) {
    Rect textableRect = new Rect( totalBorderSize, totalBorderSize, textableDimensions.width, textableDimensions.height );
    blackRect = new Rect( textableRect );
    blackRect.inset( -blackBorderSize, -blackBorderSize );
    whiteRect = new Rect( blackRect );
    whiteRect.inset( -whiteBorderSize, -whiteBorderSize );

    miniHeight = Math.round( textableRect.height() * FCCTVRatingView.MINI_HEIGHT_FACTOR );

    int tl = textableRect.left;
    int tt = textableRect.top;
    int tr = textableRect.right;
    int tb = tt + miniHeight;
    tvRect = new Rect( tl, tt, tr, tb );

    int rl = textableRect.left;
    int rt = tb;
    int rr = textableRect.right;
    int rb = textableRect.bottom - (hasLabels ? miniHeight : 0);
    ratingRect = new Rect( rl, rt, rr, rb );

    int ll = textableRect.left;
    int lt = rb;
    int lr = textableRect.right;
    int lb = textableRect.bottom;
    labelsRect = new Rect( ll, lt, lr, lb );
  }

  private void updatePosition( FCCTVRatingConfiguration.Position position, int measuredWidth, int measuredHeight ) {
    int right = measuredWidth - whiteRect.width();
    int bottom = measuredHeight - whiteRect.height();
    switch( position ) {
    default:
    case TopLeft:     left = 0;     top = 0;      break;
    case BottomLeft:  left = 0;     top = bottom; break;
    case TopRight:    left = right; top = 0;      break;
    case BottomRight: left = right; top = bottom; break;
    }
  }
}