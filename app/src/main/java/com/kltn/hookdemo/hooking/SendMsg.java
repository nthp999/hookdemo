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
    private static boolean WARNING_FLAG = true;
    private static final String TAG = "KLTN2021";

    public static void starthook(XC_LoadPackage.LoadPackageParam lpparam) {
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
                    if (!sms.contains(LoginActivity.getPsw()))
                        return;

                    // Show AlertDialog if warning = true
                    if (WARNING_FLAG)
                        param.setResult(replaceHookedMethod(param));

                    WARNING_FLAG = true;
                }

                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    String dst = param.args[0].toString();
                    String sms = param.args[2].toString();

                    MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                            "sendTextMessage", "Message contains sensitive data");

                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show AlertDialog
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                            builder1.setTitle("Warning");
                            builder1.setMessage("Your message contains sensitive data. Do you want to continue?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [ALLOW]: Send sensitive message", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                                                    "sendTextMessage", "[ALLOW]" + dst + ":" + sms);

                                            WARNING_FLAG = false;
                                            SmsManager.getDefault().sendTextMessage(param.args[0].toString(), null, param.args[2].toString(), null, null);
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Block",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                                    "[XPOSED] [BLOCK]: Block sensitive message", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                                                    "sendTextMessage", "[BLOCK]" + dst + ":" + sms);
                                            WARNING_FLAG = true;
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
            Log.e("KLTN2021", "ERROR: " + e.getMessage());
        }


    }
}
