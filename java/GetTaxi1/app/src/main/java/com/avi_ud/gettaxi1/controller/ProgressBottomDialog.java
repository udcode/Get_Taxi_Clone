package com.avi_ud.gettaxi1.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.avi_ud.gettaxi1.R;

public class ProgressBottomDialog extends BottomSheetDialogFragment {
    public ProgressBottomDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setWindowAnimations(R.style.Slide);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.progress_bottom_dialog, container, false);
    }

    public void sendOk(){
        final ImageView ok = (ImageView) getView().findViewById(R.id.okImage);
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        final TextView info = (TextView) getView().findViewById(R.id.infoTxt);

        progressBar.setVisibility(View.GONE);
        ok.setVisibility(View.VISIBLE);

        Animation a = new AlphaAnimation(1.00f, 0.00f);

        a.setDuration(1000);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ok.setVisibility(View.GONE);
                info.setText("Your order has been placed.\nNow wait for your driver.");
                info.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 10000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ok.startAnimation(a);
    }
}
