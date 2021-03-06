package com.mobile.zxw.myapplication.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.RecyclerViewNoBgAdapter;
import com.mobile.zxw.myapplication.bean.WSShopDetailedBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.myinterface.MenuCommand;
import com.mobile.zxw.myapplication.ui.CenterMenuDialog;
import com.mobile.zxw.myapplication.ui.Menu;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.ShareContent;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class WXShopDetailedActivity extends AppCompatActivity implements View.OnClickListener,UMShareListener {

    Button wxshop_back;
    TextView tv_wxshop_js, tv_wxshop_deatails,iv_wxshop_wxh;
    ImageView iv_wxshop_zs, iv_wxshop_ewm;
    RecyclerView recycler_wxshop_deatails;

    RecyclerViewNoBgAdapter recyclerViewNoBgAdapter;
    List<String> list_url = new ArrayList<>();

    private Context mContext;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    //加载对话框
    private Dialog mLoadingDialog;

    String xinxiID;
    String sessionID;
    String userid;

    //分享
    Button bt_wshop_details_share;


    WSShopDetailedBean wSShopDetailedBean;
    static int DATA_OK = 0; //获取数据成功
    static int DATA_ERROR = 1; //获取数据是失败
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
                    cancelDialog();
                    Toast.makeText(mContext,"获取数据失败，请重新加载",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    cancelDialog();
                    Toast.makeText(mContext,"图片已保存至ZXW文件夹",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wxshop_details);
        mContext = this;

        if(okHttpClient == null){
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                WXShopDetailedActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        xinxiID = getIntent().getStringExtra("ID");

        initView();
        initData();
    }

    public void initView(){

        bt_wshop_details_share = (Button)findViewById(R.id.bt_wshop_details_share);
        bt_wshop_details_share.setOnClickListener(this);

        wxshop_back = (Button)findViewById(R.id.wxshop_back);
        wxshop_back.setOnClickListener(this);

        tv_wxshop_js = (TextView)findViewById(R.id.tv_wxshop_js);
        tv_wxshop_deatails = (TextView)findViewById(R.id.tv_wxshop_deatails);
        iv_wxshop_wxh = (TextView)findViewById(R.id.iv_wxshop_wxh);
        iv_wxshop_wxh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showMenu(iv_wxshop_wxh);
                return true;
            }
        });

        iv_wxshop_zs = (ImageView)findViewById(R.id.iv_wxshop_zs);
        iv_wxshop_ewm = (ImageView)findViewById(R.id.iv_wxshop_ewm);
        iv_wxshop_ewm.setOnClickListener(this);
        iv_wxshop_ewm.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEWMMenu(iv_wxshop_ewm);
                return true;
            }
        });
        recycler_wxshop_deatails = (RecyclerView)findViewById(R.id.recycler_wxshop_deatails);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        recycler_wxshop_deatails.setLayoutManager(layoutManager);
        //绑定适配器
        recyclerViewNoBgAdapter = new RecyclerViewNoBgAdapter(this, list_url);
        recycler_wxshop_deatails.setAdapter(recyclerViewNoBgAdapter);
    }

    public void initData(){
        showDialog("正在加载数据");
        getWSDetails();
    }

    private void setData(){
        tv_wxshop_js.setText(wSShopDetailedBean.getTitle());
        tv_wxshop_deatails.setText(wSShopDetailedBean.getContent());
        iv_wxshop_wxh.setText(wSShopDetailedBean.getWeixin());
        Glide.with(mContext).load(HttpUtils.URL+"/"+wSShopDetailedBean.getImg()).into(iv_wxshop_zs);
        Glide.with(mContext).load(HttpUtils.URL+"/"+wSShopDetailedBean.getErweima()).into(iv_wxshop_ewm);

        list_url.clear();
        List<String> list = wSShopDetailedBean.getTupian();
        if(list != null){
            for(int i=0;i<list.size();i++){
                list_url.add(HttpUtils.URL+"/"+list.get(i));
            }
        }
        recyclerViewNoBgAdapter.notifyDataSetChanged();
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



    //获取详情数据
    public void getWSDetails(){

        RequestBody requestBody = new FormBody.Builder().add("id",xinxiID)
                .add("sessionID",sessionID).add("userid",userid).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/login/getWSDetails.asp").post(requestBody).build();
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

                    Log.d("getQZJobDetails","response.code()=="+response.code());
                    Log.d("getQZJobDetails","response.message()=="+response.message());
                    Log.d("getQZJobDetails","content=="+content);

                    if(response.code() == 200){
                        Gson gson = new Gson();
                        Entity<WSShopDetailedBean> result = gson.fromJson(content, new TypeToken<Entity<WSShopDetailedBean>>() {}.getType());
                        if(result.getCode() == 0){
                            wSShopDetailedBean = result.getData();
                            handler.sendEmptyMessage(DATA_OK);
                        }else{
                            handler.sendEmptyMessage(DATA_ERROR);
                        }

                    }else {
                        handler.sendEmptyMessage(DATA_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wxshop_back:
               finish();
                break;
            case R.id.bt_wshop_details_share:
                getPermission();
                break;
            case R.id. iv_wxshop_ewm:
                Intent intent = new Intent(mContext, ShowImageActivity.class);
                intent.putExtra("imgUrl",wSShopDetailedBean.getErweima());
                startActivity(intent);
                break;
            default:{
                break;
            }
        }
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
            String imageurl = HttpUtils.URL+"/"+wSShopDetailedBean.getImg();
            String content = "【"+wSShopDetailedBean.getTitle()+"】"+"http://zhengxinw.com/m/shop/weishangxiangxi.asp?id="+xinxiID+" 点击链接打开；";
            UMImage image = new UMImage(WXShopDetailedActivity.this, imageurl);//网络图片

//            new ShareAction(ShopDetailsActivity.this).withText(content)
//                    .withMedia(image)
//                    .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
//                    .setCallback(this)
//                    .setShareContent()
//                    .open();
            UMWeb web = new UMWeb("http://zhengxinw.com/m/shop/weishangxiangxi.asp?id="+xinxiID);
            web.setTitle(content);//标题
            web.setThumb(image);  //缩略图
            web.setDescription(content);//描述
            ShareContent shareContent = new ShareContent();
            shareContent.mText = content;
            new ShareAction(WXShopDetailedActivity.this)
                    .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.QZONE,SHARE_MEDIA.WEIXIN,SHARE_MEDIA.WEIXIN_CIRCLE)
                    .setShareContent(shareContent)
                    .withMedia(web)
                    .setCallback(this)//回调监听器
                    .open();
        }
    }

    @Override
    public void onStart(SHARE_MEDIA platform) {
    }
    /**
     * @descrption 分享成功的回调
     * @param platform 平台类型
     */
    @Override
    public void onResult(SHARE_MEDIA platform) {
        Toast.makeText(WXShopDetailedActivity.this,"分享成功",Toast.LENGTH_LONG).show();
    }
    /**
     * @descrption 分享失败的回调
     * @param platform 平台类型
     * @param t 错误原因
     */
    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        Toast.makeText(WXShopDetailedActivity.this,"分享失败"+t.getMessage(),Toast.LENGTH_LONG).show();
    }
    /**
     * @descrption 分享取消的回调
     * @param platform 平台类型
     */
    @Override
    public void onCancel(SHARE_MEDIA platform) {
        Toast.makeText(WXShopDetailedActivity.this,"分享取消",Toast.LENGTH_LONG).show();
    }

    public void showMenu(final TextView textView) {
        //可以按照需求随意添加 中心显示的Dialog
        CenterMenuDialog centerMenuDialog = new CenterMenuDialog(WXShopDetailedActivity.this);
        //第一个选择条目
        Menu copyMenu = new Menu.Builder().setCaption("复制").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                myClipboard.setPrimaryClip(ClipData.newPlainText("content", textView.getText()));
                Toast.makeText(WXShopDetailedActivity.this, "内容已经复制", Toast.LENGTH_SHORT).show();
            }
        }).build();
        centerMenuDialog.addMenu(copyMenu);

        //第二个选择条目
        Menu themeMenu = new Menu.Builder().setCaption("取消").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                Toast.makeText(WXShopDetailedActivity.this, "已经取消", Toast.LENGTH_SHORT).show();
            }
        }).build();
        centerMenuDialog.addMenu(themeMenu);

        //显示Dialog
        centerMenuDialog.show();
    }


    public void showEWMMenu(final ImageView imageView) {
        //可以按照需求随意添加 中心显示的Dialog
        CenterMenuDialog centerMenuDialog = new CenterMenuDialog(WXShopDetailedActivity.this);
        //第一个选择条目
        Menu copyMenu = new Menu.Builder().setCaption("查看图片").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                Intent intent = new Intent(mContext, ShowImageActivity.class);
                intent.putExtra("imgUrl",wSShopDetailedBean.getErweima());
                startActivity(intent);
            }
        }).build();
        centerMenuDialog.addMenu(copyMenu);

        //第二个选择条目
        Menu themeMenu = new Menu.Builder().setCaption("保存到相册").setMenuCommand(new MenuCommand() {
            @Override
            public void onClick() {
                new getImageCacheAsyncTask(mContext).execute(HttpUtils.URL+"/"+wSShopDetailedBean.getErweima());
            }
        }).build();
        centerMenuDialog.addMenu(themeMenu);

        //显示Dialog
        centerMenuDialog.show();
    }

    public class getImageCacheAsyncTask extends AsyncTask<String, Void, File> {
        private final Context context;

        public getImageCacheAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected File doInBackground(String... params) {
            String imgUrl =  params[0];
            try {
                return Glide.with(context)
                        .load(imgUrl)
                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();
            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            if (result == null) {
                return;
            }
            //这里得到的就是我们要的文件了，接下来是保存文件。
            // 首先保存图片
            File appDir = new File(Environment.getExternalStorageDirectory(), "ZXW");
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = "zxw_"+System.currentTimeMillis() + ".jpg";
            File file = new File(appDir, fileName);
            //最后一步就是复制文件咯
            copy(result,file);

            // 其次把文件插入到系统图库
            String path = file.getAbsolutePath();
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), path, fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);

            handler.sendEmptyMessage(2);
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public void copy(File source, File target){
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fileInputStream != null){
                    fileInputStream.close();
                }
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
