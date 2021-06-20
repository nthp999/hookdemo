package com.kltn.hookdemo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyCustomListview extends ArrayAdapter<String> {
    private final Activity context;
    private String [] mainitem;
    private String [] subitem;

    private TextView tv_main;
    private TextView tv_sub;

    public MyCustomListview(Activity context, String[] maintitle, String[] subtitle) {
        super(context, R.layout.list_item_layout, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.mainitem = maintitle;
        this.subitem = subtitle;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View rowView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item_layout, null, true);

            tv_main = (TextView) rowView.findViewById(R.id.textView_main);
            tv_sub = (TextView) rowView.findViewById(R.id.textView_sub);
        }

        tv_main.setText(mainitem[position]);
        tv_sub.setText(subitem[position]);

        return rowView;

    };
}
