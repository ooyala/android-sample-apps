package com.ooyala.android.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by pbq on 1/24/16.
 */
public class SDCardLogcatOoyalaEventsLogger {
    private int count;

    public SDCardLogcatOoyalaEventsLogger(){
        count=0;
    }

    public void writeToSdcardLog(String text) {

        // Keeps track of incoming notifications and makes sure count is right
        count=count+1;
        text=text+" count:"+count;

        //Writing events into file on device if the file already exists , do nothing if file does not exist
        File logFile = new File("sdcard/log.file");
        if (logFile.exists()) {
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }// end of else if

    }// end of writeToSdcardLog function
}
