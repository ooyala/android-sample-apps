package com.ooyala.android;

import com.ooyala.android.OoyalaPlayer.State;
import com.ooyala.android.player.PlayerInterface;
import com.ooyala.android.plugin.AdPluginInterface;
import com.ooyala.android.plugin.LifeCycleInterface;
import com.ooyala.android.util.DebugMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The plugin that manage ad plugins. AdPlugin manager handles content/ad switch
 * and queries plugins when certain event happens.
 *
 */
class AdPluginManager implements LifeCycleInterface, AdPluginManagerInterface {
  private static final String TAG = AdPluginManager.class.getName();
  private OoyalaPlayer _player;
  private List<AdPluginInterface> _plugins = new ArrayList<AdPluginInterface>();
  private AdPluginInterface _activePlugin = null;
  private AdMode _admode = AdMode.None;
  private int _parameter = 0;

  /**
   * Constructor
   *
   * @param player
   *          the ooyalaplayer who owns the ad plugin manager
   * @return true on success, false otherwise
   */
  public AdPluginManager(OoyalaPlayer player) {
    _player = player;
  }

  @Override
  public boolean registerPlugin(final AdPluginInterface plugin) {
    if (_plugins.contains(plugin)) {
      DebugMode.logD(TAG, "plugin " + plugin.toString() + "already exist");
      return false;
    }

    for (AdPluginInterface p : _plugins) {
      if (plugin.getClass() == p.getClass()) {
        DebugMode.logD(TAG, "plugin " + p.toString() + " is same class as "
            + plugin.toString());
      }
    }

    DebugMode.logD(TAG, "register ad plugin" + plugin.toString());
    _plugins.add(plugin);
    return true;
  }

  @Override
  public boolean deregisterPlugin(final AdPluginInterface plugin) {
    if (!_plugins.contains(plugin)) {
      DebugMode.logD(TAG, plugin.toString()
          + "is not registered or has been removed");
      return false;
    }

    if (_activePlugin == plugin) {
      DebugMode.assertFail(TAG,
          "try to deregister when the plugin is still active");
      return false;
    }

    _plugins.remove(plugin);
    DebugMode.logD(TAG, "deregister ad plugin" + plugin.toString());
    return true;
  }

  @Override
  public boolean exitAdMode(final AdPluginInterface plugin) {
    if (plugin == null) {
      DebugMode.assertFail(TAG, "exitAdModed.plugin is null");
      return false;
    }

    if (!_plugins.contains(plugin)) {
      DebugMode.assertFail(TAG, plugin.toString()
          + " exit admode before it register");
      return false;
    }

    if (_activePlugin != plugin) {
      if (_activePlugin != null) {
        DebugMode.assertFail(TAG, plugin.toString()
          + " exit admode but active plugin is " + _activePlugin.toString());
        return false;
      } else {
        return true;
      }
    }

    AdPluginInterface nextPlugin = null;
    if (_admode != AdMode.PluginInitiated) {
      nextPlugin = getNextPlugin(_plugins, plugin);

      while (nextPlugin != null && !pluginNeedsAdMode(nextPlugin, _admode)) {
        nextPlugin = getNextPlugin(_plugins, nextPlugin);
      }
    }

    if (nextPlugin == null) {
      AdMode mode = _admode;
      _admode = AdMode.None;
      setActivePlugin(null);
      // Need to set _admode to none before calling process exit admode.
      _player.contextSwitcher.processExitAdModes(mode, true);
    } else {
      setActivePlugin(nextPlugin);
      _activePlugin.onAdModeEntered();
    }
    return true;
  }

  // boolean Manager.insertPluginView()
  // boolean Manager.removePluginView()

  // this should only be called when switchToCast during ad playing. package private on purpose
  void forceExitAdMode() {
    DebugMode.logD(TAG, "forceExitAdMode");
    _admode = AdMode.None;
    if (_activePlugin != null && _activePlugin.getPlayerInterface() != null) {
      _activePlugin.getPlayerInterface().stop();
    }
    _activePlugin = null;
  }

  @Override
  public void reset() {
    if (_activePlugin != null) {
      _activePlugin.reset();
    }
  }

  @Override
  public void suspend() {
    if (_activePlugin != null) {
      _activePlugin.suspend();
    }
  }

  @Override
  public void resume() {
    if (_activePlugin != null) {
      _activePlugin.resume();
    }
  }

  @Override
  public void resume(int timeInMilliSecond, State stateToResume) {
    if (_activePlugin != null) {
      _activePlugin.resume(timeInMilliSecond, stateToResume);
    }
  }

  @Override
  public void destroy() {
    if (_activePlugin != null) {
      _activePlugin.destroy();
    }
  }

  public void onAdModeEntered() {
    if (_activePlugin == null) {
      DebugMode.assertFail(TAG, "enter ad mode when active plugin is null");
      return;
    }
    _activePlugin.onAdModeEntered();
  }

  // helper functions
  private static AdPluginInterface getNextPlugin(
      List<AdPluginInterface> plugins, final AdPluginInterface plugin) {
    if (plugins.size() == 0) {
      return null;
    }

    if (plugin == null) {
      return plugins.get(0);
    }

    DebugMode.assertCondition(plugins.contains(plugin), TAG,
        "the list does not contain plugin " + plugin.toString());
    int index = plugins.indexOf(plugin);
    if (index < 0 || index >= plugins.size() - 1) {
      return null;
    }
    return plugins.get(index + 1);
  }

  /**
   * AdPluginManager is notified by OoyalaPlayer for ad events query plugins
   * whether they need control
   *
   * @return a token when any of the plugins wants control or null if no plugin
   *         needs control
   *
   */
  boolean onAdMode(AdMode mode, int parameter) {
    _parameter = parameter;
    if (_plugins.size() <= 0) {
      return false;
    }

//    if (mode == AdMode.ContentChanged) {
//      resetManager();
//    }

    AdPluginInterface plugin = _plugins.get(0);
    while (plugin != null && !pluginNeedsAdMode(plugin, mode)) {
      plugin = getNextPlugin(_plugins, plugin);
    }

    if (plugin != null) {
      setActivePlugin(plugin);
      _admode = mode;
      return true;
    }

    return false;
  }

  /**
   * ad manager queries plugin whether it needs ad mode
   */
  private boolean pluginNeedsAdMode(AdPluginInterface plugin, AdMode mode) {
    if (plugin == null) {
      DebugMode.assertFail(TAG,
          "plugin method is called when active plugin is null");
      return false;
    }

    switch (mode) {
    case ContentChanged:
      return plugin.onContentChanged();
    case InitialPlay:
      return plugin.onInitialPlay();
    case Playhead:
      return plugin.onPlayheadUpdate(_parameter);
    case CuePoint:
      return plugin.onCuePoint(_parameter);
    case ContentFinished:
      return plugin.onContentFinished();
    case ContentError:
      return plugin.onContentError(_parameter);
    default:
      DebugMode.assertFail(TAG, "request admode when admode is not defined");
      return false;
    }
  }

  public void resetManager() {
    if (inAdMode()) {
      forceExitAdMode();
    }
    if (_activePlugin != null) {
      _activePlugin.destroy();
      _activePlugin = null;
    }
  }

  public AdMode adMode() {
    return _admode;
  }

  public boolean inAdMode() {
    return _activePlugin != null;
  }

  public void resetAds() {
    for (AdPluginInterface p : _plugins) {
      p.resetAds();
    }
  }

  public void skipAd() {
    for (AdPluginInterface p : _plugins) {
      p.skipAd();
    }
  }

  public PlayerInterface getPlayerInterface() {
    if (_activePlugin != null) {
      return _activePlugin.getPlayerInterface();
    }
    return null;
  }

  public AdPluginInterface getActivePlugin() {
    return _activePlugin;
  }

  protected void setActivePlugin(AdPluginInterface plugin) {
    _activePlugin = plugin;
  }

  @Override
  public boolean requestAdMode(AdPluginInterface plugin) {
    if (_activePlugin != null) {
      return false;
    }
    _activePlugin = plugin;
    _admode = AdMode.PluginInitiated;
    return true;
  }

  @Override
  public Set<Integer> getCuePointsInMilliSeconds() {
    Set<Integer> cuePoints = new HashSet<Integer>();
    for (AdPluginInterface plugin : _plugins) {
      cuePoints.addAll(plugin.getCuePointsInMilliSeconds());
    }
    return cuePoints;
  }

}
