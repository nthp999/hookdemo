package com.kltn.hookdemo.hooking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.kltn.hookdemo.GetTime;
import com.kltn.hookdemo.MyBroadcastSender;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedBridge.hookAllConstructors;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class CallIntent {
    private static String TAG = "KLTN2021";
    private static boolean WARNING_FLAG = true;
    private static String KEY = null;

    public static void starthookCam_Vid(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // ================================= Intent.class Constructor ==============================================
            findAndHookConstructor(Intent.class, String.class, Uri.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    // Intent action == "android.intent.action.SENDTO"
                    if (!param.args[0].equals("android.intent.action.SENDTO")) {
                        return;
                    }

                    // Send log
                    MyBroadcastSender.brSender(GetTime.time(), "android.content.Intent",
                            "android.intent.action.SENDTO", "PHONE NUMBER: " + param.args[1].toString());
                    MyBroadcastSender.brSender(GetTime.time(), "android.content.Intent",
                            "android.intent.action.SENDTO", "DATA: '" + param.args[0] + "'");
                    Log.d(TAG,"SENDTO: " + param.args[0] + ":" + param.args[1]);
                }
            });

            // =================================== putExtra () =====================================================
            // Hook "putExtra" method - Intent.class
            findAndHookMethod(Intent.class, "putExtra", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    KEY = param.args[0].toString();
                    Log.d(TAG,"KEY: " + KEY);
                }
            });

            // Activity.class - startActivity
            findAndHookMethod(Activity.class,"startActivity", Intent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    Intent result = (Intent) param.args[0];
                    String action = result.getAction();
                    Log.e(TAG, "Action: " + action);

                    if (action.equals("android.intent.action.SENDTO"))
                    {
                        String sms = result.getStringExtra(KEY);
                        if (!sms.contains(LoginActivity.getPsw()))
                            return;
                        if (WARNING_FLAG)
                            param.setResult(replaceHookedMethod(param));

                        Log.d(TAG, "StartActivity; " + sms);
                        WARNING_FLAG = true;
                    }
                    else if (action.equals("android.intent.action.SET_ALARM")) {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Set Alarm", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.app.Activity",
                                "startActivity" , "Set Alarm");
                        Log.d(TAG, "startActivity");
                    }
                    else if (action.equals("android.intent.action.VIEW")) {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: View Calendar", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.app.Activity",
                                "startActivity" , "View Calendar");
                        Log.d(TAG, "startActivity");
                    }
                }

                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    Intent result = (Intent) param.args[0];
                    String sms = result.getStringExtra(KEY);

                    ActivityContext.getCurrentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Show AlertDialog
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(ActivityContext.getCurrentActivity());
                            builder1.setTitle("Warning");
                            builder1.setMessage("Your message contains sensitive data. Do you want to call 'startActivity()' and send message?");
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "Allow",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "[XPOSED][Allow]: Send sensitive sms", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "Activity.class", "startActivity",
                                                    "[ALLOW]" + "DATA: '" + sms + "'");
                                            WARNING_FLAG = false;
                                            ActivityContext.getCurrentActivity().startActivity((Intent) param.args[0]);
                                        }
                                    });

                            builder1.setNegativeButton(
                                    "Block",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(), "[XPOSED][Block]: Block sensitive sms", Toast.LENGTH_SHORT).show();
                                            MyBroadcastSender.brSender(GetTime.time(), "Activity.class", "startActivity",
                                                    "[BLOCK]" + "DATA: '" + sms + "'");
                                            param.setResult(null);
                                            WARNING_FLAG = true;
                                        }
                                    });

                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    });
                    return true;
                }
            });
        }
        catch (Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
        }

        // =====================================
        try {
            findAndHookMethod(Activity.class,"startActivityForResult", Intent.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Intent result = (Intent) param.args[0];
                    String action = result.getAction();

                    if (action == "android.media.action.IMAGE_CAPTURE") {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Open Camera (Image Capture)", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.app.Activity",
                                "startActivityForResult", "Open Camera (Image Capture)");
                        Log.d(TAG, "startActivityForResult");
                    }
                    else if (action == "android.media.action.VIDEO_CAPTURE") {
                        Toast.makeText(ActivityContext.getCurrentActivity().getApplicationContext(),
                                "[XPOSED] [WARNING]: Open Camera (Video Capture)", Toast.LENGTH_SHORT).show();
                        MyBroadcastSender.brSender(GetTime.time(), "android.app.Activity",
                                "startActivityForResult", "Open Camera (Video Capture)");
                        Log.d(TAG, "startActivityForResult");
                    }
                }
            });
        } catch (Exception e) {
            XposedBridge.log("ERROR: " + e.getMessage());
        }
    }
}
