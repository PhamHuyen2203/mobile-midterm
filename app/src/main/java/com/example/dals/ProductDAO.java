package com.example.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static final int CART_INSERTED = 1;
    public static final int CART_UPDATED = 2;
    public static final int CART_FAILED = -1;

    private final Context context;

    public ProductDAO(Context context) {
        this.context =
                context.getApplicationContext();
    }

    /**
     * Tìm sản phẩm:
     *
     * - Theo tên bằng LIKE.
     * - Hoặc theo khoảng PromotionalPrice.
     * - Nếu nhập cả hai điều kiện thì dùng OR.
     * - Nếu không nhập điều kiện thì lấy toàn bộ.
     */
    public List<Product> searchProducts(
            String keyword,
            Double minPrice,
            Double maxPrice
    ) {
        List<Product> products =
                new ArrayList<>();

        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database =
                    DatabaseHelper.openDatabase(context);

            String normalizedKeyword =
                    keyword == null
                            ? ""
                            : keyword.trim();

            boolean hasKeyword =
                    !TextUtils.isEmpty(
                            normalizedKeyword
                    );

            boolean hasPriceRange =
                    minPrice != null
                            && maxPrice != null;

            StringBuilder sql =
                    new StringBuilder();

            sql.append(
                    "SELECT "
            );

            sql.append(
                    "p.ProductID, "
            );

            sql.append(
                    "p.CategoryID, "
            );

            sql.append(
                    "c.CategoryName, "
            );

            sql.append(
                    "p.ProductName, "
            );

            sql.append(
                    "p.OriginalPrice, "
            );

            sql.append(
                    "p.PromotionalPrice, "
            );

            sql.append(
                    "p.ImageURL, "
            );

            sql.append(
                    "p.Rating, "
            );

            sql.append(
                    "p.Description "
            );

            sql.append(
                    "FROM Products AS p "
            );

            sql.append(
                    "INNER JOIN Categories AS c "
            );

            sql.append(
                    "ON c.CategoryID = p.CategoryID "
            );

            List<String> arguments =
                    new ArrayList<>();

            if (hasKeyword && hasPriceRange) {

                /*
                 * Tên sản phẩm HOẶC khoảng giá.
                 */
                sql.append(
                        "WHERE p.ProductName LIKE ? "
                );

                sql.append(
                        "OR p.PromotionalPrice " +
                                "BETWEEN ? AND ? "
                );

                arguments.add(
                        "%" + normalizedKeyword + "%"
                );

                arguments.add(
                        String.valueOf(minPrice)
                );

                arguments.add(
                        String.valueOf(maxPrice)
                );

            } else if (hasKeyword) {

                sql.append(
                        "WHERE p.ProductName LIKE ? "
                );

                arguments.add(
                        "%" + normalizedKeyword + "%"
                );

            } else if (hasPriceRange) {

                sql.append(
                        "WHERE p.PromotionalPrice " +
                                "BETWEEN ? AND ? "
                );

                arguments.add(
                        String.valueOf(minPrice)
                );

                arguments.add(
                        String.valueOf(maxPrice)
                );
            }

            sql.append(
                    "ORDER BY p.ProductName ASC"
            );

            cursor = database.rawQuery(
                    sql.toString(),
                    arguments.toArray(
                            new String[0]
                    )
            );

            while (cursor.moveToNext()) {
                products.add(
                        mapProduct(cursor)
                );
            }

            return products;

        } catch (Exception exception) {
            throw new RuntimeException(
                    exception
            );

        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (database != null) {
                database.close();
            }
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng.
     *
     * Chưa tồn tại:
     * INSERT INTO Carts.
     *
     * Đã tồn tại:
     * UPDATE duy nhất cột Quantity.
     */
    public int addProductToCart(
            int userID,
            int productID,
            int addedQuantity
    ) {
        if (userID <= 0
                || productID <= 0
                || addedQuantity <= 0) {
            return CART_FAILED;
        }

        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database =
                    DatabaseHelper.openDatabase(context);

            database.beginTransaction();

            String checkSql =
                    "SELECT CartID, Quantity " +
                            "FROM Carts " +
                            "WHERE UserID = ? " +
                            "AND ProductID = ? " +
                            "LIMIT 1";

            cursor = database.rawQuery(
                    checkSql,
                    new String[]{
                            String.valueOf(userID),
                            String.valueOf(productID)
                    }
            );

            int result;

            if (cursor.moveToFirst()) {

                int currentQuantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "Quantity"
                                )
                        );

                int newQuantity =
                        currentQuantity
                                + addedQuantity;

                ContentValues updateValues =
                        new ContentValues();

                /*
                 * Chỉ cập nhật Quantity.
                 */
                updateValues.put(
                        "Quantity",
                        newQuantity
                );

                int affectedRows =
                        database.update(
                                "Carts",
                                updateValues,
                                "UserID = ? AND ProductID = ?",
                                new String[]{
                                        String.valueOf(userID),
                                        String.valueOf(productID)
                                }
                        );

                result =
                        affectedRows > 0
                                ? CART_UPDATED
                                : CART_FAILED;

            } else {

                ContentValues insertValues =
                        new ContentValues();

                insertValues.put(
                        "UserID",
                        userID
                );

                insertValues.put(
                        "ProductID",
                        productID
                );

                insertValues.put(
                        "Quantity",
                        addedQuantity
                );

                long insertedID =
                        database.insert(
                                "Carts",
                                null,
                                insertValues
                        );

                result =
                        insertedID != -1
                                ? CART_INSERTED
                                : CART_FAILED;
            }

            database.setTransactionSuccessful();

            return result;

        } catch (Exception exception) {
            throw new RuntimeException(
                    exception
            );

        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (database != null) {
                if (database.inTransaction()) {
                    database.endTransaction();
                }

                database.close();
            }
        }
    }

    /**
     * Lấy danh sách sản phẩm bán chạy nhất.
     */
    public List<TopProduct> getTopSellingProducts(int n) {
        List<TopProduct> products = new ArrayList<>();
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database = DatabaseHelper.openDatabase(context);
            String sql = "SELECT p.ProductID, p.ProductName, p.ImageURL, p.PromotionalPrice, " +
                    "SUM(od.Quantity) AS TotalSold " +
                    "FROM Products p " +
                    "JOIN OrderDetails od ON p.ProductID = od.ProductID " +
                    "JOIN Orders o ON od.OrderID = o.OrderID " +
                    "WHERE o.Status = 'Paid' " +
                    "GROUP BY p.ProductID, p.ProductName, p.ImageURL, p.PromotionalPrice " +
                    "ORDER BY TotalSold DESC " +
                    "LIMIT ?";

            cursor = database.rawQuery(sql, new String[]{String.valueOf(n)});

            while (cursor.moveToNext()) {
                TopProduct top = new TopProduct();
                top.productID = cursor.getInt(cursor.getColumnIndexOrThrow("ProductID"));
                top.productName = cursor.getString(cursor.getColumnIndexOrThrow("ProductName"));
                top.imageURL = cursor.getString(cursor.getColumnIndexOrThrow("ImageURL"));
                top.price = cursor.getDouble(cursor.getColumnIndexOrThrow("PromotionalPrice"));
                top.totalSold = cursor.getInt(cursor.getColumnIndexOrThrow("TotalSold"));
                products.add(top);
            }
            return products;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (cursor != null) cursor.close();
            if (database != null) database.close();
        }
    }

    public static class TopProduct {
        public int productID;
        public String productName;
        public String imageURL;
        public double price;
        public int totalSold;
    }

    private Product mapProduct(
            Cursor cursor
    ) {
        Product product =
                new Product();

        product.setProductID(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "ProductID"
                        )
                )
        );

        product.setCategoryID(
                cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                "CategoryID"
                        )
                )
        );

        product.setCategoryName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "CategoryName"
                        )
                )
        );

        product.setProductName(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "ProductName"
                        )
                )
        );

        product.setOriginalPrice(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "OriginalPrice"
                        )
                )
        );

        product.setPromotionalPrice(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "PromotionalPrice"
                        )
                )
        );

        product.setImageURL(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "ImageURL"
                        )
                )
        );

        product.setRating(
                cursor.getDouble(
                        cursor.getColumnIndexOrThrow(
                                "Rating"
                        )
                )
        );

        product.setDescription(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                "Description"
                        )
                )
        );

        return product;
    }
}