package com.example.mycontacts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mycontacts.Contact.ContactEntry;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "todaycontact.db";
    public static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_TABLE = "CREATE TABLE " + ContactEntry.TABLE_NAME + " ("
                + ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // 主键自增
                + ContactEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_PHONENUMBER + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_WORKPLACE + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_HOMEPLACE + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_TYPEOFCONTACT + " TEXT NOT NULL, "
                + ContactEntry.COLUMN_PICTURE  + " TEXT);";

        sqLiteDatabase.execSQL(SQL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
