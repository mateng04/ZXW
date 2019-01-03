package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.ShopEvaluateAdapter;
import com.mobile.zxw.myapplication.bean.ShopEvaluateBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.EntityFY;
import com.mobile.zxw.myapplication.ui.widget.XListView;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

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

public class ShopEvaluateActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener{


    private Context mContext;
    private XListView mListView;
    private List<ShopEvaluateBean> list_shop_carevaluate = new ArrayList<ShopEvaluateBean>();
    private List<ShopEvaluateBean> list_shop_carevaluate_temp;
    private ShopEvaluateAdapter adapter_shop_evaluate;

    Button bt_shop_evaluate_back;
    String xxid;

    private  int countPage = 0;
    private  int currtPage = 0;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    if(currtPage == 1){
                        list_shop_carevaluate.clear();
                    }
                    if(list_shop_carevaluate_temp != null){
                        list_shop_carevaluate.addAll(list_shop_carevaluate_temp);
                    }
                    adapter_shop_evaluate.notifyDataSetChanged();
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
        setContentView(R.layout.activity_shop_evaluate);
        mContext = this;

        xxid = getIntent().getStringExtra("xxid");

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ShopEvaluateActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        System.out.println("xxid-----------"+xxid);

        initViews();
        initData();
    }

    //操作控件
    private void initViews() {

        bt_shop_evaluate_back = (Button)findViewById(R.id.bt_shop_evaluate_back);
        bt_shop_evaluate_back.setOnClickListener(this);

        mListView = (XListView)findViewById(R.id.lv_shop_pj);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_shop_evaluate = new ShopEvaluateAdapter(ShopEvaluateActivity.this,list_shop_carevaluate);
        mListView.setAdapter(adapter_shop_evaluate);



    }
    public void initData(){

//        list_shop_carevaluate.clear();
//
//        ShopEvaluateBean shopEvaluateBean1 = new ShopEvaluateBean();
//        shopEvaluateBean1.setEvaluateName("张大夫");
//        shopEvaluateBean1.setGrade("普通会员");
//        shopEvaluateBean1.setMemberTime("2018-05-06");
//        shopEvaluateBean1.setEscription("3.5");
//        shopEvaluateBean1.setAttitude("4");
//        shopEvaluateBean1.setLogistics("4.5");
//        shopEvaluateBean1.setEvaluateContent("质量不错");
//        shopEvaluateBean1.setEvaluateTime("2018-07-04");
//        list_shop_carevaluate.add(shopEvaluateBean1);

        getEvaluateData();

    }


    @Override
    public void onRefresh() {
        System.out.println("----------------onRefresh");
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        getEvaluateData();
    }

    @Override
    public void onLoadMore() {
        getEvaluateData();
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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_shop_evaluate_back:
                finish();
                break;
        }
    }

    //获取数据
    public void getEvaluateData(){
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                .add("currtPage",currtPage+"")  .add("num","20")
                .add("shopID",xxid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getEvaluateData.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
                handler.sendEmptyMessage(2);

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("getEvaluateData","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityFY<List<ShopEvaluateBean>> result = gson.fromJson(content, new TypeToken<EntityFY<List<ShopEvaluateBean>>>() {}.getType());
                        if("0".equals(result.getCode())){
                            list_shop_carevaluate_temp = result.getData();
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
}


