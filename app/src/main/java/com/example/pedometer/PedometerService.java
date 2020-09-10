package com.example.pedometer;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class PedometerService extends Service implements SensorEventListener {

    PedometerDataModel pedometerDataModel;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
    private Realm realm;
    private RealmResults<PedometerDataModel> pedometerDataList;


    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pedometerDataList = realm.where(PedometerDataModel.class).findAll();

        startForegroundService();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    private void startForegroundService() {
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_baseline_fitness_center_24)
                .setContentTitle("만보기")
                .setContentText("만보기 실행중")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, builder.build());
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String name = "Pedometer";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel notificationChannel = new NotificationChannel("Pedometer", name, importance);
        notificationChannel.setShowBadge(false);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            stopSelf();
        }
        return "Pedometer";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pedometerDataList = realm.where(PedometerDataModel.class).findAll();

        startForegroundService();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            int today = Integer.parseInt(date.format(new Date()));

            if (pedometerDataList.isEmpty()) {
                realm.beginTransaction();
                pedometerDataModel = realm.createObject(PedometerDataModel.class);
                pedometerDataModel.setDate(today);
                pedometerDataModel.setSteps(1);
                realm.commitTransaction();

            } else if (pedometerDataList.last().getDate() == today) {
                realm.beginTransaction();
                pedometerDataList.last().addSteps(1);
                realm.commitTransaction();

            } else if (pedometerDataList.last().getDate() != today) {
                realm.beginTransaction();
                pedometerDataModel = realm.createObject(PedometerDataModel.class);
                pedometerDataModel.setDate(today);
                pedometerDataModel.setSteps(1);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}