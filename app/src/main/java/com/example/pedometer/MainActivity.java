package com.example.pedometer;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Realm realm;
    RealmResults<PedometerDataModel> pedometerDataList;

    SensorManager sensorManager;
    Sensor stepDetectorSensor;

    private RecyclerView recyclerView;
    private PedometerListAdapter pedometerListAdapter = new PedometerListAdapter();
    private TextView tv_pedometer;
    private TextView tv_date;
    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_pedometer = findViewById(R.id.tv_pedometer);
        tv_date = findViewById(R.id.tv_date);
        recyclerView = findViewById(R.id.rv_view);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pedometerDataList = realm.where(PedometerDataModel.class).findAll();

        Intent intent = new Intent(this, PedometerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(intent);
        else startService(intent);

        recyclerView.setAdapter(pedometerListAdapter);
        pedometerListAdapter.setList(pedometerDataList);

        int date = Integer.parseInt(this.date.format(new Date()));

        if (!pedometerDataList.isEmpty()) {
            if (pedometerDataList.last().getDate() == date) {
                tv_pedometer.setText(String.valueOf(pedometerDataList.last().getSteps()));
            } else tv_pedometer.setText("0");
        } else tv_pedometer.setText("0");
        tv_date.setText(Integer.toString(date));
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            tv_pedometer.setText(String.valueOf(pedometerDataList.last().getSteps()));
            tv_date.setText(String.valueOf(pedometerDataList.last().getDate()));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}