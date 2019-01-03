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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.MessageCenterAdapter;
import com.mobile.zxw.myapplication.bean.MessageCenterBean;
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

public class MessageCenterActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener{

    private Context mContext;
    private XListView mListView;
    private List<MessageCenterBean> list_messages = new ArrayList<MessageCenterBean>();
    private MessageCenterAdapter adapter_message;
    private Button message_center_back;

    private int countPage = 0;
    private int currtPage = 0;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    String sessionID;
    String userid;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    adapter_message.notifyDataSetChanged();
                    onLoad();
                    break;
                case 1:
                    Toast.makeText(mContext,"数据加载失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MessageCenterActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");


        initView();
        initData();
    }

    public void initView(){

        message_center_back = (Button)findViewById(R.id.message_center_back);
        message_center_back.setOnClickListener(this);

        mListView = (XListView)findViewById(R.id.lv_mess_center);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_message = new MessageCenterAdapter(MessageCenterActivity.this,list_messages);
        mListView.setAdapter(adapter_message);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MessageCentent","position=="+position);
                Intent intent = new Intent(mContext, MessageContentActivity.class);
                String xxid = list_messages.get(position-1).getXxid();
                intent.putExtra("ID",xxid);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.message_center_back :
                finish();
                break;

            default:
                break;
        }
    }
    public void initData(){
        list_messages.clear();
        getXiaoXi();
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mListView.autoRefresh();
        }
    }

    @Override
    public void onRefresh() {
        System.out.println("----------------onRefresh");
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        getXiaoXi();
    }

    @Override
    public void onLoadMore() {
        System.out.println("----------------onLoadMore");
        getXiaoXi();
    }

    private void onLoad() {
        System.out.println("----------------onLoad");
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(Utils.getTime());

        if(currtPage >= countPage){
            mListView.setPullLoadComplete(true);
        }else {
            mListView.setPullLoadComplete(false);
        }
        mListView.setPullLoadEnable(true);
//        mIndex = mIndex +1;
    }


    //获取数据
    public void getXiaoXi(){

        currtPage = currtPage + 1;

        RequestBody requestBody = new FormBody.Builder()
                .add("currtPage",currtPage+"")  .add("num","20").add("userid",userid)
                .add("sessionID",sessionID).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/xiaoxi/getXiaoXi.asp").post(requestBody).build();
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

                        EntityFY<List<MessageCenterBean>> result = gson.fromJson(content, new TypeToken<EntityFY<List<MessageCenterBean>>>() {}.getType());

                        String code = result.getCode();
                        if("0".equals(code)){
                            countPage = result.getCountPage();
                            currtPage = result.getCurrtPage();
                            List<MessageCenterBean> list = result.getData();

                            if(currtPage == 1){
                                list_messages.clear();
                            }
                            if(list != null && list.size() > 0 ){
                                list_messages.addAll(list);
                            }
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }

                    }
                }
            }
        });
    }
}


