package com.pixel.reddot;

import android.app.Application;

import com.pixel.sreddot.utils.RedDotUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/24.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 传入一个String列表,这些String对应RedDotTextView的tag属性,有多少个RedDotTextView就传入多少个tag
        RedDotUtil.registerViews(this, new ArrayList<String>() {
            {
                add("1#-1");
                add("2#1");
                add("3#2");
            }
        });
    }
}
