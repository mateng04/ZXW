package com.mobile.zxw.myapplication.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.RecyclerViewNoBgAdapter;
import com.mobile.zxw.myapplication.adapter.ShopParamAdapter;
import com.mobile.zxw.myapplication.bean.HomeSCBean;
import com.mobile.zxw.myapplication.bean.ShopDetailsBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.myinterface.MenuCommand;
import com.mobile.zxw.myapplication.ui.CenterMenuDialog;
import com.mobile.zxw.myapplication.ui.Menu;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.mobile.zxw.myapplication.until.Utils;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ShopDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext = null;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;
    String sessionID;
    String userid;
    String xxid;

    private String shopcar,shopcarnum;
    private String[] shopcar_arr;
    private String[] shopcarnum_arr;

    //加载对话框
    private Dialog mLoadingDialog;

    ShopDetailsBean shopDetailsBean;

    Button bt_shop_details_back;
    ImageView iv_shop_details_cplxtp;
    TextView tv_shop_details_jiage, tv_shop_details_kucui, tv_shop_details_title,tv_shop_details_fahuodi;
    TextView tv_shop_details_qq, tv_shop_details_weixin, tv_shop_details_zyts;
    TextView tv_shop_details_cpxqnr;
    LinearLayout ll_shop_details_qqkf;
    LinearLayout ll_shop_details_shoucang;
    Button bt_shop_details_jrgwc,bt_shop_details_ljgm;
    TextView tv_shop_details_pjsl,tv_shop_details_pjry,tv_shop_details_pjnr,tv_shop_details_pjsj;

    private RecyclerView recycler_shop_deatails;
    RecyclerViewNoBgAdapter recyclerViewNoBgAdapter;
    List<String> list_url = new ArrayList<>();

    List<HomeSCBean> list_sd = new ArrayList<>();

    Spinner sp_rl_sd_splx;
    ArrayAdapter<String> splxAdapter;
    private List<String>  splxList;
    private List<String>  cplxidList = new ArrayList<>();

    RelativeLayout rl_sd_spcs;
    PopupWindow popWindow;

    //评价查看更多
    TextView tv_shop_details_ckgd;

    private List<String> list_spcs = new ArrayList<String>();

    //分享
    Button bt_shop_details_share;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int SHOUCANG_DATA_OK = 2;
    static int SHOUCANG_DATA_ERROR = 3;
    static int SHOUCANG_DATA_CZ = 4;
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
                        Toast.makeText(mContext,"获取数据失败",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                        Toast.makeText(mContext,"收藏成功",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                        Toast.makeText(mContext,"收藏失败",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                        Toast.makeText(mContext,"您之前已经收藏过该商品了!",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);
        mContext = ShopDetailsActivity.this;
        APP.getInstance().addActivity(ShopDetailsActivity.this);

        sharedPreferencesHelper = new SharedPreferencesHelper(
                ShopDetailsActivity.this, "config");
        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xxid = getIntent().getStringExtra("xxid");
        System.out.println("xxid---------"+xxid);
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }


        initView();
        initData();

//        UmengTool.checkWx(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shopcar = (String) sharedPreferencesHelper.getSharedPreference("shopcar", "");
        shopcar_arr = shopcar.split("-");
        shopcarnum = (String) sharedPreferencesHelper.getSharedPreference("shopcarnum", "");
        shopcarnum_arr = shopcarnum.split("-");
    }

    public void initView() {

        bt_shop_details_share = (Button) findViewById(R.id.bt_shop_details_share);
        bt_shop_details_share.setOnClickListener(this);
        bt_shop_details_back = (Button) findViewById(R.id.bt_shop_details_back);
        bt_shop_details_back.setOnClickListener(this);

        iv_shop_details_cplxtp = (ImageView) findViewById(R.id.iv_shop_details_cplxtp);

        tv_shop_details_jiage = (TextView) findViewById(R.id.tv_shop_details_jiage);
        tv_shop_details_kucui = (TextView) findViewById(R.id.tv_shop_details_kucui);
        tv_shop_details_title = (TextView) findViewById(R.id.tv_shop_details_title);
        tv_shop_details_fahuodi = (TextView) findViewById(R.id.tv_shop_details_fahuodi);

        tv_shop_details_qq = (TextView) findViewById(R.id.tv_shop_details_qq);
        tv_shop_details_qq.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMenu(tv_shop_details_qq);
                return true;
            }
        });
        tv_shop_details_weixin = (TextView) findViewById(R.id.tv_shop_details_weixin);
        tv_shop_details_weixin.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMenu(tv_shop_details_weixin);
                return true;
            }
        });
        tv_shop_details_zyts = (TextView) findViewById(R.id.tv_shop_details_zyts);

        tv_shop_details_cpxqnr = (TextView) findViewById(R.id.tv_shop_details_cpxqnr);

        sp_rl_sd_splx = (Spinner) findViewById(R.id.sp_rl_sd_splx);
        splxList = new ArrayList<>();
        splxAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,splxList);
        sp_rl_sd_splx.setAdapter(splxAdapter);
        sp_rl_sd_splx.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String xid = cplxidList.get(position);
                if(!xxid.equals(xid)){
                    showDialog("");
                    xxid = xid;
                    getCommodityDetails();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        recycler_shop_deatails = (RecyclerView) findViewById(R.id.recycler_shop_deatails);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recycler_shop_deatails.setLayoutManager(layoutManager);
        //绑定适配器
        recyclerViewNoBgAdapter = new RecyclerViewNoBgAdapter(this, list_url);
        recycler_shop_deatails.setAdapter(recyclerViewNoBgAdapter);

        rl_sd_spcs = (RelativeLayout)findViewById(R.id.rl_sd_spcs);
        rl_sd_spcs.setOnClickListener(this);

        tv_shop_details_ckgd = (TextView) findViewById(R.id.tv_shop_details_ckgd);
        tv_shop_details_ckgd.setOnClickListener(this);

        ll_shop_details_qqkf = (LinearLayout) findViewById(R.id.ll_shop_details_qqkf);
        ll_shop_details_qqkf.setOnClickListener(this);
        ll_shop_details_shoucang = (LinearLayout) findViewById(R.id.ll_shop_details_shoucang);
        ll_shop_details_shoucang.setOnClickListener(this);

        bt_shop_details_jrgwc = (Button) findViewById(R.id.bt_shop_details_jrgwc);
        bt_shop_details_jrgwc.setOnClickListener(this);
        bt_shop_details_ljgm = (Button) findViewById(R.id.bt_shop_details_ljgm);
        bt_shop_details_ljgm.setOnClickListener(this);

        tv_shop_details_pjsl = (TextView) findViewById(R.id.tv_shop_details_pjsl);
        tv_shop_details_pjry = (TextView) findViewById(R.id.tv_shop_details_pjry);
        tv_shop_details_pjnr = (TextView) findViewById(R.id.tv_shop_details_pjnr);
        tv_shop_details_pjsj = (TextView) findViewById(R.id.tv_shop_details_pjsj);
    }

    public void initData() {
        showDialog("");
        getCommodityDetails();
    }

    public void setData(){

        Glide.with(mContext).load(HttpUtils.URL+"/"+shopDetailsBean.getCplxtp()).into(iv_shop_details_cplxtp);

        tv_shop_details_jiage.setText("¥ "+shopDetailsBean.getSpjg());
        tv_shop_details_kucui.setText(shopDetailsBean.getSpkc());
        tv_shop_details_title.setText(shopDetailsBean.getCplxmr());
        tv_shop_details_fahuodi.setText(shopDetailsBean.getFhd());

        tv_shop_details_qq.setText(shopDetailsBean.getQqkf());
        tv_shop_details_weixin.setText(shopDetailsBean.getWxh());
        String zyts = mContext.getResources().getString(R.string.shop_details_zyts);
        tv_shop_details_zyts.setText(zyts+shopDetailsBean.getZyts());

        tv_shop_details_cpxqnr.setText(shopDetailsBean.getCpxqnr());

        list_url.clear();
        List<String> list_url_temp = shopDetailsBean.getCpxqtp();
        if(list_url_temp != null){
            for(String tp : list_url_temp){
                list_url.add(HttpUtils.URL+"/"+tp);
            }
        }
        recyclerViewNoBgAdapter.notifyDataSetChanged();

        list_spcs.clear();
        List canshu = shopDetailsBean.getXq();
        if(canshu != null){
            list_spcs.addAll(canshu);
        }

        cplxidList.clear();
        List cplxidList_temp = shopDetailsBean.getCplxid();
        if(cplxidList_temp != null){
            cplxidList.addAll(cplxidList_temp);
        }

        splxList.clear();
        List splx_temp = shopDetailsBean.getCplx();
        if(splx_temp != null){
            splxList.addAll(splx_temp);
        }
        for (int i=0;i<splxList.size();i++){
            if(splxList.get(i).equals(shopDetailsBean.getCplxmr())){
                sp_rl_sd_splx.setSelection(i);
            }
        }
        splxAdapter.notifyDataSetChanged();

        String shuliang = shopDetailsBean.getPjsl();
        if(shopDetailsBean == null || "".equals(shopDetailsBean)){
            tv_shop_details_pjsl.setText("宝贝评价(0)");
        }else {
            tv_shop_details_pjsl.setText("宝贝评价("+shuliang+")");
        }

        tv_shop_details_pjry.setText(shopDetailsBean.getPjry());

        String pjnr = shopDetailsBean.getPjnr();
        if(pjnr == null || "".equals(pjnr)){
            tv_shop_details_pjnr.setText("没有信息!");
        }else {
            tv_shop_details_pjnr.setText(pjnr);
        }
        tv_shop_details_pjsj.setText(shopDetailsBean.getPjsj());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_shop_details_share:
                getPermission();
                break;
            case R.id.bt_shop_details_back:
                finish();
                break;
            case R.id.ll_shop_details_shoucang:
                if(isLogin()){
                    shoucang();
                }else{
                    Utils.getLoginDialog(mContext);
                }

                break;
            case R.id.rl_sd_spcs:
                backgroundAlpha(0.5f);
                showPopwindow();
                break;

            case R.id.tv_shop_details_ckgd:
                Intent intent = new Intent();
                intent.putExtra("xxid",xxid);
                intent.setClass(ShopDetailsActivity.this,ShopEvaluateActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_shop_details_qqkf:
                String qq = shopDetailsBean.getQqkf();
                if(qq == null || "".equals(qq)){
                    Toast.makeText(mContext,"暂无QQ客服",Toast.LENGTH_SHORT).show();
                    return;
                }
                // 跳转之前，可以先判断手机是否安装QQ
                if (Utils.isQQClientAvailable(this)) {
                    // 跳转到客服的QQ
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin="+qq;
                    Intent intent_qq = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    // 跳转前先判断Uri是否存在，如果打开一个不存在的Uri，App可能会崩溃
                    if (Utils.isValidIntent(mContext,intent_qq)) {
                        startActivity(intent_qq);
                    }
                }else{
                    Toast.makeText(mContext,"手机没有安装QQ",Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.bt_shop_details_jrgwc:

                if(isLogin()){
                    for(String id:shopcar_arr){
                        if(xxid.equals(id)){
                            Toast.makeText(mContext,"该商品已经在购物车了",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    shopcar_arr = Arrays.copyOf(shopcar_arr, shopcar_arr.length+1);
                    shopcar_arr[shopcar_arr.length-1] = xxid;
                    shopcarnum_arr = Arrays.copyOf(shopcarnum_arr, shopcarnum_arr.length+1);
                    shopcarnum_arr[shopcarnum_arr.length-1] = "1";

                    shopcar = shopcar + "-" + xxid;
                    shopcarnum = shopcarnum + "-" + "1";
                    if(shopcarnum.startsWith("-")){
                        shopcarnum =  shopcarnum.substring(1,shopcarnum.length());
                    }
                    if(shopcar.startsWith("-")){
                        shopcar =  shopcar.substring(1,shopcar.length());
                    }
                    sharedPreferencesHelper.put("shopcarnum",shopcarnum);
                    sharedPreferencesHelper.put("shopcar",shopcar);
                    Toast.makeText(mContext,"已经成功加入购物车了，请继续购物!",Toast.LENGTH_SHORT).show();
                }else{
                    Utils.getLoginDialog(mContext);
                }

                    break;
            case R.id.bt_shop_details_ljgm:

                if(isLogin()){
                    for(String id:shopcar_arr){
                        if(xxid.equals(id)){
                            Intent intent_ljgm = new Intent();
                            intent_ljgm.setClass(ShopDetailsActivity.this,ShopCarActivity.class);
                            startActivity(intent_ljgm);
                            return;
                        }
                    }
                    shopcar_arr = Arrays.copyOf(shopcar_arr, shopcar_arr.length+1);
                    shopcar_arr[shopcar_arr.length-1] = xxid;
                    shopcarnum_arr = Arrays.copyOf(shopcarnum_arr, shopcarnum_arr.length+1);
                    shopcarnum_arr[shopcarnum_arr.length-1] = "1";

                    shopcar = shopcar + "-" + xxid;
                    shopcarnum = shopcarnum + "-" + "1";
                    if(shopcarnum.startsWith("-")){
                        shopcarnum =  shopcarnum.substring(1,shopcarnum.length());
                    }
                    if(shopcar.startsWith("-")){
                        shopcar =  shopcar.substring(1,shopcar.length());
                    }
                    sharedPreferencesHelper.put("shopcarnum",shopcarnum);
                    sharedPreferencesHelper.put("shopcar",shopcar);

                    Intent intent_ljgm = new Intent();
                    intent_ljgm.setClass(ShopDetailsActivity.this,ShopCarActivity.class);
                    startActivity(intent_ljgm);
                }else{
                    Utils.getLoginDialog(mContext);
                }

                break;
            default:
                break;
        }
    }


    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    public void showPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_shop_details_canshu, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.popWindow);
        ll.setFocusableInTouchMode(true);
        ll.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU || keyCode == KeyEvent.KEYCODE_BACK) {
                    popWindow.dismiss();
                }
                return false;
            }
        });

        ListView lv_pop_sd_cs = (ListView) view.findViewById(R.id.lv_pop_sd_cs);
        ShopParamAdapter adapter_spcs = new ShopParamAdapter(ShopDetailsActivity.this,list_spcs);
        lv_pop_sd_cs.setAdapter(adapter_spcs);
        TextView tv_pp_shop_details_sure = (TextView) view.findViewById(R.id.tv_pp_shop_details_sure);
        tv_pp_shop_details_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });

        popWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popWindow.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0xb0808080);
        popWindow.setBackgroundDrawable(dw);

        popWindow.setAnimationStyle(R.style.popuwindow_shop_canshu);
        popWindow.showAtLocation(ShopDetailsActivity.this.findViewById(R.id.bt_shop_details_back), Gravity.BOTTOM, 0, 0);


        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    //获取数据
    public void getCommodityDetails(){

        RequestBody requestBody = new FormBody.Builder()
                .add("id",xxid).add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getCommodityDetails.asp").post(requestBody).build();
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
                    Log.d("getCommodityDetails","getCommodityDetails=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<ShopDetailsBean> result = gson.fromJson(content, new TypeToken<Entity<ShopDetailsBean>>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            shopDetailsBean = result.getData();

                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }

                    }
                }
            }
        });
    }

    //收藏
    public void shoucang(){

        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userid).add("sessionID",sessionID).add("shopid",xxid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/shoucang.asp").post(requestBody).build();
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
                    Log.d("shoucang","shoucang=="+content);
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        int code = result.getCode();
                        if(code == 0){
                            handler.sendEmptyMessage(SHOUCANG_DATA_OK);
                        }else if(code == 1){
                            handler.sendEmptyMessage(SHOUCANG_DATA_CZ);
                        }else{
                            handler.sendEmptyMessage(SHOUCANG_DATA_ERROR);
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

    public boolean  isLogin(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        if(!"".equals(sessionID) && !"".equals(userid)){
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    public void getPermission(){
        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,Manifest.permission.WRITE_APN_SETTINGS};
            ActivityCompat.requestPermissions(this,mPermissionList,123);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode != 123){
            Toast.makeText(this, "请给与权限", Toast.LENGTH_SHORT).show();
        }else{
            String imageurl = HttpUtils.URL+"/"+shopDetailsBean.getCplxtp();
            String content = "【"+shopDetailsBean.getCplxmr()+"】"+"http://zhengxinw.com/m/shop/xinxixiangxi.asp?id="+xxid+" 点击链接打开；";
            UMImage image = new UMImage(ShopDetailsActivity.this, imageurl);//网络图片

//            new ShareAction(ShopDetailsActivity.this).withText(content)
//                    .withMedia(image)
//                    .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
//                    .setCallback(this)
//                    .setShareContent()
//                    .open();
            UMWeb web = new UMWeb("http://zhengxinw.com/m/shop/xinxixiangxi.asp?id="+xxid);
            web.setTitle(content);//标题
            web.setThumb(image);  //缩略图
            web.setDescription(content);//描述
            ShareContent shareContent = new ShareContent();
            shareContent.mText = content;
            new ShareAction(ShopDetailsActivity.this)
                    .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withMedia(web)
                    .setCallback(shareListener)//回调监听器
                    .open();
        }
    }

    public void showMenu(final TextView textView) {
        //可以按照需求随意添加 中心显示的Dialog
        CenterMenuDialog centerMenuDialog = new CenterMenuDialog(ShopDetailsActivity.this);
        //第一个选择条目
        Menu copyMenu = new Menu.Builder().setCaption("复制").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setPrimaryClip(ClipData.newPlainText("content", textView.getText()));
                Toast.makeText(ShopDetailsActivity.this, "内容已经复制", Toast.LENGTH_SHORT).show();
            }
        }).build();
        centerMenuDialog.addMenu(copyMenu);

        //第二个选择条目
        Menu themeMenu = new Menu.Builder().setCaption("取消").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                Toast.makeText(ShopDetailsActivity.this, "已经取消", Toast.LENGTH_SHORT).show();
            }
        }).build();
        centerMenuDialog.addMenu(themeMenu);

        //显示Dialog
        centerMenuDialog.show();
    }


    private UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {

        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(ShopDetailsActivity.this,"分享成功",Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            System.out.println("getMessage---"+t.getMessage());
            Toast.makeText(ShopDetailsActivity.this,"分享失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(ShopDetailsActivity.this,"分享取消",Toast.LENGTH_LONG).show();

        }
    };
}


