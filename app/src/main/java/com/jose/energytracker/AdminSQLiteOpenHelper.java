package com.jose.energytracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AdminSQLiteOpenHelper  extends SQLiteOpenHelper {


    public AdminSQLiteOpenHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
    }


    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("create table alimentos (nombre String primary key, kcal int)");
        bd.execSQL("create table diario (nombre String primary key, kcal int, uds int)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


}