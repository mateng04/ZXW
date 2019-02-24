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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.WechatPageAdapter;
import com.mobile.zxw.myapplication.bean.AdvertisementBean;
import com.mobile.zxw.myapplication.bean.MenuData;
import com.mobile.zxw.myapplication.bean.ShopYJBean;
import com.mobile.zxw.myapplication.bean.WechatBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityWechat;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.myinterface.OnTabActivityResultListener;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.ui.LoadListView;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
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

public class WechatBusinessPageActivity extends AppCompatActivity implements LoadListView.IloadInterface, OnTabActivityResultListener,View.OnClickListener, IListener {

    private static final int REQUEST_CODE = 100;//请求码
    private Context mContext = null;

    private boolean flag = true;
    EditText et_wechat_page_sousuo,et_wechat_page_header_sousuo;
    Button bt_wechat_page_sousuo,bt_wechat_page_fbsp;
    Button bt_wechat_page_header_sousuo,bt_wechat_page_header_fbsp;

    String searchContent = "";

    Banner banner;
    List images = new ArrayList();
    List titles = new ArrayList();
    List<AdvertisementBean> adList = new ArrayList<AdvertisementBean>();
    List<AdvertisementBean> adTempList;

    List<WechatBean> list_sc = new ArrayList<WechatBean>();
    List<WechatBean> result_list;

    LoadListView lv_wechat_sc;
    WechatPageAdapter wechatPageAdapter;


    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String shengId = "";
    String chengshiId = "";
    String quxianId = "";
    String xiangzhenId = "";

    private int countPage = 0;
    private int currtPage = 0;

    private ArrayList<MenuData> selectList = new ArrayList<>();

    //加载对话框
    private Dialog mLoadingDialog;

    ShopYJBean shopYJBean;
    String zhye;

    static int AD_OK = 0; //广告
    static int SHOP_OK = 1; //全职
    static int YJ_OK = 2;
    static int YJ_ERROR = 3;
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
                    }
                    if(result_list != null && result_list.size() > 0 ){
                        list_sc.addAll(result_list);
                    }
                    wechatPageAdapter.notifyDataSetChanged();
                    lv_wechat_sc.loadComplete();
                    cancelDialog();
                    break;
                case 2:
                    cancelDialog();
                    SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                            mContext, "setting");
                    String shopyajin = (String) sp_setting.getSharedPreference("shopyajin","");
                    System.out.println("shopyajin----"+shopyajin);
                    System.out.println("shopYJBean.getShopyajin()----"+shopYJBean.getShopyajin());
                    if(shopYJBean != null && !"".equals(shopYJBean.getShopyajin())){
                        if(Integer.valueOf(shopYJBean.getShopyajin()) < Integer.valueOf(shopyajin)){
                            showYZDialog();
                        }else{
                            startActivity(new Intent(mContext, ReleaseMallActivity.class));
                        }
                    }
                    break;
                case 3:
                    cancelDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechatbus_page);

        mContext = WechatBusinessPageActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                WechatBusinessPageActivity.this, "config");

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

    public void initView(){

        et_wechat_page_sousuo = (EditText) findViewById(R.id.et_wechat_page_sousuo);
        bt_wechat_page_sousuo = (Button) findViewById(R.id.bt_wechat_page_sousuo);
        bt_wechat_page_sousuo.setOnClickListener(this);
        bt_wechat_page_fbsp = (Button) findViewById(R.id.bt_wechat_page_fbsp);
        bt_wechat_page_fbsp.setOnClickListener(this);

        LinearLayout invis = (LinearLayout) findViewById(R.id.ll_wechat_page_invis);

        //LoadListView
        lv_wechat_sc = (LoadListView)findViewById(R.id.lv_wechat_sc);
        wechatPageAdapter = new WechatPageAdapter(WechatBusinessPageActivity.this, list_sc,sharedPreferencesHelper);
        lv_wechat_sc.setInterface(this);
        lv_wechat_sc.setHeaderView(invis);
        lv_wechat_sc.setAdapter(wechatPageAdapter);


        //下面设置悬浮和头顶部分内容
        final View header = View.inflate(this, R.layout.view_wechat_page_header, null);//头部内容,会隐藏的部分
        lv_wechat_sc.addHeaderView(header);//添加头部
        final View header2 = View.inflate(this, R.layout.view_wechat_page_header2, null);//头部内容,一直显示的部分
        lv_wechat_sc.addHeaderView(header2);//添加头部

        et_wechat_page_header_sousuo = (EditText)header2.findViewById(R.id.et_wechat_page_header_sousuo);
        bt_wechat_page_header_sousuo = (Button) header2.findViewById(R.id.bt_wechat_page_header_sousuo);
        bt_wechat_page_header_sousuo.setOnClickListener(this);
        bt_wechat_page_header_fbsp = (Button) header2.findViewById(R.id.bt_wechat_page_header_fbsp);
        bt_wechat_page_header_fbsp.setOnClickListener(this);


        banner = (Banner) header.findViewById(R.id.wechat_banner);

        TextWatcher1 textWatcher1 = new TextWatcher1();
        TextWatcher2 textWatcher2 = new TextWatcher2();

        et_wechat_page_sousuo.addTextChangedListener(textWatcher1);
        et_wechat_page_header_sousuo.addTextChangedListener(textWatcher2);
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
            currtPage = 0;
            countPage = 0;
            lv_wechat_sc.cancleAllDataSuccess();

            System.out.println("notifyAllActivity--------------------wechatbusi----");
            getAdvertisementData();
            getWechatData();
        }
        System.out.println("jobpage--"+shengId+"-"+chengshiId+"-"+quxianId+"-"+xiangzhenId);
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
                et_wechat_page_header_sousuo.setText(editable.toString());
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
                et_wechat_page_sousuo.setText(editable.toString());
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

        getAdvertisementData();
        getWechatData();
    }

    @Override
    public void onLoad() {
        if(currtPage >= countPage){
            //通知ListView加载完毕
            lv_wechat_sc.loadComplete();
            lv_wechat_sc.setAllDataSuccess();
        }else{
            getWechatData();
        }
    }

    public void loadBanner(){
        banner.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
        banner.setImageLoader(new GlideImageLoader());
        banner.setImages(images);
        banner.setBannerTitles(titles);
        banner.isAutoPlay(true);
        banner.start();
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(mContext, AdvertisingContentActivity.class);
                intent.putExtra("adID",adList.get(position).getAdID());
                mContext.startActivity(intent);
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
    public void getAdvertisementData(){

        RequestBody requestBody = new FormBody.Builder().add("pageID","6").add("chengshiid",chengshiId)
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
                    Log.d("MallPageActivity","WechatBusinessPageActivity=="+content);
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
                        result_list = result.getData();

                        handler.sendEmptyMessage(SHOP_OK);
                    }
                }
            }
        });
    }


    @Override
    public void onTabActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //实现该处逻辑
            ArrayList<MenuData> result = data.getExtras().getParcelableArrayList("result");//得到新Activity 关闭后返回的数据
            selectList.clear();
            selectList.addAll(result);
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

    public boolean  isLogin(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        if(!"".equals(sessionID) && !"".equals(userid)){
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_wechat_page_sousuo :
                System.out.println("bt_wechat_page_sousuo---------");
                currtPage = 0;
                searchContent = et_wechat_page_sousuo.getText().toString().trim();
                getWechatData();
                break;
            case R.id.bt_wechat_page_fbsp :
                showDialog("");
                getShopYajin();
                break;
            case R.id.bt_wechat_page_header_sousuo :
                System.out.println("bt_wechat_page_header_sousuo---------");
                currtPage = 0;
                searchContent = et_wechat_page_sousuo.getText().toString().trim();
                getWechatData();
                break;
            case R.id.bt_wechat_page_header_fbsp :
                showDialog("");
                getShopYajin();
                break;
            default:
                break;
        }
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
        AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
        builder.setTitle("提示" ) ;
        builder.setMessage("您的商城押金不足，无法发布信息，请先充值！" ) ;
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mContext.startActivity(new Intent(mContext, ShopYaJinActivity.class));
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}
