package com.developers.dictionary.flying;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by gurtej on 12/1/17.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
      //      Toast.makeText(context,"REBOOTY",Toast.LENGTH_LONG);
            Intent pushIntent = new Intent(context, ClipboardService.class);
            context.startService(pushIntent);
        }
    }
}
