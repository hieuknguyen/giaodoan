package com.example.giaodoan.model;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.giaodoan.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;

    public Product(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }
    public List<data_product> Select_product() {
        database = dbHelper.getReadableDatabase();
        List<data_product> data = new ArrayList<>();

        // Tạo truy vấn JOIN để lấy cả thông tin sản phẩm và tên danh mục
        String query = "SELECT p.id, p.category_id, c.category_name ,p.ten, p.gia, p.hinhAnh " +
                "FROM " + DatabaseHelper.TABLE_PRODUCT + " p " +
                "LEFT JOIN " + DatabaseHelper.TABLE_CATEGORY + " c ON p.category_id = c.category_id";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Tạo đối tượng data_product với thông tin từ cả hai bảng
                // Giả sử bạn đã thêm trường category_name vào class data_product
                data.add(new data_product(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getLong(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return data;
    }

    public boolean Add_product(Integer category_id, String ten, Long gia, String hinhAnh) {
        database = dbHelper.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = database.query(
                DatabaseHelper.TABLE_PRODUCT,
                null,
                "ten = ? AND category_id = ?",
                new String[]{ten, String.valueOf(category_id)},
                null, null, null
        );

        if (cursor.getCount() > 0){
            return false;
        }
        else {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("category_id", category_id);
            values.put("ten", ten);
            values.put("gia", gia);
            values.put("hinhAnh", hinhAnh);
            database.insert(DatabaseHelper.TABLE_PRODUCT, null, values);
            database.close();
        }
        return true;
    }

    public boolean Delete_product(Integer id) {
        try {
            database = dbHelper.getWritableDatabase();
            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(id)};
            int rowsDeleted = database.delete(DatabaseHelper.TABLE_PRODUCT, whereClause, whereArgs);
            return rowsDeleted > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean Update_product(Integer id, Integer category_id, String ten, Long gia, String hinhAnh) {
        try {
            database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("category_id", category_id);
            values.put("ten", ten);
            values.put("gia", gia);
            values.put("hinhAnh", hinhAnh);

            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(id)};

            int rowsUpdated = database.update(DatabaseHelper.TABLE_PRODUCT, values, whereClause, whereArgs);
            return rowsUpdated > 0;
        } catch (Exception e) {
            return false;
        }
    }
}