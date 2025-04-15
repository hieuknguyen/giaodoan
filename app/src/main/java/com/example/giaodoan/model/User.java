package com.example.giaodoan.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.giaodoan.database.DatabaseHelper;

public class User {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;

    public User(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    public data_user checkLogin(String username, String password) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                "users",
                null,
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );
        if(cursor.getCount() > 0){
            cursor.moveToNext();
            data_user data = new data_user();
            while (!cursor.isAfterLast()){
                data.set(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(8));
                cursor.moveToNext();
            }
            return data;
        }
        return null;

    }
    public boolean register(String username, String email, String password,String role) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                "users",
                null,
                "username = ?",
                new String[]{username},
                null, null, null
        );
        if (cursor.getCount() > 0){
            return false;
        }
        else{
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("email", email);
            values.put("phone", "");
            values.put("address", "");
            values.put("role", role);

            database.insert("users", null, values);
            database.close();
        }

        return true;




    }
    public boolean Update_infor(Integer user_id, String username, String password, String email, String phone) {
        try {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("password", password);
            values.put("email", email);
            values.put("phone", phone);


            String whereClause = "user_id = ?";
            String[] whereArgs = {String.valueOf(user_id)};

            int rowsUpdated = database.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
            return rowsUpdated > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
