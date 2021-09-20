package com.tao.picturehelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context){
        super(context,"picture_helper.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建表的操作
        String sql = "create table picture(_id INTEGER PRIMARY KEY AUTOINCREMENT,path varchar(100) unique not null,word text not null)";

        db.execSQL(sql);
        String sql2 = "create table token(_id INTEGER PRIMARY KEY AUTOINCREMENT,token varchar(1000) unique not null,word text not null,expireDate text not null)";
        db.execSQL(sql2);
        String sql3 = "create table user(_id INTEGER PRIMARY KEY AUTOINCREMENT,username varchar(100) unique not null,vipLevel int)";
        db.execSQL(sql3);
        String sql4 = "create table paths(_id INTEGER PRIMARY KEY AUTOINCREMENT,path varchar(1000) unique not null)";
        db.execSQL(sql4);
        String sql5 = "create table vips(_id INTEGER PRIMARY KEY AUTOINCREMENT,vipLevel varchar(10) not null,price varchar(10) not null,picNum varchar(10) not null,dollarPrice varchar(10) not null)";
        db.execSQL(sql5);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
