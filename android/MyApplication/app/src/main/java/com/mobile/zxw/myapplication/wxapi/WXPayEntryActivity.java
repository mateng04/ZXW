package com.mobile.zxw.myapplication.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.until.ListenerManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXPayEntryActivity";


    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, APP.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq baseReq) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            System.out.println("onPayFinish,errCode="+baseResp.errCode);
            //名称  描述 解决方案
            //0 成功 展示成功页面
            //-1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
            //-2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。

            switch(baseResp.errCode){
                case 0:
                    ListenerManager.getInstance().sendBroadCast(100,"支付成功");
                    break;
                case -1:
                    ListenerManager.getInstance().sendBroadCast(101,"支付错误");
                    break;
                case -2:
                    ListenerManager.getInstance().sendBroadCast(102,"用户取消");
                    break;
                default:

                    break;
            }

            this.finish();

        }

    }
}


