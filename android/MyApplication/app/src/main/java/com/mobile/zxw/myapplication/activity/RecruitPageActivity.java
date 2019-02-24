package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.GridViewAdapter;
import com.mobile.zxw.myapplication.adapter.RecruitDataAdapter;
import com.mobile.zxw.myapplication.bean.AdvertisementBean;
import com.mobile.zxw.myapplication.bean.BigZhiweiBean;
import com.mobile.zxw.myapplication.bean.GridInfo;
import com.mobile.zxw.myapplication.bean.RecruitBean;
import com.mobile.zxw.myapplication.bean.SmallZhiweiBean;
import com.mobile.zxw.myapplication.bean.YueXiClassBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityRecruit;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.ui.LoadListView;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.ScreenManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecruitPageActivity extends AppCompatActivity implements LoadListView.IloadInterface, View.OnClickListener, IListener {
    private Context mContext = null;

    static String cityName = "";
    PullRefreshLayout swipeRefreshLayout;
    PullRefreshLayout.OnRefreshListener onRefreshListener;
    static boolean isBlockedScrollView = false;

    private boolean flag = true;
    EditText et_recruit_page_sousuo,et_recruit_page_header_sousuo;
    Button bt_recruit_page_sousuo,bt_recruit_page_fbzp;
    Button bt_recruit_page_header_sousuo,bt_recruit_page_header_fbzp;
    Button bt_recruit_page_queding,bt_recruit_page_chongzhi;

    Banner banner;
    List images = new ArrayList();
    List titles = new ArrayList();
    List<AdvertisementBean> adList = new ArrayList<AdvertisementBean>();
    List<AdvertisementBean> adTempList;
    List<RecruitBean> lishi_list;
    List<BigZhiweiBean> dl_List = new ArrayList<>();
    List<BigZhiweiBean> dl_lishi_List;
    List<SmallZhiweiBean> xl_List = new ArrayList<>();
    List<SmallZhiweiBean> xl_lishi_List;
    List<YueXiClassBean> yx_List = new ArrayList<>();
    List<YueXiClassBean> yx_lishi_List;

    String DL_ID = "";
    String XL_ID = "";
    String YX_ID = "";

    private DrawerLayout dlShow;

    String bigclassid = "";
    String smallclassid = "";
    String yuexin = "";
    String searchContent = "";
    String type = "0";  // 0代表全职   1代表兼职
    TextView tv_recruit_qzzp,tv_recruit_jzzp;
    TextView tv_recruit_header_qzzp,tv_recruit_header_jzzp;
    private List<RecruitBean> list_qzzp = new ArrayList<RecruitBean>();
    private int countPage = 0;
    private int currtPage = 0;

    RecruitDataAdapter adapter_qzzp;

    private Button button_recruit_screen,button_recruit_header_screen;


    private GridView  gv_recruit_dalei, gv_recruit_xiaolei,gv_recruit_yuexin;
    TextView tv_recruit_yuexin;

    private List<GridInfo> daLeiList = new ArrayList<GridInfo>();
    private List<GridInfo> xiaoLeiList = new ArrayList<GridInfo>();
    private List<GridInfo> yueXinList = new ArrayList<GridInfo>();

    GridViewAdapter daleiAdapter;
    GridViewAdapter xiaoleiAdapter;
    GridViewAdapter yuexinAdapter;
    LoadListView lv_recruit_page;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String shengId = "";
    String chengshiId = "";
    String quxianId = "";
    String xiangzhenId = "";

    //加载对话框
    private Dialog mLoadingDialog;

    static int AD_OK = 0; //广告
    static int QZ_OK = 1; //全职
    static int JZ_OK = 2; //兼职
    static int DL_OK = 3; //大类获取成功
    static int DL_ERROR = 4; //大类获取失败
    static int XL_OK = 5; //小类获取成功
    static int XL_ERROR = 6; //小类获取失败
    static int YX_OK = 7; //小类获取成功
    static int YX_ERROR = 8; //小类获取失败
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    adList.clear();
                    if(adTempList != null && adTempList.size() > 0 ){
                        adList.addAll(adTempList);
                    }
                    if(adList.size() > 0){
                        banner.setVisibility(View.VISIBLE);
                        updateBanner();
                    }else{
                        banner.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    if(currtPage == 1){
                        list_qzzp.clear();
                    }
                    if(lishi_list != null && lishi_list.size() > 0 ){
                        list_qzzp.addAll(lishi_list);
                    }

                    adapter_qzzp.notifyDataSetChanged();
                    lv_recruit_page.loadComplete();
                    cancelDialog();

                    // 20 为每页返回总数据最多20条
                    if(lishi_list.size() < 20){
                        lv_recruit_page.setAllDataSuccess();
                    }else{
                        lv_recruit_page.cancleAllDataSuccess();
                    }

                    swipeRefreshLayout.setRefreshing(false);
                    isBlockedScrollView = false;
                    break;
                case 2:
                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 3:
                    dl_List.addAll(dl_lishi_List);
                    setDLScreenData();
                    setXLScreenData();
                    break;
                case 4:
//                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 5:
                    xl_List.clear();
                    xl_List.addAll(xl_lishi_List);
                    setXLScreenData();
                    break;
                case 6:
//                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 7:
                    yx_List.addAll(yx_lishi_List);
                    setPriceScreenData();
                    break;
                case 8:
//                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 404:
                    swipeRefreshLayout.setRefreshing(false);
                    isBlockedScrollView = false;
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_page);
        ScreenManager.getScreenManager().pushActivity(this);
        mContext = RecruitPageActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                RecruitPageActivity.this, "config");

//        chengshiId = (String) sharedPreferencesHelper.getSharedPreference("chengshiid", "");
//        quxianId = (String) sharedPreferencesHelper.getSharedPreference("quxianid", "");
//        xiangzhenId = (String) sharedPreferencesHelper.getSharedPreference("xiangzhenid", "");
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        loadBanner();
        initData();
    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        System.out.println("rp___dispatchKeyEvent");

        //拦截返回键
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            //判断触摸UP事件才会进行返回事件处理
            if (event.getAction() == KeyEvent.ACTION_UP) {
                dlShow.closeDrawer(Gravity.RIGHT);
            }
            //只要是返回事件，直接返回true，表示消费掉
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void loadBanner(){
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setBannerTitles(titles);
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(RecruitPageActivity.this, AdvertisingContentActivity.class);
                intent.putExtra("adID",adList.get(position).getAdID());
                RecruitPageActivity.this.startActivity(intent);
            }
        });
    }

    public void updateBanner(){
        images.clear();
        titles.clear();
        for(AdvertisementBean adBean: adList){
            images.add("http://www.zhengxinw.com/upload/image/"+adBean.getAdPicUrl());
            titles.add(adBean.getAdName());
        }
        banner.setImages(images);
        banner.setBannerTitles(titles);
//        banner.update(images,titles);
        banner.start();
    }

    public void initView() {

        tv_recruit_yuexin = (TextView) findViewById(R.id.tv_recruit_yuexin);

        et_recruit_page_sousuo = (EditText) findViewById(R.id.et_recruit_page_sousuo);
        bt_recruit_page_sousuo = (Button) findViewById(R.id.bt_recruit_page_sousuo);
        bt_recruit_page_sousuo.setOnClickListener(this);
        bt_recruit_page_fbzp = (Button) findViewById(R.id.bt_recruit_page_fbzp);
        bt_recruit_page_fbzp.setOnClickListener(this);


        dlShow = (DrawerLayout) findViewById(R.id.dlShow_recruit);

        bt_recruit_page_queding = (Button)findViewById(R.id.bt_recruit_page_queding);
        bt_recruit_page_queding.setOnClickListener(this);
        bt_recruit_page_chongzhi = (Button)findViewById(R.id.bt_recruit_page_chongzhi);
        bt_recruit_page_chongzhi.setOnClickListener(this);

        gv_recruit_dalei = (GridView) findViewById(R.id.gv_recruit_dalei);
        daleiAdapter = new GridViewAdapter(this,  daLeiList);
        gv_recruit_dalei.setAdapter(daleiAdapter);
        gv_recruit_dalei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i=0;i<daLeiList.size();i++){
                    if (position == i) {//当前选中的Item改变背景颜色
                        daLeiList.get(i).setSelect(true);
                    } else {
                        daLeiList.get(i).setSelect(false);
                    }
                }
                if(position == 0){
//                    SmallZhiweiClass("0");
                    DL_ID = "";
                    bigclassid = "";
                    if(xl_lishi_List == null){
                        xl_lishi_List = new ArrayList<>();
                    }else{
                        xl_lishi_List.clear();
                    }

                    handler.sendEmptyMessage(5);
                }else{
                    DL_ID = dl_List.get(position-1).getBigClassID();
                    bigclassid = dl_List.get(position-1).getBigClassName();
                    SmallZhiweiClass( dl_List.get(position-1).getBigClassID());
                }
                XL_ID = "";
                smallclassid = "";
                daleiAdapter.notifyDataSetChanged();

            }
        });

        gv_recruit_xiaolei = (GridView) findViewById(R.id.gv_recruit_xiaolei);
        xiaoleiAdapter = new GridViewAdapter(this,  xiaoLeiList);
        gv_recruit_xiaolei.setAdapter(xiaoleiAdapter);
        gv_recruit_xiaolei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i=0;i<xiaoLeiList.size();i++){
                    if (position == i) {//当前选中的Item改变背景颜色
                        xiaoLeiList.get(i).setSelect(true);
                    } else {
                        xiaoLeiList.get(i).setSelect(false);
                    }
                }
                if(position == 0){
//                    SmallZhiweiClass("0");
                    XL_ID = "";
                    smallclassid = "";
                }else{
                    XL_ID = xl_List.get(position-1).getSmallClassID();
                    smallclassid = xl_List.get(position-1).getSmallClassName();
                }
                xiaoleiAdapter.notifyDataSetChanged();
            }
        });

        gv_recruit_yuexin = (GridView) findViewById(R.id.gv_recruit_yuexin);
        yuexinAdapter = new GridViewAdapter(this,  yueXinList);
        gv_recruit_yuexin.setAdapter(yuexinAdapter);
        gv_recruit_yuexin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i=0;i<yueXinList.size();i++){
                    if (position == i) {//当前选中的Item改变背景颜色
                        yueXinList.get(i).setSelect(true);
                    } else {
                        yueXinList.get(i).setSelect(false);
                    }
                }

                if(position == 0){
                    YX_ID = "";
                    yuexin = "";
                }else{
                    YX_ID = yx_List.get(position-1).getYuexinID();
                    yuexin = yx_List.get(position-1).getYuexinName();
                }
                yuexinAdapter.notifyDataSetChanged();
            }
        });


        button_recruit_screen = (Button) findViewById(R.id.button_recruit_screen);
        button_recruit_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlShow.openDrawer(Gravity.RIGHT);
            }
        });

        LinearLayout invis = (LinearLayout) findViewById(R.id.invis);

        //LoadListView
        lv_recruit_page = (LoadListView)findViewById(R.id.lv_recruit_page);
        adapter_qzzp = new RecruitDataAdapter(RecruitPageActivity.this, list_qzzp);
        lv_recruit_page.setInterface(this);
        lv_recruit_page.setHeaderView(invis);
        lv_recruit_page.setAdapter(adapter_qzzp);
        lv_recruit_page.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    System.out.println("position------"+position);
                    System.out.println("list_qzzp.size()------"+list_qzzp.size());
                    //position 是从2开始，暂时没找到原因，所以都要减2
                    if(position < 2){
                        return;
                    }
                    if((position-2) >= list_qzzp.size()){
                        return;
                    }
                    String hrQZID = list_qzzp.get(position-2).getRecruitID();
                    Intent intent = new Intent(mContext, RecruitDetailsActivity.class);
                    intent.putExtra("ID",hrQZID);
                    if("0".equals(type)){
                        intent.putExtra("leixing","全职招聘");
                    }else{
                        intent.putExtra("leixing","兼职招聘");
                    }
//                    intent.putExtra("leixing",type);
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
//                startActivity(new Intent(RecruitPageActivity.this, RecruitDetailsActivity.class));
            }
        });

        lv_recruit_page.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (lv_recruit_page.getFirstVisiblePosition() == 0 &&
                            lv_recruit_page.getChildAt(0).getTop() >= lv_recruit_page.getListPaddingTop()) {
                        swipeRefreshLayout.setEnabled(true);
                        Log.d("TAG", "reach top!!!");
                    }else swipeRefreshLayout.setEnabled(false);
                }
                return false;
            }
        });

        //下面设置悬浮和头顶部分内容
        final View header = View.inflate(this, R.layout.view_recruit_page_header, null);//头部内容,会隐藏的部分
        lv_recruit_page.addHeaderView(header);//添加头部
        final View header2 = View.inflate(this, R.layout.view_recruit_page_header2, null);//头部内容,一直显示的部分
        lv_recruit_page.addHeaderView(header2);//添加头部

        tv_recruit_header_qzzp = (TextView) header2.findViewById(R.id.tv_recruit_header_qzzp);
        tv_recruit_header_qzzp.setOnClickListener(this);
        tv_recruit_header_jzzp = (TextView) header2.findViewById(R.id.tv_recruit_header_jzzp);
        tv_recruit_header_jzzp.setOnClickListener(this);

        et_recruit_page_header_sousuo = (EditText) header2.findViewById(R.id.et_recruit_page_header_sousuo);
        bt_recruit_page_header_sousuo = (Button) header2.findViewById(R.id.bt_recruit_page_header_sousuo);
        bt_recruit_page_header_sousuo.setOnClickListener(this);
        bt_recruit_page_header_fbzp = (Button) header2.findViewById(R.id.bt_recruit_page_header_fbzp);
        bt_recruit_page_header_fbzp.setOnClickListener(this);

        banner = (Banner) header.findViewById(R.id.recruit_banner);

        button_recruit_header_screen = (Button) findViewById(R.id.button_recruit_header_screen);
        button_recruit_header_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlShow.openDrawer(Gravity.RIGHT);
            }
        });

        tv_recruit_qzzp = (TextView) findViewById(R.id.tv_recruit_qzzp);
        tv_recruit_qzzp.setOnClickListener(this);
        tv_recruit_jzzp = (TextView) findViewById(R.id.tv_recruit_jzzp);
        tv_recruit_jzzp.setOnClickListener(this);

        swipeRefreshLayout = (PullRefreshLayout)findViewById(R.id.recruit_swipeRefreshLayout);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);

        onRefreshListener = new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                //先取消加载完成设置
                lv_recruit_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
                getAdvertisementData();
                getRecruitData(type);

                isBlockedScrollView = true;
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return isBlockedScrollView;
            }
        });

        TextWatcher1 textWatcher1 = new TextWatcher1();
        TextWatcher2 textWatcher2 = new TextWatcher2();
        et_recruit_page_sousuo.addTextChangedListener(textWatcher1);
        et_recruit_page_header_sousuo.addTextChangedListener(textWatcher2);

    }

    @Override
    public void onLoad() {
        if(currtPage >= countPage){
            //通知ListView加载完毕
            lv_recruit_page.loadComplete();
            lv_recruit_page.setAllDataSuccess();
        }else{
            getRecruitData(type);
        }
    }


    public void initData() {
        list_qzzp.clear();
        getAdvertisementData();
        getRecruitData(type);
        BigZhiweiClass();
        YueXiClass();

    }

    public void onPageRefreshData(String city,int onPageSelected){
        if(!city.equals(cityName) && onPageSelected == 1){
            cityName = city;
            if(onRefreshListener != null){
                System.out.println("onRefreshListener--------------------onRefreshListener----");
                swipeRefreshLayout.setRefreshing(true);
                onRefreshListener.onRefresh();
            }
        }
    }

    @Override
    public void notifyAllActivity(int tag,String str,String city) {
        if(tag == 1){
            shengId = "";
            chengshiId = "";
            quxianId = "";
            xiangzhenId = "";
            String[] ids = str.split("-");
            if(ids.length == 1){
                shengId = ids[0];
            }else if(ids.length == 2){
                shengId = ids[0];
                chengshiId = ids[1];
            }else if(ids.length == 3){
                shengId = ids[0];
                chengshiId = ids[1];
                quxianId = ids[2];
            }else if(ids.length == 4){
                shengId = ids[0];
                chengshiId = ids[1];
                quxianId = ids[2];
                xiangzhenId = ids[3];
            }
            System.out.println("notifyAllActivity--------------------recruitpage----");
//            list_qzzp.clear();
//            lv_recruit_page.cancleAllDataSuccess();
//            countPage = 0;
//            currtPage = 0;

            if(!city.equals(cityName) && MainActivity.onPageSelected == 1){
                cityName = city;
                if(onRefreshListener != null){
                    System.out.println("onRefreshListener--------------------onRefreshListener----");
                    swipeRefreshLayout.setRefreshing(true);
                    onRefreshListener.onRefresh();
                }
            }

//            getAdvertisementData();
//            getRecruitData(type);
        }
        System.out.println("recruitpage--"+shengId+"-"+chengshiId+"-"+quxianId+"-"+xiangzhenId);
    }

    class TextWatcher1 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (flag) {
                flag = false;
                et_recruit_page_header_sousuo.setText(editable.toString());
            } else {
                flag = true;
            }
        }
    }

    class TextWatcher2 implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (flag) {
                flag = false;
                et_recruit_page_sousuo.setText(editable.toString());
            } else {
                flag = true;
            }
        }
    }

    private void setDLScreenData(){
        GridInfo gridInfo = new GridInfo();
        gridInfo.setTitle("不限");
        gridInfo.setSelect(true);
        daLeiList.add(gridInfo);

        for(int i=0;i<dl_List.size();i++){
            GridInfo gridInfo1 = new GridInfo();
            gridInfo1.setTitle(dl_List.get(i).getBigClassName());
            daLeiList.add(gridInfo1);
        }
        daleiAdapter.notifyDataSetChanged();
    }

    private void setXLScreenData() {
        xiaoLeiList.clear();

        GridInfo xgridInfo = new GridInfo();
        xgridInfo.setTitle("不限");
        xgridInfo.setSelect(true);
        xiaoLeiList.add(xgridInfo);

        for(int i=0;i<xl_List.size();i++){
            GridInfo gridInfo1 = new GridInfo();
            gridInfo1.setTitle(xl_List.get(i).getSmallClassName());
            xiaoLeiList.add(gridInfo1);
        }
        xiaoleiAdapter.notifyDataSetChanged();
    }

    private void setPriceScreenData() {

        GridInfo ygridInfo0 = new GridInfo();
        ygridInfo0.setSelect(true);
        ygridInfo0.setTitle("不限");
        yueXinList.add(0,ygridInfo0);

        for(int i=0;i<yx_List.size();i++){
            GridInfo ygridInfo = new GridInfo();
            ygridInfo.setTitle(yx_List.get(i).getYuexinName());
            yueXinList.add(ygridInfo);
        }
        yuexinAdapter.notifyDataSetChanged();
    }

    //获取广告
    public void getAdvertisementData(){
        String pageID = "";
        if("0".equals(type)){
            pageID = "1";
        }else{
            pageID = "2";
        }

        RequestBody requestBody = new FormBody.Builder().add("pageID",pageID).add("chengshiid",chengshiId)
               .build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getAdvertisementData.asp").post(requestBody).build();
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
                    Log.d("RecruitPageActivity","getAdvertisementData=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<AdvertisementBean>> result = gson.fromJson(content, new TypeToken<Entity<List<AdvertisementBean>>>() {}.getType());
                        adTempList = result.getData();
                        handler.sendEmptyMessage(AD_OK);
                    }else {
                        handler.sendEmptyMessage(404);
                    }
                }else {
                    handler.sendEmptyMessage(404);
                }
            }
        });
    }

    //获取职位大类
    public void BigZhiweiClass(){

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/BigZhiweiClass.asp").post(requestBody).build();
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
                    Log.d("BigZhiweiClass","BigZhiweiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<BigZhiweiBean>> result = gson.fromJson(content, new TypeToken<Entity<List<BigZhiweiBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            dl_lishi_List = result.getData();
                            handler.sendEmptyMessage(DL_OK);
                        }else{
                            handler.sendEmptyMessage(DL_ERROR);
                        }

                    }
                }
            }
        });
    }

    //获取职位小类
    public void SmallZhiweiClass(String BigClassID){

        RequestBody requestBody = new FormBody.Builder().add("BigClassID",BigClassID).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/SmallZhiweiClass.asp").post(requestBody).build();
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
                    Log.d("SmallZhiweiClass","BigZhiweiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<SmallZhiweiBean>> result = gson.fromJson(content, new TypeToken<Entity<List<SmallZhiweiBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            xl_lishi_List = result.getData();
                            handler.sendEmptyMessage(XL_OK);
                        }else{
                            handler.sendEmptyMessage(XL_ERROR);
                        }

                    }
                }
            }
        });
    }

    //获取月薪类型
    public void YueXiClass(){

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/yuexi.asp").post(requestBody).build();
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
                    Log.d("yuexiClass","yuexiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<YueXiClassBean>> result = gson.fromJson(content, new TypeToken<Entity<List<YueXiClassBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            yx_lishi_List = result.getData();
                            handler.sendEmptyMessage(YX_OK);
                        }else{
                            handler.sendEmptyMessage(YX_ERROR);
                        }

                    }
                }
            }
        });
    }

    //获取招聘数据
    public void getRecruitData(String workType){

        String SC = "";
        String dl = "";
        String xl = "";
        String yx = "";
        try {
            SC = URLEncoder.encode(searchContent,"GB2312");
            dl = URLEncoder.encode(bigclassid,"GB2312");
            xl = URLEncoder.encode(smallclassid,"GB2312");
            yx = URLEncoder.encode(yuexin,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder().add("currtPage",currtPage+"").add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId)
                .add("workType",workType).addEncoded("bigclassid",dl)
                .addEncoded("smallclassid",xl).addEncoded("yuexin",yx)
                .addEncoded("searchContent",SC).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getRecruitData.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);

        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
                handler.sendEmptyMessage(404);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("RecruitPageActivity","getRecruitData=="+content);
                    if(response.code() == 200){

                        Gson gson = new Gson();
                        EntityRecruit result = gson.fromJson(content, new TypeToken<EntityRecruit>() {}.getType());
                        countPage = result.getCountPage();
                        currtPage = result.getCurrtPage();
                        lishi_list = result.getData();

                        handler.sendEmptyMessage(QZ_OK);
                    }else {
                        handler.sendEmptyMessage(404);
                    }
                }else {
                    handler.sendEmptyMessage(404);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_recruit_qzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_recruit_yuexin.setVisibility(View.VISIBLE);
                gv_recruit_yuexin.setVisibility(View.VISIBLE);
                //先取消加载完成设置
                lv_recruit_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";
                tv_recruit_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_recruit_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                tv_recruit_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_recruit_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                getRecruitData("0");
                break;
            case R.id.tv_recruit_jzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_recruit_yuexin.setVisibility(View.GONE);
                gv_recruit_yuexin.setVisibility(View.GONE);
                //先取消加载完成设置
                System.out.println("----------------------1");
                lv_recruit_page.cancleAllDataSuccess();
                System.out.println("----------------------2");
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";

                tv_recruit_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_recruit_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_recruit_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_recruit_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));

                getRecruitData("1");
                break;
            case R.id.tv_recruit_header_qzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_recruit_yuexin.setVisibility(View.VISIBLE);
                gv_recruit_yuexin.setVisibility(View.VISIBLE);
                //先取消加载完成设置
                lv_recruit_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";

                tv_recruit_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_recruit_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                tv_recruit_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_recruit_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                getRecruitData("0");
                break;
            case R.id.tv_recruit_header_jzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_recruit_yuexin.setVisibility(View.GONE);
                gv_recruit_yuexin.setVisibility(View.GONE);
                //先取消加载完成设置
                System.out.println("----------------------3");
                lv_recruit_page.cancleAllDataSuccess();
                System.out.println("----------------------4");
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";
                tv_recruit_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_recruit_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));

                tv_recruit_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_recruit_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_recruit_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_recruit_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                getRecruitData("1");
                break;
            case R.id.bt_recruit_page_queding :
                System.out.println("bt_recruit_page_queding--------"+DL_ID+"---"+XL_ID+"----"+YX_ID);
                dlShow.closeDrawer(Gravity.RIGHT);
                currtPage = 0;
                searchContent = et_recruit_page_sousuo.getText().toString().trim();
                getRecruitData(type);
                break;
            case R.id.bt_recruit_page_chongzhi :
                System.out.println("bt_recruit_page_queding--------");
                setChongZhi();
                break;
            case R.id.bt_recruit_page_sousuo :
                currtPage = 0;
                searchContent = et_recruit_page_sousuo.getText().toString().trim();
                getRecruitData(type);
                break;
            case R.id.bt_recruit_page_fbzp :
                if(isLogin()){
                    Intent intent = new Intent(mContext, ReleaseRecruitActivity.class);
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
                break;
            case R.id.bt_recruit_page_header_sousuo :
                searchContent = et_recruit_page_header_sousuo.getText().toString().trim();
                currtPage = 0;
                getRecruitData(type);
                break;
            case R.id.bt_recruit_page_header_fbzp :
                if(isLogin()){
                    Intent intent = new Intent(mContext, ReleaseRecruitActivity.class);
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
                break;
            default:
                break;
        }
    }

    private void setChongZhi(){
        for(int i=0;i<daLeiList.size();i++){
            if (i == 0) {//当前选中的Item改变背景颜色
                daLeiList.get(i).setSelect(true);
            } else {
                daLeiList.get(i).setSelect(false);
            }
        }
        DL_ID = "";
        bigclassid = "";
        if(xl_lishi_List == null){
            xl_lishi_List = new ArrayList<>();
        }else{
            xl_lishi_List.clear();
        }
        handler.sendEmptyMessage(5);
        XL_ID = "";
        smallclassid = "";
        daleiAdapter.notifyDataSetChanged();

        for(int i=0;i<yueXinList.size();i++){
            if (i == 0) {//当前选中的Item改变背景颜色
                yueXinList.get(i).setSelect(true);
            } else {
                yueXinList.get(i).setSelect(false);
            }
        }
        YX_ID = "";
        yuexin = "";
        yuexinAdapter.notifyDataSetChanged();

    }

    /**
     * 展示对话框
     * @param content 对话框显示内容
     */
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

    public boolean  isLogin(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        if(!"".equals(sessionID) && !"".equals(userid)){
            return true;
        }
        return false;
    }
}


