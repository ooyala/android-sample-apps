package com.ooyala.demo.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {

    private static final String TAG = IOUtils.class.getSimpleName();

    public static void copy(InputStream stream, String path) throws IOException {

        final File file = new File(path);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        final File parentFile = file.getParentFile();

        if (!parentFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            parentFile.mkdir();
        }

        if (file.exists()) {
            return;
        }

        OutputStream myOutput = new FileOutputStream(path, false);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = stream.read(buffer)) >= 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        stream.close();

    }

    public static byte[] readBytes(InputStream stream) throws IOException {

        try {
            ByteArrayOutputStream myOutput = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = stream.read(buffer)) >= 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            stream.close();
            return myOutput.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return new byte[0];
    }

    public static String[] read(InputStream stream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = stream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            stream.close();
            return byteArrayOutputStream.toString().split("\n");
        } finally {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        }
    }

    public static String toString(InputStream inputStream) {
        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return total.toString();
    }


}
