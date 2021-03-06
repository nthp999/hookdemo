package com.kltn.hookdemo;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class MyBroadcastSender {
    private static String KEY = "xlog";

    public static void brSender (String time, String c_class, String method, String message){
        Context context = (Context) AndroidAppHelper.currentApplication();
        ArrayList<String> data = new ArrayList<String>();

        if (!data.isEmpty())
            data.clear();

        data.add(time);
        data.add(c_class);
        data.add(method);
        data.add(message);

        try {
            Intent i = new Intent();
            i.setAction("com.kltn.CUSTOM_INTENT");
            i.putStringArrayListExtra(KEY, data);
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(i);
        }
        catch (Exception e){
            Log.e("KLTN2021 ", e.getMessage()); }
    }

}
