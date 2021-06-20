package com.kltn.hookdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kltn.hookdemo.hooking.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Button btnShowlog;

    ListView listView;
    MyCustomListview customAdapter;
    String[] lv_mainitem = {"SMS", "HTTP Connection", "Clipboard Access", "Intent Calling", "Internal/SD Access"};
    String[] lv_subitem = { "Allow", "Allow", "Allow", "Allow", "Allow"};
    String[] options = { "Allow", "Block", "Warning" };

    String TAG = "KLTN2021";
    BroadcastReceiver exampleBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        btnShowlog = (Button) findViewById(R.id.btn_showlog);
        listView = (ListView) findViewById(R.id.lv_singleops);

        // ==================== REGISTER BROADCAST RECEIVER =====================
        // Create br_recv object
        // and register to BroadcastReceiver
        exampleBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.kltn.CUSTOM_INTENT");
        registerReceiver(exampleBroadcastReceiver, filter);

        // ======================== CONFIG LISTVIEW ==============================
        // Create listview
        customAdapter = new MyCustomListview(this, lv_mainitem, lv_subitem);
        listView.setAdapter(customAdapter);

        // Set click action
        listView.setOnItemClickListener(this);

        // ============================ BUTTON ==============================
        // Button Show Log: Call ShowLog.class
        btnShowlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShowLogActivity.class);
                startActivity(intent);
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

    // setOnitemClickListener event
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), "Clicked" + position, Toast.LENGTH_SHORT ).show();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setTitle("Options " + lv_mainitem[position]);       // Set title of AlertDialog
        builder1.setIcon(R.drawable.icon);                           // Set icon

        builder1.setSingleChoiceItems(
                options,                                             // Items list
                -1,                                       // Index of checked item (-1 = no selection)
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e(TAG,options[which]);
                        lv_subitem[position] = options[which];

                    }
                });
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listView.setAdapter(customAdapter);

                    }
                });

        builder1.setNegativeButton("Cancel", null);

        AlertDialog alert1 = builder1.create();
        alert1.show();
    }
}