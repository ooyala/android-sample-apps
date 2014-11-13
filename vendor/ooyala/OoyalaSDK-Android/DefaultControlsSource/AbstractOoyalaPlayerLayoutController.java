package com.ooyala.android.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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

import com.ooyala.android.DebugMode;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.LocalizationSupport;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.player.FCCTVRatingUI;

public abstract class AbstractOoyalaPlayerLayoutController implements LayoutController {
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
  protected AlertDialog dialog;
  private FCCTVRatingUI _tvRatingUI;

  private int selectedLanguageIndex;
  private int selectedPresentationIndex;

  public int getSelectedLanguageIndex() {
    return this.selectedLanguageIndex;
  }

  public int getSelectedPresentationIndex() {
    return this.selectedPresentationIndex;
  }

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
    this(l, new OoyalaPlayer(pcode, domain, generator, null), dcs);
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
    Set<String> languageSet = _player.getAvailableClosedCaptionsLanguages();
    List<String> languageList = new ArrayList<String>(languageSet);
    Collections.sort(languageList);
    languageList.add(0, LocalizationSupport.localizedStringFor("None"));

    final Context context = _layout.getContext();

    if (this.optionList == null) {
      this.optionList = new ArrayList<String>();
      this.optionList.add(LocalizationSupport.localizedStringFor("Languages"));
      this.optionList.addAll(languageList);
      //this.optionList.add(LocalizationSupport.localizedStringFor("Presentation Styles"));
      //this.optionList.add(LocalizationSupport.localizedStringFor("Roll-Up"));
      //this.optionList.add(LocalizationSupport.localizedStringFor("Paint-On"));
      //this.optionList.add(LocalizationSupport.localizedStringFor("Pop-On"));
      this.optionList.add(LocalizationSupport.localizedStringFor("Done"));
    }

    listView = new ListView(context);
    ClosedCaptionArrayAdapter optionAdapter = new ClosedCaptionArrayAdapter(context,
        android.R.layout.simple_list_item_checked, this.optionList, this);
    listView.setAdapter(optionAdapter);
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setView(listView);
    this.dialog = builder.create();
    this.dialog.show();
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

  private void radioButtonClicked(int position) {

    if (position == (this.optionList.size() - 1)) {
      this.dialog.hide();
    } else {
      if (this.selectedLanguageIndex != 0 && this.selectedLanguageIndex != position) {
        int langIndexOnScreen = this.selectedLanguageIndex - listView.getFirstVisiblePosition();
        // check if listView is trying to unCheck Language Index that is
        // out of screen
        if (langIndexOnScreen < 0 || this.selectedLanguageIndex > listView.getLastVisiblePosition()) {
          DebugMode.logD(TAG, "previous selected language index out of screen");
        } else {
          ((RadioButton) listView.getChildAt(langIndexOnScreen)).setChecked(false);
        }
      }
      this.selectedLanguageIndex = position;
      _player.setClosedCaptionsLanguage(this.optionList.get(position));
    }

    /*else if (position != this.selectedLanguageIndex && position != this.selectedPresentationIndex) {
      if (position < this.optionList.indexOf(LocalizationSupport.localizedStringFor("Presentation Styles"))) {
        if (this.selectedLanguageIndex != 0) {
          int langIndexOnScreen = this.selectedLanguageIndex - listView.getFirstVisiblePosition();
          // check if listView is trying to unCheck Language Index that is
          // out of screen
          if (langIndexOnScreen < 0 || this.selectedLanguageIndex > listView.getLastVisiblePosition()) {
            DebugMode.logD(TAG, "previous selected language index out of screen");
          } else {
            ((RadioButton) listView.getChildAt(langIndexOnScreen)).setChecked(false);
          }
        }
        this.selectedLanguageIndex = position;
        _player.setClosedCaptionsLanguage(this.optionList.get(position));
      } else {
        if (this.selectedPresentationIndex != 0) {
          int presIndexOnScreen = this.selectedPresentationIndex - listView.getFirstVisiblePosition();
          // check if listView is trying to unCheck Presentation Index that is
          // out of screen
          if (presIndexOnScreen < 0 || this.selectedPresentationIndex > listView.getLastVisiblePosition()) {
            DebugMode.logD(TAG, "previous selected language index out of screen");
          } else {
            ((RadioButton) listView.getChildAt(presIndexOnScreen)).setChecked(false);
          }
        }
        this.selectedPresentationIndex = position;
        if (this.optionList.get(position).equals(LocalizationSupport.localizedStringFor("Roll-Up"))) {
          _player.setClosedCaptionsPresentationStyle(OOClosedCaptionPresentation.OOClosedCaptionRollUp);
        } else if (this.optionList.get(position).equals(LocalizationSupport.localizedStringFor("Paint-On"))) {
          _player.setClosedCaptionsPresentationStyle(OOClosedCaptionPresentation.OOClosedCaptionPaintOn);
        } else {
          _player.setClosedCaptionsPresentationStyle(OOClosedCaptionPresentation.OOClosedCaptionPopOn);
        }
      }
    }*/
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
      if (position == this.itemList.indexOf(LocalizationSupport.localizedStringFor("Languages"))
          || position == this.itemList.indexOf(LocalizationSupport.localizedStringFor("Presentation Styles"))) {
        TextView header = new TextView(this.context);
        header.setText(itemList.get(position));
        header.setTextColor(Color.LTGRAY);
        header.setTextSize(30);
        header.setPadding(5, 0, 10, 10);
        header.setBackgroundColor(Color.BLACK);
        return header;
      } else if (position == itemList.indexOf(LocalizationSupport.localizedStringFor("Done"))) {
        Button doneButton = new Button(this.context);
        doneButton.setText(itemList.get(position));
        doneButton.setTextColor(Color.LTGRAY);
        doneButton.setTextSize(30);
        doneButton.setPadding(5, 0, 10, 10);
        doneButton.setBackgroundColor(Color.BLACK);
        doneButton.setGravity(Gravity.CENTER_HORIZONTAL);
        final int currentPosition = position;
        doneButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            controller.radioButtonClicked(currentPosition);
          }
        });
        return doneButton;
      }
      RadioButton radioButton = new RadioButton(this.context);
      radioButton.setText(itemList.get(position));
      //radioButton.setPadding(10, 0, 0, 0); If we set the padding for radio button we will have radio button and text overlap
      final int currentPosition = position;
      if (currentPosition == this.controller.getSelectedLanguageIndex()
          || currentPosition == this.controller.getSelectedPresentationIndex()) {
        radioButton.setChecked(true);
      }
      radioButton.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
          // TODO Auto-generated method stub
          controller.radioButtonClicked(currentPosition);
        }
      });
      return radioButton;
    }
  }
}
