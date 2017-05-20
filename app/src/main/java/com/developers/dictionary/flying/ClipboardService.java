package com.developers.dictionary.flying;

import android.app.Service;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gurtej on 11/1/17.
 */

public class ClipboardService extends Service {
    private ClipboardManager mCM;
    public static ArrayList<DictObjectModel> data=new ArrayList<DictObjectModel>();
    public static final String TAG="MYLOGS";
    SharedPreferences spref;

    @Override
    public void onCreate() {
        super.onCreate();
        spref=getSharedPreferences("MyPrefs",getApplicationContext().MODE_PRIVATE );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCM= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mCM.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String newclip=mCM.getText().toString();
//                && newclip.toLowerCase().compareToIgnoreCase(spref.getString("query",""))!=0
                if(newclip!="") {
//                    Log.e(TAG, "onPrimaryClipChanged: "+spref.getString("query",""));
//                    spref.edit().putString("query",newclip.toLowerCase()).apply();
                    int flag=1;
                    String s=newclip;
                    s=s.toLowerCase();
                    for(int j=0;j<newclip.length();j++)
                    {
                        if ((s.charAt(j)>='A'&& s.charAt(j)<='Z') || (s.charAt(j)>='a'&& s.charAt(j)<='z') || s.charAt(j)=='\'' || s.charAt(j)=='.' || (s.charAt(j)>='0'&& s.charAt(j)<='9') || s.charAt(j)=='-')
                            continue;
                        else {
                            flag=0;
                            break;
                        }
                    }
                    Log.e(TAG, "ok che k" +(System.currentTimeMillis()-spref.getLong("lastStringTime",0)) );

                    if (flag == 1 && (System.currentTimeMillis()-spref.getLong("lastStringTime",0))>1000) {
                        spref.edit().putLong("lastStringTime",System.currentTimeMillis()).apply();
                        SQLiteDatabase db = SQLiteDatabase.openDatabase(LogoActivity.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
                        Cursor cursor1 = db.rawQuery("SELECT * FROM words WHERE lemma = ?", new String[]{s});
                        cursor1.moveToFirst();
                        Log.d(TAG, "onPrimaryClipChanged: myyyyyyyyyyy"+cursor1.getCount());
                        if (cursor1.getCount() ==0) {
                            //cursor is empty
                        } else {
                            Log.d(TAG, "onPrimaryClipChanged: just uuuu");
                            Intent i = new Intent(ClipboardService.this, PopUpActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
/*
///                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            Log.d(TAG, "pIsOpen: "+spref.getBoolean("pIsOpen",false));
                            Log.d(TAG, "wIsOpen: "+spref.getBoolean("wIsOpen",false));
*/
                            if(spref.getBoolean("mIsOpen",false)==false && spref.getBoolean("wIsOpen",false)==false ) {
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            }
/*
                            ActivityManager am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            am.getRunningAppProcesses();
                            if(am.)
*/
                            i.putExtra("check", flag);
                            i.putExtra("guru", s);
                            i.putExtra("dimi", getMeaning(s));
                            startActivity(i);
                        }
                        cursor1.close();
                        db.close();
                    }
                    Log.d(TAG, "onPrimaryClipChanged: "+ s);
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    String getPos (String pos) {

        String s1 = "<b><font color = '#606062'>";
        String s2 = "</font></b>";

        if (pos.equals("n")) {
            return s1+"noun"+s2;
        } else if (pos.equals("v")) {
            return s1+"verb"+s2;
        } else if (pos.equals("a")) {
            return s1+"adjective"+s2;
        } else if (pos.equals("s")) {
            return s1+"adjective satellite"+s2;
        } else if (pos.equals("r")) {
            return s1+"adverb"+s2;
        }

        return "";
    }

    String getMeaning(String query) {
        Log.d("SA", "getMeaning: GETMEANING CALLED2");

        String s1 = "<font color = '#606062'>";
        String s2 = "</font>";
        String s3 = "<b><font color = '#0097a7'>";
        String s4 = "</font></b>";

        File file=new File(LogoActivity.DB_PATH);
        if(file.exists() && !file.isDirectory())
        {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(LogoActivity.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
            Cursor cursor1 = db.rawQuery("SELECT \n" +
                    "    a.lemma AS 'word',\n" +
                    "    c.definition,\n" +
                    "    c.pos AS 'part of speech',\n" +
                    "    d.sample AS 'example sentence',\n" +
                    "    (SELECT \n" +
                    "            GROUP_CONCAT(a1.lemma)\n" +
                    "        FROM\n" +
                    "            words a1\n" +
                    "                INNER JOIN\n" +
                    "            senses b1 ON a1.wordid = b1.wordid\n" +
                    "        WHERE\n" +
                    "            b1.synsetid = b.synsetid\n" +
                    "                AND a1.lemma <> a.lemma\n" +
                    "        GROUP BY b1.synsetid) AS `synonyms`  \n" +
                    "    \n" +
                    "FROM\n" +
                    "    words a\n" +
                    "        INNER JOIN\n" +
                    "    senses b ON a.wordid = b.wordid\n" +
                    "        INNER JOIN\n" +
                    "    synsets c ON b.synsetid = c.synsetid\n" +
                    "        INNER JOIN\n" +
                    "    samples d ON b.synsetid = d.synsetid\n" +
                    "WHERE\n" +
                    "    a.lemma = ?   \n" +
                    "ORDER BY a.lemma , c.definition , d.sample;", new String[]{query});

            if (!(cursor1.moveToFirst()) || cursor1.getCount() ==0) {
                //cursor is empty
                Cursor cursor2 = db.rawQuery("SELECT \n" +
                        "    a.lemma AS 'word',\n" +
                        "    c.definition,\n" +
                        "    c.pos AS 'part of speech',\n" +
                        "    (SELECT \n" +
                        "            GROUP_CONCAT(a1.lemma)\n" +
                        "        FROM\n" +
                        "            words a1\n" +
                        "                INNER JOIN\n" +
                        "            senses b1 ON a1.wordid = b1.wordid\n" +
                        "        WHERE\n" +
                        "            b1.synsetid = b.synsetid\n" +
                        "                AND a1.lemma <> a.lemma\n" +
                        "        GROUP BY b1.synsetid) AS `synonyms`  \n" +
                        "    \n" +
                        "FROM\n" +
                        "    words a\n" +
                        "        INNER JOIN\n" +
                        "    senses b ON a.wordid = b.wordid\n" +
                        "        INNER JOIN\n" +
                        "    synsets c ON b.synsetid = c.synsetid\n" +
                        "WHERE\n" +
                        "    a.lemma = ? \n" +
                        "ORDER BY a.lemma , c.definition ;", new String[]{query});

                if (!(cursor2.moveToFirst()) || cursor2.getCount() ==0) {
                    //cursor is empty
                    Log.d("NW", "onClick: NOT WORKINGGGGG");
                } else {

                    boolean f = false;
                    int count = 0;

                    StringBuilder definition = new StringBuilder();
                    StringBuilder pos = new StringBuilder();
                    StringBuilder synonyms = new StringBuilder();

                    StringBuilder resList = new StringBuilder();

                    cursor2.moveToFirst();
                    int cnt = cursor2.getCount();

                    String w="", d="", p="", s=null;

                    while(cnt>0) {
                        cnt--;

                        w = cursor2.getString(0);
                        d = cursor2.getString(1);
                        p = cursor2.getString(2);
                        s = cursor2.getString(3);

                        if (!d.equals(definition.toString()) && f) {
                            Log.d("definition", "onClick: "+d);
                            //print previous contents
                            if (s == null || s == "" || s=="null") {
                                resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + "\n");
                                Log.d("definition ", "onClick: OKKKK");
                            } else {
                                resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                            }
                            //refresh new ones
                            count++;
                            definition = new StringBuilder();
                            definition.append(d);

                            pos = new StringBuilder();
                            pos.append(getPos(p));

                            synonyms = new StringBuilder();

                            if (s!="" && s!=null && !synonyms.toString().equals(s)) {
                                synonyms.append(s);
                            }

                        } else {
                            //append data

                            if (s!="" && s!=null && !synonyms.toString().equals(s)) {
                                synonyms.append(s);
                            }
                            if(!f) {
                                definition.append(d);
                                count++;
                            }
                        }

                        f = true;

                        cursor2.moveToNext();
                    } //while (cursor.moveToNext());

                    if (s == null || s == "" || s=="null") {
                        resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + "\n");
                        Log.d("definition ", "onClick: OKKKK");
                    } else {
                        resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                    }


                    cursor2.close();
                    db.close();

                    return (resList.toString());

                }

            } else {

                boolean f = false;
                int count = 0;

                StringBuilder definition = new StringBuilder();
                StringBuilder pos = new StringBuilder();
                StringBuilder example = new StringBuilder();
                StringBuilder synonyms = new StringBuilder();

                StringBuilder resList = new StringBuilder();

                cursor1.moveToFirst();
                int cnt = cursor1.getCount();

                String w="", d="", p="", e="", s=null;


                while(cnt>0) {
                    cnt--;

                    w = cursor1.getString(0);
                    d = cursor1.getString(1);
                    p = cursor1.getString(2);
                    e = cursor1.getString(3);
                    s = cursor1.getString(4);

                    if (!d.equals(definition.toString()) && f) {
                        Log.d("definition", "onClick: "+d);
                        //print previous contents
                        if (s == null || s == "" || s=="null" || synonyms.toString() == null) {
                            resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                            if (s!=null)
                                Log.d("definition ", "onClick: OKKKK"+s+".");
                        } else {
                            resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                            Log.d("SA", "getMeaning: SYNONYM EXISTS");
                        }
                        //refresh new ones
                        count++;
                        definition = new StringBuilder();
                        definition.append(d);

                        pos = new StringBuilder();
                        pos.append(getPos(p));

                        example = new StringBuilder();
                        example.append("-"+e+"\n");

                        synonyms = new StringBuilder();

                        if (s!="" && s!=null && !synonyms.toString().equals(s)) {
                            synonyms.append(s);
                        }

                    } else {
                        //append data
                        example.append("-"+e+"\n");

                        if (s!="" && s!=null && !synonyms.toString().equals(s)) {
                            synonyms.append(s);
                        }
                        if(!f) {
                            definition.append(d);
                            count++;
                        }
                    }

                    f = true;

                    cursor1.moveToNext();
                } //while (cursor.moveToNext());
                if (s == null || s == "" || s=="null"|| synonyms.toString() == null) {
                    resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                    if (s!=null)
                        Log.d("definition ", "onClick: OKKKK"+s+".");
                } else {
                    resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                    Log.d("SA", "getMeaning: SYNONYM EXISTS");
                }


                cursor1.close();
                db.close();
                return resList.toString();

            }
        }
        else
        {
            Toast.makeText(this, "Database does not exist!", Toast.LENGTH_LONG).show();
        }
        return "";
    }
}
