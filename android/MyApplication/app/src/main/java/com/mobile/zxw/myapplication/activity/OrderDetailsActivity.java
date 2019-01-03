package com.mobile.zxw.myapplication.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.OrderWLNRAdapter;
import com.mobile.zxw.myapplication.adapter.OrderZZAdapter;
import com.mobile.zxw.myapplication.bean.OrderArr;
import com.mobile.zxw.myapplication.bean.OrderSHXX;
import com.mobile.zxw.myapplication.bean.OrderSPXX;
import com.mobile.zxw.myapplication.bean.OrderWLXQ;
import com.mobile.zxw.myapplication.bean.OrderZT;
import com.mobile.zxw.myapplication.bean.OrderZZ;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.jsonEntity.Entity;
import com.mobile.zxw.myapplication.jsonEntity.EntityDD;
import com.mobile.zxw.myapplication.jsonEntity.EntityWXorder;
import com.mobile.zxw.myapplication.myinterface.IListener;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.ui.NestedListView;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;
import com.parkingwang.okhttp3.LogInterceptor.LogInterceptor;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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

public class OrderDetailsActivity extends AppCompatActivity implements View.OnClickListener,IListener {

    private Context mContext;
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI wxapi;
    private final static int REQUESTCODE = 1; // 返回的结果码

    //加载对话框
    private Dialog mLoadingDialog;
    //物流对话框
    private Dialog mDialog;
    private List<String> wuliuName = new ArrayList<>();
    private List<String> wuliuDaiMa = new ArrayList<>();

    private String dingdanhao;
    private String laiyuan;

    String sessionID;
    String userid;
    OkHttpClient okHttpClient;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private OrderZT jbxx;
    private OrderSPXX spxq;
    private OrderSHXX shxx;
    private List<OrderZZ> ddgz = new ArrayList<>();
    private List<OrderZZ> ddgz_temp;
    private OrderWLXQ wlxq;

    NestedScrollView nestedscrollview_order_details;

    Button order_details_back;
    TextView tv_order_de_ddshuoming;
    TextView tv_order_details_ddbh, tv_order_details_ddzt, tv_order_de_shrvalue, tv_order_de_sjhmvalue,
            tv_order_de_dzvalue;
    ImageView iv_order_de_shopurl;
    TextView tv_order_de_shoptitle, tv_order_de_shopprice, tv_order_de_shopnumber, tv_order_de_shopyunfei,
            tv_order_de_shopzongjia;
    NestedListView listview_ddzz;
    OrderZZAdapter adapter_ddzz;

    LinearLayout bt_order_details_wl;
    TextView bt_order_details_wlmc, bt_order_details_wldh;
    NestedListView listview_order_details_wlnr;
    OrderWLNRAdapter adapter_wlnr;
    private List<String> wlnr = new ArrayList<>();

    Button bt_order_details_fk;
    LinearLayout ll_order_details_thh;
    Button bt_order_details_th,bt_order_details_hh;

    private String state1;
    private String stateContent;
    private String stateShuoMing = "";
    private String jiage;
    private String shuliang;
    private String yunfei;
    private String totalPrice;

    Spinner sp_wuliu;
    EditText et_wuliu_danhao;
    ArrayAdapter<String> wuliuAdapter;
    Button bt_wuliu_confirm,bt_wuliu_cancel;

    String wuliumingcheng,wuliudanhao;

    static int DATA_OK = 0;
    static int DATA_ERROR = 1;
    static int FBWL_OK = 2;
    static int FBWL_ERROR = 3;
    static int QRSH_OK = 4;
    static int QRSH_ERROR = 5;
    static int THCG_OK = 6;
    static int HHCG_OK = 7;
    static int TYTH_OK = 8;
    static int YWMJHH_OK = 9;
    static int MJYSDTH_OK = 10;
    static int THH_ERROR = 11;
    static int QDHH_OK = 12;
    static int MJQRYTH_OK = 13;
    static int MJDJTHWC_OK = 14;

    static int CZ_DATA_OK = 102;      //微信支付发起成功
    static int CZ_DATA_ERROR = 103;   //微信支付发起失败
    static int ZF_DATA_OK = 104;      //微信支付成功
    static int ZF_DATA_ERROR = 105;   //微信支付失败

    private Handler handler = new Handler() {
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
                    Toast.makeText(mContext,"获取订单信息失败",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext,"提交物流信息成功",Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    dingdanxiangqing();
                    break;
                case 3:
                    Toast.makeText(mContext,"提交物流信息失败，请重新提交",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
//                    Toast.makeText(mContext,"提交物流信息失败，请重新提交",Toast.LENGTH_SHORT).show();
                    dingdanxiangqing();
                    break;
                case 5:
//                    Toast.makeText(mContext,"提交物流信息失败，请重新提交",Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(mContext,"退货申请成功，卖家会与您取得联系!",Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    Toast.makeText(mContext,"换货申请成功，卖家会与您取得联系!",Toast.LENGTH_SHORT).show();
                    break;
                case 8:
                    Toast.makeText(mContext,"您已经处理完 同意退货 申请!",Toast.LENGTH_SHORT).show();
                    break;
                case 9:
                    Toast.makeText(mContext,"您已经处理完 已为买家换货 申请!",Toast.LENGTH_SHORT).show();
                    break;
                case 10:
                    Toast.makeText(mContext,"您已经处理完 卖家已收到退货 申请!",Toast.LENGTH_SHORT).show();
                    break;
                case 11:
                    Toast.makeText(mContext,"您的账户余额不足完成退货处理!",Toast.LENGTH_SHORT).show();
                    break;
                case 12:
                    Toast.makeText(mContext,"买家确定换货成功!",Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(mContext,"买家确认已退货!",Toast.LENGTH_SHORT).show();
                    break;
                case 14:
                    Toast.makeText(mContext,"买家点击退货完成!",Toast.LENGTH_SHORT).show();
                    break;
                case 9000:
                    dingdanxiangqing();
                    break;
                case 4000:

                    break;
                case 104:
                    dingdanxiangqing();
                    break;
                case 105:

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mContext = this;

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        wxapi = WXAPIFactory.createWXAPI(this, APP.APP_ID, false);

        dingdanhao = getIntent().getStringExtra("dingdanhao");
        laiyuan = getIntent().getStringExtra("laiyuan");

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor()).build();
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(
                OrderDetailsActivity.this, "config");

        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");

        System.out.println("userid--------------"+userid);
        //注册监听器
        ListenerManager.getInstance().registerListtener(this);
        initView();
        initData();
    }

    public void initView() {
        order_details_back = (Button) findViewById(R.id.order_details_back);
        order_details_back.setOnClickListener(this);

        nestedscrollview_order_details = (NestedScrollView) findViewById(R.id.nestedscrollview_order_details);

        tv_order_details_ddbh = (TextView) findViewById(R.id.tv_order_details_ddbh);
        tv_order_details_ddzt = (TextView) findViewById(R.id.tv_order_details_ddzt);
        tv_order_de_ddshuoming = (TextView) findViewById(R.id.tv_order_de_ddshuoming);

        tv_order_de_shrvalue = (TextView) findViewById(R.id.tv_order_de_shrvalue);
        tv_order_de_sjhmvalue = (TextView) findViewById(R.id.tv_order_de_sjhmvalue);
        tv_order_de_dzvalue = (TextView) findViewById(R.id.tv_order_de_dzvalue);

        iv_order_de_shopurl = (ImageView) findViewById(R.id.iv_order_de_shopurl);
        tv_order_de_shoptitle = (TextView) findViewById(R.id.tv_order_de_shoptitle);
        tv_order_de_shopprice = (TextView) findViewById(R.id.tv_order_de_shopprice);
        tv_order_de_shopnumber = (TextView) findViewById(R.id.tv_order_de_shopnumber);
        tv_order_de_shopyunfei = (TextView) findViewById(R.id.tv_order_de_shopyunfei);
        tv_order_de_shopzongjia = (TextView) findViewById(R.id.tv_order_de_shopzongjia);

        listview_ddzz = (NestedListView) findViewById(R.id.listview_ddzz);
        adapter_ddzz = new OrderZZAdapter(mContext, ddgz);
        listview_ddzz.setAdapter(adapter_ddzz);

        bt_order_details_wl = (LinearLayout) findViewById(R.id.bt_order_details_wl);
        bt_order_details_wlmc = (TextView) findViewById(R.id.bt_order_details_wlmc);
        bt_order_details_wldh = (TextView) findViewById(R.id.bt_order_details_wldh);

        listview_order_details_wlnr = (NestedListView) findViewById(R.id.listview_order_details_wlnr);
        adapter_wlnr = new OrderWLNRAdapter(mContext, wlnr);
        listview_order_details_wlnr.setAdapter(adapter_wlnr);

        bt_order_details_fk = (Button) findViewById(R.id.bt_order_details_fk);
        bt_order_details_fk.setOnClickListener(this);

        ll_order_details_thh = (LinearLayout)findViewById(R.id.ll_order_details_thh);
        bt_order_details_th = (Button)findViewById(R.id.bt_order_details_th);
        bt_order_details_th.setOnClickListener(this);
        bt_order_details_hh = (Button)findViewById(R.id.bt_order_details_hh);
        bt_order_details_hh.setOnClickListener(this);
    }

    public void initData() {
//        showDialog("数据加载中");
//        dingdanxiangqing();

        setWuLiu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDialog("数据加载中");
        dingdanxiangqing();
    }

    public void setData() {
        tv_order_details_ddbh.setText(dingdanhao);
        setState();
        tv_order_de_shrvalue.setText(shxx.getShouhrxm());
        tv_order_de_sjhmvalue.setText(shxx.getShouji());
        tv_order_de_dzvalue.setText(shxx.getShouhdz());


        jiage = spxq.getJiage();
        shuliang = spxq.getShuliang();
        yunfei = spxq.getYunfei();
        totalPrice = ((Integer.valueOf(jiage) + Integer.valueOf(yunfei)) * Integer.valueOf(shuliang)) + "";
        System.out.println("jiage-" + jiage + " yunfei-" + yunfei + " shuliang-" + shuliang);
        System.out.println("totalPrice-" + totalPrice);

        new GlideImageLoader().displayImage(mContext, HttpUtils.URL + "/" + spxq.getImg(), iv_order_de_shopurl);
        tv_order_de_shoptitle.setText(spxq.getTitle());
        tv_order_de_shopprice.setText("¥" + jiage);
        tv_order_de_shopnumber.setText("×" + shuliang);
        tv_order_de_shopyunfei.setText("¥" + yunfei);
        tv_order_de_shopzongjia.setText("¥" + totalPrice);

        ddgz.clear();
        ddgz.addAll(ddgz_temp);
        adapter_ddzz.notifyDataSetChanged();
//        setHeight(listview_ddzz,adapter_ddzz);

        String wlmc = wlxq.getWuliugngsiname();
        String wldh = wlxq.getWuliuhao();

        if ("".equals(wlmc) && "".equals(wldh)) {
            bt_order_details_wl.setVisibility(View.GONE);
        } else {
            bt_order_details_wl.setVisibility(View.VISIBLE);
            bt_order_details_wlmc.setText(wlmc);
            bt_order_details_wldh.setText(wldh);
            if (wlxq.getContent() != null && wlxq.getContent().size() > 0) {
                wlnr.clear();
                wlnr.addAll(wlxq.getContent());
                adapter_wlnr.notifyDataSetChanged();
//                setHeight(listview_order_details_wlnr,adapter_wlnr);
            }

        }

    }

    public void setState() {
        state1 = jbxx.getState();
        if ("0".equals(state1)) {
            stateContent = "等待付款";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "尊敬的客户，您还没有支付该订单的款项，请您尽快付款。您可以选择微信或支付宝付款";
                bt_order_details_fk.setText("付款");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "该订单买家还没有付款";
                bt_order_details_fk.setVisibility(View.GONE);
            }

        } else if ("1".equals(state1)) {
            stateContent = "无效订单";
        } else if ("2".equals(state1)) {
            stateContent = "已付款";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "尊敬的客户，您已经支付该订单费用，现在等待卖家发货";
                bt_order_details_fk.setVisibility(View.GONE);
            } else {
                stateShuoMing = "该订单买家已经支付费用，请您尽快发货";
                bt_order_details_fk.setText("发货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            }
        } else if ("3".equals(state1)) {
            stateContent = "已发货";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "尊敬的客户，该订单卖家已发货，请您收到货物后点击确认收货";
                bt_order_details_fk.setText("确认收货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "该订单您已经发货";
                bt_order_details_fk.setText("修改物流");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            }

        } else if ("4".equals(state1)) {
            stateContent = "已完成";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "订单已经完成，感谢您在正信商城购物。如果货物有质量问题您还可以选择换货或者退货";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "订单已经完成";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }
        } else if ("5".equals(state1)) {
            stateContent = "换货中";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "订单商城正在换货中，卖家会与您联系";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "买家提出了换货申请，请您与买家取得联系。如果已为买家处理完毕请选择已为买家换货";
                bt_order_details_fk.setText("已为买家换货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("6".equals(state1)) {
            stateContent = "退货中";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "订单商城正在退货中，卖家会与您联系";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "买家提出了退货申请，请您与买家取得联系。然后选择同意退货";
                bt_order_details_fk.setText("同意退货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("7".equals(state1)) {
            stateContent = "卖家已为买家换货";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "卖家已处理您的换货申请，确认无误请点击确定换货成功";
                bt_order_details_fk.setText("确定换货成功");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "已为买家换货，等待买家确认";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }
        } else if ("8".equals(state1)) {
            stateContent = "卖家同意退货";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "卖家同意退货，如果您已经退货请选择买家确认已退货";
                bt_order_details_fk.setText("确认已退货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "卖家已同意退货，等待买家退货";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("9".equals(state1)) {
            stateContent = "买家确定换货成功";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "买家确定换货成功";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "买家确定换货成功";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("10".equals(state1)) {
            stateContent = "买家确认已退货";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "买家确认已退货,等待卖家确认已收到退货";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "买家确认已退货,如果收到退货请选择卖家已收到退货";
                bt_order_details_fk.setText("已收到退货");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            }

        } else if ("11".equals(state1)) {
            stateContent = "卖家已收到退货";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "卖家已收到退货，请选择买家点击退货完成";
                bt_order_details_fk.setText("退货完成");
                bt_order_details_fk.setVisibility(View.VISIBLE);
            } else {
                stateShuoMing = "卖家已收到退货,等待买家确认退货完成";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("12".equals(state1)) {
            stateContent = "买家点击退货完成";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "买家点击退货完成，等待管理员审批";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "买家点击退货完成，等待管理员审批。";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }

        } else if ("13".equals(state1)) {
            stateContent = "买家收到货款";
            if ("buy".equals(laiyuan)) {
                stateShuoMing = "买家收到货款";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            } else {
                stateShuoMing = "卖家收到货款";
                bt_order_details_fk.setVisibility(View.GONE);
                ll_order_details_thh.setVisibility(View.GONE);
            }
        }
        tv_order_details_ddzt.setText(stateContent);
        tv_order_de_ddshuoming.setText(stateShuoMing);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_details_back:
                finish();
                break;
            case R.id.bt_order_details_fk:
                if ("0".equals(state1)) {
                    if ("buy".equals(laiyuan)) {
                        dialogList();
                    }
                } else if ("2".equals(state1)) {
                    if (!"buy".equals(laiyuan)) {
                        //发布物流
                        showWLDialog(v,0);
                    }
                }else if ("3".equals(state1)) {
                    if (!"buy".equals(laiyuan)) {
                        wuliumingcheng = wlxq.getWuliugngsiname();
                        wuliudanhao = wlxq.getWuliuhao();
                        //修改物流
                        showWLDialog(v,1);
                    }else{
                        AlertDialog.Builder builder  = new AlertDialog.Builder(mContext);
                        builder.setTitle("提示" ) ;
                        builder.setMessage("点击确认收货即证明您已经收到货物，卖家将收到货款" ) ;
                        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                showDialog("");
                                querenshouhuo();
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    }
                }else if("5".equals(state1)){
                    showDialog("");
                    tuihuanhuo("已为买家换货");
                }else if("6".equals(state1)){
                    showDialog("");
                    tuihuanhuo("同意退货");
                }else if("7".equals(state1)){
                    showDialog("");
                    quedingtuihuanhuo("换货");
                }else if("8".equals(state1)){
                    showDialog("");
                    quedingtuihuanhuo("买家确认已退货");
                }else if("10".equals(state1)){
                    showDialog("");
                    tuihuanhuo("卖家已收到退货");
                }else if("11".equals(state1)){
                    showDialog("");
                    quedingtuihuanhuo("买家点击退货完成");
                }
                break;
            case R.id.bt_order_details_th:
                shouhou("退货");
                break;
            case R.id.bt_order_details_hh:
                shouhou("换货");
                break;
            default:
                break;
        }
    }

    /**
     * 列表
     */
    private void dialogList() {
        final String items[] = {"微信", "支付宝"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付选择");
        // builder.setMessage("是否确认退出?"); //设置内容
//        builder.setIcon(R.mipmap.ic_launcher);
        // 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                Toast.makeText(OrderDetailsActivity.this, items[which],
//                        Toast.LENGTH_SHORT).show();
                if("微信".equals(items[which])){
                    wx();
                }else if("支付宝".equals(items[which])){
                    zfb();
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void wx(){
        RequestBody requestBody = new FormBody.Builder().add("user_ID",userid).add("dingdanhao",dingdanhao).build();
        Request request = new Request.Builder().url(HttpUtils.URL+"/appServic/m/wechat_dingdan/alipayapi.asp").post(requestBody).build();
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
                    Log.d("获取与支付订单","content=="+content);
                    Log.d("response.code()",response.code()+"");
                    if(response.code() == 200){
                        Gson gson = new Gson();
                        EntityWXorder result = gson.fromJson(content, new TypeToken<EntityWXorder>() {}.getType());
                        Log.d("result.",result.toString()+"");
                        Log.d("result.getSuccess()",result.getSuccess()+"");
                        if(result.getSuccess() == 1){
                            OrderArr orderArr = result.getOrder_arr();
                            PayReq req = new PayReq();
                            req.appId = orderArr.getAppid();
                            req.partnerId = orderArr.getPartnerid();
                            req.prepayId = orderArr.getPrepayid();
                            req.packageValue = "Sign=WXPay";
                            req.nonceStr = orderArr.getNoncestr();
                            req.timeStamp = orderArr.getTimestamp();
                            req.extData = result.getData();
                            req.sign = orderArr.getSign();
                            boolean rest = wxapi.sendReq(req);
//                            Toast.makeText(OnlineRechargeActivity.this, "调起支付结果:" +rest, Toast.LENGTH_LONG).show();
                            Log.d("调起支付结果","-"+rest);
                            handler.sendEmptyMessage(CZ_DATA_OK);
                        }else{
                            handler.sendEmptyMessage(CZ_DATA_ERROR);
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

    public void zfb(){
        Intent intent = new Intent(this, H5PayDemoActivity.class);
        Bundle extras = new Bundle();
        String url = "http://zhengxinw.com/appServic/m/alipay_shop/alipayapi.asp?chengshiid=&quxianid=&xiangzhenid=&user_ID=" + userid + "&dingdanhao=" + dingdanhao;
        extras.putString("url", url);
        intent.putExtras(extras);
        startActivityForResult(intent,REQUESTCODE);
    }

    //获取订单信息
    public void dingdanxiangqing() {
        RequestBody requestBody = new FormBody.Builder()
                .add("sessionID", sessionID).add("userid", userid)
                .add("dingdanhao", dingdanhao).build();
        Request request = new Request.Builder().url(HttpUtils.URL + "/appServic/user/dingdanxiangqing.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("dingdanxiangqing", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        EntityDD result = gson.fromJson(content, new TypeToken<EntityDD>() {
                        }.getType());
                        if (result.getCode() == 0) {
                            jbxx = result.getJbxx();
                            spxq = result.getSpxq();
                            shxx = result.getShxx();
                            ddgz_temp = result.getDdgz();
                            wlxq = result.getWlxq();
                            handler.sendEmptyMessage(DATA_OK);
                        } else {
                            handler.sendEmptyMessage(DATA_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(DATA_ERROR);
                    }
                }
            }
        });
    }

    private void showDialog(String content) {
        View view = LayoutInflater.from(this).inflate(R.layout.loading, null);
        TextView loadingText = (TextView) view.findViewById(R.id.tv_load_text);
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
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
        }
    }

    public void setHeight(ListView listview, BaseAdapter adapter) {
        int height = 0;
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View temp = adapter.getView(i, null, listview);
            temp.measure(0, 0);
            height += temp.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listview.getLayoutParams();
        params.width = ViewGroup.LayoutParams.FILL_PARENT;
        params.height = height;
    }

    public void showWLDialog(View view,int type) {
        //1.创建一个Dialog对象，如果是AlertDialog对象的话，弹出的自定义布局四周会有一些阴影，效果不好
        mDialog = new Dialog(this);
        //去除标题栏
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        mDialog.setTitle("物流信息");
        //2.填充布局
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_wuliu, null);
        //将自定义布局设置进去
        mDialog.setContentView(dialogView);
        //3.设置指定的宽高,如果不设置的话，弹出的对话框可能不会显示全整个布局，当然在布局中写死宽高也可以
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //注意要在Dialog show之后，再将宽高属性设置进去，才有效果
        mDialog.show();
        window.setAttributes(lp);

        //设置点击其它地方不让消失弹窗
//        mDialog.setCancelable(false);
        initDialogView(dialogView);
        initDialogListener(type);

    }
    private void initDialogView(View view) {
        bt_wuliu_confirm = (Button) view.findViewById(R.id.bt_wuliu_confirm);
        bt_wuliu_cancel = (Button) view.findViewById(R.id.bt_wuliu_cancel);
        et_wuliu_danhao = (EditText) view.findViewById(R.id.et_wuliu_danhao);
        et_wuliu_danhao.setText(wuliudanhao);
        sp_wuliu = (Spinner) view.findViewById(R.id.sp_wuliu);
        wuliuAdapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,wuliuName);
        sp_wuliu.setAdapter(wuliuAdapter);
        for(int i=0;i<wuliuName.size();i++){
            if(wuliuName.get(i).equals(wuliumingcheng)){
                sp_wuliu.setSelection(i);
            }
        }

    }

    private void initDialogListener(int type) {
        bt_wuliu_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mDialog.dismiss();
                int position = sp_wuliu.getSelectedItemPosition();
                String wuliu_danhao = et_wuliu_danhao.getText().toString().trim();
                if("".equals(wuliu_danhao)){
                    Toast.makeText(mContext,"请填写物流单号",Toast.LENGTH_SHORT).show();
                    return;
                }
                showDialog("");
                shangjiadingdanfahuo(type);
            }
        });

        bt_wuliu_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

//        sp_wuliu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }

    public void setWuLiu(){
        wuliuName.add("顺丰速运");
        wuliuName.add("EMS");
        wuliuName.add("申通快递");
        wuliuName.add("中通快递");
        wuliuName.add("圆通速递");
        wuliuName.add("韵达快运");
        wuliuName.add("百世快递");
        wuliuName.add("全峰快递");
        wuliuName.add("天天快递");
        wuliuName.add("宅急送");
        wuliuName.add("优速快递");
        wuliuName.add("德邦物流");
        wuliuName.add("全一快递");

        wuliuDaiMa.add("shunfeng");
        wuliuDaiMa.add("ems");
        wuliuDaiMa.add("shentong");
        wuliuDaiMa.add("zhongtong");
        wuliuDaiMa.add("yuantong");
        wuliuDaiMa.add("yunda");
        wuliuDaiMa.add("huitong");
        wuliuDaiMa.add("quanfeng");
        wuliuDaiMa.add("tiantian");
        wuliuDaiMa.add("zhaijisong");
        wuliuDaiMa.add("yousu");
        wuliuDaiMa.add("debang");
        wuliuDaiMa.add("quanyi");
    }

    public void shangjiadingdanfahuo(int type) {

        String ddid = jbxx.getDdid();
//        String wuliugongsiID = wuliuDaiMa.get(sp_wuliu.getSelectedItemPosition());
        String wuliugongsiID = (sp_wuliu.getSelectedItemPosition()+1)+"";
        String wuliudanhao = et_wuliu_danhao.getText().toString().trim();

        String url = "";
        if(type == 0){
            url = "/appServic/user/shangjiadingdanfahuo.asp";
        }else{
            url = "/appServic/user/shangjiadingdanfahuoxiugai.asp";
        }

        RequestBody requestBody = new FormBody.Builder().add("sessionID", sessionID).add("userid", userid)
                .add("wuliudanhao", wuliudanhao)
                .add("ddid", ddid).add("wuliugongsiID", wuliugongsiID).build();
        Request request = new Request.Builder().url(HttpUtils.URL + url).post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("shangjiadingdanfahuo", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if (result.getCode() == 0) {
                            handler.sendEmptyMessage(FBWL_OK);
                        } else {
                            handler.sendEmptyMessage(FBWL_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(FBWL_ERROR);
                    }
                }
            }
        });
    }

    public void querenshouhuo() {

        RequestBody requestBody = new FormBody.Builder().add("sessionID", sessionID).add("userid", userid)
                .add("dingdanhao", dingdanhao).build();
        Request request = new Request.Builder().url(HttpUtils.URL + "/appServic/user/querenshouhuo.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("querenshouhuo", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if (result.getCode() == 0) {
                            handler.sendEmptyMessage(QRSH_OK);
                        } else {
                            handler.sendEmptyMessage(QRSH_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(FBWL_ERROR);
                    }
                }
            }
        });
    }

    public void shouhou(String leixing) {

        String lx = "";
        try {
            lx =  URLEncoder.encode(leixing,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        RequestBody requestBody = new FormBody.Builder().add("sessionID", sessionID).add("userid", userid)
                .add("dingdanhao", dingdanhao).addEncoded("leixing", lx).build();
        Request request = new Request.Builder().url(HttpUtils.URL + "/appServic/user/shouhou.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("shouhou", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if (result.getCode() == 0) {
                            if("退货".equals(leixing)){
                                handler.sendEmptyMessage(THCG_OK);
                            }else{
                                handler.sendEmptyMessage(HHCG_OK);
                            }
                            handler.sendEmptyMessage(QRSH_OK);
                        } else {
                            handler.sendEmptyMessage(QRSH_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(QRSH_ERROR);
                    }
                }
            }
        });
    }

    public void tuihuanhuo(String leixing) {

        String ddid = jbxx.getDdid();
        String lx = "";
        try {
            lx =  URLEncoder.encode(leixing,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        RequestBody requestBody = new FormBody.Builder().add("sessionID", sessionID).add("userid", userid)
                .add("dingdanhao", dingdanhao).add("ddid", ddid)
                .addEncoded("leixing", lx).build();
        Request request = new Request.Builder().url(HttpUtils.URL + "/appServic/user/tuihuanhuo.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("shouhou", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if (result.getCode() == 0) {
                            if("同意退货".equals(leixing)){
                                handler.sendEmptyMessage(TYTH_OK);
                            }else if("已为买家换货".equals(leixing)){
                                handler.sendEmptyMessage(YWMJHH_OK);
                            }else if("卖家已收到退货".equals(leixing)){
                                handler.sendEmptyMessage(MJYSDTH_OK);
                            }
                            handler.sendEmptyMessage(QRSH_OK);
                        } else if(result.getCode() == 1){
                            handler.sendEmptyMessage(THH_ERROR);
                        } else {
                            handler.sendEmptyMessage(QRSH_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(QRSH_ERROR);
                    }
                }
            }
        });
    }

    public void quedingtuihuanhuo(String leixing) {

        String ddid = jbxx.getDdid();
        String lx = "";
        try {
            lx =  URLEncoder.encode(leixing,"GB2312");
        } catch (Exception e) {
        } finally {
        }
        RequestBody requestBody = new FormBody.Builder().add("sessionID", sessionID).add("userid", userid)
                .add("dingdanhao", dingdanhao).add("ddid", ddid)
                .addEncoded("leixing", lx).build();
        Request request = new Request.Builder().url(HttpUtils.URL + "/appServic/user/quedingtuihuanhuo.asp").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String content = response.body().string();
                    Log.d("quedingtuihuanhuo", "content==" + content);
                    if (response.code() == 200) {
                        Gson gson = new Gson();
                        Entity result = gson.fromJson(content, new TypeToken<Entity>() {}.getType());
                        if (result.getCode() == 0) {
                            if("换货".equals(leixing)){
                                handler.sendEmptyMessage(QDHH_OK);
                            }else if("买家确认已退货".equals(leixing)){
                                handler.sendEmptyMessage(MJQRYTH_OK);
                            }else if("买家点击退货完成".equals(leixing)){
                                handler.sendEmptyMessage(MJDJTHWC_OK);
                            }
                            handler.sendEmptyMessage(QRSH_OK);
                        } else {
                            handler.sendEmptyMessage(QRSH_ERROR);
                        }
                    } else {
                        handler.sendEmptyMessage(QRSH_ERROR);
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
    public void notifyAllActivity(int tag, String str) {
        if(tag == 100){
            Toast.makeText(mContext,"支付成功",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_OK);
        }else if(tag == 101){
            Toast.makeText(mContext,"支付错误",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_ERROR);
        }else if(tag == 102){
            Toast.makeText(mContext,"用户取消支付",Toast.LENGTH_SHORT).show();
            handler.sendEmptyMessage(ZF_DATA_ERROR);
        }
    }

    @Override
    protected void onDestroy() {
        ListenerManager.getInstance().unRegisterListener(this);
        super.onDestroy();
    }
}
