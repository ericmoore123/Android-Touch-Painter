package com.example.a4_official;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.hardware.SensorEventListener;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.Objects;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    DrawingCanvas canvas;

//    Color picker setup
    ConstraintLayout mLayout;
    int defaultColor = Color.BLACK;
    Button colorPicker;

//    Mode counters
    int lineModeCounter = 0;
    int circleModeCounter = 0;

//    Controller Buttons
    Button clearButton;
    Button undoButton;
    Button rectangleModeBtn;
    Button circleModeBtn;
    Button eraserModeBtn;
    Button lineModeBtn;

//    Shake feature Implementation
    private SensorManager mSensorManager;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Initialize sensor data
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Objects.requireNonNull(mSensorManager).registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        canvas = findViewById(R.id.drawing_canvas);
//        Sets first stroke color to yellow
        canvas.pathColor = defaultColor;

//        Assign UI elements to button declarations
        clearButton = findViewById(R.id.clear_button);
        undoButton = findViewById(R.id.undo_button);
        eraserModeBtn = findViewById(R.id.eraser_button);
        lineModeBtn = findViewById(R.id.line_button);
        circleModeBtn = findViewById(R.id.circle_mode);
//        rectangleModeBtn = findViewById(R.id.rec)

//        Color picker setup
        colorPicker = findViewById(R.id.colorPicker_button);
        mLayout = (ConstraintLayout) findViewById(R.id.layout);
        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.design_default_color_on_primary);

//        Clear view control
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canvas.pathsList.size() > 0 && canvas.paintsList.size() > 0 ) {
                    canvas.pathsList.clear();
                    canvas.paintsList.clear();
                    canvas.invalidate();
                }

                if(Circle.circleList.size() > 0 && canvas.paintsList.size() > 0 ){
                    canvas.paintsList.clear();
                    Circle.circleList.clear();
                    canvas.invalidate();
                }
            }
        });

//        Undo Button
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canvas.pathsList.size() > 0 && canvas.paintsList.size() > 0){
                    canvas.pathsList.removeLast();
                    canvas.paintsList.removeLast();
                    canvas.invalidate();
                }
                if(Circle.circleList.size() > 0 && canvas.paintsList.size() > 0 ){
                    canvas.paintsList.removeLast();
                    Circle.circleList.removeLast();
                    canvas.invalidate();
                }
            }
        });

//        Open colorPicker
        colorPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        eraserModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvas.pathColor = Color.WHITE;
            }
        });

        lineModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineModeCounter++;
                if(lineModeCounter %2 == 1) {
                    canvas.lineMode = true;
                    Toast.makeText(MainActivity.this,"Line mode on!", Toast.LENGTH_LONG).show();
                }else{
                    canvas.lineMode = false;
                    Toast.makeText(MainActivity.this,"Line mode off!", Toast.LENGTH_LONG).show();
                }
                canvas.rectangleMode = false;
                canvas.circleMode = false;
            }
        });

//        Circle Mode listener
        circleModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circleModeCounter++;
                if(circleModeCounter %2 == 1){
                    canvas.circleMode = true;
                    Toast.makeText(MainActivity.this,"Circle mode on!", Toast.LENGTH_LONG).show();
                }else{
                    canvas.circleMode = false;
                    Toast.makeText(MainActivity.this,"Circle mode off!", Toast.LENGTH_LONG).show();
                }
                canvas.rectangleMode = false;
                canvas.lineMode = false;
            }
        });
    }

//    Listener for sensor data changes
    private final SensorEventListener mSensorListener = new SensorEventListener() {
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;
        if (mAccel > 12) {
//            On shake event accelerator > 12, clear screen
            if (canvas.pathsList.size() > 0 && canvas.paintsList.size() > 0 ) {
                canvas.pathsList.clear();
                canvas.paintsList.clear();
                canvas.invalidate();
            }

            if(Circle.circleList.size() > 0 && canvas.paintsList.size() > 0 ){
                canvas.paintsList.clear();
                Circle.circleList.clear();
                canvas.invalidate();
            }
        }
    }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

//    OnResume method handler
    @Override
    protected void onResume() {
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

//    OnPause method handler
    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

//    ColorPicker controller
    public void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
//                Do Nothing
            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                canvas.pathColor = color;
            }
        });
        colorPicker.show();
    }
}