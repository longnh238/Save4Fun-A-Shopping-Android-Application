package com.example.save4fun.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.save4fun.model.MyList;
import com.example.save4fun.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class DBListsHelper extends SQLiteOpenHelper {
    public static final String LISTS_TABLE = "lists";

    public DBListsHelper(Context context) {
        super(context, Constant.DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean createListForUsername(MyList list, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", list.getName());
        contentValues.put("description", list.getDescription());
        contentValues.put("type", list.getType());
        contentValues.put("username", username);
        long result = db.insert(LISTS_TABLE, null, contentValues);
        return result != -1;
    }

    @SuppressLint("Range")
    public List<MyList> getListByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from lists where username = ?", new String[]{username});

        List<MyList> lists = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                MyList list = new MyList();

                list.setId(cursor.getInt(cursor.getColumnIndex("id")));
                list.setName(cursor.getString(cursor.getColumnIndex("name")));
                list.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                list.setType(cursor.getString(cursor.getColumnIndex("type")));

                lists.add(list);
            } while (cursor.moveToNext());
        }
        return lists;
    }

    public void deleteListById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(LISTS_TABLE, "id=?", new String[]{String.valueOf(id)});
    }
}
