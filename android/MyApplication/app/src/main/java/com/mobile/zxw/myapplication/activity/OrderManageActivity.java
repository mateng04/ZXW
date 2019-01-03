package com.mobile.zxw.myapplication.activity;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.zxw.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class OrderManageActivity extends AppCompatActivity  implements View.OnClickListener{

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<String>();//页卡标题集合
    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    private Context context = null;
    LocalActivityManager mactivityManager = null;
    private static int cutterPage = 0;

    Button bt_order_manage_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manage);

        context = OrderManageActivity.this;
        mactivityManager = new LocalActivityManager(this, true);
        mactivityManager.dispatchCreate(savedInstanceState);
        initView();
    }


    public void initView() {

        bt_order_manage_back = (Button) findViewById(R.id.bt_order_manage_back);
        bt_order_manage_back.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.vp_recruit);
        mTabLayout = (TabLayout) findViewById(R.id.tabs_recruit);
        mInflater = LayoutInflater.from(this);

        Intent intent1 = new Intent(getApplicationContext(), OrderBuyerActivity.class);
        view1 = getView("OrderBuyerActivity", intent1);
        Intent intent2 = new Intent(getApplicationContext(), OrderSellerActivity.class);
        view2 = getView("OrderSellerActivity", intent2);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("买家订单");
        mTitleList.add("卖家订单");
        //添加tab选项卡，默认第一个选中
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mAdapter);

        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_order_manage_back:
                finish();
            break;
            default:
            break;
        }
    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }

    /**
     * 通过activity获取视图
     *
     * @param id
     * @param intent
     * @return
     */
    private View getView(String id, Intent intent) {
        return mactivityManager.startActivity(id, intent).getDecorView();
    }

}


