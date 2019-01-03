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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.PersonalBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetTopPageActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private Button set_top_back;
    private Button bt_settop_xyb;

    private Spinner tv_settop_zdyf;
    ArrayAdapter<String> zdyf_adapter;
    private List<String> list_zdyf = new ArrayList<String>();

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String sessionID = "";
    String userid = "";
    String gudingyue,gudingyue3,gudingyue6,gudingyue12;

    TextView tv_settop_zhye,tv_settop_xxpd,tv_settop_xxbt;
    String zhye,xxpd,xxbt,xxid;
    String gudingyuefen;

    //加载对话框
    private Dialog mLoadingDialog;

    static int ZHIDING_OK = 0;
    static int ZHIDING_ERROI = 1;
    static int YE_DATA_OK = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    cancelDialog();
                    getYuE();
                    Toast.makeText(mContext,"置顶信息成功",Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    cancelDialog();
                    Toast.makeText(mContext,"您的账户余额不足，无法发布置顶信息，请充值后发布!",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    sharedPreferencesHelper.put("zhye", zhye);
                    tv_settop_zhye.setText(zhye);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_top);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }

        Intent intent = getIntent();
        xxpd = intent.getStringExtra("xxpd");
        xxbt = intent.getStringExtra("xxbt");
        xxid = intent.getStringExtra("xxid");
        sharedPreferencesHelper = new SharedPreferencesHelper(
                SetTopPageActivity.this, "config");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        zhye = (String) sharedPreferencesHelper.getSharedPreference("zhye", "");

        SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                SetTopPageActivity.this, "setting");
        gudingyue = (String) sp_setting.getSharedPreference("gudingyue","");
        gudingyue3 = (String) sp_setting.getSharedPreference("gudingyue3","");
        gudingyue6 = (String) sp_setting.getSharedPreference("gudingyue6","");
        gudingyue12 = (String) sp_setting.getSharedPreference("gudingyue12","");

        initView();
        initData();
    }

    public void initView(){
        set_top_back = (Button) findViewById(R.id.set_top_back);
        set_top_back.setOnClickListener(this);
        bt_settop_xyb = (Button) findViewById(R.id.bt_settop_xyb);
        bt_settop_xyb.setOnClickListener(this);

        tv_settop_zhye = (TextView) findViewById(R.id.tv_settop_zhye);
        tv_settop_xxpd = (TextView) findViewById(R.id.tv_settop_xxpd);
        tv_settop_xxbt = (TextView) findViewById(R.id.tv_settop_xxbt);

        tv_settop_zdyf = (Spinner) findViewById(R.id.tv_settop_zdyf);
        zdyf_adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,list_zdyf);
        tv_settop_zdyf.setAdapter(zdyf_adapter);
        tv_settop_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    gudingyuefen = "1";
                }else if(position == 1){
                    gudingyuefen = "3";
                }else if(position == 2){
                    gudingyuefen = "6";
                }else if(position == 3){
                    gudingyuefen = "12";
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_top_back :
                finish();
                break;
            case R.id.bt_settop_xyb :
                showDialog("");
                guding();
                break;
            default:
                break;
        }
    }

    public void initData(){
        String text1 = "一个月(￥"+gudingyue+"元)";
        String text2 = "三个月(￥"+gudingyue3+"元)";
        String text3 = "六个月(￥"+gudingyue6+"元)";
        String text4 = "十二个月(￥"+gudingyue12+"元)";
        list_zdyf.add(text1);
        list_zdyf.add(text2);
        list_zdyf.add(text3);
        list_zdyf.add(text4);
        zdyf_adapter.notifyDataSetChanged();

        tv_settop_zhye.setText(zhye);
        tv_settop_xxpd.setText(xxpd);
        tv_settop_xxbt.setText(xxbt);
    }


    //获取数据
    public void guding(){

        String pd = "";
        try {
            pd = URLEncoder.encode(xxpd,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                .add("xinxiID",xxid) .addEncoded("pindao",pd)
                .add("gudingyuefen",gudingyuefen).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/guding.asp").post(requestBody).build();
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
                    Log.d("guding","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<PersonalBean> result = gson.fromJson(content, new TypeToken<Entity<PersonalBean>>(){}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(ZHIDING_OK);
                        }else{
                            handler.sendEmptyMessage(ZHIDING_ERROI);
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


