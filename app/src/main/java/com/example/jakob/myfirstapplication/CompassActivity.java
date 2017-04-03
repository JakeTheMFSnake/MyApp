package com.example.jakob.myfirstapplication;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Vibrator;

public class CompassActivity extends Activity implements SensorEventListener {
    private ImageView mPointer;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private float mLastDegree;
    MediaPlayer mediaPlayer;
    private int counter;
    private int delay;
    private TextView counterText;
    private boolean step;
    private Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mPointer = (ImageView) findViewById(R.id.pointer);
        mediaPlayer  = MediaPlayer.create(this,R.raw.rightround);
        counter = 0;
        delay = 0;
        counterText = (TextView)findViewById(R.id.counter);
        step = false;
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;

        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            RotateAnimation ra = new RotateAnimation(
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);

            int last = Math.round((-1)*mLastDegree);
            int curr = Math.round((-1)*mCurrentDegree);
            //counterText.setText("Last: "+last+" Current: "+ curr);
            counterText.setText("Last: "+Integer.toString(last)+" Current: "+ Integer.toString(curr) +" Counter: "+ Integer.toString(counter));

            if(last > curr && last > 330 && curr <= 30){
                mLastDegree = mCurrentDegree;
                delay=0;
                counter++;
                v.vibrate(500);
            }
            if(last < curr && last < 30 && curr >= 330 ){
                mLastDegree = mCurrentDegree;
                delay=0;
                counter--;
                v.vibrate(100);
                v.cancel();
                v.vibrate(100);

            }

            if(counter >= 3){
                mediaPlayer.start();
                counter = 0;

                //counterText.setText(Integer.toString(counter));
            }
            //counterText.setText(Float.toString(mLastDegree));
            delay++;
            if(delay>30){
                mLastDegree = mCurrentDegree;
                delay=0;
            }

            mCurrentDegree = -azimuthInDegress;
        }

    }
    /*
    private void doDraw(int val, TextView id){
        id.setText(Integer.toString(val));
    }*/

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

}