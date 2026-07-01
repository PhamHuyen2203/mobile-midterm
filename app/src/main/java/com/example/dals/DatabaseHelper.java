package com.example.dals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DatabaseHelper {

    public static final String DATABASE_NAME =
            "MCommerce.sqlite";

    private static final int BUFFER_SIZE = 8192;

    private DatabaseHelper() {
    }

    public static synchronized void copyDatabaseIfNeeded(
            Context context
    ) throws IOException {

        Context appContext =
                context.getApplicationContext();

        File databaseFile =
                appContext.getDatabasePath(
                        DATABASE_NAME
                );

        if (databaseFile.exists()
                && databaseFile.length() > 0) {
            return;
        }

        File parentDirectory =
                databaseFile.getParentFile();

        if (parentDirectory != null
                && !parentDirectory.exists()) {

            boolean created =
                    parentDirectory.mkdirs();

            if (!created
                    && !parentDirectory.exists()) {
                throw new IOException(
                        "Không thể tạo thư mục database."
                );
            }
        }

        try (
                InputStream inputStream =
                        appContext
                                .getAssets()
                                .open(DATABASE_NAME);

                OutputStream outputStream =
                        new FileOutputStream(databaseFile)
        ) {
            byte[] buffer =
                    new byte[BUFFER_SIZE];

            int length;

            while ((length =
                    inputStream.read(buffer)) > 0) {

                outputStream.write(
                        buffer,
                        0,
                        length
                );
            }

            outputStream.flush();
        }
    }

    public static SQLiteDatabase openDatabase(
            Context context
    ) throws IOException {

        copyDatabaseIfNeeded(context);

        File databaseFile =
                context.getApplicationContext()
                        .getDatabasePath(
                                DATABASE_NAME
                        );

        SQLiteDatabase database =
                SQLiteDatabase.openDatabase(
                        databaseFile.getAbsolutePath(),
                        null,
                        SQLiteDatabase.OPEN_READWRITE
                );

        database.execSQL(
                "PRAGMA foreign_keys = ON"
        );

        return database;
    }
}