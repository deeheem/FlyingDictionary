package com.developers.dictionary.flying;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SettingsActivity extends AppCompatActivity {

    Switch aSwitch2;
    SharedPreferences spref;
    Switch aSwitch1;
    TimePicker timePicker;
    Button btnSetTime;
    RelativeLayout rvTimePicker;
    TextView tvPermissions, tvPermDaily, line;
    Calendar calendar;
    String format = "";
    public static final String NOTIFY_CHECK="notify_check";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        spref=getSharedPreferences("MyPrefs",MODE_PRIVATE);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setTitle("Settings");

        setTitleColor(R.color.colorPrimary);
        aSwitch2 = (Switch) findViewById(R.id.switch2);
        aSwitch1 = (Switch) findViewById(R.id.switch1);
        timePicker = (TimePicker) findViewById(R.id.tpTimePicker);

        btnSetTime = (Button) findViewById(R.id.btnSetTime);
        rvTimePicker = (RelativeLayout) findViewById(R.id.rvTimePicker);
        calendar = Calendar.getInstance();

        aSwitch1.setChecked(false);
        rvTimePicker.setVisibility(GONE);

        tvPermissions = (TextView) findViewById(R.id.tvPermssions);
        tvPermDaily = (TextView) findViewById(R.id.tvPermDaily);
        line = (TextView) findViewById(R.id.line);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            aSwitch2.setChecked(true);
            aSwitch2.setClickable(false);
            tvPermDaily.setTextColor(Color.parseColor("#d3d3d3"));
            tvPermissions.setTextColor(Color.parseColor("#d3d3d3"));
            line.setBackgroundColor(Color.parseColor("#d3d3d3"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                aSwitch2.setThumbTintList(new ColorStateList(new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_enabled},
                            new int[]{}
                        },
                            new int[]{
                                    Color.parseColor("#d3d3d3"),
                                    Color.BLUE,
                                    Color.GREEN
                        })
                );
            }
        }
        else
        {
            aSwitch2.setChecked(false);
            aSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        Perman.askForPermission(SettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new Perman.OnPermissionResultListener() {
                            @Override
                            public void onGranted(String forPerm) {
                                aSwitch2.setChecked(true);
                            }

                            @Override
                            public void onDenied(String forPerm) {
                                //TODO intent bhejdio to settings activity and toast to give permissions
                            }
                        });

                    } else {

                    }
                }
            });
        }


//        Log.e("Tag", "onCreate: notify check");
//        Log.e("TAg", "onCreate: "+ spref.getBoolean(NOTIFY_CHECK,true));
        if(spref.getBoolean(NOTIFY_CHECK,true)==true)
        {
            aSwitch1.setChecked(true);
            rvTimePicker.setVisibility(VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(spref.getInt("hour",8));
                timePicker.setMinute(spref.getInt("min",0));
            }
        }
        else
        {
            aSwitch1.setChecked(false);
            rvTimePicker.setVisibility(GONE);
        }
        //Toast.makeText(SettingsActivity.this, "hello", Toast.LENGTH_SHORT).show();

        aSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    spref.edit().putBoolean(NOTIFY_CHECK,true).apply();
                    rvTimePicker.setVisibility(VISIBLE);
                } else {
                    spref.edit().putBoolean(NOTIFY_CHECK,false).apply();
                    rvTimePicker.setVisibility(GONE);
                }
            }
        });


        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime(v);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Perman.onPermResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String showTime(int hour, int min) {
        if (hour == 0) {
            hour += 12;
            format = "AM";
        } else if (hour == 12) {
            format = "PM";
        } else if (hour > 12) {
            hour -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        String time="";
        if(min<=9)
            time = (new StringBuilder().append(hour).append(" : 0").append(min).append(" ").append(format)).toString();
        else
            time = (new StringBuilder().append(hour).append(" : ").append(min).append(" ").append(format)).toString();
        Log.d("SETTINGS", "showTime: TIME SET TO"+time);
        return time;
    }

    public void setTime(View view) {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();
        spref.edit().putInt("hour",hour).apply();
        spref.edit().putInt("min",min).apply();
        Log.e("TAG", "setTime: "+hour+ " + " +min );
        Toast.makeText(SettingsActivity.this, "Time set to "+showTime(hour, min), Toast.LENGTH_LONG).show();

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

        Log.e("NOTIFY THEM", "onCreate: " + "time set to " + alarmStartTime);
        if (now.after(alarmStartTime)) {
            Log.d("Hey","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);

    }
}
