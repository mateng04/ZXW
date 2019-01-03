package com.mobile.zxw.myapplication;

import android.app.Activity;
import android.app.Application;

import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 *
 * @author WSoBan
 * @date 2018/05/03
 */
public class APP extends Application {

    private static APP sInstance;
    public static String APP_ID = "wx02b8866e85180d66";
    private List<Activity> oList;//用于存放所有启动的Activity的集合

    public APP() {
        super();
        sInstance = this;
    }

    public static APP getInstance() {
        return sInstance;
    }


    public void onCreate() {
        super.onCreate();
        oList = new ArrayList<Activity>();
        UMConfigure.init(this,"5bc1b4fef1f5563c2e000055"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");//58edcfeb310c93091c000be2 5965ee00734be40b580001a0
//        PlatformConfig.setWeixin("wx0cf37a3a762eec69", "bd2bc7cda803785f67f4356cf70a22ce");
        PlatformConfig.setWeixin("wx02b8866e85180d66", "f49ff74427883f3d0006aba2decd65bf");
//        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad","http://sns.whalecloud.com");
        PlatformConfig.setQQZone("101512944", "66a5b1b09d4b23ab6714890d5563fe4a");
    }


    /**
     * 添加Activity
     */
    public void addActivity(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }
}
