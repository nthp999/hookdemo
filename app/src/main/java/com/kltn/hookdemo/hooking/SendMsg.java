package com.kltn.hookdemo.hooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;


import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import com.kltn.hookdemo.MyBroadcastSender;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class SendMsg {
    private static boolean warning = true;
    private final String TAG = "KLTN2021";

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam, XSharedPreferences xprefs) {
        //option = xprefs.getString("SMS", "");

        // ===================== HOOK SMS WITHOUT USING INTENT =========================
        try
        {
            final Class clazz = XposedHelpers.findClass("android.telephony.SmsManager", lpparam.classLoader);
            Method send = clazz.getMethod("sendTextMessage", String.class, String.class,
                    String.class, PendingIntent.class, PendingIntent.class);

            XposedBridge.hookMethod(send, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String dst = param.args[0].toString();
                    String sms = param.args[2].toString();

                    Log.d(TAG, "android.telephony.SmsManager：sendTextMessage");
                    Log.d(TAG, "destinationAddress：" + dst);
                    Log.d(TAG, "text：" + sms);

                    // Return if it not contains sensitive data
                    if (!sms.contains(LoginActivity.getPsw())) {
                        return;
                    }

                    // Send SMS data to BroadcastReceiver
                    MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                            "sendTextMessage", "Message contains sensitive data");

                    if (warning)
                        param.setResult(replaceHookedMethod(param));


                    warning = true;
                }

                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    String dst = param.args[0].toString();
                    String sms = param.args[2].toString();

                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "In Replace");

                            //Show AlertDialog
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                            builder1.setTitle("Warning");
                            builder1.setMessage("Your message contains sensitive data. Do you want to send?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [ALLOW]: Send sensitive sms", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                                                    "sendTextMessage", "[ALLOW]" + dst + ":" + sms);

                                            warning = false;
                                            SmsManager.getDefault().sendTextMessage(param.args[0].toString(), null, param.args[2].toString(), null, null);

                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Block",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [BLOCK]: Block sensitive sms", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                                                    "sendTextMessage", "[BLOCK]" + dst + ":" + sms);
                                            warning = true;
                                            param.setResult(null);
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    });
                    return true;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "sendTextMessage: " + " ERROR: " + e.getMessage());
        }


    }

   /* private static void write (String msg){
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
    }*/

}
