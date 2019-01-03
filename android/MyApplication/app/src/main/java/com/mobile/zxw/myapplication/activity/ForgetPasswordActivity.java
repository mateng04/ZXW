package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Button bt_forget_pass_back;
    private Button bt_pass_one, bt_pass_two, bt_pass_three;
    private TabLayout mTabLayout;
    private ViewPagerSlide mViewPager;
    private LayoutInflater mInflater;

    //加载对话框
    private Dialog mLoadingDialog;

    private View view1, view2, view3;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private List<String> mTitleList = new ArrayList<String>();//页卡标题集合

    OkHttpClient okHttpClient;

    EditText et_passone_sjh,et_passone_verify_code;
    ImageView iv_passone_verify_code;

    private SharedPreferencesHelper sharedPreferencesHelper;
    String sessionID = "";
    String phoneNum = "";

    Button bt_pass_two_yzm;
    EditText et_pass_two_yzm;
    TextView et_pass_two_sjh;

    EditText et_pass_three_mima,et_pass_three_querenmima;
    RequestOptions options = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.NONE);

    static int FIRST_OK = 0;
    static int FIRST_ERROR1 = 1;
    static int FIRST_ERROR2 = 2;
    static int TWO_OK = 3;
    static int TWO_ERROR = 4;
    static int THREE_OK = 5;
    static int THREE_ERROR = 6;
    static int YZM_OK = 7;
    static int YZM_ERROR = 8;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    cancelDialog();
                    et_pass_two_sjh.setText(phoneNum);
                    mViewPager.setCurrentItem(1);
                    break;
                case 1:
                    cancelDialog();
                    Toast.makeText(mContext,"验证码不正确",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    cancelDialog();
                    Toast.makeText(mContext,"手机号不存在",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    cancelDialog();
                    mViewPager.setCurrentItem(2);
                    break;
                case 4:
                    cancelDialog();
                    Toast.makeText(mContext,"手机验证码不正确",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    cancelDialog();
                    Toast.makeText(mContext,"密码修改成功，请登录",Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    cancelDialog();
                    Toast.makeText(mContext,"重置密码失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    cancelDialog();
                    Toast.makeText(mContext,"验证码发送成功",Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    cancelDialog();
                    Toast.makeText(mContext,"验证码发送失败，请重新发送",Toast.LENGTH_LONG).show();
                    break;
                default:

                        break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mContext = this;

        sharedPreferencesHelper = new SharedPreferencesHelper(
                ForgetPasswordActivity.this, "config");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
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
        initPassOneView();
        initData();
    }



    public void initView() {

        bt_forget_pass_back = (Button) findViewById(R.id.bt_forget_pass_back);
        bt_forget_pass_back.setOnClickListener(this);

        mViewPager = (ViewPagerSlide) findViewById(R.id.vp_forget_pass);
        mTabLayout = (TabLayout) findViewById(R.id.tabs_forget_pass);
        mInflater = LayoutInflater.from(this);

        view1 = mInflater.inflate(R.layout.view_page_pass_one, null);
        view2 = mInflater.inflate(R.layout.view_page_pass_two, null);
        view3 = mInflater.inflate(R.layout.view_page_pass_three, null);

        bt_pass_one = (Button) view1.findViewById(R.id.bt_pass_one);
        bt_pass_one.setOnClickListener(this);
        bt_pass_two = (Button) view2.findViewById(R.id.bt_pass_two);
        bt_pass_two.setOnClickListener(this);
        bt_pass_three = (Button) view3.findViewById(R.id.bt_pass_three);
        bt_pass_three.setOnClickListener(this);

        bt_pass_two_yzm = (Button) view2.findViewById(R.id.bt_pass_two_yzm);
        bt_pass_two_yzm.setOnClickListener(this);
        et_pass_two_yzm = (EditText) view2.findViewById(R.id.et_pass_two_yzm);
        et_pass_two_sjh = (TextView) view2.findViewById(R.id.et_pass_two_sjh);

        et_pass_three_mima = (EditText) view3.findViewById(R.id.et_pass_three_mima);
        et_pass_three_querenmima = (EditText) view3.findViewById(R.id.et_pass_three_querenmima);

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);
        //添加页卡标题
        mTitleList.add("one");
        mTitleList.add("two");
        mTitleList.add("three");
        //添加tab选项卡，默认第一个选中
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        //给ViewPager设置适配器
        mViewPager.setAdapter(mAdapter);

        //将TabLayout和ViewPager关联起来
        mTabLayout.setupWithViewPager(mViewPager);
        //给Tabs设置适配器
        mTabLayout.setTabsFromPagerAdapter(mAdapter);
    }

    public void initPassOneView(){
        et_passone_sjh = (EditText) view1.findViewById(R.id.et_passone_sjh);
        et_passone_verify_code = (EditText) view1.findViewById(R.id.et_passone_verify_code);

        iv_passone_verify_code = (ImageView) view1.findViewById(R.id.iv_passone_verify_code);
        iv_passone_verify_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_forget_pass_back:
                finish();
                break;
            case R.id.tv_login_wjmm:

                break;
            case R.id.bt_pass_one:
                getPassWordFist();

                break;
            case R.id.bt_pass_two:
                getPassWordSecond();

                break;
            case R.id.bt_pass_three:
                resetPassWord();
                break;

            case R.id.iv_passone_verify_code:
                getYZMCode();
                break;
            case R.id.bt_pass_two_yzm:
                getSJYZMCode();
                sendYZM(bt_pass_two_yzm);
                break;

            default:
                break;
        }
    }

    public void getYZMCode(){
        String imageurl = HttpUtils.URL+"/appServic/yanzhengma.asp?yzmkey="+sessionID;

        options.signature(new ObjectKey(System.currentTimeMillis()));

        Glide.with(mContext)
                .load(imageurl)
                .apply(options)
                .into(iv_passone_verify_code);
    }

    public void getSJYZMCode(){
        String sjh = et_pass_two_sjh.getText().toString().trim();
        if(sjh.length() != 11){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.isMobileNO(sjh)){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        RequestBody requestBody = new FormBody.Builder().add("Tel",sjh).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/phoneYanzhengma.asp").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("kwwl","res=="+content);
                    if(response.code() == 200){
                        if("0".equals(content)){
                            handler.sendEmptyMessage(YZM_OK);
                        }else{
                            handler.sendEmptyMessage(YZM_ERROR);
                        }
                    }
                }
            }
        });
    }

    public void initData(){
        getYZMCode();
    }

    public void getPassWordFist(){
        String shouji = et_passone_sjh.getText().toString().trim();
        String yzm = et_passone_verify_code.getText().toString().trim();

        if("".equals(shouji) || "".equals(yzm)){
            Toast.makeText(mContext,"手机号、验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(shouji.length() != 11){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.isMobileNO(shouji)){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("");
        phoneNum = shouji;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        RequestBody requestBody = new FormBody.Builder().add("shouji",shouji).add("yzmkey",sessionID).add("yanzm",yzm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getPassWordFist.asp").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("getPassWordFist","getPassWordFist=="+content);
                    //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(FIRST_OK);
                        }else if(result.getCode() == 1){
                            handler.sendEmptyMessage(FIRST_ERROR1);
                        }else if(result.getCode() == 2){
                            handler.sendEmptyMessage(FIRST_ERROR2);
                        }
                    }
                }
            }
        });
    }

    public void getPassWordSecond(){
        String shouji = et_pass_two_sjh.getText().toString().trim();
        String yzm = et_pass_two_yzm.getText().toString().trim();

        if("".equals(yzm)){
            Toast.makeText(mContext,"验证码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("");

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        RequestBody requestBody = new FormBody.Builder().add("Tel",shouji).add("shoujyzm",yzm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/phoneYanzheng.asp").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("getPassWordSecond","getPassWordSecond=="+content);
                    //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(TWO_OK);
                        }else{
                            handler.sendEmptyMessage(TWO_ERROR);
                        }
                    }
                }
            }
        });
    }

    public void resetPassWord(){
        String mima = et_pass_three_mima.getText().toString().trim();
        String querenmima = et_pass_three_querenmima.getText().toString().trim();

        if("".equals(mima) || "".equals(querenmima)){
            Toast.makeText(mContext,"所有内容不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mima.length() < 6 || querenmima.length() < 6){
            Toast.makeText(mContext,"所有密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.checkString(mima) || !Utils.checkString(querenmima)){
            Toast.makeText(mContext,"所有密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mima.equals(querenmima)){
            Toast.makeText(mContext,"两次密码输入不一致)", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog("");

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        RequestBody requestBody = new FormBody.Builder().add("shouji",phoneNum).add("mim",mima).add("mimSure",querenmima).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/resetPassWord.asp").post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("resetPassWord","resetPassWord=="+content);
                    //此时的代码执行在子线程，修改UI的操作请使用handler跳转到UI线程。
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(THREE_OK);
                        }else{
                            handler.sendEmptyMessage(THREE_ERROR);
                        }
                    }
                }
            }
        });
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

    public void sendYZM(Button textView){
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
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                textView.setText("获取验证码");
            }
        }.start();
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


