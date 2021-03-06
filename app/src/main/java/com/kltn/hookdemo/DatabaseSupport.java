package com.kltn.hookdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseSupport extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="logs.db";
    public static final String TABLE_NAME ="logs_info";
    /*public static final String COL_ID ="ID";
    public static final String COL_TIME ="Time";
    public static final String COL_EVENT ="Event";
    public static final String COL_MSG ="Message";*/

    // Construstor of Database
    public DatabaseSupport(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE logs_info (ID INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT, class TEXT, method_action TEXT, message TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int a, int b) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(sqLiteDatabase);
    }

    // Add data into database
    public long addLog(String time, String c_class, String method, String msg) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", time);
        contentValues.put("class", c_class);
        contentValues.put("method_action", method);
        contentValues.put("message", msg);

        long res = db.insert("logs_info", null, contentValues);

        db.close();
        return res;
    }

    // Get data from database
    public ArrayList<String> getLog() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> logList = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                logList.add("[" + cursor.getString(1) + "]"
                        + " " + cursor.getString(2)
                        + " " + cursor.getString(3)
                        + " " + cursor.getString(4) +'\n');
            } while (cursor.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursor.close();
        return logList;

    }
}