package com.ooyala.sample.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bsondur on 1/11/16.
 */
public class BasicPlaybackSampleAppLog {

    public void writeToSdcardLog(int count, String text) {

        //Empty Logcat buffer
        if (count == 1) {

            try {
                Process process = new ProcessBuilder().command("logcat", "-c").redirectErrorStream(true).start();
            } catch (IOException e) {
            }
        }

        //Writing events into file on device
        File logFile = new File("sdcard/log.file");
        if (!logFile.exists()) {
            //try {
            //    //logFile.createNewFile();
            //} catch (IOException e) {
            //    // TODO Auto-generated catch block
            //    e.printStackTrace();
            //}
        }// end of if
        else if (logFile.exists()) {
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }// end of else if

    }// end of writeToSdcardLog function


}

