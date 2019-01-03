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
import com.mobile.zxw.myapplication.bean.JobPageDetailsBean;
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

public class JobPageDetailsActivity extends AppCompatActivity  implements View.OnClickListener{

    private Context mContext;
    String leixing;
    String xinxiID;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    JobPageDetailsBean jpdBean;

    TextView tv_jp_qwyx_title;
    TextView tv_jp_title, tv_jp_time, tv_jp_cishu, tv_jp_xm, tv_jp_xb, tv_jp_mz,
            tv_jp_jg, tv_jp_cssj, tv_jp_zgxl, tv_jp_qwzw, tv_jp_qwyx, tv_jp_gznx, tv_jp_qwdq, tv_jp_content,
            tv_jp_lxdh, tv_jp_lxry, tv_jp_kxsj , tv_jp_ckdh;

    RecyclerView recycler_jp_de;
    List<String> list_sd = new ArrayList<>();
    RecyclerViewNoBgAdapter recyclerViewNoBgAdapter;

    LinearLayout ll_jd_kxsj;
    Button bt_jp_back;

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
                    tv_jp_ckdh.setVisibility(View.GONE);
                    tv_jp_lxdh.setText(jpdBean.getLxdh());
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
        setContentView(R.layout.activity_job_page_details);

        mContext = JobPageDetailsActivity.this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                JobPageDetailsActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xinxiID = getIntent().getStringExtra("ID");
        leixing = getIntent().getStringExtra("leixing");

        initView();
        initData();
    }


    public void initView() {
        tv_jp_title = (TextView)findViewById(R.id.tv_jp_title);
        tv_jp_time = (TextView)findViewById(R.id.tv_jp_time);
        tv_jp_cishu = (TextView)findViewById(R.id.tv_jp_cishu);
        tv_jp_xm = (TextView)findViewById(R.id.tv_jp_xm);
        tv_jp_xb = (TextView)findViewById(R.id.tv_jp_xb);
        tv_jp_mz = (TextView)findViewById(R.id.tv_jp_mz);
        tv_jp_jg = (TextView)findViewById(R.id.tv_jp_jg);
        tv_jp_cssj = (TextView)findViewById(R.id.tv_jp_cssj);
        tv_jp_zgxl = (TextView)findViewById(R.id.tv_jp_zgxl);
        tv_jp_qwzw = (TextView)findViewById(R.id.tv_jp_qwzw);
        tv_jp_qwyx = (TextView)findViewById(R.id.tv_jp_qwyx);
        tv_jp_gznx = (TextView)findViewById(R.id.tv_jp_gznx);
        tv_jp_qwdq = (TextView)findViewById(R.id.tv_jp_qwdq);
        tv_jp_content = (TextView)findViewById(R.id.tv_jp_content);
        tv_jp_lxdh = (TextView)findViewById(R.id.tv_jp_lxdh);
        tv_jp_lxry = (TextView)findViewById(R.id.tv_jp_lxry);
        tv_jp_ckdh = (TextView)findViewById(R.id.tv_jp_ckdh);
        tv_jp_kxsj = (TextView)findViewById(R.id.tv_jp_kxsj);

        tv_jp_qwyx_title = (TextView)findViewById(R.id.tv_jp_qwyx_title);

        ll_jd_kxsj = (LinearLayout)findViewById(R.id.ll_jd_kxsj);
        if("全职简历".equals(leixing)){
            ll_jd_kxsj.setVisibility(View.GONE);
            tv_jp_qwyx_title.setText("期望月薪：");
        }else{
            ll_jd_kxsj.setVisibility(View.VISIBLE);
            tv_jp_qwyx_title.setText("期望日薪：");
        }

        tv_jp_lxdh = (TextView)findViewById(R.id.tv_jp_lxdh);
        tv_jp_lxdh.setOnClickListener(this);
        tv_jp_ckdh = (TextView)findViewById(R.id.tv_jp_ckdh);
        tv_jp_ckdh.setOnClickListener(this);

        bt_jp_back = (Button)findViewById(R.id.bt_jp_back);
        bt_jp_back.setOnClickListener(this);

        recycler_jp_de = (RecyclerView)findViewById(R.id.recycler_jp_de);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recycler_jp_de.setLayoutManager(layoutManager);
        //绑定适配器
        recyclerViewNoBgAdapter = new RecyclerViewNoBgAdapter(this, list_sd);
        recycler_jp_de.setAdapter(recyclerViewNoBgAdapter);
    }

    public void initData() {
        showDialog("正在加载数据");
        if("全职简历".equals(leixing)){
            getQZJobDetails();
        }else{
            getJZJobDetails();
        }
    }

    public void setData() {
        if(jpdBean != null){
            tv_jp_title.setText(jpdBean.getRecruitTile());
            String time[] = jpdBean.getUpdateTime().split(" ");
            tv_jp_time.setText(time[0]);
            tv_jp_cishu.setText(jpdBean.getBrowsing());
            tv_jp_xm.setText(jpdBean.getXm());
            tv_jp_xb.setText(jpdBean.getXb());
            tv_jp_mz.setText(jpdBean.getMz());
            tv_jp_jg.setText(jpdBean.getJg());
            tv_jp_cssj.setText(jpdBean.getCssj());
            tv_jp_zgxl.setText(jpdBean.getZgxl());
            tv_jp_qwzw.setText(jpdBean.getQwzw());
            tv_jp_qwyx.setText(jpdBean.getQwyx());
            tv_jp_gznx.setText(jpdBean.getGznx());
            tv_jp_qwdq.setText(jpdBean.getQwdq());
            tv_jp_kxsj.setText(jpdBean.getKxsj());
            tv_jp_content.setText(jpdBean.getZwms().replace("<Br>","\n"));
            tv_jp_lxdh.setText(jpdBean.getLxdh());
            tv_jp_lxry.setText(jpdBean.getXm());
            tv_jp_kxsj.setText(jpdBean.getKxsj());

            if("no".equals(jpdBean.getIsshowphone()) && jpdBean.getLxdh() != null && !"".equals(jpdBean.getLxdh())){
                tv_jp_ckdh.setVisibility(View.VISIBLE);
                String dh = jpdBean.getLxdh();
                String start = dh.substring(0,3);
                String end = dh.substring(dh.length()-4,dh.length());
                tv_jp_lxdh.setText(start+"****"+end);
            }else{
                tv_jp_ckdh.setVisibility(View.GONE);
                tv_jp_ckdh.setText(jpdBean.getLxdh());
            }

            list_sd.clear();
            List<String> tupian = jpdBean.getTupian();
            if(tupian != null){
                for(String tp : tupian){
                    list_sd.add(HttpUtils.URL+"/"+tp);
                }
            }
            recyclerViewNoBgAdapter.notifyDataSetChanged();
        }
    }

    //获取招聘详情数据
    public void getQZJobDetails(){

        RequestBody requestBody = new FormBody.Builder().add("id",xinxiID)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getQZJobDetails.asp").post(requestBody).build();
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

                    Log.d("getQZJobDetails","response.code()=="+response.code());
                    Log.d("getQZJobDetails","response.message()=="+response.message());
                    Log.d("getQZJobDetails","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<JobPageDetailsBean> result = gson.fromJson(content, new TypeToken<Entity<JobPageDetailsBean>>() {}.getType());
                        if(result.getCode() == 0){
                            jpdBean = result.getData();
                            Log.d("getQZJobDetails","jpdBean=="+jpdBean.getRecruitTile());
                            handler.sendEmptyMessage(QZ_DATA_OK);
                        }

                    }
                }
            }
        });
    }

    //获取招聘详情数据
    public void getJZJobDetails(){

        RequestBody requestBody = new FormBody.Builder().add("id",xinxiID)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getJZJobDetails.asp").post(requestBody).build();
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

                    Log.d("getJZJobDetails","response.code()=="+response.code());
                    Log.d("getJZJobDetails","response.message()=="+response.message());
                    Log.d("getJZJobDetails","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<JobPageDetailsBean> result = gson.fromJson(content, new TypeToken<Entity<JobPageDetailsBean>>() {}.getType());

                        if(result.getCode() == 0){
                            jpdBean = result.getData();
                            handler.sendEmptyMessage(JZ_DATA_OK);
                        }

                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_jp_back :
                finish();
                break;
            case R.id.tv_jp_lxdh :
                String content = tv_jp_lxdh.getText().toString().trim();
                if(!content.contains("*")) {
                    call("tel:"+content);
                }
                break;
            case R.id.tv_jp_ckdh :
                AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
                builder.setTitle("提示" ) ;
                builder.setMessage("你是普通会员，查看电话号码将扣除"+jpdBean.getShoufei()+"元。" ) ;
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

    //查看电话号码
    public void chakandianhua(){
        String lx = "";
        String title = "";
        try {
            title =  URLEncoder.encode(jpdBean.getRecruitTile(),"GB2312");
        } catch (Exception e) {
        } finally {
        }

        if("全职简历".equals(leixing)){
            lx = "2";
        }else{
            lx = "3";
        }

        System.out.println("xinxiID---"+xinxiID+"---title---"+title+"---lx--+lx");

        RequestBody requestBody = new FormBody.Builder().add("xinxiid",xinxiID)
                .add("sessionID",sessionID).add("userid",userid)
                .add("jine",jpdBean.getShoufei()).addEncoded("title",title)
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

                    Log.d("chakandianhua","response.code()=="+response.code());
                    Log.d("chakandianhua","response.message()=="+response.message());
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
            case 10112: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this,"请允许拨号权限后再试",Toast.LENGTH_SHORT).show();
                } else {//成功
                    String content = tv_jp_lxdh.getText().toString().trim();
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
        if(checkReadPermission(Manifest.permission.CALL_PHONE,10112)){
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }

    }
}


