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
import android.widget.CheckBox;
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
import com.mobile.zxw.myapplication.bean.JobManageBean;
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

public class JobManageActivity extends AppCompatActivity implements View.OnClickListener , ImagePickerAdapter.OnRecyclerViewItemClickListener{

    private Context mContext;
    TextView tv_personal_xgmm, tv_personal_xgsjh;
    Button bt_recruit_manage_fbzp,job_manage_back;

    private int maxImgCount = 9;               //允许选择图片最大数
    private ImagePickerAdapter imagePickerAdapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private ArrayList<ImageItem> sub_imageList = new ArrayList<>(); //需要上传的图片
    private ArrayList<ImageItem> del_imageList = new ArrayList<>(); //需要删除的图片
    private String pindao = "全职简历";

    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    JobManageBean jobManageBean;

    //加载对话框
    private Dialog mLoadingDialog;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sp_setting;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String gudingyue,gudingyue3,gudingyue6,gudingyue12;

    Spinner sp_jobmanage_type,sp_jobmanage_jlzt;
    EditText et_job_manage_biaoti,et_job_manage_zwms;
    TextView tv_job_manage_xinming,tv_job_manage_xinbie,tv_job_manage_cssj_content,et_job_manage_lxdh;
    Spinner sp_jobmanage_rovince, sp_jobmanage_city, sp_jobmanage_county, sp_jobmanage_town;
    Spinner sp_jobmanage_jldl, sp_jobmanage_jlxl,sp_jobmanage_minzu, sp_jobmanage_zgxl, sp_jobmanage_jgsheng,sp_jobmanage_jgshi;
    Spinner sp_jobmanage_yuexin,sp_jobmanage_gzjy;

    RelativeLayout rl_job_manage_qwyx;  // 期望月薪
    LinearLayout ll_job_manage_qwrx;  // 期望日薪
    EditText et_job_manage_qwrx;  // 期望日薪
    RelativeLayout rl_job_manage_kxsj;  // 空闲时间
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5,checkBox6,checkBox7;

    Button bt_job_manage_bcjl;

    TextView tv_job_manage_xxzd_title;

    RelativeLayout rl_jobmanage_zdyf;
    Spinner sp_jobmanage_zdyf;

    LinearLayout ll_job_manage_zdz;
    TextView tv_job_manage_zdz;

    private int type = 0;   //全职 兼职 标记
    private int jlzt = 0;   //简历状态  开启 关闭 标记
    private String Select1,Select2,Select3,Select4;

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
    private List<AreaBean>  jgshiList;

    private List<String>  type_arr;
    private List<String>  jlzt_arr;
    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;
    private List<String>  dl_arr;
    private List<String>  xl_arr;
    private List<String>  minzuList;
    private List<String>  xueliList;
    private List<String>  jgshi_arr;
    private List<String>  yuexin_arr;
    private List<String>  gzjyList;
    private List<String>  zdyfList;

    ArrayAdapter<String> typeAdapter;
    ArrayAdapter<String> jlztAdapter;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;
    ArrayAdapter<String> dlAdapter;
    ArrayAdapter<String> xlAdapter;
    ArrayAdapter<String> mzAdapter;
    ArrayAdapter<String> xueliAdapter;
    ArrayAdapter<String> jgsAdapter;
    ArrayAdapter<String> jgshiAdapter;
    ArrayAdapter<String> yuexinAdapter;
    ArrayAdapter<String> gzjyAdapter;
    ArrayAdapter<String> zdyfAdapter;

    private String mizu,jg_sheng,jg_shi,gzjy,yuexin,xueli,bigzhiweiclass,smallzhiweiclass,guding_yuefen;
    private String guding,gudingdaoqi;
    private String kongxianshijian;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int DATA_JC = 2;
    static int DL_OK = 3; //大类获取成功
    static int DL_ERROR = 4; //大类获取失败
    static int XL_OK = 5; //小类获取成功
    static int XL_ERROR = 6; //小类获取失败
    static int YX_OK = 7; //小类获取成功
    static int YX_ERROR = 8; //小类获取失败

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
                    setData();
                    cancelDialog();
                    break;
                case 1:
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
                    citityAdapter.notifyDataSetChanged();
                    regionAdapter.notifyDataSetChanged();
                    townAdapter.notifyDataSetChanged();
                    gzjyAdapter.notifyDataSetChanged();
                    jgsAdapter.notifyDataSetChanged();
                    jgshiAdapter.notifyDataSetChanged();
                    mzAdapter.notifyDataSetChanged();
                    xueliAdapter.notifyDataSetChanged();
                    zdyfAdapter.notifyDataSetChanged();
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
                    break;
                case 11:
                    sub_imageList.clear();
                    cancelDialog();
                    Toast.makeText(mContext, "简历信息修改完成!", Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    cancelDialog();
                    Toast.makeText(mContext, "您的账户余额不足，无法发布信息，请先充值!", Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    cancelDialog();
                    Toast.makeText(mContext, "您的账户余额不足，无法发布置顶信息，请充值后发布!", Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    cancelDialog();
                    Toast.makeText(mContext, "余额不足无法置顶到所给时间，充值后再置顶!", Toast.LENGTH_SHORT).show();
                case 15:
                    cancelDialog();
                    Toast.makeText(mContext, "登录超时!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_manage);
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

        tv_job_manage_xxzd_title = (TextView) findViewById(R.id.tv_job_manage_xxzd_title);

        et_job_manage_biaoti = (EditText) findViewById(R.id.et_job_manage_biaoti);
        et_job_manage_zwms = (EditText) findViewById(R.id.et_job_manage_zwms);

        tv_job_manage_xinming = (TextView) findViewById(R.id.tv_job_manage_xinming);
        tv_job_manage_xinbie = (TextView) findViewById(R.id.tv_job_manage_xinbie);
        tv_job_manage_cssj_content = (TextView) findViewById(R.id.tv_job_manage_cssj_content);
        et_job_manage_lxdh = (TextView) findViewById(R.id.et_job_manage_lxdh);

        job_manage_back = (Button)findViewById(R.id.job_manage_back);
        job_manage_back.setOnClickListener(this);

        sp_jobmanage_type = (Spinner) findViewById(R.id.sp_jobmanage_type);
        sp_jobmanage_jlzt = (Spinner) findViewById(R.id.sp_jobmanage_jlzt);

        sp_jobmanage_rovince = (Spinner) findViewById(R.id.sp_jobmanage_rovince);
        sp_jobmanage_city = (Spinner) findViewById(R.id.sp_jobmanage_city);
        sp_jobmanage_county = (Spinner) findViewById(R.id.sp_jobmanage_county);
        sp_jobmanage_town = (Spinner) findViewById(R.id.sp_jobmanage_town);

        sp_jobmanage_jldl = (Spinner) findViewById(R.id.sp_jobmanage_jldl);
        sp_jobmanage_jlxl = (Spinner) findViewById(R.id.sp_jobmanage_jlxl);
        sp_jobmanage_minzu = (Spinner) findViewById(R.id.sp_jobmanage_minzu);
        sp_jobmanage_zgxl = (Spinner) findViewById(R.id.sp_jobmanage_zgxl);
        sp_jobmanage_jgsheng = (Spinner) findViewById(R.id.sp_jobmanage_jgsheng);
        sp_jobmanage_jgshi = (Spinner) findViewById(R.id.sp_jobmanage_jgshi);
        sp_jobmanage_yuexin = (Spinner) findViewById(R.id.sp_jobmanage_yuexin);
        sp_jobmanage_gzjy = (Spinner) findViewById(R.id.sp_jobmanage_gzjy);

        rl_jobmanage_zdyf = (RelativeLayout) findViewById(R.id.rl_jobmanage_zdyf);
        sp_jobmanage_zdyf = (Spinner) findViewById(R.id.sp_jobmanage_zdyf);

        ll_job_manage_zdz = (LinearLayout) findViewById(R.id.ll_job_manage_zdz);
        tv_job_manage_zdz = (TextView) findViewById(R.id.tv_job_manage_zdz);

        rl_job_manage_qwyx = (RelativeLayout) findViewById(R.id.rl_job_manage_qwyx);
        ll_job_manage_qwrx = (LinearLayout) findViewById(R.id.ll_job_manage_qwrx);
        et_job_manage_qwrx = (EditText) findViewById(R.id.et_job_manage_qwrx);
        rl_job_manage_kxsj = (RelativeLayout) findViewById(R.id.rl_job_manage_kxsj);

        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        checkBox2 = (CheckBox) findViewById(R.id.checkBox2);
        checkBox3 = (CheckBox) findViewById(R.id.checkBox3);
        checkBox4 = (CheckBox) findViewById(R.id.checkBox4);
        checkBox5 = (CheckBox) findViewById(R.id.checkBox5);
        checkBox6 = (CheckBox) findViewById(R.id.checkBox6);
        checkBox7 = (CheckBox) findViewById(R.id.checkBox7);

        bt_job_manage_bcjl = (Button)findViewById(R.id.bt_job_manage_bcjl);
        bt_job_manage_bcjl.setOnClickListener(this);

        setAdapter();
        setAdapterOnItemClick();
    }

    public void initData(){
        showDialog("数据加载中");
        //从服务器获取大类
        BigZhiweiClass();
        //从服务器获取月薪
        YueXiClass();
        //异步加载基础数据
        new NewsAsyncTask().execute();
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_job_manage);
        selImageList = new ArrayList<>();
        imagePickerAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount,1);
        imagePickerAdapter.setOnItemClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(imagePickerAdapter);
    }

    public void setData(){

        et_job_manage_biaoti.setText(jobManageBean.getTitle());
        et_job_manage_zwms.setText(jobManageBean.getContent());

        tv_job_manage_xinming.setText(jobManageBean.getXingming());
        tv_job_manage_xinbie.setText(jobManageBean.getXingbie());
        if(jobManageBean.getShenfenzheng() != null && !"".equals(jobManageBean.getShenfenzheng()) && jobManageBean.getShenfenzheng().length() > 14){
            tv_job_manage_cssj_content.setText(jobManageBean.getShenfenzheng().substring(6,14));
        }
        et_job_manage_lxdh.setText(jobManageBean.getShouji());

        Select1 = jobManageBean.getSheng();
        Select2 = jobManageBean.getShi();
        Select3 = jobManageBean.getQuxian();
        Select4 = jobManageBean.getXiangzhen();

        System.out.println("jobManageBean------"+jobManageBean);
        System.out.println("Select1------"+Select1);
        System.out.println("Select2------"+Select2);
        System.out.println("Select3------"+Select3);
        System.out.println("Select4------"+Select4);

        if("".equals(Select1)){
            sp_jobmanage_rovince.setSelection(0);
        }

        for(int i=0;i<provinceList.size();i++){
            if(Select1 != null && !"".equals(Select1)){
                if(Integer.valueOf(Select1) == provinceList.get(i).getTid()){
                    sp_jobmanage_rovince.setSelection(i);
                }
            }

        }


        mizu = jobManageBean.getMinzu();
        if("".equals(mizu)){
            sp_jobmanage_minzu.setSelection(0);
        }
        for(int i=0;i<minzuList.size();i++){
            if(minzuList.get(i).equals(mizu) ){
                sp_jobmanage_minzu.setSelection(i,false);
            }
        }

        xueli = jobManageBean.getXueli();
        if("".equals(xueli)){
            sp_jobmanage_zgxl.setSelection(0);
        }
        for(int i=0;i<xueliList.size();i++){
            if(xueliList.get(i).equals(xueli) ){
                sp_jobmanage_zgxl.setSelection(i,false);
            }
        }

        jg_sheng = jobManageBean.getJiguansheng();
        jg_shi = jobManageBean.getJiguanshi();
        if("".equals(jg_sheng)){
            sp_jobmanage_jgsheng.setSelection(0);
        }
        //设置个默认的第一个
        for(int i=0;i<provinceList.size();i++){
            if(provinceList.get(i).getName().equals(jg_sheng)){
                sp_jobmanage_jgsheng.setSelection(i,false);
            }
        }

        bigzhiweiclass = jobManageBean.getBigzhiweiclass();
        smallzhiweiclass = jobManageBean.getSmallzhiweiclass();
        if("".equals(bigzhiweiclass)){
            sp_jobmanage_jldl.setSelection(0);
        }
        if(bigzhiweiclass != null && !"".equals(bigzhiweiclass)){
            //设置个默认的第一个
            String bigclass_select = "";
            for(int i=0;i<dl_arr.size();i++){
                if(dl_arr.get(i).equals(bigzhiweiclass)){
                    sp_jobmanage_jldl.setSelection(i,false);
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

        gzjy = jobManageBean.getGongzuojingyan();
        if("".equals(gzjy)){
            sp_jobmanage_gzjy.setSelection(0);
        }
        for(int i=0;i<gzjyList.size();i++){
            if(gzjyList.get(i).equals(gzjy) ){
                sp_jobmanage_gzjy.setSelection(i,false);
            }
        }
        if(jobManageBean.getDel() != null && !"".equals(jobManageBean.getDel())){
            jlzt = Integer.valueOf(jobManageBean.getDel());
        }else{
            jlzt = 0;
        }
        sp_jobmanage_jlzt.setSelection(jlzt);
//        for(int i=0;i<jlzt_arr.size();i++){
//            if(jlzt == i ){
//                sp_jobmanage_jlzt.setSelection(i,false);
//            }
//        }

        if(type == 0){
            yuexin = jobManageBean.getYuexin();
            if("".equals(yuexin)){
                sp_jobmanage_yuexin.setSelection(0);
            }
            for(int i=0;i<yuexin_arr.size();i++){
                if(yuexin_arr.get(i).equals(yuexin) ){
                    sp_jobmanage_yuexin.setSelection(i);
                }
            }
        }else{
            et_job_manage_qwrx.setText(jobManageBean.getYuexin());
        }


        guding = jobManageBean.getGuding();
        gudingdaoqi = jobManageBean.getGudingdaoqi();

        if(type == 0){
            if("1".equals(guding)  && gudingdaoqi != null && !"".equals(gudingdaoqi)){

                SimpleDateFormat sdf_gddq = new SimpleDateFormat("yyyy-MM-dd");//yyyy-mm-dd, 会出现时间不对, 因为小写的mm是代表: 秒
                Date gddqDate = null;
                try {
                    gddqDate = sdf_gddq.parse(gudingdaoqi);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date nowDate = new Date();
                if(nowDate.getTime() > gddqDate.getTime()){
                    ll_job_manage_zdz.setVisibility(View.GONE);
                    rl_jobmanage_zdyf.setVisibility(View.VISIBLE);
                }else{
                    ll_job_manage_zdz.setVisibility(View.VISIBLE);
                    rl_jobmanage_zdyf.setVisibility(View.GONE);
                    tv_job_manage_zdz.setText("置顶到期："+gudingdaoqi);
                }
            }else{
                ll_job_manage_zdz.setVisibility(View.GONE);
                rl_jobmanage_zdyf.setVisibility(View.VISIBLE);
            }
        }

        if(type == 1){
            kongxianshijian = jobManageBean.getKongxianshijian();
            if(kongxianshijian != null && !"".equals(kongxianshijian)){
                String[] kxsj = kongxianshijian.split(",");
                for (int i=0;i<kxsj.length;i++){
                    if("星期一".equals(kxsj[i])){
                        checkBox1.setChecked(true);
                    }else if("星期二".equals(kxsj[i])){
                        checkBox2.setChecked(true);
                    }else if("星期三".equals(kxsj[i])){
                        checkBox3.setChecked(true);
                    }else if("星期四".equals(kxsj[i])){
                        checkBox4.setChecked(true);
                    }else if("星期五".equals(kxsj[i])){
                        checkBox5.setChecked(true);
                    }else if("星期六".equals(kxsj[i])){
                        checkBox6.setChecked(true);
                    }else if("星期日".equals(kxsj[i])){
                        checkBox7.setChecked(true);
                    }
                }
            }
        }

        selImageList.clear();
        List<String> imgs_temp = jobManageBean.getImgs();
        if( imgs_temp != null){
            for(int i=0; i<imgs_temp.size();i++){
                System.out.println("imgs_temp.get(i)----"+imgs_temp.get(i));
                ImageItem imageItem = new ImageItem();
                imageItem.setPath(HttpUtils.URL+"/"+imgs_temp.get(i));
                selImageList.add(imageItem);
            }
            imagePickerAdapter.setImages(selImageList);
        }

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

        xl_arr.add("选择小类");

        for(int i=0;i<xl_List.size();i++){
            xl_arr.add(xl_List.get(i).getSmallClassName());
        }
        sp_jobmanage_jlxl.setSelection(0);
        xlAdapter.notifyDataSetChanged();
        for(int i=0;i<xl_arr.size();i++){
            if(xl_arr.get(i).equals(smallzhiweiclass)) {
                sp_jobmanage_jlxl.setSelection(i);
            }
        }

    }

    private void setPriceScreenData() {
        for(int i=0;i<yx_List.size();i++){
            if("面议".equals(yx_List.get(i).getYuexinName())){
                yuexin_arr.add(0,yx_List.get(i).getYuexinName());
            }else{
                yuexin_arr.add(yx_List.get(i).getYuexinName());
            }
            yuexinAdapter.notifyDataSetChanged();
        }
    }

    private void setAdapter(){
        type_arr = new ArrayList<>();
        type_arr.add("全职简历");
        type_arr.add("兼职简历");
        typeAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,type_arr);
        sp_jobmanage_type.setAdapter(typeAdapter);

        jlzt_arr = new ArrayList<>();
        jlzt_arr.add("开启");
        jlzt_arr.add("关闭");
        jlztAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,jlzt_arr);
        sp_jobmanage_jlzt.setAdapter(jlztAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_jobmanage_rovince.setAdapter(adapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_jobmanage_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_jobmanage_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_jobmanage_town.setAdapter(townAdapter);

        dl_arr = new ArrayList<>();
        dlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,dl_arr);
        sp_jobmanage_jldl.setAdapter(dlAdapter);

        xl_arr = new ArrayList<>();
        xlAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xl_arr);
        sp_jobmanage_jlxl.setAdapter(xlAdapter);

        minzuList = new ArrayList<>();
        mzAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,minzuList);
        sp_jobmanage_minzu.setAdapter(mzAdapter);

        xueliList = new ArrayList<>();
        xueliAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xueliList);
        sp_jobmanage_zgxl.setAdapter(xueliAdapter);

        jgsAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_jobmanage_jgsheng.setAdapter(jgsAdapter);

        jgshiList = new ArrayList<>();
        jgshi_arr = new ArrayList<>();
        jgshiAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,jgshi_arr);
        sp_jobmanage_jgshi.setAdapter(jgshiAdapter);

        yuexin_arr = new ArrayList<>();
        yuexinAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,yuexin_arr);
        sp_jobmanage_yuexin.setAdapter(yuexinAdapter);

        gzjyList = new ArrayList<>();
        gzjyAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,gzjyList);
        sp_jobmanage_gzjy.setAdapter(gzjyAdapter);

        zdyfList = new ArrayList<>();
        zdyfAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,zdyfList);
        sp_jobmanage_zdyf.setAdapter(zdyfAdapter);
    }

    private void setAdapterOnItemClick(){
        System.out.println("-----------setAdapterOnItemClick:");
        //简历选择
        sp_jobmanage_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("-----------sp_jobmanage_type:"+position);
                selImageList.clear();
                sub_imageList.clear();
                del_imageList.clear();
                imagePickerAdapter.setImages(selImageList);

                if(position == 1){

                    rl_job_manage_qwyx.setVisibility(View.GONE);
                    ll_job_manage_qwrx.setVisibility(View.VISIBLE);
                    rl_job_manage_kxsj.setVisibility(View.VISIBLE);

                    tv_job_manage_xxzd_title.setVisibility(View.GONE);
                    ll_job_manage_zdz.setVisibility(View.GONE);
                    rl_jobmanage_zdyf.setVisibility(View.GONE);


                }else{
                    rl_job_manage_qwyx.setVisibility(View.VISIBLE);
                    ll_job_manage_qwrx.setVisibility(View.GONE);
                    rl_job_manage_kxsj.setVisibility(View.GONE);

                    tv_job_manage_xxzd_title.setVisibility(View.VISIBLE);
                    if("1".equals(guding)  && gudingdaoqi != null && !"".equals(gudingdaoqi)){
                        ll_job_manage_zdz.setVisibility(View.VISIBLE);
                        rl_jobmanage_zdyf.setVisibility(View.GONE);
                    }else{
                        ll_job_manage_zdz.setVisibility(View.GONE);
                        rl_jobmanage_zdyf.setVisibility(View.VISIBLE);
                    }

                }

                if(type != position){
                    type = position;

                    if(type == 0){
                        pindao = "全职简历";
                    }else{
                        pindao = "兼职简历";
                    }

                    showDialog("数据加载中");
                    qiuzhiguanli();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_jobmanage_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_jobmanage_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_jobmanage_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_jobmanage_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_jobmanage_county, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_jobmanage_county.setSelection(region_index);
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

        sp_jobmanage_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_jobmanage_town, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_jobmanage_town.setSelection(town_index);
                System.out.println("xx--sp_recruit_content_county------"+town_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_jobmanage_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        //简历状态选择
        sp_jobmanage_jlzt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jlzt = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //简历大类
        sp_jobmanage_jldl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----------sp_jobmanage_jldl:");

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_jobmanage_jlxl, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(position == 0){
                    bigzhiweiclass = "";
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
        sp_jobmanage_jlxl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("-----------sp_jobmanage_jlxl:");
                if(position == 0){
                    smallzhiweiclass = xl_arr.get(0);
                    return;
                }
                System.out.println("-----------smallzhiweiclass:"+smallzhiweiclass);
                SmallZhiweiBean smallZhiweiBean = xl_List.get(position-1);
                smallzhiweiclass  = smallZhiweiBean.getSmallClassName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //民族
        sp_jobmanage_minzu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mizu  = minzuList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //工作经验
        sp_jobmanage_gzjy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gzjy  = gzjyList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //月薪
        sp_jobmanage_yuexin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yuexin  = yuexin_arr.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //学历
        sp_jobmanage_zgxl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xueli  = xueliList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //籍贯省
        sp_jobmanage_jgsheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_jobmanage_jgshi, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }

                jg_sheng  = provinceList.get(position).getName();
                jgshiList.clear();
                jgshi_arr.clear();
                jgshiList.addAll(getChildList( provinceList.get(position).getTid()));

                int  jg_shi_index =0;
                for(int i=0;i<jgshiList.size();i++){
                    jgshi_arr.add(jgshiList.get(i).getName());
                    if(jg_shi != null && !"".equals(jg_shi)){
                        if(jg_shi.equals(jgshiList.get(i).getName())){
                            jg_shi_index = i;
                        }
                    }
                }
                jgshiAdapter.notifyDataSetChanged();
                sp_jobmanage_jgshi.setSelection(jg_shi_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //籍贯市
        sp_jobmanage_jgshi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jg_shi  = jgshiList.get(position).getName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //置顶
        sp_jobmanage_zdyf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_personal_xgmm :
                startActivity(new Intent(JobManageActivity.this,ModifyPasswordActivity.class));
                break;
            case R.id.tv_personal_xgsjh :
                startActivity(new Intent(JobManageActivity.this,ModifyPhoneActivity.class));
                break;
            case R.id.job_manage_back :
                finish();
                break;
            case R.id.bt_job_manage_bcjl :
                if(type == 0){
                    baocunqiuzhi(sub_imageList);
                }else{
                    baocunqiuzhiJZ(sub_imageList);
                }

                break;
            default:
                break;
        }
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
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) imagePickerAdapter.getImages());
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
                    imagePickerAdapter.setImages(selImageList);
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
                    imagePickerAdapter.setImages(selImageList);
                }
            }
        }
    }

    //删除图片
    public void deletetupian(){

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
                .add("tupianmingcheng",tupianmingcheng).addEncoded("pindao",pd).add("xinxiID",jobManageBean.getJobid()).build();
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

            jgshiList.addAll(getChildList(provinceList.get(0).getTid())) ;
            for(int i=0;i<cityList.size();i++){
                jgshi_arr.add(cityList.get(i).getName());
            }

            String data_minzu = StreamUtils.get(APP.getInstance(), R.raw.minzu);
            Type type_minzu = new TypeToken<List<String>>() { }.getType();
            minzuList.addAll(gson.fromJson(data_minzu, type_minzu));

            String data_xueli = StreamUtils.get(APP.getInstance(), R.raw.xueli);
            Type type_xueli = new TypeToken<List<String>>() { }.getType();
            xueliList.addAll(gson.fromJson(data_xueli, type_xueli));

            String data_gzjy = StreamUtils.get(APP.getInstance(), R.raw.gongzuojingyan);
            Type type_gzjy = new TypeToken<List<String>>() { }.getType();
            gzjyList.addAll(gson.fromJson(data_gzjy, type_gzjy));

            zdyfList.add("不置顶");
            zdyfList.add("一个月(￥"+gudingyue+"元)");
            zdyfList.add("三个月(￥"+gudingyue3+"元)");
            zdyfList.add("六个月(￥"+gudingyue6+"元)");
            zdyfList.add("十二个月(￥"+gudingyue12+"元)");

            handler.sendEmptyMessage(DATA_JC);

            //获取数据
            qiuzhiguanli();
            return null;
        }
    }

    //获取数据
    public void qiuzhiguanli(){
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                .add("type",type+"").build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/qiuzhiguanli.asp").post(requestBody).build();
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
                        Entity<JobManageBean> result = gson.fromJson(content, new TypeToken<Entity<JobManageBean>>(){}.getType());
                        if(result.getCode() == 0){
                            jobManageBean = result.getData();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }
                }
            }
        });
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

        RequestBody requestBody = new FormBody.Builder().add("BigClassID",BigClassID).build();
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

    private void baocunqiuzhi(ArrayList<ImageItem> imageList) {

        String title = et_job_manage_biaoti.getText().toString().trim();
        String xingming = tv_job_manage_xinming.getText().toString().trim();
        String xingbie = tv_job_manage_xinbie.getText().toString().trim();
        String zwms = et_job_manage_zwms.getText().toString().trim();

        if("".equals(bigzhiweiclass) || "选择大类".equals(bigzhiweiclass)
                || "".equals(smallzhiweiclass) ||"选择小类".equals(smallzhiweiclass)){
            Toast.makeText(mContext,"请选择职位类别",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"招聘标题不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(zwms)){
            Toast.makeText(mContext,"自我描述不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("正在提交数据");

        System.out.println("Select1-"+Select1+"Select2-"+Select2+"Select3-"+Select3+"Select4-"+Select4);
        System.out.println("bigzhiweiclass-"+bigzhiweiclass+"smallzhiweiclass-"+smallzhiweiclass);
        System.out.println("jlzt-"+jlzt);
        System.out.println("title-"+title+"mizu-"+mizu+"jg_sheng-"+jg_sheng+"jg_shi-"+jg_shi+"gzjy-"+gzjy+"yuexin-"+yuexin+"xueli-"+xueli);
        System.out.println("zwms-"+zwms);
        System.out.println("guding_yuefen-"+guding_yuefen);

        System.out.println("selImageList-------"+imageList.size());

        String sub_bigzhiweiclass = "";
        String sub_smallzhiweiclass = "";
        String sub_mizu = "";
        String sub_jgsheng = "";
        String sub_jgshi = "";
        String sub_gzjy = "";
        String sub_yuexin = "";
        String sub_xueli = "";
        try {
            sub_bigzhiweiclass =  URLEncoder.encode(bigzhiweiclass,"GB2312");
            sub_smallzhiweiclass =  URLEncoder.encode(smallzhiweiclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            sub_mizu =  URLEncoder.encode(mizu,"GB2312");
            sub_jgsheng =  URLEncoder.encode(jg_sheng,"GB2312");
            sub_jgshi =  URLEncoder.encode(jg_shi,"GB2312");
            sub_gzjy =  URLEncoder.encode(gzjy,"GB2312");
            sub_yuexin =  URLEncoder.encode(yuexin,"GB2312");
            sub_xueli =  URLEncoder.encode(xueli,"GB2312");
            zwms =  URLEncoder.encode(zwms,"GB2312");
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
                .addFormDataPart("title", title)
                .addFormDataPart("gongzuojingyan", sub_gzjy)
                .addFormDataPart("minzu", sub_mizu)
                .addFormDataPart("jiguansheng", sub_jgsheng)
                .addFormDataPart("jiguanshi", sub_jgshi)
                .addFormDataPart("xueli", sub_xueli)
                .addFormDataPart("yuexin", sub_yuexin)
                .addFormDataPart("content", zwms)
                .addFormDataPart("del", jlzt+"")
                .addFormDataPart("gudingdaoqi", guding_yuefen)
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid);
//                .addFormDataPart("xinxiID", xxid);
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

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/baocunqiuzhi.asp").post(requestBody).build();
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

    private void baocunqiuzhiJZ(ArrayList<ImageItem> imageList) {

        String title = et_job_manage_biaoti.getText().toString().trim();
        String xingming = tv_job_manage_xinming.getText().toString().trim();
        String xingbie = tv_job_manage_xinbie.getText().toString().trim();
        String zwms = et_job_manage_zwms.getText().toString().trim();
        String qwrx = et_job_manage_qwrx.getText().toString().trim();

        if("选择大类".equals(bigzhiweiclass) || "选择小类".equals(smallzhiweiclass)){
            Toast.makeText(mContext,"请选择职位类别",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(title)){
            Toast.makeText(mContext,"招聘标题不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if("".equals(qwrx)){
            Toast.makeText(mContext,"请填写日薪",Toast.LENGTH_SHORT).show();
            return;
        }

        String gongzuoshijian = "";
        if(checkBox1.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期一,";
        }
        if(checkBox2.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期二,";
        }
        if(checkBox3.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期三,";
        }
        if(checkBox4.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期四,";
        }
        if(checkBox5.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期五,";
        }
        if(checkBox6.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期六,";
        }
        if(checkBox7.isChecked()){
            gongzuoshijian = gongzuoshijian+"星期日";
        }

        if(gongzuoshijian.endsWith(",")){
            gongzuoshijian = gongzuoshijian.substring(0,gongzuoshijian.length()-1);
        }

        if("".equals(gongzuoshijian)){
            Toast.makeText(mContext,"请选择空闲时间",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equals(zwms)){
            Toast.makeText(mContext,"自我描述不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog("正在提交数据");

        System.out.println("Select1-"+Select1+"Select2-"+Select2+"Select3-"+Select3+"Select4-"+Select4);
        System.out.println("bigzhiweiclass-"+bigzhiweiclass+"smallzhiweiclass-"+smallzhiweiclass);
        System.out.println("jlzt-"+jlzt);
        System.out.println("title-"+title+"mizu-"+mizu+"jg_sheng-"+jg_sheng+"jg_shi-"+jg_shi+"gzjy-"+gzjy+"yuexin-"+yuexin+"xueli-"+xueli);
        System.out.println("zwms-"+zwms);
        System.out.println("guding_yuefen-"+guding_yuefen);

        System.out.println("selImageList-------"+imageList.size());



        String sub_bigzhiweiclass = "";
        String sub_smallzhiweiclass = "";
        String sub_mizu = "";
        String sub_jgsheng = "";
        String sub_jgshi = "";
        String sub_gzjy = "";
        String sub_yuexin = "";
        String sub_xueli = "";
        try {
            sub_bigzhiweiclass =  URLEncoder.encode(bigzhiweiclass,"GB2312");
            sub_smallzhiweiclass =  URLEncoder.encode(smallzhiweiclass,"GB2312");
            title =  URLEncoder.encode(title,"GB2312");
            sub_mizu =  URLEncoder.encode(mizu,"GB2312");
            sub_jgsheng =  URLEncoder.encode(jg_sheng,"GB2312");
            sub_jgshi =  URLEncoder.encode(jg_shi,"GB2312");
            sub_gzjy =  URLEncoder.encode(gzjy,"GB2312");
            sub_yuexin =  URLEncoder.encode(qwrx,"GB2312");
            sub_xueli =  URLEncoder.encode(xueli,"GB2312");
            zwms =  URLEncoder.encode(zwms,"GB2312");
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
                .addFormDataPart("title", title)
                .addFormDataPart("gongzuojingyan", sub_gzjy)
                .addFormDataPart("minzu", sub_mizu)
                .addFormDataPart("jiguansheng", sub_jgsheng)
                .addFormDataPart("jiguanshi", sub_jgshi)
                .addFormDataPart("xueli", sub_xueli)
                .addFormDataPart("yuexin", sub_yuexin)
                .addFormDataPart("content", zwms)
                .addFormDataPart("del", jlzt+"")
                .addFormDataPart("kongxianshijian", gongzuoshijian)
                .addFormDataPart("gudingdaoqi", "0")
                .addFormDataPart("sessionID", sessionID)
                .addFormDataPart("userid", userid);
//                .addFormDataPart("xinxiID", xxid);
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

        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/baocunqiuzhiJZ.asp").post(requestBody).build();
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
}


