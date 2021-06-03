package com.kltn.hookdemo.hooking;

import android.app.AndroidAppHelper;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kltn.hookdemo.DatabaseSupport;
import com.kltn.hookdemo.MyBroadcastSender;
import com.kltn.hookdemo.SaveLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SendMsg {
    private static String DESTINATION = null;
    private static String DATA = null;
    private static boolean CHECK = false;


    private MyBroadcastSender mybrSender = new MyBroadcastSender();

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            final Class clazz = XposedHelpers.findClass("android.telephony.SmsManager",lpparam.classLoader);
            Method send = clazz.getMethod("sendTextMessage",String.class, String.class,
                                                String.class, PendingIntent.class, PendingIntent.class);


            XposedBridge.hookMethod(send, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //super.beforeHookedMethod(param);
                    DESTINATION = param.args[0].toString();
                    DATA = param.args[2].toString();

                    Log.d("KLTN2021 ","android.telephony.SmsManager：sendTextMessage");
                    Log.d("KLTN2021 ","destinationAddress："+ DESTINATION);
                    Log.d("KLTN2021 ","text："+ DATA);

                    if (DATA.contains(LoginActivity.getPsw())) {
                        CHECK = true;
                        Log.e("KLTN2021 ", "Status: " + CHECK);

                        param.setResult(null);

                        mybrSender.brSender(DATA + " contains sensitive value!");

                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });
        } catch (Exception e) {
            Log.e("KLTN2021", "sendTextMessage: " + " ERROR: " + e.getMessage());
        }
    }

    private static void write (String msg){
        FileWriter fWriter;
        File logFile = new File("/data/data/com.kltn.hookdemo/123.txt");
        Log.i("KLTN2021 ", "/data/data/com.kltn.hookdemo/123.txt");
        if (!logFile.exists()) {
            return;
        }
        try {
            fWriter = new FileWriter(logFile, true);
            fWriter.write(msg);
            fWriter.flush();
            fWriter.close();
        } catch(Exception e){
            Log.e("KLTN2021 ", "ERROR: " + e.getMessage());
        }
    }

}
