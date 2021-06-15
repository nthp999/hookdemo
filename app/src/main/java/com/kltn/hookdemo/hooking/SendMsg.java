package com.kltn.hookdemo.hooking;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;


import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class SendMsg {
    private static String DESTINATION = null;
    private static String DATA = null;
    private static String TAG = "KLTN2021";
    private static boolean SAFE = true;

    private MyBroadcastSender mybrSender = new MyBroadcastSender();

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) {

        // ============== HOOK SMS WITHOUT USING INTENT ================
        try
        {
            final Class clazz = XposedHelpers.findClass("android.telephony.SmsManager", lpparam.classLoader);
            Method send = clazz.getMethod("sendTextMessage", String.class, String.class,
                    String.class, PendingIntent.class, PendingIntent.class);

            XposedBridge.hookMethod(send, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    DESTINATION = param.args[0].toString();
                    DATA = param.args[2].toString();

                    Log.d(TAG, "android.telephony.SmsManager：sendTextMessage");
                    Log.d(TAG, "destinationAddress：" + DESTINATION);
                    Log.d(TAG, "text：" + DATA);

                    if (DATA.contains(LoginActivity.getPsw())) {
                        SAFE = false;
                        Log.d(TAG, "SAFE Status: " + SAFE);
                        param.setResult(null);

                        mybrSender.brSender(
                                GetTime.time(),
                                "sendTextMessage",
                                "Message contains sensitive data");

                        mybrSender.brSender(
                                GetTime.time(),
                                "sendTextMessage",
                                DESTINATION + ":" + DATA);
                    }

                    SAFE = true;
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "sendTextMessage: " + " ERROR: " + e.getMessage());
        }


        // ================ HOOK SMS USING INTENT ==================
        try {
            findAndHookConstructor(
                    Intent.class,
                    String.class,
                    Uri.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (param.args[0].equals("android.intent.action.SENDTO")) {
                                mybrSender.brSender(
                                        GetTime.time(),
                                        "android.intent.action.SENDTO",
                                        "ACTION: " + param.args[0]);

                                mybrSender.brSender(
                                        GetTime.time(),
                                        "android.intent.action.SENDTO",
                                        "PHONE NUMBER: " + param.args[1].toString());

                                Log.d(TAG, param.args[0].toString());

                                findAndHookMethod(
                                        Intent.class,
                                        "putExtra",
                                        String.class,
                                        String.class,
                                        new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                                super.beforeHookedMethod(param);

                                                mybrSender.brSender(
                                                        GetTime.time(),
                                                        "android.intent.action.SENDTO",
                                                        "DATA: " + param.args[1].toString());

                                                Log.e(TAG, param.args[0].toString() + " : "+ param.args[1].toString());
                                            }
                                        });
                            }
                        }
                    });
        } catch (Exception e){
            Log.e(TAG, "ERROR: " + e.getMessage());
        }




    }


    private static void write (String msg){
        FileWriter fWriter;
        File logFile = new File("/data/data/com.kltn.hookdemo/123.txt");
        Log.i(TAG, "/data/data/com.kltn.hookdemo/123.txt");
        if (!logFile.exists()) {
            return;
        }
        try {
            fWriter = new FileWriter(logFile, true);
            fWriter.write(msg);
            fWriter.flush();
            fWriter.close();
        } catch(Exception e){
            Log.e(TAG, "ERROR: " + e.getMessage());
        }
    }

}
