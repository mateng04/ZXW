package com.mobile.zxw.myapplication.activity;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.mobile.zxw.myapplication.bean.SettingBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
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

public class AdvertisingReleaseActivity extends AppCompatActivity implements View.OnClickListener,ImagePickerAdapter.OnRecyclerViewItemClickListener {

    private Context mContext;
    //加载对话框
    private Dialog mLoadingDialog;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sp_setting;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String yue,yue3,yue6,yue12;
    private List<String>  zdyfList;

    Spinner sp_release_ad_xzpd;
    ArrayAdapter<String> xzpdAdapter;
    private List<String>  pdList;

    Spinner sp_release_ad_xzyf;
    ArrayAdapter<String> xzyfAdapter;
    String guanggaoyuefen;

    private String Select1,Select2;
    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<String>  province_arr;
    private List<String>  city_arr;
    Spinner sp_release_ad_province, sp_release_ad_city;
    ArrayAdapter<String> provinceadapter;
    ArrayAdapter<String> citityAdapter;

    EditText sp_release_ad_ggbt,sp_release_ad_ggnr;
    Button bt_release_ad_fbgg;
    Button bt_release_ad_back;

    RecyclerView recycler_release_ad_nrtp;
    RecyclerView recycler_release_ad_ggtp;

    private int wxtp_maxImgCount = 9;               //允许选择图片最大数
    private ImagePickerAdapter  wxtp_imagePickerAdapter;
    private ArrayList<ImageItem>  wxtp_selImageList; //当前选择的所有图片
    private ArrayList<ImageItem>  wxtp_sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem>  wxtp_del_imageList = new ArrayList<>(); //需要删除的图片
    public static final int IMAGE_ITEM_ADD = -1;
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

    TextView tv_release_ad_yue;
    String zhye;
    String pindao;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int YE_DATA_OK = 2;
    static int YE_DATA_ERROR = 3;
    static int FB_OK = 4;
    static int FB_STATE1 = 5;
    static int FB_STATE2 = 6;
    static int DATA_XTSZ_S = 7; //  获取系统设置成功
    static int DATA_XTSZ_F = 8;//  获取系统设置失败

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    for(int i=0;i<provinceList.size();i++){
                        province_arr.add(provinceList.get(i).getName());
                    }
                    provinceadapter.notifyDataSetChanged();
                    cancelDialog();
                    break;
                case 1:
                    break;
                case 2:
                    tv_release_ad_yue.setText(zhye);
                    break;
                case 3:

                    break;
                case 4:
                    cancelDialog();
                    Toast.makeText(mContext,"广告发布成功!",Toast.LENGTH_LONG).show();
                    finish();
                    break;
                case 5:
//                    adapter_qzzp.notifyDataSetChanged();
                    Toast.makeText(mContext,"登录超时!",Toast.LENGTH_LONG).show();
                    cancelDialog();
                    break;
                case 6:
                    cancelDialog();
                    Toast.makeText(mContext,"您的账户余额不足，无法发布广告，请先充值!",Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    yue = (String) sp_setting.getSharedPreference("indexadyue", "");
                    yue3 = (String) sp_setting.getSharedPreference("indexadyue3", "");
                    yue6 = (String) sp_setting.getSharedPreference("indexadyue6", "");
                    yue12 = (String) sp_setting.getSharedPreference("indexadyue12", "");
                    initData();
                    break;
                case 8:
                    cancelDialog();
                    Toast.makeText(mContext,"获取数据失败，请重新打开页面",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertising_release);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                mContext, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        sp_setting = new SharedPreferencesHelper(
                mContext, "setting");
//        yue = (String) sp_setting.getSharedPreference("adyue", "");
//        yue3 = (String) sp_setting.getSharedPreference("adyue3", "");
//        yue6 = (String) sp_setting.getSharedPreference("adyue6", "");
//        yue12 = (String) sp_setting.getSharedPreference("adyue12", "");

        yue = (String) sp_setting.getSharedPreference("indexadyue", "");
        yue3 = (String) sp_setting.getSharedPreference("indexadyue3", "");
        yue6 = (String) sp_setting.getSharedPreference("indexadyue6", "");
        yue12 = (String) sp_setting.getSharedPreference("indexadyue12", "");

        initView();
        getxitongshezhi();
//        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getYuE();
    }
    public void initView(){
        tv_release_ad_yue  = (TextView) findViewById(R.id.tv_release_ad_yue);

        sp_release_ad_xzpd  = (Spinner)findViewById(R.id.sp_release_ad_xzpd);
        sp_release_ad_province  = (Spinner)findViewById(R.id.sp_release_ad_province);
        sp_release_ad_city  = (Spinner)findViewById(R.id.sp_release_ad_city);
        sp_release_ad_xzyf  = (Spinner)findViewById(R.id.sp_release_ad_xzyf);

        sp_release_ad_ggbt = (EditText) findViewById(R.id.sp_release_ad_ggbt);
        sp_release_ad_ggnr = (EditText) findViewById(R.id.sp_release_ad_ggnr);

        bt_release_ad_back = (Button)findViewById(R.id.bt_release_ad_back);
        bt_release_ad_back.setOnClickListener(this);
        bt_release_ad_fbgg = (Button)findViewById(R.id.bt_release_ad_fbgg);
        bt_release_ad_fbgg.setOnClickListener(this);

        initImagePicker();

        initWXTPWidget();
        initWXEWMWidget();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(wxtp_maxImgCount);              //选中数量限制
    }

    private void initWXTPWidget() {
        recycler_release_ad_nrtp = (RecyclerView) findViewById(R.id.recycler_release_ad_nrtp);
        wxtp_selImageList = new ArrayList<>();
        wxtp_imagePickerAdapter = new ImagePickerAdapter(this, wxtp_selImageList, wxtp_maxImgCount,2);
        wxtp_imagePickerAdapter.setOnItemClickListener(this);

        recycler_release_ad_nrtp.setLayoutManager(new GridLayoutManager(this, 3));
        recycler_release_ad_nrtp.setHasFixedSize(true);
        recycler_release_ad_nrtp.setAdapter(wxtp_imagePickerAdapter);
    }

    private void initWXEWMWidget() {
        recycler_release_ad_ggtp = (RecyclerView) findViewById(R.id.recycler_release_ad_ggtp);
        wxewm_selImageList = new ArrayList<>();
        wxewm_imagePickerAdapter = new ImagePickerAdapter(this, wxewm_selImageList, wxewm_maxImgCount,3);
        wxewm_imagePickerAdapter.setOnItemClickListener(this);

        recycler_release_ad_ggtp.setLayoutManager(new GridLayoutManager(this, 3));
        recycler_release_ad_ggtp.setHasFixedSize(true);
        recycler_release_ad_ggtp.setAdapter(wxewm_imagePickerAdapter);
    }

    public void initData(){

        showDialog("数据加载中");

        pdList = new ArrayList<>();
        pdList.add("网站首页");
        pdList.add("全职招聘");
        pdList.add("兼职招聘");
        pdList.add("全职简历");
        pdList.add("兼职简历");
        pdList.add("正信商城");
        pdList.add("微商专区");
        xzpdAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,pdList);
        sp_release_ad_xzpd.setAdapter(xzpdAdapter);

        zdyfList = new ArrayList<>();
        zdyfList.add("一个月(￥"+yue+"元)");
        zdyfList.add("三个月(￥"+yue3+"元)");
        zdyfList.add("六个月(￥"+yue6+"元)");
        zdyfList.add("十二个月(￥"+yue12+"元)");
        xzyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zdyfList);
        sp_release_ad_xzyf.setAdapter(xzyfAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        provinceadapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_release_ad_province.setAdapter(provinceadapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_release_ad_city.setAdapter(citityAdapter);

        setAdapterOnItemClick();

        new NewsAsyncTask().execute();
    }

    private void setAdapterOnItemClick(){
        sp_release_ad_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_release_ad_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_release_ad_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_release_ad_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Select2  = cityList.get(position).getTid()+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_release_ad_xzyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    guanggaoyuefen = "1";
                }else if(position == 1){
                    guanggaoyuefen = "3";
                }else if(position == 2){
                    guanggaoyuefen = "6";
                }else if(position == 3){
                    guanggaoyuefen = "12";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_release_ad_xzpd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pindao = pdList.get(position);
                if("网站首页".equals(pindao)){
                    yue = (String) sp_setting.getSharedPreference("indexadyue", "");
                    yue3 = (String) sp_setting.getSharedPreference("indexadyue3", "");
                    yue6 = (String) sp_setting.getSharedPreference("indexadyue6", "");
                    yue12 = (String) sp_setting.getSharedPreference("indexadyue12", "");
                    zdyfList.clear();
                    zdyfList.add("一个月(￥"+yue+"元)");
                    zdyfList.add("三个月(￥"+yue3+"元)");
                    zdyfList.add("六个月(￥"+yue6+"元)");
                    zdyfList.add("十二个月(￥"+yue12+"元)");
                }else{
                    yue = (String) sp_setting.getSharedPreference("adyue", "");
                    yue3 = (String) sp_setting.getSharedPreference("adyue3", "");
                    yue6 = (String) sp_setting.getSharedPreference("adyue6", "");
                    yue12 = (String) sp_setting.getSharedPreference("adyue12", "");
                    zdyfList.clear();
                    zdyfList.add("一个月(￥"+yue+"元)");
                    zdyfList.add("三个月(￥"+yue3+"元)");
                    zdyfList.add("六个月(￥"+yue6+"元)");
                    zdyfList.add("十二个月(￥"+yue12+"元)");
                }


                sp_release_ad_xzyf.setAdapter(xzyfAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_release_ad_fbgg :
                guanggaofabu();
                break;
            case R.id.bt_release_ad_back :
                finish();
                break;
            default:
                break;
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

            String data_all= StreamUtils.get(APP.getInstance(), R.raw.area_all);
            allList = gson.fromJson(data_all, type);
            String data_all2= StreamUtils.get(APP.getInstance(), R.raw.area_all2);
            List allList2 = gson.fromJson(data_all2, type);
            allList.addAll(allList2);

            handler.sendEmptyMessage(DATA_OK);
            return null;
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

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {

            case IMAGE_ITEM_ADD:
                System.out.println("xxxxxxxx-----"+IMAGE_ITEM_ADD);
                ImagePicker.getInstance().setSelectLimit(wxtp_maxImgCount - wxtp_selImageList.size());
                Intent intent2 = new Intent(mContext, ImageGridActivity.class);
                startActivityForResult(intent2, WXTP_REQUEST_CODE_SELECT);
                break;

            case IMAGE_ITEM_ADD_3:
                //打开选择,本次允许选择的数量
                System.out.println("tttttttt-----"+IMAGE_ITEM_ADD_3);
                ImagePicker.getInstance().setSelectLimit(wxewm_maxImgCount - wxewm_selImageList.size());
                Intent intent3 = new Intent(mContext, ImageGridActivity.class);
                startActivityForResult(intent3, WXEWM_REQUEST_CODE_SELECT);


                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);

                if(position == IMAGE_ITEM_ADD_33){
                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) wxewm_imagePickerAdapter.getImages());
                }else{

                    intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) wxtp_imagePickerAdapter.getImages());
                }
                if(position == IMAGE_ITEM_ADD_33){
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
                }else{
                    intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                }

                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);

                if(position == IMAGE_ITEM_ADD_33){
                    startActivityForResult(intentPreview, WXEWM_REQUEST_CODE_PREVIEW);
                }else{
                    startActivityForResult(intentPreview, WXTP_REQUEST_CODE_PREVIEW);
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
            if(data != null && requestCode == WXTP_REQUEST_CODE_SELECT){
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
            if(data != null && requestCode == WXTP_REQUEST_CODE_PREVIEW){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {


                    //需要上传的图片
                    wxtp_selImageList.removeAll(images);
                    wxtp_sub_imageList.removeAll(wxtp_selImageList);


                    wxtp_selImageList.clear();
                    wxtp_selImageList.addAll(images);
                    wxtp_imagePickerAdapter.setImages(wxtp_selImageList);
                }
            }else if(data != null && requestCode == WXEWM_REQUEST_CODE_PREVIEW){
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (images != null) {

                    //需要上传的图片
                    wxewm_selImageList.removeAll(images);
                    wxewm_sub_imageList.removeAll(wxewm_selImageList);

                    wxewm_selImageList.clear();
                    wxewm_selImageList.addAll(images);
                    wxewm_imagePickerAdapter.setImages(wxewm_selImageList);
                }
            }
        }
    }

    //获取余额
    public void getYuE(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
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


    public void guanggaofabu(){
        String title = sp_release_ad_ggbt.getText().toString().trim();
        String content = sp_release_ad_ggnr.getText().toString().trim();


        if("".equals(title)){
            Toast.makeText(mContext,"广告标题不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equals(content)){
            Toast.makeText(mContext,"广告不能为空",Toast.LENGTH_LONG).show();
            return;
        }

        if(wxewm_selImageList.size() == 0){
            Toast.makeText(mContext,"广告图片不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(wxtp_selImageList.size() == 0){
            Toast.makeText(mContext,"最少添加一条内容图片",Toast.LENGTH_LONG).show();
            return;
        }

        showDialog("正在提交数据");

        System.out.println("wxtp_selImageList-------"+wxtp_selImageList.size());

        try {
            title =  URLEncoder.encode(title,"GB2312");
            content =  URLEncoder.encode(content,"GB2312");
            pindao =  URLEncoder.encode(pindao,"GB2312");
        } catch (Exception e) {
        } finally {
        }

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userid",userid)
                .addFormDataPart("sessionID",sessionID)
                .addFormDataPart("Select1",Select1)
                .addFormDataPart("Select2",Select2)
                .addFormDataPart("guanggaoyuefen",guanggaoyuefen)
                .addFormDataPart("title", title)
                .addFormDataPart("pindao", pindao)
                .addFormDataPart("content", content);


        if(wxewm_sub_imageList.size() > 0){
            File file_ewm = new File(wxewm_sub_imageList.get(0).getPath());
            if(wxewm_sub_imageList.get(0).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("img", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpg"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("img", file_ewm.getName(), RequestBody.create(MediaType.parse("image/png"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("img", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("img", file_ewm.getName(), RequestBody.create(MediaType.parse("image/gif"), file_ewm));
            }
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
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/guanggaofabu.asp").post(requestBody).build();
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
                            handler.sendEmptyMessage(FB_STATE2);
                        }

                    }
                }
            }
        });
    }

    //获取系统设置
    public void getxitongshezhi(){
        RequestBody requestBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/getxitongshezhi.asp").post(requestBody).build();
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
                    Log.d("getxitongshezhi","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<SettingBean> result = gson.fromJson(content, new TypeToken<Entity<SettingBean>>() {}.getType());
                        SharedPreferencesHelper sp_setting = new SharedPreferencesHelper(
                                AdvertisingReleaseActivity.this, "setting");
                        if(result.getCode() == 0){
                            SettingBean settingBean = result.getData();
                            sp_setting.put("ci",settingBean.getCi());
                            sp_setting.put("yue",settingBean.getYue());
                            sp_setting.put("yue3",settingBean.getYue3());
                            sp_setting.put("yue6",settingBean.getYue6());
                            sp_setting.put("yue12",settingBean.getYue12());
                            sp_setting.put("indexadyue",settingBean.getIndexadyue());
                            sp_setting.put("indexadyue3",settingBean.getIndexadyue3());
                            sp_setting.put("indexadyue6",settingBean.getIndexadyue6());
                            sp_setting.put("indexadyue12",settingBean.getIndexadyue12());
                            sp_setting.put("adyue",settingBean.getAdyue());
                            sp_setting.put("adyue3",settingBean.getAdyue3());
                            sp_setting.put("adyue6",settingBean.getAdyue6());
                            sp_setting.put("adyue12",settingBean.getAdyue12());
                            sp_setting.put("gudingyue",settingBean.getGudingyue());
                            sp_setting.put("gudingyue3",settingBean.getGudingyue3());
                            sp_setting.put("gudingyue6",settingBean.getGudingyue6());
                            sp_setting.put("gudingyue12",settingBean.getGudingyue12());
                            sp_setting.put("shopfabu",settingBean.getShopfabu());
                            sp_setting.put("tel1",settingBean.getTel1());
                            sp_setting.put("shopyajin",settingBean.getShopyajin());
                            handler.sendEmptyMessage(DATA_XTSZ_S);
                        }else{
                            handler.sendEmptyMessage(DATA_XTSZ_F);
                        }
                    }else{
                        handler.sendEmptyMessage(DATA_XTSZ_F);
                    }
                }
            }
        });
    }
}


