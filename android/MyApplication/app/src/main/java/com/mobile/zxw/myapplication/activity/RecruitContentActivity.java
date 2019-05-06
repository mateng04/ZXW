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
import com.mobile.zxw.myapplication.bean.RecruitContentBean;
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
import java.util.ArrayList;
import java.util.Calendar;
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

public class RecruitContentActivity extends AppCompatActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener  {

    private Context mContext;
    String hyjb = "";   //  普通   VIP
    TextView tv_personal_xgmm, tv_personal_xgsjh;
    Button bt_recruit_manage_fbzp;

    TextView tv_recruit_content_cz; //充值

    Button bt_recruit_content_back;

    RecruitContentBean recruitContentBean;

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private File file;

    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private ArrayList<ImageItem> sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem> del_imageList = new ArrayList<>(); //需要删除的图片

    private int maxImgCount = 9;               //允许选择图片最大数

    String sessionID;
    String userid;
    String userName;
    String xxid;
    String denglushouji;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private SharedPreferencesHelper sp_setting;
    private String gudingyue,gudingyue3,gudingyue6,gudingyue12;
    Calendar calendar;

    //加载对话框
    private Dialog mLoadingDialog;

    private int type = 0;   //全职 兼职 标记
    private String pindao;
    private String Select1,Select2,Select3,Select4;
    private String bigzhiweiclass,smallzhiweiclass;
    private String xbyq,xlyq,gzjy,mizu,yuexin,nlfwk,nlfwj,gudingyuefen;
    private String guding,gudingdaoqi;

    RelativeLayout rl_recruit_content_zdyf;
    Spinner sp_recruit_content_zdyf;
    LinearLayout ll_recruit_content_zdz;
    TextView tv_recruit_content_zdz;

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

    Spinner sp_recruit_content_type;
    Spinner sp_recruit_content_rovince,sp_recruit_content_city,sp_recruit_content_county,sp_recruit_content_town;
    Spinner sp_recruit_content_zwdl,sp_recruit_content_zwxl;
    TextView tv_recruit_yxqx_content;
    EditText et_recruit_content_title,et_recruit_content_address,et_recruit_content_peoples;
    Spinner sp_recruit_content_xbyq,sp_recruit_content_xlyq,sp_recruit_content_gzjy,sp_recruit_content_mzyq,sp_recruit_content_yxqk;
    Spinner sp_recruit_content_nlfwk, sp_recruit_content_nlfwj;
    EditText et_recruit_content_zwms;
    Button bt_recruit_content_fbzp;
    EditText et_recruit_content_lxr,et_recruit_content_lxdh;


    RelativeLayout rl_recruit_content_yxqk;
    RelativeLayout rl_recruit_content_kxsj;
    LinearLayout ll_recruit_content_xzqk;
    EditText et_recruit_content_xzqk;

    CheckBox recruit_content_checkBox1,recruit_content_checkBox2,recruit_content_checkBox3,recruit_content_checkBox4,recruit_content_checkBox5,
            recruit_content_checkBox6,recruit_content_checkBox7;

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

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
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
    static int DELETE_OK = 16; //删除图片成功
    static int DELETE_ERROR = 17; //删除图片失败
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
                    getHrDetails();
//                    cancelDialog();
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
                    Toast.makeText(mContext, "保存招聘成功", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mContext, "登录超时,请重新登陆", Toast.LENGTH_SHORT).show();
                    break;
                case 16:
                    break;
                case 17:
                    Toast.makeText(mContext, "删除图片失败，请刷新数据重新删除", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_content);
        mContext = this;

        sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, "config");
        //会员级别
        hyjb = (String) sharedPreferencesHelper.getSharedPreference("hyjb", "");

        calendar = Calendar.getInstance();
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }

        xxid = getIntent().getStringExtra("xxid");
        type = Integer.valueOf(getIntent().getStringExtra("type"));
        if(type == 0){
            pindao = "全职招聘";
        }else{
            pindao = "兼职招聘";
        }
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        denglushouji = (String) sharedPreferencesHelper.getSharedPreference("denglushouji", "");
        userName = (String) sharedPreferencesHelper.getSharedPreference("userName", "");

        sp_setting = new SharedPreferencesHelper(
                mContext, "setting");
        gudingyue = (String) sp_setting.getSharedPreference("gudingyue", "");
        gudingyue3 = (String) sp_setting.getSharedPreference("gudingyue3", "");
        gudingyue6 = (String) sp_setting.getSharedPreference("gudingyue6", "");
        gudingyue12 = (String) sp_setting.getSharedPreference("gudingyue12", "");

        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();

        initView();
        initData();
    }



    public void initView(){

        bt_recruit_content_back = (Button) findViewById(R.id.bt_recruit_content_back);
        bt_recruit_content_back.setOnClickListener(this);

        sp_recruit_content_type = (Spinner) findViewById(R.id.sp_recruit_content_type);

        sp_recruit_content_rovince = (Spinner) findViewById(R.id.sp_recruit_content_rovince);
        sp_recruit_content_city = (Spinner) findViewById(R.id.sp_recruit_content_city);
        sp_recruit_content_county = (Spinner) findViewById(R.id.sp_recruit_content_county);
        sp_recruit_content_town = (Spinner) findViewById(R.id.sp_recruit_content_town);

        sp_recruit_content_zwdl = (Spinner) findViewById(R.id.sp_recruit_content_zwdl);
        sp_recruit_content_zwxl = (Spinner) findViewById(R.id.sp_recruit_content_zwxl);

        tv_recruit_yxqx_content = (TextView) findViewById(R.id.tv_recruit_yxqx_content);
        tv_recruit_yxqx_content.setOnClickListener(this);

        et_recruit_content_title = (EditText) findViewById(R.id.et_recruit_content_title);
        et_recruit_content_address = (EditText) findViewById(R.id.et_recruit_content_address);
        et_recruit_content_peoples = (EditText) findViewById(R.id.et_recruit_content_peoples);

        sp_recruit_content_xbyq = (Spinner) findViewById(R.id.sp_recruit_content_xbyq);
        sp_recruit_content_xlyq = (Spinner) findViewById(R.id.sp_recruit_content_xlyq);
        sp_recruit_content_gzjy = (Spinner) findViewById(R.id.sp_recruit_content_gzjy);
        sp_recruit_content_mzyq = (Spinner) findViewById(R.id.sp_recruit_content_mzyq);
        sp_recruit_content_yxqk = (Spinner) findViewById(R.id.sp_recruit_content_yxqk);

        sp_recruit_content_nlfwk = (Spinner) findViewById(R.id.sp_recruit_content_nlfwk);
        sp_recruit_content_nlfwj = (Spinner) findViewById(R.id.sp_recruit_content_nlfwj);

        et_recruit_content_zwms = (EditText) findViewById(R.id.et_recruit_content_zwms);
        et_recruit_content_zwms.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.et_recruit_content_zwms:
                        // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                        if (canVerticalScroll(et_recruit_content_zwms))
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);//告诉父view，你可以处理了
                        }
                }
                return false;
            }
        });

        bt_recruit_content_fbzp = (Button) findViewById(R.id.bt_recruit_content_fbzp);
        bt_recruit_content_fbzp.setOnClickListener(this);

        et_recruit_content_lxr = (EditText) findViewById(R.id.et_recruit_content_lxr);
        if(userName != ""){
            et_recruit_content_lxr.setText(userName.substring(0,1)+"先生");
        }
        et_recruit_content_lxdh = (EditText) findViewById(R.id.et_recruit_content_lxdh);
        et_recruit_content_lxdh.setText(denglushouji);

        sp_recruit_content_zdyf = (Spinner) findViewById(R.id.sp_recruit_content_zdyf);

        rl_recruit_content_zdyf = (RelativeLayout) findViewById(R.id.rl_recruit_content_zdyf);
        ll_recruit_content_zdz = (LinearLayout) findViewById(R.id.ll_recruit_content_zdz);
        tv_recruit_content_zdz = (TextView) findViewById(R.id.tv_recruit_content_zdz);

        rl_recruit_content_yxqk = (RelativeLayout) findViewById(R.id.rl_recruit_content_yxqk);
        rl_recruit_content_kxsj = (RelativeLayout) findViewById(R.id.rl_recruit_content_kxsj);
        ll_recruit_content_xzqk = (LinearLayout) findViewById(R.id.ll_recruit_content_xzqk);
        et_recruit_content_xzqk = (EditText) findViewById(R.id.et_recruit_content_xzqk);

        recruit_content_checkBox1 = (CheckBox) findViewById(R.id.recruit_content_checkBox1);
        recruit_content_checkBox2 = (CheckBox) findViewById(R.id.recruit_content_checkBox2);
        recruit_content_checkBox3 = (CheckBox) findViewById(R.id.recruit_content_checkBox3);
        recruit_content_checkBox4 = (CheckBox) findViewById(R.id.recruit_content_checkBox4);
        recruit_content_checkBox5 = (CheckBox) findViewById(R.id.recruit_content_checkBox5);
        recruit_content_checkBox6 = (CheckBox) findViewById(R.id.recruit_content_checkBox6);
        recruit_content_checkBox7 = (CheckBox) findViewById(R.id.recruit_content_checkBox7);

        if( type == 1){
            rl_recruit_content_yxqk.setVisibility(View.GONE);
            ll_recruit_content_xzqk.setVisibility(View.VISIBLE);
            rl_recruit_content_kxsj.setVisibility(View.VISIBLE);
        }else{
            rl_recruit_content_yxqk.setVisibility(View.VISIBLE);
            ll_recruit_content_xzqk.setVisibility(View.GONE);
            rl_recruit_content_kxsj.setVisibility(View.GONE);
        }

        tv_recruit_content_cz = (TextView) findViewById(R.id.tv_recruit_content_cz);
        tv_recruit_content_cz.setOnClickListener(this);

        if("".equals(hyjb)){
            et_recruit_content_lxr.setEnabled(false);
            et_recruit_content_lxdh.setEnabled(false);
        }else if("普通".equals(hyjb)){
            et_recruit_content_lxr.setEnabled(false);
            et_recruit_content_lxdh.setEnabled(false);
        }else if("VIP".equals(hyjb)){
            et_recruit_content_lxr.setEnabled(true);
            et_recruit_content_lxdh.setEnabled(true);
        }

        setAdapter();
        setAdapterOnItemClick();
    }

    public void initData(){
        showDialog("");
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
        sp_recruit_content_type.setAdapter(typeAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        provinceAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_recruit_content_rovince.setAdapter(provinceAdapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_recruit_content_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_recruit_content_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_recruit_content_town.setAdapter(townAdapter);

        dl_arr = new ArrayList<>();
        dlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,dl_arr);
        sp_recruit_content_zwdl.setAdapter(dlAdapter);

        xl_arr = new ArrayList<>();
        xlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xl_arr);
        sp_recruit_content_zwxl.setAdapter(xlAdapter);

        xbyqList = new ArrayList<>();
        xbyqAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xbyqList);
        sp_recruit_content_xbyq.setAdapter(xbyqAdapter);

        xlyqList = new ArrayList<>();
        xueliAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xlyqList);
        sp_recruit_content_xlyq.setAdapter(xueliAdapter);

        gzjyList = new ArrayList<>();
        gzjyAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,gzjyList);
        sp_recruit_content_gzjy.setAdapter(gzjyAdapter);

        minzuList = new ArrayList<>();
        mzAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,minzuList);
        sp_recruit_content_mzyq.setAdapter(mzAdapter);

        yuexinList = new ArrayList<>();
        yuexinAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,yuexinList);
        sp_recruit_content_yxqk.setAdapter(yuexinAdapter);

        nlfwkList = new ArrayList<>();
        nlfwkAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,nlfwkList);
        sp_recruit_content_nlfwk.setAdapter(nlfwkAdapter);

        nlfwjList = new ArrayList<>();
        nlfwjAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,nlfwjList);
        sp_recruit_content_nlfwj.setAdapter(nlfwjAdapter);

        zdyfList = new ArrayList<>();
        zdyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zdyfList);
        sp_recruit_content_zdyf.setAdapter(zdyfAdapter);
    }

    private void setAdapterOnItemClick(){
        System.out.println("-----------setAdapterOnItemClick:");
        //简历选择
        sp_recruit_content_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                if( type == 1){
                    rl_recruit_content_yxqk.setVisibility(View.GONE);
                    ll_recruit_content_xzqk.setVisibility(View.VISIBLE);
                    rl_recruit_content_kxsj.setVisibility(View.VISIBLE);
                }else{
                    rl_recruit_content_yxqk.setVisibility(View.VISIBLE);
                    ll_recruit_content_xzqk.setVisibility(View.GONE);
                    rl_recruit_content_kxsj.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_recruit_content_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_recruit_content_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_recruit_content_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_recruit_content_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_recruit_content_county, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_recruit_content_county.setSelection(region_index);
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

        sp_recruit_content_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_recruit_content_town, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_recruit_content_town.setSelection(town_index);
                System.out.println("xx--sp_recruit_content_county------"+town_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_recruit_content_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        //简历大类
        sp_recruit_content_zwdl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_recruit_content_zwxl, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
        sp_recruit_content_zwxl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        sp_recruit_content_xbyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xbyq  = xbyqList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //性别要求
        sp_recruit_content_xlyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xlyq  = xlyqList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //工作经验
        sp_recruit_content_gzjy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gzjy  = gzjyList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //民族要求
        sp_recruit_content_mzyq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mizu  = minzuList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //月薪要求
        sp_recruit_content_yxqk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yuexin  = yuexinList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围开始
        sp_recruit_content_nlfwk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nlfwk  = nlfwkList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围结束
        sp_recruit_content_nlfwj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nlfwj  = nlfwjList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //年龄范围结束
        sp_recruit_content_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        System.out.println("smallzhiweiclass-------------"+smallzhiweiclass);


        xl_arr.add("选择小类");

        for(int i=0;i<xl_List.size();i++){
            xl_arr.add(xl_List.get(i).getSmallClassName());
        }
        sp_recruit_content_zwxl.setSelection(0);
        xlAdapter.notifyDataSetChanged();
        for(int i=0;i<xl_arr.size();i++){
            if(xl_arr.get(i).equals(smallzhiweiclass)) {
                sp_recruit_content_zwxl.setSelection(i);
            }
        }
    }

    private void setPriceScreenData() {
        yuexinList.add("不限");
        for(int i=0;i<yx_List.size();i++){
            yuexinList.add(yx_List.get(i).getYuexinName());
        }
        yuexinAdapter.notifyDataSetChanged();

    }

    public void setData(){

        tv_recruit_yxqx_content.setText(recruitContentBean.getYouxiaoqi());
        et_recruit_content_title.setText(recruitContentBean.getTitle());
        et_recruit_content_address.setText(recruitContentBean.getDizhi());
        et_recruit_content_peoples.setText(recruitContentBean.getRenshu());
        et_recruit_content_zwms.setText(recruitContentBean.getContent());
        et_recruit_content_lxr.setText(recruitContentBean.getLianxiren());
        et_recruit_content_lxdh.setText(recruitContentBean.getLianxidianhua());

        Select1 = recruitContentBean.getSheng();
        Select2 = recruitContentBean.getShi();
        Select3 = recruitContentBean.getQuxian();
        Select4 = recruitContentBean.getXiangzhen();

        for(int i=0;i<provinceList.size();i++){
            if(Integer.valueOf(Select1) == provinceList.get(i).getTid()){
                sp_recruit_content_rovince.setSelection(i);
            }
        }

        mizu = recruitContentBean.getMinzu();
        for(int i=0;i<minzuList.size();i++){
            if(minzuList.get(i).equals(mizu) ){
                sp_recruit_content_mzyq.setSelection(i,false);
            }
        }
        xbyq = recruitContentBean.getXingbie();
        for(int i=0;i<xbyqList.size();i++){
            if(xbyqList.get(i).equals(xbyq) ){
                sp_recruit_content_xbyq.setSelection(i,false);
            }
        }
        xlyq = recruitContentBean.getXueli();
        for(int i=0;i<xlyqList.size();i++){
            if(xlyqList.get(i).equals(xlyq) ){
                sp_recruit_content_xlyq.setSelection(i,false);
            }
        }

        nlfwk = recruitContentBean.getNianlingfanwei1();
        if("不限".endsWith(nlfwk) || "0".endsWith(nlfwk) || "".endsWith(nlfwk)){
            sp_recruit_content_nlfwk.setSelection(0);
        }else{
            for(int i=0;i<nlfwkList.size();i++){
                if(nlfwkList.get(i).equals(nlfwk+"岁") ){
                    sp_recruit_content_nlfwk.setSelection(i);
                }
            }
        }

        nlfwj = recruitContentBean.getNianlingfanwei2();
        if("不限".endsWith(nlfwj) || "0".endsWith(nlfwj) || "".endsWith(nlfwj)){
            sp_recruit_content_nlfwj.setSelection(0);
        }else{
            for(int i=0;i<nlfwjList.size();i++){
                if(nlfwjList.get(i).equals(nlfwj+"岁") ){
                    sp_recruit_content_nlfwj.setSelection(i);
                }
            }
        }

        bigzhiweiclass = recruitContentBean.getBigzhiweiclass();
        smallzhiweiclass = recruitContentBean.getSmallzhiweiclass();
        if(bigzhiweiclass != null && !"".equals(bigzhiweiclass)){
            //设置个默认的第一个
            String bigclass_select = "";
            for(int i=0;i<dl_arr.size();i++){
                if(dl_arr.get(i).equals(bigzhiweiclass)){
                    sp_recruit_content_zwdl.setSelection(i);
//                    if(i == 0){
//                        bigclass_select = "";
//                    }else {
//                        bigclass_select = dl_List.get(i-1).getBigClassID();
//                    }
                }
            }


//            xl_List.clear();
//            SmallZhiweiClass(bigclass_select);
        }

        gzjy = recruitContentBean.getGongzuojingyan();
        for(int i=0;i<gzjyList.size();i++){
            if(gzjyList.get(i).equals(gzjy) ){
                sp_recruit_content_gzjy.setSelection(i,false);
            }
        }

        if(type == 0){
            yuexin = recruitContentBean.getYuexin();
            for(int i=0;i<yuexinList.size();i++){
                if(yuexinList.get(i).equals(yuexin) ){
                    sp_recruit_content_yxqk.setSelection(i,false);
                }
            }
        }else{
            et_recruit_content_xzqk.setText(recruitContentBean.getXinzi());
        }


        guding = recruitContentBean.getGuding();
        gudingdaoqi = recruitContentBean.getGudingdaoqi();

//        if(type == 1){
//            kongxianshijian = recruitContentBean.getKongxianshijian();
//            if(kongxianshijian != null && !"".equals(kongxianshijian)){
//                String[] kxsj = kongxianshijian.split(",");
//                for (int i=0;i<kxsj.length;i++){
//                    if("星期一".equals(kxsj[i])){
//                        checkBox1.setChecked(true);
//                    }else if("星期二".equals(kxsj[i])){
//                        checkBox2.setChecked(true);
//                    }else if("星期三".equals(kxsj[i])){
//                        checkBox3.setChecked(true);
//                    }else if("星期四".equals(kxsj[i])){
//                        checkBox4.setChecked(true);
//                    }else if("星期五".equals(kxsj[i])){
//                        checkBox5.setChecked(true);
//                    }else if("星期六".equals(kxsj[i])){
//                        checkBox6.setChecked(true);
//                    }else if("星期日".equals(kxsj[i])){
//                        checkBox7.setChecked(true);
//                    }
//                }
//            }
//
//        }

        selImageList.clear();
        List<String> imgs_temp = recruitContentBean.getImg();
        if( imgs_temp != null){
            for(int i=0; i<imgs_temp.size();i++){

                System.out.println("imgs_temp.get(i)----"+imgs_temp.get(i));

                ImageItem imageItem = new ImageItem();
                imageItem.setPath(HttpUtils.URL+"/"+imgs_temp.get(i));
                selImageList.add(imageItem);
            }
            adapter.setImages(selImageList);
        }

        String kongxianshijian = recruitContentBean.getGongzuoshijian();
        if(kongxianshijian != null && !"".equals(kongxianshijian)){

            String[] kxsj = kongxianshijian.split(",");
            for (int i=0;i<kxsj.length;i++){
                if("星期一".equals(kxsj[i])){
                    recruit_content_checkBox1.setChecked(true);
                }else if("星期二".equals(kxsj[i])){
                    recruit_content_checkBox2.setChecked(true);
                }else if("星期三".equals(kxsj[i])){
                    recruit_content_checkBox3.setChecked(true);
                }else if("星期四".equals(kxsj[i])){
                    recruit_content_checkBox4.setChecked(true);
                }else if("星期五".equals(kxsj[i])){
                    recruit_content_checkBox5.setChecked(true);
                }else if("星期六".equals(kxsj[i])){
                    recruit_content_checkBox6.setChecked(true);
                }else if("星期日".equals(kxsj[i])){
                    recruit_content_checkBox7.setChecked(true);
                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_personal_xgmm :
                startActivity(new Intent(mContext,ModifyPasswordActivity.class));
                break;
            case R.id.bt_recruit_content_fbzp :
                if(type == 0){
                    hrxiugai(sub_imageList);
                }else{
                    hrxiugaiJZ(sub_imageList);
                }

                break;
            case R.id.tv_recruit_yxqx_content :
                showDatePickerDialog();
                break;
            case R.id.bt_recruit_content_back :
                finish();
                break;
            case R.id.tv_recruit_content_cz :
                startActivity(new Intent(mContext, OnlineRechargeActivity.class));
                break;

            default:
                break;
        }
    }

    private void showDatePickerDialog(){
        if(calendar == null){
            calendar = Calendar.getInstance();
        }

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

                Intent intent1 = new Intent(mContext, ImageGridActivity.class);
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
                    //需要上传的图片
                    sub_imageList.addAll(images);

                    selImageList.addAll(images);
                    adapter.setImages(selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {

                    del_imageList.clear();
                    del_imageList.addAll(selImageList);
                    del_imageList.removeAll(sub_imageList);
                    del_imageList.removeAll(images);
                    if(del_imageList.size() > 0){
                        deletetupian();
                    }

                    //需要上传的图片
                    sub_imageList.clear();
                    sub_imageList.addAll(images);

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


    private void hrxiugai(ArrayList<ImageItem> imageList) {
        String youxiaoqi = tv_recruit_yxqx_content.getText().toString().trim();
        String title = et_recruit_content_title.getText().toString().trim();
        String address = et_recruit_content_address.getText().toString().trim();
        String peoples = et_recruit_content_peoples.getText().toString().trim();
        String zwms = et_recruit_content_zwms.getText().toString().trim();
        String lianxiren = et_recruit_content_lxr.getText().toString().trim();
        String lianxidianhua = et_recruit_content_lxdh.getText().toString().trim();
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

        System.out.println("selImageList-------"+imageList.size());

        String nianlingfanwei1,nianlingfanwei2;

        if("不限".equals(nlfwk) || "0".equals(nlfwk) || "".equals(nlfwk)){
            nianlingfanwei1 = "0";
        }else{
            nianlingfanwei1 = nlfwk.substring(0,nlfwk.length()-1);
        }
        if("不限".equals(nlfwj) || "0".equals(nlfwj) || "".equals(nlfwj)){
            nianlingfanwei2 = "0";
        }else{
            nianlingfanwei2 = nlfwj.substring(0,nlfwj.length()-1);
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
                .addFormDataPart("nianlingfanwei1", nianlingfanwei1)
                .addFormDataPart("nianlingfanwei2", nianlingfanwei2)
                .addFormDataPart("yuexin", sub_yuexin)
                .addFormDataPart("content", zwms)
                .addFormDataPart("lianxiren", lianxiren)
                .addFormDataPart("lianxidianhua", lianxidianhua)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid)
                .addFormDataPart("xinxiID", xxid);
//                .addFormDataPart("gudingyuefen", gudingyuefen);

//                .addFormDataPart("key", bodyParams);
        System.out.println("imageList.get(i).getPath()---"+imageList.size());
        int position = selImageList.size() - imageList.size();
        for (int i = 0; i < imageList.size(); i++) {
            File file = new File(imageList.get(i).getPath());
            System.out.println("imageList.get(i).getPath()---"+imageList.get(i).getPath());
            if(imageList.get(i).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            }else if(imageList.get(i).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }else if(imageList.get(i).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
            }else if(imageList.get(i).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/gif"), file));
            }
        }
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/hrxiugai.asp").post(requestBody).build();
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

    private void hrxiugaiJZ(ArrayList<ImageItem> imageList) {
        String youxiaoqi = tv_recruit_yxqx_content.getText().toString().trim();
        String title = et_recruit_content_title.getText().toString().trim();
        String address = et_recruit_content_address.getText().toString().trim();
        String peoples = et_recruit_content_peoples.getText().toString().trim();
        String zwms = et_recruit_content_zwms.getText().toString().trim();
        String lianxiren = et_recruit_content_lxr.getText().toString().trim();
        String lianxidianhua = et_recruit_content_lxdh.getText().toString().trim();
        String xinzi = et_recruit_content_xzqk.getText().toString().trim();

        String gongzuoshijian = "";
        if(recruit_content_checkBox1.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期一,";
        }
        if(recruit_content_checkBox2.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期二,";
        }
        if(recruit_content_checkBox3.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期三,";
        }
        if(recruit_content_checkBox4.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期四,";
        }
        if(recruit_content_checkBox5.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期五,";
        }
        if(recruit_content_checkBox6.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期六,";
        }
        if(recruit_content_checkBox7.isChecked()){
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

        System.out.println("selImageList-------"+imageList.size());

        String nianlingfanwei1,nianlingfanwei2;

        if("不限".equals(nlfwk) || "".equals(nlfwk) || "0".equals(nlfwk)){
            nianlingfanwei1 = "0";
        }else{
            nianlingfanwei1 = nlfwk.substring(0,nlfwk.length()-1);
        }
        if("不限".equals(nlfwj) || "".equals(nlfwj) || "0".equals(nlfwj)){
            nianlingfanwei2 = "0";
        }else{
            nianlingfanwei2 = nlfwj.substring(0,nlfwj.length()-1);
        }

        String sub_bigzhiweiclass = "";
        String sub_smallzhiweiclass = "";
        String sub_xbyq = "";
        String sub_xlyq = "";
        String sub_gzjy = "";
        String sub_mizu = "";
        String sub_yuexin = "";
        String sub_xinzidanwei = "元/天";
        String sub_gzsj = "";
        try {
            sub_bigzhiweiclass =  URLEncoder.encode(bigzhiweiclass,"GB2312");
            sub_smallzhiweiclass =  URLEncoder.encode(smallzhiweiclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            address =  URLEncoder.encode(address,"GB2312");
            sub_xbyq =  URLEncoder.encode(xbyq,"GB2312");
            sub_xlyq =  URLEncoder.encode(xlyq,"GB2312");
            sub_gzjy =  URLEncoder.encode(gzjy,"GB2312");
            sub_mizu =  URLEncoder.encode(mizu,"GB2312");
//            sub_yuexin =  URLEncoder.encode(yuexin,"GB2312");
            zwms =  URLEncoder.encode(zwms,"GB2312");
            lianxiren =  URLEncoder.encode(lianxiren,"GB2312");
            sub_xinzidanwei =  URLEncoder.encode(sub_xinzidanwei,"GB2312");
            sub_gzsj =  URLEncoder.encode(gongzuoshijian,"GB2312");
        } catch (Exception e) {
            e.printStackTrace();
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
                .addFormDataPart("nianlingfanwei1", nianlingfanwei1)
                .addFormDataPart("nianlingfanwei2", nianlingfanwei2)
                .addFormDataPart("xinzi", xinzi)
                .addFormDataPart("xinzidanwei", sub_xinzidanwei)
                .addFormDataPart("gongzuoshijian", sub_gzsj)
                .addFormDataPart("content", zwms)
                .addFormDataPart("lianxiren", lianxiren)
                .addFormDataPart("lianxidianhua", lianxidianhua)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid)
                .addFormDataPart("xinxiID", xxid);
//                .addFormDataPart("gudingyuefen", gudingyuefen);

//                .addFormDataPart("key", bodyParams);
        int position = selImageList.size() - imageList.size();
        for (int i = 0; i < imageList.size(); i++) {
            File file = new File(imageList.get(i).getPath());
            if(imageList.get(i).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            }else if(imageList.get(i).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }else if(imageList.get(i).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
            }else if(imageList.get(i).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/gif"), file));
            }
        }
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/hrxiugaiJZ.asp").post(requestBody).build();
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

//            cityList.addAll(getChildList(provinceList.get(0).getTid())) ;
//            for(int i=0;i<cityList.size();i++){
//                city_arr.add(cityList.get(i).getName());
//            }
//
//            regionList  = getChildList(cityList.get(0).getTid());
//            for(int i=0;i<regionList.size();i++){
//                region_arr.add(regionList.get(i).getName()) ;
//            }
//
//            townList  = getChildList(regionList.get(0).getTid());
//            for(int i=0;i<townList.size();i++){
//                town_arr.add(townList.get(i).getName());
//            }

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

    //获取数据
    public void getHrDetails(){
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                 .add("xxid",xxid).build();
        Request request;
        if(type == 0){
            request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getHrDetails.asp").post(requestBody).build();
        }else{
            request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getJZHrDetails.asp").post(requestBody).build();
        }

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
                    Log.d("qiuzhiguanli","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<RecruitContentBean> result = gson.fromJson(content, new TypeToken<Entity<RecruitContentBean>>(){}.getType());
                        if(result.getCode() == 0){
                            recruitContentBean = result.getData();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }
                }
            }
        });
    }

    //删除图片
    public void deletetupian(){

        for(int i=0;i<del_imageList.size();i++){
            String path = del_imageList.get(i).getPath();
            System.out.println("path------------"+path);
            if(path.startsWith(HttpUtils.URL)){
                deletetupianImg(path.replace(HttpUtils.URL+"/",""));
            }
        }
    }

    public void deletetupianImg(String tupianmingcheng){
        System.out.println("deletetupianImg------------"+tupianmingcheng);
        String pd = "";
        try {
            pd =  URLEncoder.encode(pindao,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                .add("tupianmingcheng",tupianmingcheng).addEncoded("pindao",pd).add("xinxiID",xxid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/deletetupian.asp").post(requestBody).build();
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
                    Log.d("deletetupianImg","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>(){}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(DELETE_OK);
                        }else{
                            handler.sendEmptyMessage(DELETE_ERROR);
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

