package com.ooyala.android;

import com.ooyala.android.util.WeakReferencePassThroughEquals;

import java.util.HashSet;
import java.util.Set;

/**
 * Connect objects which can read ID3 tags from asset streams,
 * to objects that want to perform some action (e.g. update analytics)
 * when an ID3 tag is seen.
 */
public class ID3TagNotifier {

  private static final ID3TagNotifier s_instance = new ID3TagNotifier();
  public static final ID3TagNotifier s_getInstance() { return s_instance; }

  public static interface ID3TagNotifierListener {
    /**
     * Not guaranteed to be called on the main UI thread.
     * When you implement this, consider posting a runnable on the main UI thread
     * to do the actual work.
     */
    void onTag(final byte[] tag );

    void onPrivateMetadata(final String owner, final byte[] privateMetadata);

    void onTxxxMetadata(final String description, final String value);

    void onGeobMetadata(final String mimeType, final String filename, final String description, final byte[] data);
  }

  private final Set<WeakReferencePassThroughEquals<ID3TagNotifierListener>> listeners;

  public ID3TagNotifier() {
    this.listeners = new HashSet<WeakReferencePassThroughEquals<ID3TagNotifierListener>>();
  }

  public void addWeakListener( ID3TagNotifierListener listener ) {
    synchronized( listeners ) {
      listeners.add( new WeakReferencePassThroughEquals<ID3TagNotifierListener>(listener) );
    }
  }

  public void removeWeakListener( ID3TagNotifierListener listener ) {
    synchronized( listeners ) {
      listeners.remove(new WeakReferencePassThroughEquals<ID3TagNotifierListener>(listener));
    }
  }

  public void onTag(final byte[] tag ) {
    synchronized( listeners ) {
      for( WeakReferencePassThroughEquals<ID3TagNotifierListener> wl : listeners ) {
        final ID3TagNotifierListener l = wl.get();
        if( l != null ) {
          l.onTag( tag );
        }
      }
    }
  }

  public void onPrivateMetadata(final String owner, final byte[] privateMetadata) {
    synchronized( listeners ) {
      for( WeakReferencePassThroughEquals<ID3TagNotifierListener> wl : listeners ) {
        final ID3TagNotifierListener l = wl.get();
        if( l != null ) {
          l.onPrivateMetadata(owner, privateMetadata);
        }
      }
    }
  }

  public void onTxxxMetadata(final String description, final String value) {
    synchronized( listeners ) {
      for( WeakReferencePassThroughEquals<ID3TagNotifierListener> wl : listeners ) {
        final ID3TagNotifierListener l = wl.get();
        if( l != null ) {
          l.onTxxxMetadata(description, value);
        }
      }
    }
  }

  public void onGeobMetadata(final String mimeType, final String filename, final String description, final byte[] data) {
    synchronized( listeners ) {
      for( WeakReferencePassThroughEquals<ID3TagNotifierListener> wl : listeners ) {
        final ID3TagNotifierListener l = wl.get();
        if( l != null ) {
          l.onGeobMetadata(mimeType, filename, description, data);
        }
      }
    }
  }
}
