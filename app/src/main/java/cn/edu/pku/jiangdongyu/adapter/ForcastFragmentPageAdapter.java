package cn.edu.pku.jiangdongyu.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by jiangdongyu on 2016/12/2.
 */
public class ForcastFragmentPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;

    public ForcastFragmentPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        mFragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    private int mChildCount = 0;
    @Override    public void notifyDataSetChanged() {
        // 重写这个方法，取到子Fragment的数量，用于下面的判断，以执行多少次刷新
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }
    @Override    public int getItemPosition(Object object) {
        if ( mChildCount > 0) {
            // 这里利用判断执行若干次不缓存，刷新
            mChildCount --;
            // 返回这个是强制ViewPager不缓存，每次滑动都刷新视图
            return POSITION_NONE;
        }
        // 这个则是缓存不刷新视图
        return super.getItemPosition(object);
    }
}
