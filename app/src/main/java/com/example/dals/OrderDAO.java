package com.example.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OrderDAO {
    private final Context context;

    public OrderDAO(Context context) {
        this.context = context.getApplicationContext();
    }

    public OrderStats getOrderStats(int userID) {
        OrderStats stats = new OrderStats();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = DatabaseHelper.openDatabase(context);

            // 1. Total orders
            cursor = db.rawQuery("SELECT COUNT(*) FROM Orders WHERE UserID = ?", new String[]{String.valueOf(userID)});
            if (cursor.moveToFirst()) {
                stats.totalOrders = cursor.getInt(0);
            }
            cursor.close();

            // 2. Processing orders
            cursor = db.rawQuery("SELECT COUNT(*) FROM Orders WHERE UserID = ? AND Status = 'Processing'", new String[]{String.valueOf(userID)});
            if (cursor.moveToFirst()) {
                stats.processingOrders = cursor.getInt(0);
            }
            cursor.close();

            // 3. Total spent (Paid)
            cursor = db.rawQuery("SELECT SUM(TotalAmount) FROM Orders WHERE UserID = ? AND Status = 'Paid'", new String[]{String.valueOf(userID)});
            if (cursor.moveToFirst()) {
                stats.totalSpentPaid = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return stats;
    }

    /**
     * Lấy toàn bộ danh sách đơn hàng (Admin) kèm tên khách hàng.
     * Sử dụng LIMIT và OFFSET để phân trang.
     */
    public java.util.List<AdminOrder> getAllOrdersPaginated(int limit, int offset) {
        java.util.List<AdminOrder> orderList = new java.util.ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = DatabaseHelper.openDatabase(context);
            String sql = "SELECT o.OrderID, o.UserID, u.FullName, o.OrderDate, o.TotalAmount, o.Status " +
                    "FROM Orders o " +
                    "JOIN Users u ON o.UserID = u.UserID " +
                    "ORDER BY o.OrderDate DESC " +
                    "LIMIT ? OFFSET ?";
            
            cursor = db.rawQuery(sql, new String[]{String.valueOf(limit), String.valueOf(offset)});

            while (cursor.moveToNext()) {
                AdminOrder order = new AdminOrder();
                order.orderID = cursor.getInt(cursor.getColumnIndexOrThrow("OrderID"));
                order.userID = cursor.getInt(cursor.getColumnIndexOrThrow("UserID"));
                order.customerName = cursor.getString(cursor.getColumnIndexOrThrow("FullName"));
                order.orderDate = cursor.getString(cursor.getColumnIndexOrThrow("OrderDate"));
                order.totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalAmount"));
                order.status = cursor.getString(cursor.getColumnIndexOrThrow("Status"));
                orderList.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return orderList;
    }

    public static class AdminOrder {
        public int orderID;
        public int userID;
        public String customerName;
        public String orderDate;
        public double totalAmount;
        public String status;
    }

    /**
     * Báo cáo doanh thu theo danh mục (Admin).
     * Chỉ lấy các đơn hàng đã thanh toán (Paid).
     */
    public java.util.List<CategoryRevenue> getRevenueByCategory() {
        java.util.List<CategoryRevenue> report = new java.util.ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = DatabaseHelper.openDatabase(context);
            String sql = "SELECT c.CategoryName, " +
                    "SUM(od.Quantity) AS TotalQuantity, " +
                    "SUM(od.Quantity * od.Price) AS TotalRevenue " +
                    "FROM Categories c " +
                    "JOIN Products p ON c.CategoryID = p.CategoryID " +
                    "JOIN OrderDetails od ON p.ProductID = od.ProductID " +
                    "JOIN Orders o ON od.OrderID = o.OrderID " +
                    "WHERE o.Status = 'Paid' " +
                    "GROUP BY c.CategoryName " +
                    "ORDER BY TotalRevenue DESC";

            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                CategoryRevenue item = new CategoryRevenue();
                item.categoryName = cursor.getString(cursor.getColumnIndexOrThrow("CategoryName"));
                item.totalQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("TotalQuantity"));
                item.totalRevenue = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalRevenue"));
                report.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return report;
    }

    public static class CategoryRevenue {
        public String categoryName;
        public int totalQuantity;
        public double totalRevenue;
    }

    /**
     * Báo cáo khách hàng chi tiêu nhiều nhất (Admin).
     * Chỉ lấy các đơn hàng đã thanh toán (Paid).
     */
    public java.util.List<CustomerSpending> getCustomerSpendingReport() {
        java.util.List<CustomerSpending> report = new java.util.ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = DatabaseHelper.openDatabase(context);
            String sql = "SELECT u.Username, u.FullName, u.Email, SUM(o.TotalAmount) AS TotalSpent " +
                    "FROM Users u " +
                    "JOIN Orders o ON u.UserID = o.UserID " +
                    "WHERE o.Status = 'Paid' " +
                    "GROUP BY u.UserID, u.Username, u.FullName, u.Email " +
                    "ORDER BY TotalSpent DESC";

            cursor = db.rawQuery(sql, null);

            while (cursor.moveToNext()) {
                CustomerSpending item = new CustomerSpending();
                item.username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
                item.fullName = cursor.getString(cursor.getColumnIndexOrThrow("FullName"));
                item.email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
                item.totalSpent = cursor.getDouble(cursor.getColumnIndexOrThrow("TotalSpent"));
                report.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return report;
    }

    public static class CustomerSpending {
        public String username;
        public String fullName;
        public String email;
        public double totalSpent;
    }

    public static class OrderStats {
        public int totalOrders;
        public int processingOrders;
        public double totalSpentPaid;
    }
}