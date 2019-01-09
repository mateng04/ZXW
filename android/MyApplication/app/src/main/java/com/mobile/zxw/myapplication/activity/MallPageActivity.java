package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.MallPageAdapter;
import com.mobile.zxw.myapplication.adapter.WechatPageAdapter;
import com.mobile.zxw.myapplication.bean.AdvertisementBean;
import com.mobile.zxw.myapplication.bean.MallBean;
import com.mobile.zxw.myapplication.bean.MenuData;
import com.mobile.zxw.myapplication.bean.ShopYJBean;
import com.mobile.zxw.myapplication.bean.WechatBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityMall;
import com.mobile.zxw.myapplication.jsonEntity.EntityWechat;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.myinterface.OnTabActivityResultListener;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.ui.LoadListView;
import com.mobile.zxw.myapplication.until.ListenerManager;
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

public class MallPageActivity extends AppCompatActivity implements  LoadListView.IloadInterface, OnTabActivityResultListener, IListener,View.OnClickListener{

    private static final int REQUEST_CODE = 100;//请求码
    private Context context = null;

    PullRefreshLayout swipeRefreshLayout;
    static boolean isBlockedScrollView = false;

    TextView tv_mall_page_splxsz,tv_mall_page_header_splxsz;

    private boolean flag = true;
    EditText et_mall_page_sousuo,et_mall_page_header_sousuo;
    Button bt_mall_page_sousuo,bt_mall_page_fbsp;
    Button bt_mall_page_header_sousuo,bt_mall_page_header_fbsp;

    String searchContent = "";
    String shengId = "";
    String chengshiId = "";
    String quxianId = "";
    String xiangzhenId = "";
    String bigclassid = "";
    String smallclassid = "";

    Banner banner;
    List images = new ArrayList();
    List titles = new ArrayList();
    List<AdvertisementBean> adList = new ArrayList<AdvertisementBean>();
    List<AdvertisementBean> adTempList;

    private RecyclerView recycler_mall_sc;
    List<MallBean> list_sc = new ArrayList<MallBean>();
    List<MallBean> result_list;

    List<WechatBean> ws_list_sc = new ArrayList<WechatBean>();
    List<WechatBean> ws_result_list;


    private String shopcar;
    private String[] shopcar_arr;

    LoadListView lv_mall_sc;
    MallPageAdapter mallPageAdapter;
    WechatPageAdapter wechatPageAdapter;

    private LinearLayout ll_mall_gwc;
    private LinearLayout ll_mall_header_gwc;
    private TextView tv_mall_page_gwc;
    private TextView tv_mall_page_header_gwc;
    private RelativeLayout rl_mall_screen;
    private RelativeLayout rl_mall_header_screen;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    TextView tv_mall_wssc,tv_mall_wszq;
    TextView tv_mall_header_wssc,tv_mall_header_wszq;
    String type = "0";  // 0网上商城   1微商专区
    private int countPage = 0;
    private int currtPage = 0;

    private ArrayList<MenuData> selectList = new ArrayList<>();

    //加载对话框
    private Dialog mLoadingDialog;

    ShopYJBean shopYJBean;
    String zhye;

    static int AD_OK = 0; //广告
    static int SHOP_OK = 1;
    static int YJ_OK = 2;
    static int YJ_ERROR = 3;
    static int WS_SHOP_OK = 4;
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
                        list_sc.clear();
                        ws_list_sc.clear();
                    }
                    if(result_list != null && result_list.size() > 0 ){
                        list_sc.addAll(result_list);
                    }

                    mallPageAdapter.notifyDataSetChanged();
                    if(currtPage >= countPage){
                        //通知ListView加载完毕
                        lv_mall_sc.loadComplete();
                        lv_mall_sc.setAllDataSuccess();
                    }else{
                        lv_mall_sc.loadComplete();
                    }
                    cancelDialog();

                    swipeRefreshLayout.setRefreshing(false);
                    isBlockedScrollView = false;
                    break;
                case 2:
                    cancelDialog();
                    SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                            context, "setting");
                    String shopyajin = (String) sp_setting.getSharedPreference("shopyajin","");
                    System.out.println("shopyajin----"+shopyajin);
                    System.out.println("shopYJBean.getShopyajin()----"+shopYJBean.getShopyajin());
                    if(shopYJBean != null && !"".equals(shopYJBean.getShopyajin())){
                        if(Integer.valueOf(shopYJBean.getShopyajin()) < Integer.valueOf(shopyajin)){
                            showYZDialog();
                        }else{
                            startActivity(new Intent(context, ReleaseMallActivity.class));
                        }
                    }
                    break;
                case 3:
                    cancelDialog();
                    break;
                case 4:
                    if(currtPage == 1){
                        list_sc.clear();
                        ws_list_sc.clear();
                    }
                    if(ws_result_list != null && ws_result_list.size() > 0 ){
                        ws_list_sc.addAll(ws_result_list);
                    }
                    wechatPageAdapter.notifyDataSetChanged();
                    if(currtPage >= countPage){
                        //通知ListView加载完毕
                        lv_mall_sc.loadComplete();
                        lv_mall_sc.setAllDataSuccess();
                    }else{
                        lv_mall_sc.loadComplete();
                    }
                    cancelDialog();
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
        setContentView(R.layout.activity_mall_page);

        context = MallPageActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MallPageActivity.this, "config");

        shopcar = (String) sharedPreferencesHelper.getSharedPreference("shopcar", "");
        shopcar_arr = shopcar.split("-");
        System.out.println("shopcar_arr---"+shopcar_arr.toString());
        System.out.println("shopcar_arr---"+shopcar_arr.length);
//        chengshiId = (String) sharedPreferencesHelper.getSharedPreference("chengshiid", "");
//        quxianId = (String) sharedPreferencesHelper.getSharedPreference("quxianid", "");
//        xiangzhenId = (String) sharedPreferencesHelper.getSharedPreference("xiangzhenid", "");
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        loadBanner();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void invisibleOnScreen() {
        System.out.println("onResume------");
        shopcar = (String) sharedPreferencesHelper.getSharedPreference("shopcar", "");
        System.out.println("shopcar------"+shopcar);
        shopcar_arr = shopcar.split("-");
        for(int i=0;i<shopcar_arr.length;i++){
            System.out.println("tv_mall_page_gwc------"+shopcar_arr[i]);
        }
        System.out.println("tv_mall_page_gwc------"+shopcar_arr.length);
        System.out.println("tv_mall_page_header_gwc------"+tv_mall_page_header_gwc);
        if("".equals(shopcar)){
            tv_mall_page_gwc.setText("0");
            tv_mall_page_header_gwc.setText("0");
        }else{
            tv_mall_page_gwc.setText(shopcar_arr.length+"");
            tv_mall_page_header_gwc.setText(shopcar_arr.length+"");
        }

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

    public void initView(){

        tv_mall_page_gwc = (TextView) findViewById(R.id.tv_mall_page_gwc);
        tv_mall_page_gwc.setText(shopcar_arr.length+"");
//        tv_mall_page_splxsz = (TextView) findViewById(R.id.tv_mall_page_splxsz);

        et_mall_page_sousuo = (EditText) findViewById(R.id.et_mall_page_sousuo);
        bt_mall_page_sousuo = (Button) findViewById(R.id.bt_mall_page_sousuo);
        bt_mall_page_sousuo.setOnClickListener(this);
        bt_mall_page_fbsp = (Button) findViewById(R.id.bt_mall_page_fbsp);
        bt_mall_page_fbsp.setOnClickListener(this);

        rl_mall_screen = (RelativeLayout) findViewById(R.id.rl_mall_screen);
        rl_mall_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MallPageActivity.this, ShopScreenActivity.class);
                intent.putParcelableArrayListExtra("select",selectList);
                getParent().startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ll_mall_gwc = (LinearLayout) findViewById(R.id.ll_mall_gwc);
        ll_mall_gwc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin()){
                    startActivity(new Intent(MallPageActivity.this, ShopCarActivity.class));
                }else{
                    Utils.getLoginDialog(context);
                }
            }
        });

        LinearLayout invis = (LinearLayout) findViewById(R.id.ll_mall_page_invis);

        //LoadListView
        lv_mall_sc = (LoadListView)findViewById(R.id.lv_mall_sc);
        mallPageAdapter = new MallPageAdapter(MallPageActivity.this, list_sc);
        wechatPageAdapter = new WechatPageAdapter(MallPageActivity.this, ws_list_sc,sharedPreferencesHelper);
        lv_mall_sc.setInterface(this);
        lv_mall_sc.setHeaderView(invis);
        lv_mall_sc.setAdapter(mallPageAdapter);

        lv_mall_sc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (lv_mall_sc.getFirstVisiblePosition() == 0 &&
                            lv_mall_sc.getChildAt(0).getTop() >= lv_mall_sc.getListPaddingTop()) {
                        swipeRefreshLayout.setEnabled(true);
                        Log.d("TAG", "reach top!!!");
                    }else swipeRefreshLayout.setEnabled(false);
                }
                return false;
            }
        });


        //下面设置悬浮和头顶部分内容
        final View header = View.inflate(this, R.layout.view_mall_page_header, null);//头部内容,会隐藏的部分
        lv_mall_sc.addHeaderView(header);//添加头部
        final View header2 = View.inflate(this, R.layout.view_mall_page_header2, null);//头部内容,一直显示的部分
        lv_mall_sc.addHeaderView(header2);//添加头部

//        tv_mall_page_header_splxsz = (TextView) header2.findViewById(R.id.tv_mall_page_header_splxsz);

        tv_mall_page_header_gwc = (TextView) header2.findViewById(R.id.tv_mall_page_header_gwc);
        tv_mall_page_header_gwc.setText(shopcar_arr.length+"");

        tv_mall_header_wssc = (TextView) header2.findViewById(R.id.tv_mall_header_wssc);
        tv_mall_header_wssc.setOnClickListener(this);
        tv_mall_header_wszq = (TextView) header2.findViewById(R.id.tv_mall_header_wszq);
        tv_mall_header_wszq.setOnClickListener(this);

        et_mall_page_header_sousuo = (EditText)header2.findViewById(R.id.et_mall_page_header_sousuo);
        bt_mall_page_header_sousuo = (Button) header2.findViewById(R.id.bt_mall_page_header_sousuo);
        bt_mall_page_header_sousuo.setOnClickListener(this);
        bt_mall_page_header_fbsp = (Button) header2.findViewById(R.id.bt_mall_page_header_fbsp);
        bt_mall_page_header_fbsp.setOnClickListener(this);

        ll_mall_header_gwc = (LinearLayout)header2.findViewById(R.id.ll_mall_header_gwc);
        ll_mall_header_gwc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogin()){
                    startActivity(new Intent(MallPageActivity.this, ShopCarActivity.class));
                }else{
                    Utils.getLoginDialog(context);
                }
            }
        });

        rl_mall_header_screen = (RelativeLayout) header2.findViewById(R.id.rl_mall_header_screen);
        rl_mall_header_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MallPageActivity.this, ShopScreenActivity.class);
                intent.putParcelableArrayListExtra("select",selectList);
                getParent().startActivityForResult(intent,REQUEST_CODE);
            }
        });

        banner = (Banner) header.findViewById(R.id.mall_banner);

        TextWatcher1 textWatcher1 = new TextWatcher1();
        TextWatcher2 textWatcher2 = new TextWatcher2();

        et_mall_page_sousuo.addTextChangedListener(textWatcher1);
        et_mall_page_header_sousuo.addTextChangedListener(textWatcher2);


        if("".equals(shopcar)){
            tv_mall_page_gwc.setText("0");
            tv_mall_page_header_gwc.setText("0");
        }else{
            tv_mall_page_gwc.setText(shopcar_arr.length+"");
            tv_mall_page_header_gwc.setText(shopcar_arr.length+"");
        }

        tv_mall_wssc = (TextView) findViewById(R.id.tv_mall_wssc);
        tv_mall_wssc.setOnClickListener(this);
        tv_mall_wszq = (TextView) findViewById(R.id.tv_mall_wszq);
        tv_mall_wszq.setOnClickListener(this);

        swipeRefreshLayout = (PullRefreshLayout)findViewById(R.id.mall_swipeRefreshLayout);
        swipeRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // start refresh
                //先取消加载完成设置
                lv_mall_sc.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;

                if("0".equals(type)){
                    getAdvertisementData("5");
                }else if("1".equals(type)){
                    getAdvertisementData("6");
                }

                getMallData();

                isBlockedScrollView = true;
            }
        });
        swipeRefreshLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return isBlockedScrollView;
            }
        });
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
                et_mall_page_header_sousuo.setText(editable.toString());
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
                et_mall_page_sousuo.setText(editable.toString());
            } else {
                flag = true;
            }
        }
    }

//    @Override
//    public void onItemClick(int position, HomeSCBean model) {
//        System.out.println("position------"+position);
//        System.out.println("model------"+model.getShopPicUrl());
//
//        Intent intent = new Intent();
//        intent.setClass(MallPageActivity.this,ShopDetailsActivity.class);
//        startActivity(intent);
//    }

    public void initData(){

        getAdvertisementData("5");
        getMallData();
    }

    @Override
    public void onLoad() {
        if(currtPage >= countPage){
            //通知ListView加载完毕
            lv_mall_sc.loadComplete();
            lv_mall_sc.setAllDataSuccess();
        }else{
            if("0".equals(type)){
                getMallData();
            }else if("1".equals(type)){
                getWechatData();
            }
        }
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
                Intent intent = new Intent(MallPageActivity.this, AdvertisingContentActivity.class);
                intent.putExtra("adID",adList.get(position).getAdID());
                MallPageActivity.this.startActivity(intent);
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

    //获取广告
    public void getAdvertisementData(String pageID){

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
                    Log.d("MallPageActivity","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<AdvertisementBean>> result = gson.fromJson(content, new TypeToken<Entity<List<AdvertisementBean>>>() {}.getType());
                        adTempList = result.getData();
                        handler.sendEmptyMessage(AD_OK);
                    }
                }
            }
        });
    }

    //获取商城数据
    public void getMallData(){
        String SC = "";
        String DL = "";
        String XL = "";
        try {
            SC = URLEncoder.encode(searchContent,"GB2312");
            DL = URLEncoder.encode(bigclassid,"GB2312");
            XL = URLEncoder.encode(smallclassid,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder()
                .add("currtPage",currtPage+"")  .add("num","20").add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId)
                .addEncoded("bigclassid",DL).addEncoded("smallclassid",XL)
                .addEncoded("searchContent",SC).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getShopData.asp").post(requestBody).build();
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
                    Log.d("MallPageActivity","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityMall result = gson.fromJson(content, new TypeToken<EntityMall>() {}.getType());
                        countPage = result.getCountPage();
                        currtPage = result.getCurrtPage();
                        result_list = result.getData();

                        handler.sendEmptyMessage(SHOP_OK);
                    }else {
                        handler.sendEmptyMessage(404);
                    }
                }else {
                    handler.sendEmptyMessage(404);
                }
            }
        });
    }

    //获取商城数据
    public void getWechatData(){
        String SC = "";
        try {
            SC = URLEncoder.encode(searchContent,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder()
                .add("currtPage",currtPage+"")  .add("num","20").add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId)
                .addEncoded("searchContent",SC).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getWSShopData.asp").post(requestBody).build();
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
                    Log.d("MallPageActivity","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityWechat result = gson.fromJson(content, new TypeToken<EntityWechat>() {}.getType());
                        countPage = result.getCountPage();
                        currtPage = result.getCurrtPage();
                        ws_result_list = result.getData();

                        handler.sendEmptyMessage(WS_SHOP_OK);
                    }
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        System.out.println("result--------"+requestCode);
//        ArrayList<MenuData> result = data.getExtras().getParcelableArrayList("result");//得到新Activity 关闭后返回的数据
//        System.out.println("result--------"+result.size());
//        selectList.clear();
//        selectList.addAll(result);
//    }

    @Override
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            showDialog("数据加载中");

            //实现该处逻辑
            ArrayList<MenuData> result = data.getExtras().getParcelableArrayList("result");//得到新Activity 关闭后返回的数据
            selectList.clear();
            selectList.addAll(result);
            if("不限".equals(selectList.get(1).getName())){
                if("不限".equals(selectList.get(0).getName())){
//                    tv_mall_page_splxsz.setText("商品类型");
//                    tv_mall_page_header_splxsz.setText("商品类型");
                    bigclassid = "";
                    smallclassid = "";
                }else{
//                    tv_mall_page_splxsz.setText(selectList.get(0).getName());
//                    tv_mall_page_header_splxsz.setText(selectList.get(0).getName());
                    bigclassid = selectList.get(0).getName();
                    smallclassid = "";
                }
            }else {
//                tv_mall_page_splxsz.setText(selectList.get(1).getName());
//                tv_mall_page_header_splxsz.setText(selectList.get(1).getName());
                bigclassid = selectList.get(0).getName();
                smallclassid = selectList.get(1).getName();
            }
            currtPage = 0;
            countPage = 0;
            if("0".equals(type)){
                getMallData();
            }else if("1".equals(type)){
                getWechatData();
            }
        }
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

    @Override
    public void notifyAllActivity(int tag,String str) {
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
            if("0".equals(type)){
                getAdvertisementData("5");
            }else if("1".equals(type)){
                getAdvertisementData("6");
            }

            currtPage = 0;
            countPage = 0;
            lv_mall_sc.cancleAllDataSuccess();
            if("0".equals(type)){
                getMallData();
            }else if("1".equals(type)){
                getWechatData();
            }

        }
        System.out.println("jobpage--"+shengId+"-"+chengshiId+"-"+quxianId+"-"+xiangzhenId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_mall_page_sousuo :
                System.out.println("bt_wechat_page_sousuo---------");
                currtPage = 0;
                countPage = 0;
                searchContent = et_mall_page_sousuo.getText().toString().trim();
                if("0".equals(type)){
                    getMallData();
                }else if("1".equals(type)){
                    getWechatData();
                }
                break;
            case R.id.bt_mall_page_fbsp :
                if(isLogin()){
                    showDialog("");
                    getShopYajin();
                }else{
                    Utils.getLoginDialog(context);
                }
                break;
            case R.id.bt_mall_page_header_sousuo :
                System.out.println("bt_wechat_page_header_sousuo---------");
                currtPage = 0;
                countPage = 0;
                searchContent = et_mall_page_sousuo.getText().toString().trim();
                if("0".equals(type)){
                    getMallData();
                }else if("1".equals(type)){
                    getWechatData();
                }
                break;
            case R.id.bt_mall_page_header_fbsp :
                if(isLogin()){
                    showDialog("");
                    getShopYajin();
                }else{
                    Utils.getLoginDialog(context);
                }
                break;
            case R.id.tv_mall_wssc :
                showDialog("正在加载数据");

                et_mall_page_sousuo.setText("");
                et_mall_page_header_sousuo.setText("");
                searchContent = "";

                rl_mall_screen.setVisibility(View.VISIBLE);
                rl_mall_header_screen.setVisibility(View.VISIBLE);
                ll_mall_gwc.setVisibility(View.VISIBLE);
                ll_mall_header_gwc.setVisibility(View.VISIBLE);

                //先取消加载完成设置
                lv_mall_sc.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";
                tv_mall_wssc.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_wssc.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_wszq.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_wszq.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                tv_mall_header_wssc.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_header_wssc.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_header_wszq.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_header_wszq.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                getAdvertisementData("5");
                lv_mall_sc.setAdapter(mallPageAdapter);
                getMallData();
                break;
            case R.id.tv_mall_wszq :
                showDialog("正在加载数据");

                et_mall_page_sousuo.setText("");
                et_mall_page_header_sousuo.setText("");
                searchContent = "";

                rl_mall_screen.setVisibility(View.GONE);
                rl_mall_header_screen.setVisibility(View.GONE);
                ll_mall_gwc.setVisibility(View.GONE);
                ll_mall_header_gwc.setVisibility(View.GONE);

                //先取消加载完成设置
                lv_mall_sc.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";

                tv_mall_wssc.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_wssc.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                tv_mall_wszq.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_wszq.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_header_wssc.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_header_wssc.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                tv_mall_header_wszq.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_header_wszq.setBackgroundColor(context.getResources().getColor(R.color.red));

                getAdvertisementData("6");
                lv_mall_sc.setAdapter(wechatPageAdapter);
                getWechatData();
                break;
            case R.id.tv_mall_header_wssc :
                showDialog("正在加载数据");

                et_mall_page_sousuo.setText("");
                et_mall_page_header_sousuo.setText("");
                searchContent = "";

                rl_mall_screen.setVisibility(View.VISIBLE);
                rl_mall_header_screen.setVisibility(View.VISIBLE);
                ll_mall_gwc.setVisibility(View.VISIBLE);
                ll_mall_header_gwc.setVisibility(View.VISIBLE);

                //先取消加载完成设置
                lv_mall_sc.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";

                tv_mall_wssc.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_wssc.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_wszq.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_wszq.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                tv_mall_header_wssc.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_header_wssc.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_header_wszq.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_header_wszq.setBackgroundColor(context.getResources().getColor(R.color.transparent));

                getAdvertisementData("5");
                lv_mall_sc.setAdapter(mallPageAdapter);
                getMallData();
                break;
            case R.id.tv_mall_header_wszq :
                showDialog("正在加载数据");

                et_mall_page_sousuo.setText("");
                et_mall_page_header_sousuo.setText("");
                searchContent = "";

                rl_mall_screen.setVisibility(View.GONE);
                rl_mall_header_screen.setVisibility(View.GONE);
                ll_mall_gwc.setVisibility(View.GONE);
                ll_mall_header_gwc.setVisibility(View.GONE);

                //先取消加载完成设置
                lv_mall_sc.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";
                tv_mall_wssc.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_wssc.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                tv_mall_wszq.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_wszq.setBackgroundColor(context.getResources().getColor(R.color.red));
                tv_mall_header_wssc.setTextColor(context.getResources().getColor(R.color.black));
                tv_mall_header_wssc.setBackgroundColor(context.getResources().getColor(R.color.transparent));
                tv_mall_header_wszq.setTextColor(context.getResources().getColor(R.color.white));
                tv_mall_header_wszq.setBackgroundColor(context.getResources().getColor(R.color.red));

                getAdvertisementData("6");
                lv_mall_sc.setAdapter(wechatPageAdapter);
                getWechatData();
                break;
            default:
                break;
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
