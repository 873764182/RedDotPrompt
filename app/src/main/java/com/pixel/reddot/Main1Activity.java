package com.pixel.reddot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pixel.sreddot.callback.OnMessageUpdateListener;
import com.pixel.sreddot.utils.RedDotUtil;

public class Main1Activity extends Activity {

    private final OnMessageUpdateListener onMessageUpdateListener = new OnMessageUpdateListener() {
        @Override
        public void onUpdate(String tag, int msgSize) {
            Toast.makeText(Main1Activity.this, tag + " -> " + msgSize, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RedDotUtil.addOnMessageUpdateListener(onMessageUpdateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RedDotUtil.removeOnMessageUpdateListener(onMessageUpdateListener);
    }

    public void onToNext(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void add_1(View view) {
        RedDotUtil.updateMessage(this, "1#-1", 1);
    }

    public void add_2(View view) {
        RedDotUtil.updateMessage(this, "2#1", 1);
    }

    public void add_3(View view) {
        RedDotUtil.updateMessage(this, "3#2", 1);
    }

    public void del_1(View view) {
        RedDotUtil.updateMessage(this, "1#-1", 0);
    }


    public void del_2(View view) {
        RedDotUtil.updateMessage(this, "2#1", 0);
    }

    public void del_3(View view) {
        RedDotUtil.updateMessage(this, "3#2", 0);
    }

}
