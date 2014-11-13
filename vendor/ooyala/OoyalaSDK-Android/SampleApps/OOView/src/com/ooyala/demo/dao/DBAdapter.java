package com.ooyala.demo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {
    private static final String TAG = "DBAdapter";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ooyala_db";
    private static final String TABLE_WATCH_LIST = "watch_list";
    private static final String FIELD_API_KEY = "api_key";
    private static final String FIELD_EMBED_CODE = "embed_code";

    private static final String SQL_DROP_TEMPLATE = "DROP TABLE IF EXISTS %s";
    private static final String SQL_CREATE_WATCH_LIST_TABLE = String.format(
            "CREATE TABLE \"%s\" (\n" +
                    "\t \"%s\" text NOT NULL,\n" +
                    "\t \"%s\" text NOT NULL\n" +
                    ")", TABLE_WATCH_LIST, FIELD_API_KEY, FIELD_EMBED_CODE);

    private static final String SQL_DELETE_TABLE_DELETE = String.format(SQL_DROP_TEMPLATE, TABLE_WATCH_LIST);

    private final DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }


    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_WATCH_LIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.d(TAG, String.format("Upgrading database from version %d to %d, which will destroy all old data", oldVersion, newVersion));
            db.execSQL(SQL_DELETE_TABLE_DELETE);
            onCreate(db);
        }
    }

    public List<String> findWatchList(String apiKey) {
        List<String> embedCodes = new ArrayList<String>();
        if (db == null || !db.isOpen()) {
            open();
        }
        try {
            assert db != null;
            final Cursor cursor = db.query(TABLE_WATCH_LIST, new String[]{FIELD_EMBED_CODE}, FIELD_API_KEY + " = ?", new String[]{apiKey},
                    null,
                    null,
                    null);

            while (cursor.moveToNext()) {
                String embedCode = cursor.getString(0);
                embedCodes.add(embedCode);
            }
            cursor.close();
            return embedCodes;
        } finally {
            close();
        }

    }

    public void addToWatchList(String apiKey, String embedCode) {
        if (db == null || !db.isOpen()) {
            open();
        }
        try {
            assert db != null;
            final ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_API_KEY, apiKey);
            contentValues.put(FIELD_EMBED_CODE, embedCode);

            db.insert(TABLE_WATCH_LIST, null, contentValues);
        } finally {
            close();
        }

    }

    public void removeToWatchList(String apiKey, String embedCode) {
        if (db == null || !db.isOpen()) {
            open();
        }
        try {
            assert db != null;
            final ContentValues contentValues = new ContentValues();
            contentValues.put(FIELD_API_KEY, apiKey);
            contentValues.put(FIELD_EMBED_CODE, embedCode);

            db.delete(TABLE_WATCH_LIST, FIELD_API_KEY + " = ? AND " + FIELD_EMBED_CODE + " = ?", new String[]{apiKey, embedCode});
        } finally {
            close();
        }

    }

    public boolean existsInWatchList(String apiKey, String embedCode) {
        if (db == null || !db.isOpen()) {
            open();
        }
        try {
            assert db != null;
            final Cursor cursor = db.query(TABLE_WATCH_LIST, new String[]{FIELD_EMBED_CODE}, FIELD_API_KEY + " = ? AND " + FIELD_EMBED_CODE + " = ?", new String[]{apiKey, embedCode},
                    null,
                    null,
                    null);

            try {
                if (cursor.moveToNext()) {
                    return true;
                }
            } finally {
                cursor.close();
            }
        } finally {
            close();
        }

        return false;
    }


    //---opens the database---
    private DBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        db.setLockingEnabled(true);
        return this;
    }

    //---closes the database---
    private void close() {
        try {
            dbHelper.close();
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


}
