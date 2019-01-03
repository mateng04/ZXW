package com.mobile.zxw.myapplication.ui.area;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mobile.zxw.myapplication.APP;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.until.StreamUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Description : 地址解析器
 *
 * @author WSoBan
 * @date 2018/05/03
 */
public class AreaParser {

    public static String provinceLevel = "1";

    private static AreaParser mInstance;
    private List<AreaBean> allList;
    private List<AreaBean> provinceList;    //这个不带全国
    private List<AreaBean> provinceList_qg;    //这个带全国
    private List<AreaBean> cityList;
    private List<AreaBean> regionList;
//    private List<AreaBean> allList;

    private AreaParser() {
//        String data = StreamUtils.get(APP.getInstance(), R.raw.area);
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<AreaBean>>() {
//        }.getType();
//        allList = new ArrayList<>();
//        allList = gson.fromJson(data, type);
//        provinceList = new ArrayList<>();
//        for (AreaBean areaBean : allList) {
//            if (provinceLevel.equals(areaBean.getLevel())) {
//                provinceList.add(areaBean);
//            }
//        }

        Gson gson = new Gson();
        String data_province = StreamUtils.get(APP.getInstance(), R.raw.area_province);
        Type type = new TypeToken<List<AreaBean>>() {
        }.getType();
        provinceList = new ArrayList<>();
        provinceList = gson.fromJson(data_province, type);

        String data_province_qg = StreamUtils.get(APP.getInstance(), R.raw.area_province2);
        Type type_qg = new TypeToken<List<AreaBean>>() {
        }.getType();
        provinceList_qg = new ArrayList<>();
        provinceList_qg = gson.fromJson(data_province_qg, type_qg);

        String data_city = StreamUtils.get(APP.getInstance(), R.raw.area_city);
        cityList = new ArrayList<>();
        cityList = gson.fromJson(data_city, type);

        String data_region= StreamUtils.get(APP.getInstance(), R.raw.area_region);
        regionList = new ArrayList<>();
        regionList = gson.fromJson(data_region, type);

        String data_all= StreamUtils.get(APP.getInstance(), R.raw.area_all);
        allList = new ArrayList<>();
        allList = gson.fromJson(data_all, type);

        String data_all2= StreamUtils.get(APP.getInstance(), R.raw.area_all2);
        List allList2 = gson.fromJson(data_all2, type);

        allList.addAll(allList2);

    }

    public static synchronized AreaParser getInstance() {
        if (mInstance == null) {
            mInstance = new AreaParser();
        }
        return mInstance;
    }

    public List<AreaBean> getProvinceList() {
        if (provinceList == null) {
            return new ArrayList<>();
        }
        return provinceList;
    }

    public List<AreaBean> getProvinceList2() {
        if (provinceList_qg == null) {
            return new ArrayList<>();
        }
        return provinceList_qg;
    }

    public List<AreaBean> getCityList(int tid) {
        String id = String.valueOf(tid);
        List<AreaBean> childList = new ArrayList<>();
        if (TextUtils.isEmpty(id)) {
            return childList;
        }
        for (AreaBean areaBean : cityList) {
            if (id.equals(areaBean.getPid())) {
                System.out.println("getName-22---"+areaBean.getName());
                System.out.println("getName-22---"+areaBean.check.get());
                childList.add(areaBean);
            }
        }
        return childList;
    }

    public List<AreaBean> getRegionList(int tid) {
        String id = String.valueOf(tid);
        List<AreaBean> childList = new ArrayList<>();
        if (TextUtils.isEmpty(id)) {
            return childList;
        }
        for (AreaBean areaBean : regionList) {
            if (id.equals(areaBean.getPid())) {
                childList.add(areaBean);
            }
        }
        return childList;
    }

    public List<AreaBean> getChildList(int tid) {

        System.out.println("--------------------"+allList.size());
        String id = String.valueOf(tid);
        List<AreaBean> childList = new ArrayList<>();
        if (TextUtils.isEmpty(id)) {
            return childList;
        }
        for (AreaBean areaBean : allList) {
            if (id.equals(areaBean.getPid())) {
                childList.add(areaBean);
            }
        }
        return childList;
    }

    public int getChoosePos(List<AreaBean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).check.get()) {
                return i;
            }
        }
        return 0;
    }
}
