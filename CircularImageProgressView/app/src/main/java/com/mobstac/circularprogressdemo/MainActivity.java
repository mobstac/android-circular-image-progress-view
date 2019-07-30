package com.mobstac.circularprogressdemo;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mobstac.circularimageprogress.CircularImageProgressView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity {

    CircularImageProgressView circularImageProgressView;
    SeekBar seekProgress, seekWidth;
    TextView progressText, widthText;
    Handler player;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularImageProgressView = (CircularImageProgressView) findViewById(R.id.circular_image_progress);
        seekProgress = (SeekBar) findViewById(R.id.seek_progress);
        seekWidth = (SeekBar) findViewById(R.id.seek_width);
        progressText = (TextView) findViewById(R.id.value_progress);
        widthText = (TextView) findViewById(R.id.value_width);
        fab = (FloatingActionButton) findViewById(R.id.play_fab);

        Picasso.with(this).load("https://b.zmtcdn.com/data/collections/e40960514831cb9b74c552d69eceee0f_1418387628_l.jpg").resize(100,100).centerCrop().transform(new CropCircleTransformation()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d("MainAct","bitmap");
                circularImageProgressView.setImage(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d("MainAct","bitmap failed");

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d("MainAct","bitmap prepare");
                circularImageProgressView.setImageResource(R.mipmap.ic_launcher);

            }
        });


        seekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circularImageProgressView.setProgress(progress);
                progressText.setText(String.valueOf(circularImageProgressView.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                circularImageProgressView.setCircleWidth(progress);
                widthText.setText(String.valueOf(circularImageProgressView.getCircleWidth()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });
    }

    private void startTimer() {
        if (circularImageProgressView != null) {
            circularImageProgressView.setProgress(0);
            if (player == null)
                player = new Handler();
            player.removeCallbacks(playRunnable);
            player.post(playRunnable);
        }
    }


    Runnable playRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = circularImageProgressView.getProgress();
            if (progress < circularImageProgressView.getMax()) {
                seekProgress.setProgress(progress + 1);
                float percentage = ((float) circularImageProgressView.getProgress() / (float) circularImageProgressView.getMax()) * 100F;
                float width = (75F * percentage) / 100F;
                seekWidth.setProgress((int) (width));
                player.postDelayed(this, 40);

            }
        }
    };

}