package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.mobile.zxw.myapplication.ui.VerifyCode;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.ui.area.BottomDialog;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.StreamUtils;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener,IListener {

    private Context mContext;

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;

    private final static int REQUESTCODE = 1; // 返回的结果码

    VerifyCode verify_code;
    TextView tv_login_wjmm;
    ImageView iv_login_verify_code;
    EditText et_login_verify_code;
    OkHttpClient okHttpClient;
    Button bt_registration_back;

    private String Select1,Select2,Select3,Select4;

    Spinner sp_registration_rovince,sp_registration_city,sp_registration_county,sp_registration_town;

    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
    private List<AreaBean> townList;

    ArrayAdapter<String> provinceAdapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;

    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;


    RelativeLayout rl_registration_area;
    TextView tv_registration_area_content;
    BottomDialog dialog ;

    RelativeLayout rl_reg_ktyf,rl_reg_zffs;
    Spinner sp_reg_huzl,sp_reg_xb,sp_reg_ktyf,sp_reg_zffs;
    String[]  huzl_arr={"普通会员","VIP会员"};
    String[]  xb_arr={"男","女"};
//    String[]  ktyf_arr={"一个月(￥50元)","三个月(￥90元)","六个月(￥120元)","十二个月(￥180元)"};
    String[]  zffs_arr={"微信","支付宝"};

    private SharedPreferencesHelper sp_setting;
    String yue,yue3,yue6,yue12;
    private List<String> ktyfList;

    int hyzl_flag = 0;  //  0代表普通会员  1代表VIP会员
    int xb_flag = 0;  //  0代表男  1代表女
    int zffs_flag = 0;

    String kaitongyuefen;

    EditText et_reg_hyxm,et_reg_mm,et_reg_qrmm,et_reg_sjh,et_reg_yzm;
    Button bt_registration,bt_reg_hqyzm;

    CheckBox checkBox_reg;
    TextView tv_reg_fwtk, tv_reg_bmxy;

    //加载对话框
    private Dialog mLoadingDialog;

//    String shengId= "";
//    String chengshiId= "";
//    String quxianId= "";
//    String xiangzhenId= "";

    static int DATA_JC = 3;
    static int DATA_OK = 9000;
    static int DATA_ERROR = 4000;

    static int DATA_CANCLE = 6001;  //支付宝支付取消
    static int ZF_DATA_OK = 101;      //微信支付成功
    static int ZF_DATA_OERROR = 104;      //微信支付失败
    static int CZ_DATA_OK = 102;      //微信支付发起成功
    static int CZ_DATA_ERROR = 103;   //微信支付发起失败

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
//                    Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
                    break;
                case 3:
//                    Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
                    provinceAdapter.notifyDataSetChanged();
                    break;
                case 10:
                    if(hyzl_flag == 0){
                        cancelDialog();
                        Toast.makeText(mContext,"注册成功，请登陆",Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        chengweihuiyuan();
                    }

                    break;
                case 11:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"手机号已注册",Toast.LENGTH_LONG).show();
                    break;
                case 12:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"验证码不正确",Toast.LENGTH_LONG).show();
                    break;
                case 9000:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"注册VIP会员成功，请登陆",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 4000:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"注册成普通会员，请登陆后升级VIP",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 6001:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    break;
                case 101:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"注册VIP会员成功，请登陆",Toast.LENGTH_LONG).show();
                    finish();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 102:
//                    bt_registration.setEnabled(true);
//                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 103:
                    bt_registration.setEnabled(true);
                    cancelDialog();
//                    sharedPreferencesHelper.put("zhye", zhye);
                    break;
                case 104:
                    bt_registration.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"注册成普通会员，请登陆后升级VIP",Toast.LENGTH_LONG).show();
                    finish();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mContext = this;

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, APP.APP_ID, false);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }

        sp_setting = new SharedPreferencesHelper(
                mContext, "setting");
        yue = (String) sp_setting.getSharedPreference("yue", "");
        yue3 = (String) sp_setting.getSharedPreference("yue3", "");
        yue6 = (String) sp_setting.getSharedPreference("yue6", "");
        yue12 = (String) sp_setting.getSharedPreference("yue12", "");

        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        initData();
    }


    public void initView(){

        bt_registration_back = (Button) findViewById(R.id.bt_registration_back);
        bt_registration_back.setOnClickListener(this);

        et_reg_hyxm = (EditText) findViewById(R.id.et_reg_hyxm);
        et_reg_mm = (EditText) findViewById(R.id.et_reg_mm);
        et_reg_qrmm = (EditText) findViewById(R.id.et_reg_qrmm);
        et_reg_sjh = (EditText) findViewById(R.id.et_reg_sjh);
        et_reg_yzm = (EditText) findViewById(R.id.et_reg_yzm);

        sp_registration_rovince = (Spinner) findViewById(R.id.sp_registration_rovince);
        sp_registration_city = (Spinner) findViewById(R.id.sp_registration_city);
        sp_registration_county = (Spinner) findViewById(R.id.sp_registration_county);
        sp_registration_town = (Spinner) findViewById(R.id.sp_registration_town);

//        rl_registration_area = (RelativeLayout)findViewById(R.id.rl_registration_area);
//        rl_registration_area.setOnClickListener(this);
//        tv_registration_area_content = (TextView) findViewById(R.id.tv_registration_area_content);


        sp_reg_huzl = (Spinner)findViewById(R.id.sp_reg_huzl);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,huzl_arr);
        sp_reg_huzl.setAdapter(adapter);
        sp_reg_xb = (Spinner)findViewById(R.id.sp_reg_xb);
        ArrayAdapter<String> adapter_xb=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xb_arr);
        sp_reg_xb.setAdapter(adapter_xb);


        ktyfList = new ArrayList<>();
        ktyfList.add("一个月(￥"+yue+"元)");
        ktyfList.add("三个月(￥"+yue3+"元)");
        ktyfList.add("六个月(￥"+yue6+"元)");
        ktyfList.add("十二个月(￥"+yue12+"元)");



        sp_reg_ktyf = (Spinner)findViewById(R.id.sp_reg_ktyf);
        ArrayAdapter<String> adapter_ktyf=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,ktyfList);
        sp_reg_ktyf.setAdapter(adapter_ktyf);

        sp_reg_zffs = (Spinner)findViewById(R.id.sp_reg_zffs);
        ArrayAdapter<String> adapter_zffs=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zffs_arr);
        sp_reg_zffs.setAdapter(adapter_zffs);

        rl_reg_ktyf = (RelativeLayout) findViewById(R.id.rl_reg_ktyf);
        rl_reg_zffs = (RelativeLayout) findViewById(R.id.rl_reg_zffs);

        bt_registration = (Button) findViewById(R.id.bt_registration);
        bt_registration.setOnClickListener(this);
        bt_reg_hqyzm = (Button) findViewById(R.id.bt_reg_hqyzm);
        bt_reg_hqyzm.setOnClickListener(this);


        checkBox_reg = (CheckBox) findViewById(R.id.checkBox_reg);
        tv_reg_fwtk = (TextView) findViewById(R.id.tv_reg_fwtk);
        tv_reg_fwtk.setOnClickListener(this);
        tv_reg_bmxy = (TextView) findViewById(R.id.tv_reg_bmxy);
        tv_reg_bmxy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_registration_back :
                finish();
                break;
//            case R.id.rl_registration_area :
//                dialog.show();
//                break;
            case R.id.bt_registration :
                registration();
                break;
            case R.id.bt_reg_hqyzm :
                String sjh = et_reg_sjh.getText().toString().trim();
                if("".equals(sjh)){
                    Toast.makeText(mContext,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Utils.isMobileNO(sjh)){
                    Toast.makeText(mContext,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                getcode(sjh);
                //测试发送验证码
                sendYZM();
                break;
            case R.id.tv_reg_fwtk :
                Intent intent = new Intent(RegistrationActivity.this, WebViewActivity.class);
                intent.putExtra("from","fwtk");
                startActivity(intent);
                break;
            case R.id.tv_reg_bmxy :
                Intent intent2 = new Intent(RegistrationActivity.this, WebViewActivity.class);
                intent2.putExtra("from","bmxy");
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    public void initData(){

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        provinceAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_registration_rovince.setAdapter(provinceAdapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_registration_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_registration_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_registration_town.setAdapter(townAdapter);

        sp_registration_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_registration_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Select1  = provinceList.get(position).getTid()+"";
                System.out.println("xx--sp_recruit_content_rovince------"+Select1);

                cityList.clear();
                cityList.addAll(getChildList(Integer.valueOf(Select1)));
                city_arr.clear();
                int city_index =0;
                for(int i=0;i<cityList.size();i++){
                    city_arr.add(cityList.get(i).getName());
                    if(Select2 != null && !"".equals(Select2)){
                        if(Integer.valueOf(Select2) == cityList.get(i).getTid()){
                            city_index = i;
                        }
                    }
                }
                System.out.println("xx--sp_recruit_content_rovince------"+city_index);
                citityAdapter.notifyDataSetChanged();
                sp_registration_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_registration_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_registration_county, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Select2  = cityList.get(position).getTid()+"";
                System.out.println("xx--sp_recruit_content_city------"+Select2);

                regionList.clear();
                regionList.addAll(getChildList(Integer.valueOf(Select2))) ;
                region_arr.clear();
                int region_index =0;
                for(int i=0;i<regionList.size();i++){
                    region_arr.add(regionList.get(i).getName());
                    if(Select3 != null && !"".equals(Select3)){
                        if(Integer.valueOf(Select3) == regionList.get(i).getTid()){
                            region_index = i;
                        }
                    }

                }
                regionAdapter.notifyDataSetChanged();
                sp_registration_county.setSelection(region_index);
                System.out.println("xx--sp_recruit_content_city------"+region_index);

                if(regionList.size() == 0){
                    townList.clear();
                    town_arr.clear();
                    townAdapter.notifyDataSetChanged();
                    Select3 = "";
                    Select4 = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_registration_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_registration_town, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Select3  = regionList.get(position).getTid()+"";
                System.out.println("xx--sp_recruit_content_county------"+Select3);
                townList.clear();
                townList.addAll(getChildList(Integer.valueOf(Select3)));
                town_arr.clear();

                int town_index =0;
                for(int i=0;i<townList.size();i++){
                    town_arr.add(townList.get(i).getName());
                    if(Select4 != null && !"".equals(Select4)){
                        if(Integer.valueOf(Select4) == townList.get(i).getTid()){
                            town_index = i;
                        }
                    }
                }
                townAdapter.notifyDataSetChanged();
                sp_registration_town.setSelection(town_index);
                System.out.println("xx--sp_recruit_content_county------"+town_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_registration_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(townList.size() > 0){
                    Select4  = townList.get(position).getTid()+"";
                }else{
                    Select4 = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        sp_reg_huzl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hyzl_flag = position;
                if(position == 0){
                    rl_reg_ktyf.setVisibility(View.GONE);
                    rl_reg_zffs.setVisibility(View.GONE);
                }else {
                    rl_reg_ktyf.setVisibility(View.VISIBLE);
                    rl_reg_zffs.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_reg_xb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xb_flag = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_reg_ktyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        sp_reg_zffs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                zffs_flag = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        new NewsAsyncTask().execute();

    }

    private void registration(){
        String hyzl = huzl_arr[hyzl_flag];
        String xm = et_reg_hyxm.getText().toString().trim();
        String xb = xb_arr[xb_flag];
//        String ssqy = tv_registration_area_content.getText().toString().trim();
        String mm = et_reg_mm.getText().toString().trim();
        String qrmm = et_reg_qrmm.getText().toString().trim();
        String sjh = et_reg_sjh.getText().toString().trim();
        String yzm = et_reg_yzm.getText().toString().trim();
//        String ktyf = ktyfList.get(ktyf_flag);
        String zffs = zffs_arr[zffs_flag];

        if("".equals(zffs) || "".equals(xm) || "".equals(xb)  || "".equals(mm) || "".equals(qrmm) || "".equals(sjh)
                || "".equals(yzm) || "".equals(kaitongyuefen) || "".equals(zffs)){
            Toast.makeText(mContext,"注册内容不能空", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.checkname(xm)){
            Toast.makeText(mContext,"真实姓名必须为中文", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mm.length() < 6 ){
            Toast.makeText(mContext,"登录密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.checkString(mm)){
            Toast.makeText(mContext,"登录密码(只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(qrmm.length() < 6 ){
            Toast.makeText(mContext,"确认密码(6~20位，只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.checkString(qrmm)){
            Toast.makeText(mContext,"确认密码(只能是英文、数字或英文与数字组合)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mm.equals(qrmm)){
            Toast.makeText(mContext,"两次密码输入不一致)", Toast.LENGTH_SHORT).show();
            return;
        }

        if(sjh.length() != 11){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Utils.isMobileNO(sjh)){
            Toast.makeText(mContext,"请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(yzm.length() != 4){
            Toast.makeText(mContext,"请输入正确的验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!checkBox_reg.isChecked()){
            Toast.makeText(mContext,"请阅读并同意正信网服务条款和保密协议", Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog("正在提交数据");

        registerJK();
    }

    public void sendYZM(){
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                bt_reg_hqyzm.setEnabled(false);
                bt_reg_hqyzm.setBackgroundColor(mContext.getResources().getColor(R.color.s_gray));
                bt_reg_hqyzm.setText("重新发送(" + millisUntilFinished / 1000 + ")");

            }

            @Override
            public void onFinish() {
                bt_reg_hqyzm.setEnabled(true);
                bt_reg_hqyzm.setBackgroundColor(mContext.getResources().getColor(R.color.red));
                bt_reg_hqyzm.setText("获取验证码");

            }
        }.start();
    }

    //注册
    public void registerJK(){

        String hyzl = huzl_arr[hyzl_flag];
        String xm = et_reg_hyxm.getText().toString().trim();
        String xb = xb_arr[xb_flag];
//        String ssqy = tv_registration_area_content.getText().toString().trim();
        String mm = et_reg_mm.getText().toString().trim();
        String qrmm = et_reg_qrmm.getText().toString().trim();
        String sjh = et_reg_sjh.getText().toString().trim();
        String yzm = et_reg_yzm.getText().toString().trim();
//        String ktyf = ktyfList.get(ktyf_flag);
        String zffs = zffs_arr[zffs_flag];


        try {
            xm = URLEncoder.encode(xm,"GB2312");
            xb = URLEncoder.encode(xb,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder().add("select1",Select1)
                .add("select2",Select2)
                .add("select3",Select3).add("select4",Select4)
                .addEncoded("xingming",xm).addEncoded("xingbie",xb)
                .add("mim",mm).add("quermm",qrmm)
                .add("shouji",sjh).add("shoujyzm",yzm).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/register.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
                Toast.makeText(mContext,"注册失败，请重新注册",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String content = response.body().string();

                    Log.d("RegistrationActivity","response.code()=="+response.code());
                    Log.d("RegistrationActivity","response.message()=="+response.message());
                    Log.d("RegistrationActivity","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content,  new TypeToken<Entity>() {}.getType());

                        int code = result.getCode();
                        if(code == 0){
                            handler.sendEmptyMessage(10);
                        }else if(code == 1){
                            handler.sendEmptyMessage(11);
                        }else if(code == 2){
                            handler.sendEmptyMessage(12);
                        }
                    }
                }
            }
        });
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
                    Log.d("kwwl","response.code()=="+response.code());
                    Log.d("kwwl","response.message()=="+response.message());
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

    /**
     * 展示对话框
     * @param content 对话框显示内容
     */
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
//            Toast.makeText(mContext,"支付成功",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_OK);
        }else if(tag == 101){
            Toast.makeText(mContext,"支付错误",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_OERROR);
        }else if(tag == 102){
            Toast.makeText(mContext,"用户取消支付",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_OERROR);
        }
    }

    // 创建一个内部类来实现 ,在实现下面内部类之前,需要自定义的Bean对象来封装处理Josn格式的数据
    class  NewsAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            Gson gson = new Gson();

            String data_province = StreamUtils.get(APP.getInstance(), R.raw.area_province);
            Type type = new TypeToken<List<AreaBean>>() { }.getType();
            provinceList.addAll(gson.fromJson(data_province, type));
            for(int i=0;i<provinceList.size();i++){
                province_arr.add(provinceList.get(i).getName());
            }

            String data_all= StreamUtils.get(APP.getInstance(), R.raw.area_all);
            allList = gson.fromJson(data_all, type);
            String data_all2= StreamUtils.get(APP.getInstance(), R.raw.area_all2);
            List allList2 = gson.fromJson(data_all2, type);
            allList.addAll(allList2);

            handler.sendEmptyMessage(DATA_JC);

            return null;
        }
    }

    public List<AreaBean> getChildList(int tid) {

//        System.out.println("--------------------"+allList.size());
        String id = String.valueOf(tid);
        List<AreaBean> childList = new ArrayList<>();
        if (TextUtils.isEmpty(id)) {
            return childList;
        }
        for (AreaBean areaBean : allList) {
            if (id.equals(areaBean.getPid())) {
                childList.add(areaBean);
            }
        }
        return childList;
    }

    public void chengweihuiyuan(){
        bt_registration.setEnabled(false);
        if("微信".equals( zffs_arr[zffs_flag])){
            wx();
        }else if("支付宝".equals( zffs_arr[zffs_flag])){
            zfb();
        }


    }

    public void wx(){

        String sjh = et_reg_sjh.getText().toString().trim();
        String xingming = et_reg_hyxm.getText().toString().trim();
        String zhiffs = "微信";

        RequestBody requestBody = new FormBody.Builder()
                .add("userid","").add("sessionID","")
                .add("xingming",xingming).add("shouji",sjh)
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
        String sjh = et_reg_sjh.getText().toString().trim();
        String xingming = et_reg_hyxm.getText().toString().trim();
        String zhiffs = "支付宝";
//        try {
//            zhiffs =  URLEncoder.encode("支付宝","GB2312");
//            xingming =  URLEncoder.encode(et_reg_hyxm.getText().toString().trim(),"GB2312");
//        } catch (Exception e) {
//        } finally {
//        }

        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        String url = "http://zhengxinw.com/appServic/user/chengweihuiyuan.asp?userid="  + "&xingming=" + xingming+ "&shouji=" + sjh+ "&kaitongyuefen=" + kaitongyuefen+ "&zhiffs=" + zhiffs;
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivityForResult(intent,REQUESTCODE);
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
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }
}


