package com.kltn.hookdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowLogActivity extends AppCompatActivity {
    TextView tvLog;
    DatabaseSupport db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);

        tvLog = (TextView) findViewById(R.id.tv_log);
        tvLog.setMovementMethod(new ScrollingMovementMethod());

        db = new DatabaseSupport(ShowLogActivity.this);
        ArrayList<String> list = db.getLog();
        for (int i = 0; i < list.size(); i++)
            tvLog.append(list.get(i));

    }
}
