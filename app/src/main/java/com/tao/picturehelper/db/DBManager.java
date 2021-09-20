package com.tao.picturehelper.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    public static SQLiteDatabase database;
    public static void initDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }
    public static List<PictureWord>queryAllPicture(){
        Cursor cursor = database.query("picture", null, null, null, null, null,null);
        List<PictureWord>pictureWordsList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String path = cursor.getString(cursor.getColumnIndex("path"));
            String word = cursor.getString(cursor.getColumnIndex("word"));
            PictureWord pictureWord = new PictureWord();
            pictureWord.setPath(path);
            pictureWord.setWord(word);
            pictureWordsList.add(pictureWord);
        }
        return pictureWordsList;
    }

    public static int updateToken(String token, Date exireTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time_string = simpleDateFormat.format(exireTime);
        ContentValues values = new ContentValues();
        values.put("token",token);
        values.put("expireDate",time_string);

        int token1 = database.update("token", values, "word=?",new String[]{"x"} ); return token1;
    }

    public static int udpateUserVipLevel(String username, int vipLevel){
        ContentValues values = new ContentValues();
        values.put("username",username);
        values.put("vipLevel",vipLevel);

        int token1 = database.update("user", values, "username=?",new String[]{username} ); return token1;
    }
    public static int udpateVip(String vipLevel,String price,String picNum,String dollarPrice){
        ContentValues values = new ContentValues();
        values.put("vipLevel",vipLevel);
        values.put("price",price);
        values.put("dollarPrice",dollarPrice);
        values.put("picNum",picNum);
        int token1 = database.update("vips", values, "vipLevel=?",new String[]{vipLevel} ); return token1;
    }
    public static long addPicture(String path,String word){
        ContentValues values = new ContentValues();
        values.put("path",path);
        values.put("word",word);
        return database.insert("picture",null,values);
    }
    public static long addUser(String username,int vipLevel){
        ContentValues values = new ContentValues();
        values.put("username",username);
        values.put("vipLevel",vipLevel);
        return database.insert("user",null,values);
    }
    public static long addVip(String vipLevel,String price,String pic_num,String dollarPrice){
        ContentValues values = new ContentValues();
        values.put("vipLevel",vipLevel);
        values.put("price",price);
        values.put("dollarPrice",dollarPrice);
        values.put("picNum",pic_num);
        return database.insert("vips",null,values);
    }
    public static Map<String, Object> queryUser(){
        Map<String,Object> map = new HashMap<>();
        Cursor cursor = database.query("user", null, null, null, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String vipLevel = cursor.getString(cursor.getColumnIndex("vipLevel"));
            map.put("username",username);
            map.put("vipLevel",vipLevel);
            return map;
        }
        return null;
    }
    public static long addToken(String token,Date expireTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time_str = simpleDateFormat.format(expireTime);
        ContentValues values = new ContentValues();
        values.put("token",token);
        values.put("expireDate",time_str);
        values.put("word","x");
        return database.insert("token",null,values);
    }
    public static long addPath(String path){
        ContentValues values = new ContentValues();
        values.put("path",path);
        return database.insert("paths",null,values);
    }

    public static String queryPictureByWord(String word){
        Cursor cursor = database.query("picture", null, "word like '%"+word+"%'", null, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("word"));
            return content;
        }
        return null;
    }
    public static String queryTokenByWord(){
        Cursor cursor = database.query("token", null, "word like '%"+"x"+"%'", null, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("token"));
            return content;
        }
        return null;
    }
    public static String queryPicrureByPath(String path){
        Cursor cursor = database.query("picture", null, "path=?", new String[]{path}, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("word"));
            return content;
        }
        return null;
    }
    public static String queryPathByPath(String path){
        Cursor cursor = database.query("paths", null, "path=?", new String[]{path}, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("path"));
            return content;
        }
        return null;
    }
    public static Map<String, String> queryVipByVipLevel(String vipLevel){
        Map<String,String> map = new HashMap<>();
        Cursor cursor = database.query("vips", null, "vipLevel=?", new String[]{vipLevel}, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String price = cursor.getString(cursor.getColumnIndex("price"));
            String dollarPrice = cursor.getString(cursor.getColumnIndex("dollarPrice"));
            String pic_num = cursor.getString(cursor.getColumnIndex("picNum"));
            map.put("price",price);
            map.put("picNum",pic_num);
            map.put("dollarPrice",dollarPrice);
            return map;
        }
        return null;
    }

    public static String queryAllUser(){
        Cursor cursor = database.query("picture", null, null, null, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex("username"));
            return username;
        }
        return null;
    }
    public static int getPictureCount(){
        Cursor cursor = database.query("picture", null, null, null, null, null, null);
        int count = cursor.getCount();
        return count;
    }
    public static int getTokenCount(){
        Cursor cursor = database.query("token", null, null, null, null, null, null);
        int count = cursor.getCount();
        return count;
    }

    public static List<PictureWord>queryAllpicture(){
        Cursor cursor = database.query("picture", null, null, null, null, null, null);
        List<PictureWord>list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            PictureWord bean = new PictureWord(id, city, content);
            list.add(bean);
        }
        return list;
    }
    public static List<Map<String,Object>>queryAllToken(){
        List<Map<String,Object>> list = new ArrayList<>();
        Cursor cursor = database.query("token", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String token = cursor.getString(cursor.getColumnIndex("token"));
            String expireTime = cursor.getString(cursor.getColumnIndex("expireDate"));
            Map<String,Object> map = new HashMap<>();
            map.put("token",token);
            map.put("expireTime",expireTime);
            list.add(map);
        }
        return list;
    }
    public static List<String> queryAllPath(){
        List<String> list = new ArrayList<>();
        Cursor cursor = database.query("paths", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String paht = cursor.getString(cursor.getColumnIndex("path"));
            list.add(paht);
        }
        return list;
    }

    public static int deletepictureByCity(String city){
        return database.delete("picture","city=?",new String[]{city});
    }

    public static void deleteAllpicture(){
        String sql = "delete from picture";
        database.execSQL(sql);
    }
    public static void deleteAllUser(){
        String sql = "delete from user";
        database.execSQL(sql);
    }
    public static void deleteAllPaths(){
        String sql = "delete from paths";
        database.execSQL(sql);
    }
    public static void deleteAllVips(){
        String sql = "delete from vips";
        database.execSQL(sql);
    }
}
