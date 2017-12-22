package com.ooyala.android;

/**
 * An object that is passed from the OoyalaPlayer to the all Observers that contains notification name and data.
 * This class helps provide contextual information in Observer updates.
 */
public class OoyalaNotification {

  /**
   * Help parse the Object event types.
   * @see java.util.Observer#update(Observable, Object)
   * @param arg the second argument passed to the update method.
   * @return "UNKNOWN" if arg is not an instance of OoyalaNotification, otherwise the name of the notification.
   */
  public static final String getNameOrUnknown( Object arg ) {
    String name = UNKNOWN_NOTIFICATION_NAME;
    if( arg instanceof OoyalaNotification ) {
      name = ((OoyalaNotification)arg).getName();
    }
    return name;
  }

  public static final String UNKNOWN_NOTIFICATION_NAME = "UNKNOWN";
  public static final String OLD_STATE_KEY = "oldState";
  public static final String NEW_STATE_KEY = "newState";

  private final String name;
  private final Object data;

  /**
   * Instantiate an OoyalaNotification with just a name, and no data.
   * @param notificationName non-null string, one of the OoyalaPlayer.*_NOTIFICATION_NAMEs.
   */
  public OoyalaNotification(String notificationName) {
    this( notificationName, null );
  }

  /**
   * Instantiate an OoyalaNotification with a name and associated data.
   * @param notificationName non-null string, one of the OoyalaPlayer.*_NOTIFICATION_NAMEs.
   * @param data possibly null notification-specific data.
   */
  public OoyalaNotification(String notificationName, Object data) {
    this.name = notificationName;
    this.data = data;
  }

  /**
   * Get the name of the notification.
   * @return the 'notificationName' originally passed in to the constructor.
   * @see #OoyalaNotification(String)
   * @see #OoyalaNotification(String, Object)
   */
  public String getName() {
    return name;
  }

  /**
   * Get the data that is provided within this notification.
   * @return possibly null data originally passed in to the constructor.
   */
  public Object getData() {
    return data;
  }

  @Override
  public String toString() {
    return "[" + getClass().getSimpleName() + "@" + hashCode() + ":name=" + name + ";data=" + (data==null?"<null>":data) + "]";
  }
}
