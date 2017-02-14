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

    // 设置个别名,如当前用户登录有消息,但是换一个用户登录时,消息应该不存在.
    public static void setRedDotTag(String tag) {
        RedDotData.TAG = tag;
    }

    public static String getRedDotTag() {
        return RedDotData.TAG;
    }

    public synchronized static int getMsgSize(Context context, String tag) {
        if (tag == null) {
            throw new NullPointerException("控件tag属性不能为空");
        }

        // TODO 要求必须设置tag属性 格式也要按要求 如101#100#true 101是当前控件的ID,100是当前控件的父ID,true代表只显示红点不显示消息数量.
        String[] msgs = tag.split(RedDotUtil.SEPARATOR);

        return getMsgSize(context, msgs);
    }

    public synchronized static int getMsgSize(Context context, String[] msgs) {
        if (msgs.length < 2) {
            throw new NullPointerException("控件tag属性值格式不对. 如101#100#true,101是当前控件的ID,100是当前控件的父ID,true代表只显示true这个默认文字不显示消息数量.");
        }
        int msgSize = 0;
        ViewMsg msg = ViewMsg.queryByView(context, msgs[0]);
        if (msg == null) {
            ViewMsg.save(context, new ViewMsg(-1, msgs[0], msgs[1], 0));
        } else {
            ViewMsg.update(context, new ViewMsg(msg.get_id(), msgs[0], msgs[1], msg.getMsgNumber()));  // 主要是为避免父ID改变的情况下
        }

        msg = ViewMsg.queryByView(context, msgs[0]);
        msgSize = msg.getMsgNumber();
        Map<String, ViewMsg> msgMap = new Hashtable<>();
        RedDotUtil.getAllSubclass(context, msgMap, msgs[0]);
        for (Map.Entry<String, ViewMsg> entry : msgMap.entrySet()) {
            ViewMsg sMsg = ViewMsg.queryByView(context, entry.getValue().getOneselfId());
            if (sMsg != null) {
                msgSize += sMsg.getMsgNumber();
            }
        }
        return msgSize;
    }

    // 获取当前对象的所有子对象以及子对象的子对象
    public static void getAllSubclass(Context context, Map<String, ViewMsg> msgMap, String oneselfId) {
        if ("-1".equals(oneselfId)) return;
        List<ViewMsg> msgs = ViewMsg.querySubclass(context, oneselfId);
        if (msgs != null && msgs.size() > 0) {
            for (ViewMsg message : msgs) {
                msgMap.put(message.getOneselfId(), message);
                getAllSubclass(context, msgMap, message.getOneselfId());
            }
        }
    }

}
