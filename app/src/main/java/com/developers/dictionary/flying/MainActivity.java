package com.developers.dictionary.flying;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.*;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    ArrayList<String> data = new ArrayList<>();
    ArrayList<String> filteredList = new ArrayList<>();
    RecyclerView rView;
    SearchAdapter sAdapter;
    SearchManager searchManager;
    SearchView searchView;
    TabLayout tabLayout;
    SharedPreferences spref;

    @Override
    protected void onResume() {
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);
        spref.edit().putBoolean("mIsOpen",true).apply();
        super.onResume();
    }

    @Override
    protected void onStop() {
        spref.edit().putBoolean("mIsOpen",false).apply();
        Log.d(TAG, "onStop: "+ spref.getBoolean("mIsOpen",true));
        super.onPause();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: Main Activity Paused "+ spref.getBoolean("mIsOpen",true));
        super.onPause();
    }

    private static final String TAG = "yooyooy" ;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        if(getIntent().getBooleanExtra("initialize",false)==true)
        {
            View view = getLayoutInflater().inflate(R.layout.tutorial, null, false);

            ImageView ivTutorial = (ImageView) view.findViewById(R.id.ivTutorial);

            ivTutorial.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.tutorial, 1000, 1000));

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setIcon(R.drawable.logo);
            adb.setTitle(R.string.app_name);
            adb.setView(view);
            adb.create();
            adb.show();

        }
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);

        rView = (RecyclerView) findViewById(R.id.rvSearch);
        sAdapter = new SearchAdapter(filteredList, this, searchView);
        rView.setLayoutManager(new LinearLayoutManager(this));
        rView.setAdapter(sAdapter);
        rView.setVisibility(GONE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class); // AlarmReceiver = broadcast receiver

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();

        alarmStartTime.set(Calendar.HOUR_OF_DAY, spref.getInt("hour",8));
        alarmStartTime.set(Calendar.MINUTE, spref.getInt("min",0));
        alarmStartTime.set(Calendar.SECOND, 0);


        Calendar now = Calendar.getInstance();

        Log.e(TAG, "onCreate: " + "time set to " + alarmStartTime);
        if (now.after(alarmStartTime)) {
            Log.d("Hey","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                //TODO: search menu disable problem
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu: "+"searchView here");
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG,  "onMenuItemActionExpand called");
                mViewPager.setVisibility(GONE);
                tabLayout.setVisibility(GONE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "onMenutItemActionCollapse called");
                mViewPager.setVisibility(VISIBLE);
                tabLayout.setVisibility(VISIBLE);
                rView.setVisibility(GONE);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        query = query.toLowerCase();
        Asyncho asyncho = new Asyncho(this);
        asyncho.execute(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("CHANGE", "onQueryTextChange: "+newText);

        newText = newText.toLowerCase();


        if (newText.length() == 0) {
            filteredList.clear();
            sAdapter.refresh(filteredList,searchView);
        } else if(newText.length() == 1) {


            Log.d("TAG", "onQueryTextChange: OKKKKKKKKKKK");

            data.clear();
            filteredList.clear();

            SQLiteDatabase db = SQLiteDatabase.openDatabase(LogoActivity.DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

            String[] selectionArgs = new String[] { newText + "%" };
            String whereClause = " lemma LIKE " + "?";
            Cursor cursor = db.query("words", new String[] {"lemma"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                data.add(cursor.getString(0));
                filteredList.add(cursor.getString(0));
            }
            cursor.close();
            db.close();

            Log.d("TAG", "onQueryTextChange: "+filteredList.size());
            sAdapter.refresh(filteredList,searchView);

        } else {

            filteredList.clear();

            for (int i = 0; i < data.size(); i++) {

                final String text = data.get(i).toLowerCase();
                if (text.contains(newText) && text.substring(0, newText.length()).equalsIgnoreCase(newText)) {
                    filteredList.add(text);
                }
            }
            Log.d("TAG", "onQueryTextChange: "+filteredList.size());
            Log.d(TAG, "onQueryTextChange: break");
            sAdapter.refresh(filteredList,searchView);
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:
                Log.d(TAG, "onOptionsItemSelected: SEARCH");

                rView.setVisibility(VISIBLE);

                return true;

            case R.id.action_tutorial:

                View view = getLayoutInflater().inflate(R.layout.tutorial, null, false);

                ImageView ivTutorial = (ImageView) view.findViewById(R.id.ivTutorial);

                ivTutorial.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.tutorial, 1000, 1000));

                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setIcon(R.drawable.logo);
                adb.setTitle(R.string.app_name);
                adb.setView(view);
                adb.create();
                adb.show();
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);

                return true;
            case R.id.action_about:
                View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.logo);
                builder.setTitle(R.string.app_name);
                builder.setView(messageView);
                builder.create();
                builder.show();
                return true;
            case R.id.action_exit:
                finish();
                System.exit(0);
                return true;
            default:
                return false;
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void startTabbedActivity(final Activity logoActivity , boolean z){

        final Intent i = new Intent(logoActivity, MainActivity.class);
//        DataHolder.setData(wordcombimelist, meancombimelist);
        i.putExtra("initialize",z);
        logoActivity.startActivityForResult(i,111);

//        logoActivity.startActivity(i);
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
            // dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Loading. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

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


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //  TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                case 0:
                    return new abcFragment();

                case 1:
                    return new HistoryFragment();
                case 2:
                    return new FavouriteFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ABC...";
                case 1:
                    return "HISTORY";
                case 2:
                    return "FAVOURITES";
            }
            return null;
        }
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


