package com.developers.dictionary.flying;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by gurtej on 8/2/17.
 */
public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Receiver Seen");
        Intent service1 = new Intent(context, NotificationService.class);
        service1.setData((Uri.parse("custom://"+System.currentTimeMillis())));
        context.startService(service1);
    }
}
