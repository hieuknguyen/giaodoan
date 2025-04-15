package com.example.giaodoan.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.giaodoan.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;
    public Category(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public List<data_category> Select_category() {
        database = dbHelper.getReadableDatabase();
        List<data_category> data = new ArrayList<>();

        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_CATEGORY,
                null,
                null,
                null,
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                data.add(new data_category(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

    public boolean Add_category(String category_name, String description) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_CATEGORY,
                null,
                "category_name = ?",
                new String[]{category_name},
                null, null, null
        );
        if (cursor.getCount() > 0){
            return false;
        }
        else {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("category_name", category_name);
            values.put("description", description);
            database.insert("categories", null, values);
            database.close();
        }
        return true;

    }
    public boolean Delete_category(Integer category_id) {
        try {


            database = dbHelper.getWritableDatabase();
            String whereClause = "category_id = ?";
            String[] whereArgs = {String.valueOf(category_id)};
            database.delete(DatabaseHelper.TABLE_PRODUCT, whereClause, whereArgs);
            int rowsDeleted = database.delete(DatabaseHelper.TABLE_CATEGORY, whereClause, whereArgs);
            return rowsDeleted > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
