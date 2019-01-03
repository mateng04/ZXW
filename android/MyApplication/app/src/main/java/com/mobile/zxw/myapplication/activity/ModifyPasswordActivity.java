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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    EditText et_mp_jiumm, et_mp_xinmm, et_mp_quemm;
    Button modify_pass_back,bt_modify_pass;

    //加载对话框
    private Dialog mLoadingDialog;

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
                    sharedPreferencesHelper.put("userid", "");
                    sharedPreferencesHelper.put("sessionID", "");
                    sharedPreferencesHelper.put("userName", "");
                    sharedPreferencesHelper.put("denglushouji", "");
                    sharedPreferencesHelper.put("denglumima", "");
                    sharedPreferencesHelper.put("zhye", "");
                    sharedPreferencesHelper.put("xfje", "");
                    sharedPreferencesHelper.put("hyjb", "");
                    cancelDialog();
                    Toast.makeText(mContext,"密码修改成功，请重新登陆",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ModifyPasswordActivity.this,LoginPageActivity.class));
                    APP.getInstance().removeALLActivity();
//                    finish();
                    break;
                case 1:
                    cancelDialog();
                    Toast.makeText(mContext,"旧密码错误，请输入正确的密码",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    cancelDialog();
                    Toast.makeText(mContext,"请求网络失败，请检查网络",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pass);
        mContext = this;
        APP.getInstance().addActivity(ModifyPasswordActivity.this);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ModifyPasswordActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");


        initView();
    }


    public void initView(){
        et_mp_jiumm = (EditText) findViewById(R.id.et_mp_jiumm);
        et_mp_xinmm = (EditText) findViewById(R.id.et_mp_xinmm);
        et_mp_quemm = (EditText) findViewById(R.id.et_mp_quemm);
        modify_pass_back = (Button) findViewById(R.id.modify_pass_back);
        modify_pass_back.setOnClickListener(this);
        bt_modify_pass = (Button) findViewById(R.id.bt_modify_pass);
        bt_modify_pass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_pass_back :
                finish();
                break;
            case R.id.bt_modify_pass :
                submitData();
                break;
            default:
                break;
        }
    }

    public void submitData(){
        String jiumm = et_mp_jiumm.getText().toString().trim();
        String xinmm = et_mp_xinmm.getText().toString().trim();
        String quemm = et_mp_quemm.getText().toString().trim();
        if("".equals(jiumm) || "".equals(xinmm) || "".equals(quemm)){
            Toast.makeText(mContext,"所有内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(jiumm.length() < 6 || xinmm.length() < 6 || quemm.length() < 6){
            Toast.makeText(mContext,"所有密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.checkString(jiumm) || !Utils.checkString(xinmm) || !Utils.checkString(quemm)){
            Toast.makeText(mContext,"所有密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!xinmm.equals(quemm)){
            Toast.makeText(mContext,"两次密码输入不一致)", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("正在提交数据");
        modifyPassword(jiumm,xinmm,quemm);
    }

    //获取修改密码
    public void modifyPassword(String jiumm,String xinmm,String querxmm){
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID)
                .add("userid",userid).add("jiumm",jiumm)
                .add("xinmm",xinmm).add("querxmm",querxmm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/modifyPassword.asp").post(requestBody).build();
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
                    Log.d("ModifyPasswordActivity","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>(){}.getType());
                        if(result.getCode() == 0){
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
}


