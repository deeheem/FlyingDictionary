package com.developers.dictionary.flying;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<BubblePojo> dataSet;
    Context context;
    boolean check =true;
    TextToSpeech tts;


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView word,meaning;
        ImageView share, pronounce;
        RelativeLayout expandable;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.expandable= (RelativeLayout)itemView.findViewById(R.id.expandableLayout);
            this.word= (TextView)itemView.findViewById(R.id.wordtext);
            this.meaning = (TextView) itemView.findViewById(R.id.meaningtext);
            this.share= (ImageView) itemView.findViewById(R.id.ivShare);
            this.pronounce = (ImageView) itemView.findViewById(R.id.ivPronounce);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("__________", "onClick: YOU CLICKED");
                }
            });
        }
    }

    public CustomAdapter(Context context, ArrayList<BubblePojo> data) {
        this.dataSet = data;
        this.context=context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int listPosition) {

        final TextView word1= myViewHolder.word;
        final TextView meaning1 = myViewHolder.meaning;
        final ImageView share1=myViewHolder.share;
        final ImageView pronounce1 = myViewHolder.pronounce;
        final String s1 = "<font color = '#606062'>";
        final String s2 = "</font>";

        word1.setText(dataSet.get(listPosition).getWord());

        meaning1.setText("");
        meaning1.setTextIsSelectable(true);

        word1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("SA", "getMeaning: GETMEANING CALLED3");
                String query = dataSet.get(listPosition).getWord();
                check = dataSet.get(listPosition).isCheck();

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
                                        resList.append("\n" +"<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + "\n");
                                        Log.d("definition ", "onClick: OKKKK");
                                    } else {
                                        resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
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
                                resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + "\n");
                                Log.d("definition ", "onClick: OKKKK");
                            } else {
                                resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2  + definition.toString() +s1+ "\n\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                            }

                            meaning1.setText(Html.fromHtml(resList.toString().replace("\n","<br />")));

                            cursor2.close();
                            db.close();
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
                                    resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                                    Log.d("definition ", "onClick: OKKKK");
                                } else {
                                    resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
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
                            resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + "\n");
                            Log.d("definition ", "onClick: OKKKK");
                        } else {
                            resList.append("\n" + "<b>"+s1+ count+s2+"</b>" + ". "+s1+"("+s2 + getPos(p) + s1+") "+s2 + definition.toString() + s1+"\n\nExamples:\n"+s2 + example.toString() + s1+"\nSynonyms:\n"+s2 + synonyms.toString() + "\n");
                        }

                        meaning1.setText(Html.fromHtml(resList.toString().replace("\n","<br />")));

                        cursor1.close();
                        db.close();
                    }
                }
                else
                {
                    Toast.makeText(context, "Database does not exist!", Toast.LENGTH_LONG).show();
                }

                if(!check)
                {
                    myViewHolder.expandable.animate()
                            .alpha(0.0f)
                            .setDuration(1000);


                    myViewHolder.expandable.setVisibility(View.GONE);
                    check=true;
                    dataSet.get(listPosition).setCheck(true);

                }
                else {
                    myViewHolder.expandable.setVisibility(View.VISIBLE);
                    myViewHolder.expandable.animate()
                            .alpha(1.0f)
                            .setDuration(1000);

                    check=false;
                    dataSet.get(listPosition).setCheck(false);
                }

            }
        });
        share1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                share1.startAnimation(animFadein);

                StringBuilder sbuild=new StringBuilder();

                sbuild.append("Define " +word1.getText().toString() +" :\n"+meaning1.getText().toString());
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, sbuild.toString());
                context.startActivity(Intent.createChooser(i,"Share Via"));

            }
        });

        pronounce1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                pronounce1.startAnimation(animFadein);

                tts.speak(word1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
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

//606062