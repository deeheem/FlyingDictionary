package com.developers.dictionary.flying;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper{

    public static final  String DB_NAME = "HistFav.db";
    public static final  int DB_VER = 1;

    public interface Consts{
        String LBR= " ( ";
        String RBR= " ) ";
        String COMMA = " , ";
        String SEMCOL = " ; ";
        String TYPE_INT = " integer ";
        String TYPE_BOOLEAN = "boolean";
        String TYPE_TEXT = " text ";
        String TYPE_PK = " primary key ";
        String TYPE_AI = " autoincrement ";



    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME,null,DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Words.CMD_TABLE_CREATE_FAV);
        db.execSQL(Words.CMD_TABLE_CREATE_HIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

