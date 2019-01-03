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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.GridViewAdapter;
import com.mobile.zxw.myapplication.adapter.JobDataAdapter;
import com.mobile.zxw.myapplication.bean.AdvertisementBean;
import com.mobile.zxw.myapplication.bean.BigZhiweiBean;
import com.mobile.zxw.myapplication.bean.GridInfo;
import com.mobile.zxw.myapplication.bean.JobBean;
import com.mobile.zxw.myapplication.bean.SmallZhiweiBean;
import com.mobile.zxw.myapplication.bean.YueXiClassBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityJob;
import com.mobile.zxw.myapplication.myinterface.IListener;
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

public class JobPageActivity extends AppCompatActivity implements LoadListView.IloadInterface , View.OnClickListener, IListener {
    private Context mContext = null;

    private boolean flag = true;
    EditText et_job_page_sousuo,et_job_page_header_sousuo;
    Button bt_job_page_sousuo,bt_job_page_fbqz;
    Button bt_job_page_header_sousuo,bt_job_page_header_fbqz;
    Button bt_job_page_queding,bt_job_page_chongzhi;

    Banner banner;
    List images = new ArrayList();
    List titles = new ArrayList();
    List<AdvertisementBean> adList = new ArrayList<AdvertisementBean>();
    List<AdvertisementBean> adTempList;
    List<BigZhiweiBean> dl_List = new ArrayList<>();
    List<BigZhiweiBean> dl_lishi_List;
    List<SmallZhiweiBean> xl_List = new ArrayList<>();
    List<SmallZhiweiBean> xl_lishi_List;
    List<YueXiClassBean> yx_List = new ArrayList<>();
    List<YueXiClassBean> yx_lishi_List;

    private DrawerLayout dlShow;

    private List<JobBean> list_qzzp = new ArrayList<JobBean>();
    List<JobBean> lishi_list;

    JobDataAdapter adapter_qzzp;

    private Button button_job_screen,button_job_header_screen;

    private GridView gv_job_dalei, gv_job_xiaolei, gv_job_yuexin;
    TextView tv_job_yuexin;

    String DL_ID = "";
    String XL_ID = "";
    String YX_ID = "";

    String bigclassid = "";
    String smallclassid = "";
    String yuexin = "";
    String searchContent = "";

    private List<GridInfo> daLeiList = new ArrayList<GridInfo>();
    private List<GridInfo> xiaoLeiList = new ArrayList<GridInfo>();
    private List<GridInfo> yueXinList = new ArrayList<GridInfo>();

    GridViewAdapter daleiAdapter;
    GridViewAdapter xiaoleiAdapter;
    GridViewAdapter yuexinAdapter;
    LoadListView lv_job_page;

    TextView tv_job_qzzp,tv_job_jzzp;
    TextView tv_job_header_qzzp,tv_job_header_jzzp;
    String type = "0";  // 0代表全职   1代表兼职
    private int countPage = 0;
    private int currtPage = 0;

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
                    lv_job_page.loadComplete();
                    cancelDialog();
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
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_page);

        mContext = JobPageActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                JobPageActivity.this, "config");

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
    public void onBackPressed() {
        dlShow.closeDrawer(Gravity.RIGHT);
        super.onBackPressed();
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
                Intent intent = new Intent(JobPageActivity.this, AdvertisingContentActivity.class);
                intent.putExtra("adID",adList.get(position).getAdID());
                JobPageActivity.this.startActivity(intent);
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

        tv_job_yuexin  = (TextView) findViewById(R.id.tv_job_yuexin);

        et_job_page_sousuo = (EditText) findViewById(R.id.et_job_page_sousuo);
        bt_job_page_sousuo = (Button) findViewById(R.id.bt_job_page_sousuo);
        bt_job_page_sousuo.setOnClickListener(this);
        bt_job_page_fbqz = (Button) findViewById(R.id.bt_job_page_fbqz);
        bt_job_page_fbqz.setOnClickListener(this);

        dlShow = (DrawerLayout) findViewById(R.id.dlShow_job);

        bt_job_page_queding = (Button)findViewById(R.id.bt_job_page_queding);
        bt_job_page_queding.setOnClickListener(this);
        bt_job_page_chongzhi = (Button)findViewById(R.id.bt_job_page_chongzhi);
        bt_job_page_chongzhi.setOnClickListener(this);

        gv_job_dalei = (GridView) findViewById(R.id.gv_job_dalei);
        daleiAdapter = new GridViewAdapter(this,  daLeiList);
        gv_job_dalei.setAdapter(daleiAdapter);
        gv_job_dalei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        gv_job_xiaolei = (GridView) findViewById(R.id.gv_job_xiaolei);
        xiaoleiAdapter = new GridViewAdapter(this,  xiaoLeiList);
        gv_job_xiaolei.setAdapter(xiaoleiAdapter);
        gv_job_xiaolei.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        gv_job_yuexin = (GridView) findViewById(R.id.gv_job_yuexin);
        yuexinAdapter = new GridViewAdapter(this,  yueXinList);
        gv_job_yuexin.setAdapter(yuexinAdapter);
        gv_job_yuexin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("yuexinAdapter----position----"+position);
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
                System.out.println("yuexinAdapter----yuexin----"+yuexin);
            }
        });

        button_job_screen = (Button) findViewById(R.id.button_job_screen);
        button_job_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlShow.openDrawer(Gravity.RIGHT);
            }
        });

        LinearLayout invis = (LinearLayout) findViewById(R.id.invis_job);

        //LoadListView
        lv_job_page = (LoadListView)findViewById(R.id.lv_job_page);
        adapter_qzzp = new JobDataAdapter(JobPageActivity.this, list_qzzp);
        lv_job_page.setInterface(this);
        lv_job_page.setHeaderView(invis);
        lv_job_page.setAdapter(adapter_qzzp);
        lv_job_page.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isLogin()){
                    System.out.println("position------"+position);

                    //position 是从2开始，暂时没找到原因，所以都要减2
                    if(position < 2){
                        return;
                    }
                    if((position-2) >= list_qzzp.size()){
                        return;
                    }

                    String hrQZID = list_qzzp.get(position-2).getJobID();
                    Intent intent = new Intent(mContext, JobPageDetailsActivity.class);
                    intent.putExtra("ID",hrQZID);
                    if("0".equals(type)){
                        intent.putExtra("leixing","全职简历");
                    }else{
                        intent.putExtra("leixing","兼职简历");
                    }
//                    intent.putExtra("leixing",type);
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
//                startActivity(new Intent(RecruitPageActivity.this, RecruitDetailsActivity.class));
            }
        });


        //下面设置悬浮和头顶部分内容
        final View header = View.inflate(this, R.layout.view_job_page_header, null);//头部内容,会隐藏的部分
        lv_job_page.addHeaderView(header);//添加头部
        final View header2 = View.inflate(this, R.layout.view_job_page_header2, null);//头部内容,一直显示的部分
        lv_job_page.addHeaderView(header2);//添加头部

        tv_job_header_qzzp = (TextView) header2.findViewById(R.id.tv_job_header_qzzp);
        tv_job_header_qzzp.setOnClickListener(this);
        tv_job_header_jzzp = (TextView) header2.findViewById(R.id.tv_job_header_jzzp);
        tv_job_header_jzzp.setOnClickListener(this);

        et_job_page_header_sousuo = (EditText) header2.findViewById(R.id.et_job_page_header_sousuo);
        bt_job_page_header_sousuo = (Button) header2.findViewById(R.id.bt_job_page_header_sousuo);
        bt_job_page_header_sousuo.setOnClickListener(this);
        bt_job_page_header_fbqz = (Button) header2.findViewById(R.id.bt_job_page_header_fbqz);
        bt_job_page_header_fbqz.setOnClickListener(this);

        banner = (Banner) header.findViewById(R.id.job_banner);

        button_job_header_screen = (Button) findViewById(R.id.button_job_header_screen);
        button_job_header_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlShow.openDrawer(Gravity.RIGHT);
            }
        });

        tv_job_qzzp = (TextView) findViewById(R.id.tv_job_qzzp);
        tv_job_qzzp.setOnClickListener(this);
        tv_job_jzzp = (TextView) findViewById(R.id.tv_job_jzzp);
        tv_job_jzzp.setOnClickListener(this);
    }

    @Override
    public void onLoad() {
        if(currtPage >= countPage){
            //通知ListView加载完毕
            lv_job_page.loadComplete();
            lv_job_page.setAllDataSuccess();
        }else{
            getjobData(type);
        }

    }


    public void initData() {
        list_qzzp.clear();
        getAdvertisementData();
        getjobData(type);
        BigZhiweiClass();
        YueXiClass();

        TextWatcher1 textWatcher1 = new TextWatcher1();
        TextWatcher2 textWatcher2 = new TextWatcher2();

        et_job_page_sousuo.addTextChangedListener(textWatcher1);
        et_job_page_header_sousuo.addTextChangedListener(textWatcher2);
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

            list_qzzp.clear();
            lv_job_page.cancleAllDataSuccess();
            countPage = 0;
            currtPage = 0;
            getAdvertisementData();
            getjobData(type);
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
                et_job_page_header_sousuo.setText(editable.toString());
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
                et_job_page_sousuo.setText(editable.toString());
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
//        for(int i=0;i<yx_List.size();i++){
//            GridInfo ygridInfo = new GridInfo();
//            if("面议".equals(yx_List.get(i).getYuexinName())){
//                ygridInfo.setSelect(true);
//                ygridInfo.setTitle(yx_List.get(i).getYuexinName());
//                yueXinList.add(0,ygridInfo);
//            }else{
//                ygridInfo.setTitle(yx_List.get(i).getYuexinName());
//                yueXinList.add(ygridInfo);
//            }
//
//            yuexinAdapter.notifyDataSetChanged();
//        }
        GridInfo ygridInfo = new GridInfo();
        ygridInfo.setSelect(true);
        ygridInfo.setTitle("不限");
        yueXinList.add(0,ygridInfo);

        for(int i=0;i<yx_List.size();i++){
            GridInfo ygridInfo2 = new GridInfo();
            ygridInfo2.setTitle(yx_List.get(i).getYuexinName());
            yueXinList.add(ygridInfo2);
        }
        yuexinAdapter.notifyDataSetChanged();
    }

    //获取广告
    public void getAdvertisementData(){
        String pageID = "";
        if("0".equals(type)){
            pageID = "3";
        }else{
            pageID = "4";
        }

        RequestBody requestBody = new FormBody.Builder().add("pageID","2").add("chengshiid",chengshiId)
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
                    Log.d("JobPageActivity","gg=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<AdvertisementBean>> result = gson.fromJson(content, new TypeToken<Entity<List<AdvertisementBean>>>() {}.getType());
                        adList.clear();
                        adTempList = result.getData();
                        handler.sendEmptyMessage(AD_OK);
                    }
                }
            }
        });
    }

    //获取简历数据
    public void getjobData(String workType){
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

        RequestBody requestBody = new FormBody.Builder()
                .add("currtPage",currtPage+"")  .add("num","20").add("chengshiid",chengshiId)
                .add("quxianid",quxianId).add("xiangzhenid",xiangzhenId)
                .add("workType",workType).addEncoded("bigclassid",dl)
                .addEncoded("smallclassid",xl).addEncoded("yuexin",yx)
                .addEncoded("searchContent",SC).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getJobData.asp").post(requestBody).build();
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
                    Log.d("JobPageActivity","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityJob result = gson.fromJson(content, new TypeToken<EntityJob>() {}.getType());
                        countPage = result.getCountPage();
                        currtPage = result.getCurrtPage();
                        lishi_list = result.getData();
                        handler.sendEmptyMessage(QZ_OK);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_job_qzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_job_yuexin.setVisibility(View.VISIBLE);
                gv_job_yuexin.setVisibility(View.VISIBLE);
                //先取消加载完成设置
                lv_job_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";
                tv_job_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                tv_job_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                getjobData("0");
                break;
            case R.id.tv_job_jzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_job_yuexin.setVisibility(View.GONE);
                gv_job_yuexin.setVisibility(View.GONE);

                //先取消加载完成设置
                lv_job_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";

                tv_job_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_job_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_job_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));

                getjobData("1");
                break;
            case R.id.tv_job_header_qzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_job_yuexin.setVisibility(View.VISIBLE);
                gv_job_yuexin.setVisibility(View.VISIBLE);
                //先取消加载完成设置
                lv_job_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "0";

                tv_job_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                tv_job_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));

                getjobData("0");
                break;
            case R.id.tv_job_header_jzzp :
                showDialog("正在加载数据");
                setChongZhi();
                tv_job_yuexin.setVisibility(View.GONE);
                gv_job_yuexin.setVisibility(View.GONE);
                //先取消加载完成设置
                lv_job_page.cancleAllDataSuccess();
                countPage = 0;
                currtPage = 0;
//                list_qzzp.clear();
                type = "1";
                tv_job_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_job_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                tv_job_header_qzzp.setTextColor(mContext.getResources().getColor(R.color.black));
                tv_job_header_qzzp.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                tv_job_header_jzzp.setTextColor(mContext.getResources().getColor(R.color.white));
                tv_job_header_jzzp.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                getjobData("1");
                break;
            case R.id.bt_job_page_queding :
                System.out.println("bt_recruit_page_queding--------"+DL_ID+"---"+XL_ID+"----"+YX_ID);
                dlShow.closeDrawer(Gravity.RIGHT);
                currtPage = 0;
                searchContent = et_job_page_sousuo.getText().toString().trim();
                getjobData(type);
                break;
            case R.id.bt_job_page_chongzhi :
                System.out.println("bt_recruit_page_queding--------");
                setChongZhi();
                break;
            case R.id.bt_job_page_sousuo :
                currtPage = 0;
                searchContent = et_job_page_sousuo.getText().toString().trim();
                getjobData(type);
                break;
            case R.id.bt_job_page_fbqz :
                if(isLogin()){
                    Intent intent = new Intent(mContext, JobManageActivity.class);
                    mContext.startActivity(intent);
                }else {
                    Utils.getLoginDialog(mContext);
                }
                break;
            case R.id.bt_job_page_header_sousuo :
                searchContent = et_job_page_header_sousuo.getText().toString().trim();
                currtPage = 0;
                getjobData(type);
                break;
            case R.id.bt_job_page_header_fbqz:
                if(isLogin()){
                    Intent intent = new Intent(mContext, JobManageActivity.class);
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
