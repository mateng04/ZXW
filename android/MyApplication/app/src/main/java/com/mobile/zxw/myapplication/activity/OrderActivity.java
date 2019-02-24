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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.ShopOrderAdapter;
import com.mobile.zxw.myapplication.bean.OrderArr;
import com.mobile.zxw.myapplication.bean.PersonalBean;
import com.mobile.zxw.myapplication.bean.ShopCarBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityWXorder;
import com.mobile.zxw.myapplication.jsonEntity.EntityYE;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.ui.area.AreaBean;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.StreamUtils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener,IListener {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;

    private Context mContext;
    private final static int REQUESTCODE = 1; // 返回的结果码

    RadioButton male_zhye;
    RadioGroup rg_order;

    //加载对话框
    private Dialog mLoadingDialog;

    Button bt_order_back;
    Button bt_order_sure;

    private String shopcar,shopcarnum,userid,sessionID;
    private String[] shopcar_arr;
    private String[] shopcarnum_arr;
    int spzj,yf,hj;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    PersonalBean personalBean;

    ListView lv_order;
    private List<ShopCarBean> list_shop_car = new ArrayList<ShopCarBean>();
    private List<ShopCarBean> list_shop_car_temp;
    private ShopOrderAdapter adapter_shop_order;

    TextView tv_order_spzj,tv_order_yf,tv_order_hj;
    Spinner sp_order_rovince,sp_order_city,sp_order_county,sp_order_town;

    EditText et_order_shrxm,et_order_shouji,et_order_xxdz;

    private String Select1,Select2,Select3,Select4;

    private String shouhrxm,shouji,shouhdz,zhiffs;

    String zhye;

    private List<AreaBean> allList;
    private List<AreaBean> provinceList;
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
    private List<AreaBean> townList;

    private List<String>  province_arr;
    private List<String>  city_arr;
    private List<String>  region_arr;
    private List<String>  town_arr;

    ArrayAdapter<String> adapter;
    ArrayAdapter<String> citityAdapter;
    ArrayAdapter<String> regionAdapter;
    ArrayAdapter<String> townAdapter;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int DATA_JC = 2;
    static int PERSONAL_DATA_OK = 3;
    static int PERSONAL_DATA_ERROR = 4;
    static int YE_DATA_OK = 5;
    static int YE_DATA_ERROR = 6;
    static int SUBMIT_DATA_OK = 7;
    static int SUBMIT_DATA_STATE1 = 8;
    static int SUBMIT_DATA_STATE2 = 9;
    static int SUBMIT_DATA_STATE3 = 10;
    static int CZ_DATA_OK = 12;      //微信支付发起成功
    static int CZ_DATA_ERROR = 13;   //微信支付发起失败
    static int ZFJG1 = 9000;
    static int ZFJG2 = 4000;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    setData();
                    getYuE();
                    break;
                case 1:
                    Toast.makeText(mContext,"数据加载失败",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    setJCData();
                    break;
                case 3:
                    setPERSData();
                    cancelDialog();
                    break;
                case 4:
                    break;
                case 5:
                    if(Integer.valueOf(zhye) >= hj){
                        male_zhye.setVisibility(View.VISIBLE);
                        male_zhye.setText("账户余额  您的账户余额为"+zhye+"元。");
                    }else{
                        male_zhye.setVisibility(View.GONE);
                    }
                    break;
                case 6:
                    break;
                case 7:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"商品购买成功,请从订单管理查看详情",Toast.LENGTH_SHORT).show();
                    sharedPreferencesHelper.put("shopcarnum","");
                    sharedPreferencesHelper.put("shopcar","");
                    finish();
                    break;
                case 8:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"连接服务器失败",Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"该商品已下架，无法购买!",Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
                    Toast.makeText(mContext,"商品ID不存在!",Toast.LENGTH_SHORT).show();
                    break;
                case 9000:
                    cancelDialog();
                    Toast.makeText(mContext,"商品购买成功,请从订单管理查看详情",Toast.LENGTH_LONG).show();
                    sharedPreferencesHelper.put("shopcarnum","");
                    sharedPreferencesHelper.put("shopcar","");
                    finish();
                    break;
                case 4000:
                    cancelDialog();
//                    Toast.makeText(mContext,"商品ID不存在!",Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
//                    Toast.makeText(mContext,"商品ID不存在!",Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    bt_order_sure.setEnabled(true);
                    cancelDialog();
//                    Toast.makeText(mContext,"商品ID不存在!",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mContext = this;

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, APP.APP_ID, false);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                OrderActivity.this, "config");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");

        shopcar = (String) sharedPreferencesHelper.getSharedPreference("shopcar", "");
        shopcar_arr = shopcar.split("-");
        shopcarnum = (String) sharedPreferencesHelper.getSharedPreference("shopcarnum", "");
        shopcarnum_arr = shopcarnum.split("-");

        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        initData();
    }


    public void initView(){
        lv_order = (ListView) findViewById(R.id.lv_order);
        adapter_shop_order = new ShopOrderAdapter(OrderActivity.this,list_shop_car,sharedPreferencesHelper);
        lv_order.setAdapter(adapter_shop_order);

        tv_order_spzj = (TextView) findViewById(R.id.tv_order_spzj);
        tv_order_yf = (TextView) findViewById(R.id.tv_order_yf);
        tv_order_hj = (TextView) findViewById(R.id.tv_order_hj);

        bt_order_back = (Button) findViewById(R.id.bt_order_back);
        bt_order_back.setOnClickListener(this);
        bt_order_sure = (Button) findViewById(R.id.bt_order_sure);
        bt_order_sure.setOnClickListener(this);

        et_order_shrxm = (EditText) findViewById(R.id.et_order_shrxm);
        et_order_shouji = (EditText) findViewById(R.id.et_order_shouji);
        et_order_xxdz = (EditText) findViewById(R.id.et_order_xxdz);

        sp_order_rovince = (Spinner) findViewById(R.id.sp_order_rovince);
        sp_order_city = (Spinner) findViewById(R.id.sp_order_city);
        sp_order_county = (Spinner) findViewById(R.id.sp_order_county);
        sp_order_town = (Spinner) findViewById(R.id.sp_order_town);

        male_zhye = (RadioButton) findViewById(R.id.male_zhye);
        rg_order = (RadioGroup) findViewById(R.id.rg_order);
        rg_order.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.male_zhye:
                        zhiffs = "账户余额";
                    break;
                    case R.id.male_zfb:
                        zhiffs = "支付宝";
                        break;
                    case R.id.male_wx:
                        zhiffs = "微信";
                        break;
                    default:
                    break;
                }
            }
        });
    }
    public void initData(){
        showDialog("正在加载数据");

        list_shop_car.clear();

        provinceList = new ArrayList<>();
        province_arr = new ArrayList<>();
        adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,province_arr);
        sp_order_rovince.setAdapter(adapter);

        cityList = new ArrayList<>();
        city_arr = new ArrayList<>();
        citityAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,city_arr);
        sp_order_city.setAdapter(citityAdapter);

        regionList = new ArrayList<>();
        region_arr = new ArrayList<>();
        regionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,region_arr);
        sp_order_county.setAdapter(regionAdapter);

        townList = new ArrayList<>();
        town_arr = new ArrayList<>();
        townAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,town_arr);
        sp_order_town.setAdapter(townAdapter);

        sp_order_rovince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_order_city, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_order_city.setSelection(city_index);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_order_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_order_county, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_order_county.setSelection(region_index);
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

        sp_order_county.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);	//设置mOldSelectedPosition可访问
                    field.setInt(sp_order_town, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
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
                sp_order_town.setSelection(town_index);
                System.out.println("xx--sp_recruit_content_county------"+town_index);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        sp_order_town.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        new NewsAsyncTask().execute();
    }

    public void setJCData(){
        for(int i=0;i<provinceList.size();i++){
            province_arr.add(provinceList.get(i).getName());
        }
        adapter.notifyDataSetChanged();
    }

    public void setPERSData(){

        Select1 = personalBean.getSelect1();
        Select2 = personalBean.getSelect2();
        Select3 = personalBean.getSelect3();
        Select4 = personalBean.getSelect4();

        et_order_shrxm.setText(personalBean.getZsxm());
        et_order_shouji.setText(personalBean.getSjh());
        et_order_xxdz.setText(personalBean.getDizhi());

        for(int i=0;i<provinceList.size();i++){
            if(Integer.valueOf(Select1) == provinceList.get(i).getTid()){
                sp_order_rovince.setSelection(i,false);
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
        sp_order_city.setSelection(city_index,false);

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
        sp_order_county.setSelection(region_index,false);

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
            sp_order_town.setSelection(town_index,false);
        }else{
            sp_order_town.setSelection(0,false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_order_back :
                finish();
                break;
            case R.id.bt_order_sure :
                submitData();
                break;
            default:
                break;
        }
    }

    public void submitData(){
        shouhrxm = et_order_shrxm.getText().toString().trim();
        shouji = et_order_shouji.getText().toString().trim();
        shouhdz = et_order_xxdz.getText().toString().trim();

        if("".equals(shouhrxm) || "".equals(shouji) || "".equals(shouhdz)){
            Toast.makeText(mContext,"收货人信息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

        if(zhiffs == null || "".equals(zhiffs)){
            zhiffs = "微信";
        }
        System.out.println("shouhrxm--"+shouhrxm+"shouji--"+shouji+"Select1--"+Select1+"Select2--"+Select2);
        System.out.println("Select3--"+Select3+"Select4--"+Select4+"shouhdz--"+shouhdz+"zhiffs--"+zhiffs);
        System.out.println("zongjia--"+hj);
//        shop/buy3.asp?

        if("支付宝".equals(zhiffs)){
            String mshopcar = shopcar.replace("-",",");
            String mshopcarnum = shopcarnum.replace("-",",");

            Intent intent = new Intent(this, H5PayDemoActivity.class);
            Bundle extras = new Bundle();
            String url = "http://zhengxinw.com/appServic/user/buy3.asp?userid=" + userid + "&sessionID=" + sessionID + "&shouji=" +
                    shouji +"&shouhrxm=" + shouhrxm +"&Select1=" + Select1 +"&Select2=" + Select2+"&Select3=" + Select3+"&Select4=" +
                    Select4+"&shouhdz=" + shouhdz + "&zhiffs=" + "zhifubao" + "&shopcar=" + mshopcar + "&shopcarnum=" + mshopcarnum;
            extras.putString("url", url);
            intent.putExtras(extras);
            startActivityForResult(intent,REQUESTCODE);
        }else if("微信".equals(zhiffs)){
            showDialog("");
            wxChongZhi();
        }else{
            showDialog("");
            buy3();
        }



//        shouhrxm:%D5%D4%CA%C0%BD%DC  赵世杰
//        shouji:13772373442
//        Select1:340000
//        Select2:340500
//        Select3:340503
//        Select4:340503001
//        shouhdz:%3F%3F%3F%3F%3F%3F   ??????
//        zhiffs:%D5%CB%BB%A7%D3%E0%B6%EE   账户余额
//        zongjia:2
    }

    public void setData(){
        list_shop_car.clear();
        for(int i=0;i<list_shop_car_temp.size();i++){
            for(int x=0;x<shopcar_arr.length;x++){
                if(shopcar_arr[x].equals(list_shop_car_temp.get(i).getShopID())){
                    list_shop_car_temp.get(i).setShopNum(shopcarnum_arr[x]);
                }
            }
        }
        list_shop_car.addAll(list_shop_car_temp);
        adapter_shop_order.notifyDataSetChanged();
        setHeight(lv_order,adapter_shop_order);

        for(int i=0;i<list_shop_car.size();i++){
            if( list_shop_car.get(i).getFreight() != null && !"".equals( list_shop_car.get(i).getFreight())){
                yf = yf + (Integer.valueOf(list_shop_car.get(i).getFreight())*Integer.valueOf(list_shop_car.get(i).getShopNum()));
            }
            if( list_shop_car.get(i).getShopPrice() != null && !"".equals( list_shop_car.get(i).getShopPrice())){
                spzj = spzj + (Integer.valueOf(list_shop_car.get(i).getShopPrice())*Integer.valueOf(list_shop_car.get(i).getShopNum()));
            }
        }
        hj = spzj + yf;
        tv_order_spzj.setText(spzj+"");
        tv_order_yf.setText(yf+"");
        tv_order_hj.setText(hj+"");
    }

    //获取数据
    public void getShopCarData(){

        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid).add("sessionID",sessionID).add("shopID",shopcar).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/getShopCarData.asp").post(requestBody).build();
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
                    Log.d("getShopCarData","getShopCarData=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();

                        Entity<List<ShopCarBean>> result = gson.fromJson(content, new TypeToken<Entity<List<ShopCarBean>>>() {}.getType());

                        if( result.getCode() == 0){
                            list_shop_car_temp = result.getData();

                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }

                    }
                }
            }
        });
    }

    public void setHeight(ListView listview, BaseAdapter adapter){
        int height = 0;
        int count = adapter.getCount();
        for(int i=0;i<count;i++){
            View temp = adapter.getView(i,null,listview);
            temp.measure(0,0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.width = ViewGroup.LayoutParams.FILL_PARENT;
        params.height = height;
    }

    @Override
    public void notifyAllActivity(int tag, String str,String city) {
        if(tag == 100){
            Toast.makeText(mContext,"支付成功",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(SUBMIT_DATA_OK);
        }else if(tag == 101){
            Toast.makeText(mContext,"支付错误",Toast.LENGTH_SHORT).show();
        }else if(tag == 102){
            Toast.makeText(mContext,"用户取消支付",Toast.LENGTH_SHORT).show();
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

            handler.sendEmptyMessage(DATA_JC);

            //获取数据
            getShopCarData();
            getPersonalData();
            return null;
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
                            handler.sendEmptyMessage(PERSONAL_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(PERSONAL_DATA_ERROR);
                        }
                    }
                }
            }
        });
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

    //获取余额
    public void getYuE(){

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

    public void wxChongZhi(){
        bt_order_sure.setEnabled(false);

        String mshopcar = shopcar.replace("-",",");
        String mshopcarnum = shopcarnum.replace("-",",");

        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid).add("sessionID",sessionID)
                .add("shouji",shouji).add("shouhrxm",shouhrxm)
                .add("Select1",Select1).add("Select2",Select2)
                .add("Select3",Select3).add("Select4",Select4)
                .add("shouhdz",shouhdz).add("zhiffs","wx")
                .add("shopcar",mshopcar).add("shopcarnum",mshopcarnum).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/m/wechat_shop/buy3.asp").post(requestBody).build();
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
                    Log.d("wxChongZhi","=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        Log.d("wxChongZhi","result=="+result);
                        Log.d("wxChongZhi"," result.getCode()=="+ result.getCode());
                        if( result.getCode() == 0){
                                Log.d("wxChongZhi","result==2222");
//                            Gson gson = new Gson();
                                EntityWXorder entityWXorder = gson.fromJson(content, new TypeToken<EntityWXorder>() {}.getType());
                                Log.d("wxChongZhi.",entityWXorder.toString()+"");
                                Log.d("wxChongZhi",entityWXorder.getSuccess()+"");
                                if(entityWXorder.getSuccess() == 1){
                                    OrderArr orderArr = entityWXorder.getOrder_arr();
                                    PayReq req = new PayReq();
                                    req.appId = orderArr.getAppid();
                                    req.partnerId = orderArr.getPartnerid();
                                    req.prepayId = orderArr.getPrepayid();
                                    req.packageValue = "Sign=WXPay";
                                    req.nonceStr = orderArr.getNoncestr();
                                    req.timeStamp = orderArr.getTimestamp();
                                    req.extData = entityWXorder.getData();
                                    req.sign = orderArr.getSign();
                                    boolean rest = wxapi.sendReq(req);
//                            Toast.makeText(OnlineRechargeActivity.this, "调起支付结果:" +rest, Toast.LENGTH_LONG).show();
                                    Log.d("调起支付结果","-"+rest);
                                    handler.sendEmptyMessage(CZ_DATA_OK);
                                }else{
                                    handler.sendEmptyMessage(CZ_DATA_ERROR);
                                }

                        }else if( result.getCode() == 200){
                            handler.sendEmptyMessage(SUBMIT_DATA_OK);
                        }else if( result.getCode() == 1){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
                        }else if( result.getCode() == 2){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
                        }else if( result.getCode() == 3){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
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

    //获取数据
    public void buy3(){

        shouhrxm = et_order_shrxm.getText().toString().trim();
        shouji = et_order_shouji.getText().toString().trim();
        shouhdz = et_order_xxdz.getText().toString().trim();

        String zffs = "";
        if("支付宝".equals(zhiffs)){
            zffs = "zhifubao";
        }else{
            zffs = "zhanghuyue";
        }

        String mshopcar = shopcar.replace("-",",");
        String mshopcarnum = shopcarnum.replace("-",",");

        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid).add("sessionID",sessionID)
                .add("shouji",shouji).add("shouhrxm",shouhrxm)
                .add("Select1",Select1).add("Select2",Select2)
                .add("Select3",Select3).add("Select4",Select4)
                .add("shouhdz",shouhdz).add("zhiffs",zffs)
                .add("shopcar",mshopcar).add("shopcarnum",mshopcarnum)
                .build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/buy3.asp").post(requestBody).build();
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
                    Log.d("buy3","buy3=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();

                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());

                        if( result.getCode() == 0){
                            handler.sendEmptyMessage(SUBMIT_DATA_OK);
                        }else if( result.getCode() == 1){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
                        }else if( result.getCode() == 2){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
                        }else if( result.getCode() == 3){
                            handler.sendEmptyMessage(SUBMIT_DATA_STATE1);
                        }

                    }
                }
            }
        });
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


