package com.andbot.villbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLDataException;

public class Database_manager {
    private DatabaseHelper dbhelper;
    private Context context;
    private SQLiteDatabase database;

    public Database_manager(Context context) {
        this.context = context;
    }

    public Database_manager open() throws SQLDataException {
        dbhelper = new DatabaseHelper(context);
        database = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
    }

    public void insert(String Name, String Gender){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.COL_1, Name);
        contentValues.put(DatabaseHelper.COL_2,Gender);
        Log.d("Fetch", String.valueOf(fetch()));
        database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
    }
    public Cursor fetch(){
        String [] columns = new String[] {DatabaseHelper.COL_1,DatabaseHelper.COL_2};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,columns,null,null,null,null,null);
        if(cursor != null)
            cursor.moveToNext();
        return cursor;
    }
}
