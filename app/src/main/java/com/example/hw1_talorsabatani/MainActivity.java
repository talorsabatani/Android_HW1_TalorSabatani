package com.example.hw1_talorsabatani;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Button right, left, slow, fast;

    private int playerIndex;
    private ImageView[][] path;
    private int [][] vals;

    private ImageView [] skulls;
    private int skulls_count = 3;;

    private Timer timer ;
    private int clock = 0;
    private int speed = 9;

    private TextView gameTime;
    private ProgressBar panel_PRG_time;

    private Animation anima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findViews();
        initView();

    }

    private void initView() {
        right = findViewById(R.id.right_BTN);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerIndex<2)
                    moveRight(right);
            }
        });
        left = findViewById(R.id.left_BTN);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playerIndex>0)
                    moveLeft(left);
            }
        });

        slow = findViewById(R.id.slower);
        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slower(slow);
            }
        });

        fast = findViewById(R.id.faster);
        fast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faster(fast);
            }
        });
    }

    private void faster(View view) {
        speed = 5;
        panel_PRG_time.setProgress(2);
    }

    private void slower(View view){
        speed = 9;
        panel_PRG_time.setProgress(0);
    }

    private void stopTicker() {
        timer.cancel();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    private void startTicker() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateClock();
                        updateUI();
                    }
                });
            }
        },10, 1000);
    }

    private void updateClock() {
        clock++;
        gameTime.setText("Time: "+clock);
        moveEnemy();
        if(clock % speed == 0){
            rand();
        }
        if(clock%20==0){
            anima = AnimationUtils.loadAnimation(this, R.anim.bounce);
            gameTime.startAnimation(anima);
        }
    }

    private void rand() {
        vals[0][(int) (Math.random() * (3) + 0)] = 1;
    }

    private void moveEnemy(){
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                if(vals[i][j] == 1 && i+1 < vals.length){
                    vals[i+1][j]=1;
                    vals[i][j]=0;
                    return;
                }
                else if(vals[i][j] == 1) {
                    vals[i][j]=0;
                    checkCrash(j);
                    return;
                }
            }
        }

    }


    private void checkCrash(int i) {
        if (playerIndex==i){
            path[3][playerIndex].setImageResource(R.drawable.img_skull);
            anima = AnimationUtils.loadAnimation(this, R.anim.rotate);
            path[3][playerIndex].startAnimation(anima);
            vibrate();
            skulls[skulls_count - 1].setVisibility(View.INVISIBLE);
            skulls_count--;
            if (skulls_count == 0)
                finish();

        }
    }

    private void updateUI() {
        for (int i = 0; i < path.length-1; i++) {
            for (int j = 0; j < path[i].length; j++) {
                ImageView im = path[i][j];
                if (vals[i][j] == 0) {
                    im.setVisibility(View.INVISIBLE);
                } else if (vals[i][j] == 1) {
                    im.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTicker();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTicker();
    }


    private void findViews() {
        path = new ImageView[][]{
                {findViewById(R.id.demo_IMG_00), findViewById(R.id.demo_IMG_01), findViewById(R.id.demo_IMG_02)},
                {findViewById(R.id.demo_IMG_10), findViewById(R.id.demo_IMG_11), findViewById(R.id.demo_IMG_12)},
                {findViewById(R.id.demo_IMG_20), findViewById(R.id.demo_IMG_21), findViewById(R.id.demo_IMG_22)},
                {findViewById(R.id.demo_IMG_30), findViewById(R.id.demo_IMG_31), findViewById(R.id.demo_IMG_32)}
        };

        skulls = new ImageView[]{
                findViewById(R.id.skull1), findViewById(R.id.skull2), findViewById(R.id.skull3)
        };

        vals = new int[path.length][path[0].length];
        for (int i = 0; i < vals.length; i++) {
            for (int j = 0; j < vals[i].length; j++) {
                vals[i][j] = 0;
            }
        }
        gameTime = findViewById(R.id.gameTime);
        panel_PRG_time = findViewById(R.id.panel_PRG_time);
        panel_PRG_time.setMax(2);
        panel_PRG_time.setProgress(0);

        updateUI();
        playerIndex = 1;
    }

    public void moveRight(View view) {
        path[3][playerIndex].setImageResource(0);
        path[3][playerIndex+1].setImageResource(R.drawable.img_jack);
        playerIndex++;
    }

    public void moveLeft(View view) {
        path[3][playerIndex].setImageResource(0);
        path[3][playerIndex-1].setImageResource(R.drawable.img_jack);
        playerIndex--;
    }


}