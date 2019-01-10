package com.mobile.zxw.myapplication.ui;

import com.mobile.zxw.myapplication.myinterface.MenuCommand;

public class Menu {
    public int resourceId;
    public String caption;
    public MenuCommand menuCommand;

    public static class Builder {
        int resourceId;
        String caption;
        MenuCommand menuCommand;
        public Builder setResourceId(int resourceId){
            this.resourceId=resourceId;
            return this;
        }
        public Builder setCaption(String caption){
            this.caption=caption;
            return this;
        }
        public Builder setMenuCommand(MenuCommand menuCommand){
            this.menuCommand=menuCommand;
            return this;
        }
        public Menu build(){
            Menu menu=new Menu();
            menu.resourceId=resourceId;
            menu.menuCommand=menuCommand;
            menu.caption=caption;
            return menu;
        }
    }
}