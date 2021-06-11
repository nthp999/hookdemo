package com.kltn.hookdemo.hooking;

import android.util.Log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LoginActivity {

    private static String username = null;
    private static String psw = null;

    public void starthook(XC_LoadPackage.LoadPackageParam lpparam) throws NoSuchMethodException {
        XposedBridge.log("/data/data/com.example.demoappkltn loaded");

        try{
            XC_MethodHook info = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    username = param.args[0].toString();
                    psw = param.args[1].toString();

                    Log.d("KLTN2021 ", username + ":" + psw);
                }
            };

            XposedHelpers.findAndHookMethod("com.example.demoappkltn.DatabaseSupport", lpparam.classLoader,
                    "check_User", String.class, String.class, info);

        } catch (Exception e){
            Log.d("KLTN2021", "check_User method: " + " ERROR: " + e.getMessage());
        }
    }

    public static String getPsw (){
        return psw;
    }
}
