package com.ooyala.android.ui;

import android.graphics.Color;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;

/**
 * This LayoutController is a faster LayoutController that will work only on one specific case: The
 * OoyalaPlayerLayout it controls is a direct child of the Activity's base layout which is a FrameLayout. This
 * LayoutController uses basic controls and allows additional overlays to be added. Fullscreening is done by
 * simply resizing the OoyalaPlayerLayout to fill the entire screen, which does not trigger a player reload
 * thus causing this to be much faster at Fullscreening than OoyalaPlayerLayoutController.
 * @author jigish
 */
public class OptimizedOoyalaPlayerLayoutController extends AbstractOoyalaPlayerLayoutController {
  private boolean _fullscreen = false;
  private FrameLayout.LayoutParams _inlineLP = null;
  private FrameLayout.LayoutParams _fullscreenLP = new FrameLayout.LayoutParams(
      FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.FILL);

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain) {
    this(l, pcode, domain, DefaultControlStyle.AUTO);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param generator An embedTokenGenerator used to sign SAS requests
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, EmbedTokenGenerator generator) {
    this(l, pcode, domain, DefaultControlStyle.AUTO, generator);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param p the instantiated player to use
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p) {
    this(l, p, DefaultControlStyle.AUTO);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, DefaultControlStyle dcs) {
    this(l, pcode, domain, dcs, null);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   * @param generator An embedTokenGenerator used to sign SAS requests
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, DefaultControlStyle dcs, EmbedTokenGenerator generator) {
    this(l, new OoyalaPlayer(pcode, domain, generator, null), dcs);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param pcode the provider code to use
   * @param domain the embed domain to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   * @param generator An embedTokenGenerator used to sign SAS requests
   * @param options Extra values, can be null in which case defaults values are used.
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, String pcode, PlayerDomain domain, DefaultControlStyle dcs, EmbedTokenGenerator generator, Options options) {
    this(l, new OoyalaPlayer(pcode, domain, generator, options), dcs);
  }

  /**
   * Instantiate a FastOoyalaPlayerLayoutController
   * @param l the layout to use
   * @param p the instantiated player to use
   * @param dcs the DefaultControlStyle to use (AUTO is default controls, NONE has no controls)
   */
  public OptimizedOoyalaPlayerLayoutController(OoyalaPlayerLayout l, OoyalaPlayer p, DefaultControlStyle dcs) {
    super(l, p, dcs);
    extraInit(dcs);
    _layout.setBackgroundColor(Color.BLACK);
  }

  private void extraInit(DefaultControlStyle dcs) {
    if (dcs == DefaultControlStyle.AUTO) {
      _fullscreenControls = _inlineControls;
      _fullscreenOverlay = _inlineOverlay;
    }
    _inlineLP = (FrameLayout.LayoutParams) _layout.getLayoutParams();
    _fullscreenLayout = _layout;
  }

  /**
   * @return true if currently in fullscreen, false if not
   */
  @Override
  public boolean isFullscreen() {
    return _fullscreen;
  }

  /**
   * Sets the fullscreen state to this layout controller.
   * @param fullscreen
   */
  @Override
  protected void doFullscreenChange(boolean fullscreen) {
    if (isFullscreen() && !fullscreen) { // Fullscreen -> Not Fullscreen
      _fullscreen = fullscreen;
      _layout.setLayoutParams(_inlineLP);
    } else if (!isFullscreen() && fullscreen) { // Not Fullscreen -> Fullscreen
      _fullscreen = fullscreen;
      _layout.setLayoutParams(_fullscreenLP);
      _layout.bringToFront();
    }
  }
}
