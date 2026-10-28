
package com.example.bookmarkmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_NAME = "Bookmarks.db";
    String TABLE = "bookmarks";

    public DatabaseHelper(Context context) {
        super(context, "Bookmarks.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "url TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {

    }

    // INSERT
    public long addBookmark(String title, String url) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);

        return db.insert(TABLE, null, values);
    }

    // READ
    public Cursor getAllBookmarks() {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, title, url FROM " + TABLE +
                        " ORDER BY id DESC", null);
    }

    // UPDATE
    public void updateBookmark(int id, String title, String url) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);

        db.update(TABLE, values, "id=?",
                new String[]{String.valueOf(id)});
    }

    // DELETE
    public void deleteBookmark(int id) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE, "id=?",
                new String[]{String.valueOf(id)});
    }
}
