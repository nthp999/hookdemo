package com.kltn.hookdemo.hooking;

import android.app.PendingIntent;
import android.util.Log;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;


import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class SendMsg {
    private static String DESTINATION = null;
    private static String DATA = null;
    private static String TAG = "KLTN2021";
    private static boolean SAFE = true;
    //Context context = (Context) AndroidAppHelper.currentApplication();
    //Thread t = new Thread();

    private MyBroadcastSender mybrSender = new MyBroadcastSender();

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
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

                        mybrSender.brSender(GetTime.time(),
                                "sendTextMessage",
                                "Send SMS: Message contains your password value");

                        mybrSender.brSender(GetTime.time(),
                                "sendTextMessage",
                                "SMS DATA: " + DATA + " / DesAddr: " + DESTINATION);
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
    }

    public void starthook_Intent(XC_LoadPackage.LoadPackageParam lpparam) {
        final Class<?> intent = findClass("android.content.Intent", lpparam.classLoader);

        try {
            hookAllConstructors(intent, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (param.args.length != 1)
                        return;

                    if (param.args[0].equals("android.intent.action.SENDTO")) {
                        mybrSender.brSender(GetTime.time(),
                                "android.content.Intent",
                                "Action: " + param.args[0]);
                        Log.e(TAG, param.args[0].toString() + " : " + param.args[1].toString());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "android.content.Intent: " + " ERROR: " + e.getMessage());
        }

        findAndHookMethod(
                intent,
                "putExtra",
                String.class,
                String.class,
                lpparam.classLoader,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Log.e(TAG, param.args[0].toString() + " : "+ param.args[1].toString());
                    }
                });
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
