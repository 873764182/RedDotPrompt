package com.pixel.sreddot.entity;

import android.content.Context;
import android.database.Cursor;

import com.pixel.sreddot.data.RedDotData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/24.
 * <p>
 * 消息实体
 */

public class ViewMsg {
    private int _id = -1;
    private String oneselfId = "";
    private String parentId = "";
    private int msgNumber = 0;

    public ViewMsg() {
    }

    public ViewMsg(int _id, String oneselfId, String parentId, int msgNumber) {
        this._id = _id;
        this.oneselfId = oneselfId;
        this.parentId = parentId;
        this.msgNumber = msgNumber;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getOneselfId() {
        return oneselfId;
    }

    public void setOneselfId(String oneselfId) {
        this.oneselfId = oneselfId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }

    @Override
    public String toString() {
        return "ViewMsg{" +
                "_id=" + _id +
                ", oneselfId='" + oneselfId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", msgNumber=" + msgNumber +
                '}';
    }

    // =======================================================================================

    public static void save(Context context, ViewMsg message) {
        RedDotData.executeUpdate(context,
                "INSERT INTO ViewMsg (OneselfId, ParentId, MsgNumber) VALUES (?, ?, ?)",
                message.getOneselfId(), message.getParentId(), message.getMsgNumber() + "");
    }

    public static void update(Context context, ViewMsg message) {
        RedDotData.executeUpdate(context,
                "UPDATE ViewMsg SET OneselfId = ?, ParentId = ?, MsgNumber = ? WHERE _id = ?",
                message.getOneselfId(), message.getParentId(), message.getMsgNumber() + "", message.get_id() + "");
    }

    public static void updateByView(Context context, ViewMsg message) {
        RedDotData.executeUpdate(context,
                "UPDATE ViewMsg SET OneselfId = ?, ParentId = ?, MsgNumber = ? WHERE OneselfId = ?",
                message.getOneselfId(), message.getParentId(), message.getMsgNumber() + "", message.getOneselfId());
    }

    public static void delete(Context context, Integer _id) {
        RedDotData.executeUpdate(context,
                "DELETE FROM ViewMsg WHERE _id = ?", _id.toString());
    }

    public static void deleteByView(Context context, String viewId) {
        RedDotData.executeUpdate(context,
                "DELETE FROM ViewMsg WHERE OneselfId = ?", viewId);
    }

    public static ViewMsg query(Context context, Integer _id) {
        Cursor cursor = RedDotData.executeQuery(context,
                "SELECT * FROM ViewMsg WHERE _id = ?", _id.toString());
        List<ViewMsg> messageList = cursorToMessage(cursor);
        return messageList.size() > 0 ? messageList.get(0) : null;
    }

    public static ViewMsg queryByView(Context context, String viewId) {
        Cursor cursor = RedDotData.executeQuery(context,
                "SELECT * FROM ViewMsg WHERE OneselfId = ?", viewId);
        List<ViewMsg> messageList = cursorToMessage(cursor);
        return messageList.size() > 0 ? messageList.get(0) : null;
    }

    public static List<ViewMsg> query(Context context) {
        return query(context, -1, -1);
    }

    public static List<ViewMsg> query(Context context, Integer startPage, Integer endPage) {
        String sql = "SELECT * FROM ViewMsg";
        if (startPage != -1 && endPage != -1) {
            sql += " LIMIT " + startPage + ", " + endPage;
        }
        return cursorToMessage(RedDotData.executeQuery(context, sql));
    }

    // 获取直接子类列表
    public static List<ViewMsg> querySubclass(Context context, String OneselfId) {
        return cursorToMessage(RedDotData.executeQuery(context,
                "SELECT * FROM ViewMsg WHERE ParentId = ?", OneselfId));
    }

    private static List<ViewMsg> cursorToMessage(Cursor cursor) {
        List<ViewMsg> messageList = new ArrayList<>();
        while (cursor.moveToNext()) {
            ViewMsg message = new ViewMsg();
            message.set_id(cursor.getInt(0));
            message.setOneselfId(cursor.getString(1));
            message.setParentId(cursor.getString(2));
            message.setMsgNumber(cursor.getInt(3));
            messageList.add(message);
        }
        return messageList;
    }

}
