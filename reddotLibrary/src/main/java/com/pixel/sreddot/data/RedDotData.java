package com.pixel.sreddot.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/10/24.
 */

public class RedDotData extends SQLiteOpenHelper {

    public RedDotData(Context context) {
        super(context, "red_dot_msg.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
         *  ViewMsg 表名
         *  _id 逻辑ID
         *  OneselfId 自己的ID
         *  ParentId 父级的ID
         *  MsgNumber 自己要显示的消息数量
         */
        db.execSQL("CREATE TABLE IF NOT EXISTS ViewMsg " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, OneselfId VARCHAR, ParentId VARCHAR, MsgNumber INTEGER, Tag VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // ============================================================================================================================================

    private static SQLiteDatabase database = null;

    private synchronized static SQLiteDatabase getDatabase(Context context) {
        if (database == null) {
            database = new RedDotData(context).getWritableDatabase();
        }
        return database;
    }

    /**
     * 执行非查询语句
     */
    public static void executeUpdate(Context context, String sql, String... params) {
        SQLiteDatabase db = getDatabase(context);
        try {
            db.beginTransaction();
            db.execSQL(sql, params);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 执行查询语句
     */
    public static Cursor executeQuery(Context context, String sql, String... params) {
        return getDatabase(context).rawQuery(sql, params);
    }

    public static Cursor executeQuery(Context context, String sql) {
        return getDatabase(context).rawQuery(sql, null);
    }

}
