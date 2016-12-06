package cn.edu.pku.jiangdongyu.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.edu.pku.jiangdongyu.bean.FutureWeather;
import cn.edu.pku.jiangdongyu.miniweather.R;


public class MyFragment extends Fragment {

    private TextView weekTv,temperatureTv,climateTv,windTv,weekTv1,temperatureTv1,climateTv1,windTv1,weekTv2,temperatureTv2,climateTv2,windTv2;
    private ImageView weatherImg1,weatherImg2,weatherImg;

    void initView(View view){
        weekTv = (TextView) view.findViewById(R.id.week_after);
        temperatureTv = (TextView) view.findViewById(R.id.temperature_after);
        climateTv = (TextView) view.findViewById(R.id.climate_after);
        windTv = (TextView) view.findViewById(R.id.wind_after);

        weekTv1 = (TextView) view.findViewById(R.id.week_after1);
        temperatureTv1 = (TextView) view.findViewById(R.id.temperature_after1);
        climateTv1 = (TextView) view.findViewById(R.id.climate_after1);
        windTv1 = (TextView) view.findViewById(R.id.wind_after1);

        weekTv2 = (TextView) view.findViewById(R.id.week_after2);
        temperatureTv2 = (TextView) view.findViewById(R.id.temperature_after2);
        climateTv2 = (TextView) view.findViewById(R.id.climate_after2);
        windTv2 = (TextView) view.findViewById(R.id.wind_after2);

        weatherImg = (ImageView) view.findViewById(R.id.weather_img_after);
        weatherImg1 = (ImageView) view.findViewById(R.id.weather_img_after1);
        weatherImg2 = (ImageView) view.findViewById(R.id.weather_img_after2);

        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");

        weekTv1.setText("N/A");
        temperatureTv1.setText("N/A");
        climateTv1.setText("N/A");
        windTv1.setText("N/A");

        weekTv2.setText("N/A");
        temperatureTv2.setText("N/A");
        climateTv2.setText("N/A");
        windTv2.setText("N/A");

        //textView = (TextView) findViewById(R.id.textView);
    }

    void weatherJudge(String weather,ImageView weatherImg){
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
            case "霾转晴":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_mai);
                break;
            case "霾":
                weatherImg.setImageResource(R.drawable.biz_plugin_weather_mai);
                break;

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        initView(view);
        List<FutureWeather> list = (List<FutureWeather>) getArguments().getSerializable("list");
        if(!list.isEmpty()){
            weekTv.setText(list.get(0).getDate());
            temperatureTv.setText(list.get(0).getWendu());
            climateTv.setText(list.get(0).getType());
            windTv.setText(list.get(0).getFengli());
            weatherJudge(list.get(0).getType(),weatherImg);

            weekTv1.setText(list.get(1).getDate());
            temperatureTv1.setText(list.get(1).getWendu());
            climateTv1.setText(list.get(1).getType());
            windTv1.setText(list.get(1).getFengli());
            weatherJudge(list.get(1).getType(),weatherImg1);

            weekTv2.setText(list.get(2).getDate());
            temperatureTv2.setText(list.get(2).getWendu());
            climateTv2.setText(list.get(2).getType());
            windTv2.setText(list.get(2).getFengli());
            weatherJudge(list.get(2).getType(),weatherImg2);
        }

        return view;
    }
}
