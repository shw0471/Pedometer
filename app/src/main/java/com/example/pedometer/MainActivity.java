package com.example.pedometer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    PedometerDataModel pedometerDataModel;
    RealmResults<PedometerDataModel> pedometerDataList;

    RecyclerView recyclerView;
    PedometerListAdapter pedometerListAdapter = new PedometerListAdapter();

    private PedometerService pedometerService = new PedometerService();
    private TextView tv_pedometer;
    public StepCallback stepCallback = new StepCallback() {
        @Override
        public void onStepCallBack(int date) {
            if (pedometerDataList.isEmpty()) {
                realm.beginTransaction();
                pedometerDataModel = realm.createObject(PedometerDataModel.class);
                pedometerDataModel.setDate(date);
                pedometerDataModel.setSteps(1);
                realm.commitTransaction();

                tv_pedometer.setText(String.valueOf(pedometerDataModel.getSteps()));

            } else if (pedometerDataList.last().getDate() == date) {
                realm.beginTransaction();
                pedometerDataList.last().addSteps(1);
                realm.commitTransaction();

                tv_pedometer.setText(String.valueOf(pedometerDataList.last().getSteps()));

            } else if (pedometerDataList.last().getDate() != date) {
                realm.beginTransaction();
                pedometerDataModel = realm.createObject(PedometerDataModel.class);
                pedometerDataModel.setDate(date);
                pedometerDataModel.setSteps(1);
                realm.commitTransaction();

                tv_pedometer.setText(String.valueOf(pedometerDataModel.getSteps()));
            }
        }
    };

    private Button btn_f5;
    private Date today = new Date();
    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PedometerService.MyBinder myBinder = (PedometerService.MyBinder) iBinder;
            pedometerService = myBinder.getService();
            pedometerService.setCallback(stepCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_pedometer = findViewById(R.id.tv_pedometer);
        recyclerView = findViewById(R.id.rv_view);
        btn_f5 = findViewById(R.id.btn_f5);

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pedometerDataList = realm.where(PedometerDataModel.class).findAll();

        Intent intent = new Intent(this, PedometerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


        recyclerView.setAdapter(pedometerListAdapter);
        pedometerListAdapter.setList(pedometerDataList);

        int date = Integer.parseInt(this.date.format(today));

        if (!pedometerDataList.isEmpty()) {
            if (pedometerDataList.last().getDate() == date) {
                tv_pedometer.setText(String.valueOf(pedometerDataList.last().getSteps()));
            } else tv_pedometer.setText("0");
        } else tv_pedometer.setText("0");

        btn_f5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pedometerListAdapter.notifyDataSetChanged();
            }
        });
    }
}