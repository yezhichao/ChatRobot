package com.example.android_robot_01;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by yezhichao on 16-2-27.
 */
public class CommandObserver {

    public boolean onCommand(Context context ,String msg){
        if (msg.contains("电话")){
            Uri uri = Uri.parse("tel:");
            Intent it = new Intent(Intent.ACTION_DIAL, uri);
            context.startActivity(it);
            return true;
        }else {
            return false;
        }
    }
}
