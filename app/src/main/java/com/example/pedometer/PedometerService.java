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
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PedometerService extends Service implements SensorEventListener {


    private MyBinder myBinder = new MyBinder();
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private StepCallback callback;
    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

    public void setCallback(StepCallback callback) {
        this.callback = callback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel);
        } else {
            stopSelf();
        }
        return "Pedometer";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        if (stepDetectorSensor != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (callback != null)
                callback.onStepCallBack(Integer.parseInt(date.format(new Date())));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    class MyBinder extends Binder {
        PedometerService getService() {
            return PedometerService.this;
        }
    }
}