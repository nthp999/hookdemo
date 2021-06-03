package com.kltn.hookdemo;

import android.app.AndroidAppHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static String KEY = "xlog";
    private DatabaseSupport db;

    //Code thi hành khi Receiver nhận được Intent
    @Override
    public void onReceive(Context context, Intent intent) {
        db = new DatabaseSupport(context);
        Log.d("KLTN2021 " ,"==> Broadcast Receiver");
        String data = intent.getStringExtra(KEY);
        Log.d("KLTN2021 " ,"Data received:  " + data);

        if (db.addLog("sendSMS", "Data received: " + data) > 0)
        {
            Log.e("KLTN2021_dtb ", "Success");
        }
    }

}


