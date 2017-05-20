package com.developers.dictionary.flying;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by gurtej on 16/1/17.
 */
public class FavouriteFragment extends Fragment {

    FloatingActionButton fabShare,fabDelete,fabSort;
    ArrayList<String > wordsList=new ArrayList<>();
    RecyclerView rView;
    View rootView;
    DatabaseHelper dbHelper;
    SharedPreferences spref;
    public static final String SORT_FAV_KEY="sort_fav_key";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_favourites,container,false);
        spref=this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        fabShare = (FloatingActionButton) rootView.findViewById(R.id.fabShare);
        fabDelete = (FloatingActionButton) rootView.findViewById(R.id.fabDelete);
        fabSort = (FloatingActionButton) rootView.findViewById(R.id.fabSort);
        dbHelper=new DatabaseHelper(getContext());

        rView= (RecyclerView) rootView.findViewById(R.id.favList);
        final RecycleAdapter rAdapter=new RecycleAdapter(getContext(),wordsList,Words.TABLE_NAME_FAV,dbHelper,spref.getInt(SORT_FAV_KEY,1));
        rView.setLayoutManager(new LinearLayoutManager(getContext()));
        rView.setAdapter(rAdapter);



         wordsList= Words.getAllWords(Words.TABLE_NAME_FAV,dbHelper.getReadableDatabase(),spref.getInt(SORT_FAV_KEY,1));
        rAdapter.updateTodos(wordsList);

        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own FAV action", Snackbar.LENGTH_LONG)
 //                       .setAction("Action", null).show();
                StringBuilder sbuild=new StringBuilder();
                for(int j=0;j<wordsList.size();j++)
                {
                    sbuild.append(wordsList.get(j)+"\n");
                }
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT,"Subject test");
                i.putExtra(Intent.EXTRA_TEXT, "MY FAVOURITES\n" + sbuild.toString());
                startActivity(Intent.createChooser(i,"Share Via"));

            }
        });

        fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setIcon(R.drawable.logo);
                builder.setTitle(R.string.app_name);
                builder.setMessage("Clear Favourites ?");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Words.removeAll(Words.TABLE_NAME_FAV,dbHelper.getWritableDatabase());
                        wordsList= Words.getAllWords(Words.TABLE_NAME_FAV,dbHelper.getReadableDatabase(),spref.getInt(SORT_FAV_KEY,1));
                        rAdapter.updateTodos(wordsList);
                    }
                });
                builder.create();
                builder.show();


            }
        });
        fabSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup= new PopupMenu(getContext(),view);
                popup.getMenuInflater().inflate(R.menu.menu_sort,popup.getMenu());
                if(spref.getInt(SORT_FAV_KEY,1)==1)
                    popup.getMenu().getItem(1).setChecked(true);
                else
                    popup.getMenu().getItem(0).setChecked(true);

                /// 1 chrono
                /// 0 alpha
                Log.d("TAG", "onClick: show me sortig");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.alphabetical)
                        {
                            item.setChecked(true);
                            //TOdo : ALPHA SORT
                            if(spref.getInt(SORT_FAV_KEY,1)!=0)
                            {
                                spref.edit().putInt(SORT_FAV_KEY,0).apply();
                                wordsList=Words.getAllWords(Words.TABLE_NAME_FAV,dbHelper.getReadableDatabase(),0);
                                rAdapter.updateTodos(wordsList);
                            }
                        }
                        else if(item.getItemId()==R.id.chronological)
                        {
                            item.setChecked(true);
                            //TOdo : CHRONO SORT
                            if(spref.getInt("sort_fav_key",1)!=1) {
                                spref.edit().putInt("sort_fav_key", 1).apply();
                                wordsList=Words.getAllWords(Words.TABLE_NAME_FAV,dbHelper.getReadableDatabase(),1);
                                rAdapter.updateTodos(wordsList);
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });


        return rootView;
    }


}










