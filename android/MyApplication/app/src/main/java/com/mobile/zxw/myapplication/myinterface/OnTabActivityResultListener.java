package com.mobile.zxw.myapplication.myinterface;

import android.content.Intent;

/**
 * 发送广播接口
 * @author  cWX286335
 * @version  [版本号, 2015年6月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface OnTabActivityResultListener {
    public void onTabActivityResult(int requestCode, int resultCode, Intent data);
}