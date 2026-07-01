package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.models.Cart;
import com.example.models.Product;

import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private final Context context;

    public CartDAO(Context context) {
        this.context = context.getApplicationContext();
    }

    public List<CartItem> getCartByUserID(int userID) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = DatabaseHelper.openDatabase(context);
            String sql = "SELECT c.CartID, c.UserID, c.ProductID, c.Quantity, " +
                    "p.ProductName, p.PromotionalPrice, p.ImageURL " +
                    "FROM Carts c JOIN Products p ON c.ProductID = p.ProductID " +
                    "WHERE c.UserID = ?";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(userID)});

            while (cursor.moveToNext()) {
                CartItem item = new CartItem();
                item.setCartID(cursor.getInt(cursor.getColumnIndexOrThrow("CartID")));
                item.setUserID(cursor.getInt(cursor.getColumnIndexOrThrow("UserID")));
                item.setProductID(cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("Quantity")));
                item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow("ProductName")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("PromotionalPrice")));
                item.setImageURL(cursor.getString(cursor.getColumnIndexOrThrow("ImageURL")));
                cartItems.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return cartItems;
    }

    public boolean updateQuantity(int cartID, int newQuantity) {
        if (newQuantity <= 0) {
            return deleteCartItem(cartID);
        }
        SQLiteDatabase db = null;
        try {
            db = DatabaseHelper.openDatabase(context);
            ContentValues values = new ContentValues();
            values.put("Quantity", newQuantity);
            return db.update("Carts", values, "CartID = ?", new String[]{String.valueOf(cartID)}) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public boolean deleteCartItem(int cartID) {
        SQLiteDatabase db = null;
        try {
            db = DatabaseHelper.openDatabase(context);
            return db.delete("Carts", "CartID = ?", new String[]{String.valueOf(cartID)}) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) db.close();
        }
    }

    public boolean checkout(int userID) {
        SQLiteDatabase db = null;
        try {
            db = DatabaseHelper.openDatabase(context);
            db.beginTransaction();

            // 1. Calculate TotalAmount from Cart
            String totalSql = "SELECT SUM(c.Quantity * p.PromotionalPrice) FROM Carts c " +
                    "JOIN Products p ON c.ProductID = p.ProductID WHERE c.UserID = ?";
            Cursor cursor = db.rawQuery(totalSql, new String[]{String.valueOf(userID)});
            double totalAmount = 0;
            if (cursor.moveToFirst()) {
                totalAmount = cursor.getDouble(0);
            }
            cursor.close();

            if (totalAmount <= 0) {
                return false;
            }

            // 2. Insert into Orders table
            ContentValues orderValues = new ContentValues();
            orderValues.put("UserID", userID);
            orderValues.put("OrderDate", new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date()));
            orderValues.put("TotalAmount", totalAmount);
            orderValues.put("Status", "Processing");
            long orderID = db.insert("Orders", null, orderValues);

            if (orderID == -1) {
                return false;
            }

            // 3. Copy from Cart to OrderDetails using INSERT ... SELECT
            String copySql = "INSERT INTO OrderDetails (OrderID, ProductID, Quantity, Price) " +
                    "SELECT " + orderID + ", c.ProductID, c.Quantity, p.PromotionalPrice " +
                    "FROM Carts c JOIN Products p ON c.ProductID = p.ProductID " +
                    "WHERE c.UserID = ?";
            db.execSQL(copySql, new String[]{String.valueOf(userID)});

            // 4. Delete Cart items for this User
            db.delete("Carts", "UserID = ?", new String[]{String.valueOf(userID)});

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (db != null) {
                if (db.inTransaction()) db.endTransaction();
                db.close();
            }
        }
    }

    // Inner class for UI representation
    public static class CartItem {
        private int cartID;
        private int userID;
        private int productID;
        private int quantity;
        private String productName;
        private double price;
        private String imageURL;

        // Getters and Setters
        public int getCartID() { return cartID; }
        public void setCartID(int cartID) { this.cartID = cartID; }
        public int getUserID() { return userID; }
        public void setUserID(int userID) { this.userID = userID; }
        public int getProductID() { return productID; }
        public void setProductID(int productID) { this.productID = productID; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public String getImageURL() { return imageURL; }
        public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    }
}