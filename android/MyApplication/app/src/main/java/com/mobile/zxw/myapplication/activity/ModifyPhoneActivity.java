package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.mobile.zxw.myapplication.ui.ViewPagerSlide;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String denglushouji;
    String userid;

    Button modify_phone_back;

    TextView tv_personal_xgmm;
    //加载对话框
    private Dialog mLoadingDialog;

    private TabLayout mTabLayout;
    private ViewPagerSlide mViewPager;
    private LayoutInflater mInflater;

    private View view1, view2;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private List<String> mTitleList = new ArrayList<String>();//页卡标题集合

    TextView bt_modify_phone_one_xyb,bt_modify_phone_two_xyb;

    TextView tv_mpo_lxdh;
    EditText et_mpo_yzm;
    Button bt_mpo_hqyzm;

    EditText et_mpt_lxdh, et_mpt_yzm;
    Button bt_mpt_hqyzm;
    String lxdh2;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    Toast.makeText(mContext,"验证码发送成功",Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(mContext,"验证码发送失败，请重新发送",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    cancelDialog();
                    Toast.makeText(mContext,"提交数据异常，请重新提交",Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    cancelDialog();
                    mViewPager.setCurrentItem(1);
                    break;
                case 4:
                    cancelDialog();
                    Toast.makeText(mContext,"手机验证码不正确",Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    getcode(lxdh2);
                    sendYZM(bt_mpt_hqyzm);
                    break;
                case 6:
                    Toast.makeText(mContext,"这个手机号已经存在",Toast.LENGTH_LONG).show();
                case 7:
                    sharedPreferencesHelper.put("userid", "");
                    sharedPreferencesHelper.put("sessionID", "");
                    sharedPreferencesHelper.put("userName", "");
                    sharedPreferencesHelper.put("denglushouji", "");
                    sharedPreferencesHelper.put("denglumima", "");
                    sharedPreferencesHelper.put("zhye", "");
                    sharedPreferencesHelper.put("xfje", "");
                    sharedPreferencesHelper.put("hyjb", "");
                    cancelDialog();
                    Toast.makeText(mContext,"手机号修改成功，请重新登录",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ModifyPhoneActivity.this,LoginPageActivity.class));
                    APP.getInstance().removeALLActivity();
                    break;
                case 8:
                    cancelDialog();
                    Toast.makeText(mContext,"手机验证码不正确",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);
        mContext = this;
        APP.getInstance().addActivity(ModifyPhoneActivity.this);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ModifyPhoneActivity.this, "config");

        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");

        initView();
//        initData();
    }


    public void initView(){

        modify_phone_back = (Button) findViewById(R.id.modify_phone_back);
        modify_phone_back.setOnClickListener(this);

        mViewPager = (ViewPagerSlide) findViewById(R.id.vp_modify_phone);
        mTabLayout = (TabLayout) findViewById(R.id.tabs_modify_phone);
        mInflater = LayoutInflater.from(this);

        view1 = mInflater.inflate(R.layout.view_modify_phone_one, null);
        view2 = mInflater.inflate(R.layout.view_modify_phone_two, null);

        bt_mpo_hqyzm = (Button) view1.findViewById(R.id.bt_mpo_hqyzm);
        bt_mpo_hqyzm.setOnClickListener(this);
        tv_mpo_lxdh = (TextView) view1.findViewById(R.id.tv_mpo_lxdh);
        tv_mpo_lxdh.setText(denglushouji);
        et_mpo_yzm = (EditText) view1.findViewById(R.id.et_mpo_yzm);
        bt_modify_phone_one_xyb = (TextView) view1.findViewById(R.id.bt_modify_phone_one_xyb);
        bt_modify_phone_one_xyb.setOnClickListener(this);

        bt_mpt_hqyzm = (Button) view2.findViewById(R.id.bt_mpt_hqyzm);
        bt_mpt_hqyzm.setOnClickListener(this);
        et_mpt_lxdh = (EditText) view2.findViewById(R.id.et_mpt_lxdh);
        et_mpt_yzm = (EditText) view2.findViewById(R.id.et_mpt_yzm);
        bt_modify_phone_two_xyb = (TextView) view2.findViewById(R.id.bt_modify_phone_two_xyb);
        bt_modify_phone_two_xyb.setOnClickListener(this);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        //添加页卡标题
        mTitleList.add("one");
        mTitleList.add("two");
        //添加tab选项卡，默认第一个选中
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mAdapter);

        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_modify_phone_one_xyb :
                String lxdh1 = tv_mpo_lxdh.getText().toString().trim();
                if(!Utils.isMobileNO(lxdh1)){
                    Toast.makeText(mContext,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                String yzm = et_mpo_yzm.getText().toString().trim();
                if("".equals(lxdh1) || "".equals(yzm)){
                    Toast.makeText(mContext,"手机号、验证码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog("验证手机验证码");
                phoneYanzheng(lxdh1,yzm);
                break;
            case R.id.bt_modify_phone_two_xyb :
                String lxdh3 = et_mpt_lxdh.getText().toString().trim();
                if(!Utils.isMobileNO(lxdh3)){
                    Toast.makeText(mContext,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                String yzm3 = et_mpt_yzm.getText().toString().trim();
                if("".equals(lxdh3) || "".equals(yzm3)){
                    Toast.makeText(mContext,"手机号、验证码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog("");
                modifyPhone(lxdh3,yzm3);
                break;
            case R.id.bt_mpo_hqyzm :
                String lxdh = tv_mpo_lxdh.getText().toString().trim();
//                if(!Utils.isMobileNO(lxdh)){
//                    Toast.makeText(mContext,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
//                    return;
//                }
                getcode(lxdh);
                sendYZM(bt_mpo_hqyzm);
                break;
            case R.id.bt_mpt_hqyzm :
                lxdh2 = et_mpt_lxdh.getText().toString().trim();

                if(!Utils.isMobileNO(lxdh2)){
                    Toast.makeText(mContext,"请输入正确的电话号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                CheckRegName(lxdh2);
                break;
            case R.id.modify_phone_back:
                finish();
                break;
            default:
                break;
        }
    }

    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }

    }

    //获取验证码
    public void getcode(String sjh){
        RequestBody requestBody = new FormBody.Builder().add("Tel",sjh).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/phoneYanzhengma.asp").post(requestBody).build();
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
                    Log.d("kwwl","res=="+content);
                    if(response.code() == 200){
                        if("0".equals(content)){
                            handler.sendEmptyMessage(0);
                        }else{
                            handler.sendEmptyMessage(1);
                        }
                    }
                }
            }
        });
    }

    //验证验证码
    public void phoneYanzheng(String sjh, String yzm){
        RequestBody requestBody = new FormBody.Builder().add("Tel",sjh).add("shoujyzm",yzm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/phoneYanzheng.asp").post(requestBody).build();
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
                    Log.d("kwwl","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(3);
                        }else{
                            handler.sendEmptyMessage(4);
                        }
                    }
                }else{
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    //检查电话号码是否存在
    public void CheckRegName(String sjh){
        RequestBody requestBody = new FormBody.Builder().add("shouji",sjh).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/CheckRegName.asp").post(requestBody).build();
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
                    Log.d("kwwl","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>(){}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(5);
                        }else{
                            handler.sendEmptyMessage(6);
                        }
                    }
                }else{
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    //
    public void modifyPhone(String sjh,String yzm){
        RequestBody requestBody = new FormBody.Builder().add("shouji",sjh).add("shoujiyzm",yzm)
                .add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/modifyPhone.asp").post(requestBody).build();
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
                    Log.d("kwwl","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>(){}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(7);
                        }else{
                            handler.sendEmptyMessage(8);
                        }
                    }
                }else{
                    handler.sendEmptyMessage(2);
                }
            }
        });
    }

    /**
     * 展示对话框
     * @param content 对话框显示内容
     */
    private void showDialog(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.loading, null);
        TextView loadingText = (TextView)view.findViewById(R.id.tv_load_text);
        loadingText.setText(content);

        mLoadingDialog = new Dialog(this, R.style.MyDialogStyle);
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

    public void sendYZM(TextView textView){
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setEnabled(false);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.s_gray));
                textView.setText("重新发送(" + millisUntilFinished / 1000 + ")");
            }

            @Override
            public void onFinish() {
                textView.setEnabled(true);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                textView.setText("获取验证码");
            }
        }.start();
    }
}


