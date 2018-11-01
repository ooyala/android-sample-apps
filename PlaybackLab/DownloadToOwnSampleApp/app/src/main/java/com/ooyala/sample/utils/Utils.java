package com.ooyala.sample.utils;

import android.content.Context;

import com.ooyala.android.util.DebugMode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
  private static final String TAG = Utils.class.getSimpleName();

  /**
   * Retrieves the given file and converts it to a string
   *
   * @param context android context
   * @param fileName file to retrieve
   * @return a String with the file contents
   */
  public static String fileToString(Context context, String fileName) {
    final String SEPARATOR = System.getProperty("line.separator");
    String ret = "";
    try {
      InputStream inputStream = context.openFileInput(fileName);
      if (inputStream != null) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();
        while ((receiveString = bufferedReader.readLine()) != null) {
          stringBuilder.append(receiveString);
          stringBuilder.append(SEPARATOR);
        }
        inputStream.close();
        ret = stringBuilder.toString();
      }
    } catch (FileNotFoundException e) {
      DebugMode.logW(TAG, fileName + " not found: " + e.getMessage(), e);
    } catch (IOException e) {
      DebugMode.logW(TAG, fileName + " IOException: " + e.getMessage(), e);
    }
    return ret;
  }

  /**
   * Used to log long string
   *
   * @param data data to log
   * @param tag tag to be used
   */
  public static void logLongString(String data, String tag) {
    final int MAX_LOG_SIZE = 1000;
    for(int i = 0; i <= data.length() / MAX_LOG_SIZE; i++) {
      int start = i * MAX_LOG_SIZE;
      int end = (i + 1) * MAX_LOG_SIZE;
      end = end > data.length() ? data.length() : end;
      DebugMode.logI(tag, data.substring(start, end));
    }
  }
}
