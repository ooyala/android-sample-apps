package com.ooyala.android.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.captions.ClosedCaptionsStyle;
import com.ooyala.android.captions.ClosedCaptionsView;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Caption;
import com.ooyala.android.item.Video;
import com.ooyala.android.player.FCCTVRatingUI;
import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public abstract class AbstractOoyalaPlayerLayoutController implements LayoutController, Observer {
  private static final String TAG = AbstractOoyalaPlayerLayoutController.class.getName();
  public static enum DefaultControlStyle {
    NONE, AUTO
  };

  protected OoyalaPlayerLayout _layout = null;
  protected Dialog _fullscreenDialog = null;
  protected OoyalaPlayerLayout _fullscreenLayout = null;
  protected OoyalaPlayerControls _inlineControls = null;
  protected OoyalaPlayerControls _fullscreenControls = null;
  protected OoyalaPlayerControls _inlineOverlay = null;
  protected OoyalaPlayerControls _fullscreenOverlay = null;
  protected OoyalaPlayer _player = null;
  protected boolean _fullscreenButtonShowing = true;
  protected List<String> optionList;
  protected ListView listView;
  protected AlertDialog ccLanguageDialog;
  protected boolean ccLanguageDialogCanceled;
  protected RadioButton ccLanguageDialogUncommittedSelection;
  private FCCTVRatingUI _tvRatingUI;
  private ClosedCaptionsView _closedCaptionsView;
  private ClosedCaptionsStyle _closedCaptionsStyle = null;
  private String selectedLanguageId;
  private final String languageNone;

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain) {
    this(l, pcode, domain, DefaultControlStyle.AUTO);
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param generator An embedTokenGenerator used to sign SAS requests
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, EmbedTokenGenerator generator) {
    this(l, pcode, domain, DefaultControlStyle.AUTO, generator);
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param p the instantiated player to use
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p) {
    this(l, p, DefaultControlStyle.AUTO);
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, DefaultControlStyle dcs) {
    this(l, new OoyalaPlayer(pcode, domain), dcs);
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   * @param generator An embedTokenGenerator used to sign SAS requests
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain,
      DefaultControlStyle dcs, EmbedTokenGenerator generator) {
    this( l, new OoyalaPlayer( pcode, domain, generator, null ), dcs );
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   * @param generator An embedTokenGenerator used to sign SAS requests
   * @param options Extra values, can be null in which case defaults values are used.
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain,
      DefaultControlStyle dcs, EmbedTokenGenerator generator, Options options) {
    this(l, new OoyalaPlayer(pcode, domain, generator, options), dcs);
  }

  /**
   * Instantiate an AbstractOoyalaPlayerLayoutController
   *
   * @param l the layout to use
   * @param p the instantiated player to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   */
  public AbstractOoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p, DefaultControlStyle dcs) {
    _player = p;
    _layout = l;
    _player.setLayoutController(this);
    _layout.setLayoutController(this);
    if (dcs == DefaultControlStyle.AUTO) {
      setInlineControls(createDefaultControls(_layout, false));
      _inlineControls.hide();
      _player.addObserver(_inlineControls);
    }
    _player.addObserver( this );
    languageNone = LocalizationSupport.localizedStringFor( "None" );
    selectedLanguageId = languageNone;
  }

  @Override
  public void addVideoView( View videoView ) {
    removeVideoView();
    if( videoView != null ) {
      _tvRatingUI = new FCCTVRatingUI( _player, videoView, getLayout(), _player.getOptions().getTVRatingConfiguration() );
    }
  }

  @Override
  public void removeVideoView() {
    if( _tvRatingUI != null ) {
      _tvRatingUI.destroy();
      _tvRatingUI = null;
    }
  }

  @Override
  public void reshowTVRating() {
    if( _tvRatingUI != null ) {
      _tvRatingUI.reshow();
    }
  }

  public void setInlineOverlay(OoyalaPlayerControls controlsOverlay) {
    _inlineOverlay = controlsOverlay;
    _inlineOverlay.setOoyalaPlayer(_player);
  }

  public void setFullscreenOverlay(OoyalaPlayerControls controlsOverlay) {
    _fullscreenOverlay = controlsOverlay;
    _fullscreenOverlay.setOoyalaPlayer(_player);
  }

  public void setInlineControls(OoyalaPlayerControls controls) {
    if(_inlineControls != null) _inlineControls.hide();
    _player.deleteObserver(_inlineControls);
    _inlineControls = controls;
    if (_inlineControls != null) {
      if (!isFullscreen()) {
        _player.addObserver(_inlineControls);
      }
      _inlineControls.setFullscreenButtonShowing(_fullscreenButtonShowing);
    }
  }

  public void setFullscreenControls(OoyalaPlayerControls controls) {
    if(_fullscreenControls != null) _fullscreenControls.hide();
    _player.deleteObserver(_fullscreenControls);
    _fullscreenControls = controls;
    if (_fullscreenControls != null) {
      if (isFullscreen()) {
        _player.addObserver(_fullscreenControls);
      }
      _fullscreenControls.setFullscreenButtonShowing(_fullscreenButtonShowing);
    }
  }

  /**
   * Get the OoyalaPlayer associated with this Controller
   *
   * @return the OoyalaPlayer
   */
  public OoyalaPlayer getPlayer() {
    return _player;
  }

  /**
   * Get the current active layout
   *
   * @return the current active layout
   */
  @Override
  public FrameLayout getLayout() {
    return isFullscreen() ? _fullscreenLayout.getPlayerFrame() : _layout.getPlayerFrame();
  }

  public OoyalaPlayerControls getControls() {
    return isFullscreen() ? _fullscreenControls : _inlineControls;
  }

  public OoyalaPlayerControls getOverlay() {
    return isFullscreen() ? _fullscreenOverlay : _inlineOverlay;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event, OoyalaPlayerLayout source) {
    // the MediaController will hide after 3 seconds - tap the screen to make it appear again
    if (_player != null && event.getAction() == MotionEvent.ACTION_DOWN) {
      switch (_player.getState()) {
      case INIT:
      case LOADING:
      case ERROR:
        return false;
      default:
        if (getControls() != null) {
          if (getControls().isShowing()) {
            getControls().hide();
          } else {
            getControls().show();
          }
        }
        if (getOverlay() != null) {
          if (getOverlay().isShowing()) {
            getOverlay().hide();
          } else {
            getOverlay().show();
          }
        }
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    boolean handled = false;
    if (_player != null) {
      switch (_player.getState()) {
      case PLAYING:
        switch (keyCode) {
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
          _player.pause();
          handled = true;
          break;
        case KeyEvent.KEYCODE_MEDIA_REWIND:
          _player.previousVideo(OoyalaPlayer.DO_PLAY);
          handled = true;
          break;
        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
          _player.nextVideo(OoyalaPlayer.DO_PLAY);
          handled = true;
          break;
        default:
          break;
        }
        break;

      case READY:
      case PAUSED:
        switch (keyCode) {
        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
          _player.play();
          handled = true;
          break;
        case KeyEvent.KEYCODE_MEDIA_REWIND:
          _player.previousVideo(OoyalaPlayer.DO_PAUSE);
          handled = true;
          break;
        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
          _player.nextVideo(OoyalaPlayer.DO_PAUSE);
          handled = true;
          break;
        default:
          break;
        }
        break;

      default:
        break;
      }
    }
    return handled;
  }

  @Override
  public final void setFullscreen(boolean fullscreen) {
    beforeFullscreenChange();
    doFullscreenChange( fullscreen );
    afterFullscreenChange();
  }

  @Override
  public boolean isFullscreen() {
    return false;
  }

  private FCCTVRatingView.RestoreState _tvRatingRestoreState;
  protected void beforeFullscreenChange() {
    if (_tvRatingUI != null) {
      _tvRatingRestoreState = _tvRatingUI.getRestoreState();
    }
  }

  protected abstract void doFullscreenChange( boolean fullscreen );

  protected void afterFullscreenChange() {
      boolean pushable = _tvRatingUI != null && _tvRatingRestoreState != null;
      if( pushable ) {
        _tvRatingUI.restoreState( _tvRatingRestoreState );
      }
  }

  public OoyalaPlayerControls createDefaultControls(OoyalaPlayerLayout layout, boolean fullscreen) {
    if (fullscreen) {
      return new DefaultOoyalaPlayerFullscreenControls(_player, layout);
    } else {
      return new DefaultOoyalaPlayerInlineControls(_player, layout);
    }
  }

  /**
   * Create and display the list of available languages.
   */
  @Override
  public void showClosedCaptionsMenu() {
    if (this.ccLanguageDialog == null || (this.ccLanguageDialog != null && !this.ccLanguageDialog.isShowing())) {
      Set<String> languageSet = _player.getAvailableClosedCaptionsLanguages();
      List<String> languageList = new ArrayList<>();
      for (String language : languageSet) {
        if (_player.getCurrentItem().getVTTClosedCaptions() != null) {
          String languageFullName = _player.getCurrentItem().getVTTClosedCaptions().getLanguageFullName(language);
          if (null != languageFullName) languageList.add(languageFullName);
        } else {
          languageList.add(language);
        }
      }
      Collections.sort(languageList);
      languageList.add(0, languageNone );
  
      final Context context = _layout.getContext();
  
      if (this.optionList == null) {
        this.optionList = new ArrayList<String>();
        this.optionList.add(LocalizationSupport.localizedStringFor("Languages"));
        this.optionList.addAll(languageList);
        this.optionList.add(LocalizationSupport.localizedStringFor("Done"));
      }
  
      listView = new ListView(context);
      ClosedCaptionArrayAdapter optionAdapter = new ClosedCaptionArrayAdapter(context,
          android.R.layout.simple_list_item_checked, this.optionList, this);
      listView.setAdapter(optionAdapter);
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setView( listView );
//      builder.setOnDismissListener( new DialogInterface.OnDismissListener() {
//        @Override public void onDismiss( DialogInterface dialog ) { ccLanguageDialogDismissed(); }
//      } );
      builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
        @Override public void onCancel( DialogInterface dialog ) { onCCLanguageDialogCanceled(); }
      } );
      this.ccLanguageDialog = builder.create();
      this.ccLanguageDialog.setCanceledOnTouchOutside( true );
      this.ccLanguageDialog.show();
      this.ccLanguageDialogCanceled = false;
    }
  }

  /**
   * setFullscreenButtonShowing will enable and disable visibility of the fullscreen button
   */
  public void setFullscreenButtonShowing(boolean showing){

    if (_inlineControls != null) {
      _inlineControls.setFullscreenButtonShowing(showing);
    }
    if (_fullscreenControls != null) {
      _fullscreenControls.setFullscreenButtonShowing(showing);
    }
    _fullscreenButtonShowing = showing;
  }

  private void changeLanguage() {
    if( ! ccLanguageDialogCanceled ) {
      String language = ccLanguageDialogUncommittedSelection.getText().toString();
      if (_player.getCurrentItem().getVTTClosedCaptions() != null) {
        String languageCode = _player.getCurrentItem().getVTTClosedCaptions().getLanguageCode(language);
        if (null != languageCode) language = languageCode;
      }
      if( _player == null ) {
        DebugMode.logE( TAG, "Trying to set Closed Captions while player is null" );
      } else {
        if( language.equals( languageNone ) ) {
          _player.setClosedCaptionsLanguage( "" );
        } else {
          _player.setClosedCaptionsLanguage( language );
        }
      }
      DebugMode.logD( TAG, "Closed captions language is now: '" + language + "'" );
      selectedLanguageId = language;
    }
  }

  private void onCCLanguageDialogCanceled() {
    ccLanguageDialogCanceled = true;
  }

  private void onCCDialogDoneClicked() {
    this.ccLanguageDialog.dismiss();
  }

  private void onCCDialogLanguageClicked( RadioButton button ) {
    DebugMode.logD( TAG, "onCCDialogLanguageClicked: " + ", " + button.getText() + ", " + button.isChecked() );
    ccLanguageDialogUncommittedSelection = button;
    checkOnly( button );
    changeLanguage();
  }

  private void checkOnly( RadioButton button ) {
    for( int i = 0; i < listView.getCount(); i++ ) {
//      DebugMode.logD( TAG, "checkOnly: " + i );
      View v = listView.getChildAt( i );
      if( v instanceof RadioButton ) {
        RadioButton rb = (RadioButton)v;
        rb.setChecked( rb == button ? true : false );
//        DebugMode.logD( TAG, "checkOnly: " + i + ", " + rb.isChecked() + ", " + rb.getText() );
      }
    }
  }

  public void setClosedCaptionsPresentationStyle() {
    removeClosedCaptionsView();
    _closedCaptionsView = new ClosedCaptionsView(_layout.getContext());
    if( _closedCaptionsStyle != null ) {
      _closedCaptionsView.setStyle(_closedCaptionsStyle);
    }
    refreshClosedCaptionsView();
  }

  private void removeClosedCaptionsView() {
    if (_closedCaptionsView != null) {
      ViewGroup parent = (ViewGroup)_closedCaptionsView.getParent();
      parent.removeView(_closedCaptionsView);
      _closedCaptionsView = null;
    }
  }

  private boolean shouldShowClosedCaptions() {
    return _player != null && !_player.isShowingAd() &&
        _player.getCurrentItem() != null &&
        _player.getClosedCaptionsLanguage() != null &&
        !_player.getClosedCaptionsLanguage().equals("") &&
        !_player.isInCastMode();
  }

  private void refreshClosedCaptionsView() {
    removeClosedCaptionsView();
    if (shouldShowClosedCaptions()) {
      addClosedCaptionsView();
      displayCurrentClosedCaption();
    }
  }

  private void addClosedCaptionsView() {
    _closedCaptionsStyle = new ClosedCaptionsStyle(getLayout().getContext());
    _closedCaptionsStyle.bottomMargin = getControls().bottomBarOffset();
    _closedCaptionsView = new ClosedCaptionsView(getLayout().getContext());
    _closedCaptionsView.setStyle(_closedCaptionsStyle);
    getLayout().addView(_closedCaptionsView);
  }

  /**
   * @return the current ClosedCaptionsStyle
   */
  public ClosedCaptionsStyle getClosedCaptionsStyle() {
    return _closedCaptionsStyle;
  }

  /**
   * Set the ClosedCaptionsStyle
   *
   * @param closedCaptionsStyle
   *          the ClosedCaptionsStyle to use
   */
  public void setClosedCaptionsStyle(ClosedCaptionsStyle closedCaptionsStyle) {
    _closedCaptionsStyle = closedCaptionsStyle;
    if (_closedCaptionsStyle != null) {
      if( _closedCaptionsView != null ) {
        _closedCaptionsView.setStyle(_closedCaptionsStyle);
        _closedCaptionsView.setStyle(_closedCaptionsStyle);
      }
    }
    refreshClosedCaptionsView();
  }

  /**
   * Set the bottomMargin of closedCaptions view
   *
   * @param bottomMargin
   *          the bottom margin to use
   */
  public void setClosedCaptionsBottomMargin(int bottomMargin) {
    if( _closedCaptionsStyle != null ) {
      _closedCaptionsStyle.bottomMargin = bottomMargin;
      if( _closedCaptionsView != null ) {
        _closedCaptionsView.setStyle(_closedCaptionsStyle);
      }
    }
  }

  void displayCurrentClosedCaption() {
    if (_closedCaptionsView == null || _player == null || _player.getCurrentItem() == null)
      return;

    Video currentItem = _player.getCurrentItem();
    String ccLanguage = _player.getClosedCaptionsLanguage();

    // PB-3090: we currently only support captions for the main content, not
    // also the advertisements.
    if (ccLanguage != null && currentItem.hasClosedCaptions() && !_player.isShowingAd()) {
      double currT = _player.getPlayheadTime() / 1000d;
      if (_closedCaptionsView.getCaption() == null
          || currT > _closedCaptionsView.getCaption().getEnd()
          || currT < _closedCaptionsView.getCaption().getBegin()) {
        Caption caption = currentItem.getClosedCaptions().getCaption(
                _player.getClosedCaptionsLanguage(), currT);
        if (caption != null && caption.getBegin() <= currT
            && caption.getEnd() >= currT) {
          _closedCaptionsView.setCaption(caption);
        } else {
          _closedCaptionsView.setCaption(null);
        }
      }
    } else {
      if (ccLanguage != null && ccLanguage.equals(OoyalaPlayer.LIVE_CLOSED_CAPIONS_LANGUAGE)) {
        return;
      }
      // In what scenario we need to call this?
      _closedCaptionsView.setCaption(null);
    }
  }

  class ClosedCaptionArrayAdapter extends ArrayAdapter<String> {

    private final List<String> itemList;
    private final Context context;
    private final AbstractOoyalaPlayerLayoutController controller;

    public ClosedCaptionArrayAdapter(Context context, int textViewResourceId, List<String> objects,
        AbstractOoyalaPlayerLayoutController controller) {
      super(context, textViewResourceId, objects);
      this.itemList = objects;
      this.context = context;
      this.controller = controller;
    }

    @Override
    public boolean isEnabled(int position) {
      return (position != 0)
          && (position != this.itemList
          .indexOf(LocalizationSupport.localizedStringFor("Presentation Styles")));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      // If "Languages" or "Presentation Styles", do NOT add a radio button
      if( position == this.itemList.indexOf( LocalizationSupport.localizedStringFor( "Languages" ) )
        || position == this.itemList.indexOf( LocalizationSupport.localizedStringFor( "Presentation Styles" ) ) ) {
        TextView header = new TextView( this.context );
        header.setText( itemList.get( position ) );
        header.setTextColor( Color.LTGRAY );
        header.setTextSize( 30 );
        header.setPadding( 5, 0, 10, 10 );
        header.setBackgroundColor( Color.BLACK );
        return header;
      } else if( position == itemList.indexOf( LocalizationSupport.localizedStringFor( "Done" ) ) ) {
        Button doneButton = new Button( this.context );
        doneButton.setText( itemList.get( position ) );
        doneButton.setTextColor( Color.LTGRAY );
        doneButton.setTextSize( 30 );
        doneButton.setPadding( 5, 0, 10, 10 );
        doneButton.setBackgroundColor( Color.BLACK );
        doneButton.setGravity( Gravity.CENTER_HORIZONTAL );
        final int currentPosition = position;
        doneButton.setOnClickListener( new View.OnClickListener() {
          @Override public void onClick( View v ) { controller.onCCDialogDoneClicked(); }
        } );
        return doneButton;
      }
      else {
        return createRadioButton( position );
      }
    }

    private RadioButton createRadioButton( int position ) {
      final RadioButton radioButton = new RadioButton( this.context );
      final String language = itemList.get( position );
      radioButton.setText( language );

      //We're about to create a radio button.  Check if the language is our currently selected language

      //Check if the currently selected language code exists in CurrentItem, and if this button's text matches it
      boolean isCurrentButtonSelectedAfterFullNameConversion = (_player.getCurrentItem().getVTTClosedCaptions() != null &&
      language.equals( _player.getCurrentItem().getVTTClosedCaptions().getLanguageFullName(this.controller.selectedLanguageId)));

      //Check if the currently select language code matches the button's intended text directly
      boolean isCurrentButtonSelectedDirectly = language.equals(this.controller.selectedLanguageId);

      //If currently selected is "none" and we're about to render the None button
      boolean isCurrentButtonSelectedNone = language.equals(languageNone) && this.controller.selectedLanguageId == languageNone;

      if (isCurrentButtonSelectedAfterFullNameConversion
              || isCurrentButtonSelectedDirectly
              || isCurrentButtonSelectedNone) {

        radioButton.setChecked( true );
        controller.ccLanguageDialogUncommittedSelection = radioButton;
      }
      radioButton.setOnClickListener( new View.OnClickListener() {
        @Override public void onClick( View v ) {
          controller.onCCDialogLanguageClicked( (RadioButton) v );
        }
      });
      return radioButton;
    }
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    String notificationName = null;

    OoyalaNotification ooNotification = null;
    if (arg1 instanceof OoyalaNotification){
      ooNotification = (OoyalaNotification)arg1;
      notificationName = OoyalaNotification.getNameOrUnknown(arg1);
    } else {
      DebugMode.logW(TAG, "Unidentified notification, ignorning for now");
      return;
    }

    if (notificationName.equals(OoyalaPlayer.STATE_CHANGED_NOTIFICATION_NAME)) {
      refreshClosedCaptionsView();
    } else if (notificationName.equals(OoyalaPlayer.AD_STARTED_NOTIFICATION_NAME)) {
      removeClosedCaptionsView();
    } else if (notificationName.equals(OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME)) {
      displayCurrentClosedCaption();
    } else if (notificationName.equals(OoyalaPlayer.CLOSED_CAPTIONS_LANGUAGE_CHANGED_NAME)) {
      refreshClosedCaptionsView();
    } else if (notificationName.equals(OoyalaPlayer.LIVE_CC_CHANGED_NOTIFICATION_NAME)) {
      String caption = ((Map<String, String>)ooNotification.getData()).get(OoyalaPlayer.CLOSED_CAPTION_TEXT);
      refreshClosedCaptionsView();
      if (_closedCaptionsView != null) {
        _closedCaptionsView.setCaptionText(caption);
      }
    }
  }

}
