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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.mobile.zxw.myapplication.bean.BigShopBean;
import com.mobile.zxw.myapplication.bean.SmallShopBean;
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

public class ReleaseMallActivity extends AppCompatActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener {

    private Context mContext;

    TextView tv_release_mall_cz;    //充值

    LinearLayout ll_release_mall_wxzq;
    TextView tv_release_mall_wxzq_yxqz_content;
    EditText et_release_mall_wxzq_spbt,et_release_mal_wxzql_spxq,et_release_mall_wxzq_wxzh;
    RecyclerView recycler_release_mall_wxzq_wsewm;
    RecyclerView recycler_release_mall_wxzq_sptp;
    Spinner sp_release_mall_wxzq_zdyf;
    LinearLayout ll_release_mall_zxsc;
    private List<String>  wxzq_zdyfList;
    ArrayAdapter<String> wxzq_zdyfAdapter;
    String wxzq_guding_yuefen;

    RecyclerView recyclerView_wxtp;
    RecyclerView recyclerView_wxewm;

    private int wxtp_maxImgCount = 9;               //允许选择图片最大数
    private ImagePickerAdapter  wxtp_imagePickerAdapter;
    private ArrayList<ImageItem>  wxtp_selImageList; //当前选择的所有图片
    private ArrayList<ImageItem>  wxtp_sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem>  wxtp_del_imageList = new ArrayList<>(); //需要删除的图片
//    public static final int IMAGE_ITEM_ADD = -1;
    public static final int  WXTP_REQUEST_CODE_SELECT = 200;
    public static final int WXTP_REQUEST_CODE_PREVIEW = 201;

    private int wxewm_maxImgCount = 1;               //允许选择图片最大数
    private ImagePickerAdapter wxewm_imagePickerAdapter;
    private ArrayList<ImageItem>  wxewm_selImageList; //当前选择的所有图片
    private ArrayList<ImageItem>  wxewm_sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem>  wxewm_del_imageList = new ArrayList<>(); //需要删除的图片
    public static final int IMAGE_ITEM_ADD_3 = -3;
    public static final int IMAGE_ITEM_ADD_33 = 100;
    public static final int  WXEWM_REQUEST_CODE_SELECT = 300;
    public static final int WXEWM_REQUEST_CODE_PREVIEW = 301;

    //***************************************************************//

    private Button bt_release_mall_back;
    //加载对话框
    private Dialog mLoadingDialog;
    String sessionID;
    String userid;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sp_setting;
    private SharedPreferencesHelper sharedPreferencesHelper;

    int type;   //0正信商城    1微商专区
    String xxid;

    Spinner sp_release_mall_splx;
    ArrayAdapter<String> splxAdapter;
    private List<String> splx_arr;

    Spinner sp_release_mall_spdl,sp_release_mall_spxl;
    String bigshopclass,smallshopclass;
    ArrayAdapter<String> dlAdapter;
    ArrayAdapter<String> xlAdapter;
    private List<String> dl_arr;
    private List<String>  xl_arr;
    List<BigShopBean> dl_List = new ArrayList<>();
    List<BigShopBean> dl_lishi_List;
    List<SmallShopBean> xl_List = new ArrayList<>();
    List<SmallShopBean> xl_lishi_List;

    Spinner sp_release_mall_rovince,sp_release_mall_city,sp_release_mall_county,sp_release_mall_town;
    private String Select1,Select2,Select3,Select4;
    ArrayAdapter<String> rovinceAdapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;
    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
    private List<AreaBean> townList;
    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;

    TextView tv_release_mall_yxqz_content;
    Calendar calendar;

    EditText et_release_mall_spbt,et_release_mall_spxh,et_release_mall_spdw,et_release_mall_spdj,
            et_release_mall_spsl,et_release_mall_spyf,et_release_mall_spxq;

    private int maxImgCount = 9;               //允许选择图片最大数
    private ImagePickerAdapter imagePickerAdapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private ArrayList<ImageItem> sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem> del_imageList = new ArrayList<>(); //需要删除的图片
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private String pindao = "正信商城";

    EditText et_release_mall_shuxing1,et_release_mall_shuxingneirong1,et_release_mall_shuxing2,et_release_mall_shuxingneirong2,et_release_mall_shuxing3,et_release_mall_shuxingneirong3,
            et_release_mall_shuxing4,et_release_mall_shuxingneirong4,et_release_mall_shuxing5,et_release_mall_shuxingneirong5,et_release_mall_shuxing6,et_release_mall_shuxingneirong6,
            et_release_mall_shuxing7,et_release_mall_shuxingneirong7,et_release_mall_shuxing8,et_release_mall_shuxingneirong8,et_release_mall_shuxing9,et_release_mall_shuxingneirong9,
            et_release_mall_shuxing10,et_release_mall_shuxingneirong10,et_release_mall_shuxing11,et_release_mall_shuxingneirong11,et_release_mall_shuxing12,et_release_mall_shuxingneirong12,
            et_release_mall_shuxing13,et_release_mall_shuxingneirong13,et_release_mall_shuxing14,et_release_mall_shuxingneirong14,et_release_mall_shuxing15,et_release_mall_shuxingneirong15,
            et_release_mall_shuxing16,et_release_mall_shuxingneirong16,et_release_mall_shuxing17,et_release_mall_shuxingneirong17,et_release_mall_shuxing18,et_release_mall_shuxingneirong18,
            et_release_mall_shuxing19,et_release_mall_shuxingneirong19,et_release_mall_shuxing20,et_release_mall_shuxingneirong20;

    Spinner sp_release_mall_zdyf;
    private List<String>  zdyfList;
    ArrayAdapter<String> zdyfAdapter;
    String guding_yuefen;
    private String gudingyue,gudingyue3,gudingyue6,gudingyue12;

    Button  bt_release_mall_fbsp;

    List<String> shuxing_list;
    List<String> shuxingneirong_list;

    static int DL_OK = 0;
    static int DL_ERROR = 1;
    static int XL_OK = 2;
    static int XL_ERROR = 3;
    static int DATA_JC = 4;
    static int FB_OK = 5;
    static int FB_STATE1 = 6;
    static int FB_STATE2 = 7;
    static int FB_STATE3 = 8;
    static int FB_STATE4 = 9;
    static int FB_STATE5 = 10;
    static int TIMEOUT = 100;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    dl_List.addAll(dl_lishi_List);
                    setDLScreenData();
                    break;
                case 1:
                    break;
                case 2:
                    xl_List.clear();
                    xl_arr.clear();
                    xl_List.addAll(xl_lishi_List);
                    setXLScreenData();
                    break;
                case 3:
                    break;
                case 4:
                    rovinceAdapter.notifyDataSetChanged();
                    citityAdapter.notifyDataSetChanged();
                    regionAdapter.notifyDataSetChanged();
                    townAdapter.notifyDataSetChanged();
                    zdyfAdapter.notifyDataSetChanged();
                    wxzq_zdyfAdapter.notifyDataSetChanged();
                    cancelDialog();
                    break;
                case 5:
                    cancelDialog();
                    Toast.makeText(mContext,"发布商品成功!",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 6:
                    cancelDialog();
//                    Toast.makeText(mContext,"",Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    cancelDialog();
                    Toast.makeText(mContext,"您的商城押金不足，无法发布信息，请先充值!",Toast.LENGTH_LONG).show();
                    break;
                case 8:
                    cancelDialog();
                    Toast.makeText(mContext,"您的账户余额不足，无法发布信息，请先充值!",Toast.LENGTH_LONG).show();
                    break;
                case 9:
                    cancelDialog();
                    Toast.makeText(mContext,"您的账户余额不足，无法发布置顶信息，请充值后发布!",Toast.LENGTH_LONG).show();
                    break;
                case 10:
                    cancelDialog();
                    Toast.makeText(mContext,"信息已经发布成功，但是您的账户余额不够扣除置顶费用，无法实现信息置顶,请充值!",Toast.LENGTH_LONG).show();
                    break;
                case 100:
                    cancelDialog();
                    Toast.makeText(mContext,"连接服务器异常，请重新发布",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release_mall);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, "config");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xxid = getIntent().getStringExtra("xxid");

        showDialog("正在加载数据");

        //最好放到 Application oncreate执行
        initImagePicker();
        initWidget();

        initWXTPWidget();
        initWXEWMWidget();

        initView();
        initData();
    }
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
    }

    private void initWidget() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_release_mall);
        selImageList = new ArrayList<>();
        imagePickerAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount,1);
        imagePickerAdapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagePickerAdapter);
    }

    private void initWXTPWidget() {
        recyclerView_wxtp = (RecyclerView) findViewById(R.id.recycler_release_mall_wxzq_sptp);
        wxtp_selImageList = new ArrayList<>();
        wxtp_imagePickerAdapter = new ImagePickerAdapter(this, wxtp_selImageList, wxtp_maxImgCount,2);
        wxtp_imagePickerAdapter.setOnItemClickListener(this);

        recyclerView_wxtp.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView_wxtp.setHasFixedSize(true);
        recyclerView_wxtp.setAdapter(wxtp_imagePickerAdapter);
    }

    private void initWXEWMWidget() {
        recyclerView_wxewm = (RecyclerView) findViewById(R.id.recycler_release_mall_wxzq_wsewm);
        wxewm_selImageList = new ArrayList<>();
        wxewm_imagePickerAdapter = new ImagePickerAdapter(this, wxewm_selImageList, wxewm_maxImgCount,3);
        wxewm_imagePickerAdapter.setOnItemClickListener(this);

        recyclerView_wxewm.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView_wxewm.setHasFixedSize(true);
        recyclerView_wxewm.setAdapter(wxewm_imagePickerAdapter);
    }


    public void initView(){

        bt_release_mall_back = (Button) findViewById(R.id.bt_release_mall_back);
        bt_release_mall_back.setOnClickListener(this);

        ll_release_mall_wxzq = (LinearLayout)findViewById(R.id.ll_release_mall_wxzq);
        tv_release_mall_wxzq_yxqz_content = (TextView)findViewById(R.id.tv_release_mall_wxzq_yxqz_content);
        tv_release_mall_wxzq_yxqz_content.setOnClickListener(this);
        et_release_mall_wxzq_spbt = (EditText)findViewById(R.id.et_release_mall_wxzq_spbt);
        et_release_mal_wxzql_spxq = (EditText)findViewById(R.id.et_release_mal_wxzql_spxq);
        et_release_mal_wxzql_spxq.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.et_release_mal_wxzql_spxq:
                        // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                        if (canVerticalScroll(et_release_mal_wxzql_spxq))
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);//告诉父view，你可以处理了
                        }
                }
                return false;
            }
        });
        et_release_mall_wxzq_wxzh = (EditText)findViewById(R.id.et_release_mall_wxzq_wxzh);
        sp_release_mall_wxzq_zdyf = (Spinner)findViewById(R.id.sp_release_mall_wxzq_zdyf);
        ll_release_mall_zxsc = (LinearLayout)findViewById(R.id.ll_release_mall_zxsc);

        if(type == 0){
            ll_release_mall_zxsc.setVisibility(View.VISIBLE);
            ll_release_mall_wxzq.setVisibility(View.GONE);
        }else{
            ll_release_mall_zxsc.setVisibility(View.GONE);
            ll_release_mall_wxzq.setVisibility(View.VISIBLE);
        }

        //*****************************************************************************//
        sp_release_mall_splx = (Spinner)findViewById(R.id.sp_release_mall_splx);

        sp_release_mall_spdl = (Spinner)findViewById(R.id.sp_release_mall_spdl);
        sp_release_mall_spxl = (Spinner)findViewById(R.id.sp_release_mall_spxl);

        sp_release_mall_rovince = (Spinner)findViewById(R.id.sp_release_mall_rovince);
        sp_release_mall_city = (Spinner)findViewById(R.id.sp_release_mall_city);
        sp_release_mall_county = (Spinner)findViewById(R.id.sp_release_mall_county);
        sp_release_mall_town = (Spinner)findViewById(R.id.sp_release_mall_town);

        tv_release_mall_yxqz_content = (TextView) findViewById(R.id.tv_release_mall_yxqz_content);
        tv_release_mall_yxqz_content.setOnClickListener(this);

        et_release_mall_spbt = (EditText) findViewById(R.id.et_release_mall_spbt);
        et_release_mall_spxh = (EditText) findViewById(R.id.et_release_mall_spxh);
        et_release_mall_spdw = (EditText) findViewById(R.id.et_release_mall_spdw);
        et_release_mall_spdj = (EditText) findViewById(R.id.et_release_mall_spdj);
        et_release_mall_spsl = (EditText) findViewById(R.id.et_release_mall_spsl);
        et_release_mall_spyf = (EditText) findViewById(R.id.et_release_mall_spyf);
        et_release_mall_spxq = (EditText) findViewById(R.id.et_release_mall_spxq);
        et_release_mall_spxq.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (v.getId()) {
                    case R.id.et_release_mall_spxq:
                        // 解决scrollView中嵌套EditText导致不能上下滑动的问题
                        if (canVerticalScroll(et_release_mall_spxq))
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.getParent().requestDisallowInterceptTouchEvent(false);//告诉父view，你可以处理了
                        }
                }
                return false;
            }
        });
        et_release_mall_shuxing1 = (EditText) findViewById(R.id.et_release_mall_shuxing1);
        et_release_mall_shuxingneirong1 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong1);
        et_release_mall_shuxing2 = (EditText) findViewById(R.id.et_release_mall_shuxing2);
        et_release_mall_shuxingneirong2 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong2);
        et_release_mall_shuxing3 = (EditText) findViewById(R.id.et_release_mall_shuxing3);
        et_release_mall_shuxingneirong3 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong3);
        et_release_mall_shuxing4 = (EditText) findViewById(R.id.et_release_mall_shuxing4);
        et_release_mall_shuxingneirong4 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong4);
        et_release_mall_shuxing5 = (EditText) findViewById(R.id.et_release_mall_shuxing5);
        et_release_mall_shuxingneirong5 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong5);
        et_release_mall_shuxing6 = (EditText) findViewById(R.id.et_release_mall_shuxing6);
        et_release_mall_shuxingneirong6 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong6);
        et_release_mall_shuxing7 = (EditText) findViewById(R.id.et_release_mall_shuxing7);
        et_release_mall_shuxingneirong7 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong7);
        et_release_mall_shuxing8 = (EditText) findViewById(R.id.et_release_mall_shuxing8);
        et_release_mall_shuxingneirong8 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong8);
        et_release_mall_shuxing9 = (EditText) findViewById(R.id.et_release_mall_shuxing9);
        et_release_mall_shuxingneirong9 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong9);
        et_release_mall_shuxing10 = (EditText) findViewById(R.id.et_release_mall_shuxing10);
        et_release_mall_shuxingneirong10 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong10);
        et_release_mall_shuxing11 = (EditText) findViewById(R.id.et_release_mall_shuxing11);
        et_release_mall_shuxingneirong11 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong11);
        et_release_mall_shuxing12 = (EditText) findViewById(R.id.et_release_mall_shuxing12);
        et_release_mall_shuxingneirong12 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong12);
        et_release_mall_shuxing13 = (EditText) findViewById(R.id.et_release_mall_shuxing13);
        et_release_mall_shuxingneirong13 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong13);
        et_release_mall_shuxing14 = (EditText) findViewById(R.id.et_release_mall_shuxing14);
        et_release_mall_shuxingneirong14 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong14);
        et_release_mall_shuxing15 = (EditText) findViewById(R.id.et_release_mall_shuxing15);
        et_release_mall_shuxingneirong15 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong15);
        et_release_mall_shuxing16 = (EditText) findViewById(R.id.et_release_mall_shuxing16);
        et_release_mall_shuxingneirong16 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong16);
        et_release_mall_shuxing17 = (EditText) findViewById(R.id.et_release_mall_shuxing17);
        et_release_mall_shuxingneirong17 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong17);
        et_release_mall_shuxing18 = (EditText) findViewById(R.id.et_release_mall_shuxing18);
        et_release_mall_shuxingneirong18 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong18);
        et_release_mall_shuxing19 = (EditText) findViewById(R.id.et_release_mall_shuxing19);
        et_release_mall_shuxingneirong19 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong19);
        et_release_mall_shuxing20 = (EditText) findViewById(R.id.et_release_mall_shuxing20);
        et_release_mall_shuxingneirong20 = (EditText) findViewById(R.id.et_release_mall_shuxingneirong20);

        sp_release_mall_zdyf = (Spinner)findViewById(R.id.sp_release_mall_zdyf);

        bt_release_mall_fbsp = (Button) findViewById(R.id.bt_release_mall_fbsp);
        bt_release_mall_fbsp.setOnClickListener(this);

        tv_release_mall_cz = (TextView) findViewById(R.id.tv_release_mall_cz);
        tv_release_mall_cz.setOnClickListener(this);

        setAdapter();
        setAdapterOnItemClick();
    }

    private void initData(){
//        showDialog("数据加载中");
        //从服务器获取大类
        BigShopClass();
        //从服务器获取月薪
//        YueXiClass();
        //异步加载基础数据
        new NewsAsyncTask().execute();
    }

    private void setAdapter(){
        splx_arr = new ArrayList<>();
        splx_arr.add("正信商城");
        splx_arr.add("微商专区");
        splxAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,splx_arr);
        sp_release_mall_splx.setAdapter(splxAdapter);

        dl_arr = new ArrayList<>();
        dlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,dl_arr);
        sp_release_mall_spdl.setAdapter(dlAdapter);
        xl_arr = new ArrayList<>();
        xlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xl_arr);
        sp_release_mall_spxl.setAdapter(xlAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        rovinceAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_release_mall_rovince.setAdapter(rovinceAdapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_release_mall_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_release_mall_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_release_mall_town.setAdapter(townAdapter);

        zdyfList = new ArrayList<>();
        zdyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zdyfList);
        sp_release_mall_zdyf.setAdapter(zdyfAdapter);

        wxzq_zdyfList = new ArrayList<>();
        wxzq_zdyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,wxzq_zdyfList);
        sp_release_mall_wxzq_zdyf.setAdapter(wxzq_zdyfAdapter);
    }

    private void setAdapterOnItemClick(){
        sp_release_mall_splx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;

                if(type != position){
                    type = position;

                    if(type == 0){
                        pindao = "正信商城";
                    }else{
                        pindao = "1微商专区";
                    }

//                    showDialog("数据加载中");
//                    qiuzhiguanli();
                }

                if(type == 0){
                    ll_release_mall_zxsc.setVisibility(View.VISIBLE);
                    ll_release_mall_wxzq.setVisibility(View.GONE);
                }else{
                    ll_release_mall_zxsc.setVisibility(View.GONE);
                    ll_release_mall_wxzq.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //简历大类
        sp_release_mall_spdl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----------sp_jobmanage_jldl:");

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_release_mall_spxl, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(position == 0){
                    bigshopclass = dl_arr.get(0);
                    SmallShopClass("");
                    return;
                }
                BigShopBean bigShopBean = dl_List.get(position-1);
                bigshopclass  = bigShopBean.getBigClassName();
                SmallShopClass( bigShopBean.getBigClassID());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //简历小类
        sp_release_mall_spxl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----------sp_jobmanage_jlxl:");
                if(position == 0){
                    smallshopclass = xl_arr.get(0);
                    return;
                }
                System.out.println("-----------smallzhiweiclass:"+smallshopclass);
                SmallShopBean smallShopBean = xl_List.get(position-1);
                smallshopclass  = smallShopBean.getSmallClassName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_release_mall_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_release_mall_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_release_mall_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_release_mall_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_release_mall_county, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_release_mall_county.setSelection(region_index);
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

        sp_release_mall_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_release_mall_town, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_release_mall_town.setSelection(town_index);
                System.out.println("xx--sp_recruit_content_county------"+town_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_release_mall_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        //置顶
        sp_release_mall_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("sp_jobmanage_zdyf-----"+position);
                if(position == 0){
                    guding_yuefen = "0";
                }else if(position == 1){
                    guding_yuefen = "1";
                }else if(position == 2){
                    guding_yuefen = "3";
                }else if(position == 3){
                    guding_yuefen = "6";
                }else if(position == 4){
                    guding_yuefen = "12";
                }
//                guding_yuefen  = zdyfList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //置顶
        sp_release_mall_wxzq_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){
                    wxzq_guding_yuefen = "0";
                }else if(position == 1){
                    wxzq_guding_yuefen = "1";
                }else if(position == 2){
                    wxzq_guding_yuefen = "3";
                }else if(position == 3){
                    wxzq_guding_yuefen = "6";
                }else if(position == 4){
                    wxzq_guding_yuefen = "12";
                }
//                guding_yuefen  = zdyfList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setDLScreenData(){

        dl_arr.add("选择大类");
        for(int i=0;i<dl_List.size();i++){
            dl_arr.add(dl_List.get(i).getBigClassName());
        }
        dlAdapter.notifyDataSetChanged();
    }

    private void setXLScreenData() {

        xl_arr.add("选择小类");

        for(int i=0;i<xl_List.size();i++){
            xl_arr.add(xl_List.get(i).getSmallClassName());
        }
        xlAdapter.notifyDataSetChanged();
        if(smallshopclass == null || "".equals(smallshopclass)){
            sp_release_mall_spxl.setSelection(0);
        }
        for(int i=0;i<xl_arr.size();i++){
            if(xl_arr.get(i).equals(smallshopclass)) {
                sp_release_mall_spxl.setSelection(i);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_release_mall_back :
                finish();
                break;
            case R.id.tv_release_mall_yxqz_content :
                showDatePickerDialog();
                break;
            case R.id.tv_release_mall_wxzq_yxqz_content :
                showDatePickerDialog();
                break;
            case R.id.tv_release_mall_cz :
                startActivity(new Intent(ReleaseMallActivity.this, OnlineRechargeActivity.class));
                break;
            case R.id.bt_release_mall_fbsp :
                if(type == 0){
                    shuxing_list = getShuxingList();
                    shuxingneirong_list = getShuXingNeiRongList();
                    shopfabu();
                }else{
                    shopfabu2();
                }

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
//                Toast.makeText(mContext, year+"year "+(monthOfYear+1)+"month "+dayOfMonth+"day", Toast.LENGTH_LONG).show();
                if(type == 0){
                    tv_release_mall_yxqz_content.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                }else{
                    tv_release_mall_wxzq_yxqz_content.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                }

            }
        }, year, month, day);
        datePicker.show();
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

    //获取职位大类
    public void BigShopClass(){

        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/BigShopClass.asp").post(requestBody).build();
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
                    Log.d("BigShopClass","BigShopClass=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<List<BigShopBean>> result = gson.fromJson(content, new TypeToken<Entity<List<BigShopBean>>>() {}.getType());
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
    public void SmallShopClass(String BigClassID){

        RequestBody requestBody = new FormBody.Builder().add("BigClassID",BigClassID).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/SmallshopClass.asp").post(requestBody).build();
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
                        Entity<List<SmallShopBean>> result = gson.fromJson(content, new TypeToken<Entity<List<SmallShopBean>>>() {}.getType());
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

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {

            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                System.out.println("IMAGE_ITEM_ADD-----"+IMAGE_ITEM_ADD);
//                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
//
////                ImagePicker.getInstance().setSelectLimit(maxImgCount);
//
//                Intent intent1 = new Intent(mContext, ImageGridActivity.class);
//                /* 如果需要进入选择的时候显示已经选中的图片，
//                 * 详情请查看ImagePickerActivity
//                 * */
////                intent1.putExtra(ImageGridActivity.EXTRAS_IMAGES,images);

                if(type == 0){
                    ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                    Intent intent1 = new Intent(mContext, ImageGridActivity.class);
                    startActivityForResult(intent1, REQUEST_CODE_SELECT);
                }else{
                    ImagePicker.getInstance().setSelectLimit(wxtp_maxImgCount - wxtp_selImageList.size());
                    Intent intent2 = new Intent(mContext, ImageGridActivity.class);
                    startActivityForResult(intent2, WXTP_REQUEST_CODE_SELECT);

                }
                break;

            case IMAGE_ITEM_ADD_3:
                //打开选择,本次允许选择的数量
                System.out.println("IMAGE_ITEM_ADD_3-----"+IMAGE_ITEM_ADD_3);
                ImagePicker.getInstance().setSelectLimit(wxewm_maxImgCount - wxewm_selImageList.size());

                Intent intent3 = new Intent(mContext, ImageGridActivity.class);

                startActivityForResult(intent3, WXEWM_REQUEST_CODE_SELECT);


                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
//                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imagePickerAdapter.getImages());
                if(type == 0){
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imagePickerAdapter.getImages());
                }else{
                    if(position == IMAGE_ITEM_ADD_33){
                        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) wxewm_imagePickerAdapter.getImages());
                    }else{

                        intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) wxtp_imagePickerAdapter.getImages());
                    }
                }
                if(position == IMAGE_ITEM_ADD_33){
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
                }else{
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                }

//                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                if(type == 0){
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                }else{

                    if(position == IMAGE_ITEM_ADD_33){
                        startActivityForResult(intentPreview, WXEWM_REQUEST_CODE_PREVIEW);
                    }else{
                        startActivityForResult(intentPreview, WXTP_REQUEST_CODE_PREVIEW);
                    }
                }

                break;
        }
    }

    ArrayList<ImageItem> images = null;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("resultCode-----"+resultCode);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            System.out.println("requestCode-----"+requestCode);
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
//                    selImageList.clear();
                    //需要上传的图片
                    sub_imageList.addAll(images);

                    selImageList.addAll(images);
                    imagePickerAdapter.setImages(selImageList);
                }
            }else if(data != null && requestCode == WXTP_REQUEST_CODE_SELECT){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
//                    selImageList.clear();
                    //需要上传的图片
                    wxtp_sub_imageList.addAll(images);

                    wxtp_selImageList.addAll(images);
                    wxtp_imagePickerAdapter.setImages(wxtp_selImageList);
                }
            }else if(data != null && requestCode == WXEWM_REQUEST_CODE_SELECT){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
//                    selImageList.clear();
                    //需要上传的图片
                    wxewm_sub_imageList.addAll(images);

                    wxewm_selImageList.addAll(images);
                    wxewm_imagePickerAdapter.setImages(wxewm_selImageList);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            //预览图片返回
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {

                    //需要上传的图片
                    sub_imageList.clear();
                    sub_imageList.addAll(images);

                    selImageList.clear();
                    selImageList.addAll(images);
                    imagePickerAdapter.setImages(selImageList);
                }
            }else if(data != null && requestCode == WXTP_REQUEST_CODE_PREVIEW){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {

                    //需要上传的图片
                    wxtp_sub_imageList.clear();
                    wxtp_sub_imageList.addAll(images);

                    wxtp_selImageList.clear();
                    wxtp_selImageList.addAll(images);
                    wxtp_imagePickerAdapter.setImages(wxtp_selImageList);
                }
            }else if(data != null && requestCode == WXEWM_REQUEST_CODE_PREVIEW){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {

                    //需要上传的图片
                    wxewm_sub_imageList.clear();
                    wxewm_sub_imageList.addAll(images);

                    wxewm_selImageList.clear();
                    wxewm_selImageList.addAll(images);
                    wxewm_imagePickerAdapter.setImages(wxewm_selImageList);
                }
            }
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

            sp_setting = new SharedPreferencesHelper(
                    mContext, "setting");
            gudingyue = (String) sp_setting.getSharedPreference("gudingyue", "");
            gudingyue3 = (String) sp_setting.getSharedPreference("gudingyue3", "");
            gudingyue6 = (String) sp_setting.getSharedPreference("gudingyue6", "");
            gudingyue12 = (String) sp_setting.getSharedPreference("gudingyue12", "");

            zdyfList.add("不置顶");
            zdyfList.add("一个月(￥"+gudingyue+"元)");
            zdyfList.add("三个月(￥"+gudingyue3+"元)");
            zdyfList.add("六个月(￥"+gudingyue6+"元)");
            zdyfList.add("十二个月(￥"+gudingyue12+"元)");

            wxzq_zdyfList.add("不置顶");
            wxzq_zdyfList.add("一个月(￥"+gudingyue+"元)");
            wxzq_zdyfList.add("三个月(￥"+gudingyue3+"元)");
            wxzq_zdyfList.add("六个月(￥"+gudingyue6+"元)");
            wxzq_zdyfList.add("十二个月(￥"+gudingyue12+"元)");

            handler.sendEmptyMessage(DATA_JC);

            //获取数据
//            qiuzhiguanli();
            return null;
        }
    }


    private  List<String> getShuxingList(){
        List<String> list_temp = new ArrayList<>();
        String shuxing1 = et_release_mall_shuxing1.getText().toString().trim();
        String shuxing2 = et_release_mall_shuxing2.getText().toString().trim();
        String shuxing3 = et_release_mall_shuxing3.getText().toString().trim();
        String shuxing4 = et_release_mall_shuxing4.getText().toString().trim();
        String shuxing5 = et_release_mall_shuxing5.getText().toString().trim();
        String shuxing6 = et_release_mall_shuxing6.getText().toString().trim();
        String shuxing7 = et_release_mall_shuxing7.getText().toString().trim();
        String shuxing8 = et_release_mall_shuxing8.getText().toString().trim();
        String shuxing9 = et_release_mall_shuxing9.getText().toString().trim();
        String shuxing10 = et_release_mall_shuxing10.getText().toString().trim();
        String shuxing11 = et_release_mall_shuxing11.getText().toString().trim();
        String shuxing12 = et_release_mall_shuxing12.getText().toString().trim();
        String shuxing13 = et_release_mall_shuxing13.getText().toString().trim();
        String shuxing14 = et_release_mall_shuxing14.getText().toString().trim();
        String shuxing15 = et_release_mall_shuxing15.getText().toString().trim();
        String shuxing16 = et_release_mall_shuxing16.getText().toString().trim();
        String shuxing17 = et_release_mall_shuxing17.getText().toString().trim();
        String shuxing18 = et_release_mall_shuxing18.getText().toString().trim();
        String shuxing19 = et_release_mall_shuxing19.getText().toString().trim();
        String shuxing20 = et_release_mall_shuxing20.getText().toString().trim();
        try {
            list_temp.add(URLEncoder.encode(shuxing1,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing2,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing3,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing4,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing5,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing6,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing7,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing8,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing9,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing10,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing11,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing12,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing13,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing14,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing15,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing16,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing17,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing18,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing19,"GB2312"));
            list_temp.add(URLEncoder.encode(shuxing20,"GB2312"));
        } catch (Exception e) {
        } finally {
        }

        return list_temp;
    }

    private  List<String> getShuXingNeiRongList(){
        List<String> shuxingneirong_temp = new ArrayList<>();
        String shuxingneirong1 = et_release_mall_shuxingneirong1.getText().toString().trim();
        String shuxingneirong2 = et_release_mall_shuxingneirong2.getText().toString().trim();
        String shuxingneirong3 = et_release_mall_shuxingneirong3.getText().toString().trim();
        String shuxingneirong4 = et_release_mall_shuxingneirong4.getText().toString().trim();
        String shuxingneirong5 = et_release_mall_shuxingneirong5.getText().toString().trim();
        String shuxingneirong6 = et_release_mall_shuxingneirong6.getText().toString().trim();
        String shuxingneirong7 = et_release_mall_shuxingneirong7.getText().toString().trim();
        String shuxingneirong8 = et_release_mall_shuxingneirong8.getText().toString().trim();
        String shuxingneirong9 = et_release_mall_shuxingneirong9.getText().toString().trim();
        String shuxingneirong10 = et_release_mall_shuxingneirong10.getText().toString().trim();
        String shuxingneirong11 = et_release_mall_shuxingneirong11.getText().toString().trim();
        String shuxingneirong12 = et_release_mall_shuxingneirong12.getText().toString().trim();
        String shuxingneirong13 = et_release_mall_shuxingneirong13.getText().toString().trim();
        String shuxingneirong14 = et_release_mall_shuxingneirong14.getText().toString().trim();
        String shuxingneirong15 = et_release_mall_shuxingneirong15.getText().toString().trim();
        String shuxingneirong16 = et_release_mall_shuxingneirong16.getText().toString().trim();
        String shuxingneirong17 = et_release_mall_shuxingneirong17.getText().toString().trim();
        String shuxingneirong18 = et_release_mall_shuxingneirong18.getText().toString().trim();
        String shuxingneirong19 = et_release_mall_shuxingneirong19.getText().toString().trim();
        String shuxingneirong20 = et_release_mall_shuxingneirong20.getText().toString().trim();

        try {
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong1,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong2,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong3,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong4,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong5,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong6,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong7,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong8,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong9,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong10,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong11,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong12,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong13,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong14,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong15,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong16,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong17,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong18,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong19,"GB2312"));
            shuxingneirong_temp.add(URLEncoder.encode(shuxingneirong20,"GB2312"));
        } catch (Exception e) {
        } finally {
        }

        return shuxingneirong_temp;
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

    private void shopfabu() {

        String youxiaoqi = tv_release_mall_yxqz_content.getText().toString().trim();
        String title = et_release_mall_spbt.getText().toString().trim();
        String xinghao = et_release_mall_spxh.getText().toString().trim();
        String content = et_release_mall_spxq.getText().toString().trim();
        String danwei = et_release_mall_spdw.getText().toString().trim();
        String jiage = et_release_mall_spdj.getText().toString().trim();
        String yunfei = et_release_mall_spyf.getText().toString().trim();
        String shuliang = et_release_mall_spsl.getText().toString().trim();


        if("".equals(bigshopclass) || "选择大类".equals(smallshopclass)
                || "".equals(bigshopclass) ||"选择小类".equals(smallshopclass)){
            Toast.makeText(mContext,"请选择商品类别",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(youxiaoqi)){
            Toast.makeText(mContext,"请选择有效期",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"商品标题不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(xinghao)){
            Toast.makeText(mContext,"商品型号不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(danwei)){
            Toast.makeText(mContext,"商品单位不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(jiage)){
            Toast.makeText(mContext,"商品单价不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(shuliang)){
            Toast.makeText(mContext,"商品数量不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(yunfei)){
            Toast.makeText(mContext,"商品运费不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(content)){
            Toast.makeText(mContext,"商品详情不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(selImageList.size() == 0){
            Toast.makeText(mContext,"最少添加一条商品图片",Toast.LENGTH_LONG).show();
            return;
        }
        showDialog("正在提交数据");


        System.out.println("selImageList-------"+selImageList.size());

        String sub_bigshopclass = "";
        String sub_smallshopclass = "";
        try {
            sub_bigshopclass =  URLEncoder.encode(bigshopclass,"GB2312");
            sub_smallshopclass =  URLEncoder.encode(smallshopclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            xinghao =  URLEncoder.encode(xinghao,"GB2312");
            content =  URLEncoder.encode(content,"GB2312");
            danwei =  URLEncoder.encode(danwei,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Select1", Select1)
                .addFormDataPart("Select2", Select2)
                .addFormDataPart("Select3", Select3)
                .addFormDataPart("Select4", Select4)
                .addFormDataPart("youxiaoqi", youxiaoqi)
                .addFormDataPart("title", title)
                .addFormDataPart("xinghao", xinghao)
                .addFormDataPart("content", content)
                .addFormDataPart("danwei", danwei)
                .addFormDataPart("jiage", jiage)
                .addFormDataPart("yunfei", yunfei)
                .addFormDataPart("shuliang", shuliang)
                .addFormDataPart("bigshopclass", sub_bigshopclass)
                .addFormDataPart("smallshopclass", sub_smallshopclass)

//                .addFormDataPart("title", title)
                .addFormDataPart("gudingyuefen", guding_yuefen)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid);

        System.out.println("imageList.get(i).getPath()---"+sub_imageList.size());
        int position = selImageList.size() - sub_imageList.size();
        for (int i = 1; i < sub_imageList.size()+1; i++) {
            File file = new File(sub_imageList.get(i-1).getPath());
            System.out.println("imageList.get(i).getPath()---"+sub_imageList.get(i-1).getPath());
            if(sub_imageList.get(i-1).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            }else if(sub_imageList.get(i-1).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }else if(sub_imageList.get(i-1).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
            }else if(sub_imageList.get(i-1).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/gif"), file));
            }
        }

        for(int i=1;i<21;i++){
            requestBodyBuilder.addFormDataPart("shuxing"+i, shuxing_list.get(i-1));
            requestBodyBuilder.addFormDataPart("shuxingneirong"+i, shuxingneirong_list.get(i-1));
        }
        RequestBody requestBody = requestBodyBuilder.build();

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopfabu.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG",e.getMessage());
                okHttpClient.dispatcher().cancelAll();
                okHttpClient.connectionPool().evictAll();
                okHttpClient.newCall(request).enqueue(this);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("shopfabu","content=="+content);
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
                        }else if(code == 4){
                            handler.sendEmptyMessage(FB_STATE4);
                        }else if(code == 5){
                            handler.sendEmptyMessage(FB_STATE5);
                        }

                    }else {
                        handler.sendEmptyMessage(TIMEOUT);
                    }
                }
            }
        });
    }

    private void shopfabu2() {

        String youxiaoqi = tv_release_mall_wxzq_yxqz_content.getText().toString().trim();
        String title = et_release_mall_wxzq_spbt.getText().toString().trim();
        String content = et_release_mal_wxzql_spxq.getText().toString().trim();
        String weixin = et_release_mall_wxzq_wxzh.getText().toString().trim();


        if("".equals(youxiaoqi)){
            Toast.makeText(mContext,"请选择有效期",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"商品标题不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(content)){
            Toast.makeText(mContext,"商品详情不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(weixin)){
            Toast.makeText(mContext,"微信账号不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(wxtp_selImageList.size() == 0){
            Toast.makeText(mContext,"最少添加一条商品图片",Toast.LENGTH_LONG).show();
            return;
        }
        if(wxewm_selImageList.size() == 0){
            Toast.makeText(mContext,"微商二维码图片不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        showDialog("正在提交数据");


        System.out.println("wxtp_selImageList-------"+wxtp_selImageList.size());

        try {
            title =  URLEncoder.encode(title,"GB2312");
            content =  URLEncoder.encode(content,"GB2312");
            weixin =  URLEncoder.encode(weixin,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("youxiaoqi", youxiaoqi)
                .addFormDataPart("title", title)
                .addFormDataPart("content", content)
                .addFormDataPart("weixin", weixin)

//                .addFormDataPart("title", title)
                .addFormDataPart("gudingyuefen", wxzq_guding_yuefen)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid);

        System.out.println("imageList.get(i).getPath()---"+sub_imageList.size());

        File file_ewm = new File(wxewm_selImageList.get(0).getPath());
        if(wxewm_selImageList.get(0).getPath().endsWith("jpg")){
            requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpg"), file_ewm));
        }else if(wxewm_selImageList.get(0).getPath().endsWith("png")){
            requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/png"), file_ewm));
        }else if(wxewm_selImageList.get(0).getPath().endsWith("jpeg")){
            requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file_ewm));
        }else if(wxewm_selImageList.get(0).getPath().endsWith("gif")){
            requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/gif"), file_ewm));
        }


        int position = wxtp_selImageList.size() - wxtp_sub_imageList.size();
        for (int i = 1; i < wxtp_sub_imageList.size()+1; i++) {
            File file = new File(wxtp_sub_imageList.get(i-1).getPath());
            System.out.println("imageList.get(i).getPath()---"+wxtp_sub_imageList.get(i-1).getPath());
            if(wxtp_sub_imageList.get(i-1).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            }else if(wxtp_sub_imageList.get(i-1).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/png"), file));
            }else if(wxtp_sub_imageList.get(i-1).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
            }else if(wxtp_sub_imageList.get(i-1).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("img"+(i+position), file.getName(), RequestBody.create(MediaType.parse("image/gif"), file));
            }
        }

        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopfabu2.asp").post(requestBody).build();
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
                    Log.d("shopfabu2","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            handler.sendEmptyMessage(FB_OK);
                        }else if(code == 1){
                            handler.sendEmptyMessage(FB_STATE1);
                        }else if(code == 2){
                            handler.sendEmptyMessage(FB_STATE3);
                        }else if(code == 3){
                            handler.sendEmptyMessage(FB_STATE4);
                        }else if(code == 4){
                            handler.sendEmptyMessage(FB_STATE5);
                        }else if(code == 5){
                            handler.sendEmptyMessage(FB_STATE5);
                        }

                    }
                }
            }
        });
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
}


