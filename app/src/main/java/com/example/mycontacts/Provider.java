package com.example.mycontacts;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.net.URI;

public class Provider extends ContentProvider {

    public static final int CONTACTS = 100;
    public static final int CONTACTS_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_CONTACTS, CONTACTS);
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_CONTACTS + "/#", CONTACTS_ID); // 代表列名的#
    }

    public DbHelper mDbHelper;  // DhHelper

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // 查询方法只需要读，拿可读就够了
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // 初始化cursor
        Cursor cursor;

        // 查询可能会查到多个结果或单个结果
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                cursor = database.query(Contract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case CONTACTS_ID:
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.ContactEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cant Query" + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        // 插入方法只能有一种匹配
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return insertContact(uri, values);

            default:
                throw new IllegalArgumentException("Can't insert a new contact" + uri);
        }

    }

    private Uri insertContact(Uri uri, ContentValues values) {

        String name = values.getAsString(Contract.ContactEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Name is required");
        }

        String number = values.getAsString(Contract.ContactEntry.COLUMN_PHONENUMBER);
        if (number == null) {
            throw new IllegalArgumentException("number is required");
        }

        String email = values.getAsString(Contract.ContactEntry.COLUMN_EMAIL);
        if (email == null) {
            throw new IllegalArgumentException("email is required");
        }

        String work = values.getAsString(Contract.ContactEntry.COLUMN_WORKPLACE);
        if (work == null) {
            throw new IllegalArgumentException("workplace is required");
        }

        String home = values.getAsString(Contract.ContactEntry.COLUMN_HOMEPLACE);
        if (home == null) {
            throw new IllegalArgumentException("homeplace is required");
        }

        String type = values.getAsString(Contract.ContactEntry.COLUMN_TYPEOFCONTACT);
        if (type == null || !Contract.ContactEntry.isValidType(type)) { // 如果类型有错误，同样也不能执行插入
            throw new IllegalArgumentException("type is required");
        }

        // 要插入新数据，所以要拿到可写的
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Contract.ContactEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                rowsDeleted = database.delete(Contract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CONTACTS_ID:
                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.ContactEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Can't execute delete" + uri);

        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // 同样可以一行一行更新
        int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTACTS:
                return updateContact(uri, values, selection, selectionArgs);

            case CONTACTS_ID:

                selection = Contract.ContactEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateContact(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Can't update the contact");


        }
    }

    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Contract.ContactEntry.COLUMN_NAME)) {
            String name = values.getAsString(Contract.ContactEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Name is required");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_PHONENUMBER)) {

            String number = values.getAsString(Contract.ContactEntry.COLUMN_PHONENUMBER);
            if (number == null) {
                throw new IllegalArgumentException("number is required");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_EMAIL)) {
            String email = values.getAsString(Contract.ContactEntry.COLUMN_EMAIL);
            if (email == null) {
                throw new IllegalArgumentException("email is required");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_WORKPLACE)) {
            String work = values.getAsString(Contract.ContactEntry.COLUMN_WORKPLACE);
            if (work == null) {
                throw new IllegalArgumentException("work place is required");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_HOMEPLACE)) {
            String home = values.getAsString(Contract.ContactEntry.COLUMN_HOMEPLACE);
            if (home == null) {
                throw new IllegalArgumentException("home place is required");
            }
        }

        if (values.containsKey(Contract.ContactEntry.COLUMN_TYPEOFCONTACT)) {
            String type = values.getAsString(Contract.ContactEntry.COLUMN_TYPEOFCONTACT);
            if (type == null || !Contract.ContactEntry.isValidType(type)) {
                throw new IllegalArgumentException("type is required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(Contract.ContactEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}