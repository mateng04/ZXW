package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.PersonalBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.StreamUtils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
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

public class PersonalActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    TextView tv_personal_xgmm, tv_personal_xgsjh;

    //加载对话框
    private Dialog mLoadingDialog;

    private String Select1,Select2,Select3,Select4;
    private String mizu,jg_sheng,jg_shi,xueli;

    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
    private List<AreaBean> townList;
    private List<String> minzuList;
    private List<String>  xueliList;
    private List<AreaBean> jgshiList;

    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;
    private List<String>  jgshi_arr;


    PersonalBean personalBean;
    Button bt_personal_bc, personal_back;

    TextView et_personal_zsxm;
    EditText et_personal_kfqq,et_personal_kfwx;
    TextView tv_personal_lxdh;
    Spinner sp_per_rovince,sp_per_city,sp_per_county,sp_per_town,sp_per_mz,sp_per_jgs,sp_per_jgshi,
            sp_per_xueli;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;
    ArrayAdapter<String> mzAdapter;
    ArrayAdapter<String> jgsAdapter;
    ArrayAdapter<String> jgshiAdapter;
    ArrayAdapter<String> xueliAdapter;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int DATA_JC = 2;
    static int SUBMIT_DATA_OK = 3;
    static int SUBMIT_DATA_ERROR = 4;
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
                    mzAdapter.notifyDataSetChanged();
                    jgsAdapter.notifyDataSetChanged();
                    jgshiAdapter.notifyDataSetChanged();
                    xueliAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    Toast.makeText(mContext,"保存个人资料成功",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(mContext,"保存个人资料失败,请重新保存",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        mContext = PersonalActivity.this;

        APP.getInstance().addActivity(PersonalActivity.this);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                PersonalActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        initView();
        initData();
    }

    public void initView(){
        tv_personal_xgmm = (TextView) findViewById(R.id.tv_personal_xgmm);
        tv_personal_xgmm.setOnClickListener(this);
        tv_personal_xgsjh = (TextView) findViewById(R.id.tv_personal_xgsjh);
        tv_personal_xgsjh.setOnClickListener(this);

        et_personal_zsxm = (TextView) findViewById(R.id.et_personal_zsxm);
        et_personal_kfqq = (EditText) findViewById(R.id.et_personal_kfqq);
        et_personal_kfwx = (EditText) findViewById(R.id.et_personal_kfwx);

        tv_personal_lxdh = (TextView) findViewById(R.id.tv_personal_lxdh);

        sp_per_rovince = (Spinner) findViewById(R.id.sp_per_rovince);
        sp_per_city = (Spinner) findViewById(R.id.sp_per_city);
        sp_per_county = (Spinner) findViewById(R.id.sp_per_county);
        sp_per_town = (Spinner) findViewById(R.id.sp_per_town);
        sp_per_mz = (Spinner) findViewById(R.id.sp_per_mz);

        sp_per_jgs = (Spinner) findViewById(R.id.sp_per_jgs);
        sp_per_jgshi = (Spinner) findViewById(R.id.sp_per_jgshi);

        sp_per_xueli = (Spinner) findViewById(R.id.sp_per_xueli);

        bt_personal_bc = (Button)findViewById(R.id.bt_personal_bc);
        bt_personal_bc.setOnClickListener(this);
        personal_back = (Button)findViewById(R.id.personal_back);
        personal_back.setOnClickListener(this);
    }

    public void initData(){
        showDialog("正在加载数据");

        minzuList = new ArrayList<>();
        mzAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,minzuList);
        sp_per_mz.setAdapter(mzAdapter);

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_per_rovince.setAdapter(adapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_per_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_per_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_per_town.setAdapter(townAdapter);

        jgsAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_per_jgs.setAdapter(jgsAdapter);

        jgshiList = new ArrayList<>();
        jgshi_arr = new ArrayList<>();
        jgshiAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,jgshi_arr);
        sp_per_jgshi.setAdapter(jgshiAdapter);

        xueliList = new ArrayList<>();
        xueliAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,xueliList);
        sp_per_xueli.setAdapter(xueliAdapter);

        new NewsAsyncTask().execute();
    }

    public void setData(){

        et_personal_zsxm.setText(personalBean.getZsxm());
        tv_personal_lxdh.setText(personalBean.getSjh());
        et_personal_kfqq.setText(personalBean.getQq());
        et_personal_kfwx.setText(personalBean.getWeixin());

        Select1 = personalBean.getSelect1();
        Select2 = personalBean.getSelect2();
        Select3 = personalBean.getSelect3();
        Select4 = personalBean.getSelect4();

        mizu = personalBean.getMinzu();
        for(int i=0;i<minzuList.size();i++){
            if(minzuList.get(i).equals(mizu) ){
                sp_per_mz.setSelection(i,false);
            }
        }
        sp_per_mz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mizu  = minzuList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        xueli = personalBean.getXueli();
        for(int i=0;i<xueliList.size();i++){
            if(xueliList.get(i).equals(xueli) ){
                sp_per_xueli.setSelection(i,false);
            }
        }
        sp_per_xueli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xueli  = xueliList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        jg_sheng = personalBean.getSheng();
        //设置个默认的第一个
        int jg_sheng_select = provinceList.get(0).getTid();
        for(int i=0;i<provinceList.size();i++){
            if(provinceList.get(i).getName().equals(jg_sheng)){
                sp_per_jgs.setSelection(i,false);
                jg_sheng_select = provinceList.get(i).getTid();
            }
        }
        sp_per_jgs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jg_sheng  = provinceList.get(position).getName();
                jgshiList.clear();
                jgshi_arr.clear();
                jgshiList.addAll(getChildList( provinceList.get(position).getTid()));
                for(int i=0;i<jgshiList.size();i++){
                    jgshi_arr.add(jgshiList.get(i).getName());
                }
                jgshiAdapter.notifyDataSetChanged();
                sp_per_jgshi.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        jg_shi = personalBean.getShi();
        jgshiList.clear();
        jgshiList.addAll(getChildList(jg_sheng_select));
        int jgshi_index =0;
        for(int i=0;i<jgshiList.size();i++){
            jgshi_arr.add(jgshiList.get(i).getName());
            if(jgshiList.get(i).getName().equals(jg_shi)){
                jgshi_index = i;
            }
        }
        jgshiAdapter.notifyDataSetChanged();
        sp_per_jgshi.setSelection(jgshi_index,false);

        sp_per_jgshi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jg_shi  = jgshiList.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        for(int i=0;i<provinceList.size();i++){
            if(Integer.valueOf(Select1) == provinceList.get(i).getTid()){
                sp_per_rovince.setSelection(i,false);
            }
        }
        cityList.clear();
        cityList.addAll(getChildList(Integer.valueOf(Select1)));
        city_arr.clear();
        int city_index =0;
        for(int i=0;i<cityList.size();i++){
            city_arr.add(cityList.get(i).getName());
            if(Integer.valueOf(Select2) == cityList.get(i).getTid()){
                city_index = i;
            }
        }
        citityAdapter.notifyDataSetChanged();
        sp_per_city.setSelection(city_index,false);

        regionList.clear();
        regionList.addAll(getChildList(Integer.valueOf(Select2))) ;
        region_arr.clear();
        int region_index =0;
        for(int i=0;i<regionList.size();i++){
            region_arr.add(regionList.get(i).getName());
            if(Integer.valueOf(Select3) == regionList.get(i).getTid()){
                region_index = i;
            }
        }
        regionAdapter.notifyDataSetChanged();
        sp_per_county.setSelection(region_index,false);

        townList.clear();
        townList.addAll(getChildList(Integer.valueOf(Select3)));

        town_arr.clear();
        int town_index =0;
        for(int i=0;i<townList.size();i++){
            town_arr.add(townList.get(i).getName());
            if(Integer.valueOf(Select4) == townList.get(i).getTid()){
                town_index = i;
            }
        }
        townAdapter.notifyDataSetChanged();
        if(townList.size() > 0){
            sp_per_town.setSelection(town_index,false);
        }else{
            sp_per_town.setSelection(0,false);
        }

        sp_per_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                sp_per_city.setSelection(0);

                Select2  = cityList.get(0).getTid()+"";
                regionList.clear();
                regionList.addAll(getChildList(Integer.valueOf(Select2))) ;
                region_arr.clear();
                for(int i=0;i<regionList.size();i++){
                    region_arr.add(regionList.get(i).getName());
                }
                regionAdapter.notifyDataSetChanged();
                sp_per_county.setSelection(0);

                if(regionList.size() > 0){
                    Select3  = regionList.get(0).getTid()+"";
                    townList.clear();
                    townList.addAll(getChildList(Integer.valueOf(Select3)));
                    town_arr.clear();
                    for(int i=0;i<townList.size();i++){
                        town_arr.add(townList.get(i).getName());
                    }
                    townAdapter.notifyDataSetChanged();
                    sp_per_town.setSelection(0);
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
                    sp_per_town.setSelection(0);
                    Select4 = "";
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_per_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                sp_per_county.setSelection(0);
                if(regionList.size() > 0){
                    Select3  = regionList.get(0).getTid()+"";
                    townList.clear();
                    townList.addAll(getChildList(Integer.valueOf(Select3)));
                    town_arr.clear();
                    for(int i=0;i<townList.size();i++){
                        town_arr.add(townList.get(i).getName());
                    }
                    townAdapter.notifyDataSetChanged();
                    sp_per_town.setSelection(0);
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
                    sp_per_town.setSelection(0);
                    Select4 = "";
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_per_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                sp_per_town.setSelection(0);
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

        sp_per_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_personal_xgmm :
                startActivity(new Intent(PersonalActivity.this,ModifyPasswordActivity.class));
                break;
            case R.id.tv_personal_xgsjh :
                startActivity(new Intent(PersonalActivity.this,ModifyPhoneActivity.class));
                break;
            case R.id.bt_personal_bc :
                submitData();
                break;
            case R.id.personal_back :
                finish();
                break;
            default:
                break;
        }
    }

    //获取数据
    public void getPersonalData(){
        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/getPersonalDatat.asp").post(requestBody).build();
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
                    Log.d("PersonalActivity","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<PersonalBean> result = gson.fromJson(content, new TypeToken<Entity<PersonalBean>>(){}.getType());
                        if(result.getCode() == 0){
                            personalBean = result.getData();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }
                }
            }
        });
    }

    public void submitData(){
        PersonalBean personalBean = new PersonalBean();
        personalBean.setSelect1(provinceList.get(sp_per_rovince.getSelectedItemPosition()).getTid()+"");
        personalBean.setSelect2(cityList.get(sp_per_city.getSelectedItemPosition()).getTid()+"");
        personalBean.setSelect3(regionList.get(sp_per_county.getSelectedItemPosition()).getTid()+"");
        personalBean.setSelect4(townList.get(sp_per_town.getSelectedItemPosition()).getTid()+"");
        personalBean.setZsxm(et_personal_zsxm.getText().toString().trim());
        personalBean.setSjh(tv_personal_lxdh.getText().toString().trim());
        personalBean.setSheng(provinceList.get(sp_per_jgs.getSelectedItemPosition()).getName());
        personalBean.setShi(jgshiList.get(sp_per_jgshi.getSelectedItemPosition()).getName());
        personalBean.setMinzu(minzuList.get(sp_per_mz.getSelectedItemPosition()));
        personalBean.setXueli(xueliList.get(sp_per_xueli.getSelectedItemPosition()));
        personalBean.setQq(et_personal_kfqq.getText().toString().trim());
        personalBean.setWeixin(et_personal_kfwx.getText().toString().trim());
        updatePersonalDatat (personalBean);
    }

    //提交数据
    public void updatePersonalDatat(PersonalBean personalBean){
        String mz = "";
        String sheng = "";
        String shi = "";
        String xueli = "";
        String qq = "";
        String weixin = "";
        try {
            mz = URLEncoder.encode(personalBean.getMinzu(),"GB2312");
            sheng = URLEncoder.encode(personalBean.getSheng(),"GB2312");
            shi = URLEncoder.encode(personalBean.getShi(),"GB2312");
            xueli = URLEncoder.encode(personalBean.getXueli(),"GB2312");
            qq = URLEncoder.encode(personalBean.getQq(),"GB2312");
            weixin = URLEncoder.encode(personalBean.getWeixin(),"GB2312");

        } catch (Exception e) {
        } finally {
        }

        RequestBody requestBody = new FormBody.Builder().add("sessionID",sessionID).add("userid",userid)
                .add("Select1",personalBean.getSelect1()).add("Select2",personalBean.getSelect2())
                .add("Select3",personalBean.getSelect3()) .add("Select4",personalBean.getSelect4())
                .addEncoded("minzu",mz).addEncoded("sheng",sheng)
                .addEncoded("shi",shi).addEncoded("xueli",xueli)
                .addEncoded("qq",qq) .addEncoded("weixin",weixin).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/updatePersonalDatat.asp").post(requestBody).build();
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
                    Log.d("PersonalActivity","res=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>(){}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(SUBMIT_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(SUBMIT_DATA_ERROR);
                        }
                    }else{
                        handler.sendEmptyMessage(SUBMIT_DATA_ERROR);
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

    // 创建一个内部类来实现 ,在实现下面内部类之前,需要自定义的Bean对象来封装处理Josn格式的数据
    class  NewsAsyncTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            Gson gson = new Gson();
            String data_minzu = StreamUtils.get(APP.getInstance(), R.raw.minzu);
            Type type_minzu = new TypeToken<List<String>>() { }.getType();
            minzuList.addAll(gson.fromJson(data_minzu, type_minzu));

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

            String data_xueli = StreamUtils.get(APP.getInstance(), R.raw.xueli);
            Type type_xueli = new TypeToken<List<String>>() { }.getType();
            xueliList.addAll(gson.fromJson(data_xueli, type_xueli));

            handler.sendEmptyMessage(DATA_JC);

            //获取数据
            getPersonalData();
            return null;
        }
    }
}


