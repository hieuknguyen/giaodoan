package com.example.giaodoan.model;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.giaodoan.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class purchased_product {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;

    public purchased_product(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public List<data_purchased_product> Select_product(Integer user_id) {
        database = dbHelper.getReadableDatabase();
        List<data_purchased_product> data = new ArrayList<>();

        // Tạo truy vấn JOIN để lấy cả thông tin sản phẩm và tên danh mục
        String query = "SELECT purchased_products_id, product_id, user_id ,ten, total_amount, price, rating, review,has_review " +
                "FROM " + DatabaseHelper.TABLE_PURCHASED_PRODUCTS +
                " WHERE user_id = ?";
        Cursor cursor = database.rawQuery(query,new String[]{String.valueOf(user_id)});
        if (cursor.moveToFirst()) {
            do {
                data.add(new data_purchased_product(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getLong(5),
                        cursor.getInt(6),
                        cursor.getString(7),
                        cursor.getInt(8)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }
    public long addPurchasedProduct(Integer product_id,Integer user_id, String product_name, Integer total_amount, Long price) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("product_id", product_id);
        values.put("ten", product_name);
        values.put("user_id", user_id);
        values.put("total_amount", total_amount);
        values.put("price", price);
        values.put("rating", 0);
        values.put("review","");
        values.put("has_review", 0);
        long id = db.insert(DatabaseHelper.TABLE_PURCHASED_PRODUCTS, null, values);
        db.close();
        return id;
    }
    public boolean Update_purchased_product(Integer user_id,Integer purchased_product_id, String review, Integer rating) {
        try {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("user_id",user_id);
            values.put("review", review);
            values.put("rating", rating);
            values.put("has_review", 1);

            String whereClause = "purchased_products_id = ?";
            String[] whereArgs = {String.valueOf(purchased_product_id)};

            int rowsUpdated = database.update(DatabaseHelper.TABLE_PURCHASED_PRODUCTS, values, whereClause, whereArgs);
            return rowsUpdated > 0;
        } catch (Exception e) {
            Log.e("check", String.valueOf(e));
            return false;
        }
    }

}
