package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.MessageContentBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageContentActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    String xinxiID;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private Button message_content_back;
    private TextView tv_mc_title,tv_mc_content,tv_mc_time;

    MessageContentBean messageContentBean;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    setData();
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
        setContentView(R.layout.activity_message_content);
        mContext = MessageContentActivity.this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MessageContentActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xinxiID = getIntent().getStringExtra("ID");

        initView();
        initData();

    }

    public void initView(){
        message_content_back = (Button) findViewById(R.id.message_content_back);
        message_content_back.setOnClickListener(this);

        tv_mc_title = (TextView) findViewById(R.id.tv_mc_title);
        tv_mc_content = (TextView) findViewById(R.id.tv_mc_content);
        tv_mc_time = (TextView) findViewById(R.id.tv_mc_time);
    }

    public void initData(){
        getXXContent();
    }

    public void setData(){
        if(messageContentBean != null){
            tv_mc_title.setText(messageContentBean.getXxbt());
            tv_mc_content.setText(messageContentBean.getXxnr().replace("<p>","").replace("</p>",""));
            tv_mc_time.setText(messageContentBean.getXxsj());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {      //判断标志位
            case R.id.message_content_back:
                finish();
                break;
        }
    }

    //获取数据
    public void getXXContent(){

        RequestBody requestBody = new FormBody.Builder()
                .add("xxid",xinxiID).add("userid",userid)
                .add("sessionID",sessionID).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/xiaoxi/getXXContent.asp").post(requestBody).build();
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
                    Log.d("MessageContentActivity","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<MessageContentBean> result = gson.fromJson(content, new TypeToken<Entity<MessageContentBean>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            messageContentBean = result.getData();

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


