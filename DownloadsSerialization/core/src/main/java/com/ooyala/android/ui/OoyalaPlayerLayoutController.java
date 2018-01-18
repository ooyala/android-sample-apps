package com.ooyala.android.ui;

import android.app.Dialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;

/**
 * This LayoutController is a generic LayoutController that will work in most cases (regardless of the
 * containing Layout type). It uses basic controls and allows additional overlays to be added. Fullscreening
 * is done by opening a full screen Dialog and filling it with a dynamically created OoyalaPlayerLayout.
 * Because of this, playback will be suspended and subsequently resumed during this process. As a result,
 * fullscreening is slower than if the OoyalaPlayerLayout is embeded directly in the Activity's base layout,
 * that base layout is a FrameLayout, and the LayoutController used is FastOoyalaPlayerLayoutController.
 */
public class OoyalaPlayerLayoutController extends AbstractOoyalaPlayerLayoutController {

 /**
   * Instantiate an OoyalaPlayerLayoutController
   * @param l the layout to use
   * @param p the instantiated player to use
   */
  public OoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p) {
    this(l, p, DefaultControlStyle.AUTO);
  }

 /**
   * Instantiate an OoyalaPlayerLayoutController
   * @param l the layout to use
   * @param p the instantiated player to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   */
  public OoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p, DefaultControlStyle dcs) {
    super(l, p, dcs);
  }

  /**
   * @return true if currently in fullscreen, false if not
   */
  @Override
  public boolean isFullscreen() {
    return _fullscreenLayout != null;
  }

  /**
   * Sets the fullscreen state to this layout controller.
   * @param fullscreen
   */
  @Override
  protected void doFullscreenChange(boolean fullscreen) {
    _player.suspend();
    OoyalaPlayerControls controlsToShow = null;
    OoyalaPlayerControls overlayToShow = null;
    if (isFullscreen() && !fullscreen) { // Fullscreen -> Not Fullscreen
      _fullscreenDialog.dismiss();
      _fullscreenDialog = null;
      _fullscreenLayout.removeAllViews();
      _fullscreenLayout = null;
      controlsToShow = _inlineControls;
      if (_inlineOverlay != null) {
        _inlineOverlay.setParentLayout(_layout);
        overlayToShow = _inlineOverlay;
      }

      if (_inlineControls != null) {
        _player.addObserver(_inlineControls);
      }
      if (_fullscreenControls != null) {
        _player.deleteObserver(_fullscreenControls);
      }

      _inlineControls.setVisible(true);

    } else if (!isFullscreen() && fullscreen) { // Not Fullscreen -> Fullscreen
      _fullscreenDialog = new Dialog(_layout.getContext(), android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen) {
        @Override
        public void onBackPressed() {
          if (_player.isFullscreen()) {
            _player.setFullscreen(false);
          } else {
            super.onBackPressed();
          }
        }
      };
      _fullscreenLayout = new OoyalaPlayerLayout(_fullscreenDialog.getContext());
      _fullscreenLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT, Gravity.FILL));
      _fullscreenLayout.setLayoutController(this);
      _fullscreenDialog.setContentView(_fullscreenLayout);
      _fullscreenDialog.show();

      _inlineControls.setVisible(false);

      if (_fullscreenControls == null) {
        setFullscreenControls(createDefaultControls(_fullscreenLayout, true));
      } else {
        _fullscreenControls.setParentLayout(_fullscreenLayout);
      }
      controlsToShow = _fullscreenControls;
      if (_fullscreenOverlay != null) {
        _fullscreenOverlay.setParentLayout(_fullscreenLayout);
        overlayToShow = _fullscreenOverlay;
      }

      if (_inlineControls != null) {
        _player.deleteObserver(_inlineControls);
      }
      if (_fullscreenControls != null) {
        _player.addObserver(_fullscreenControls);
      }
    }
    if (controlsToShow != null) {
      controlsToShow.show();
    }
    if (overlayToShow != null) {
      overlayToShow.show();
    }
    _player.resume();
  }
}
