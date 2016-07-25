package com.example.qzl.qqslidemenu_50;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class MainActivity extends Activity {

    private ListView menu_listview,main_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        main_listview = (ListView) findViewById(R.id.main_listview);
        menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        main_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.NAMES){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null?super.getView(position, convertView, parent):convertView;
                //先缩小view
                ViewHelper.setScaleX(view,0.5f);
                ViewHelper.setScaleY(view,0.5f);
                //以属性动画放大，设置放大动画
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });
    }
}
