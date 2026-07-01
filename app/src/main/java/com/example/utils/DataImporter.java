package com.example.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dals.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DataImporter {
    private static final String TAG = "DataImporter";
    private static final String JSON_FILE = "MCommerce.json";

    public static void importJsonToSqlite(Context context) {
        SQLiteDatabase db = null;
        try {
            db = DatabaseHelper.openDatabase(context);
            
            // Xóa bảng cũ và tạo lại nếu cần (Hoặc bạn có thể chỉ kiểm tra nếu bảng trống)
            createTables(db);

            String jsonString = loadJSONFromAsset(context);
            if (jsonString == null) return;

            JSONObject jsonObject = new JSONObject(jsonString);

            importCategories(db, jsonObject.getJSONArray("categories"));
            importUsers(db, jsonObject.getJSONArray("users"));
            importProducts(db, jsonObject.getJSONArray("products"));
            importCarts(db, jsonObject.getJSONArray("carts"));
            importOrders(db, jsonObject.getJSONArray("orders"));
            importOrderDetails(db, jsonObject.getJSONArray("orderDetails"));

            Log.d(TAG, "Import dữ liệu từ JSON thành công!");

        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi import dữ liệu: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }
    }

    private static void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Categories (CategoryID INTEGER PRIMARY KEY, CategoryName TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Users (UserID INTEGER PRIMARY KEY, Username TEXT, FullName TEXT, Email TEXT, Password TEXT, Type TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Products (ProductID INTEGER PRIMARY KEY, CategoryID INTEGER, ProductName TEXT, OriginalPrice REAL, PromotionalPrice REAL, ImageURL TEXT, Rating REAL, Description TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Carts (CartID INTEGER PRIMARY KEY, UserID INTEGER, ProductID INTEGER, Quantity INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Orders (OrderID INTEGER PRIMARY KEY, UserID INTEGER, OrderDate TEXT, TotalAmount REAL, Status TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS OrderDetails (OrderDetailID INTEGER PRIMARY KEY, OrderID INTEGER, ProductID INTEGER, Quantity INTEGER, Price REAL)");
    }

    private static String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(JSON_FILE);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static void importCategories(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("Categories", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("CategoryID", obj.getInt("CategoryID"));
            values.put("CategoryName", obj.getString("CategoryName"));
            db.insert("Categories", null, values);
        }
    }

    private static void importUsers(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("Users", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("UserID", obj.getInt("UserID"));
            values.put("Username", obj.getString("Username"));
            values.put("FullName", obj.getString("FullName"));
            values.put("Email", obj.getString("Email"));
            values.put("Password", obj.getString("Password"));
            values.put("Type", obj.getString("Type"));
            db.insert("Users", null, values);
        }
    }

    private static void importProducts(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("Products", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("ProductID", obj.getInt("ProductID"));
            values.put("CategoryID", obj.getInt("CategoryID"));
            values.put("ProductName", obj.getString("ProductName"));
            values.put("OriginalPrice", obj.getDouble("OriginalPrice"));
            values.put("PromotionalPrice", obj.getDouble("PromotionalPrice"));
            values.put("ImageURL", obj.getString("ImageURL"));
            values.put("Rating", obj.getDouble("Rating"));
            values.put("Description", obj.getString("Description"));
            db.insert("Products", null, values);
        }
    }

    private static void importCarts(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("Carts", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("CartID", obj.getInt("CartID"));
            values.put("UserID", obj.getInt("UserID"));
            values.put("ProductID", obj.getInt("ProductID"));
            values.put("Quantity", obj.getInt("Quantity"));
            db.insert("Carts", null, values);
        }
    }

    private static void importOrders(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("Orders", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("OrderID", obj.getInt("OrderID"));
            values.put("UserID", obj.getInt("UserID"));
            values.put("OrderDate", obj.getString("OrderDate"));
            values.put("TotalAmount", obj.getDouble("TotalAmount"));
            values.put("Status", obj.getString("Status"));
            db.insert("Orders", null, values);
        }
    }

    private static void importOrderDetails(SQLiteDatabase db, JSONArray array) throws JSONException {
        db.delete("OrderDetails", null, null);
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            ContentValues values = new ContentValues();
            values.put("OrderDetailID", obj.getInt("OrderDetailID"));
            values.put("OrderID", obj.getInt("OrderID"));
            values.put("ProductID", obj.getInt("ProductID"));
            values.put("Quantity", obj.getInt("Quantity"));
            values.put("Price", obj.getDouble("Price"));
            db.insert("OrderDetails", null, values);
        }
    }
}