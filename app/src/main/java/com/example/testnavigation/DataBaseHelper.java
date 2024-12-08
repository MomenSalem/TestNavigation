package com.example.testnavigation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(EMAIL_ADDRESS TEXT PRIMARY KEY, FIRST_NAME TEXT, LAST_NAME TEXT, PASSWORD TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public void insertUser(User user) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("EMAIL_ADDRESS", user.getEmailAddress());
        contentValues.put("FIRST_NAME", user.getFirstName());
        contentValues.put("LAST_NAME", user.getLastName());
        contentValues.put("PASSWORD", user.getPassword());
        sqLiteDatabase.insert("user", null, contentValues);

         // Delete
//        sqLiteDatabase.execSQL("DELETE FROM user");

    }

    public Cursor getAllUsers() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM user", null);
    }

    public Cursor getUser(String emailAddress) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        return sqLiteDatabase.rawQuery("SELECT * FROM user WHERE EMAIL_ADDRESS = ?", new String[]{emailAddress});
    }

    public boolean checkUserExists(String emailAddress) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE EMAIL_ADDRESS = ?", new String[]{emailAddress});
        return cursor.getCount() > 0;
    }
}
