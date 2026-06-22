package com.example.accountbook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "account_book.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    private static final String TABLE_RECORDS = "records";

    // 列名
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";

    private static DatabaseHelper instance;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_RECORDS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TYPE + " TEXT,"
                + COLUMN_CATEGORY + " TEXT,"
                + COLUMN_AMOUNT + " REAL,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT"
                + ")";
        db.execSQL(createTable);
        Log.d("DatabaseHelper", "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    // 插入记录
    public long insertRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, record.getType());
        values.put(COLUMN_CATEGORY, record.getCategory());
        values.put(COLUMN_AMOUNT, record.getAmount());
        values.put(COLUMN_NOTE, record.getNote());
        values.put(COLUMN_DATE, record.getDate());
        values.put(COLUMN_TIME, record.getTime());

        long id = db.insert(TABLE_RECORDS, null, values);
        db.close();
        return id;
    }

    // 获取所有记录
    public List<Record> getAllRecords() {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RECORDS + " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                record.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                record.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                record.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                record.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // 获取按日期分组的记录
    public List<Record> getRecordsByDate(String date) {
        List<Record> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_RECORDS + " WHERE " + COLUMN_DATE + " = ? ORDER BY " + COLUMN_TIME + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{date});

        if (cursor.moveToFirst()) {
            do {
                Record record = new Record();
                record.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                record.setType(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                record.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)));
                record.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));
                record.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
                record.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    // 获取收入总和
    public double getTotalIncome() {
        return getTotalByType("income");
    }

    // 获取支出总和
    public double getTotalExpense() {
        return getTotalByType("expense");
    }

    private double getTotalByType(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_RECORDS + " WHERE " + COLUMN_TYPE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{type});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // 删除记录
    public void deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // 更新记录
    public int updateRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, record.getType());
        values.put(COLUMN_CATEGORY, record.getCategory());
        values.put(COLUMN_AMOUNT, record.getAmount());
        values.put(COLUMN_NOTE, record.getNote());
        values.put(COLUMN_DATE, record.getDate());
        values.put(COLUMN_TIME, record.getTime());

        int rowsAffected = db.update(TABLE_RECORDS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(record.getId())});
        db.close();
        return rowsAffected;
    }
}