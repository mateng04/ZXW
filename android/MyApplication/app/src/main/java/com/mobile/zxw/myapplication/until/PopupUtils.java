package com.mobile.zxw.myapplication.until;

import com.mobile.zxw.myapplication.bean.PopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mobile.zxw.myapplication.until.StaticUtils.DOUBLE_POP;
import static com.mobile.zxw.myapplication.until.StaticUtils.ONLY_ONE_POP;

/**
 * 文件名：PopupHelpep.java
 * 类型：工具类
 * 作用：提供区分菜单类型与转换数据的帮助方法
 * Created by Chitose on 2018/4/30.
 */

public class PopupUtils {

    public PopupUtils() {}

    public static int popSort(List<PopItem> itemList){

        boolean doubleFlag = false;

        for (PopItem item: itemList) {
            //是否只有一级菜单栏
            if(item.getPid()==0){

            }else if(item.getPid()!=0){
                doubleFlag = true;
            }
        }
        //二级菜单栏
        if(doubleFlag){
            return DOUBLE_POP;
        }
        //一级菜单栏
        else{
            return ONLY_ONE_POP;
        }

    }

/*    public static int popSort(List<List<PopItem>> popList) {

        boolean doubleFlag = false;

        for(int i = 1; i<popList.size();i++){
            if(popList.get(i).size()!=0){
                for(PopItem item: popList.get(i)){
                    if(popList.get(item.getId()).size()!=0){
                        return TRIPLE_POP;
                    }
                    else{
                        doubleFlag = true;
                    }
                }
            }
        }
        //二级菜单栏
        if(doubleFlag){
            return DOUBLE_POP;
        }
        //一级菜单栏
        else{
            return ONLY_ONE_POP;
        }
    }*/

    public static void popListInit(Map<Integer, List<PopItem>> allMap, List<PopItem> itemList){

        //先获取所有 父类0
        List<PopItem> popList = new ArrayList<>();
        for(PopItem popItem:itemList){
            if(popItem.getPid() == 0 ){
                popList.add(popItem);
            }
        }
        allMap.put(0,popList);

        for(PopItem popItem:popList){
            List<PopItem> list = new ArrayList<>();
            for(PopItem item:itemList){
                if(item.getPid() == popItem.getId() ){
                    list.add(item);
                }
            }
            allMap.put( popItem.getId() ,list);
        }
    }

}
