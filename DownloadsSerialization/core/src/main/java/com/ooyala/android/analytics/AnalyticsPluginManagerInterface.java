package com.ooyala.android.analytics;

/**
 * The Interface used by an Analytics Plugin Manager. Should not implemented explicitly within applications
 */
public interface AnalyticsPluginManagerInterface {

  /**
   * Register an Analytics Plugin
   *
   * @param plugin
   *          the plugin to be registered
   * @return true on success, false otherwise
   */
  boolean registerPlugin(final AnalyticsPluginInterface plugin);

  /**
   * Deregister an Analytics Plugin
   *
   * @param plugin
   *          the plugin to be deregistered
   * @return true on success, false otherwise
   */
  boolean deregisterPlugin(final AnalyticsPluginInterface plugin);
}
