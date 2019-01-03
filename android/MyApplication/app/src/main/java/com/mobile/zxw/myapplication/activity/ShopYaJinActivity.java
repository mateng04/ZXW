package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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
import com.mobile.zxw.myapplication.bean.ShopYJBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
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

public class ShopYaJinActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    //加载对话框
    private Dialog mLoadingDialog;

    TextView tv_shopyajin_czzh,tv_shopyajin_dqye,tv_shopyajin_yjyj,tv_shopyajin_scyj,tv_shopyajin_xzffy;
    Button bt_shopyajin_xyb;
    Button shop_yajin_back;
    LinearLayout ll_shopyajin_chongzhi;

    String jine = "";
    String userName;
    String denglushouji;
    String zhye;
    String shopyajin;
    int xzffy;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private SharedPreferencesHelper sp_setting;

    ShopYJBean shopYJBean;

    static int YE_DATA_OK = 0;
    static int YE_DATA_ERROR = 1;
    static int YJ_OK = 2;
    static int YJ_ERROR = 3;
    static int KCYJ_OK = 4;
    static int KCYJ_ERROR = 5;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    tv_shopyajin_dqye.setText("¥"+zhye);
                    getShopYajin();
                    break;
                case 1:
//                    sharedPreferencesHelper.put("zhye", zhye);
                    cancelDialog();
                    break;
                case 2:
                    tv_shopyajin_yjyj.setText("¥"+shopYJBean.getShopyajin());
                    if(shopYJBean != null && !"".equals(shopYJBean.getShopyajin())){
                        if(Integer.valueOf(shopYJBean.getShopyajin()) < Integer.valueOf(shopyajin)){
                            xzffy = Integer.valueOf(shopyajin)-Integer.valueOf(shopYJBean.getShopyajin());
                            System.out.println("xzffy--"+xzffy);
                            System.out.println(tv_shopyajin_xzffy);
                            tv_shopyajin_xzffy.setText("¥"+xzffy);

                            if(Integer.valueOf(zhye) >= xzffy){
                                bt_shopyajin_xyb.setVisibility(View.VISIBLE);
                                ll_shopyajin_chongzhi.setVisibility(View.GONE);
                            }else{
                                bt_shopyajin_xyb.setVisibility(View.GONE);
                                ll_shopyajin_chongzhi.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    cancelDialog();
                    break;
                case 3:
                    cancelDialog();
                    Toast.makeText(mContext,"余额不足",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(mContext,"商城押金支付完成，您可以发布商品了!",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 5:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_yajin);
        mContext = this;

        sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");

        sp_setting = new SharedPreferencesHelper(
                ShopYaJinActivity.this, "setting");
        shopyajin = (String) sp_setting.getSharedPreference("shopyajin","");


        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }

        initView();
        initData();
    }



    public void initView(){
        tv_shopyajin_czzh = (TextView) findViewById(R.id.tv_shopyajin_czzh);
        tv_shopyajin_dqye = (TextView) findViewById(R.id.tv_shopyajin_dqye);
        tv_shopyajin_yjyj = (TextView) findViewById(R.id.tv_shopyajin_yjyj);
        tv_shopyajin_scyj = (TextView) findViewById(R.id.tv_shopyajin_scyj);
        tv_shopyajin_xzffy = (TextView) findViewById(R.id.tv_shopyajin_xzffy);

        bt_shopyajin_xyb = (Button) findViewById(R.id.bt_shopyajin_xyb);
        bt_shopyajin_xyb.setOnClickListener(this);

        shop_yajin_back = (Button) findViewById(R.id.shop_yajin_back);
        shop_yajin_back.setOnClickListener(this);
        ll_shopyajin_chongzhi = (LinearLayout) findViewById(R.id.ll_shopyajin_chongzhi);
        ll_shopyajin_chongzhi.setOnClickListener(this);
    }

    public void initData(){
        tv_shopyajin_czzh.setText(userName+"("+denglushouji+")");
        tv_shopyajin_scyj.setText("¥"+shopyajin);

        showDialog("数据加载中");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getYuE();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shop_yajin_back :
                finish();
                break;
            case R.id.ll_shopyajin_chongzhi :
                mContext.startActivity(new Intent(mContext, OnlineRechargeActivity.class));
                break;
            case R.id.bt_shopyajin_xyb :
                kouchuyajin();
                break;
            default:
                break;
        }
    }

    //扣除押金
    public void kouchuyajin(){
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid).add("jine",xzffy+"").build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/kouchuyajin.asp").post(requestBody).build();
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
                    Log.d("kouchuyajin","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityYE result = gson.fromJson(content, new TypeToken<EntityYE>() {}.getType());
                        if(result.getCode() == 0){
                            zhye = result.getYue()+"";
                            handler.sendEmptyMessage(KCYJ_OK);
                        }else{
                            handler.sendEmptyMessage(KCYJ_ERROR);
                        }
                    }
                }
            }
        });
    }

    //获取余额
    public void getYuE(){

        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getYuE.asp").post(requestBody).build();
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
                    Log.d("getYuE","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityYE result = gson.fromJson(content, new TypeToken<EntityYE>() {}.getType());
                        if(result.getCode() == 0){
                            zhye = result.getYue()+"";
                            handler.sendEmptyMessage(YE_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(YE_DATA_ERROR);
                        }

                    }
                }
            }
        });
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
}


