package com.developers.dictionary.flying;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.developers.dictionary.flying.DatabaseHelper.Consts.*;

/**
 * Created by gurtej on 6/2/17.
 */

public class Words {

    public static final String TABLE_NAME_HIST="history";
    public static final String TABLE_NAME_FAV="favourites";

    public interface Columns{
        String TIME="time";
        String LEMMA="word";
    }

    public static final String CMD_TABLE_CREATE_HIST=
            "CREATE TABLE "+ TABLE_NAME_HIST +
                    LBR +
                    Columns.LEMMA+ TYPE_TEXT +  COMMA +
                    Columns.TIME +TYPE_TEXT +
                    RBR +SEMCOL;

    public static final String CMD_TABLE_CREATE_FAV=
            "CREATE TABLE "+ TABLE_NAME_FAV +
                    LBR +
                    Columns.LEMMA+ TYPE_TEXT +  COMMA +
                    Columns.TIME +TYPE_TEXT +
                    RBR +SEMCOL;

    public static boolean addWord(String word, String timestamp , String table_name , SQLiteDatabase db)
    {
        if(db.isReadOnly())
            return false;
        ContentValues taskObj=new ContentValues();
        taskObj.put(Columns.LEMMA,word);
        taskObj.put(Columns.TIME,timestamp);
        Log.d(TAG, "addWord: "+word);
            db.insert(table_name,null,taskObj);
        return true;
    }

    public static String getTime(String word, String table_name, SQLiteDatabase db) //TODO: REDUNDANT
    {
            String whereclause= Columns.LEMMA +" = ?";
        Cursor cursor=db.query(table_name,new String[]{Columns.TIME},whereclause,new String[]{word},null,null,null);
        cursor.moveToFirst();
        String s=cursor.getString(cursor.getColumnIndex(Columns.TIME));
        cursor.close();
        db.close();
        return s;
    }


    public static boolean updateTime(String word, String timestamp , String table_name ,SQLiteDatabase db)
    {
        if(db.isReadOnly())
            return false;

        Log.d(TAG, "UpdateTime: "+word);
        db.rawQuery(" update " +table_name+ " set " + Columns.TIME + " = ? "  + " where " +Columns.LEMMA + " = ? " ,new String[] {timestamp,word});
        return true;
    }
    public static ArrayList<String> getAllWords(String table_name,SQLiteDatabase db, int order_by)
    {
        String[] PROJECTION={
                Columns.LEMMA,Columns.TIME
        };
        String order_String;

        if(order_by==1)
            order_String=Columns.TIME +" DESC ";
        else
            order_String=Columns.LEMMA ;

        Cursor cursor=db.query(
                table_name,
                PROJECTION,
                null,null,null,null,order_String
        );

        Log.d(TAG, "getAllWords: ");
        ArrayList<String> wordArrayList=new ArrayList<>();
        if(cursor.getCount()!=0) {
            cursor.moveToFirst();
            int lemmaIndex = cursor.getColumnIndex(Columns.LEMMA);
            Log.d(TAG, "LEMMAINDEX: "+lemmaIndex);
            int timeIndex = cursor.getColumnIndex(Columns.TIME);

            do {
                Log.e(TAG, "getAllWords: " + cursor.getString(lemmaIndex));
                wordArrayList.add(cursor.getString(lemmaIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wordArrayList;
    }

    public static boolean find(String table_name, String qLemma, SQLiteDatabase db) {
        Cursor cursor=db.query(table_name,new String[]{ "count"+ LBR + Columns.LEMMA  +RBR}, Columns.LEMMA +" = ? ",new String[] {qLemma},
        null,null,null);

        cursor.moveToFirst();
        if(cursor.getCount()==0 || cursor.getInt(0)==0)
            return false;
        return true;
    }
    public static boolean dropall(String table_name,SQLiteDatabase db)
    {
            db.rawQuery(" drop "+ table_name,null);
        return true;
    }

    public static boolean removeWord(String table_name, String qlemma, SQLiteDatabase db)
    {
        if(db.isReadOnly())
            return false;
        String whereClause=Columns.LEMMA + " = ? ";
        db.delete(table_name,
                whereClause,
                new String[]{
                        qlemma
                });
        return true;

    }
    public static boolean removeAll(String table_name,SQLiteDatabase db)
    {
        if(db.isReadOnly())
            return false;
//        String whereClause=Columns.LEMMA + " = ? ";
        db.delete(table_name,null,null);
        return true;

    }

}
