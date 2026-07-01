package com.example.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.models.User;

public class UserDAO {

    private final Context context;

    public UserDAO(Context context) {
        this.context =
                context.getApplicationContext();
    }

    /**
     * Kiểm tra đăng nhập bằng Username và Password.
     *
     * @return User nếu đăng nhập đúng, null nếu sai.
     */
    public User login(
            String username,
            String password
    ) {
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database =
                    DatabaseHelper.openDatabase(context);

            String sql =
                    "SELECT " +
                            "UserID, " +
                            "Username, " +
                            "FullName, " +
                            "Email, " +
                            "Type " +
                            "FROM Users " +
                            "WHERE Username = ? " +
                            "AND Password = ? " +
                            "LIMIT 1";

            String[] selectionArgs = {
                    username,
                    password
            };

            cursor = database.rawQuery(
                    sql,
                    selectionArgs
            );

            if (!cursor.moveToFirst()) {
                return null;
            }

            User user = new User();

            user.setUserID(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "UserID"
                            )
                    )
            );

            user.setUsername(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Username"
                            )
                    )
            );

            user.setFullName(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "FullName"
                            )
                    )
            );

            user.setEmail(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Email"
                            )
                    )
            );

            user.setType(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Type"
                            )
                    )
            );

            return user;

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Không thể đăng nhập: "
                            + exception.getMessage(),
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
     * Lấy lại thông tin User từ UserID đã lưu trong Session.
     */
    public User getUserByID(int userID) {
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database =
                    DatabaseHelper.openDatabase(context);

            String sql =
                    "SELECT " +
                            "UserID, " +
                            "Username, " +
                            "FullName, " +
                            "Email, " +
                            "Type " +
                            "FROM Users " +
                            "WHERE UserID = ? " +
                            "LIMIT 1";

            cursor = database.rawQuery(
                    sql,
                    new String[]{
                            String.valueOf(userID)
                    }
            );

            if (!cursor.moveToFirst()) {
                return null;
            }

            User user = new User();

            user.setUserID(
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow(
                                    "UserID"
                            )
                    )
            );

            user.setUsername(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Username"
                            )
                    )
            );

            user.setFullName(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "FullName"
                            )
                    )
            );

            user.setEmail(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Email"
                            )
                    )
            );

            user.setType(
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "Type"
                            )
                    )
            );

            return user;

        } catch (Exception exception) {
            throw new RuntimeException(
                    "Không thể lấy thông tin User: "
                            + exception.getMessage(),
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
}