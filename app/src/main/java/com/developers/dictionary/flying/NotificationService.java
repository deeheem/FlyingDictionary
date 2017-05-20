package com.developers.dictionary.flying;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import static android.content.ContentValues.TAG;

/**
 * Created by gurtej on 8/2/17.
 */
public class NotificationService extends IntentService {

    private NotificationManager notificationManager;
    private PendingIntent pendingIntent;
    private static int NOTIFICATION_ID = 1;
    Notification notification;
    public NotificationService() {
        super("MIS");
    }
    SharedPreferences spref;

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = this.getApplicationContext();
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, PopUpActivity.class);
        String s=getWord();
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);

        mIntent.putExtra("guru",s);
        mIntent.putExtra("dimi",getMeaning(s));
        mIntent.putExtra("check",1);
        mIntent.putExtra("addToHistory",0);
/*
        Bundle bundle = new Bundle();
        bundle.putString("test", "test");
        mIntent.putExtras(bundle);
*/
        pendingIntent = PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = this.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        notification = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notify_logo)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.logo))
                .setTicker("ticker value")
                .setAutoCancel(true)
                .setPriority(8)
                .setSound(soundUri)
                .setContentTitle("Word of the Day")
                .setContentText(s).build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        notification.ledARGB = 0xFFFFA500;
        notification.ledOnMS = 800;
        notification.ledOffMS = 1000;
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        spref.getBoolean(SettingsActivity.NOTIFY_CHECK,true);
        Log.e(TAG, "onHandleIntent: "+SettingsActivity.NOTIFY_CHECK);
        if(spref.getBoolean(SettingsActivity.NOTIFY_CHECK,true)==true)                   //TODO: OPTIMISE
        notificationManager.notify(NOTIFICATION_ID, notification);
        Log.i("notif","Notifications sent.");
    }

    private String getWord() {
        String s="";
        File file=new File(LogoActivity.DB_PATH);
        if(file.exists() && !file.isDirectory()) {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(LogoActivity.DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
            Cursor cursor = db.query("words", new String[]{"lemma"}, null, null, null, null, "random() limit 1");
            cursor.moveToFirst();
            s = cursor.getString(0);
            Log.e(TAG, "getWord: " + s);
            cursor.close();
            db.close();
        }
        return s;
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
