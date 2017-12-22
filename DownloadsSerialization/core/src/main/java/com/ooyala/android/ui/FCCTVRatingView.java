package com.ooyala.android.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.ooyala.android.util.DebugMode;
import com.ooyala.android.FCCTVRating;
import com.ooyala.android.configuration.FCCTVRatingConfiguration;

/* todo:
 * + update from content item.
 * + handle change between fullscreen & inline.
 * + handle all of TVRatingConfiguration.
 */

public class FCCTVRatingView extends View {

  public static final class RestoreState {
    public final boolean isShowing;
    public final FCCTVRating tvRating;
    public RestoreState(boolean isShowing, FCCTVRating tvRating) {
      this.isShowing = isShowing;
      this.tvRating = tvRating;
    }
  }

  private static final String TAG = "FCCTVRatingView";
  private static final long OVER_CLICKING_PREVENTION_MSEC = 250;

  static boolean isSquareish( int w, int h ) {
    final float fullRatio = w / (float)h;
    final boolean squareish = fullRatio > 0.9 && fullRatio < 1.1;
    return squareish;
  }

  private static final int FADE_IN_MSEC = 1 * 500;
  private static final int FADE_OUT_MSEC = 1 * 1000;
  static final float MINI_HEIGHT_FACTOR = 0.2f;
  private long clickTime;
  private Paint textPaint;
  private Paint blackPaint;
  private Paint whitePaint;
  private Paint clearPaint;
  private float miniTextSize;
  private float miniTextScaleX;
  // n means 'possibly null'; a reminder to check.
  private FCCTVRatingViewStampDimensions nStampDimensions;
  private Bitmap nBitmap;
  private AlphaAnimation nFadeInAnimation;
  private AlphaAnimation nFadeOutAnimation;
  private FCCTVRatingConfiguration nTVRatingConfiguration;
  private FCCTVRating nTVRating;

  public FCCTVRatingView( Context context ) {
    this( context, null );
  }

  public FCCTVRatingView( Context context, AttributeSet attrs ) {
    super( context, attrs );
    initPaints( FCCTVRatingConfiguration.DEFAULT_OPACITY );
    this.miniTextSize = 0;
    this.miniTextScaleX = 0;
    this.nTVRatingConfiguration = FCCTVRatingConfiguration.s_getDefaultTVRatingConfiguration();
  }

  public RestoreState getRestoreState() {
    RestoreState state = null;
    if( nTVRating != null ) {
      state = new RestoreState( getVisibility() == VISIBLE, nTVRating );
    }
    return state;
  }

  public void restoreState( RestoreState state ) {
    setTVRating( state.tvRating );
    if( state.isShowing ) {
      reshow();
    }
  }

  public void reshow() {
    if( nTVRatingConfiguration != null && nTVRatingConfiguration.durationSeconds > 0 ) {
      setAlphaForView(this, 1);
      setVisibility(VISIBLE);
      startAnimation();
    }
    else {
      setAlphaForView(this, 0);
      setVisibility(GONE);
    }
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    freeResources();
  }

  @Override
  public boolean onTouchEvent( MotionEvent event ) {
    boolean consumed = false;
    final boolean clickAllowed = System.currentTimeMillis() - clickTime > OVER_CLICKING_PREVENTION_MSEC;
    final boolean isVisible = getVisibility() == VISIBLE;
    // we're fighting with the controls so have to do it on down, rather than up.
    final boolean isDown = event.getAction() == MotionEvent.ACTION_DOWN;
    final boolean contains = nStampDimensions == null ? false : nStampDimensions.contains( event.getX(), event.getY() );
    if( hasClickthrough() && clickAllowed && isVisible && isDown && contains ) {
      getContext().startActivity(
          new Intent(
              Intent.ACTION_VIEW,
              Uri.parse( nTVRating.clickthrough )
              )
          );
      clickTime = event.getDownTime();
      consumed = true;
    }
    return consumed;
  }

  @Override
  protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
    if( ! hasTVRatingConfiguration() ) {
      setMeasuredDimension( 0, 0 );
    }
    else {
      final int paddingLeft = getPaddingLeft();
      final int paddingTop = getPaddingTop();
      final int paddingRight = getPaddingRight();
      final int paddingBottom = getPaddingBottom();
      final int viewWidthSize = MeasureSpec.getSize(widthMeasureSpec);
      final int viewHeightSize = MeasureSpec.getSize(heightMeasureSpec);
      final int elementWidth = viewWidthSize - paddingLeft - paddingRight;
      final int elementHeight = viewHeightSize - paddingTop - paddingBottom;
      int measuredWidth = elementWidth + paddingLeft + paddingRight;
      int measuredHeight = elementHeight + paddingTop + paddingBottom;
      measuredWidth = Math.max(measuredWidth, getSuggestedMinimumWidth());
      measuredHeight = Math.max(measuredHeight, getSuggestedMinimumHeight());
      setMeasuredDimension( measuredWidth, measuredHeight );
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged( w, h, oldw, oldh );
    freeResources();
  }

  /**
   * For the configuration to actually take effect, it must be set before Android's
   * layout system calls onMeasure.
   */
  public void setTVRatingConfiguration( FCCTVRatingConfiguration TVRatingConfiguration ) {
    nTVRatingConfiguration = TVRatingConfiguration;
    if( hasTVRatingConfiguration() ) {
      initPaints( TVRatingConfiguration.opacity );
      freeResources();
    }
  }

  private void initPaints( float opacity ) {
    final int iOpacity = (int)Math.round(255*opacity);

    blackPaint = new Paint();
    blackPaint.setColor( Color.argb( iOpacity, 0, 0, 0 ) );
    blackPaint.setStyle( Paint.Style.FILL );

    whitePaint = new Paint();
    whitePaint.setColor( Color.argb( iOpacity, 255, 255, 255 ) );
    whitePaint.setStyle( Paint.Style.FILL );

    textPaint = new Paint( Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG );
    textPaint.setColor( Color.argb( iOpacity, 255, 255, 255 ) );
    textPaint.setStyle( Paint.Style.FILL );
    Typeface tf = Typeface.create( "DroidSans", Typeface.BOLD );
    textPaint.setTypeface( tf );
    textPaint.setTextAlign( Align.CENTER );

    clearPaint = new Paint();
    clearPaint.setColor( Color.TRANSPARENT );
    clearPaint.setStyle( Paint.Style.FILL );
  }

  public void setTVRating( FCCTVRating tvRating ) {
    if( tvRating != null && !tvRating.equals(nTVRating) ) {
      nTVRating = tvRating;
  	  freeResources();
    }
  }

  public FCCTVRating getTVRating() {
    return nTVRating;
  }

  private void startAnimation() {
    if( hasValidRating() &&
        ! hasAnimation() &&
        hasTVRatingConfiguration() &&
        nTVRatingConfiguration.durationSeconds != FCCTVRatingConfiguration.DURATION_NONE ) {
    	startFadeInAnimation();
    }
  }

  private void startFadeInAnimation() {
	  nFadeInAnimation = new AlphaAnimation( 0f, 1f );
	  nFadeInAnimation.setDuration( FADE_IN_MSEC );
	  nFadeInAnimation.setFillAfter( true );
	  nFadeInAnimation.setAnimationListener(new AnimationListener(){
		  @Override
		  public void onAnimationStart(Animation arg0) {
			  setVisibility( VISIBLE );
		  }
		  @Override
		  public void onAnimationEnd(Animation arg0) {
			  startFadeOutAnimation();
		  }
		  @Override
		  public void onAnimationRepeat(Animation arg0) {
		  }
	  });
	  startAnimation( nFadeInAnimation );
  }

  private void setAlphaForView(View v, float alpha) {
    AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
    animation.setDuration(0);
    animation.setFillAfter(true);
    v.startAnimation(animation);
  }

  private void startFadeOutAnimation() {
    if( hasTVRatingConfiguration() &&
        nTVRatingConfiguration.durationSeconds != FCCTVRatingConfiguration.DURATION_FOR_EVER ) {
      nFadeOutAnimation = new AlphaAnimation( 1f, 0f );
      nFadeOutAnimation.setStartOffset( nTVRatingConfiguration.durationSeconds * 1000 );
      nFadeOutAnimation.setDuration( FADE_OUT_MSEC );
      nFadeOutAnimation.setFillAfter( true );
      nFadeOutAnimation.setAnimationListener(new AnimationListener(){
        @Override
        public void onAnimationStart(Animation arg0) {
        }
        @Override
        public void onAnimationEnd(Animation arg0) {
          setVisibility( INVISIBLE );
        }
        @Override
        public void onAnimationRepeat(Animation arg0) {
        }
      });
      startAnimation( nFadeOutAnimation );
    }
  }

  private void freeResources() {
    nBitmap = null;
  }

  private boolean hasTVRatingConfiguration() {
    return nTVRatingConfiguration != null;
  }

  private boolean hasAnimation() {
    return nFadeInAnimation != null || nFadeOutAnimation != null;
  }

  private boolean hasValidRating() {
    return nTVRating != null && nTVRating.ageRestriction != null;
  }

  private boolean hasLabels() {
    return nTVRating != null && nTVRating.labels != null && nTVRating.labels.length() > 0;
  }

  private boolean hasClickthrough() {
    return nTVRating != null && nTVRating.clickthrough != null;
  }

  private boolean hasValidStampDimensions() {
    return nStampDimensions == null ? false : nStampDimensions.whiteRect.width() > 0 && nStampDimensions.whiteRect.height() > 0;
  }

  private boolean hasBitmap() {
    return nBitmap != null;
  }

  @Override
  protected void onDraw( Canvas canvas ) {
    super.onDraw( canvas );
    maybeGenerateBitmap();
    if( hasBitmap() ) {
      DebugMode.assertCondition( nStampDimensions != null, TAG, "nStampDimensions should not be null if we bitmap is non-null" );
      canvas.drawBitmap( nBitmap, nStampDimensions.left, nStampDimensions.top, null );
    }
  }

  private void maybeGenerateBitmap() {
    if( hasValidRating() ) {
      nStampDimensions = new FCCTVRatingViewStampDimensions( getContext(), nTVRatingConfiguration, getMeasuredWidth(), getMeasuredHeight(), hasLabels() );
      if( hasValidStampDimensions() ) {
        generateBitmap();
      }
    }
    else {
      freeResources();
    }
  }

  private void generateBitmap() {
    nBitmap = Bitmap.createBitmap( nStampDimensions.whiteRect.width(), nStampDimensions.whiteRect.height(), Bitmap.Config.ARGB_8888 ); // todo: Check for fastest ARGB mode vs. SurfaceView.
    Canvas c = new Canvas( nBitmap );
    drawBitmapStamp( c );
  }

  private void drawBitmapStamp( Canvas c ) {
    drawBitmapStampBackground( c );
    drawBitmapStampTV( c );
    drawBitmapStampLabels( c );
    drawBitmapStampRating( c );
  }

  private void drawBitmapStampBackground( Canvas c ) {
    c.clipRect( nStampDimensions.whiteRect, Region.Op.REPLACE );
    c.drawRect( nStampDimensions.whiteRect, whitePaint );
    c.drawRect( nStampDimensions.blackRect, blackPaint );
  }

  private void drawBitmapStampTV( Canvas c ) {
    c.clipRect( nStampDimensions.tvRect, Region.Op.REPLACE );
    drawTV( c, nStampDimensions.tvRect );
  }

  private void drawBitmapStampLabels( Canvas c ) {
    if( hasLabels() ) {
      c.clipRect( nStampDimensions.labelsRect, Region.Op.REPLACE );
      drawLabels( c, nStampDimensions.labelsRect, nTVRating.labels );
    }
  }

  private void drawBitmapStampRating( Canvas c ) {
    if( hasValidRating() ) {
      c.clipRect( nStampDimensions.ratingRect, Region.Op.REPLACE );
      drawRating( c, nStampDimensions.ratingRect, nTVRating.ageRestriction );
    }
  }

  private void drawTV( Canvas c, Rect r ) {
    updateMiniTextPaintFactors( r );
    String text = "TV";
    drawTextInRectGivenTextFactors( c, r, text, miniTextSize, miniTextScaleX );
  }

  private void drawLabels( Canvas c, Rect r, String labels ) {
    drawTextInRectGivenTextFactors( c, r, labels, miniTextSize, miniTextScaleX );
  }

  private void drawRating( Canvas c, Rect r, String rating ) {
    drawTextInRectAutoTextFactors( c, r, rating );
  }

  private void updateTextPaintFactors( Rect r, String text ) {
    Pair<Float,Float> sizeAndScaleX = calculateTextPaintFactors( r, text );
    textPaint.setTextSize( sizeAndScaleX.first );
    textPaint.setTextScaleX( sizeAndScaleX.second );
  }

  private void updateMiniTextPaintFactors( Rect exampleMiniRect ) {
    // calculate text factors; same ones are used for both top and bottom strips of the rating stamp.
    Pair<Float,Float> sizeAndScaleX = calculateTextPaintFactors( exampleMiniRect, "VSLDFV" );
    miniTextSize = sizeAndScaleX.first;
    miniTextScaleX = sizeAndScaleX.second;
  }

  private Pair<Float,Float> calculateTextPaintFactors( Rect r, String text ) {
    textPaint.setTextSize( 1000 );
    Rect tb = new Rect();
    textPaint.getTextBounds( text, 0, text.length(), tb );
    float ts = r.height()/(float)tb.height()*1000;
    // fudge factors to really fit into rect.
    float textSize = ts * 0.7f;
    float tsx = r.width()/(float)tb.width()*1000;
    float textScaleX = Math.min( 1f, tsx ) * 0.7f;
    return new Pair<Float,Float>( textSize, textScaleX );
  }

  private void drawTextInRectGivenTextFactors( Canvas c, Rect r, String text, float textSize, float textScaleX ) {
    textPaint.setTextSize( textSize );
    textPaint.setTextScaleX( textScaleX );
    drawTextInRect( c, r, text );
  }

  private void drawTextInRectAutoTextFactors( Canvas c, Rect r, String text ) {
    updateTextPaintFactors( r, text );
    drawTextInRect( c, r, text );
  }

  private void drawTextInRect( Canvas c, Rect r, String text ) {
    Rect tb = new Rect();
    textPaint.getTextBounds( text, 0, text.length(), tb );
    int tl = r.left + Math.round(r.width()/2f);
    int tt = r.top + Math.round((r.height() + tb.height())/2f);
    c.drawText( text, tl, tt, textPaint );
  }
}

