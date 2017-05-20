package com.developers.dictionary.flying;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PopUpActivity extends AppCompatActivity {

    private static final String TAG ="MyPopUpActivity" ;
    TextView tvPopUpWord, tvPopUpMeaning;
    ImageView ivSpeaker, ivGlobe, ivStar,ivShare;
    String s, sU, sMeaning;
    TextToSpeech tts;
    int flag;
    int use;
    ArrayList<String >wordsList=new ArrayList<>();
    DatabaseHelper dbHelper;
    SharedPreferences spref;

    @Override
    protected void onStop() {
        spref.edit().putBoolean("pIsOpen",false).apply();
        super.onStop();
    }

    @Override
    protected void onResume() {
        spref.edit().putBoolean("pIsOpen",true).apply();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: show value whether close or open" +spref.getBoolean("pIsOpen",true));
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);
        dbHelper=new DatabaseHelper(this);
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        spref.edit().putBoolean("pIsOpen",true);
        tvPopUpWord = (TextView) findViewById(R.id.tvPopUpWord);
        tvPopUpMeaning = (TextView) findViewById(R.id.tvPopUpMeaning);
        ivSpeaker = (ImageView) findViewById(R.id.ivSpeaker);
        ivGlobe = (ImageView) findViewById(R.id.ivGlobe);
        ivStar = (ImageView) findViewById(R.id.ivStar);
        ivShare= (ImageView) findViewById(R.id.ivShare);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });

        Intent gotIntent = getIntent();
        s = gotIntent.getStringExtra("guru");
        use=gotIntent.getIntExtra("addToHistory",1);
        sU = s.substring(0,1).toUpperCase();
        sU = sU + s.substring(1);
        //setTitle(sU); TODO
        s.toLowerCase();

        sMeaning=gotIntent.getStringExtra("dimi");
        flag=gotIntent.getIntExtra("check",1);
        if( sMeaning.length()==0)
        {
            sMeaning="No such word exists!";
            tvPopUpMeaning.setTextColor(Color.RED);
            tvPopUpMeaning.setText("No such word exists!");
            tvPopUpWord.setText(s+": ");
        }
        else
        {
            tvPopUpMeaning.setText(Html.fromHtml(sMeaning.replace("\n","<br />")));   //TODO: deprecated
            tvPopUpWord.setText(s+": ");
        }
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sbuild=new StringBuilder();

                sbuild.append("Define " +s +" :\n"+Html.fromHtml(sMeaning.replace("\n","<br />")));
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, sbuild.toString());
                startActivity(Intent.createChooser(i,"Share Via"));

            }
        });


        if(flag==1 && Words.find(Words.TABLE_NAME_FAV,s,dbHelper.getReadableDatabase()))
        {
            Log.d(TAG, "onCreate: ");
            ivStar.setImageResource(R.drawable.star_yellow);
        }
//        if(flag==1 && use==1)
        if(flag==1)
        {
            if(Words.find(Words.TABLE_NAME_HIST,s,dbHelper.getReadableDatabase()))
            {
                Log.d(TAG, "onCreate: heyyyy");
                Words.removeWord(Words.TABLE_NAME_HIST,s,dbHelper.getWritableDatabase());
//                Words.updateTime(s,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),Words.TABLE_NAME_HIST,dbHelper.getWritableDatabase());
//                Log.d(TAG, "onCreate: "+Words.getTime(s,Words.TABLE_NAME_HIST,dbHelper.getReadableDatabase()));
            }
//            else
            Words.addWord(s,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),Words.TABLE_NAME_HIST,dbHelper.getWritableDatabase());
        }


        ivSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                ivSpeaker.startAnimation(animFadein);

                tts.speak(sU, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        ivGlobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                ivGlobe.startAnimation(animFadein);

                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, s);
                startActivity(intent);
            }
        });


        ivStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                ivStar.startAnimation(animFadein);

                if(ivStar.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.star_black).getConstantState())) {

                    if(flag==1) {
                        Log.d("POPUP------>", "onClick: Yellow selected");
                        Toast.makeText(PopUpActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();

                        Words.addWord(s,new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()),Words.TABLE_NAME_FAV,dbHelper.getWritableDatabase());

                       ivStar.setImageResource(R.drawable.star_yellow);
                    }
                    else
                        Toast.makeText(PopUpActivity.this,"Invalid Word",Toast.LENGTH_SHORT);

                } else {
                    Log.d("POPUP------>", "onClick: Black selected");
                    Toast.makeText(PopUpActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    ivStar.setImageResource(R.drawable.star_black);
                    Words.removeWord(Words.TABLE_NAME_FAV,s,dbHelper.getWritableDatabase());

                }
            }
        });
        ivStar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
                return false;
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Perman.onPermResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


