package com.mobile.zxw.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.LoginBean;
import com.mobile.zxw.myapplication.bean.SettingBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityLogin;
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

public class WelcomeActivity extends AppCompatActivity  {
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    String sessionID;
    String userid;
    String shouji;
    String mim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏以及状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /**标题是属于View的，所以窗口所有的修饰部分被隐藏后标题依然有效,需要去掉标题**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                WelcomeActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        shouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");
        mim = (String) sharedPreferencesHelper.getSharedPreference("denglumima", "");

        initData();

        boolean isFirstOpen = (Boolean) sharedPreferencesHelper.getSharedPreference("isFirstOpen", true);

        if(isFirstOpen){
            sharedPreferencesHelper.put("isFirstOpen", false);
            Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
        }else{
            System.out.println("updateSessionID-sessionID--"+sessionID);
            System.out.println("updateSessionID-userid--"+userid);
            System.out.println("updateSessionID-shouji--"+shouji);
            System.out.println("updateSessionID-mim--"+mim);
            if(!"".equals(sessionID) && !"".equals(userid) && !"".equals(shouji) && !"".equals(mim)){
                updateSessionID();
            }else {

                handler.sendEmptyMessageDelayed(0,1000);
            }

        }


    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            getHome();
            super.handleMessage(msg);
        }
    };

    public void getHome(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void initData(){
        getxitongshezhi();
    }

    //获取系统设置
    public void getxitongshezhi(){
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/getxitongshezhi.asp").post(requestBody).build();
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
                    Log.d("getxitongshezhi","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<SettingBean> result = gson.fromJson(content, new TypeToken<Entity<SettingBean>>() {}.getType());
                        SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                                WelcomeActivity.this, "setting");
                        if(result.getCode() == 0){
                            SettingBean settingBean = result.getData();
                            sp_setting.put("ci",settingBean.getCi());
                            sp_setting.put("yue",settingBean.getYue());
                            sp_setting.put("yue3",settingBean.getYue3());
                            sp_setting.put("yue6",settingBean.getYue6());
                            sp_setting.put("yue12",settingBean.getYue12());
                            sp_setting.put("indexadyue",settingBean.getIndexadyue());
                            sp_setting.put("indexadyue3",settingBean.getIndexadyue3());
                            sp_setting.put("indexadyue6",settingBean.getIndexadyue6());
                            sp_setting.put("indexadyue12",settingBean.getIndexadyue12());
                            sp_setting.put("adyue",settingBean.getAdyue());
                            sp_setting.put("adyue3",settingBean.getAdyue3());
                            sp_setting.put("adyue6",settingBean.getAdyue6());
                            sp_setting.put("adyue12",settingBean.getAdyue12());
                            sp_setting.put("gudingyue",settingBean.getGudingyue());
                            sp_setting.put("gudingyue3",settingBean.getGudingyue3());
                            sp_setting.put("gudingyue6",settingBean.getGudingyue6());
                            sp_setting.put("gudingyue12",settingBean.getGudingyue12());
                            sp_setting.put("shopfabu",settingBean.getShopfabu());
                            sp_setting.put("tel1",settingBean.getTel1());
                            sp_setting.put("shopyajin",settingBean.getShopyajin());

                        }else{

                        }
                    }
                }
            }
        });
    }

    //更新登陆session
    public void updateSessionID(){
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid)
                .add("shouji",shouji) .add("mim",mim).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/updateSessionID.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sharedPreferencesHelper.put("userid", "");
                sharedPreferencesHelper.put("userName", "");
                sharedPreferencesHelper.put("denglushouji", "");
                sharedPreferencesHelper.put("denglumima", "");
                sharedPreferencesHelper.put("zhye", "");
                sharedPreferencesHelper.put("xfje", "");
                sharedPreferencesHelper.put("hyjb", "");

                Log.i("TAG",e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] b = response.body().bytes(); //获取数据的bytes
                    String content = new String(b, "GB2312");
                    Log.d("updateSessionID","info=="+content);

                    Gson gson = new Gson();
                    EntityLogin result = gson.fromJson(content, new TypeToken<EntityLogin>() {}.getType());
                    int code = result.getCode();
                    if(code == 0){
                        LoginBean loginBean = result.getLoginBean();
                        sharedPreferencesHelper.put("userid", loginBean.getUserid());
                        sharedPreferencesHelper.put("userName", loginBean.getUserName());
                        sharedPreferencesHelper.put("zhye", loginBean.getZhye());
                        sharedPreferencesHelper.put("xfje", loginBean.getXfje());
                        sharedPreferencesHelper.put("hyjb", loginBean.getHyjb());
                    }else if(code == 1){
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
//                        sharedPreferencesHelper.put("denglushouji", "");
//                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                    }
//                    handler.sendEmptyMessage(0);
                    handler.sendEmptyMessageDelayed(0,1000);
                }else{
//                    handler.sendEmptyMessage(0);
                    handler.sendEmptyMessageDelayed(0,1000);
                }
            }
        });
    }
}
