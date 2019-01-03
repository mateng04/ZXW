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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.OrderManageAdapter;
import com.mobile.zxw.myapplication.bean.OrderManageBean;
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

public class OrderSellerActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener {

    private Context mContext;
    private XListView mListView;
    private List<OrderManageBean> list_messages = new ArrayList<OrderManageBean>();
    private List<OrderManageBean> temp_list_messages;
    private OrderManageAdapter adapter_message;

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
        setContentView(R.layout.activity_order_buyer);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                OrderSellerActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        initView();
        initData();
    }

    public void initView(){

        mListView = (XListView)findViewById(R.id.lv_order_buyer);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_message = new OrderManageAdapter(OrderSellerActivity.this,list_messages);
        mListView.setAdapter(adapter_message);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startActivity(new Intent(MessageCenterActivity.this,MessageContentActivity.class));
                Intent intent = new Intent(mContext,OrderDetailsActivity.class);
                intent.putExtra("dingdanhao",list_messages.get(position-1).getDdh());
                intent.putExtra("laiyuan","seller");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login :

                break;

            default:
                break;
        }
    }
    public void initData(){
        shopdingdan();
    }


    @Override
    public void onRefresh() {
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        shopdingdan();
    }

    @Override
    public void onLoadMore() {
        shopdingdan();
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
    //获取订单信息
    public void shopdingdan(){
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder().add("currtPage",currtPage+"")  .add("num","20")
                .add("sessionID",sessionID).add("userid",userid)
                .add("type","1").build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopdingdan.asp").post(requestBody).build();
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
                    Log.d("shopdingdan","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityFY<List<OrderManageBean>> result = gson.fromJson(content, new TypeToken<EntityFY<List<OrderManageBean>>>() {}.getType());
                        if("0".equals(result.getCode())){
                            temp_list_messages = result.getData();
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


