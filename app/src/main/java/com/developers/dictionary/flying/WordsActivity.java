package com.developers.dictionary.flying;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class WordsActivity extends AppCompatActivity {

    private static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private static RecyclerView recyclerView;
    SharedPreferences spref;

    SearchView searchView;
    public static ArrayList<BubblePojo> data = new ArrayList<>();

    char letter;
    @Override
    protected void onResume() {
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        spref.edit().putBoolean("wIsOpen",true).apply();
        super.onResume();
    }

    @Override
    protected void onStop() {
        spref.edit().putBoolean("wIsOpen",false).apply();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

        final Intent gotIntent = getIntent();
        letter = gotIntent.getCharExtra("letter", 'a');

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("Search Here");
        searchView.setQueryRefinementEnabled(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data.clear();
        adapter = new CustomAdapter(WordsActivity.this,data);
        recyclerView.setAdapter(adapter);

        File file=new File(LogoActivity.DB_PATH);
        if(file.exists() && !file.isDirectory())
        {
            Log.d("WA", "openDataBase: "+"File exists");

            SQLiteDatabase db = SQLiteDatabase.openDatabase(LogoActivity.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

            String[] selectionArgs = new String[] { letter + "%" };
            String whereClause = " lemma LIKE " + "?";
            Cursor cursor = db.query("words", new String[] {"lemma"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                data.add(new BubblePojo(cursor.getString(0), true));
            }
            cursor.close();
            db.close();

            adapter.notifyDataSetChanged();

        }
        else
        {
            Toast.makeText(this, "Database does not exist!", Toast.LENGTH_LONG).show();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return  false; }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();

                final ArrayList<BubblePojo> filteredList = new ArrayList<>();

                for (int i = 0; i < data.size(); i++) {

                    final String text = data.get(i).getWord().toLowerCase();
                    if (text.contains(newText) && text.substring(0, newText.length()).equalsIgnoreCase(newText)) {
                        filteredList.add(new BubblePojo(text, true));
                    }
                }

                adapter = new CustomAdapter(WordsActivity.this,filteredList);
                recyclerView.setAdapter(adapter);

                return true;
            }
        });
    }


    public static void startNewActivity(final Context context, final char letter) {

        final Intent i=new Intent(context,WordsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        i.putExtra("letter",letter);
        context.startActivity(i);

    }

}
