package com.mobile.zxw.myapplication.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LaiYingtang on 2016/5/22.
 * 列数据的bean
 */
public class MenuData implements Parcelable {
    public int id;
    public String name;
    public int flag;

    public MenuData(int id, String name, int flag) {
        this.id = id;
        this.name = name;
        this.flag = flag;
    }

    public MenuData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(flag);
    }

    // 反序列过程：必须实现Parcelable.Creator接口，并且对象名必须为CREATOR
    // 读取Parcel里面数据时必须按照成员变量声明的顺序，Parcel数据来源上面writeToParcel方法，读出来的数据供逻辑层使用
    public static final Parcelable.Creator<MenuData> CREATOR = new Creator<MenuData>() {

        @Override
        public MenuData createFromParcel(Parcel source) {
            return new MenuData(source.readInt(), source.readString(), source.readInt());
        }

        @Override
        public MenuData[] newArray(int size) {
            return new MenuData[size];
        }
    };
}
