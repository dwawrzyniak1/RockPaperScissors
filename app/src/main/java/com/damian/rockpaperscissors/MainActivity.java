package com.damian.rockpaperscissors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static int[] mImageLightResources = {R.drawable.rock, R.drawable.paper, R.drawable.scissors};
    private static int[] mImageDarkResources = {R.drawable.darkrock, R.drawable.paper, R.drawable.scissors};
    private static int[] mDescriptionResources = {R.string.rock, R.string.paper, R.string.scissors};
    private static Random rand = new Random();

    private long lastUpdate = 0;
    private float last_x;
    private static final int SHAKE_THRESHOLD = 350;

    private SensorManager mSensorManager;
    private Sensor mLinearAcceleration;
    private Sensor mLight;
    private ImageView mImageView;
    private TextView mTextView;
    private Switch mSwitch;
    private Animation mAnimation;
    private long lastUpdateLight = 0;
    private int[] mImageResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mImageView = findViewById(R.id.acMain_ivRockPaperScissors);
        mTextView = findViewById(R.id.acMain_tvDescription);
        mSwitch = findViewById(R.id.sw_handed);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.content_animation);
        lastUpdate = System.currentTimeMillis();
        mImageResources = mImageLightResources;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            changeImage(sensorEvent);
        }
        if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT);{
            changeImageResources(sensorEvent);
        }
    }

    private void changeImageResources(SensorEvent event) {
        float lux = event.values[0];
        long curTime = System.currentTimeMillis();
        if(curTime - lastUpdateLight > 300){
            if(lux > 0){
                Log.d("light", String.valueOf(lux));
                mImageResources = lux > 20 ? mImageLightResources : mImageDarkResources;
            }
        }
    }

    private void changeImage(SensorEvent event) {
        float x = event.values[0];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 200) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float speed = (x - last_x)/ diffTime * 10000;

            int index = rand.nextInt(mImageResources.length);

            if(mSwitch.isChecked()){
                if (speed > SHAKE_THRESHOLD) {
                    changeImageSetViews(index);
                }
            }else{
                if (speed < SHAKE_THRESHOLD*(-1)) {
                    changeImageSetViews(index);
                }
            }

            last_x = x;
        }
    }

    private void changeImageSetViews(int index) {
        mImageView.setVisibility(View.INVISIBLE);
        mImageView.setImageResource(mImageResources[index]);
        mImageView.clearAnimation();
        mImageView.startAnimation(mAnimation);
        mTextView.setText(mDescriptionResources[index]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}

