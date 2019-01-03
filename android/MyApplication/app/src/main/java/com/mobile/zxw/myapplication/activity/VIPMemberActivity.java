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
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.OrderArr;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityWXorder;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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

public class VIPMemberActivity extends AppCompatActivity implements View.OnClickListener,IListener {

    private Context mContext;
    private final static int REQUESTCODE = 1; // 返回的结果码

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;

    //加载对话框
    private Dialog mLoadingDialog;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper,sp_setting;
    String userName;
    String denglushouji;
    String zhye;
    String hyjb;

    String yue,yue3,yue6,yue12;
    private List<String> ktyfList;
    ArrayAdapter<String> ktyfAdapter;
    String kaitongyuefen;

    private List<String> zffsList;
    ArrayAdapter<String> zffsAdapter;
    String zhiffs;

    Button bt_vip_member_back;
    TextView tv_vip_member_isshow;
    TextView tv_vip_member_dqzh;
    Spinner sp_vip_member_ktyf,sp_vip_member_czfs;
    Button bt_vip_member_xyb;

    static int DATA_OK = 9000;
    static int DATA_ERROR = 4000;
    static int DATA_CANCLE = 6001;  //支付宝支付取消
    static int ZF_DATA_OK = 1;      //微信支付成功
    static int CZ_DATA_OK = 2;      //微信支付发起成功
    static int CZ_DATA_ERROR = 3;   //微信支付发起失败
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 9000:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
                    if("VIP".equals(hyjb)){
                        Toast.makeText(VIPMemberActivity.this,"VIP续费成功", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(VIPMemberActivity.this,"VIP购买成功,请重新登陆", Toast.LENGTH_LONG).show();
                        sharedPreferencesHelper.put("sessionID", "");
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
                        sharedPreferencesHelper.put("denglushouji","");
                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                        startActivity(new Intent(mContext,LoginPageActivity.class));
                        finish();
                    }


                    break;
                case 4000:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
                    break;
                case 6001:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
                    break;
                case 1:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
                    if("VIP".equals(hyjb)){
                        Toast.makeText(VIPMemberActivity.this,"VIP续费成功", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(VIPMemberActivity.this,"VIP购买成功,请重新登陆", Toast.LENGTH_LONG).show();
                        sharedPreferencesHelper.put("sessionID", "");
                        sharedPreferencesHelper.put("userid", "");
                        sharedPreferencesHelper.put("userName", "");
                        sharedPreferencesHelper.put("denglushouji","");
                        sharedPreferencesHelper.put("denglumima", "");
                        sharedPreferencesHelper.put("zhye", "");
                        sharedPreferencesHelper.put("xfje", "");
                        sharedPreferencesHelper.put("hyjb", "");
                        startActivity(new Intent(mContext,LoginPageActivity.class));
                        finish();
                    }
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 2:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 3:
                    bt_vip_member_xyb.setEnabled(true);
                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_member);
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
        hyjb = (String) sharedPreferencesHelper.getSharedPreference("hyjb", "");

        sp_setting = new SharedPreferencesHelper(
                mContext, "setting");
        yue = (String) sp_setting.getSharedPreference("yue", "");
        yue3 = (String) sp_setting.getSharedPreference("yue3", "");
        yue6 = (String) sp_setting.getSharedPreference("yue6", "");
        yue12 = (String) sp_setting.getSharedPreference("yue12", "");

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
        bt_vip_member_back = (Button)findViewById(R.id.bt_vip_member_back);
        bt_vip_member_back.setOnClickListener(this);

        tv_vip_member_isshow = (TextView) findViewById(R.id.tv_vip_member_isshow);
        tv_vip_member_dqzh = (TextView) findViewById(R.id.tv_vip_member_dqzh);
        sp_vip_member_ktyf = (Spinner) findViewById(R.id.sp_vip_member_ktyf);
        sp_vip_member_czfs = (Spinner) findViewById(R.id.sp_vip_member_czfs);

        bt_vip_member_xyb = (Button)findViewById(R.id.bt_vip_member_xyb);
        bt_vip_member_xyb.setOnClickListener(this);
    }

    public void initData(){
        tv_vip_member_dqzh.setText(userName+"("+denglushouji+")");

        if("".equals(hyjb)){
            tv_vip_member_isshow.setVisibility(View.GONE);
        }else if("普通".equals(hyjb)){
            tv_vip_member_isshow.setVisibility(View.GONE);
        }else if("VIP".equals(hyjb)){
            tv_vip_member_isshow.setVisibility(View.VISIBLE);
        }

        ktyfList = new ArrayList<>();
        ktyfList.add("一个月(￥"+yue+"元)");
        ktyfList.add("三个月(￥"+yue3+"元)");
        ktyfList.add("六个月(￥"+yue6+"元)");
        ktyfList.add("十二个月(￥"+yue12+"元)");
        ktyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,ktyfList);
        sp_vip_member_ktyf.setAdapter(ktyfAdapter);

        //工作经验
        sp_vip_member_ktyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(position == 0){
                   kaitongyuefen = 1+"";
               }else if(position == 1){
                   kaitongyuefen = 3+"";
               }else if(position == 2){
                   kaitongyuefen = 6+"";
               }else if(position == 3){
                   kaitongyuefen = 12+"";
               }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        zffsList = new ArrayList<>();
        zffsList.add("微信");
        zffsList.add("支付宝");
        zffsAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zffsList);
        sp_vip_member_czfs.setAdapter(zffsAdapter);
        //工作经验
        sp_vip_member_czfs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zhiffs = zffsList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_vip_member_back :
                finish();
                break;
            case R.id.bt_vip_member_xyb :
//                chengweihuiyuan();
//                String xingming = "";
//                try {
//                    zhiffs =  URLEncoder.encode(zhiffs,"GB2312");
//                    xingming =  URLEncoder.encode(userName,"GB2312");
//                } catch (Exception e) {
//                } finally {
//                }
                bt_vip_member_xyb.setEnabled(false);
                showDialog("");
                if("微信".equals(zhiffs)){
                    wx();
                }else if("支付宝".equals(zhiffs)){
                    zfb();
                }


                break;
            default:
                break;
        }
    }

    public void wx(){

        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid).add("sessionID",sessionID)
                .add("xingming",userName).add("shouji",denglushouji)
                .add("kaitongyuefen",kaitongyuefen).add("zhiffs",zhiffs).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/m/wechat_vip/chengweihuiyuan.asp").post(requestBody).build();
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
    }

    public void zfb(){
        System.out.println("userid-----------------"+userid);
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        String url = "http://zhengxinw.com/appServic/user/chengweihuiyuan.asp?userid=" + userid + "&sessionID=" + sessionID + "&xingming=" + userName+ "&shouji=" + denglushouji+ "&kaitongyuefen=" + kaitongyuefen+ "&zhiffs=" + zhiffs;
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivityForResult(intent,REQUESTCODE);
    }

    public void chengweihuiyuan(){

        String xingming = "";
        try {
            zhiffs =  URLEncoder.encode(zhiffs,"GB2312");
            xingming =  URLEncoder.encode(userName,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid)
                .add("xingming",xingming).add("shouji",denglushouji)
                .add("kaitongyuefen",kaitongyuefen).add("zhiffs",zhiffs).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/chengweihuiyuan.asp").post(requestBody).build();
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
                    Log.d("chengweihuiyuan","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
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

    // 为了获取结果
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 super.onActivityResult(requestCode, resultCode, data);
                 // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
                 // operation succeeded. 默认值是-1

         int result = data.getExtras().getInt("result",100);//得到新Activity 关闭后返回的数据
         System.out.println("result----"+result);
         handler.sendEmptyMessage(result);
     }

    @Override
    public void notifyAllActivity(int tag, String str) {
        if(tag == 100){
//            Toast.makeText(mContext,"支付成功",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_OK);
        }else if(tag == 101){
            Toast.makeText(mContext,"支付错误",Toast.LENGTH_SHORT).show();
        }else if(tag == 102){
            Toast.makeText(mContext,"用户取消支付",Toast.LENGTH_SHORT).show();
        }
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
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }
}


