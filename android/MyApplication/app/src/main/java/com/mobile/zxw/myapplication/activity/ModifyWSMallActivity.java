package com.mobile.zxw.myapplication.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.ImagePickerAdapter;
import com.mobile.zxw.myapplication.bean.ModifyWSMallBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.until.GlideImageLoader;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.File;
import java.io.IOException;
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

public class ModifyWSMallActivity extends AppCompatActivity implements View.OnClickListener, ImagePickerAdapter.OnRecyclerViewItemClickListener {

    private Context mContext;
    private String pindao = "微商专区";

    Button bt_modify_ws_mall_back;

    TextView tv_modify_ws_mall_wxzq_yxqz_content;
    EditText et_modify_ws_mall_wxzq_spbt,et_modify_ws_mal_wxzql_spxq,et_modify_ws_mall_wxzq_wxzh;

    RecyclerView recyclerView_wxtp;
    RecyclerView recyclerView_wxewm;

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


    //加载对话框
    private Dialog mLoadingDialog;
    String sessionID;
    String userid;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sp_setting;
    private SharedPreferencesHelper sharedPreferencesHelper;

    String xxid;

    Button  bt_modify_ws_mall_fbsp;
    Calendar calendar;
    ModifyWSMallBean modifyWSMallBean;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
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
                    cancelDialog();
                    setData();
                    break;
                case 5:
                    cancelDialog();
                    Toast.makeText(mContext,"保存商品成功!",Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_modify_ws_mall);
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

        initImagePicker();

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
        imagePicker.setSelectLimit(wxtp_maxImgCount);              //选中数量限制
    }

    private void initWXTPWidget() {
        recyclerView_wxtp = (RecyclerView) findViewById(R.id.recycler_modify_ws_mall_wxzq_sptp);
        wxtp_selImageList = new ArrayList<>();
        wxtp_imagePickerAdapter = new ImagePickerAdapter(this, wxtp_selImageList, wxtp_maxImgCount,2);
        wxtp_imagePickerAdapter.setOnItemClickListener(this);

        recyclerView_wxtp.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView_wxtp.setHasFixedSize(true);
        recyclerView_wxtp.setAdapter(wxtp_imagePickerAdapter);
    }

    private void initWXEWMWidget() {
        recyclerView_wxewm = (RecyclerView) findViewById(R.id.recycler_modify_ws_mall_wxzq_wsewm);
        wxewm_selImageList = new ArrayList<>();
        wxewm_imagePickerAdapter = new ImagePickerAdapter(this, wxewm_selImageList, wxewm_maxImgCount,3);
        wxewm_imagePickerAdapter.setOnItemClickListener(this);

        recyclerView_wxewm.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView_wxewm.setHasFixedSize(true);
        recyclerView_wxewm.setAdapter(wxewm_imagePickerAdapter);
    }


    public void initView(){

        bt_modify_ws_mall_back = (Button) findViewById(R.id.bt_modify_ws_mall_back);
        bt_modify_ws_mall_back.setOnClickListener(this);

        tv_modify_ws_mall_wxzq_yxqz_content = (TextView)findViewById(R.id.tv_modify_ws_mall_wxzq_yxqz_content);
        tv_modify_ws_mall_wxzq_yxqz_content.setOnClickListener(this);
        et_modify_ws_mall_wxzq_spbt = (EditText)findViewById(R.id.et_modify_ws_mall_wxzq_spbt);
        et_modify_ws_mal_wxzql_spxq = (EditText)findViewById(R.id.et_modify_ws_mal_wxzql_spxq);
        et_modify_ws_mall_wxzq_wxzh = (EditText)findViewById(R.id.et_modify_ws_mall_wxzq_wxzh);


        bt_modify_ws_mall_fbsp = (Button) findViewById(R.id.bt_modify_ws_mall_fbsp);
        bt_modify_ws_mall_fbsp.setOnClickListener(this);

    }

    private void initData(){
        shop2xiangxi();
    }

    private void setData(){
        tv_modify_ws_mall_wxzq_yxqz_content.setText(modifyWSMallBean.getYouxiaoqi());
        et_modify_ws_mall_wxzq_spbt.setText(modifyWSMallBean.getTitle());
        et_modify_ws_mal_wxzql_spxq.setText(modifyWSMallBean.getContent());
        et_modify_ws_mall_wxzq_wxzh.setText(modifyWSMallBean.getWeixin());

        wxtp_selImageList.clear();
        List<String> imgs_temp = modifyWSMallBean.getImgs();
        if( imgs_temp != null){
            for(int i=0; i<imgs_temp.size();i++){
                ImageItem imageItem = new ImageItem();
                imageItem.setPath(HttpUtils.URL+"/"+imgs_temp.get(i));
                wxtp_selImageList.add(imageItem);
            }
            wxtp_imagePickerAdapter.setImages(wxtp_selImageList);
        }

        wxewm_selImageList.clear();
        String ewm_temp = modifyWSMallBean.getErweima();
        if( ewm_temp != null){
            ImageItem imageItem = new ImageItem();
            imageItem.setPath(HttpUtils.URL+"/"+ewm_temp);
            wxewm_selImageList.add(imageItem);

            wxewm_imagePickerAdapter.setImages(wxewm_selImageList);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_modify_ws_mall_wxzq_yxqz_content :
                showDatePickerDialog();
                break;
            case R.id.bt_modify_ws_mall_fbsp :
                shopxiugai2();
                break;
            case R.id.bt_modify_ws_mall_back :
                finish();
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
                tv_modify_ws_mall_wxzq_yxqz_content.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);

            }
        }, year, month, day);
        datePicker.show();
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

                    wxtp_del_imageList.clear();
                    wxtp_del_imageList.addAll(wxtp_selImageList);
                    wxtp_del_imageList.removeAll(wxtp_sub_imageList);
                    wxtp_del_imageList.removeAll(images);
                    if(wxtp_del_imageList.size() > 0){
                        deletetupian(wxtp_del_imageList);
                    }

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

                    wxewm_del_imageList.clear();
                    wxewm_del_imageList.addAll(wxewm_selImageList);
                    wxewm_del_imageList.removeAll(wxewm_sub_imageList);
                    wxewm_del_imageList.removeAll(images);
                    if(wxewm_del_imageList.size() > 0){
                        deletetupian(wxewm_del_imageList);
                    }

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

    //删除图片
    public void deletetupian(ArrayList<ImageItem> del_imageList){

        String imgUrl = "";
        for(int i=0;i<del_imageList.size();i++){
            String path = del_imageList.get(i).getPath();
            System.out.println("path------------"+path);
            if(path.startsWith(HttpUtils.URL)){
                imgUrl = imgUrl + path.replace(HttpUtils.URL+"/","") + ",";
            }
        }
        imgUrl = imgUrl.substring(0,imgUrl.length()-1);
        deletetupianImg(imgUrl);
    }

    public void deletetupianImg(String tupianmingcheng){
        System.out.println("tupianmingcheng------------"+tupianmingcheng);
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
                    Log.d("qiuzhiguanli","res=="+content);
//                    if(response.code() == 200){
//                        Gson gson = new Gson();
//                        Entity<RecruitContentBean> result = gson.fromJson(content, new TypeToken<Entity<RecruitContentBean>>(){}.getType());
//                        if(result.getCode() == 0){
//                            recruitContentBean = result.getData();
//                            handler.sendEmptyMessage(DATA_OK);
//                        }else{
//                            handler.sendEmptyMessage(DATA_ERROR);
//                        }
//                    }
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


    private void shopxiugai2() {

        String youxiaoqi = tv_modify_ws_mall_wxzq_yxqz_content.getText().toString().trim();
        String title = et_modify_ws_mall_wxzq_spbt.getText().toString().trim();
        String content = et_modify_ws_mal_wxzql_spxq.getText().toString().trim();
        String weixin = et_modify_ws_mall_wxzq_wxzh.getText().toString().trim();


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
        if(wxewm_selImageList.size() == 0){
            Toast.makeText(mContext,"微商二维码图片不能为空",Toast.LENGTH_LONG).show();
            return;
        }
        if(wxtp_selImageList.size() == 0){
            Toast.makeText(mContext,"最少添加一条商品图片",Toast.LENGTH_LONG).show();
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
                .addFormDataPart("shopid",xxid)
                .addFormDataPart("youxiaoqi", youxiaoqi)
                .addFormDataPart("title", title)
                .addFormDataPart("content", content)
                .addFormDataPart("weixin", weixin)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid);


        if(wxewm_sub_imageList.size() > 0){
            File file_ewm = new File(wxewm_sub_imageList.get(0).getPath());
            if(wxewm_sub_imageList.get(0).getPath().endsWith("jpg")){
                requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpg"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("png")){
                requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/png"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("jpeg")){
                requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file_ewm));
            }else if(wxewm_sub_imageList.get(0).getPath().endsWith("gif")){
                requestBodyBuilder.addFormDataPart("erweima", file_ewm.getName(), RequestBody.create(MediaType.parse("image/gif"), file_ewm));
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
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopxiugai2.asp").post(requestBody).build();
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

    public void shop2xiangxi(){

        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid)
                .add("shopid",xxid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shop2xiangxi.asp").post(requestBody).build();
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
                    Log.d("shanchushop","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<ModifyWSMallBean> result = gson.fromJson(content, new TypeToken<Entity<ModifyWSMallBean>>() {}.getType());
                        if(result.getCode() == 0){
                            modifyWSMallBean = result.getData();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }
                }
            }
        });
    }
}


