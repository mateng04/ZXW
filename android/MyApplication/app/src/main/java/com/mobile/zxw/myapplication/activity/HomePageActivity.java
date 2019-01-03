package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.HomeQZAdapter;
import com.mobile.zxw.myapplication.adapter.HomeQZJZAdapter;
import com.mobile.zxw.myapplication.adapter.HomeSCAdapter;
import com.mobile.zxw.myapplication.adapter.HomeZPAdapter;
import com.mobile.zxw.myapplication.adapter.HomeZPJZAdapter;
import com.mobile.zxw.myapplication.bean.AdvertisementBean;
import com.mobile.zxw.myapplication.bean.HomeQZBean;
import com.mobile.zxw.myapplication.bean.HomeQZJZBean;
import com.mobile.zxw.myapplication.bean.HomeSCBean;
import com.mobile.zxw.myapplication.bean.HomeWSBean;
import com.mobile.zxw.myapplication.bean.HomeZPBean;
import com.mobile.zxw.myapplication.bean.HomeZPJZBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityHomeHr;
import com.mobile.zxw.myapplication.jsonEntity.EntityHomeJob;
import com.mobile.zxw.myapplication.jsonEntity.EntityHomeShop;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener, IListener,  HomeSCAdapter.OnItemClickListener {

    Context mContext = null;
    Banner banner;
    List images = new ArrayList();
    List titles = new ArrayList();

    private String shengId="";
    private String chengshiId="";
    private String quxianId="";
    private String xiangzhenId="";


    ScrollView sv_home_page;

    ImageView more_zp, more_qz, more_sc, more_wszq;

    ListView listview_qzzp, listview_jzzp,listview_qzjl,listview_jzjl;
    HomeZPAdapter adapter_qzzp;
    HomeZPJZAdapter adapter_jzzp;
    HomeQZAdapter adapter_qzjl;
    HomeQZJZAdapter adapter_jzjl;
    HomeSCAdapter homeSCAdapter;
    HomeSCAdapter homeSCAdapterWSZQ;


    private List<HomeZPBean> list_qzzp = new ArrayList<HomeZPBean>();
    private List<HomeZPBean> list_temp_qzzp;
    private List<HomeZPJZBean> list_jzzp = new ArrayList<HomeZPJZBean>();
    private List<HomeZPJZBean> list_jtemp_zzp;
    private List<HomeQZBean> list_qzjl = new ArrayList<HomeQZBean>();
    private List<HomeQZBean> list_temp_qzjl;
    private List<HomeQZJZBean> list_jzjl = new ArrayList<HomeQZJZBean>();
    private List<HomeQZJZBean> list_temp_jzjl;

    private RecyclerView recycler_sc, recycler_wszq;
    List<HomeSCBean> list_sc = new ArrayList<>();
    List<HomeSCBean> list_temp_sc;
    List<HomeSCBean> list_wszq = new ArrayList<>();
    List<HomeSCBean> list_temp_wszq ;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    List<AdvertisementBean> adList = new ArrayList<AdvertisementBean>();
    List<AdvertisementBean> adTempList;

    static int AD_OK = 0; //广告
    static int ZP_OK = 1; //招聘数据
    static int QZ_OK = 2; //求职数据
    static int SC_OK = 3; //商城数据


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
                    list_qzzp.clear();
                    if(list_temp_qzzp != null){
                        list_qzzp.addAll(list_temp_qzzp);
                    }
                    list_jzzp.clear();
                    if(list_jtemp_zzp != null){
                        list_jzzp.addAll(list_jtemp_zzp);
                    }
                    adapter_qzzp.notifyDataSetChanged();
                    setHeight(listview_qzzp,adapter_qzzp);
                    adapter_jzzp.notifyDataSetChanged();
                    setHeight(listview_jzzp,adapter_jzzp);
                    sv_home_page.scrollTo(0,0);
                    break;
                case 2:
                    list_qzjl.clear();
                    if(list_temp_qzjl != null){
                        list_qzjl.addAll(list_temp_qzjl);
                    }
                    list_jzjl.clear();
                    if(list_temp_jzjl != null){
                        list_jzjl.addAll(list_temp_jzjl);
                    }
                    adapter_qzjl.notifyDataSetChanged();
                    setHeight(listview_qzjl,adapter_qzjl);
                    adapter_jzjl.notifyDataSetChanged();
                    setHeight(listview_jzjl,adapter_jzjl);
                    sv_home_page.scrollTo(0,0);
                    break;
                case 3:
                    list_sc.clear();
                    if(list_temp_sc != null){
                        list_sc.addAll(list_temp_sc);
                    }
                    System.out.println("list_sc------------"+list_sc.size());
                    homeSCAdapter.notifyDataSetChanged();
                    list_wszq.clear();
                    if(list_temp_wszq != null){
                        list_wszq.addAll(list_temp_wszq);
                    }
                    System.out.println("list_wszq------------"+list_wszq.size());
                    homeSCAdapterWSZQ.notifyDataSetChanged();
//                    adapter_qzjl.notifyDataSetChanged();
//                    adapter_jzjl.notifyDataSetChanged();
//                    setHeight(listview_jzjl,adapter_jzjl);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mContext = HomePageActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                HomePageActivity.this, "config");

//        chengshiId = (String) sharedPreferencesHelper.getSharedPreference("chengshiid", "");
//        quxianId = (String) sharedPreferencesHelper.getSharedPreference("quxianid", "");
//        xiangzhenId = (String) sharedPreferencesHelper.getSharedPreference("xiangzhenid", "");

        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        loadBanner();
        initData();

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
                Intent intent = new Intent(HomePageActivity.this, AdvertisingContentActivity.class);
                intent.putExtra("adID",adList.get(position).getAdID());
                HomePageActivity.this.startActivity(intent);
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

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
//        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
//        banner.stopAutoPlay();
    }

    @Override
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.more_zp:
                //发送广播通知所有注册该接口的监听器
                ListenerManager.getInstance().sendBroadCast(0,"more_zp");
                break;
            case R.id.more_qz:
                ListenerManager.getInstance().sendBroadCast(0,"more_qz");
                break;
            case R.id.more_sc:
                ListenerManager.getInstance().sendBroadCast(0,"more_sc");
                break;
            case R.id.more_wszq:
                ListenerManager.getInstance().sendBroadCast(0,"more_wszq");
                break;

            default:
                break;
        }
    }

    public void initView(){

        banner = (Banner) findViewById(R.id.home_banner);

        sv_home_page = (ScrollView)findViewById(R.id.sv_home_page);

        more_zp = (ImageView)findViewById(R.id.more_zp);
        more_zp.setOnClickListener(this);
        more_qz = (ImageView)findViewById(R.id.more_qz);
        more_qz.setOnClickListener(this);
        more_sc = (ImageView)findViewById(R.id.more_sc);
        more_sc.setOnClickListener(this);
        more_wszq = (ImageView)findViewById(R.id.more_wszq);
        more_wszq.setOnClickListener(this);

        adapter_qzzp = new HomeZPAdapter(HomePageActivity.this,list_qzzp);
        listview_qzzp = (ListView) findViewById(R.id.listview_qzzp);
        listview_qzzp.setAdapter(adapter_qzzp);
        listview_qzzp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    String hrQZID = list_qzzp.get(position).getHrQuanZhiID();
                    Intent intent = new Intent(mContext, RecruitDetailsActivity.class);
                    intent.putExtra("ID",hrQZID);
                    intent.putExtra("leixing","全职招聘");
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
            }
        });

        adapter_jzzp = new HomeZPJZAdapter(HomePageActivity.this,list_jzzp);
        listview_jzzp = (ListView) findViewById(R.id.listview_jzzp);
        listview_jzzp.setAdapter(adapter_jzzp);
        listview_jzzp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    String hrJZID = list_jzzp.get(position).getHrJianZhiID();
                    Intent intent = new Intent(mContext, RecruitDetailsActivity.class);
                    intent.putExtra("ID",hrJZID);
                    intent.putExtra("leixing","兼职招聘");
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
            }
        });

        adapter_qzjl = new HomeQZAdapter(HomePageActivity.this,list_qzjl);
        listview_qzjl = (ListView) findViewById(R.id.listview_qzjl);
        listview_qzjl.setAdapter(adapter_qzjl);
        listview_qzjl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    String jobQZID = list_qzjl.get(position).getJobQuanZhiID();
                    Intent intent = new Intent(mContext, JobPageDetailsActivity.class);
                    intent.putExtra("ID",jobQZID);
                    intent.putExtra("leixing","全职简历");
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
            }
        });

        adapter_jzjl = new HomeQZJZAdapter(HomePageActivity.this,list_jzjl);
        listview_jzjl = (ListView) findViewById(R.id.listview_jzjl);
        listview_jzjl.setAdapter(adapter_jzjl);
        listview_jzjl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    String jobJZID = list_jzjl.get(position).getJobJianZhiID();
                    Intent intent = new Intent(mContext, JobPageDetailsActivity.class);
                    intent.putExtra("ID",jobJZID);
                    intent.putExtra("leixing","兼职简历");
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
            }
        });

        recycler_sc = (RecyclerView) findViewById(R.id.recycler_sc);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        layoutManager.setAutoMeasureEnabled(true);
        recycler_sc.setLayoutManager(layoutManager);
        //绑定适配器
        homeSCAdapter = new HomeSCAdapter(this, list_sc,"sc");
        recycler_sc.setAdapter(homeSCAdapter);
        homeSCAdapter.setOnItemClickListener(this);//将接口传递到数据产生的地方

        recycler_wszq = (RecyclerView) findViewById(R.id.recycler_wszq);
        StaggeredGridLayoutManager layoutManagerWSZQ = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        layoutManagerWSZQ.setAutoMeasureEnabled(true);
        recycler_wszq.setLayoutManager(layoutManagerWSZQ);
        //绑定适配器
        homeSCAdapterWSZQ = new HomeSCAdapter(this, list_wszq,"wxzq");
        recycler_wszq.setAdapter(homeSCAdapterWSZQ);
        homeSCAdapterWSZQ.setOnItemClickListener(this);//将接口传递到数据产生的地方

    }

    public void initData(){

        getAdvertisementData();
        hrQuanZhiTile();
        jobQuanZhiTile();
        shopTile();

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

            jobQuanZhiTile();
            getAdvertisementData();
            hrQuanZhiTile();
            shopTile();
        }
        System.out.println("homepage--"+shengId+"-"+chengshiId+"-"+quxianId+"-"+xiangzhenId);
    }

    @Override
    public void onItemClick(int position, HomeSCBean model,String tag) {
        System.out.println("position-"+position);
        if("wxzq".equals(tag)){
            if(isLogin()){
                String wsID = list_wszq.get(position).getShopID();
                Intent intent = new Intent(mContext, WXShopDetailedActivity.class);
                intent.putExtra("ID",wsID);
                mContext.startActivity(intent);
            }
        }else if("sc".equals(tag)){
            if(isLogin()){
                Intent intent = new Intent(mContext,ShopDetailsActivity.class);
                intent.putExtra("xxid",list_sc.get(position).getShopID());
                mContext.startActivity(intent);
            }
        }
    }

    public void setHeight(ListView listview, BaseAdapter adapter){
        int height = 0;
        int count = adapter.getCount();
        for(int i=0;i<count;i++){
            View temp = adapter.getView(i,null,listview);
            temp.measure(0,0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.width = ViewGroup.LayoutParams.FILL_PARENT;
        params.height = height;
    }


    //获取广告
    public void getAdvertisementData(){
        Log.d("kwwl","getAdvertisementData()==");
        RequestBody requestBody = new FormBody.Builder().add("pageID","0").add("chengshiid",chengshiId).build();
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
                    Log.d("HomePage","HomePage=="+content);
//                    Log.d("kwwl","res=="+response.body().string());
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

    //获取招聘数据
    public void hrQuanZhiTile(){

        RequestBody requestBody = new FormBody.Builder().add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/hrQuanZhiTile.asp").post(requestBody).build();
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

                    Log.d("kwwl","response.code()=="+response.code());
                    Log.d("kwwl","response.message()=="+response.message());
//                    Log.d("kwwl","res=="+response.body().string());

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityHomeHr result = gson.fromJson(content,  new TypeToken<EntityHomeHr>() {}.getType());
                        list_temp_qzzp = result.getHrQuanZhi();
                        list_jtemp_zzp = result.getHrJianZhi();

                        handler.sendEmptyMessage(ZP_OK);

                    }

                }
            }
        });
    }

    //获取求职数据
    public void jobQuanZhiTile(){

        RequestBody requestBody = new FormBody.Builder().add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/jobQuanZhiTile.asp").post(requestBody).build();
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

                    Log.d("jobQuanZhiTile","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityHomeJob result = gson.fromJson(content,  new TypeToken<EntityHomeJob>() {}.getType());
                        list_temp_qzjl = result.getJobQuanZhi();
                        list_temp_jzjl = result.getJobJianZhi();

                        handler.sendEmptyMessage(QZ_OK);

                    }

                }
            }
        });
    }

    //获取商城数据
    public void shopTile(){

        RequestBody requestBody = new FormBody.Builder().add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/shopTile.asp").post(requestBody).build();
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

                    Log.d("kwwl","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityHomeShop result = gson.fromJson(content,  new TypeToken<EntityHomeShop>() {}.getType());
                        list_temp_sc = result.getShop();

                        List<HomeWSBean> wsList = result.getWs();
                        list_temp_wszq = new ArrayList<>();
                        if(wsList != null){
                            for(int i=0;i<wsList.size();i++){
                                HomeSCBean homeSCBean = new HomeSCBean();
                                homeSCBean.setShopID(wsList.get(i).getWsID());
                                homeSCBean.setShopPicUrl(wsList.get(i).getSwsPicUrl());
                                homeSCBean.setShopPrice(wsList.get(i).getWsPopularity());
                                homeSCBean.setShopTile(wsList.get(i).getWsTile());
                                list_temp_wszq.add(homeSCBean);
                            }
                        }
                        handler.sendEmptyMessage(SC_OK);
                    }
                }
            }
        });
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
