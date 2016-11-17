package cn.edu.pku.jiangdongyu.miniweather;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.jiangdongyu.bean.TodayWeather;
import cn.edu.pku.jiangdongyu.service.UpdateWeatherService;
import cn.edu.pku.jiangdongyu.util.NetUtil;

/**
 * Created by jiangdongyu on 16/9/20.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private ImageView mUpdateBtn;

    private ImageView mCitySelect;

    private MyWeatherBroadcast mWeatherBroadcast;

    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,
            temperatureTv,climateTv,windTv,city_name_Tv,cur_temperature_Tv;

    private ImageView weatherImg,pmImg;

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch(msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if(NetUtil.getNetWorkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("MiniWeather","网络OK！");
            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_LONG).show();

        }else{
            Log.d("MiniWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
        startWeatherService();

    }

    private void startWeatherService() {
        Intent weatherService = new Intent(this, UpdateWeatherService.class);
        startService(weatherService);
    }

    @Override
    protected void onStart() {
        // 更新时间/天气广播接收器
        mWeatherBroadcast = new MyWeatherBroadcast();

        // 过滤出系统发送的时间改变的广播
        IntentFilter filter1 = new IntentFilter();
        filter1.addAction("WEATHER_CHANGED_ACTION");

        // 注册广播
        registerReceiver(mWeatherBroadcast, filter1);

        super.onStart();
    }

    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm2_5_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        cur_temperature_Tv = (TextView) findViewById(R.id.cur_temperatureNum);

        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        weekTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
        city_name_Tv.setText("N/A");
        cur_temperature_Tv.setText("N/A");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
            //startActivity(i);
            startActivityForResult(i,1);
        }

        if(v.getId() == R.id.title_update_btn){
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetWorkState(this)!=NetUtil.NETWORK_NONE){
                Log.d("myWeather","网络OK！");
                queryWeatherCode(cityCode);

            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }

        }

    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetWorkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather"," 网络OK");
                queryWeatherCode(newCityCode);
            }else{
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);

                    todayWeather = parseXML(responseStr);
                    if(todayWeather!=null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con!=null){
                        con.disconnect();
                    }
                }

            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;

        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while(eventType!=xmlPullParser.END_DOCUMENT){
                switch(eventType){
                      //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                       break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(xmlPullParser.getName().equals("city")){
                            eventType = xmlPullParser.next();
                            todayWeather.setCity(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("updatetime")){
                            eventType = xmlPullParser.next();
                            todayWeather.setUpdatetime(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("shidu")){
                            eventType = xmlPullParser.next();
                            todayWeather.setShidu(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("wendu")){
                            eventType = xmlPullParser.next();
                            todayWeather.setWendu(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("pm25")){
                            eventType = xmlPullParser.next();
                            todayWeather.setPm25(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("quality")){
                            eventType = xmlPullParser.next();
                            todayWeather.setQuality(xmlPullParser.getText());
                        }else if(xmlPullParser.getName().equals("fengxiang") && fengxiangCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setFengxiang(xmlPullParser.getText());
                            fengxiangCount++;
                        }else if(xmlPullParser.getName().equals("fengli") && fengliCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setFengli(xmlPullParser.getText());
                            fengliCount++;
                        }else if(xmlPullParser.getName().equals("date") && dateCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setDate(xmlPullParser.getText());
                            dateCount++;
                        }else if(xmlPullParser.getName().equals("high") && highCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                            highCount++;
                        }else if(xmlPullParser.getName().equals("low") && lowCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                            lowCount++;
                        }
                        else if(xmlPullParser.getName().equals("type") && typeCount==0){
                            eventType = xmlPullParser.next();
                            todayWeather.setType(xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();

            }

        }catch(XmlPullParserException e){
            e.printStackTrace();

        }catch (IOException e){
            e.printStackTrace();
        }
        return todayWeather;

    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        if(todayWeather.getPm25()!=null){
            pmDataTv.setText(todayWeather.getPm25());
            if(Integer.parseInt(todayWeather.getPm25())<50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }else if(Integer.parseInt(todayWeather.getPm25())<100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else if(Integer.parseInt(todayWeather.getPm25())<150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }else if(Integer.parseInt(todayWeather.getPm25())<200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }else if(Integer.parseInt(todayWeather.getPm25())<300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }else{
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }else{
            pmDataTv.setText("N/A");
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        }

        if(todayWeather.getQuality()!=null){
            pmQualityTv.setText(todayWeather.getQuality());
        }else{
            pmQualityTv.setText("N/A");
        }

        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText(todayWeather.getFengli());
        cur_temperature_Tv.setText(todayWeather.getWendu()+"℃");

         switch(todayWeather.getType()){
             case "暴雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
                 break;
             case "暴雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
                 break;
             case "大暴雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
                 break;
             case "大雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
                 break;
             case "大雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
                 break;
             case "多云":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
                 break;
             case "雷阵雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
                 break;
             case "雷阵雨冰雹":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
                 break;
             case "晴":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                 break;
             case "沙尘暴":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
                 break;
             case "特大暴雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
                 break;
             case "雾":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
                 break;
             case "小雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
                 break;
             case "小雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
                 break;
             case "阴":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_yin);
                 break;
             case "雨夹雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
                 break;
             case "阵雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
                 break;
             case "阵雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
                 break;
             case "中雪":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
                 break;
             case "中雨":
                 weatherImg.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
                 break;
         }
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, UpdateWeatherService.class));//停止更新服务
        unregisterReceiver(mWeatherBroadcast);
        super.onDestroy();
    }


    public class MyWeatherBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到citycode
            SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sp.getString("main_city_code","101010100");
            queryWeatherCode(cityCode);

        }
    }
}
