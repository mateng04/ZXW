package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.MallManageAdapter;
import com.mobile.zxw.myapplication.bean.PopItem;
import com.mobile.zxw.myapplication.bean.RecruitManageBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityFY;
import com.mobile.zxw.myapplication.ui.PopupDownMenu;
import com.mobile.zxw.myapplication.ui.widget.XListView;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;

import java.io.IOException;
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

import static com.mobile.zxw.myapplication.until.Data.initFirstMallData;
import static com.mobile.zxw.myapplication.until.Data.initSecondData;
import static com.mobile.zxw.myapplication.until.Data.initThirdMallData;

public class MallManageActivity extends AppCompatActivity implements XListView.IXListViewListener, View.OnClickListener{

    private Context mContext;

    //三个按钮和整体布局
    private TextView tv_mall_demo;
    private TextView tv_mall_demo2;
    private TextView tv_mall_demo3;

    //存放列表数据的List
    private List<PopItem> itemList = new ArrayList<>();
    private List<PopItem> itemList2 = new ArrayList<>();
    private List<PopItem> itemList3 = new ArrayList<>();

    //黑色背景布局
    private View darkView;
    private Animation animIn;
    private Animation animOut;

    //PopupDownMenu的定义
    private PopupDownMenu p1;
    private PopupDownMenu p2;
    private PopupDownMenu p3;

    //刷选参数
    private String dataflag = "0";     //商城0 微商1 flag
    private String youxiaoqi = "2";     // 2代表全部  0代表正常   1代表过期
    private String bigshopclass = "";     //大类
    private String smallshopclass = "";     // 小类

    private XListView mListView;
    private List<RecruitManageBean> list_messages = new ArrayList<RecruitManageBean>();
    List<RecruitManageBean> temp_list_messages;
    private MallManageAdapter adapter_message;

    //加载对话框
    private Dialog mLoadingDialog;

    private  int countPage = 0;
    private  int currtPage = 0;
    String xxid;
    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private Button bt_mall_manage_fbsp;
    private Button mall_manage_back;
    int position = 0;   //  删除的位置

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int DATA_TOP = 2;
    static int DATA_DELETE = 3;
    static int DATA_DELETE_OK = 4;
    static int DATA_DELETE_ERROR = 5;
    static int DATA_SHAXIN = 6;
    static int DATA_SHAXIN_OK = 7;
    static int DATA_SHAXIN_ERROR = 8;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    if(currtPage == 1 || currtPage == 0){
                        list_messages.clear();
                    }
                    if(temp_list_messages != null){
                        list_messages.addAll(temp_list_messages);
                    }
                    adapter_message.notifyDataSetChanged();
                    onLoad();
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    showDialog("正在删除数据");
                    xxid = (String) msg.obj;
                    position = msg.arg1;
                    shanchushop();
                    break;
                case 4:
//                    if(position == 0){
//                        return;
//                    }
                    list_messages.remove(position);
                    adapter_message.notifyDataSetChanged();
                    cancelDialog();
                    break;
                case 5:
                    cancelDialog();
                    Toast.makeText(mContext,"删除失败",Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    showDialog("正在刷新数据");
                    xxid = (String) msg.obj;
                    position = msg.arg1;
                    shuaxinshop();
                    break;
                case 7:
                    cancelDialog();
                    Toast.makeText(mContext,"刷新成功",Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    cancelDialog();
                    Toast.makeText(mContext,"刷新失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mall_manage);
        mContext = this;
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                MallManageActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        initView();
        initData();
        initPop();
    }

    public void initView(){
        //三个按钮的初始化
        tv_mall_demo = findViewById(R.id.tv_mall_demo);
        tv_mall_demo2 = findViewById(R.id.tv_mall_demo2);
        tv_mall_demo3 = findViewById(R.id.tv_mall_demo3);
        tv_mall_demo.setOnClickListener(this);
        tv_mall_demo2.setOnClickListener(this);
        tv_mall_demo3.setOnClickListener(this);

        //黑色背景的初始化
        animIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_anim);
        animOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_anim);
        darkView = findViewById(R.id.main_darkview);
        darkView.startAnimation(animIn);
        darkView.setVisibility(View.GONE);


        mListView = (XListView)findViewById(R.id.lv_mall_manage);
        mListView.setPullRefreshEnable(true);
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(true);
        mListView.setAutoLoadEnable(true);
        mListView.setXListViewListener(this);
        mListView.setRefreshTime(Utils.getTime());

        adapter_message = new MallManageAdapter(MallManageActivity.this,list_messages,handler,dataflag);
        mListView.setAdapter(adapter_message);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(mContext,"position--"+position,Toast.LENGTH_SHORT).show();
                String shopid = list_messages.get(position-1).getId();
                if("0".equals(dataflag)){
                    Intent intent = new Intent(MallManageActivity.this,ModifyMallActivity.class);
                    intent.putExtra("xxid",shopid);
                    startActivity(intent);
                }else{
                    Intent intent2 = new Intent(MallManageActivity.this,ModifyWSMallActivity.class);
                    intent2.putExtra("xxid",shopid);
                    startActivity(intent2);
                }

            }
        });

        bt_mall_manage_fbsp = (Button)findViewById(R.id.bt_mall_manage_fbsp);
        bt_mall_manage_fbsp.setOnClickListener(this);
        mall_manage_back = (Button)findViewById(R.id.mall_manage_back);
        mall_manage_back.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            mListView.autoRefresh();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_mall_demo:
                //背景颜色变暗
                darkView.startAnimation(animIn);
                darkView.setVisibility(View.VISIBLE);
                //避免另外两个pop窗口还存在的情况
                if (p2.popupWindow.isShowing()) {
                    p2.popupWindow.dismiss();
                }
                if (p3.popupWindow.isShowing()) {
                    p3.popupWindow.dismiss();
                }
                //利用popFlag来实现连续点击同一按钮，点了一次再点外面布局的pop窗弹出效果
                if (!p1.popupWindow.isShowing()) {
                    v.setSelected(true);
                    //创建后立即呈现
                    p1.popupWindow.showAsDropDown(v,0,5);
                }
                //实现第二次点击收回的效果
                else {
                    p1.popupWindow.dismiss();
                    //背景颜色变回正常
                    darkView.startAnimation(animOut);
                    darkView.setVisibility(View.GONE);
                }
                break;

            case R.id.tv_mall_demo2:
                //背景颜色变暗
                darkView.startAnimation(animIn);
                darkView.setVisibility(View.VISIBLE);
                //避免另外两个pop窗口还存在的情况
                if (p1.popupWindow.isShowing()) {
                    p1.popupWindow.dismiss();
                }
                if (p3.popupWindow.isShowing()) {
                    p3.popupWindow.dismiss();
                }
                //利用popFlag来实现连续点击同一按钮，点了一次再点外面布局的pop窗弹出效果
                if (!p2.popupWindow.isShowing()) {
                    v.setSelected(true);
                    //创建后立即呈现
                    p2.popupWindow.showAsDropDown(v,0,5);
                }
                //实现第二次点击收回的效果
                else {
                    p2.popupWindow.dismiss();
                    //背景颜色变回正常
                    darkView.startAnimation(animOut);
                    darkView.setVisibility(View.GONE);
                }
                break;

            case R.id.tv_mall_demo3:
                //背景颜色变暗
                darkView.startAnimation(animIn);
                darkView.setVisibility(View.VISIBLE);
                //避免另外两个pop窗口还存在的情况
                if (p1.popupWindow.isShowing()) {
                    p1.popupWindow.dismiss();
                }
                if (p2.popupWindow.isShowing()) {
                    p2.popupWindow.dismiss();
                }
                //利用popFlag来实现连续点击同一按钮，点了一次再点外面布局的pop窗弹出效果
                if (!p3.popupWindow.isShowing()) {
                    v.setSelected(true);
                    p3.popupWindow.showAsDropDown(v,0,5);
                }
                //实现第二次点击收回的效果
                else {
                    p3.popupWindow.dismiss();
                    //背景颜色变回正常
                    darkView.startAnimation(animOut);
                    darkView.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_mall_manage_fbsp:
                startActivity(new Intent(MallManageActivity.this,ReleaseMallActivity.class));
                break;
            case R.id.mall_manage_back:
                finish();
                break;
            default:
                break;
        }
    }

    public void initData(){
        //初始化数据
        initFirstMallData(itemList);
        initSecondData(itemList2);
        initThirdMallData(itemList3);

        shopguanli();
    }


    private void initPop() {

        View view;
        ListView firstListView;
        ListView secondListView;
        ListView thirdListView;

        //三个popupWindow的背景
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.bg_filter_down);

        //初始化PopupWindow中的ListView
        view = LayoutInflater.from(this).inflate(R.layout.popup_one_layout, null);
        firstListView = view.findViewById(R.id.pop_listview);

        p1 = new PopupDownMenu(this, itemList, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT, view, drawable, firstListView);
        p1.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                //背景颜色变回正常
                darkView.startAnimation(animOut);
                darkView.setVisibility(View.GONE);

                //处理数据方式要防止取得的数据是不正常的数据
                if(p1.results[3]!=null) {
                    if (p1.results[0] != null) {
                        tv_mall_demo.setText(p1.results[0]);
                    }
//                    Toast.makeText(RecruitManageActivity.this, p1.results[0], Toast.LENGTH_SHORT).show();
                    if("商城".equals(p1.results[0]) && "0".equals(dataflag)){
                        tv_mall_demo.setSelected(false);
                        return;
                    }
                    if("微商".equals(p1.results[0]) && "1".equals(dataflag)){
                        tv_mall_demo.setSelected(false);
                        return;
                    }

                    if("商城".equals(p1.results[0])){
                        dataflag = "0";
                    }else if("微商".equals(p1.results[0])){
                        dataflag = "1";
                    }
                    adapter_message.setFlag(dataflag);
                    queryData();
                }
                tv_mall_demo.setSelected(false);
            }
        });

        //初始化PopupWindow中的两个ListView
        view = LayoutInflater.from(this).inflate(R.layout.popup_one_layout, null);
        firstListView = view.findViewById(R.id.pop_listview);

        p2 = new PopupDownMenu(this, itemList2, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT, view, drawable, firstListView);
        p2.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                //背景颜色变回正常
                darkView.startAnimation(animOut);
                darkView.setVisibility(View.GONE);

                //处理数据方式要防止取得的数据是不正常的数据
                if(p2.results[3]!=null) {
                    if (p2.results[0] != null) {
                        tv_mall_demo2.setText(p2.results[0]);
                    }
//                    Toast.makeText(RecruitManageActivity.this, p2.results[0] , Toast.LENGTH_SHORT).show();
                    if("全部".equals(p2.results[0]) && "2".equals(youxiaoqi)){
                        tv_mall_demo2.setSelected(false);
                        return;
                    }
                    if("正常".equals(p2.results[0]) && "0".equals(youxiaoqi)){
                        tv_mall_demo2.setSelected(false);
                        return;
                    }
                    if("过期".equals(p2.results[0]) && "1".equals(youxiaoqi)){
                        tv_mall_demo2.setSelected(false);
                        return;
                    }

                    if("全部".equals(p2.results[0])){
                        youxiaoqi = "2";
                    }else if("正常".equals(p2.results[0])){
                        youxiaoqi = "0";
                    }else if("过期".equals(p2.results[0])){
                        youxiaoqi = "1";
                    }
                    queryData();
                }
                tv_mall_demo2.setSelected(false);
            }
        });

        //初始化PopupWindow中的两个ListView
        view = LayoutInflater.from(this).inflate(R.layout.popup_double_layout, null);
        firstListView = view.findViewById(R.id.pop_listview_left);
        secondListView = view.findViewById(R.id.pop_listview_right);

        p3 = new PopupDownMenu(this, itemList3, WindowManager.LayoutParams.MATCH_PARENT,800, view, drawable, firstListView, secondListView);
        p3.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                //背景颜色变回正常
                darkView.startAnimation(animOut);
                darkView.setVisibility(View.GONE);

                //处理数据方式要防止取得的数据是不正常的数据
                if (p3.results[3] != null) {
                    if (p3.results[0] != null) {
                        if (p3.results[1] != null) {
//                            tv_demo3.setText(p3.results[1]);
                        } else {
//                            tv_demo3.setText(p3.results[0]);
                        }
                    }

                    if("不限".equals(p3.results[0]) && "".equals(bigshopclass) && "不限".equals(p3.results[1]) && "".equals(smallshopclass)){
                        tv_mall_demo3.setSelected(false);
                        return;
                    }

                    if(bigshopclass.equals(p3.results[0]) && smallshopclass.equals(p3.results[1])){
                        tv_mall_demo3.setSelected(false);
                        return;
                    }


                    if("不限".equals(p3.results[0])){
                        bigshopclass = "";
                    }else {
                        bigshopclass = p3.results[0];
                    }
                    if("不限".equals(p3.results[1])){
                        smallshopclass = "";
                    }else {
                        if(p3.results[1] == null){
                            smallshopclass = "";
                        }else{
                            smallshopclass = p3.results[1];
                        }

                    }
                    queryData();
//                    Toast.makeText(RecruitManageActivity.this, p3.results[0] + "+" + p3.results[1], Toast.LENGTH_SHORT).show();
                }
                tv_mall_demo3.setSelected(false);
            }
        });
    }

    private void queryData(){
        System.out.println("queryData-----------dataflag:"+dataflag+"------youxiaoqi:"+youxiaoqi+"------bigzhiweiclass:"+bigshopclass+"------smallzhiweiclass:"+smallshopclass);
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        shopguanli();
    }

    @Override
    public void onRefresh() {
        currtPage = 0;
        mListView.setPullLoadComplete(false);
        mListView.setPullLoadEnable(false);
        shopguanli();
    }

    @Override
    public void onLoadMore() {
        shopguanli();
    }

    private void onLoad() {

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
    //商品管理
    public void shopguanli(){
        String dl = "";
        String xl = "";
        try {
            dl = URLEncoder.encode(bigshopclass,"GB2312");
            xl = URLEncoder.encode(smallshopclass,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        currtPage = currtPage + 1;
        RequestBody requestBody = new FormBody.Builder()
                .add("currtPage",currtPage+"").add("num","20")
                .add("sessionID",sessionID).add("userid",userid)
                .add("type",dataflag).add("youxiaoqi",youxiaoqi)
                .addEncoded("bigshopclass",dl).addEncoded("smallshopclass",xl)
                .build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopguanli.asp").post(requestBody).build();
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
                    Log.d("zhaopinguanli","content=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityFY<List<RecruitManageBean>> result = gson.fromJson(content, new TypeToken<EntityFY<List<RecruitManageBean>>>() {}.getType());
                        String code = result.getCode();
                        if("0".equals(code)){
                            countPage = result.getCountPage();
                            currtPage = result.getCurrtPage();
                            temp_list_messages = result.getData();

                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    }
                }
            }
        });
    }

    //删除商品信息
    public void shanchushop(){

        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid)
                .add("spid",xxid).add("type",dataflag).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shanchushop.asp").post(requestBody).build();
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
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(DATA_DELETE_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_DELETE_ERROR);
                        }
                    }
                }
            }
        });
    }

    //刷新商品信息
    public void shuaxinshop(){

        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID",sessionID).add("userid",userid)
                .add("spid",xxid).add("type",dataflag).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/user/shopshuaxin.asp").post(requestBody).build();
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
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if(result.getCode() == 0){
                            handler.sendEmptyMessage(DATA_SHAXIN_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_SHAXIN_ERROR);
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
}


