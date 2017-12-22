package com.ooyala.android.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import android.content.Context;

/**
 * This is a simple helper around a Java File, with the file parent directory always being
 * the internal cache dir of the given Context.
 */
final public class TemporaryInternalStorageFile {

  private final File tmpFile;

  /**
   * Please create these via TemporaryInternalStorageFileManager to avoid file name collisions.
   * @see TemporaryInternalStorageFileManager#next
   */
  public TemporaryInternalStorageFile( final Context context, final String prefix, final String ext ) throws IOException {
    final File dir = context.getCacheDir();
    tmpFile = File.createTempFile( prefix, ext, dir );
    if( tmpFile != null && ! tmpFile.exists() ) {
      tmpFile.createNewFile();
    }
  }

  public File getFile() {
    return tmpFile;
  }

  public String getAbsolutePath() {
    return tmpFile == null ? "" : tmpFile.getAbsolutePath();
  }

  public void write( final String body ) throws FileNotFoundException {
    if( tmpFile != null ) {
      final PrintWriter pw = new PrintWriter( tmpFile );
      pw.write( body );
      pw.flush();
      pw.close();
    }
  }
}