package cn.edu.pku.jiangdongyu.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.jiangdongyu.adapter.ViewPagerAdapter;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener{
    private ViewPagerAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private ImageView[] dots;
    private int[] ids = {R.id.iv1,R.id.iv2,R.id.iv3};

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide);
        initSharedPreferences();

        initViews();
        initDots();

        btn = (Button) views.get(2).findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    //初始化页面
    private void initViews(){
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.welcome_page1,null));
        views.add(inflater.inflate(R.layout.welcome_page2,null));
        views.add(inflater.inflate(R.layout.welcome_page3,null));
        vpAdapter = new ViewPagerAdapter(views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);
    }

    //初始化圆点
    private void initDots(){
        dots = new ImageView[views.size()];
        for(int i=0;i<views.size();i++){
            dots[i] = (ImageView) findViewById(ids[i]);
        }
    }



    private void initSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);

        String judgeFirst = sharedPreferences.getString("guide_judge","");
        if(judgeFirst.isEmpty()){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("guide_judge","true");
            editor.commit();
        }else {
            Intent i = new Intent(GuideActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }

    }
    //重写导航换页事件监听
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<ids.length;i++){
            if(i==position){
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }else {
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
