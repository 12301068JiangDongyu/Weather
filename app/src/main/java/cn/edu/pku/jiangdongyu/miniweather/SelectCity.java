package cn.edu.pku.jiangdongyu.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.pku.jiangdongyu.app.MyApplication;
import cn.edu.pku.jiangdongyu.bean.City;

/**
 * Created by jiangdongyu on 2016/10/18.
 */
public class SelectCity extends Activity implements View.OnClickListener {
    private ImageView mBackBtn;
    private ListView listView;
    //EditText eSearch;
    //ImageView ivDeleteText;

    List<City> mCityList;
    //private ArrayList<City> filterCityList;
    ArrayAdapter adapter;
    //Handler myhandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mCityList = MyApplication.getInstance().getCityList();
        //filterCityList = new ArrayList<City>();
        mBackBtn = (ImageView) findViewById(R.id.title_back);

        mBackBtn.setOnClickListener(this);
        //set_eSearch_TextChanged();//设置eSearch搜索框的文本改变时监听器l
        set_listView_adapter();
    }

//    private void initCityList(){
//        setCityListAdapter();
//    }
//    private void setCityListAdapter(){
//        if(filterCityList.size() == 0){
//            listView.setAdapter(new ArrayAdapter<City>(this, android.R.layout.simple_expandable_list_item_1, filterCityList));
//        }else{
//            set_listView_adapter();
//        }
//    }

    private void set_listView_adapter() {
        listView = (ListView) findViewById(R.id.city_list);
        adapter = new ArrayAdapter<City>(this, android.R.layout.simple_expandable_list_item_1, mCityList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City item =(City)listView.getItemAtPosition(position);
                Intent i = new Intent();
                i.putExtra("cityCode",item.getNumber());

                SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
                SharedPreferences.Editor editor  = sharedPreferences.edit();
                editor.putString("main_city_code",item.getNumber());
                editor.commit();

                setResult(RESULT_OK,i);
                finish();
            }
        });
    }

    /**
     * 设置搜索框的文本更改时的监听器
     */
//    private void set_eSearch_TextChanged()
//    {
//        eSearch = (EditText) findViewById(R.id.etSearch);
//        eSearch.addTextChangedListener(new TextWatcher() {
//            private CharSequence temp;
//
//            @Override
//            public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
//                // TODO Auto-generated method stub
//                temp = s;
//                filterCityList.clear();
//                for(City item : mCityList){
//                    if(item.toString().contains(eSearch.getText().toString().trim())){
//                        filterCityList.add(item);
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                          int arg3) {
//                // TODO Auto-generated method stub
//                //这是文本框改变之前会执行的动作
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode","101020100");
                setResult(RESULT_OK,i);
                finish();
                break;
            default:
                break;
        }
    }
}
