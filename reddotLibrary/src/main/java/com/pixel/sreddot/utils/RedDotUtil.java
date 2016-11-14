package com.pixel.sreddot.utils;

import android.content.Context;

import com.pixel.sreddot.RedDotTextView;
import com.pixel.sreddot.data.RedDotData;
import com.pixel.sreddot.entity.ViewMsg;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 */

public class RedDotUtil {
    public static final String SEPARATOR = "#"; // 分隔符
    private static final Map<String, RedDotTextView> RED_DOT_TEXT_VIEW_MAP = new Hashtable<>();

    public synchronized static void addView(String viewId, RedDotTextView view) {
        if (RED_DOT_TEXT_VIEW_MAP.get(viewId) == null) {
            RED_DOT_TEXT_VIEW_MAP.put(viewId, view);
        }
    }

    public synchronized static void delView(String viewId) {
        if (RED_DOT_TEXT_VIEW_MAP.get(viewId) != null) {
            RED_DOT_TEXT_VIEW_MAP.remove(viewId);
        }
    }

    public synchronized static void executeRefresh() {
        for (Map.Entry<String, RedDotTextView> entry : RED_DOT_TEXT_VIEW_MAP.entrySet()) {
            RedDotTextView textView = entry.getValue();
            if (textView != null) textView.onInit();
        }
    }

    public synchronized static void initRedDotViews(final Context context, final List<String> tags) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String tag : tags) {
                    String[] id = tag.split(SEPARATOR);
                    if (ViewMsg.queryByView(context, id[0]) == null) {
                        ViewMsg.save(context, new ViewMsg(-1, id[0], id[1], 0));
                    }
                }
            }
        }).start();
    }

    public synchronized static void updateMessage(Context context, String tag, int msgNumber) {
        String[] id = tag.split(SEPARATOR);
        updateMessage(context, id[0], id[1], msgNumber);
    }

    public synchronized static void updateMessage(Context context, String oneselfId, String ParentId, int msgNumber) {
        ViewMsg msg = ViewMsg.queryByView(context, oneselfId);
        if (msg == null) {
            ViewMsg.save(context, new ViewMsg(-1, oneselfId, ParentId, msgNumber));
        } else {
            ViewMsg.updateByView(context, new ViewMsg(-1, oneselfId, ParentId, msgNumber));
        }
        executeRefresh();
    }

    public static void setRedDotTag(String tag) {
        RedDotData.TAG = tag;
    }

    public static String getRedDotTag() {
        return RedDotData.TAG;
    }
}
