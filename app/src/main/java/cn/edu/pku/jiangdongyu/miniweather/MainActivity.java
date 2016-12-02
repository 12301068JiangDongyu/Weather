package cn.edu.pku.jiangdongyu.miniweather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.jiangdongyu.adapter.ForcastFragmentPageAdapter;
import cn.edu.pku.jiangdongyu.fragment.MyFragment;
import cn.edu.pku.jiangdongyu.service.UpdateWeatherService;
import cn.edu.pku.jiangdongyu.bean.FutureWeather;
import cn.edu.pku.jiangdongyu.bean.TodayWeather;
import cn.edu.pku.jiangdongyu.util.NetUtil;
import cn.edu.pku.jiangdongyu.util.ParseData;

/**
 * Created by jiangdongyu on 2016/9/20.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_FUTURE_WEATHER = 2;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private ProgressBar mTitleUpdateProgress;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTv,climateTv,windTv,city_name_Tv,temp_now_Tv,textView;
    private ImageView weatherImg,pmImg;

    private MyWeatherBroadcast mWeatherBroadcast;

    private SharedPreferences sharedPreferences;
    private ForcastFragmentPageAdapter ffpAdapter;
    private ViewPager vp;
    private List<Fragment> fragments;

    private ImageView[] dots;
    private int[] ids = {R.id.iv4,R.id.iv5};
    //更新后的cityCode
    private String newCityCode = "101010100";
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                case UPDATE_FUTURE_WEATHER:
                    updateFutureWeather((List<FutureWeather>) msg.obj);
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
        //sharedPreferences判断加初始化
        initSharedPreference();

        mTitleUpdateProgress = (ProgressBar) findViewById(R.id.title_update_progress);
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);

        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_LONG).show();
        }else {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }

        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        initView();
        initAfterView(null);

        startWeatherService();
    }

    //sharedPreferences判断加初始化
    private void initSharedPreference(){
        //记住上次退出应用之前选择的城市
        sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        String cityCode = sharedPreferences.getString("main_city_code","");
        Log.d("test1",cityCode);
        if(!cityCode.isEmpty()){
            newCityCode = cityCode;
        }
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

    //初始化页面控件
    void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_25);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        temp_now_Tv = (TextView) findViewById(R.id.temperature_now);

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
        temp_now_Tv.setText("N/A");
    }

    void initAfterView(List<FutureWeather> list){
        dots = new ImageView[2];
        for(int i=0;i<2;i++){
            dots[i] = (ImageView) findViewById(ids[i]);
        }
        fragments=new ArrayList<Fragment>();
        List<FutureWeather> listOne = new ArrayList<FutureWeather>();
        List<FutureWeather> listTwo = new ArrayList<FutureWeather>();
        if(list != null){
            for(int i=0;i<list.size();i++){
                if(i<3){
                    listOne.add(list.get(i));
                    Log.d("test",listOne.toString());
                }else{
                    listTwo.add(list.get(i));
                }
            }
        }

        vp = (ViewPager) findViewById(R.id.main_viewpager);
        for (int i=0;i<2;i++){
            Bundle bundle=new Bundle();
            if(i==0){
                bundle.putSerializable("list", (Serializable) listOne);
            }else{
                bundle.putSerializable("list", (Serializable) listTwo);
            }
            MyFragment myFragment=new MyFragment();
            myFragment.setArguments(bundle);
            fragments.add(myFragment);
        }
        ffpAdapter = new ForcastFragmentPageAdapter(getSupportFragmentManager(),fragments);
        vp.setAdapter(ffpAdapter);

        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int index)//设置标题的颜色以及下划线的移动效果
            {
                for(int i=0;i<ids.length;i++){
                    if(i==index){
                        dots[i].setImageResource(R.drawable.page_indicator_focused_red);
                    }else {
                        dots[i].setImageResource(R.drawable.page_indicator_unfocused_red);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int index)
            {
            }
        });  ;
    }

    void updateFutureWeather(List<FutureWeather> list){
        //start
        // 可以删除这段代码看看，数据源更新而viewpager不更新的情况
        // 在数据源更新前增加的代码，将上一次数据源的fragment对象从FragmentManager中删除
        if (vp.getAdapter() != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> fragments = fm.getFragments();
            if(fragments != null && fragments.size() >0){
                for (int i = 0; i < fragments.size(); i++) {
                    ft.remove(fragments.get(i));
                }
            }
            ft.commit();
        }
        // End
        List<FutureWeather> listOne = new ArrayList<FutureWeather>();
        List<FutureWeather> listTwo = new ArrayList<FutureWeather>();
        for(int i=0;i<list.size();i++){
            if(i<3){
                listOne.add(list.get(i));
                Log.d("test",listOne.toString());
            }else{
                listTwo.add(list.get(i));
            }
        }
        fragments.clear();
        for (int i=0;i<2;i++){
            Bundle bundle=new Bundle();
            if(i==0){
                bundle.putSerializable("list", (Serializable) listOne);
            }else{
                bundle.putSerializable("list", (Serializable) listTwo);
            }
            MyFragment myFragment=new MyFragment();
            myFragment.setArguments(bundle);
            fragments.add(myFragment);
        }
        // 重写adapter的notifyDataChanged方法
        ffpAdapter.notifyDataSetChanged();
    }
    //更新图片
    void updateTodayImg(TodayWeather todayWeather){
        //对pm进行判断
        if(todayWeather.getPm25()!=null){
            int pm25 = Integer.parseInt(todayWeather.getPm25());
            if(pm25>=0&&pm25<=50){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
            }else if(pm25<=100){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
            }else if(pm25<=150){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
            }else if(pm25<=200){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
            }else if(pm25<=300){
                pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
            }else {
                pmImg.setImageResource(R.drawable.biz_plugin_weather_greater_300);
            }
        }

        String weather = todayWeather.getType();

        switch (weather){
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
            case "晴转霾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
                break;
        }
    }

    //更新其他信息
    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        if(todayWeather.getPm25()==null){
            pmDataTv.setText("N/A");
        }else{
            pmDataTv.setText(todayWeather.getPm25());
        }
        if(todayWeather.getQuality()==null){
            pmQualityTv.setText("N/A");
        }else{
            pmQualityTv.setText(todayWeather.getQuality());
        }

        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:"+todayWeather.getFengli());
        temp_now_Tv.setText(todayWeather.getWendu()+"℃");
        updateTodayImg(todayWeather);
        //更新完成显示
        mUpdateBtn.setVisibility(View.VISIBLE);
        mTitleUpdateProgress.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this,"更新成功",Toast.LENGTH_SHORT).show();
    }
    //根据cityCode，获取城市天气信息
    private  void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        final String futureAddress = "http://api.k780.com:88/?app=weather.future&weaid="+cityCode+"&appkey=10003&sign=b59bc3ef6191eb9f747dd4e83c99f2a4&format=xml"+cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TodayWeather todayWeather = null;
                ParseData parseData = new ParseData();
                try {
                    String responseStr = parseData.readData(address);
                    todayWeather = parseData.parseTodayXML(responseStr);

                    if(todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }

                    String responseFutureStr = parseData.readData(futureAddress);
                    List<FutureWeather> list = new ArrayList<FutureWeather>();
                    //获取并解析今日天气以及未来天气
                    list = parseData.parseFutureJSON(responseFutureStr);
                    if(list != null){
                        Message msg = new Message();
                        msg.what = UPDATE_FUTURE_WEATHER;
                        msg.obj = list;
                        mHandler.sendMessage(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this,SelectCity.class);
            i.putExtra("cityName",city_name_Tv.getText());
            startActivityForResult(i,1);
        }
        if(v.getId() == R.id.title_update_btn){
            //将刷新按钮设为不可见;
            mTitleUpdateProgress.setVisibility(View.VISIBLE);
            mUpdateBtn.setVisibility(View.INVISIBLE);
            sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code",newCityCode);
            Log.d("myWeather1",cityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather","网络OK");
                queryWeatherCode(cityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }
    //接收返回的数据
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            newCityCode=data.getStringExtra("cityCode");

            Log.d("myWeather","选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }else {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }
}
