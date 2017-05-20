package com.developers.dictionary.flying;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by deeheem on 8/2/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.wordHolder> {

    ArrayList<String> data;
    Context context;
    TextToSpeech tts;
    SearchView searchView;
    String queryWord;

    public SearchAdapter(ArrayList<String> data, Context context, SearchView searchView) {
        this.data = data;
        this.context = context;
        this.searchView=searchView;
    }

    @Override
    public wordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView=inflater.inflate(R.layout.list_search, parent, false);
        return new wordHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchAdapter.wordHolder holder, int position) {
        holder.tvWord.setText(data.get(position));
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
        holder.tvWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryWord = holder.tvWord.getText().toString(); //queryWord.toLowerCase();
                Log.d("TAG", "onClick: "+queryWord);
                Asyncho asyncho = new Asyncho(context);
                asyncho.execute(queryWord);
            }
        });

        holder.ivSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryWord = holder.tvWord.getText().toString();
                tts.speak(queryWord, TextToSpeech.QUEUE_FLUSH, null);
                Log.d("T", "onClick: clicked1");
            }
        });
        holder.ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryWord = holder.tvWord.getText().toString();
             searchView.setQuery(queryWord,false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void refresh(ArrayList<String> filteredList,SearchView searchView) {
        this.data=filteredList;
        this.searchView=searchView;
        notifyDataSetChanged();
    }

    static class wordHolder extends RecyclerView.ViewHolder {

        TextView tvWord;
        ImageView ivSpeaker, ivArrow;

        public wordHolder(View itemView) {
            super(itemView);
            this.tvWord= (TextView) itemView.findViewById(R.id.tvSearchedWord);
            this.ivSpeaker = (ImageView) itemView.findViewById(R.id.ivSpeakerSearch);
            this.ivArrow = (ImageView) itemView.findViewById(R.id.ivArrow);
        }
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
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            this.queryWord = params[0].toLowerCase();
            return getMeaning(queryWord);
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
                for (int j = 0; j < s.length(); j++) {
                    if ((s.charAt(j)>='A'&& s.charAt(j)<='Z') || (s.charAt(j)>='a'&& s.charAt(j)<='z') || s.charAt(j)=='\'' || s.charAt(j)=='.' || (s.charAt(j)>='0'&& s.charAt(j)<='9') || s.charAt(j)=='-')
                        continue;
                    else {
                        flag = 0;
                        break;
                    }
                }
                s=s.toLowerCase();
                Intent i = new Intent(context, PopUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("check", flag);
                i.putExtra("guru", s);
                i.putExtra("dimi", meaning);
                context.startActivity(i);
                super.onPostExecute(s);
            }
        }
    }

    String getMeaning(String query) {


        Log.d("SA", "getMeaning: GETMEANING CALLED1");
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
                            if (s!="" && s!=null && !s.equals(synonyms.toString())) {
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
                        if (s == null || s == "" || s=="null") {
                            resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                            if (s!=null)
                            Log.d("definition ", "onClick: OKKKK"+s+".");
                        } else {
                            resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
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
                if (s == null || s == "" || s=="null") {
                    resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                    if (s!=null)
                        Log.d("definition ", "onClick: OKKKK"+s+".");
                } else {
                    resList.append("\n" + s3+count+s4 + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
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
}

