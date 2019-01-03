package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.LoginBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.EntityLogin;
import com.mobile.zxw.myapplication.ui.VerifyCode;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener,  CompoundButton.OnCheckedChangeListener  {

    private Context mContext;
    VerifyCode verify_code;
    TextView tv_login_wjmm,tv_login_register;
    ImageView iv_login_verify_code;
    EditText et_login_sjh, et_login_mim,  et_login_verify_code;
    OkHttpClient okHttpClient;
    Button bt_login,login_back;
    ToggleButton togglePwd;

    private SharedPreferencesHelper sharedPreferencesHelper;

    static String sessionID = "";
    static String denglushouji = "";
    static String denglumima = "";

    RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE);

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    Toast.makeText(mContext,"登陆成功",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 1:
                    Toast.makeText(mContext,"验证码不正确",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(mContext,"手机密码不正确",Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(mContext,"登陆失败，请重新登陆",Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(mContext,"登陆失败，请检查网络",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        sharedPreferencesHelper = new SharedPreferencesHelper(
                LoginPageActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");
        denglumima = (String) sharedPreferencesHelper.getSharedPreference("denglumima", "");
        if("".equals(sessionID)){
            Random rand = new Random();
            StringBuffer sb=new StringBuffer();
            for (int i=1;i<=32;i++){
                int randNum = rand.nextInt(9)+1;
                String num=randNum+"";
                sb=sb.append(num);
            }
            sessionID = String.valueOf(sb);
//            sharedPreferencesHelper.put("sessionID", sessionID);
        }

        initView();
        initData();
    }

    public void initView(){
//        verify_code = (VerifyCode)findViewById(R.id.verify_code);

        tv_login_wjmm = (TextView) findViewById(R.id.tv_login_wjmm);
        tv_login_wjmm.setOnClickListener(this);
        tv_login_register = (TextView) findViewById(R.id.tv_login_register);
        tv_login_register.setOnClickListener(this);

        et_login_verify_code = (EditText) findViewById(R.id.et_login_verify_code);
        iv_login_verify_code = (ImageView) findViewById(R.id.iv_login_verify_code);
        iv_login_verify_code.setOnClickListener(this);
        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);

        et_login_mim = (EditText) findViewById(R.id.et_login_mim);
        et_login_sjh = (EditText) findViewById(R.id.et_login_sjh);
        et_login_sjh.setText(denglushouji);
        et_login_mim.setText(denglumima);

        togglePwd = (ToggleButton) findViewById(R.id.togglePwd);
        togglePwd.setOnCheckedChangeListener(this);

        login_back = (Button) findViewById(R.id.login_back);
        login_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login :
                initLogin();
                break;
            case R.id.tv_login_wjmm :
                startActivity(new Intent(mContext,ForgetPasswordActivity.class));
                break;
            case R.id.tv_login_register :
                startActivity(new Intent(mContext,RegistrationActivity.class));
                break;
            case R.id.iv_login_verify_code :
                getYZMCode();
                break;
            case R.id.login_back :
                finish();
                break;
            default:
                break;
        }
    }

    public void initData(){
        getYZMCode();
    }

    public void getYZMCode(){
        String imageurl = HttpUtils.URL+"/appServic/yanzhengma.asp?yzmkey="+sessionID;

        options.signature(new ObjectKey(System.currentTimeMillis()));

        Glide.with(mContext)
                .load(imageurl)
                .apply(options)
                .into(iv_login_verify_code);
    }

    public void initLogin(){

        System.out.println("sessionID-----"+sessionID);
        String shouji = et_login_sjh.getText().toString().trim();
        String yzm = et_login_verify_code.getText().toString().trim();
        String mim = et_login_mim.getText().toString().trim();

        if("".equals(shouji) || "".equals(mim) || "".equals(yzm)){
            Toast.makeText(mContext,"手机号、密码、验证码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        RequestBody requestBody = new FormBody.Builder().add("shouji",shouji).add("mim",mim).add("yzmkey",sessionID).add("yanzm",yzm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login.asp").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(4);
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    byte[] b = response.body().bytes(); //获取数据的bytes
                    String content = new String(b, "GB2312");
                    Log.d("sessionID","response.code()=="+response.code());
                    Log.d("sessionID","response.message()=="+response.message());
                    Log.d("sessionID","info=="+content);

                    Gson gson = new Gson();
                    EntityLogin result = gson.fromJson(content, new TypeToken<EntityLogin>() {}.getType());
                    int code = result.getCode();
                    if(code == 0){
                        String keys = result.getKeys();
                        System.out.println("sessionID---"+keys);
                        sharedPreferencesHelper.put("sessionID", keys);
                        LoginBean loginBean = result.getLoginBean();
                        sharedPreferencesHelper.put("userid", loginBean.getUserid());
                        sharedPreferencesHelper.put("userName", loginBean.getUserName());
                        sharedPreferencesHelper.put("denglushouji", shouji);
                        sharedPreferencesHelper.put("denglumima", mim);
                        sharedPreferencesHelper.put("zhye", loginBean.getZhye());
                        sharedPreferencesHelper.put("xfje", loginBean.getXfje());
                        sharedPreferencesHelper.put("hyjb", loginBean.getHyjb());
                        handler.sendEmptyMessage(0);
                    }else if(code == 1){
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
                        sharedPreferencesHelper.put("denglushouji", "");
                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                        handler.sendEmptyMessage(1);
                    }else if(code == 2){
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
                        sharedPreferencesHelper.put("denglushouji", "");
                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                        handler.sendEmptyMessage(2);
                    }
                }else{
                    handler.sendEmptyMessage(3);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            //如果选中，显示密码
            et_login_mim.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            et_login_mim.setSelection(et_login_mim.getText().toString().trim().length());
        }else{
            //否则隐藏密码
            et_login_mim.setTransformationMethod(PasswordTransformationMethod.getInstance());
            et_login_mim.setSelection(et_login_mim.getText().toString().trim().length());
        }


    }
}


