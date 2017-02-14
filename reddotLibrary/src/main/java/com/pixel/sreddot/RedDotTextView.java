package com.pixel.sreddot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.pixel.sreddot.entity.ViewMsg;
import com.pixel.sreddot.utils.RedDotUtil;

import java.util.Hashtable;
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
        Object tag = getTag();
        if (tag == null) {
            throw new NullPointerException("控件tag属性不能为空");
        }
        msgs = tag.toString().split(RedDotUtil.SEPARATOR);
        msgSize = RedDotUtil.getMsgSize(mContext, msgs);
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

        setGravity(Gravity.CENTER);

        RedDotUtil.addView(msgs[0], this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        RedDotUtil.delView(msgs[0]);
    }

}
