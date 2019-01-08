package com.mobile.zxw.myapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.ShopYJBean;
import com.mobile.zxw.myapplication.bean.XiaoFeiJinEBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.myinterface.OnTabActivityResultListener;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.ui.area.BottomDialog;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IListener, View.OnClickListener {

    private String shengID="";
    private String shiID="";
    private String quxianID="";
    private String xiangzhenID="";

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<String>();//页卡标题集合
    private View view1, view2, view3, view4, view5;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private LocalActivityManager manager;

    DrawerLayout drawer;
    private View drawerRootView;

    Context context = null;
    LocalActivityManager mactivityManager = null;

    LinearLayout ll_hyzx_zpgl, ll_hyzx_qzgl, ll_hyzx_scgl, ll_hyzx_ddgl, ll_hyzx_zjgl, ll_hyzx_gggl, ll_hyzx_xxzx;

    TextView tv_header_cz, tv_header_mx;
    TextView tv_city_name;
    TextView tv_hyzx_tc;    // 退出按钮

    TextView tv_hyzx_kf;    // 客服电话

    BottomDialog dialog;

    ShopYJBean shopYJBean;
    String zhye;

    TextView tv_hyzx_hy,tv_hyzx_ktviphy,tv_hyzx_xm,tv_hyzx_zhye,tv_hyzx_xfje;
    ImageView imageView_tx;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    //加载对话框
    private Dialog mLoadingDialog;
    static int YJ_OK = 0;
    static int YJ_ERROR = 1;
    static int XFJE_OK = 2;
    static int XFJE_ERROR = 3;
    static int YE_DATA_OK = 4;
    static int YE_DATA_ERROR = 5;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    cancelDialog();
                    SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                            MainActivity.this, "setting");
                    String shopyajin = (String) sp_setting.getSharedPreference("shopyajin","");
                    System.out.println("shopyajin----"+shopyajin);
                    System.out.println("shopYJBean.getShopyajin()----"+shopYJBean.getShopyajin());
                    if(shopYJBean != null && !"".equals(shopYJBean.getShopyajin())){
                        if(Integer.valueOf(shopYJBean.getShopyajin()) < Integer.valueOf(shopyajin)){
                            showYZDialog();
                        }else{
                            startActivity(new Intent(MainActivity.this, MallManageActivity.class));
                        }
                    }
                    break;
                case 1:
                    cancelDialog();
                    break;
                case 2:
                    String xfje = (String) sharedPreferencesHelper.getSharedPreference("xfje", "");
                    tv_hyzx_xfje.setText(xfje);
                    break;
                case 4:
                    tv_hyzx_zhye.setText(zhye);
                    break;
                case 5:
                    break;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }

        sharedPreferencesHelper = new SharedPreferencesHelper(
                MainActivity.this, "config");

//        shengID = (String) sharedPreferencesHelper.getSharedPreference("shengID", "");
//        shiID = (String) sharedPreferencesHelper.getSharedPreference("shiID", "");
//        quxianID = (String) sharedPreferencesHelper.getSharedPreference("quxianID", "");
//        xiangzhenID = (String) sharedPreferencesHelper.getSharedPreference("xiangzhenID", "");

        context = MainActivity.this;
        mactivityManager = new LocalActivityManager(this, true);
        mactivityManager.dispatchCreate(savedInstanceState);

        //注册监听器
        ListenerManager.getInstance().registerListtener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        drawerRootView = findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerView.setClickable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        });


        toolbar.setNavigationIcon(R.mipmap.toolbar_logo);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
                System.out.println("sessionID-------------"+sessionID);
                String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
                if(!"".equals(sessionID) && !"".equals(userid)){
                    openOrCloseDrawer();
                }else{
                    startActivity(new Intent(MainActivity.this, LoginPageActivity.class));
                }
            }
        });
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); //关闭手势滑动,侧边栏关闭
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED); //打开手势滑动，侧边栏开启
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        initView();

    }

    private void openOrCloseDrawer() {
        if (drawer.isDrawerOpen(drawerRootView)) {
            drawer.closeDrawer(drawerRootView);
        } else {
            drawer.openDrawer(drawerRootView);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        System.out.println("main___dispatchKeyEvent");
        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    // 用来计算返回键的点击间隔时间
    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return;
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.city_choice);
        tv_city_name = (TextView) MenuItemCompat.getActionView(item);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //打开评论
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this,CityChoiceActivity.class);
//                startActivity(intent);

                dialog.show();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.city_choice) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        System.out.println("id----" + id);
//        if (id == R.id.ll_hyzx_zpgl) {
//            // Handle the camera action
//        } else if (id == R.id.ll_hyzx_qzgl) {
//
//        } else if (id == R.id.ll_hyzx_scgl) {
//
//        } else if (id == R.id.ll_hyzx_ddgl) {
//
//        } else if (id == R.id.ll_hyzx_zjgl) {
//
//        } else if (id == R.id.ll_hyzx_gggl) {
//
//        }else if (id == R.id.ll_hyzx_xxzx) {
//            startActivity(new Intent(MainActivity.this,MessageCenterActivity.class));
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume----");
        getxiaofeijine();
        getYuE();

        String userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");
        tv_hyzx_xm.setText(userName);
//        String zhye = (String) sharedPreferencesHelper.getSharedPreference("zhye", "");
//        tv_hyzx_zhye.setText(zhye);
//        String xfje = (String) sharedPreferencesHelper.getSharedPreference("xfje", "");
//        tv_hyzx_xfje.setText(xfje);
        String hyjb = (String) sharedPreferencesHelper.getSharedPreference("hyjb", "");
        if("".equals(hyjb)){
            tv_hyzx_hy.setText("普通会员");
            tv_hyzx_ktviphy.setVisibility(View.VISIBLE);
        }else if("普通".equals(hyjb)){
            tv_hyzx_hy.setText("普通会员");
            tv_hyzx_ktviphy.setVisibility(View.VISIBLE);
        }else if("VIP".equals(hyjb)){
            tv_hyzx_hy.setText("VIP会员");
            tv_hyzx_ktviphy.setVisibility(View.VISIBLE);
        }

        mactivityManager.dispatchResume();
        System.out.println("mViewPager.getCurrentItem()---"+mViewPager.getCurrentItem());
        if(mViewPager != null){
            switch (mViewPager.getCurrentItem()) {
                case 3:
                    Activity subActivity = mactivityManager.getActivity("MallPageActivity");
                    if(subActivity != null ){
                        ((MallPageActivity)subActivity ).invisibleOnScreen();
                    }
                    break;
                case 4:
                    Activity advertisingManageActivity = mactivityManager.getActivity("AdvertisingManageActivity");
                    if(advertisingManageActivity != null ){
                        ((AdvertisingManageActivity)advertisingManageActivity ).invisibleOnScreen();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void initView() {

        tv_hyzx_xm = (TextView) findViewById(R.id.tv_hyzx_xm);
        tv_hyzx_zhye = (TextView) findViewById(R.id.tv_hyzx_zhye);
        tv_hyzx_xfje = (TextView) findViewById(R.id.tv_hyzx_xfje);
        tv_hyzx_hy = (TextView) findViewById(R.id.tv_hyzx_hy);
        tv_hyzx_ktviphy = (TextView) findViewById(R.id.tv_hyzx_ktviphy);
        tv_hyzx_ktviphy.setOnClickListener(this);

        imageView_tx = (ImageView) findViewById(R.id.imageView_tx);
        imageView_tx.setOnClickListener(this);

        dialog = new BottomDialog(this);
        dialog.setResultCallBack(result -> parserData(result));

        ll_hyzx_zpgl = (LinearLayout) findViewById(R.id.ll_hyzx_zpgl);
        ll_hyzx_zpgl.setOnClickListener(this);
        ll_hyzx_qzgl = (LinearLayout) findViewById(R.id.ll_hyzx_qzgl);
        ll_hyzx_qzgl.setOnClickListener(this);
        ll_hyzx_scgl = (LinearLayout) findViewById(R.id.ll_hyzx_scgl);
        ll_hyzx_scgl.setOnClickListener(this);
        ll_hyzx_ddgl = (LinearLayout) findViewById(R.id.ll_hyzx_ddgl);
        ll_hyzx_ddgl.setOnClickListener(this);
        ll_hyzx_zjgl = (LinearLayout) findViewById(R.id.ll_hyzx_zjgl);
        ll_hyzx_zjgl.setOnClickListener(this);
        ll_hyzx_gggl = (LinearLayout) findViewById(R.id.ll_hyzx_gggl);
        ll_hyzx_gggl.setOnClickListener(this);
        ll_hyzx_xxzx = (LinearLayout) findViewById(R.id.ll_hyzx_xxzx);
        ll_hyzx_xxzx.setOnClickListener(this);

        tv_header_cz = (TextView) findViewById(R.id.tv_header_cz);
        tv_header_cz.setOnClickListener(this);
        tv_header_mx = (TextView) findViewById(R.id.tv_header_mx);
        tv_header_mx.setOnClickListener(this);

        tv_hyzx_tc = (TextView) findViewById(R.id.tv_hyzx_tc);
        tv_hyzx_tc.setOnClickListener(this);

        tv_hyzx_kf = (TextView) findViewById(R.id.tv_hyzx_kf);
        tv_hyzx_kf.setOnClickListener(this);

        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mInflater = LayoutInflater.from(this);


        Intent intent1 = new Intent(getApplicationContext(), HomePageActivity.class);
        view1 = getView("HomePageActivity", intent1);
//        view1 = manager.startActivity("HomePageActivity",intent1).getDecorView();
        Intent intent2 = new Intent(getApplicationContext(), RecruitPageActivity.class);
        view2 = getView("RecruitPageActivity", intent2);
//        view2 = manager.startActivity("RecruitPageActivity",intent2).getDecorView();
        Intent intent3 = new Intent(getApplicationContext(), JobPageActivity.class);
        view3 = getView("JobPageActivity", intent3);
//        view3 = manager.startActivity("JobPageActivity",intent3).getDecorView();
        Intent intent4 = new Intent(getApplicationContext(), MallPageActivity.class);
        view4 = getView("MallPageActivity", intent4);
//        view4 = manager.startActivity("MallPageActivity",intent4).getDecorView();
//        Intent intent5 = new Intent(getApplicationContext(), WechatBusinessPageActivity.class);
//        view5 = getView("WechatBusinessPageActivity", intent5);
        Intent intent5 = new Intent(getApplicationContext(), AdvertisingManageActivity.class);
        view5 = getView("AdvertisingManageActivity", intent5);
//        view5 = manager.startActivity("WechatBusinessPageActivity",intent5).getDecorView();


        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        mViewList.add(view4);
        mViewList.add(view5);
        //添加页卡标题
        mTitleList.add("首页");
        mTitleList.add("招聘");
        mTitleList.add("求职");
        mTitleList.add("商城");
        mTitleList.add("广告");
        //添加tab选项卡，默认第一个选中
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(3)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(4)));


        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        //给ViewPager设置适配器
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                System.out.println("onPageSelected----");
                switch (mViewPager.getCurrentItem()) {
                    case 4:
                        Activity advertisingManageActivity = mactivityManager.getActivity("AdvertisingManageActivity");
                        if(advertisingManageActivity != null ){
                            ((AdvertisingManageActivity)advertisingManageActivity ).onPageSelect();
                        }
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);

    }

    @Override
    public void notifyAllActivity(int tag,String str) {

        if(tag == 0 ){
            if ("more_zp".equals(str)) {
                mViewPager.setCurrentItem(1);
            } else if ("more_qz".equals(str)) {
                mViewPager.setCurrentItem(2);
            } else if ("more_sc".equals(str)) {
                mViewPager.setCurrentItem(3);
            } else if ("more_wszq".equals(str)) {
                mViewPager.setCurrentItem(4);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_hyzx_zpgl:
                startActivity(new Intent(MainActivity.this, RecruitManageActivity.class));
                break;
            case R.id.ll_hyzx_qzgl:
                startActivity(new Intent(MainActivity.this, JobManageActivity.class));
                break;
            case R.id.ll_hyzx_scgl:
                showDialog("");
                getShopYajin();
//                startActivity(new Intent(MainActivity.this, MallManageActivity.class));
                break;
            case R.id.ll_hyzx_ddgl:
                startActivity(new Intent(MainActivity.this, OrderManageActivity.class));
                break;
            case R.id.ll_hyzx_zjgl:
                startActivity(new Intent(MainActivity.this, FundManageActivity.class));
                break;
            case R.id.ll_hyzx_gggl:
                startActivity(new Intent(MainActivity.this, AdvertisingManageActivity.class));
                break;
            case R.id.ll_hyzx_xxzx:
                startActivity(new Intent(MainActivity.this, MessageCenterActivity.class));
                break;
            case R.id.tv_header_cz:
                startActivity(new Intent(MainActivity.this, OnlineRechargeActivity.class));
                break;
            case R.id.tv_hyzx_ktviphy:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                startActivity(new Intent(MainActivity.this, VIPMemberActivity.class));
                break;
            case R.id.imageView_tx:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                startActivity(new Intent(MainActivity.this, PersonalActivity.class));
                break;
            case R.id.tv_header_mx:
                startActivity(new Intent(MainActivity.this, ConsumptionDetailedActivity.class));
                break;
            case R.id.tv_hyzx_tc:
                AlertDialog.Builder builder  = new AlertDialog.Builder(context);
                builder.setTitle("提示" ) ;
                builder.setMessage("确定退出当前账号？" ) ;
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        sharedPreferencesHelper.put("sessionID", "");
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
//                        sharedPreferencesHelper.put("denglushouji","");
//                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.tv_hyzx_kf:
                String tel = tv_hyzx_kf.getText().toString();
                System.out.println("tel---"+tel);
                Intent intent = new Intent();               //创建Intent对象
                intent.setAction(Intent.ACTION_CALL);      //设置动作为拨打电话
                intent.setData(Uri.parse("tel:" + tel));   // 设置要拨打的电话号码
                startActivity(intent);                     //启动Activity
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

    @Override
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }

    public void parserData(Map<Integer, AreaBean> currentMap) {
        shengID = "";
        shiID = "";
        quxianID = "";
        xiangzhenID = "";
        StringBuilder names = new StringBuilder();
        for (Integer in : currentMap.keySet()) {
            names.append(currentMap.get(in).getName());
            if(in == 0){
                System.out.println("yyyy----"+currentMap.get(in).getName());
                if("全国".equals(currentMap.get(in).getName())){
                    shengID = "";
                }else{
                    shengID = currentMap.get(in).getTid()+"";
                }
            }else if(in == 1){
                shiID = currentMap.get(in).getTid()+"";
            }else if(in == 2){
                quxianID = currentMap.get(in).getTid()+"";
            }else if(in == 3){
                xiangzhenID = currentMap.get(in).getTid()+"";
            }
        }
        sharedPreferencesHelper.put("shengID", shengID);
        sharedPreferencesHelper.put("shiID", shiID);
        sharedPreferencesHelper.put("quxianID", quxianID);
        sharedPreferencesHelper.put("xiangzhenID", xiangzhenID);

        ListenerManager.getInstance().sendBroadCast(1,shengID+"-"+shiID+"-"+quxianID+"-"+xiangzhenID);

        tv_city_name.setText(String.format("%s", names.toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取当前活动的Activity实例
//        Activity subActivity = mactivityManager.getCurrentActivity();
        Activity subActivity = mactivityManager.getActivity("MallPageActivity");

        //判断是否实现返回值接口
        if (subActivity instanceof OnTabActivityResultListener) {
            //获取返回值接口实例
            OnTabActivityResultListener listener = (OnTabActivityResultListener) subActivity;
            //转发请求到子Activity
            listener.onTabActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取商城押金
    public void getShopYajin(){

        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/getShopYajin.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("getShopYajin","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<ShopYJBean> result = gson.fromJson(content, new TypeToken<Entity<ShopYJBean>>() {}.getType());
                        if(result.getCode() == 0){
                            shopYJBean = result.getData();
                            handler.sendEmptyMessage(YJ_OK);
                        }else{
                            handler.sendEmptyMessage(YJ_ERROR);
                        }
                    }
                }
            }
        });
    }

    //获取商城押金
    public void getxiaofeijine(){

        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/getxiaofeijine.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("getxiaofeijine","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        XiaoFeiJinEBean result = gson.fromJson(content, new TypeToken<XiaoFeiJinEBean>() {}.getType());
                        if(result.getCode() == 0){
                            sharedPreferencesHelper.put("xfje", result.getJine());
                            handler.sendEmptyMessage(XFJE_OK);
                        }else{
                            handler.sendEmptyMessage(XFJE_ERROR);
                        }
                    }
                }
            }
        });
    }

    //获取余额
    public void getYuE(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getYuE.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String content = response.body().string();
                    Log.d("getYuE","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityYE result = gson.fromJson(content, new TypeToken<EntityYE>() {}.getType());
                        if(result.getCode() == 0){
                            zhye = result.getYue()+"";
                            handler.sendEmptyMessage(YE_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(YE_DATA_ERROR);
                        }

                    }
                }
            }
        });
    }

    private void showDialog(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.loading, null);
        TextView loadingText = (TextView)view.findViewById(R.id.tv_load_text);
        loadingText.setText(content);

        mLoadingDialog = new Dialog(this, R.style.MyDialogStyle);
//      对话框是否可以取消
        mLoadingDialog.setCancelable(true);
//        mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//
//            }
//        });
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.show();
    }

    /**
     * 取消对话框
     */
    private void cancelDialog() {
        if(mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
    }

    private void showYZDialog(){
        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setTitle("提示" ) ;
        builder.setMessage("您的商城押金不足，无法发布信息，请先充值！" ) ;
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                context.startActivity(new Intent(context, ShopYaJinActivity.class));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
