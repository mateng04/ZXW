package com.mobile.zxw.myapplication.ui.area;

import android.databinding.ObservableBoolean;
import android.support.v4.content.ContextCompat;

import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;

import java.io.Serializable;

/**
 * Description :
 *
 * @author WSoBan
 * @date 2018/05/03
 */
public class AreaBean implements Serializable {

    public ObservableBoolean check = new ObservableBoolean(false);
    /**
     * ids : 5846,5847,9909
     * level : 3
     * level4Useing : N
     * name : 呈贡区
     * names : 云南省,昆明市,呈贡区
     * notvalid :
     * pid : 5847
     * tid : 9909
     */

    private String ids;
    private String level;
    private String level4Useing;
    private String name;
    private String names;
    private String notvalid;
    private String pid;
    private int tid;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel4Useing() {
        return level4Useing;
    }

    public void setLevel4Useing(String level4Useing) {
        this.level4Useing = level4Useing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getNotvalid() {
        return notvalid;
    }

    public void setNotvalid(String notvalid) {
        this.notvalid = notvalid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getTextColor() {
        if (check.get()) {
            return ContextCompat.getColor(APP.getInstance(), R.color.colorPrimary);
        }
        return ContextCompat.getColor(APP.getInstance(), R.color.black);
    }
}
