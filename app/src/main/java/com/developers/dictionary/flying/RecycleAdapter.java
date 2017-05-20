package com.developers.dictionary.flying;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

/**
 * Created by gurtej on 18/1/17.
 */

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.wordHolder> {
     String table_name;
    Context context;
    ArrayList<String> favList;
    String s;
    int ORDER_BY;
    DatabaseHelper dbHelper;
    public RecycleAdapter( Context context, ArrayList<String> favList, String table_name, DatabaseHelper dbHelper,int ORDER_BY) {
        this.context = context;
        this.favList = favList;
        this.table_name=table_name;
        this.dbHelper=dbHelper;
        this.ORDER_BY=ORDER_BY;
    }

    @Override
    public wordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.list_favourites,parent,false);
        return new wordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(wordHolder holder, final int position) {

        final String queryWord=favList.get(position).toLowerCase();
        holder.tvWord.setText(queryWord);
        //TODO
        holder.tvWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i=new Intent(context,PopUpActivity.class);
                i.putExtra("guru",queryWord);
                i.putExtra("dimi",getMeaning(queryWord));
                context.startActivity(i);*/
                Asyncho asyncho = new Asyncho(context);
                asyncho.execute(queryWord);

            }
        });
        holder.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vs) {
                s=favList.get(position);
                Log.d(TAG, "onClick: deletion");
                Words.removeWord(table_name,s,dbHelper.getWritableDatabase());
                favList=Words.getAllWords(table_name,dbHelper.getReadableDatabase(),ORDER_BY);
                updateTodos(favList);
                Snackbar snackbar= Snackbar.make(vs,"WORD DELETED",Snackbar.LENGTH_SHORT).setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v,"WORD RESTORED",Snackbar.LENGTH_SHORT).show();
                        Words.addWord(s,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),table_name,dbHelper.getWritableDatabase());
                        favList=Words.getAllWords(table_name,dbHelper.getReadableDatabase(),ORDER_BY);
                        updateTodos(favList);
                    }
                });
                snackbar.show();

            }
        });

    }

    public class Asyncho extends AsyncTask<String, Void, String> {
        String queryWord;
        Context context;
        ProgressDialog dialog;

        public Asyncho(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            /*dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();*/

            this.queryWord = params[0].toLowerCase();
            return getMeaning(this.queryWord);
        }

        @Override
        protected void onPostExecute(String meaning) {
            //DO NOT REMOVE THIS LOG EVERRRRRRRRRRRRRR
            Log.d("OPE", "onPostExecute: "+meaning);
            if (dialog != null) {
                dialog.dismiss();
            }
            SharedPreferences spref = context.getSharedPreferences("MyPrefs", context.MODE_PRIVATE);

//            && queryWord.toLowerCase().compareToIgnoreCase(spref.getString("query", "")) != 0
            if (queryWord != "" ) {

                spref.edit().putString("query", queryWord.toLowerCase()).apply();

                int flag = 1;
                String s = queryWord;
                s.toLowerCase();
                for (int j = 0; j < queryWord.length(); j++) {
                    if (s.charAt(j) >= 'a' && s.charAt(j) <= 'z')
                        continue;
                    else {
                        flag = 0;
                        break;
                    }
                }
                Intent i = new Intent(context, PopUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("check", flag);
                i.putExtra("guru", queryWord);
                i.putExtra("dimi", meaning);
                context.startActivity(i);
                super.onPostExecute(s);
            }
        }
    }


    @Override
    public int getItemCount() {
        return favList.size();
    }

    static class wordHolder extends RecyclerView.ViewHolder{
        TextView tvWord;
        ImageView ivCross;
        public wordHolder(View itemView) {
            super(itemView);
            this.tvWord= (TextView) itemView.findViewById(R.id.tvFav);
            ivCross= (ImageView) itemView.findViewById(R.id.ivCross);
        }
    }

    public void updateTodos(ArrayList<String> wordsArrayList) {
        this.favList=wordsArrayList;
        notifyDataSetChanged();
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
            Toast.makeText(context, "Database does not exist!", Toast.LENGTH_LONG).show();
        }
        return "";
    }
}

