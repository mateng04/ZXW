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
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplyMoneyActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private EditText et_apply_money_jine,et_apply_money_khyh,et_apply_money_yhzh;
    private TextView tv_apply_money_yue,tv_apply_money_dqzh,tv_apply_money_txjl;

    String jine;
    String yinhang;
    String zhanghao;

    String userName;
    String denglushouji;
    String zhye;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    Button bt_apply_money_xyb,apply_money_back;

    //加载对话框
    private Dialog mLoadingDialog;

    static int YE_DATA_OK = 0;
    static int YE_DATA_ERROR = 1;
    static int TX_DATA_OK = 2;
    static int TX_DATA_ERROR = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    sharedPreferencesHelper.put("zhye", zhye);
                    tv_apply_money_yue.setText(zhye);
                    break;
                case 1:
                    cancelDialog();
                    break;
                case 2:
                    cancelDialog();
                    Toast.makeText(mContext,"提现申请成功，我们会尽快为您处理!",Toast.LENGTH_SHORT).show();
                    getYuE();
                    break;
                case 3:
                    Toast.makeText(mContext,"提现申请失败，请重新申请!",Toast.LENGTH_SHORT).show();
                    getYuE();
                    cancelDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_money);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ApplyMoneyActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        zhye = (String) sharedPreferencesHelper.getSharedPreference("zhye", "");
        userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");

        initView();
        initData();
    }

    public void initView(){
        tv_apply_money_txjl = (TextView) findViewById(R.id.tv_apply_money_txjl);
        tv_apply_money_txjl.setOnClickListener(this);

        tv_apply_money_dqzh = (TextView) findViewById(R.id.tv_apply_money_dqzh);
        tv_apply_money_yue = (TextView) findViewById(R.id.tv_apply_money_yue);

        et_apply_money_jine = (EditText) findViewById(R.id.et_apply_money_jine);
        et_apply_money_khyh = (EditText) findViewById(R.id.et_apply_money_khyh);
        et_apply_money_yhzh = (EditText) findViewById(R.id.et_apply_money_yhzh);

        bt_apply_money_xyb = (Button)findViewById(R.id.bt_apply_money_xyb);
        bt_apply_money_xyb.setOnClickListener(this);
        apply_money_back = (Button)findViewById(R.id.apply_money_back);
        apply_money_back.setOnClickListener(this);
    }

    public void initData(){
        tv_apply_money_dqzh.setText(userName+"("+denglushouji+")");
        tv_apply_money_yue.setText(zhye);
        getYuE();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_apply_money_txjl :
                startActivity(new Intent(ApplyMoneyActivity.this,PresentRecordActivity.class));
                break;
            case R.id.bt_apply_money_xyb :
                jine = et_apply_money_jine.getText().toString().trim();
                yinhang = et_apply_money_khyh.getText().toString().trim();
                zhanghao = et_apply_money_yhzh.getText().toString().trim();

                if("".equals(jine) || "".equals(yinhang) || "".equals(zhanghao)){
                    Toast.makeText(mContext,"内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!"".equals(zhye)){
                    if(Integer.valueOf(jine) > Integer.valueOf(zhye)){
                        Toast.makeText(mContext,"提现金额不能超过账户余额!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                showDialog("正在提交数据");
                zijintixian(jine,yinhang,zhanghao);
                break;
            case R.id.apply_money_back :
                finish();
                break;
            default:
                break;
        }
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

                        }

                    }
                }
            }
        });
    }

    //资金提现
    public void zijintixian(String jine,String yinhang,String zhanghao){

        String yh = "";
        try {
            yh = URLEncoder.encode(yinhang,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder().add("jine",jine)
                .addEncoded("yinhang",yh).add("zhanghao",zhanghao)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/zijintixian.asp").post(requestBody).build();
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

                    Log.d("getQZJobDetails","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<String> result = gson.fromJson(content, new TypeToken<Entity<String>>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(TX_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(TX_DATA_ERROR);
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


