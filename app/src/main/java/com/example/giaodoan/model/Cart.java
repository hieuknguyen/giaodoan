package com.example.giaodoan.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.giaodoan.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;
    public Cart(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public List<data_cart> Select_cart(Integer user_id) {
        database = dbHelper.getReadableDatabase();
        List<data_cart> data = new ArrayList<>();
        Cursor cartsCursor = database.query(
                DatabaseHelper.TABLE_CARTS,
                null,
                "user_id = ?",
                new String[]{String.valueOf(user_id)},
                null, null, null
        );


        String query = "SELECT c.cart_id,p.id AS product_id,cat.category_id AS category_id, cat.category_name AS category_name, " +
                "p.ten AS product_name, p.gia AS price, c.total_amount " +
                "FROM " + DatabaseHelper.TABLE_CARTS + " c " +
                "JOIN " + DatabaseHelper.TABLE_PRODUCT + " p ON c.product_id = p.id " +
                "LEFT JOIN " + DatabaseHelper.TABLE_CATEGORY + " cat ON p.category_id = cat.category_id " +
                "WHERE c.user_id = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(user_id)});

        if (cursor.moveToFirst()) {
            do {
                data_cart cart = new data_cart(
                        cursor.getInt(0),  // cart_id
                        cursor.getInt(1),
                        cursor.getInt(2),// category_name
                        cursor.getString(3),  // product_name
                        cursor.getString(4),     // price
                        cursor.getLong(5),      // total_amount
                        cursor.getInt(6)
                );

                data.add(cart);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public boolean Add_cart(Integer user_id, Integer product_id, Integer Category_id,Integer quantity) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_CARTS,
                null,
                "user_id = ? AND product_id = ?",
                new String[]{String.valueOf(user_id), String.valueOf(product_id)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            try {
                String cart_id = cursor.getString(0);
                database = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("total_amount", quantity);
                values.put("updated_at", System.currentTimeMillis()); // Cập nhật thời gian

                String whereClause = "cart_id = ?";
                String[] whereArgs = {String.valueOf(cart_id)};

                int rowsUpdated = database.update(DatabaseHelper.TABLE_CARTS, values, whereClause, whereArgs);
                cursor.close();
                return rowsUpdated > 0;
            } catch (Exception e) {
                cursor.close();
                return false;
            }
        } else {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id", user_id);
            values.put("product_id", product_id);
            values.put("category_id", Category_id);
            values.put("total_amount", quantity);
            long newRowId = database.insert(DatabaseHelper.TABLE_CARTS, null, values);
            database.close();
            return newRowId != -1;
        }

    }

    public boolean reduce_cart(Integer cart_id){

        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_CARTS,
                null,
                "cart_id = ?",
                new String[]{String.valueOf(cart_id)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            try {

                int currentQuantity = cursor.getInt(4);
                if(currentQuantity == 1){
                    return Delete_cart(cart_id);
                }
                else {
                    database = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("total_amount", currentQuantity - 1);
                    values.put("updated_at", System.currentTimeMillis()); // Cập nhật thời gian

                    String whereClause = "cart_id = ?";
                    String[] whereArgs = {String.valueOf(cart_id)};

                    int rowsUpdated = database.update(DatabaseHelper.TABLE_CARTS, values, whereClause, whereArgs);
                    cursor.close();
                    return rowsUpdated > 0;
                }


            } catch (Exception e) {
                cursor.close();
                return false;
            }
        }
        return false;
    }


    public boolean update_cart(Integer cart_id, Integer index) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_CARTS,
                null,
                "cart_id = ?",
                new String[]{String.valueOf(cart_id)},
                null, null, null
        );
        if (cursor.moveToFirst()) {
            try {

                database = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("total_amount", index);
                values.put("updated_at", System.currentTimeMillis()); // Cập nhật thời gian

                String whereClause = "cart_id = ?";
                String[] whereArgs = {String.valueOf(cart_id)};

                int rowsUpdated = database.update(DatabaseHelper.TABLE_CARTS, values, whereClause, whereArgs);
                cursor.close();
                return rowsUpdated > 0;
            } catch (Exception e) {
                cursor.close();
                return false;
            }
        }
        return false;
    }
    public boolean Delete_cart(Integer cart_id) {
        try {
            database = dbHelper.getWritableDatabase();
            String whereClause = "cart_id = ?";
            String[] whereArgs = {String.valueOf(cart_id)};
            int rowsDeleted = database.delete(DatabaseHelper.TABLE_CARTS, whereClause, whereArgs);
            return rowsDeleted > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
