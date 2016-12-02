package cn.edu.pku.jiangdongyu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import cn.edu.pku.jiangdongyu.miniweather.MainActivity;

/**
 * Created by jiangdongyu on 2016/11/15.
 */
public class UpdateWeatherService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //定时器每隔1小时发送一次广播
        new Timer().scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Intent weatherIntent = new Intent();
                weatherIntent.setAction("WEATHER_CHANGED_ACTION");//自定义Action
                sendBroadcast(weatherIntent); //发送广播
            }
        }, 0, 60*60*1000);

    }




}
