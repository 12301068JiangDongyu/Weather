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
import android.widget.TextView;

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
    private TextView titleName;
    private ListView listView;
    private EditText eSearch;
    private ArrayList<City> filterCityList;
    private List<City> mCityList;
    private ArrayAdapter adapter;
    private ArrayAdapter adapterFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        mCityList = MyApplication.getInstance().getCityList();
        filterCityList = new ArrayList<City>();

        titleName = (TextView) findViewById(R.id.title_name);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        listView = (ListView) findViewById(R.id.city_list);
        eSearch = (EditText) findViewById(R.id.etSearch);

        titleName.setText("当前城市：" + this.getIntent().getStringExtra("cityName"));

        mBackBtn.setOnClickListener(this);

        initCityList();
        bindEvents();

    }

    private void set_listView_adapter() {
        if (filterCityList.size() == 0) {
            adapter = new ArrayAdapter<City>(this, android.R.layout.simple_expandable_list_item_1, mCityList);
            listView.setAdapter(adapter);
        } else {
            adapterFilter = new ArrayAdapter<City>(this, android.R.layout.simple_expandable_list_item_1, filterCityList);
            listView.setAdapter(adapterFilter);//设置搜索后的adapter
        }

    }

    private void initCityList() {
        set_listView_adapter();
    }

    private void bindEvents() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City item = (City) listView.getItemAtPosition(position);
                Intent i = new Intent();
                i.putExtra("cityCode", item.getNumber());

                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("main_city_code", item.getNumber());
                editor.commit();

                setResult(RESULT_OK, i);
                titleName.setText("当前城市：" + item.getCity());
                finish();
            }
        });

        eSearch.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                temp = s;
                filterCityList.clear();
                for (City item : mCityList) {
                    if (item.toString().contains(eSearch.getText().toString().trim())) {
                        filterCityList.add(item);
                    }
                }
                set_listView_adapter();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                SelectCity.this.finish();
                break;
            default:
                break;
        }
    }
}
