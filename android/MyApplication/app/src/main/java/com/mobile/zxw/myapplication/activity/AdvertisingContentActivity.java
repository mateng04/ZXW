package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.RecyclerViewNoBgAdapter;
import com.mobile.zxw.myapplication.bean.AdverContentBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
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

public class AdvertisingContentActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    List<String> list_sd = new ArrayList<>();
    RecyclerView recycler_advertising_content;

    TextView tv_ad_content_title,tv_ad_content_time,tv_ad_content_count,tv_ad_content_content;

    String adID = "";
    AdverContentBean adverContentBean;
    RecyclerViewNoBgAdapter recyclerViewNoBgAdapter;

    Button advertising_content_back;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    //加载对话框
    private Dialog mLoadingDialog;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    setData();
                    cancelDialog();
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
        setContentView(R.layout.activity_advertising_content);
        mContext = this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                AdvertisingContentActivity.this, "config");
        adID = getIntent().getStringExtra("adID");

        initView();
        initData();
    }

    public void initView(){

        advertising_content_back = (Button) findViewById(R.id.advertising_content_back);
        advertising_content_back.setOnClickListener(this);

        tv_ad_content_title = (TextView) findViewById(R.id.tv_ad_content_title);
        tv_ad_content_time = (TextView) findViewById(R.id.tv_ad_content_time);
        tv_ad_content_count = (TextView) findViewById(R.id.tv_ad_content_count);
        tv_ad_content_content = (TextView) findViewById(R.id.tv_ad_content_content);

        recycler_advertising_content = (RecyclerView) findViewById(R.id.recycler_advertising_content);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recycler_advertising_content.setLayoutManager(layoutManager);
        //绑定适配器
        recyclerViewNoBgAdapter = new RecyclerViewNoBgAdapter(this, list_sd);
        recycler_advertising_content.setAdapter(recyclerViewNoBgAdapter);
    }

    public void initData() {
        showDialog("正在加载数据");
        getADContent();
    }

    public void setData() {
        tv_ad_content_title.setText(adverContentBean.getTitle());
        tv_ad_content_time.setText(adverContentBean.getDaoqishijian());
        tv_ad_content_count.setText(adverContentBean.getClick());
        String content = adverContentBean.getContent().replace("<Br>","\n")
                .replace("&nbsp;","  ");
        tv_ad_content_content.setText(content);

        list_sd.clear();
        List<String> tupian = adverContentBean.getImgs();
        if(tupian != null){
            for(String tp : tupian){
                System.out.println("tupian------------"+tp);
                list_sd.add(HttpUtils.URL+"/"+tp);
            }
        }
        recyclerViewNoBgAdapter.notifyDataSetChanged();
    }

    //获取数据
    public void getADContent(){

        RequestBody requestBody = new FormBody.Builder()
                .add("adID",adID).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getADContent.asp").post(requestBody).build();
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
                    Log.d("getXXContent","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<AdverContentBean> result = gson.fromJson(content, new TypeToken<Entity<AdverContentBean>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            adverContentBean = result.getData();

                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }

                    }
                }
            }
        });
    }

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advertising_content_back :
                finish();
                break;
            default:
                break;
        }
    }
}


