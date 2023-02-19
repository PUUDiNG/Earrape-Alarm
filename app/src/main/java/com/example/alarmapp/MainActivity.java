package com.example.alarmapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private EditText editTextTime;
    private TextView invalidText;


    private String[] timeArray;
    private int hourTime;
    private int minuteTime;
    private boolean timeValid;

    private boolean isAm;
    private boolean isPm;
    private int standardHour;
    private int standardMinute;

    private String timeString;
    private int localHour;
    private int localMinute;
    private Timer timer = new Timer("Timer");

    private MediaPlayer mp;

    private boolean quizDone;
    private Button quizButton;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        editTextTime = findViewById(R.id.enterTime);
        editTextTime.addTextChangedListener(timeTextWatcher);
        invalidText = findViewById(R.id.invalidText);
        timeValid = true;

        timeArray = new String[4];
        for (int i = 0; i < 4; i++){
            timeArray[i] = "0";
        }

        quizButton = findViewById(R.id.quizButton);
        quizButton.setVisibility(View.INVISIBLE);


        isAm = true;
        isPm = false;


        TimerTask task = new TimerTask(){
            public void run(){
                LocalTime time = LocalTime.now();
                timeString = time.toString();
                localHour = Character.getNumericValue(timeString.charAt(0)) * 10 + Character.getNumericValue(timeString.charAt(1));
                localMinute = Character.getNumericValue(timeString.charAt(3)) * 10 + Character.getNumericValue(timeString.charAt(4));
                System.out.println(localHour + " : " + localMinute);
                System.out.println(standardHour + " and " + standardMinute);


                if (localHour == standardHour && localMinute == standardMinute){

                    try {
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {

                                quizButton.setVisibility(View.VISIBLE);
                            }
                        });
                        ring();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        timer.schedule(task, 0,1000);

        quizDone = false;

    }

    public void amPress(View v){
        Button b = (Button) v;
        b.setTextColor(Color.parseColor("#ff0000"));

        ((Button)findViewById(R.id.pmButton)).setTextColor(Color.parseColor("#a1a1a1"));
        isAm = true;
        isPm = false;
    }
    public void pmPress(View v){
        Button b = (Button) v;
        b.setTextColor(Color.parseColor("#ff0000"));

        ((Button)findViewById(R.id.amButton)).setTextColor(Color.parseColor("#a1a1a1"));
        isAm = false;
        isPm = true;
    }

    public void timeCalc(View v){
        if (isAm){
            standardHour = hourTime;
            standardMinute = minuteTime;
        }
        else if (isAm && hourTime == 12){
            standardHour = hourTime - 12;
            standardMinute = minuteTime;
        }
        else if (isPm){
            standardHour = hourTime + 12;
            standardMinute = minuteTime;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ring() throws IOException {
        timer.cancel();

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        mp.setDataSource(this, Uri.parse("android.resource://"+getPackageName()+"/raw/music"));
        mp.prepare();
        mp.start();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!quizDone) {
                    mp.start();
                }
            }
        });
        mp.start();
    }

    public void quiz(View v){
        mp.stop();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private TextWatcher timeTextWatcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextView v = findViewById(R.id.timeText);


            for (int i = 0; i < s.length(); i++){
                timeArray[3-i] = String.valueOf(s.charAt(i));
            }
            for (int i = s.length(); i < 4; i++){
                timeArray[3-i] = "0";
            }



            v.setText(timeArray[3] + timeArray[2] + ":" + timeArray[1] + timeArray[0]);


            hourTime = Integer.parseInt(timeArray[3]) * 10 + Integer.parseInt(timeArray[2]);
            minuteTime = Integer.parseInt(timeArray[1]) * 10 + Integer.parseInt(timeArray[0]);

            if (hourTime > 12){
                invalidText.setVisibility(View.VISIBLE);
                timeValid = false;
            }
            else if (minuteTime > 60){
                invalidText.setVisibility(View.VISIBLE);
                timeValid = false;
            }
            else if (hourTime == 12 && minuteTime > 0){
                invalidText.setVisibility(View.VISIBLE);
                timeValid = false;
            }
            else {
                invalidText.setVisibility(View.INVISIBLE);
                timeValid = true;
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}