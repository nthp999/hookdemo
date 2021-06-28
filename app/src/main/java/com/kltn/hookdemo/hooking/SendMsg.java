package com.kltn.hookdemo.hooking;

import android.app.AlertDialog;
import android.app.PendingIntent;
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
import java.net.URI;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class SendMsg {
    private static String option = "Warning";
    private static String DESTINATION = null;
    private static String DATA = null;
    private static String intentDATA = null;
    private static String TAG = "KLTN2021";
    private static boolean SAFE = true;

    private MyBroadcastSender mybrSender = new MyBroadcastSender();

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam, XSharedPreferences xprefs) {
        //option = xprefs.getString("SMS", "");

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

                    // Return if it not contains sensitive data
                    if (!DATA.contains(LoginActivity.getPsw())) {
                        SAFE = true;            // Safe
                        return;
                    }

                    SAFE = false;               // Unsafe
                    Log.d(TAG, "SAFE Status: " + SAFE);

                    switch (option) {
                        case "Allow":   // Allow, display Toast message
                        {
                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "[XPOSED][Allow]: Send sensitive sms", Toast.LENGTH_SHORT).show();
                            option = "Warning";
                            break;
                        }
                        case "Block":   // Block, intercept call function
                        {
                            param.setResult(null);
                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "[XPOSED][Block]: Send sensitive sms", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case "Warning":   // Warning, display dialog
                        {
                            Log.d(TAG, "In case 3");
                            param.setResult(replaceHookedMethod(param));
                            break;
                        }
                        default:
                            break;
                    }

                    // Send data to BroadcastReceiver
                    mybrSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                            "sendTextMessage", "Message contains sensitive data");
                    mybrSender.brSender(GetTime.time(), "android.telephony.SmsManager",
                            "sendTextMessage", DESTINATION + ":" + DATA);


                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                }

                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "[XPOSED][Warning]: Send sensitive sms", Toast.LENGTH_SHORT).show();

                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "In Replace");

                            //Show AlertDialog
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                            builder1.setTitle("Warning");
                            builder1.setMessage("Your sms contains sensitive data. Do you want send SMS?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            option = "Allow";
                                            SmsManager.getDefault().sendTextMessage(param.args[0].toString(), null, param.args[2].toString(), null, null);

                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Block",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
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


        // ================ HOOK SMS USING INTENT ==================
        try {
            // Hook Construtor of Intent.class
            findAndHookConstructor(
                    Intent.class,
                    String.class,
                    Uri.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            // Send to br_recv if intent action equal "android.intent.action.SENDTO"
                            if (param.args[0].equals("android.intent.action.SENDTO")) {
                                mybrSender.brSender(GetTime.time(), "android.content.Intent",
                                        "android.intent.action.SENDTO", "ACTION: " + param.args[0]);

                                mybrSender.brSender(GetTime.time(), "android.content.Intent",
                                        "android.intent.action.SENDTO", "PHONE NUMBER: " + param.args[1].toString());

                                Log.d(TAG, param.args[0].toString());


                            }
                        }
                    });

            // Hook "putExtra" method - Intent.class
            findAndHookMethod(
                    Intent.class,
                    "putExtra",
                    String.class,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            intentDATA = param.args[1].toString();
                            if (intentDATA.contains(LoginActivity.getPsw())) {
                                SAFE = false;
                                Log.d(TAG, "SAFE Status: " + SAFE);
                                param.setResult(null);

                                ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "SENTO Toast");
                                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "WARNING: Intent_SENDTO", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                mybrSender.brSender(GetTime.time(), "android.content.Intent",
                                        "putExtra", "DATA: '" + param.args[1].toString() + "'");

                                Log.d(TAG, param.args[0].toString() + " : " + param.args[1].toString());
                            }
                        }
                    });
        }
        catch (Exception e) {
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
