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
    private String[] msgs = null;
    private int msgSize = 0;

    public RedDotTextView(Context context) {
        super(context);
        this.mContext = context;

        this.onInit();
    }

    public RedDotTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        this.onInit();
    }

    public RedDotTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        this.onInit();
    }

    public void onInit() {
        if (this.getTag() == null) {
            throw new NullPointerException("控件tag属性不能为空");
        }
        String tagStr = this.getTag().toString();
        // TODO 要求必须设置tag属性 格式也要按要求 如101#100#true 101是当前控件的ID,100是当前控件的父ID,true代表只显示红点不显示消息数量.
        msgs = tagStr.split(RedDotUtil.SEPARATOR);

        if (msgs == null || msgs.length < 2) {
            throw new NullPointerException("控件tag属性值格式不对. 如101#100#true,101是当前控件的ID,100是当前控件的父ID,true代表只显示true这个默认文字不显示消息数量.");
        }

        ViewMsg msg = ViewMsg.queryByView(mContext, msgs[0]);
        if (msg == null) {
            ViewMsg.save(mContext, new ViewMsg(-1, msgs[0], msgs[1], 0));
        } else {
            ViewMsg.update(mContext, new ViewMsg(msg.get_id(), msgs[0], msgs[1], msg.getMsgNumber()));  // 主要是为避免父ID改变的情况下
        }

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
            setVisibility(VISIBLE);
            if (msgs.length >= 3) {   // 只显示点 不显示 消息数量
                setText(msgs[2]);
            } else {
                setText(String.valueOf(msgSize));
            }
        } else {
            setVisibility(INVISIBLE);
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
