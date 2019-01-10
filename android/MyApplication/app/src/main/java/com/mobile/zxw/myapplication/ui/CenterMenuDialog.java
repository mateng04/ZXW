package com.mobile.zxw.myapplication.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class CenterMenuDialog {
    AlertDialog.Builder builder;

    List<Menu> menuList=new ArrayList<>();

    public void addMenu(Menu menu) {
        menuList.add(menu);
    }
    public CenterMenuDialog(Context context){
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
    }

    public void show(){
        String arr[]=new String[menuList.size()];
        for (int i = 0; i < menuList.size(); i++) {
            arr[i]=menuList.get(i).caption;
        }
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Menu menu=menuList.get(i);
                if(menu.menuCommand!=null){
                    menu.menuCommand.onClick();
                }
            }
        });
        builder.create().show();
    }
}