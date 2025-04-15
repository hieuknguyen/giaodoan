package com.example.giaodoan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;


public class DatabaseHelper extends SQLiteOpenHelper {
    // Thông tin database
    private static final String DATABASE_NAME = "do_an12.db"; // Sửa lại tên này
    private static final int DATABASE_VERSION = 2;


    public static final String TABLE_USERS = "users";

    public static final String TABLE_CATEGORY = "categories";
    public static final String TABLE_PRODUCT = "product";
    public static final String TABLE_CARTS = "carts";
    public static final String TABLE_PURCHASED_PRODUCTS = "purchased_products";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public boolean deleteDatabase(Context context) {

        boolean deleted = context.deleteDatabase(DATABASE_NAME);
        return deleted;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON");
        String create_user_table = "CREATE TABLE users ("+
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "username TEXT NOT NULL UNIQUE, "+
                "password TEXT NOT NULL, "+
                "email TEXT NOT NULL UNIQUE, "+
                "phone TEXT, "+
                "address TEXT, "+
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "role TEXT CHECK(role IN ('Admin', 'User', 'Staff')) DEFAULT 'User' )";
        db.execSQL(create_user_table);
        String create_category_table = "CREATE TABLE categories ("+
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "category_name TEXT NOT NULL UNIQUE, "+
                "description TEXT, "+
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";
        db.execSQL(create_category_table);

        String create_product_table = "CREATE TABLE product ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "category_id INTERGER NOT NULL, "+
                "ten TEXT NOT NULL, "+
                "gia INTEGER NOT NULL, "+
                "hinhAnh TEXT, "+
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
                "FOREIGN KEY (category_id) REFERENCES \" + TABLE_CATEGORY + \"(category_id))";
        db.execSQL(create_product_table);
        String create_cart_table = "CREATE TABLE carts ("+
                "cart_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "user_id INTEGER NOT NULL, "+
                "category_id INTERGER NOT NULL,"+
                "product_id INTEGER NOT NULL, "+
                "total_amount INTEGER NOT NULL, "+
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
                "FOREIGN KEY (user_id) REFERENCES users(user_id),"+
                "FOREIGN KEY (category_id) REFERENCES categories(category_id),"+
                "FOREIGN KEY (product_id) REFERENCES product(id))";
        db.execSQL(create_cart_table);
        String create_purchased_products_table = "CREATE TABLE purchased_products (" +
                "purchased_products_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "product_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, "+
                "ten TEXT NOT NULL, " +
                "purchased_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "total_amount  INTEGER NOT NULL, " +
                "price REAL NOT NULL, " +
                "rating FLOAT DEFAULT 0, " +
                "review TEXT, " +
                "review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "has_review INTEGER DEFAULT 0," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id),"+
                "FOREIGN KEY (product_id) REFERENCES product(id))";

        db.execSQL(create_purchased_products_table);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PURCHASED_PRODUCTS);

        onCreate(db);
    }

}