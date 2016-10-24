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

        RedDotUtil.initRedDotViews(this, new ArrayList<String>() {
            {
                add("1#-1");
                add("2#1");
                add("3#2");
            }
        });
    }
}
