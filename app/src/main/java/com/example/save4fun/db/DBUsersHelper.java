package com.example.save4fun.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.save4fun.model.User;
import com.example.save4fun.util.Constant;

public class DBUsersHelper extends SQLiteOpenHelper {
    public static final String USERS_TABLE = "users";

    public DBUsersHelper(Context context) {
        super(context, Constant.DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user.getUsername());
        contentValues.put("password", user.getPassword());
        if(user.getPassword() != null && user.getPassword().isEmpty()) {
            contentValues.put("email", user.getEmail());
        }
        long result = db.insert(USERS_TABLE, null, contentValues);
        return result != -1;
    }

    public boolean isUserExisted(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from users where username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    public boolean authenticate(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor =
                db.rawQuery("select * from users where username = ? and password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from users where username = ?", new String[]{username});
        User user = new User();
        if (cursor.moveToFirst()) {
            user.setUsername(cursor.getString(0));
            user.setFirstName(cursor.getString(2));
            user.setLastName(cursor.getString(3));
            user.setBirthday(cursor.getString(4));
            user.setEmail(cursor.getString(5));
            user.setPhone(cursor.getString(6));
            return user;
        }
        return null;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("birthday", user.getBirthday());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());

        db.update("users", values, "username=?", new String[]{String.valueOf(user.getUsername())});
        db.close();
    }
}
