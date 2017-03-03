package com.pixel.sreddot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pixel.sreddot.RedDotTextView;
import com.pixel.sreddot.callback.OnMessageUpdateListener;
import com.pixel.sreddot.entity.ViewMsg;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/10/24.
 * <p>
 * API集合
 */

public class RedDotUtil {

    // 控件容器
    protected static final Map<String, RedDotTextView> VIEW_MAP = new Hashtable<>();
    // 监听器集合
    protected static final Set<OnMessageUpdateListener> msgUpdateListeners = new HashSet<>();
    // 别名KEY
    public static final String ALIAS = "red_view_alias";
    // 分隔符
    public static final String SEPARATOR = "#";

    /**
     * 添加一个控件到容器
     *
     * @param viewId 控件ID
     * @param view   控件
     */
    public synchronized static void addView(String viewId, RedDotTextView view) {
        if (VIEW_MAP.get(viewId) == null) {
            VIEW_MAP.put(viewId, view);
        }
    }

    /**
     * 从容器中删除一个控件
     *
     * @param viewId 控件ID
     */
    public synchronized static void delView(String viewId) {
        if (VIEW_MAP.get(viewId) != null) {
            VIEW_MAP.remove(viewId);
        }
    }

    /**
     * 刷新所有控件
     */
    public synchronized static void executeViewsRefresh() {
        for (Map.Entry<String, RedDotTextView> entry : VIEW_MAP.entrySet()) {
            RedDotTextView textView = entry.getValue();
            if (textView != null) {
                textView.onInit();
            }
        }
    }

    /**
     * 注册容器中的控件 异步
     *
     * @param context  application
     * @param viewTags 控件tag集合
     */
    public synchronized static void registerViews(final Context context, final List<String> viewTags) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String tag : viewTags) {
                    String[] id = tag.split(SEPARATOR);
                    if (ViewMsg.queryByView(context, id[0]) == null) {
                        ViewMsg.save(context, new ViewMsg(-1, id[0], id[1], 0));
                    }
                }
            }
        }).start();
    }

    /**
     * 更新指定控件消息
     *
     * @param context   application
     * @param tag       控件TAG
     * @param msgNumber 控件消息
     */
    public synchronized static void updateMessage(Context context, String tag, int msgNumber) {
        String[] id = tag.split(SEPARATOR);
        updateMessage(context, id[0], id[1], msgNumber);
    }

    /**
     * 更新指定控件消息
     *
     * @param context   application
     * @param oneselfId 控件自己ID
     * @param parentId  控件父级ID
     * @param msgNumber 控件消息
     */
    public synchronized static void updateMessage(Context context, String oneselfId, String parentId, int msgNumber) {
        ViewMsg msg = ViewMsg.queryByView(context, oneselfId);
        if (msg == null) {
            ViewMsg.save(context, new ViewMsg(-1, oneselfId, parentId, msgNumber));
        } else {
            ViewMsg.updateByView(context, new ViewMsg(-1, oneselfId, parentId, msgNumber));
        }

        RedDotUtil.executeViewsRefresh();

        // 回调消息更新监听接口
        for (OnMessageUpdateListener onMessageUpdateListener : msgUpdateListeners) {
            onMessageUpdateListener.onUpdate(oneselfId + "#" + parentId, msgNumber);
        }
    }

    /**
     * 设置个别名.如当前用户登录有消息,但是换一个用户登录时,消息应该不存在.
     *
     * @param context application
     * @param alias   别名 (系统占用关键字"alias")
     */
    public synchronized static void setAlias(Context context, String alias) {
        Config.saveString(context, ALIAS, alias);
    }

    /**
     * 读取别名
     *
     * @param context application
     * @return 别名
     */
    public synchronized static String getAlias(Context context) {
        return Config.queryString(context, ALIAS);
    }

    /**
     * 获取指定控件的消息数量
     *
     * @param context application
     * @param tag     控件TAG
     * @return 消息数量
     */
    public synchronized static int getMsgSize(Context context, String tag) {
        return getMsgSize(context, tag, false);
    }

    /**
     * 获取指定控件的消息数量
     *
     * @param context   application
     * @param tag       控件TAG
     * @param recursive 是否递归获取所有子控件的消息
     * @return 消息数量
     */
    public synchronized static int getMsgSize(Context context, String tag, boolean recursive) {
        if (tag == null) {
            throw new NullPointerException("控件tag属性不能为空");
        }
        // 要求必须设置tag属性 格式也要按要求 如101#100#true 101是当前控件的ID,100是当前控件的父ID,true代表只显示红点不显示消息数量.
        String[] viewIds = tag.split(RedDotUtil.SEPARATOR);
        return getMsgSize(context, viewIds, recursive);
    }

    /**
     * 获取指定控件的消息数量
     *
     * @param context   application
     * @param viewIds   控件ID/控件父ID
     * @param recursive 是否递归获取所有子控件的消息
     * @return 消息数量
     */
    public synchronized static int getMsgSize(Context context, String[] viewIds, boolean recursive) {
        if (viewIds.length < 2) {
            throw new NullPointerException("控件tag属性值格式不对. 如101#100#true,101是当前控件的ID,100是当前控件的父ID,true代表只显示true这个默认文字不显示消息数量.");
        }
        int msgSize = 0;
        ViewMsg msg = ViewMsg.queryByView(context, viewIds[0]);
        if (msg == null) {
            ViewMsg.save(context, new ViewMsg(-1, viewIds[0], viewIds[1], 0));
        } else {
            ViewMsg.update(context, new ViewMsg(msg.get_id(), viewIds[0], viewIds[1], msg.getMsgNumber()));  // 主要是为避免父ID改变的情况下
        }

        msg = ViewMsg.queryByView(context, viewIds[0]);
        msgSize = msg.getMsgNumber();
        if (!recursive) {
            return msgSize;
        }
        Map<String, ViewMsg> msgMap = new Hashtable<>();
        RedDotUtil.getAllSubclass(context, msgMap, viewIds[0]);
        for (Map.Entry<String, ViewMsg> entry : msgMap.entrySet()) {
            ViewMsg sMsg = ViewMsg.queryByView(context, entry.getValue().getOneselfId());
            if (sMsg != null) {
                msgSize += sMsg.getMsgNumber();
            }
        }
        return msgSize;
    }

    /**
     * 获取当前对象的所有子对象以及子对象的子对象
     *
     * @param context   application
     * @param msgMap    保存对象的容器
     * @param oneselfId 对象的父ID
     */
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

    /**
     * 注册一个消息更新监听器.有一些平级的View(不属于当前库管理的View)在消息变更时也需要更新
     *
     * @param onMessageUpdateListener 消息更新监听器
     */
    public synchronized static void addOnMessageUpdateListener(OnMessageUpdateListener onMessageUpdateListener) {
        msgUpdateListeners.add(onMessageUpdateListener);
    }

    /**
     * 移除消息监听器
     *
     * @param onMessageUpdateListener 消息更新监听器
     */
    public synchronized static void removeOnMessageUpdateListener(OnMessageUpdateListener onMessageUpdateListener) {
        msgUpdateListeners.remove(onMessageUpdateListener);
    }

    /**
     * 保存别名使用
     */
    public static class Config {

        private static SharedPreferences preferences = null;
        private static SharedPreferences.Editor editor = null;

        public static SharedPreferences READ(Context context) {
            if (preferences == null)
                preferences = context.getSharedPreferences("red_dot_view.config", Context.MODE_PRIVATE);
            return preferences;
        }

        public static SharedPreferences.Editor WRITE(Context context) {
            if (editor == null) editor = READ(context).edit();
            return editor;
        }

        public static boolean saveString(Context context, String key, String value) {
            return WRITE(context).putString(key, value).commit();
        }

        public static String queryString(Context context, String key) {
            return READ(context).getString(key, "alias");
        }
    }

}
