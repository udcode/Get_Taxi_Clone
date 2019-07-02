package com.avi_ud.gettaxi2.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.avi_ud.gettaxi2.R;

//loading animation easy
public class LoadingProgressBar {
    private Activity activity;
    private ProgressBar bar;

    public LoadingProgressBar(Activity activity, RelativeLayout layout) {
        this.activity = activity;
        LayoutInflater inflater = LayoutInflater.from(activity);
        bar = (ProgressBar ) inflater.inflate(R.layout.loading_progress_bar,layout, false);
        bar.setVisibility(View.GONE);
        layout.addView(bar);
    }
    public void show() {
        bar.setVisibility(View.VISIBLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public  void hide() {
        bar.setVisibility(View.GONE);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
