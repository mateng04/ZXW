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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.OrderArr;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.EntityWXorder;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OnlineRechargeActivity extends AppCompatActivity implements View.OnClickListener,IListener {

    private Context mContext;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;
    String zfFlag = "";

    //加载对话框
    private Dialog mLoadingDialog;

    private TextView tv_online_czmx;

    private EditText et_or_jine;
    TextView tv_or_czzh,tv_or_dqye;
    Spinner sp_or_czfs;
    ArrayAdapter<String> czfsAdapter;
    private List<String> czfsList;

    Button bt_or_chongzhi;
    Button online_recharge_back;

    String jine = "";
    String dingdanhao;
    String userName;
    String denglushouji;
    String zhye;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    static int YE_DATA_OK  = 0;
    static int YE_DATA_ERROR = 1;
    static int CZ_DATA_OK = 2;      //微信支付发起成功
    static int CZ_DATA_ERROR = 3;   //微信支付发起失败
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    sharedPreferencesHelper.put("zhye", zhye);
                    tv_or_dqye.setText(zhye);
                    break;
                case 1:
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 2:
                    bt_or_chongzhi.setEnabled(true);
                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 3:
                    bt_or_chongzhi.setEnabled(true);
                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_recharge);
        mContext = this;

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, APP.APP_ID, false);

        sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");
        zhye = (String) sharedPreferencesHelper.getSharedPreference("zhye", "");


        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        initData();
    }



    public void initView(){
        online_recharge_back = (Button) findViewById(R.id.online_recharge_back);
        online_recharge_back.setOnClickListener(this);

        tv_online_czmx = (TextView) findViewById(R.id.tv_online_czmx);
        tv_online_czmx.setOnClickListener(this);

        bt_or_chongzhi = (Button) findViewById(R.id.bt_or_chongzhi);
        bt_or_chongzhi.setOnClickListener(this);
        et_or_jine = (EditText) findViewById(R.id.et_or_jine);

        tv_or_czzh = (TextView) findViewById(R.id.tv_or_czzh);
        tv_or_dqye = (TextView) findViewById(R.id.tv_or_dqye);
        et_or_jine = (EditText) findViewById(R.id.et_or_jine);

        sp_or_czfs = (Spinner) findViewById(R.id.sp_or_czfs);
        czfsList = new ArrayList<>();
        czfsList.add("微信");
        czfsList.add("支付宝");
        czfsAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,czfsList);
        sp_or_czfs.setAdapter(czfsAdapter);
//        sp_or_czfs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Another interface callback
//            }
//        });

    }

    public void initData(){
        tv_or_czzh.setText(userName+"("+denglushouji+")");
        tv_or_dqye.setText(zhye);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getYuE();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.online_recharge_back :
                finish();
                break;
            case R.id.tv_online_czmx :
                startActivity(new Intent(OnlineRechargeActivity.this,RechargeDetailedActivity.class));
                break;
            case R.id.bt_or_chongzhi :
                jine = et_or_jine.getText().toString().trim();
                if("".equals(jine)){
                    Toast.makeText(mContext,"充值金额不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(Integer.valueOf(jine) > 5000 || Integer.valueOf(jine) <= 0){
                    Toast.makeText(mContext,"充值金额必须为大于0小于5000的正整数",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(sp_or_czfs.getSelectedItemPosition() == 0){
                    wxChongZhi();
                }else{
                    System.out.println("11111");
                    chongZhi();
                }

                break;
            default:
                break;
        }
    }

    public void wxChongZhi(){
        bt_or_chongzhi.setEnabled(false);
        showDialog("");

        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Random random = new Random();
        for (int i=0;i<4;i++)
        {
            str += random.nextInt(10);
        }
        dingdanhao = str;

        String dingdanmingcheng = userName+"("+denglushouji+")充值"+jine+"元";
        String ddmc = "";
        try {
            ddmc = URLEncoder.encode(dingdanmingcheng,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder().add("user_ID",userid).add("jine",jine)
        .add("dingdanhao",dingdanhao).add("dingdanmingcheng",dingdanmingcheng).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/m/wechat_chongzhi/alipayapi.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendEmptyMessage(CZ_DATA_ERROR);
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String content = response.body().string();
                    Log.d("获取与支付订单","content=="+content);
                    Log.d("response.code()",response.code()+"");
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityWXorder result = gson.fromJson(content, new TypeToken<EntityWXorder>() {}.getType());
                        Log.d("result.",result.toString()+"");
                        Log.d("result.getSuccess()",result.getSuccess()+"");
                        if(result.getSuccess() == 1){
                            OrderArr orderArr = result.getOrder_arr();
                            PayReq req = new PayReq();
                            req.appId = orderArr.getAppid();
                            req.partnerId = orderArr.getPartnerid();
                            req.prepayId = orderArr.getPrepayid();
                            req.packageValue = "Sign=WXPay";
                            req.nonceStr = orderArr.getNoncestr();
                            req.timeStamp = orderArr.getTimestamp();
                            req.extData = result.getData();
                            req.sign = orderArr.getSign();
                            boolean rest = wxapi.sendReq(req);
//                            Toast.makeText(OnlineRechargeActivity.this, "调起支付结果:" +rest, Toast.LENGTH_LONG).show();
                            Log.d("调起支付结果","-"+rest);
                            handler.sendEmptyMessage(CZ_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(CZ_DATA_ERROR);
                        }

                    }else{
                        handler.sendEmptyMessage(CZ_DATA_ERROR);
                    }
                }else{
                    handler.sendEmptyMessage(CZ_DATA_ERROR);
                }
            }
        });


//        Intent intent = new Intent(this, H5PayDemoActivity.class);
//        Bundle extras = new Bundle();
//        String url = "http://zhengxinw.com/appServic/m/wechat_chongzhi/alipayapi.asp?user_ID="+userid+"&jine="+jine+"&dingdanhao="+dingdanhao+"&dingdanmingcheng="+dingdanmingcheng+"&dingdanmiaoshu="+dingdanmingcheng;
//        extras.putString("url", url);
//        intent.putExtras(extras);
//        startActivity(intent);
    }

    //
    public void chongZhi(){
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Random random = new Random();
        for (int i=0;i<4;i++)
        {
            str += random.nextInt(10);
        }
        dingdanhao = str;

        String dingdanmingcheng = userName+"("+denglushouji+")充值"+jine;
//        String ddmc = "";
//        try {
//            ddmc = URLEncoder.encode(dingdanmingcheng,"GB2312");
//        } catch (Exception e) {
//        } finally {
//        }

        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        String url = "http://zhengxinw.com/appServic/m/alipay_chongzhi/alipayapi.asp?chengshiid=&quxianid=&xiangzhenid=&user_ID="+userid+"&jine="+jine+"&dingdanhao="+dingdanhao+"&dingdanmingcheng="+dingdanmingcheng+"&dingdanmiaoshu="+dingdanmingcheng;
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivity(intent);

//        RequestBody requestBody = new FormBody.Builder().add("user_ID","48").add("jine","10")
//                .add("dingdanhao","CZ201808080707242546").add("dingdanmingcheng","赵世杰(13772373442)充值10")
//                .add("dingdanmiaoshu","赵世杰(13772373442)充值10").build();
//        Request request = new Request.Builder().url(HttpUtils.URL+"/alipay_chongzhi/alipayapi.asp").post(requestBody).build();
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
    public void notifyAllActivity(int tag, String str) {
        if(tag == 100){
            Toast.makeText(mContext,"支付成功",Toast.LENGTH_SHORT).show();
        }else if(tag == 101){
            Toast.makeText(mContext,"支付错误",Toast.LENGTH_SHORT).show();
        }else if(tag == 102){
            Toast.makeText(mContext,"用户取消支付",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }
}


