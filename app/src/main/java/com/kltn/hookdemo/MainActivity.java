package com.kltn.hookdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Button btnShowlog;
    TextView tvLog;
    DatabaseSupport db;

    String TAG = "KLTN2021";
    BroadcastReceiver exampleBroadcastReceiver;
    SharedPreferences pref;

    @SuppressLint({"WorldReadableFiles", "SetWorldReadable"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        /*pref = this.getSharedPreferences("user_setting", Context.MODE_WORLD_READABLE);
        File prefsFile = new File("/data/data/com.kltn.hookdemo/shared_prefs/user_setting.xml");
        prefsFile.setReadable(true, false);         // Set readable
        if (prefsFile.exists() == false)
            Log.e(TAG, "File does not exsit");*/

        btnShowlog = (Button) findViewById(R.id.btn_showlog);
        tvLog = (TextView) findViewById(R.id.tv_log);
        tvLog.setMovementMethod(new ScrollingMovementMethod());

        // ==================== REGISTER BROADCAST RECEIVER =====================
        // Create br_recv object
        // and register to BroadcastReceiver
        exampleBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.kltn.CUSTOM_INTENT");
        registerReceiver(exampleBroadcastReceiver, filter);

        // ============================ BUTTON ==============================
        // Button Show Log: Call ShowLog.class
        btnShowlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = new DatabaseSupport(MainActivity.this);
                ArrayList<String> list = db.getLog();
                tvLog.setText(null);
                for (int i = 0; i < list.size(); i++)
                    tvLog.append(list.get(i));

                /*Intent intent = new Intent(MainActivity.this, ShowLogActivity.class);
                startActivity(intent);*/
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (exampleBroadcastReceiver != null)
            unregisterReceiver(exampleBroadcastReceiver);
    }

/*
    // setOnitemClickListener event
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Toast.makeText(getApplicationContext(), "Clicked" + position, Toast.LENGTH_SHORT ).show();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Options " + lv_mainitem[position]);       // Set title of AlertDialog
        builder1.setIcon(R.drawable.icon);                           // Set icon
        builder1.setCancelable(false);
        builder1.setSingleChoiceItems(
                options,                                             // Items list
                0,                                        // Index of checked item (0 = selection 1st item)
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG,options[which]);

                        // Update view
                        lv_subitem[position] = options[which];
                        listView.setAdapter(customAdapter);

                        // Store setting to .xml
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(lv_mainitem[position], lv_subitem[position]);
                        editor.commit();

                        Log.e(TAG, lv_mainitem[position] + ": "+ lv_subitem[position]);

                        dialog.dismiss();
                    }
                });


        builder1.setNegativeButton("Cancel", null);

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }*/
}
