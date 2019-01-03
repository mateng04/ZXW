package com.mobile.zxw.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

public class H5PayDemoActivity extends Activity {

	private WebView mWebView;



	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {      //判断标志位
				case 0:
					Toast.makeText(H5PayDemoActivity.this,"订单支付成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("result", 9000);
					H5PayDemoActivity.this.setResult(RESULT_OK, intent);
					finish();
					break;
				case 1:
					Toast.makeText(H5PayDemoActivity.this,"订单支付失败", Toast.LENGTH_LONG).show();
					Intent inten1t = new Intent();
					inten1t.putExtra("result", 4000);
					H5PayDemoActivity.this.setResult(RESULT_OK, inten1t);
					finish();
					break;
				case 2:
					Toast.makeText(H5PayDemoActivity.this,"订单支付失败，用户中途取消", Toast.LENGTH_LONG).show();
					Intent intent2 = new Intent();
					intent2.putExtra("result", 6001);
					H5PayDemoActivity.this.setResult(RESULT_OK, intent2);
					finish();
					break;
				case 3:
					Toast.makeText(H5PayDemoActivity.this,"订单支付失败，网络连接出错", Toast.LENGTH_LONG).show();
					Intent intent3 = new Intent();
					intent3.putExtra("result", 6002);
					H5PayDemoActivity.this.setResult(RESULT_OK, intent3);
					finish();
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = null;
		try {
			extras = getIntent().getExtras();
		} catch (Exception e) {
			finish();
			return;
		}
		if (extras == null) {
			finish();
			return;
		}
		String url = null;
		try {
			url = extras.getString("url");
		} catch (Exception e) {
			finish();
			return;
		}
		if (TextUtils.isEmpty(url)) {
			// 测试H5支付，必须设置要打开的url网站
			new AlertDialog.Builder(H5PayDemoActivity.this).setTitle("警告")
					.setMessage("打开的支付url网站异常")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							finish();
						}
					}).show();

		}
		super.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = new LinearLayout(getApplicationContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout, params);

		mWebView = new WebView(getApplicationContext());
		params.weight = 1;
		mWebView.setVisibility(View.VISIBLE);
		layout.addView(mWebView, params);

		WebSettings settings = mWebView.getSettings();
		settings.setRenderPriority(RenderPriority.HIGH);
		settings.setJavaScriptEnabled(true);
		settings.setSavePassword(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
		settings.setAllowFileAccess(false);
		settings.setTextSize(WebSettings.TextSize.NORMAL);
		mWebView.setVerticalScrollbarOverlay(true);
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.loadUrl(url);
	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			finish();
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, String url) {
			if (!(url.startsWith("http") || url.startsWith("https"))) {
				return true;
			}

			/**
			 * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
			 */
			final PayTask task = new PayTask(H5PayDemoActivity.this);
			boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
				@Override
				public void onPayResult(final H5PayResultModel result) {
					System.out.println("getReturnUrl----- "+ result.getReturnUrl());
					System.out.println("getResultCode----- "+ result.getResultCode());

//					返回码，标识支付状态，含义如下：
//					9000——订单支付成功
//					8000——正在处理中
//					4000——订单支付失败
//					5000——重复请求
//					6001——用户中途取消
//					6002——网络连接出错

					if("9000".equals(result.getResultCode())){
						handler.sendEmptyMessage(0);
//						return;
					}else if("8000".equals(result.getResultCode())){
//						handler.sendEmptyMessage(0);
//						return;
					}else if("4000".equals(result.getResultCode())){
						handler.sendEmptyMessage(1);
//						return;
					}else if("6001".equals(result.getResultCode())){
						handler.sendEmptyMessage(2);
//						return;
					}else if("6002".equals(result.getResultCode())){
						handler.sendEmptyMessage(3);
//						return;
					}
					final String url=result.getReturnUrl();
					if(!TextUtils.isEmpty(url)){
						H5PayDemoActivity.this.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								view.loadUrl(url);
							}
						});
					}
				}
			});

			/**
			 * 判断是否成功拦截
			 * 若成功拦截，则无需继续加载该URL；否则继续加载
			 */
			if(!isIntercepted)
				view.loadUrl(url);
			return true;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mWebView != null) {
			mWebView.removeAllViews();
			try {
				mWebView.destroy();
			} catch (Throwable t) {
			}
			mWebView = null;
		}
	}
}
