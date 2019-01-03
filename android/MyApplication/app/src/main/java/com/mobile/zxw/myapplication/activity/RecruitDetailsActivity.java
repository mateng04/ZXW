package com.mobile.zxw.myapplication.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.mobile.zxw.myapplication.bean.RecruitDetailsBean;
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

public class RecruitDetailsActivity extends AppCompatActivity  implements View.OnClickListener{

    private Context mContext;

    String leixing;
    String xinxiID;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    RecruitDetailsBean rdBean;

    TextView tv_rd_yxqk_title;
    TextView tv_rd_title, tv_rd_time, tv_rd_cishu, tv_rd_zwmc, tv_rd_xbyq, tv_rd_yxqk,
            tv_rd_gzjy, tv_rd_nnfw, tv_rd_zdxl, tv_rd_mzyq, tv_rd_zprs, tv_rd_yxqz, tv_rd_gzdd,
            tv_rd_content, tv_rd_lxdh, tv_rd_lxry, tv_rd_gzsj, tv_rd_ckdh;

    RecyclerView recycler_re_de;
    List<String> list_sd = new ArrayList<>();
    RecyclerViewNoBgAdapter recyclerViewNoBgAdapter;

    LinearLayout ll_rd_gzsj;
    Button bt_rd_back;

    //加载对话框
    private Dialog mLoadingDialog;

    String zhye;

    static int QZ_DATA_OK = 0;
    static int JZ_DATA_OK = 1;
    static int CKDH_DATA_OK = 2;
    static int CKDH_DATA_ERROR = 3;
    static int YE_DATA_OK = 4;
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
                    setData();
                    cancelDialog();
                    break;
                case 2:
                    getYuE();
                    Toast.makeText(mContext,"扣费成功，电话号码将完整显示!",Toast.LENGTH_SHORT).show();
                    tv_rd_ckdh.setVisibility(View.GONE);
                    tv_rd_lxdh.setText(rdBean.getLxdh());
                    break;
                case 3:
                    Toast.makeText(mContext,"您的账户余额不足，请登录会员中心充值!",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_details);
        mContext = RecruitDetailsActivity.this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                RecruitDetailsActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xinxiID = getIntent().getStringExtra("ID");
        leixing = getIntent().getStringExtra("leixing");

        initView();
        initData();
    }


    public void initView() {
        tv_rd_title = (TextView)findViewById(R.id.tv_rd_title);
        tv_rd_time = (TextView)findViewById(R.id.tv_rd_time);
        tv_rd_cishu = (TextView)findViewById(R.id.tv_rd_cishu);
        tv_rd_zwmc = (TextView)findViewById(R.id.tv_rd_zwmc);
        tv_rd_xbyq = (TextView)findViewById(R.id.tv_rd_xbyq);
        tv_rd_yxqk_title = (TextView)findViewById(R.id.tv_rd_yxqk_title);
        tv_rd_yxqk = (TextView)findViewById(R.id.tv_rd_yxqk);
        tv_rd_gzjy = (TextView)findViewById(R.id.tv_rd_gzjy);
        tv_rd_nnfw = (TextView)findViewById(R.id.tv_rd_nnfw);
        tv_rd_zdxl = (TextView)findViewById(R.id.tv_rd_zdxl);
        tv_rd_mzyq = (TextView)findViewById(R.id.tv_rd_mzyq);
        tv_rd_zprs = (TextView)findViewById(R.id.tv_rd_zprs);
        tv_rd_yxqz = (TextView)findViewById(R.id.tv_rd_yxqz);
        tv_rd_gzdd = (TextView)findViewById(R.id.tv_rd_gzdd);
        tv_rd_content = (TextView)findViewById(R.id.tv_rd_content);
        tv_rd_lxdh = (TextView)findViewById(R.id.tv_rd_lxdh);
        tv_rd_lxry = (TextView)findViewById(R.id.tv_rd_lxry);
        tv_rd_gzsj = (TextView)findViewById(R.id.tv_rd_gzsj);

        ll_rd_gzsj = (LinearLayout)findViewById(R.id.ll_rd_gzsj);
        if("全职招聘".equals(leixing)){
            ll_rd_gzsj.setVisibility(View.GONE);

            tv_rd_yxqk_title.setText("月薪情况：");
        }else{
            ll_rd_gzsj.setVisibility(View.VISIBLE);

            tv_rd_yxqk_title.setText("薪资标准：");
        }
        tv_rd_ckdh = (TextView)findViewById(R.id.tv_rd_ckdh);
        tv_rd_ckdh.setOnClickListener(this);
        bt_rd_back = (Button)findViewById(R.id.bt_rd_back);
        bt_rd_back.setOnClickListener(this);

        tv_rd_lxdh.setOnClickListener(this);

        recycler_re_de = (RecyclerView)findViewById(R.id.recycler_re_de);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recycler_re_de.setLayoutManager(layoutManager);
        //绑定适配器
        recyclerViewNoBgAdapter = new RecyclerViewNoBgAdapter(this, list_sd);
        recycler_re_de.setAdapter(recyclerViewNoBgAdapter);
    }

    public void initData() {
        showDialog("正在加载数据");
        if("全职招聘".equals(leixing)){
            getQZRecruitmentDetails();
        }else{
            getJZRecruitmentDetails();
        }
    }

    public void setData() {
        if(rdBean != null){
            tv_rd_title.setText(rdBean.getRecruitTile());
            String time[] = rdBean.getUpdateTime().split(" ");
            tv_rd_time.setText(time[0]);
            tv_rd_cishu.setText(rdBean.getBrowsing());
            tv_rd_zwmc.setText(rdBean.getZwmc());
            tv_rd_xbyq.setText(rdBean.getXbyq());
            if(rdBean.getXinzidanwei() != null){
                tv_rd_yxqk.setText(rdBean.getYxqk()+rdBean.getXinzidanwei());
            }else{
                tv_rd_yxqk.setText(rdBean.getYxqk());
            }
            tv_rd_gzjy.setText(rdBean.getGzjy());
            tv_rd_nnfw.setText(rdBean.getNlfw());
            tv_rd_zdxl.setText(rdBean.getZdxl());
            tv_rd_mzyq.setText(rdBean.getMzyq());
            tv_rd_zprs.setText(rdBean.getZprs());
            tv_rd_yxqz.setText(rdBean.getYxqz());
            tv_rd_gzdd.setText(rdBean.getGzdd());
            tv_rd_content.setText(rdBean.getZwms().replace("<Br>","\n"));
//            tv_rd_lxdh.setText(rdBean.getLxdh());
            tv_rd_lxry.setText(rdBean.getLxry());
            tv_rd_gzsj.setText(rdBean.getGzsj());

            if("no".equals(rdBean.getIsshowphone())){
                tv_rd_ckdh.setVisibility(View.VISIBLE);
                String dh = rdBean.getLxdh();
                String start = dh.substring(0,3);
                String end = dh.substring(dh.length()-4,dh.length());
                tv_rd_lxdh.setText(start+"****"+end);
            }else{
                tv_rd_ckdh.setVisibility(View.GONE);
                tv_rd_lxdh.setText(rdBean.getLxdh());
            }

            list_sd.clear();
            List<String> tupian = rdBean.getTupian();
            if(tupian != null){
                for(String tp : tupian){
                    list_sd.add(HttpUtils.URL+"/"+tp);
                }
            }
            recyclerViewNoBgAdapter.notifyDataSetChanged();

        }
    }

    //获取招聘详情数据
    public void getQZRecruitmentDetails(){

        RequestBody requestBody = new FormBody.Builder().add("id",xinxiID)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getQZRecruitmentDetails.asp").post(requestBody).build();
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

                    Log.d("RecruitDetailsActivity","response.code()=="+response.code());
                    Log.d("RecruitDetailsActivity","response.message()=="+response.message());
                    Log.d("RecruitDetailsActivity","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<RecruitDetailsBean> result = gson.fromJson(content, new TypeToken<Entity<RecruitDetailsBean>>() {}.getType());
                        rdBean = result.getData();

                        handler.sendEmptyMessage(QZ_DATA_OK);
                    }
                }
            }
        });
    }

    //获取招聘详情数据
    public void getJZRecruitmentDetails(){

        RequestBody requestBody = new FormBody.Builder().add("id",xinxiID)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getJZRecruitmentDetails.asp").post(requestBody).build();
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

                    Log.d("getJZRecruitmentDetails","response.code()=="+response.code());
                    Log.d("getJZRecruitmentDetails","response.message()=="+response.message());
                    Log.d("getJZRecruitmentDetails","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<RecruitDetailsBean> result = gson.fromJson(content, new TypeToken<Entity<RecruitDetailsBean>>() {}.getType());
                        rdBean = result.getData();

                        handler.sendEmptyMessage(JZ_DATA_OK);
                    }
                }
            }
        });
    }

    //查看电话号码
    public void chakandianhua(){
        String title = "";
        try {
            title =  URLEncoder.encode(rdBean.getRecruitTile(),"GB2312");
        } catch (Exception e) {
        } finally {
        }
        String lx = "";
        if("全职招聘".equals(leixing)){
            lx = "0";
        }else{
            lx = "1";
        }
        System.out.println("xinxiID---"+xinxiID+"---title---"+title+"---lx--+lx");

        RequestBody requestBody = new FormBody.Builder().add("xinxiid",xinxiID)
                .add("sessionID",sessionID).add("userid",userid)
                .add("jine",rdBean.getShoufei()).addEncoded("title",title)
                .add("leixing",lx).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/chakandianhua.asp").post(requestBody).build();
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
                    Log.d("chakandianhua","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(CKDH_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(CKDH_DATA_ERROR);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rd_back :
                finish();
                break;
            case R.id.tv_rd_lxdh :
                String content = tv_rd_lxdh.getText().toString().trim();
                if(!content.contains("*")) {
                    call("tel:"+content);
                }
                break;
            case R.id.tv_rd_ckdh :
                AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
                builder.setTitle("提示" ) ;
                builder.setMessage("你是普通会员，查看电话号码将扣除"+rdBean.getShoufei()+"元。" ) ;
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                        chakandianhua();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
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

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission,int request_code) {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) {//已有权限
            flag = true;
        } else {//申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10111: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    String content = tv_rd_lxdh.getText().toString().trim();
                    call("tel:"+content);
                }
                break;
        }
    }
    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone){
        if(checkReadPermission(Manifest.permission.CALL_PHONE,10111)){
            Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse(telPhone));
            startActivity(intent);
        }

    }
}


