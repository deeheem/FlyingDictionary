package com.developers.dictionary.flying;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by gurtej on 21/1/17.
 */

public class Perman {

    interface OnPermissionResultListener
    {
        void onGranted(String forPerm);
        void onDenied(String forPerm);
    }
    private static OnPermissionResultListener soprl;
    static  void askForPermission(Activity act,String[]perm , OnPermissionResultListener oprl){
            soprl=oprl;
        if(ContextCompat.checkSelfPermission(act,perm[0])==PackageManager.PERMISSION_DENIED)
        ActivityCompat.requestPermissions(act,perm, 111);
        else
            soprl.onGranted(perm[0]);
    }

    static void onPermResult(int requestcode, String[] perms, int[] rescodes)
    {
        if(requestcode==111) {
            for (int i = 0; i < perms.length; i++) {
                if (rescodes[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onPermResult: PermissionGranted");
                    soprl.onGranted(perms[i]);
                }
                else
                {
                    Log.i(TAG, "onPermResult: PermissionDenied");
                    soprl.onDenied(perms[i]);
                }
            }
        }

    }
}
