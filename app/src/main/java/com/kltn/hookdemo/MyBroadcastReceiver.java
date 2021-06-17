package com.kltn.hookdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MyBroadcastReceiver extends BroadcastReceiver {
    final private static String KEY = "xlog";
    private DatabaseSupport db;

    //Receiver listens and receives data in background
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseSupport(context);
        //Log.d("KLTN2021 " ,"==> Broadcast Receiver");
        ArrayList<String> data;
        data = intent.getStringArrayListExtra(KEY);
        Log.d("KLTN2021 " ,"DATA RECEIVED:  " + data);

        // Call function to add data to database
        if (db.addLog(data.get(0), data.get(1), data.get(2), data.get(3)) > 0)
        {
            Log.i("KLTN2021 ", "Success");
        }
    }

}


