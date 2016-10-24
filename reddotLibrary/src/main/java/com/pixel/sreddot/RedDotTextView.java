package com.pixel.sreddot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pixel.sreddot.entity.ViewMsg;
import com.pixel.sreddot.utils.RedDotUtil;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/24.
 * <p>
 * 红点提示文本
 */

public class RedDotTextView extends TextView {
    private final Map<String, ViewMsg> msgMap = new Hashtable<>();
    private Context mContext = null;
    private String[] msgs = new String[2];
    private int msgSize = 0;

    public RedDotTextView(Context context) {
        super(context);
        this.mContext = context;
    }

    public RedDotTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) onInit();
    }

    public void onInit() {
        String tagStr = this.getTag().toString();
        msgs = tagStr.split(RedDotUtil.SEPARATOR);   // 要求必须设置tag属性 格式也要按要求

        ViewMsg msg = ViewMsg.queryByView(mContext, msgs[0]);
        if (msg == null) {
            ViewMsg.save(mContext, new ViewMsg(-1, msgs[0], msgs[1], 0));
        } else {
            ViewMsg.update(mContext, new ViewMsg(msg.get_id(), msgs[0], msgs[1], msg.getMsgNumber()));  // 主要是为避免父ID改变的情况下
        }

        this.setVisibility(INVISIBLE);
        msg = ViewMsg.queryByView(mContext, msgs[0]);
        msgSize = msg.getMsgNumber();
        this.getAllSubclass(msgs[0]);
        for (Map.Entry<String, ViewMsg> entry : msgMap.entrySet()) {
            ViewMsg sMsg = ViewMsg.queryByView(mContext, entry.getValue().getOneselfId());
            if (sMsg != null) {
                msgSize += sMsg.getMsgNumber();
            }
        }
        if (msgSize > 0) {
            this.setVisibility(VISIBLE);
            this.setText(String.valueOf(msgSize));
        } else {
            this.setVisibility(INVISIBLE);
        }

        RedDotUtil.addView(msgs[0], this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        RedDotUtil.delView(msgs[0]);
    }

    // 获取当前对象的所有子对象以及子对象的子对象
    private void getAllSubclass(String oneselfId) {
        if ("-1".equals(oneselfId)) return;
        List<ViewMsg> msgs = ViewMsg.querySubclass(mContext, oneselfId);
        if (msgs != null && msgs.size() > 0) {
            for (ViewMsg message : msgs) {
                msgMap.put(message.getOneselfId(), message);
                getAllSubclass(message.getOneselfId());
            }
        }
    }
}
