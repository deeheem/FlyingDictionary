package com.developers.dictionary.flying;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LogoActivity extends AppCompatActivity {

    private static final String TAG ="LogoActivity" ;
    public static final String DB_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flying Dictionary/boot/root/data/1617996067-1630106156.db";

    //  "/storage/emulated/0/Flying Dictionary/boot/root/data/1617996067-1630106156.db";

    public static final String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Flying Dictionary/boot/root/data/";

    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        Window window =LogoActivity.this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        dbHelper=new DatabaseHelper(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(LogoActivity.this.getResources().getColor(R.color.colorPrimaryDark));
        }

        Perman.askForPermission(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new Perman.OnPermissionResultListener() {
            @Override
            public void onGranted(String forPerm) {
                Log.d(TAG, "onGranted: ");

                File folder = new File(outputPath);
                if(!folder.exists())
                {
                    if(folder.mkdirs());
                }

                File file = new File(DB_PATH);
                boolean b=file.exists() && !file.isDirectory();
                boolean x=true;
                if(!b || file.length()!=457623552)
                {
                    StatFs stats=new StatFs(outputPath);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        long left=stats.getAvailableBytes();
                        if(left/(1024*1024)<436)
                        {
                            View messageView = getLayoutInflater().inflate(R.layout.warning, null, false);

                            AlertDialog.Builder builder = new AlertDialog.Builder(LogoActivity.this);
                            builder.setIcon(R.drawable.logo);
                            builder.setTitle(R.string.app_name);
                            builder.setView(messageView);
                            builder.create();
                            builder.show();
                            x=false;
                        }
                    }
                }


                if(x==true) {
                    new Asyncho().execute();
                    startService(new Intent(LogoActivity.this, ClipboardService.class));
                }

            }

            @Override
            public void onDenied(String forPerm) {
                Log.d(TAG, "onDenied: ");
                Toast.makeText(LogoActivity.this, "Storage permission are needed to run this app!", Toast.LENGTH_SHORT).show();
                stopService(new Intent(LogoActivity.this, ClipboardService.class));

                startActivity(new Intent(LogoActivity.this, SettingsActivity.class));
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        finish();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: "+"STOPPED");
        finish();
        super.onStop();
    }
    public  class Asyncho extends AsyncTask<Long, Integer,Long>
    {
        ProgressDialog dialog;
        boolean y=false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            File file = new File(DB_PATH);
            boolean b=file.exists() && !file.isDirectory();
            if(!b)
            {
                dialog = new ProgressDialog(LogoActivity.this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("Initializing...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                y=true;
            }
        }

        @Override
        protected Long doInBackground(Long... params) {
            File file = new File(DB_PATH);
            boolean b=file.exists() && !file.isDirectory();
            if (!b) {
                Log.i(TAG, "doInBackground: "+"we aree here");
                try {
                    Log.d(TAG, "unzip: in IFFFFFiii");
                    InputStream fin = getApplicationContext().getAssets().open("sqlite-31.db.zip");
                    ZipInputStream zin = new ZipInputStream(fin);
                    ZipEntry ze = null;
                    File newfile = null;
                    long size = 0;
                    while ((ze = zin.getNextEntry()) != null) {

                        Log.d(TAG, "unzip: +1");
                        FileOutputStream fout = new FileOutputStream(outputPath + ze.getName());

                        byte[] buffer = new byte[32 * 1024]; // play with sizes..
                        int readCount;
                        while ((readCount = zin.read(buffer)) != -1) {
                            fout.write(buffer, 0, readCount);
                            newfile = new File(outputPath + "sqlite-31.db");
                            if (newfile != null)
                                size = newfile.length();
                            Log.d(TAG, "unzip: " + size);
                        }
                        zin.closeEntry();
                        fout.close();
                    }
                    zin.close();
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
            else
            {
                Log.i(TAG, "doInBackground: "+"we are not here");
                long startTime= System.currentTimeMillis();
                while(true)
                {
                    if(System.currentTimeMillis()-startTime>500)
                        break;
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            if (dialog != null) {
                dialog.dismiss();
            }

            MainActivity.startTabbedActivity(LogoActivity.this,y);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Perman.onPermResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}


