package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.AdverManageAdapter;
import com.mobile.zxw.myapplication.bean.AdverManageBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.EntityFY;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.ui.widget.XListView;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.StreamUtils;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AdvertisingManageActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener{

    private Context mContext;
    private XListView mListView;
    private List<AdverManageBean> list_messages = new ArrayList<AdverManageBean>();
    List<AdverManageBean> temp_list_messages;
    private AdverManageAdapter adapter_message;

    private  int countPage = 0;
    private  int currtPage = 0;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private RelativeLayout rl_adver_manage_fbgg;
    private Button bt_adver_manage_back;

    private RelativeLayout rl_adver_manage_dialog;
    private TextView rl_adver_manage_dialog_sute;

    public static List<AreaBean> provinceList;
    public static List<AreaBean> cityList;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    if(currtPage == 1){
                        list_messages.clear();
                    }
                    if(temp_list_messages != null){
                        list_messages.addAll(temp_list_messages);
                    }
                    adapter_message.notifyDataSetChanged();
                    onLoad();
                    break;
                case 1:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adver_manage);
        mContext = this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                AdvertisingManageActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initData();
    }

    public void onPageSelect(){
        if(!isLogin()){
            rl_adver_manage_dialog.setVisibility(View.VISIBLE);
            list_messages.clear();
            adapter_message.notifyDataSetChanged();
        }else{
            rl_adver_manage_dialog.setVisibility(View.GONE);
            mListView.autoRefresh();
        }
    }

    public void invisibleOnScreen(){
        System.out.println("invisibleOnScreen----");
//        initData();
    }

    public void goneOnScreen(){
//        log.d("goneOnScreen");

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        System.out.println("onWindowFocusChanged----");
        System.out.println("hasFocus----"+hasFocus);
        System.out.println("isLogin----"+isLogin());

        if (hasFocus && isLogin()) {
            rl_adver_manage_dialog.setVisibility(View.GONE);
            mListView.autoRefresh();
        }else{
            if(!isLogin()){
                rl_adver_manage_dialog.setVisibility(View.VISIBLE);
                list_messages.clear();
                adapter_message.notifyDataSetChanged();
            }
        }
    }

    public void initData(){
        if(isLogin()){
            rl_adver_manage_dialog.setVisibility(View.GONE);
            adguanli();
        }else{
            rl_adver_manage_dialog.setVisibility(View.VISIBLE);
            list_messages.clear();
            adapter_message.notifyDataSetChanged();
        }

    }

    public void initView(){

        rl_adver_manage_dialog = (RelativeLayout)findViewById(R.id.rl_adver_manage_dialog);
        rl_adver_manage_dialog_sute = (TextView)findViewById(R.id.rl_adver_manage_dialog_sute);
        rl_adver_manage_dialog_sute.setOnClickListener(this);

        bt_adver_manage_back = (Button)findViewById(R.id.bt_adver_manage_back);
        bt_adver_manage_back.setOnClickListener(this);

        mListView = (XListView)findViewById(R.id.lv_adver_manage);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_message = new AdverManageAdapter(AdvertisingManageActivity.this,list_messages);
        mListView.setAdapter(adapter_message);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){ return; }
                System.out.println("position-----"+position);
                AdverManageBean adverManageBean = list_messages.get(position-1);
                if("1".equals(adverManageBean.getDel())){
                    Toast.makeText(mContext,"该广告已删除!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if("0".equals(adverManageBean.getState())){
                    Toast.makeText(mContext,"该信息未通过审核!",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!"".equals(adverManageBean.getDqsj())){
                    Date date1  = new Date ();
                    Date  date2;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date2 = sdf.parse(adverManageBean.getDqsj());
                        if(date1.getTime() > date2.getTime()){
                            Toast.makeText(mContext,"该信息到期时间已过!",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                Intent intent = new Intent(AdvertisingManageActivity.this,AdvertisingContentActivity.class);
                intent.putExtra("adID",adverManageBean.getAdid());
                startActivity(intent);

            }
        });

        rl_adver_manage_fbgg = (RelativeLayout) findViewById(R.id.rl_adver_manage_fbgg);
        rl_adver_manage_fbgg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_adver_manage_fbgg :
                    startActivity(new Intent(AdvertisingManageActivity.this,AdvertisingReleaseActivity.class));
                break;
            case R.id.bt_adver_manage_back :
                   finish();
                break;
            case R.id.rl_adver_manage_dialog_sute :
                mContext.startActivity(new Intent(mContext, LoginPageActivity.class));
                break;

            default:
                break;
        }
    }


    @Override
    public void onRefresh() {
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        adguanli();
    }

    @Override
    public void onLoadMore() {
        adguanli();
    }

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(Utils.getTime());
        if(currtPage >= countPage){
            mListView.setPullLoadComplete(true);
        }else {
            mListView.setPullLoadComplete(false);
        }
        mListView.setPullLoadEnable(true);
    }

    //广告管理
    public void adguanli(){

        if(sessionID == null || "".equals(sessionID)){
            sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        }
        if(userid == null || "".equals(userid)){
            userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        }

        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder().add("currtPage",currtPage+"")  .add("num","20")
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/adguanli.asp").post(requestBody).build();
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

                    Gson gson = new Gson();
                    String data_province = StreamUtils.get(APP.getInstance(), R.raw.area_province);
                    Type type = new TypeToken<List<AreaBean>>() {}.getType();
                    provinceList = gson.fromJson(data_province, type);
                    String data_city = StreamUtils.get(APP.getInstance(), R.raw.area_city);
                    cityList = gson.fromJson(data_city, type);
                    String content = response.body().string();

                    if(response.code() == 200){

                        EntityFY<List<AdverManageBean>> result = gson.fromJson(content, new TypeToken<EntityFY<List<AdverManageBean>>>() {}.getType());
                        if("0".equals(result.getCode())){
                            temp_list_messages = result.getData();
                            for(AdverManageBean adverManageBean: temp_list_messages){
                                if(adverManageBean.getQysheng() != null && !"".equals(adverManageBean.getQysheng())){
                                    for(AreaBean areaBean : provinceList){
                                        if(adverManageBean.getQysheng().equals(areaBean.getTid()+"")){
                                            adverManageBean.setQysheng(areaBean.getName());

                                        }
                                    }
                                }
                                if(adverManageBean.getQyshi() != null && !"".equals(adverManageBean.getQyshi())){
                                    for(AreaBean areaBean : cityList){
                                        if(adverManageBean.getQyshi().equals(areaBean.getTid()+"")){
                                            adverManageBean.setQyshi(areaBean.getName());
                                        }
                                    }
                                }
                            }


                            countPage = result.getCountPage();
                            currtPage= result.getCurrtPage();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }else{
                        handler.sendEmptyMessage(DATA_ERROR);
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


