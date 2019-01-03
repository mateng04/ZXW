package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.MenuDialogAdapter;
import com.mobile.zxw.myapplication.adapter.MyPagerAdapter;
import com.mobile.zxw.myapplication.bean.MenuData;
import com.mobile.zxw.myapplication.ui.MyViewPager;
import com.mobile.zxw.myapplication.until.MenuDataManager;

import java.util.ArrayList;
import java.util.List;

public class ShopScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CODE = 100;//请求码
    private Context mContext;
    public MenuDataManager menuDataManager = MenuDataManager.getInstance();
    private MyViewPager mViewPager;
    private View view1,view2,view3;
    private ListView mListView1,mListView2,mListView3;
    private MenuDialogAdapter mListView1Adapter, mListView2Adapter, mListView3Adapter;
    private List<View> views = new ArrayList<View>();
    private MenuData resultDate;
    private ArrayList<MenuData> selectList = new ArrayList<>();

    private Button bt_shop_screen_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_screen);
        mContext = this;

        selectList.clear();
        ArrayList<MenuData> select = getIntent().getExtras().getParcelableArrayList("select");
        selectList.addAll(select);

        System.out.println("result--------select-"+select.size());
        initViews();

    }

    //操作控件
    private void initViews() {

        bt_shop_screen_back = (Button) findViewById(R.id.bt_shop_screen_back);
        bt_shop_screen_back.setOnClickListener(this);
        //一级
        mViewPager = (MyViewPager) findViewById(R.id.viewpager_shop_screen);
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.pager_number, null);
        view2 = inflater.inflate(R.layout.pager_number, null);
//        view3 = inflater.inflate(R.layout.pager_number, null);
        mListView1 = (ListView) view1.findViewById(R.id.listview);
        mListView2 = (ListView) view2.findViewById(R.id.listview);
//        mListView3 = (ListView) view3.findViewById(R.id.listview);

        List<MenuData> list1=menuDataManager.getTripleColumnData(this, 0);
        mListView1Adapter = new MenuDialogAdapter(this, list1);
        mListView1Adapter.setSelectedBackgroundResource(R.mipmap.select_white);//选中时
        mListView1Adapter.setHasDivider(false);
        mListView1Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
        mListView1.setAdapter(mListView1Adapter);

        //下面这个方法主要是为了显示上次选中的数据
        if(selectList.size() > 0){
            for(int i=0;i<list1.size();i++){
                if(selectList.get(0).getId() == list1.get(i).getId()){
                    if (mListView1Adapter != null){
                        mListView1Adapter.setSelectedPos(i);
                    }

                    List<MenuData> list2 = menuDataManager.getTripleColumnData(mContext, list1.get(i).getId());
                    if (mListView2Adapter == null) {
                        mListView2Adapter = new MenuDialogAdapter(mContext, list2);
//                        mListView2Adapter.setNormalBackgroundResource(R.color.white);
                        mListView2Adapter.setSelectedBackgroundResource(R.mipmap.select_white);//选中时
                        mListView2Adapter.setHasDivider(false);
                        mListView2Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                        mListView2.setAdapter(mListView2Adapter);
                    } else {
                        mListView2Adapter.setData(list2);
                        mListView2Adapter.notifyDataSetChanged();
                    }

                    for(int x=0;x<list2.size();x++){
                        if(selectList.get(1).getId() == list2.get(x).getId()){
                            mListView2Adapter.setSelectedPos(x);
                        }
                    }

                }

            }
        }else {
            if (mListView1Adapter != null){
                mListView1Adapter.setSelectedPos(0);
            }
            //第一级 1000,不限,0;    第二级 1000,不限,1000;
            List<MenuData> list2 = menuDataManager.getTripleColumnData(mContext, 1000);
            if (mListView2Adapter == null) {
                mListView2Adapter = new MenuDialogAdapter(mContext, list2);
                mListView2Adapter.setSelectedBackgroundResource(R.mipmap.select_white);//选中时
                mListView2Adapter.setHasDivider(false);
                mListView2Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                mListView2.setAdapter(mListView2Adapter);
            } else {
                mListView2Adapter.setData(list2);
                mListView2Adapter.notifyDataSetChanged();
            }

            mListView2Adapter.setSelectedPos(0);
        }


        views.add(view1);
        views.add(view2);//加载了一二级菜单
        mViewPager.setAdapter(new MyPagerAdapter(views));

        mListView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListView1Adapter != null)
                    mListView1Adapter.setSelectedPos(position);
                if (mListView2Adapter != null)
                    mListView2Adapter.setSelectedPos(-1);

//                if (views.contains(view3)) {
//                    views.remove(view3);
//                    mViewPager.getAdapter().notifyDataSetChanged();
//                }
                selectList.clear();
                MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                selectList.add(menuData);

                if (menuData.id == 0) {//不限
                    if (mListView2Adapter != null) {
                        mListView2Adapter.setData(new ArrayList<MenuData>());
                        mListView2Adapter.notifyDataSetChanged();
                    }

//                    setResultDate(menuData);
                } else {
                    List<MenuData> list2 = menuDataManager.getTripleColumnData(mContext, menuData.id);
                    if (mListView2Adapter == null) {
                        mListView2Adapter = new MenuDialogAdapter(mContext, list2);
                        mListView2Adapter.setSelectedBackgroundResource(R.mipmap.select_white);//选中时
                        mListView2Adapter.setHasDivider(false);
                        mListView2Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
                        mListView2.setAdapter(mListView2Adapter);
                    } else {
                        mListView2Adapter.setData(list2);
                        mListView2Adapter.notifyDataSetChanged();
                    }
//                    mRootView.invalidate();
                }

            }
        });

        //二级
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
//                if (mListView2Adapter != null) {
//                    mListView2Adapter.setSelectedPos(position);
//                    mListView2Adapter.setSelectedBackgroundResource(R.drawable.select_gray);//选中时
//                }
//
//                if (views.contains(view3)) {
//                    views.remove(view3);
//                }
//
//                MenuData dictUnit = (MenuData) parent.getItemAtPosition(position);
//                List<MenuData> list3 = menuDataManager.getTripleColumnData(mContext, dictUnit.id);
//                if (mListView3Adapter == null) {
//                    mListView3Adapter = new MenuDialogAdapter(mContext, list3);
//                    mListView3Adapter.setHasDivider(false);
//                    mListView3Adapter.setNormalBackgroundResource(R.color.menudialog_bg_gray);//未选中
//                    mListView3.setAdapter(mListView3Adapter);
//                } else {
//                    mListView3Adapter.setData(list3);
//                    mListView3Adapter.notifyDataSetChanged();
//                }
//
//                views.add(view3);
//                mViewPager.getAdapter().notifyDataSetChanged();
//                mViewPager.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mViewPager.setCurrentItem(views.size()-1);
//                    }
//                }, 300);

                MenuData menuData = (MenuData) parent.getItemAtPosition(position);
                if(selectList.size() > 1){
                    selectList.remove(1);
                }
                selectList.add(menuData);
                setResultDate(selectList);
            }
        });
//        mListView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                MenuData menuData = (MenuData) parent.getItemAtPosition(position);
//                setResultDate(menuData);
//            }
//        });

    }

    //传递值
    private void setResultDate(ArrayList<MenuData> selectList){

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("result",selectList);
        intent.putExtras(bundle);

        setResult(RESULT_OK, intent);
        finish();


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_shop_screen_back:
                finish();
            break;
            default:
            break;
        }
    }
}
