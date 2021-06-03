package com.kltn.hookdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseSupport extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="register.db";
    public static final String TABLE_NAME ="register_user";
    public static final String COL_ID ="ID";
    public static final String COL_TIME ="Time";
    public static final String COL_EVENT ="Event";
    public static final String COL_MSG ="Message";

    // Construstor of Database
    public DatabaseSupport(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE log (ID INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, password TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int a, int b) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME );
        onCreate(sqLiteDatabase);
    }

    public long addLog(/*String time, String event, String msg*/) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("time", "1");
        contentValues.put("event", "2");
        contentValues.put("message", "3");

        long res = db.insert("register_user", null, contentValues);
        db.close();
        return res;
    }

    /*public boolean getLog() {
        String[] columns = { COL_1 };
        SQLiteDatabase db = getReadableDatabase();
        String section = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] sectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_NAME, columns, section, sectionArgs, null, null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        if(count>0)
            return true;
        else
            return false;

    }*/
}
