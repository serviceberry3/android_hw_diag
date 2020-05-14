package com.example.testapp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

//run a Toast on the main UI thread
public class Toaster {
    public static void customToast (final String text, final Context context) {
        Activity activity = (Activity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
