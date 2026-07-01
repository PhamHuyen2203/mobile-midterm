package com.example.mcommercemobile06;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dals.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DATABASE_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        testDatabase();
    }

    private void testDatabase() {
        SQLiteDatabase database = null;
        Cursor cursor = null;

        try {
            database =
                    DatabaseHelper.openDatabase(this);

            cursor = database.rawQuery(
                    "SELECT name " +
                            "FROM sqlite_master " +
                            "WHERE type = 'table' " +
                            "AND name NOT LIKE 'sqlite_%' " +
                            "ORDER BY name",
                    null
            );

            StringBuilder result =
                    new StringBuilder();

            while (cursor.moveToNext()) {
                String tableName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "name"
                                )
                        );

                result.append(tableName)
                        .append("\n");
            }

            Log.d(
                    TAG,
                    "Danh sách bảng:\n" + result
            );

            Toast.makeText(
                    this,
                    "Mở database thành công",
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception exception) {
            Log.e(
                    TAG,
                    "Lỗi database",
                    exception
            );

            Toast.makeText(
                    this,
                    "Lỗi: " + exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

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