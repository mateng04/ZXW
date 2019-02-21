package com.mobile.zxw.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.ImagePickerAdapter;
import com.mobile.zxw.myapplication.bean.BigZhiweiBean;
import com.mobile.zxw.myapplication.bean.SmallZhiweiBean;
import com.mobile.zxw.myapplication.bean.YueXiClassBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.until.GlideImageLoader;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.StreamUtils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReleaseRecruitActivity extends AppCompatActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener  {

    private Context mContext;
    String hyjb = "";   //  普通   VIP
    TextView tv_personal_xgmm, tv_personal_xgsjh;
    Button bt_recruit_manage_fbzp;

    TextView tv_rele_manage_cz;     //充值

    Button bt_release_recruit_back;

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private File file;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 9;               //允许选择图片最大数

    String sessionID;
    String userid;
    String userName;
    String denglushouji;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private SharedPreferencesHelper sp_setting;
    String putongfabu = "0";
    private String gudingyue,gudingyue3,gudingyue6,gudingyue12;
    Calendar calendar;

    //加载对话框
    private Dialog mLoadingDialog;

    private int type = 0;   //全职 兼职 标记
    private String Select1,Select2,Select3,Select4;
    private String bigzhiweiclass,smallzhiweiclass;
    private String xbyq,xlyq,gzjy,mizu,yuexin,nlfwk,nlfwj,gudingyuefen;

    List<BigZhiweiBean> dl_List = new ArrayList<>();
    List<BigZhiweiBean> dl_lishi_List;
    List<SmallZhiweiBean> xl_List = new ArrayList<>();
    List<SmallZhiweiBean> xl_lishi_List;
    List<YueXiClassBean> yx_List = new ArrayList<>();
    List<YueXiClassBean> yx_lishi_List;

    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
    private List<AreaBean> townList;

    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;
    private List<String>  dl_arr;
    private List<String>  xl_arr;
    private List<String> xbyqList;
    private List<String> xlyqList;
    private List<String>  gzjyList;
    private List<String> minzuList;
    private List<String> yuexinList;
    private List<String> nlfwkList;
    private List<String> nlfwjList;
    private List<String> zdyfList;

    Spinner sp_rele_recruit_type;
    Spinner sp_rele_recruit_rovince,sp_rele_recruit_city,sp_rele_recruit_county,sp_rele_recruit_town;
    Spinner sp_rele_recruit_zwdl,sp_rele_recruit_zwxl;
    TextView tv_recruit_yxqx_content;
    EditText et_rele_recruit_title,et_rele_recruit_address,et_rele_recruit_peoples;
    Spinner sp_rele_recruit_xbyq,sp_rele_recruit_xlyq,sp_rele_recruit_gzjy,sp_rele_recruit_mzyq,sp_rele_recruit_yxqk;
    Spinner sp_rele_recruit_nlfwk, sp_rele_recruit_nlfwj;
    EditText et_rele_recruit_zwms;
    Button bt_rele_recruit_fbzp;
    EditText et_rele_recruit_lxr,et_rele_recruit_lxdh;
    Spinner sp_rele_recruit_zdyf;

    RelativeLayout rl_rele_recruit_yxqk;
    RelativeLayout rl_rele_recruit_kxsj;
    LinearLayout ll_job_manage_xzqk;
    EditText et_job_manage_xzqk;

    CheckBox job_manage_checkBox1,job_manage_checkBox2,job_manage_checkBox3,job_manage_checkBox4,job_manage_checkBox5,
    job_manage_checkBox6,job_manage_checkBox7;

    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> provinceAdapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;
    ArrayAdapter<String> dlAdapter;
    ArrayAdapter<String> xlAdapter;
    ArrayAdapter<String> xbyqAdapter;
    ArrayAdapter<String> xueliAdapter;
    ArrayAdapter<String> gzjyAdapter;
    ArrayAdapter<String> mzAdapter;
    ArrayAdapter<String> yuexinAdapter;
    ArrayAdapter<String> nlfwkAdapter;
    ArrayAdapter<String> nlfwjAdapter;
    ArrayAdapter<String> zdyfAdapter;

    private List<String> type_arr;

    static int DATA_JC = 2;
    static int DL_OK = 3; //大类获取成功
    static int DL_ERROR = 4; //大类获取失败
    static int XL_OK = 5; //小类获取成功
    static int XL_ERROR = 6; //小类获取失败
    static int YX_OK = 7; //月薪获取成功
    static int YX_ERROR = 8; //月薪获取失败
    static int UPLOAD_OK = 9; //上传图片成功
    static int UPLOAD_ERROR = 10; //上传图片失败
    static int FB_OK = 11; //发布招聘成功
    static int FB_STATE1 = 12; //您的账户余额不足，无法发布信息，请先充值
    static int FB_STATE2 = 13; //您的账户余额不足，无法发布置顶信息，请充值后发布!
    static int FB_STATE3 = 14; //余额不足无法置顶到所给时间，充值后再置顶
    static int FB_STATE4 = 15; //登录超时
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
//                    setData();
//                    cancelDialog();
                    break;
                case 1:
                    break;
                case 2:
                    provinceAdapter.notifyDataSetChanged();
                    citityAdapter.notifyDataSetChanged();
                    regionAdapter.notifyDataSetChanged();
                    townAdapter.notifyDataSetChanged();
                    xbyqAdapter.notifyDataSetChanged();
                    xueliAdapter.notifyDataSetChanged();
                    gzjyAdapter.notifyDataSetChanged();
                    mzAdapter.notifyDataSetChanged();
                    nlfwkAdapter.notifyDataSetChanged();
                    nlfwjAdapter.notifyDataSetChanged();
                    zdyfAdapter.notifyDataSetChanged();
                    cancelDialog();
                    break;
                case 3:
                    dl_List.addAll(dl_lishi_List);
                    setDLScreenData();
//                    setXLScreenData();
                    break;
                case 4:
//                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 5:
                    xl_List.clear();
                    xl_arr.clear();
                    xl_List.addAll(xl_lishi_List);
                    setXLScreenData();
                    break;
                case 6:
//                    adapter_qzzp.notifyDataSetChanged();
                    break;
                case 7:
                    yx_List.addAll(yx_lishi_List);
                    setPriceScreenData();
                    break;
                case 8:
//                    adapter_qzzp.notifyDataSetChanged();
                case 9:
                   Toast.makeText(mContext, "上传成功", Toast.LENGTH_SHORT).show();
                    break;
                case 10:
//                    adapter_qzzp.notifyDataSetChanged();
                case 11:
                    cancelDialog();
                    Toast.makeText(mContext, "发布招聘成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 12:
                    cancelDialog();
                    Toast.makeText(mContext, "您的账户余额不足，无法发布信息，请先充值", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    cancelDialog();
                    Toast.makeText(mContext, "您的账户余额不足，无法发布置顶信息，请充值后发布!", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    cancelDialog();
                    Toast.makeText(mContext, "余额不足无法置顶到所给时间，充值后再置顶", Toast.LENGTH_SHORT).show();
                case 15:
                    cancelDialog();
                    Toast.makeText(mContext, "登录超时", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_recruit);
        mContext = this;

        calendar = Calendar.getInstance();
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ReleaseRecruitActivity.this, "config");

        //会员级别
        hyjb = (String) sharedPreferencesHelper.getSharedPreference("hyjb", "");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");
        userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");

        sp_setting = new SharedPreferencesHelper(
                ReleaseRecruitActivity.this, "setting");
        gudingyue = (String) sp_setting.getSharedPreference("gudingyue", "");
        gudingyue3 = (String) sp_setting.getSharedPreference("gudingyue3", "");
        gudingyue6 = (String) sp_setting.getSharedPreference("gudingyue6", "");
        gudingyue12 = (String) sp_setting.getSharedPreference("gudingyue12", "");

        putongfabu = (String) sp_setting.getSharedPreference("putongfabu", "0");

        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();

        initView();
        initData();
    }



    public void initView(){

        bt_release_recruit_back = (Button) findViewById(R.id.bt_release_recruit_back);
        bt_release_recruit_back.setOnClickListener(this);

        sp_rele_recruit_type = (Spinner) findViewById(R.id.sp_rele_recruit_type);

        sp_rele_recruit_rovince = (Spinner) findViewById(R.id.sp_rele_recruit_rovince);
        sp_rele_recruit_city = (Spinner) findViewById(R.id.sp_rele_recruit_city);
        sp_rele_recruit_county = (Spinner) findViewById(R.id.sp_rele_recruit_county);
        sp_rele_recruit_town = (Spinner) findViewById(R.id.sp_rele_recruit_town);

        sp_rele_recruit_zwdl = (Spinner) findViewById(R.id.sp_rele_recruit_zwdl);
        sp_rele_recruit_zwxl = (Spinner) findViewById(R.id.sp_rele_recruit_zwxl);

        tv_recruit_yxqx_content = (TextView) findViewById(R.id.tv_recruit_yxqx_content);
        tv_recruit_yxqx_content.setOnClickListener(this);


        et_rele_recruit_title = (EditText) findViewById(R.id.et_rele_recruit_title);
        et_rele_recruit_address = (EditText) findViewById(R.id.et_rele_recruit_address);
        et_rele_recruit_peoples = (EditText) findViewById(R.id.et_rele_recruit_peoples);

        sp_rele_recruit_xbyq = (Spinner) findViewById(R.id.sp_rele_recruit_xbyq);
        sp_rele_recruit_xlyq = (Spinner) findViewById(R.id.sp_rele_recruit_xlyq);
        sp_rele_recruit_gzjy = (Spinner) findViewById(R.id.sp_rele_recruit_gzjy);
        sp_rele_recruit_mzyq = (Spinner) findViewById(R.id.sp_rele_recruit_mzyq);
        sp_rele_recruit_yxqk = (Spinner) findViewById(R.id.sp_rele_recruit_yxqk);

        sp_rele_recruit_nlfwk = (Spinner) findViewById(R.id.sp_rele_recruit_nlfwk);
        sp_rele_recruit_nlfwj = (Spinner) findViewById(R.id.sp_rele_recruit_nlfwj);

        et_rele_recruit_zwms = (EditText) findViewById(R.id.et_rele_recruit_zwms);
        et_rele_recruit_zwms.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.et_rele_recruit_zwms:
                        // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                        if (canVerticalScroll(et_rele_recruit_zwms))
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);//告诉父view，你可以处理了
                        }
                }
                return false;
            }
        });


        bt_rele_recruit_fbzp = (Button) findViewById(R.id.bt_rele_recruit_fbzp);
        bt_rele_recruit_fbzp.setOnClickListener(this);

        et_rele_recruit_lxr = (EditText) findViewById(R.id.et_rele_recruit_lxr);
        if(userName != ""){
            et_rele_recruit_lxr.setText(userName.substring(0,1)+"先生");
        }
        et_rele_recruit_lxdh = (EditText) findViewById(R.id.et_rele_recruit_lxdh);
        et_rele_recruit_lxdh.setText(denglushouji);

        sp_rele_recruit_zdyf = (Spinner) findViewById(R.id.sp_rele_recruit_zdyf);

        rl_rele_recruit_yxqk = (RelativeLayout) findViewById(R.id.rl_rele_recruit_yxqk);
        rl_rele_recruit_kxsj = (RelativeLayout) findViewById(R.id.rl_rele_recruit_kxsj);
        ll_job_manage_xzqk = (LinearLayout) findViewById(R.id.ll_job_manage_xzqk);
        et_job_manage_xzqk = (EditText) findViewById(R.id.et_job_manage_xzqk);

        job_manage_checkBox1 = (CheckBox) findViewById(R.id.job_manage_checkBox1);
        job_manage_checkBox2 = (CheckBox) findViewById(R.id.job_manage_checkBox2);
        job_manage_checkBox3 = (CheckBox) findViewById(R.id.job_manage_checkBox3);
        job_manage_checkBox4 = (CheckBox) findViewById(R.id.job_manage_checkBox4);
        job_manage_checkBox5 = (CheckBox) findViewById(R.id.job_manage_checkBox5);
        job_manage_checkBox6 = (CheckBox) findViewById(R.id.job_manage_checkBox6);
        job_manage_checkBox7 = (CheckBox) findViewById(R.id.job_manage_checkBox7);

        tv_rele_manage_cz = (TextView) findViewById(R.id.tv_rele_manage_cz);
        tv_rele_manage_cz.setOnClickListener(this);

        if("".equals(hyjb)){
            et_rele_recruit_lxr.setEnabled(false);
            et_rele_recruit_lxdh.setEnabled(false);
            if(!"".equals(putongfabu) && !"0".equals(putongfabu)){
                bt_rele_recruit_fbzp.setText("发布招聘(发布信息将扣除"+putongfabu+"元)");
            }
        }else if("普通".equals(hyjb)){
            et_rele_recruit_lxr.setEnabled(false);
            et_rele_recruit_lxdh.setEnabled(false);
            if(!"".equals(putongfabu) && !"0".equals(putongfabu)){
                bt_rele_recruit_fbzp.setText("发布招聘(发布信息将扣除"+putongfabu+"元)");
            }
        }else if("VIP".equals(hyjb)){
            et_rele_recruit_lxr.setEnabled(true);
            et_rele_recruit_lxdh.setEnabled(true);
            bt_rele_recruit_fbzp.setText("发布招聘");
        }

        setAdapter();
        setAdapterOnItemClick();
    }

    public void initData(){
        showDialog("数据加载中");

        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        tv_recruit_yxqx_content.setText(year+"-"+(month+2)+"-"+day);

        new NewsAsyncTask().execute();
        //从服务器获取大类
        BigZhiweiClass();
        YueXiClass();
    }

    private void setAdapter(){
        type_arr = new ArrayList<>();
        type_arr.add("全职招聘");
        type_arr.add("兼职招聘");
        typeAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,type_arr);
        sp_rele_recruit_type.setAdapter(typeAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        provinceAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_rele_recruit_rovince.setAdapter(provinceAdapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_rele_recruit_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_rele_recruit_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_rele_recruit_town.setAdapter(townAdapter);

        dl_arr = new ArrayList<>();
        dlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,dl_arr);
        sp_rele_recruit_zwdl.setAdapter(dlAdapter);

        xl_arr = new ArrayList<>();
        xlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xl_arr);
        sp_rele_recruit_zwxl.setAdapter(xlAdapter);

        xbyqList = new ArrayList<>();
        xbyqAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xbyqList);
        sp_rele_recruit_xbyq.setAdapter(xbyqAdapter);

        xlyqList = new ArrayList<>();
        xueliAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xlyqList);
        sp_rele_recruit_xlyq.setAdapter(xueliAdapter);

        gzjyList = new ArrayList<>();
        gzjyAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,gzjyList);
        sp_rele_recruit_gzjy.setAdapter(gzjyAdapter);

        minzuList = new ArrayList<>();
        mzAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,minzuList);
        sp_rele_recruit_mzyq.setAdapter(mzAdapter);

        yuexinList = new ArrayList<>();
        yuexinAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,yuexinList);
        sp_rele_recruit_yxqk.setAdapter(yuexinAdapter);

        nlfwkList = new ArrayList<>();
        nlfwkAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,nlfwkList);
        sp_rele_recruit_nlfwk.setAdapter(nlfwkAdapter);

        nlfwjList = new ArrayList<>();
        nlfwjAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,nlfwjList);
        sp_rele_recruit_nlfwj.setAdapter(nlfwjAdapter);

        zdyfList = new ArrayList<>();
        zdyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zdyfList);
        sp_rele_recruit_zdyf.setAdapter(zdyfAdapter);
    }

    private void setAdapterOnItemClick(){
        System.out.println("-----------setAdapterOnItemClick:");
        //简历选择
        sp_rele_recruit_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                if( type == 1){
                    rl_rele_recruit_yxqk.setVisibility(View.GONE);
                    ll_job_manage_xzqk.setVisibility(View.VISIBLE);
                    rl_rele_recruit_kxsj.setVisibility(View.VISIBLE);
                }else{
                    rl_rele_recruit_yxqk.setVisibility(View.VISIBLE);
                    ll_job_manage_xzqk.setVisibility(View.GONE);
                    rl_rele_recruit_kxsj.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_rele_recruit_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Select1  = provinceList.get(position).getTid()+"";
                cityList.clear();
                cityList.addAll(getChildList(Integer.valueOf(Select1)));
                city_arr.clear();
                for(int i=0;i<cityList.size();i++){
                    city_arr.add(cityList.get(i).getName());
                }
                citityAdapter.notifyDataSetChanged();
                sp_rele_recruit_city.setSelection(0);

                Select2  = cityList.get(0).getTid()+"";
                regionList.clear();
                regionList.addAll(getChildList(Integer.valueOf(Select2))) ;
                region_arr.clear();
                for(int i=0;i<regionList.size();i++){
                    region_arr.add(regionList.get(i).getName());
                }
                regionAdapter.notifyDataSetChanged();
                sp_rele_recruit_county.setSelection(0);

                if(regionList.size() > 0){
                    Select3  = regionList.get(0).getTid()+"";
                    townList.clear();
                    townList.addAll(getChildList(Integer.valueOf(Select3)));
                    town_arr.clear();
                    for(int i=0;i<townList.size();i++){
                        town_arr.add(townList.get(i).getName());
                    }
                    townAdapter.notifyDataSetChanged();
                    sp_rele_recruit_town.setSelection(0);
                    if(townList.size() > 0){
                        Select4  = townList.get(0).getTid()+"";
                    }else{
                        Select4 = "";
                    }
                }else{
                    Select3 = "";
                    townList.clear();
                    town_arr.clear();
                    townAdapter.notifyDataSetChanged();
                    sp_rele_recruit_town.setSelection(0);
                    Select4 = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_rele_recruit_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Select2  = cityList.get(position).getTid()+"";
                regionList.clear();
                regionList.addAll(getChildList(Integer.valueOf(Select2))) ;
                region_arr.clear();
                for(int i=0;i<regionList.size();i++){
                    region_arr.add(regionList.get(i).getName());
                }
                regionAdapter.notifyDataSetChanged();
                sp_rele_recruit_county.setSelection(0);
                if(regionList.size() > 0){
                    Select3  = regionList.get(0).getTid()+"";
                    townList.clear();
                    townList.addAll(getChildList(Integer.valueOf(Select3)));
                    town_arr.clear();
                    for(int i=0;i<townList.size();i++){
                        town_arr.add(townList.get(i).getName());
                    }
                    townAdapter.notifyDataSetChanged();
                    sp_rele_recruit_town.setSelection(0);
                    if(townList.size() > 0){
                        Select4  = townList.get(0).getTid()+"";
                    }else{
                        Select4 = "";
                    }
                }else{
                    Select3  ="";
                    townList.clear();
                    town_arr.clear();
                    townAdapter.notifyDataSetChanged();
                    sp_rele_recruit_town.setSelection(0);
                    Select4 = "";
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_rele_recruit_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Select3  = regionList.get(position).getTid()+"";
                townList.clear();
                townList.addAll(getChildList(Integer.valueOf(Select3)));
                town_arr.clear();
                for(int i=0;i<townList.size();i++){
                    town_arr.add(townList.get(i).getName());
                }
                townAdapter.notifyDataSetChanged();
                sp_rele_recruit_town.setSelection(0);
                if(townList.size() > 0){
                    Select4  = townList.get(0).getTid()+"";
                }else{
                    Select4 = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_rele_recruit_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(townList.size() > 0){
                    Select4  = townList.get(0).getTid()+"";
                }else{
                    Select4 = "";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //简历大类
        sp_rele_recruit_zwdl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_rele_recruit_zwxl, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("-----------sp_jobmanage_jldl:");
                if(position == 0){
                    bigzhiweiclass = dl_arr.get(0);
                    SmallZhiweiClass("");
                    return;
                }
                BigZhiweiBean bigZhiweiBean = dl_List.get(position-1);
                bigzhiweiclass  = bigZhiweiBean.getBigClassName();
                SmallZhiweiClass( bigZhiweiBean.getBigClassID());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //简历小类
        sp_rele_recruit_zwxl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----------sp_jobmanage_jlxl:");
                if(position == 0){
                    smallzhiweiclass = xl_arr.get(0);
                    return;
                }
                SmallZhiweiBean smallZhiweiBean = xl_List.get(position-1);
                smallzhiweiclass  = smallZhiweiBean.getSmallClassName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //性别要求
        sp_rele_recruit_xbyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xbyq  = xbyqList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //性别要求
        sp_rele_recruit_xlyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xlyq  = xlyqList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //工作经验
        sp_rele_recruit_gzjy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gzjy  = gzjyList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //民族要求
        sp_rele_recruit_mzyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mizu  = minzuList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //月薪要求
        sp_rele_recruit_yxqk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yuexin  = yuexinList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围开始
        sp_rele_recruit_nlfwk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nlfwk  = nlfwkList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围结束
        sp_rele_recruit_nlfwj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nlfwj  = nlfwjList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围结束
        sp_rele_recruit_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    gudingyuefen = "0";
                }else if(position == 1){
                    gudingyuefen = "1";
                }else if(position == 2){
                    gudingyuefen = "3";
                }else if(position == 3){
                    gudingyuefen = "6";
                }else if(position == 4){
                    gudingyuefen = "12";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setDLScreenData(){

        System.out.println("-----------setDLScreenData:");

        dl_arr.add("选择大类");

        for(int i=0;i<dl_List.size();i++){
            dl_arr.add(dl_List.get(i).getBigClassName());
        }
        dlAdapter.notifyDataSetChanged();
    }

    private void setXLScreenData() {
        System.out.println("-----------setXLScreenData:-"+xl_arr.size());
        System.out.println("-----------setXLScreenData:-"+xl_List.size());
        xl_arr.add("选择小类");

        for(int i=0;i<xl_List.size();i++){
            xl_arr.add(xl_List.get(i).getSmallClassName());
        }
        xlAdapter.notifyDataSetChanged();
        sp_rele_recruit_zwxl.setSelection(0);
    }

    private void setPriceScreenData() {
        yuexinList.add("不限");
        for(int i=0;i<yx_List.size();i++){
            yuexinList.add(yx_List.get(i).getYuexinName());
        }
        yuexinAdapter.notifyDataSetChanged();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_release_recruit_back :
                finish();
                break;
            case R.id.tv_personal_xgmm :
                startActivity(new Intent(ReleaseRecruitActivity.this,ModifyPasswordActivity.class));
                break;
            case R.id.bt_rele_recruit_fbzp :
                if(type == 0){
                    hrfabu(selImageList);
                }else{
                    hrfabuJZ(selImageList);
                }

                break;
            case R.id.tv_recruit_yxqx_content :
                showDatePickerDialog();
                break;
            case R.id.tv_rele_manage_cz :
                startActivity(new Intent(ReleaseRecruitActivity.this, OnlineRechargeActivity.class));
                break;

            default:
                break;
        }
    }

    private void showDatePickerDialog(){
        if(calendar == null){
            calendar = Calendar.getInstance();
        }
        String time = tv_recruit_yxqx_content.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker=new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                Toast.makeText(mContext, year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_SHORT).show();
                tv_recruit_yxqx_content.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        }, year, month, day);
        datePicker.show();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
//        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
//        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
//        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
//        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_release_recruit);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount,1);
        adapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量

                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());

//                ImagePicker.getInstance().setSelectLimit(maxImgCount);

                Intent intent1 = new Intent(ReleaseRecruitActivity.this, ImageGridActivity.class);
                /* 如果需要进入选择的时候显示已经选中的图片，
                 * 详情请查看ImagePickerActivity
                 * */
//                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);
                startActivityForResult(intent1, REQUEST_CODE_SELECT);

                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("resultCode-----"+resultCode);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
//                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {
                    selImageList.clear();
                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        }
    }

    public List<AreaBean> getChildList(int tid) {

        System.out.println("--------------------"+allList.size());
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


    private void hrfabu(ArrayList<ImageItem> selImageList) {
        String youxiaoqi = tv_recruit_yxqx_content.getText().toString().trim();
        String title = et_rele_recruit_title.getText().toString().trim();
        String address = et_rele_recruit_address.getText().toString().trim();
        String peoples = et_rele_recruit_peoples.getText().toString().trim();
        String zwms = et_rele_recruit_zwms.getText().toString().trim();
        String lianxiren = et_rele_recruit_lxr.getText().toString().trim();
        String lianxidianhua = et_rele_recruit_lxdh.getText().toString().trim();
        if("选择大类".equals(bigzhiweiclass) || "选择小类".equals(smallzhiweiclass)){
            Toast.makeText(mContext,"请选择职位类别",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(youxiaoqi)){
            Toast.makeText(mContext,"有效期不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"招聘标题不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(address)){
            Toast.makeText(mContext,"工作地址不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(peoples)){
            Toast.makeText(mContext,"招聘人数不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(zwms)){
            Toast.makeText(mContext,"职位描述不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("正在提交数据");

        System.out.println("Select1-"+Select1+"Select2-"+Select2+"Select3-"+Select3+"Select4-"+Select4);
        System.out.println("bigzhiweiclass-"+bigzhiweiclass+"smallzhiweiclass-"+smallzhiweiclass);
        System.out.println("youxiaoqi-"+tv_recruit_yxqx_content.getText());
        System.out.println("xbyq-"+xbyq+"gzjy-"+gzjy+"mizu-"+mizu+"yuexin-"+yuexin+"nlfwk-"+nlfwk+"nlfwj-"+nlfwj);

        System.out.println("selImageList-------"+selImageList.size());

        if("不限".equals(nlfwk) || "".equals(nlfwk) || "0".equals(nlfwk)){
            nlfwk = "0";
        }
        if("不限".equals(nlfwj) || "".equals(nlfwj) || "0".equals(nlfwj)){
            nlfwj = "0";
        }

        if(nlfwk.endsWith("岁")){
            nlfwk = nlfwk.substring(0,nlfwk.length()-1);
        }
        if(nlfwj.endsWith("岁")){
            nlfwj = nlfwj.substring(0,nlfwj.length()-1);
        }

        String sub_bigzhiweiclass = "";
        String sub_smallzhiweiclass = "";
        String sub_xbyq = "";
        String sub_xlyq = "";
        String sub_gzjy = "";
        String sub_mizu = "";
        String sub_yuexin = "";
        try {
            sub_bigzhiweiclass =  URLEncoder.encode(bigzhiweiclass,"GB2312");
            sub_smallzhiweiclass =  URLEncoder.encode(smallzhiweiclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            address =  URLEncoder.encode(address,"GB2312");
            sub_xbyq =  URLEncoder.encode(xbyq,"GB2312");
            sub_xlyq =  URLEncoder.encode(xlyq,"GB2312");
            sub_gzjy =  URLEncoder.encode(gzjy,"GB2312");
            sub_mizu =  URLEncoder.encode(mizu,"GB2312");
            sub_yuexin =  URLEncoder.encode(yuexin,"GB2312");
            zwms =  URLEncoder.encode(zwms,"GB2312");
            lianxiren =  URLEncoder.encode(lianxiren,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Select1", Select1)
                .addFormDataPart("Select2", Select2)
                .addFormDataPart("Select3", Select3)
                .addFormDataPart("Select4", Select4)
                .addFormDataPart("bigzhiweiclass", sub_bigzhiweiclass)
                .addFormDataPart("smallzhiweiclass", sub_smallzhiweiclass)
                .addFormDataPart("youxiaoqi", youxiaoqi)
                .addFormDataPart("title", title)
                .addFormDataPart("dizhi", address)
                .addFormDataPart("renshu", peoples)
                .addFormDataPart("xingbie", sub_xbyq)
                .addFormDataPart("xueli", sub_xlyq)
                .addFormDataPart("gongzuojingyan", sub_gzjy)
                .addFormDataPart("minzu", sub_mizu)
                .addFormDataPart("nianlingfanwei1", nlfwk)
                .addFormDataPart("nianlingfanwei2", nlfwj)
                .addFormDataPart("yuexin", sub_yuexin)
                .addFormDataPart("content", zwms)
                .addFormDataPart("lianxiren", lianxiren)
                .addFormDataPart("lianxidianhua", lianxidianhua)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid)
                .addFormDataPart("gudingyuefen", gudingyuefen);

//                .addFormDataPart("key", bodyParams);

        for (int i = 0; i < selImageList.size(); i++) {
            File file = new File(selImageList.get(i).getPath());
            requestBodyBuilder.addFormDataPart("img"+i, file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
        }
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/hrfabu.asp").post(requestBody).build();
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
                    Log.d("getXXContent","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){

                            handler.sendEmptyMessage(FB_OK);
                        }else if(code == 1){
                            handler.sendEmptyMessage(FB_STATE1);
                        }else if(code == 2){
                            handler.sendEmptyMessage(FB_STATE2);
                        }else if(code == 3){
                            handler.sendEmptyMessage(FB_STATE3);
                        }else{
                            handler.sendEmptyMessage(FB_STATE4);
                        }

                    }
                }
            }
        });
    }

    private void hrfabuJZ(ArrayList<ImageItem> selImageList) {
        String youxiaoqi = tv_recruit_yxqx_content.getText().toString().trim();
        String title = et_rele_recruit_title.getText().toString().trim();
        String address = et_rele_recruit_address.getText().toString().trim();
        String peoples = et_rele_recruit_peoples.getText().toString().trim();
        String zwms = et_rele_recruit_zwms.getText().toString().trim();
        String lianxiren = et_rele_recruit_lxr.getText().toString().trim();
        String lianxidianhua = et_rele_recruit_lxdh.getText().toString().trim();
        String xinzi = et_job_manage_xzqk.getText().toString().trim();

        String gongzuoshijian = "";
        if(job_manage_checkBox1.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期一,";
        }
        if(job_manage_checkBox2.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期二,";
        }
        if(job_manage_checkBox3.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期三,";
        }
        if(job_manage_checkBox4.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期四,";
        }
        if(job_manage_checkBox5.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期五,";
        }
        if(job_manage_checkBox6.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期六,";
        }
        if(job_manage_checkBox7.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期日";
        }

        if(gongzuoshijian.endsWith(",")){
            gongzuoshijian = gongzuoshijian.substring(0,gongzuoshijian.length()-1);
        }


        if("选择大类".equals(bigzhiweiclass) || "选择小类".equals(smallzhiweiclass)){
            Toast.makeText(mContext,"请选择职位类别",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(youxiaoqi)){
            Toast.makeText(mContext,"有效期不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"招聘标题不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(address)){
            Toast.makeText(mContext,"工作地址不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(peoples)){
            Toast.makeText(mContext,"招聘人数不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(gongzuoshijian)){
            Toast.makeText(mContext,"工作时间不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(xinzi)){
            Toast.makeText(mContext,"薪资不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(zwms)){
            Toast.makeText(mContext,"职位描述不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        showDialog("正在提交数据");

        System.out.println("Select1-"+Select1+"Select2-"+Select2+"Select3-"+Select3+"Select4-"+Select4);
        System.out.println("bigzhiweiclass-"+bigzhiweiclass+"smallzhiweiclass-"+smallzhiweiclass);
        System.out.println("youxiaoqi-"+tv_recruit_yxqx_content.getText());
        System.out.println("xbyq-"+xbyq+"gzjy-"+gzjy+"mizu-"+mizu+"yuexin-"+yuexin+"nlfwk-"+nlfwk+"nlfwj-"+nlfwj);

        System.out.println("selImageList-------"+selImageList.size());

        if("不限".equals(nlfwk) || "".equals(nlfwk) || "0".equals(nlfwk)){
            nlfwk = "0";
        }
        if("不限".equals(nlfwj) || "".equals(nlfwj) || "0".equals(nlfwj)){
            nlfwj = "0";
        }

        if(nlfwk.endsWith("岁")){
            nlfwk = nlfwk.substring(0,nlfwk.length()-1);
        }
        if(nlfwj.endsWith("岁")){
            nlfwj = nlfwj.substring(0,nlfwj.length()-1);
        }

        String sub_bigzhiweiclass = "";
        String sub_smallzhiweiclass = "";
        String sub_xbyq = "";
        String sub_xlyq = "";
        String sub_gzjy = "";
        String sub_mizu = "";
        String sub_yuexin = "";
        String sub_xinzidanwei = "元/天";
        try {
            sub_bigzhiweiclass =  URLEncoder.encode(bigzhiweiclass,"GB2312");
            sub_smallzhiweiclass =  URLEncoder.encode(smallzhiweiclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            address =  URLEncoder.encode(address,"GB2312");
            sub_xbyq =  URLEncoder.encode(xbyq,"GB2312");
            sub_xlyq =  URLEncoder.encode(xlyq,"GB2312");
            sub_gzjy =  URLEncoder.encode(gzjy,"GB2312");
            sub_mizu =  URLEncoder.encode(mizu,"GB2312");
            sub_yuexin =  URLEncoder.encode(yuexin,"GB2312");
            zwms =  URLEncoder.encode(zwms,"GB2312");
            lianxiren =  URLEncoder.encode(lianxiren,"GB2312");
            sub_xinzidanwei =  URLEncoder.encode(sub_xinzidanwei,"GB2312");
            gongzuoshijian =  URLEncoder.encode(gongzuoshijian,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Select1", Select1)
                .addFormDataPart("Select2", Select2)
                .addFormDataPart("Select3", Select3)
                .addFormDataPart("Select4", Select4)
                .addFormDataPart("bigzhiweiclass", sub_bigzhiweiclass)
                .addFormDataPart("smallzhiweiclass", sub_smallzhiweiclass)
                .addFormDataPart("youxiaoqi", youxiaoqi)
                .addFormDataPart("title", title)
                .addFormDataPart("dizhi", address)
                .addFormDataPart("renshu", peoples)
                .addFormDataPart("xingbie", sub_xbyq)
                .addFormDataPart("xueli", sub_xlyq)
                .addFormDataPart("gongzuojingyan", sub_gzjy)
                .addFormDataPart("minzu", sub_mizu)
                .addFormDataPart("nianlingfanwei1", nlfwk)
                .addFormDataPart("nianlingfanwei2", nlfwj)
                .addFormDataPart("xinzi", xinzi)
                .addFormDataPart("xinzidanwei", sub_xinzidanwei)
                .addFormDataPart("gongzuoshijian", gongzuoshijian)
                .addFormDataPart("content", zwms)
                .addFormDataPart("lianxiren", lianxiren)
                .addFormDataPart("lianxidianhua", lianxidianhua)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid)
                .addFormDataPart("gudingyuefen", gudingyuefen);

//                .addFormDataPart("key", bodyParams);

        for (int i = 0; i < selImageList.size(); i++) {
            File file = new File(selImageList.get(i).getPath());
            requestBodyBuilder.addFormDataPart("img"+i, file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
        }
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/hrfabuJZ.asp").post(requestBody).build();
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
                    Log.d("getXXContent","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){

                            handler.sendEmptyMessage(FB_OK);
                        }else if(code == 1){
                            handler.sendEmptyMessage(FB_STATE1);
                        }else if(code == 2){
                            handler.sendEmptyMessage(FB_STATE2);
                        }else if(code == 3){
                            handler.sendEmptyMessage(FB_STATE3);
                        }else{
                            handler.sendEmptyMessage(FB_STATE4);
                        }

                    }
                }
            }
        });
    }

    public String getFileName(String pathandname) {
        int start = pathandname.lastIndexOf("/");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }
    }


    /**
     * EditText竖直方向能否够滚动
     * @param editText  须要推断的EditText
     * @return  true：能够滚动   false：不能够滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }
System.out.println("scrollY---"+scrollY);
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    // 创建一个内部类来实现 ,在实现下面内部类之前,需要自定义的Bean对象来封装处理Josn格式的数据
    class  NewsAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            Gson gson = new Gson();
//            String data_minzu = StreamUtils.get(APP.getInstance(), R.raw.minzu);
//            Type type_minzu = new TypeToken<List<String>>() { }.getType();
//            minzuList.addAll(gson.fromJson(data_minzu, type_minzu));

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

            cityList.addAll(getChildList(provinceList.get(0).getTid())) ;
            for(int i=0;i<cityList.size();i++){
                city_arr.add(cityList.get(i).getName());
            }

            regionList  = getChildList(cityList.get(0).getTid());
            for(int i=0;i<regionList.size();i++){
                region_arr.add(regionList.get(i).getName()) ;
            }

            townList  = getChildList(regionList.get(0).getTid());
            for(int i=0;i<townList.size();i++){
                town_arr.add(townList.get(i).getName());
            }

            xbyqList.add("不限");
            xbyqList.add("男");
            xbyqList.add("女");

            String data_xueli = StreamUtils.get(APP.getInstance(), R.raw.xueli);
            Type type_xueli = new TypeToken<List<String>>() { }.getType();
            xlyqList.addAll(gson.fromJson(data_xueli, type_xueli));
            xlyqList.add(0,"不限");

            String data_gzjy = StreamUtils.get(APP.getInstance(), R.raw.gongzuojingyan);
            Type type_gzjy = new TypeToken<List<String>>() { }.getType();
            gzjyList.addAll(gson.fromJson(data_gzjy, type_gzjy));
            gzjyList.add(0,"不限");

            String data_minzu = StreamUtils.get(APP.getInstance(), R.raw.minzu);
            Type type_minzu = new TypeToken<List<String>>() { }.getType();
            minzuList.addAll(gson.fromJson(data_minzu, type_minzu));
            minzuList.add(0,"不限");

            nlfwkList.add("不限");
            nlfwjList.add("不限");
            for(int i=16;i<100;i++){
                nlfwkList.add(i+"岁");
                nlfwjList.add(i+"岁");
            }

            zdyfList.add("不置顶");
            zdyfList.add("一个月(￥"+gudingyue+"元)");
            zdyfList.add("三个月(￥"+gudingyue3+"元)");
            zdyfList.add("六个月(￥"+gudingyue6+"元)");
            zdyfList.add("十二个月(￥"+gudingyue12+"元)");

            handler.sendEmptyMessage(DATA_JC);

            return null;
        }
    }

    //获取职位大类
    public void BigZhiweiClass(){

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/BigZhiweiClass.asp").post(requestBody).build();
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
                    Log.d("BigZhiweiClass","BigZhiweiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<BigZhiweiBean>> result = gson.fromJson(content, new TypeToken<Entity<List<BigZhiweiBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            dl_lishi_List = result.getData();
                            handler.sendEmptyMessage(DL_OK);
                        }else{
                            handler.sendEmptyMessage(DL_ERROR);
                        }

                    }
                }
            }
        });
    }

    //获取职位小类
    public void SmallZhiweiClass(String BigClassID){

        RequestBody requestBody = new FormBody.Builder().add("BigClassID",BigClassID)
                .build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/SmallZhiweiClass.asp").post(requestBody).build();
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
                    Log.d("SmallZhiweiClass","SmallZhiweiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<SmallZhiweiBean>> result = gson.fromJson(content, new TypeToken<Entity<List<SmallZhiweiBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            xl_lishi_List = result.getData();
                            handler.sendEmptyMessage(XL_OK);
                        }else{
                            handler.sendEmptyMessage(XL_ERROR);
                        }

                    }
                }
            }
        });
    }

    //获取月薪类型
    public void YueXiClass(){

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/yuexi.asp").post(requestBody).build();
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
                    Log.d("yuexiClass","yuexiClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<YueXiClassBean>> result = gson.fromJson(content, new TypeToken<Entity<List<YueXiClassBean>>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            yx_lishi_List = result.getData();
                            handler.sendEmptyMessage(YX_OK);
                        }else{
                            handler.sendEmptyMessage(YX_ERROR);
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
}


