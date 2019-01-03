package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.ShopCarAdapter;
import com.mobile.zxw.myapplication.bean.ShopCarBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.ui.widget.XListView;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopCarActivity extends AppCompatActivity  implements XListView.IXListViewListener, View.OnClickListener {


    private Context mContext;
    private XListView mListView;
    private List<ShopCarBean> list_shop_car = new ArrayList<ShopCarBean>();
    private List<ShopCarBean> list_shop_car_temp;
    private ShopCarAdapter adapter_shop_car;

    Button shop_car_back;
    Button tv_shop_car_clear,tv_shop_car_continue;

    RelativeLayout rl_shop_car_spzj,rl_shop_car_yf,rl_shop_car_hj;
    TextView tv_shop_car_spzj,tv_shop_car_yf,tv_shop_car_hj;
    int spzj,yf,hj;

    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private String shopcar,shopcarnum,userid,sessionID;
    private String[] shopcar_arr;
    private String[] shopcarnum_arr;

    private int countPage = 1;
    private int currtPage = 0;

    private TextView tv_shop_car_sure;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    setData();
                    break;
                case 1:
                    Toast.makeText(mContext,"数据加载失败",Toast.LENGTH_SHORT).show();
                    break;
                case 100:
                    pushPrice();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        mContext = this;
        APP.getInstance().addActivity(ShopCarActivity.this);

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                ShopCarActivity.this, "config");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");



        initViews();

    }

    //操作控件
    private void initViews() {

        mListView = (XListView)findViewById(R.id.lv_shop_car);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_shop_car = new ShopCarAdapter(ShopCarActivity.this,list_shop_car,sharedPreferencesHelper,handler);
        mListView.setAdapter(adapter_shop_car);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                System.out.println("position-----"+position);
                Intent intent = new Intent(mContext,ShopDetailsActivity.class);
                intent.putExtra("xxid",list_shop_car.get(position-1).getShopID());
                mContext.startActivity(intent);
            }

        });

        tv_shop_car_sure = (TextView) findViewById(R.id.tv_shop_car_sure);
        tv_shop_car_sure.setOnClickListener(this);

        shop_car_back = (Button) findViewById(R.id.shop_car_back);
        shop_car_back.setOnClickListener(this);

        tv_shop_car_clear = (Button) findViewById(R.id.tv_shop_car_clear);
        tv_shop_car_clear.setOnClickListener(this);

        tv_shop_car_continue = (Button) findViewById(R.id.tv_shop_car_continue);
        tv_shop_car_continue.setOnClickListener(this);

        tv_shop_car_spzj = (TextView) findViewById(R.id.tv_shop_car_spzj);
        tv_shop_car_yf = (TextView) findViewById(R.id.tv_shop_car_yf);
        tv_shop_car_hj = (TextView) findViewById(R.id.tv_shop_car_hj);

        rl_shop_car_spzj = (RelativeLayout) findViewById(R.id.rl_shop_car_spzj);
        rl_shop_car_yf = (RelativeLayout) findViewById(R.id.rl_shop_car_yf);
        rl_shop_car_hj = (RelativeLayout) findViewById(R.id.rl_shop_car_hj);


    }

    @Override
    protected void onResume() {
        super.onResume();
        shopcar = (String) sharedPreferencesHelper.getSharedPreference("shopcar", "");
        shopcar_arr = shopcar.split("-");
        shopcarnum = (String) sharedPreferencesHelper.getSharedPreference("shopcarnum", "");
        shopcarnum_arr = shopcarnum.split("-");
        initData();
    }

    public void initData(){
        getShopCarData();
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
        adapter_shop_car.notifyDataSetChanged();
        onLoad();

        pushPrice();
    }

    public void pushPrice(){
        yf = 0;
        spzj = 0;
        hj = 0;

        for(int i=0;i<list_shop_car.size();i++){
            if( list_shop_car.get(i).getFreight() != null && !"".equals( list_shop_car.get(i).getFreight())){
                yf = yf + (Integer.valueOf(list_shop_car.get(i).getFreight())*Integer.valueOf(list_shop_car.get(i).getShopNum()));
            }
            if( list_shop_car.get(i).getShopPrice() != null && !"".equals( list_shop_car.get(i).getShopPrice())){
                spzj = spzj + (Integer.valueOf(list_shop_car.get(i).getShopPrice())*Integer.valueOf(list_shop_car.get(i).getShopNum()));
            }
        }
        hj = spzj + yf;
        tv_shop_car_spzj.setText(spzj+"");
        tv_shop_car_yf.setText(yf+"");
        tv_shop_car_hj.setText(hj+"");

        if(list_shop_car.size() > 0){
            tv_shop_car_sure.setVisibility(View.VISIBLE);
            rl_shop_car_spzj.setVisibility(View.VISIBLE);
            rl_shop_car_yf.setVisibility(View.VISIBLE);
            rl_shop_car_hj.setVisibility(View.VISIBLE);
        }else{
            tv_shop_car_sure.setVisibility(View.GONE);
            rl_shop_car_spzj.setVisibility(View.GONE);
            rl_shop_car_yf.setVisibility(View.GONE);
            rl_shop_car_hj.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {

            mListView.autoRefresh();
        }
    }

    @Override
    public void onRefresh() {
        System.out.println("----------------onRefresh");
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        getShopCarData();
    }

    @Override
    public void onLoadMore() {
        System.out.println("----------------onLoadMore");
        getShopCarData();
    }

    private void onLoad() {
        System.out.println("----------------onLoad");
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(Utils.getTime());

        if(currtPage >= countPage){
            mListView.setPullLoadComplete(true);
        }else {
            mListView.setPullLoadComplete(false);
        }
        mListView.setPullLoadEnable(true);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_shop_car_sure:
                startActivity(new Intent(ShopCarActivity.this,OrderActivity.class));
            break;
            case R.id.shop_car_back:
               finish();
            break;
            case R.id.tv_shop_car_clear:
                sharedPreferencesHelper.put("shopcarnum","");
                sharedPreferencesHelper.put("shopcar","");
                list_shop_car.clear();
                shopcar = "";
                shopcarnum = "";
                adapter_shop_car.notifyDataSetChanged();
                tv_shop_car_sure.setVisibility(View.GONE);
                rl_shop_car_spzj.setVisibility(View.GONE);
                rl_shop_car_yf.setVisibility(View.GONE);
                rl_shop_car_hj.setVisibility(View.GONE);
            break;
            case R.id.tv_shop_car_continue:
                APP.getInstance().removeALLActivity();
            break;
            default:
            break;
        }
    }

    //获取数据
    public void getShopCarData(){

        currtPage = currtPage + 1;

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
}
