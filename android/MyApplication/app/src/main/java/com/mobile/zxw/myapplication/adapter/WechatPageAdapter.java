package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.activity.WXShopDetailedActivity;
import com.mobile.zxw.myapplication.bean.WechatBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class WechatPageAdapter extends BaseAdapter {
    protected Context context;
    protected SharedPreferencesHelper sharedPreferencesHelper;
    protected LayoutInflater inflater;
    protected List<WechatBean> list;

    public WechatPageAdapter(Context context, List<WechatBean> list, SharedPreferencesHelper sharedPreferencesHelper) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    @Override
    public int getCount() {
        if (list.size() % 2 > 0) {
            return list.size() / 2 + 1;
        } else {
            return list.size() / 2;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mall_page, null);
            vh = new ViewHolder();
            vh.ll_mall_page_left = (LinearLayout) convertView.findViewById(R.id.ll_mall_page_left);
            vh.mall_page_item_pic = (ImageView) convertView.findViewById(R.id.mall_page_item_pic);
            vh.mall_page_item_title = (TextView) convertView.findViewById(R.id.mall_page_item_title);
            vh.mall_page_item_price = (TextView) convertView.findViewById(R.id.mall_page_item_price);
            vh.mall_page_item_fh = (TextView) convertView.findViewById(R.id.mall_page_item_fh);
            vh.mall_page_item_buy = (TextView) convertView.findViewById(R.id.mall_page_item_buy);

            vh.ll_mall_page_right = (LinearLayout) convertView.findViewById(R.id.ll_mall_page_right);
            vh.mall_page_item_pic_right = (ImageView) convertView.findViewById(R.id.mall_page_item_pic_right);
            vh.mall_page_item_title_right = (TextView) convertView.findViewById(R.id.mall_page_item_title_right);
            vh.mall_page_item_price_right = (TextView) convertView.findViewById(R.id.mall_page_item_price_right);
            vh.mall_page_item_fh_right = (TextView) convertView.findViewById(R.id.mall_page_item_fh_right);
            vh.mall_page_item_buy_right = (TextView) convertView.findViewById(R.id.mall_page_item_buy_right);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.mall_page_item_fh.setText("人气:");
        vh.mall_page_item_fh.setTextColor(context.getResources().getColor(R.color.black));
        vh.mall_page_item_buy.setText("查看详情");
        vh.mall_page_item_fh_right.setText("人气:");
        vh.mall_page_item_fh_right.setTextColor(context.getResources().getColor(R.color.black));
        vh.mall_page_item_buy_right.setText("查看详情");

        int distance = list.size() - position * 2;
        System.out.println("distance------"+distance);
        int cellCount = distance >= 2 ? 2 : distance;
        System.out.println("cellCount------"+cellCount);
        final List<WechatBean> itemList = list.subList(position * 2, position * 2 + cellCount);
        System.out.println("itemList.size()------"+itemList.size());
        if (itemList.size() > 0) {
//            new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +(itemList.get(0).getShopPicUrl()),vh.mall_page_item_pic);
            new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +itemList.get(0).getwShopUrl(),vh.mall_page_item_pic);
            vh.mall_page_item_title.setText(itemList.get(0).getwShopTile());
            vh.mall_page_item_price.setText(itemList.get(0).getwShopPopularity());
            vh.ll_mall_page_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, itemList.get(0).getwShopID(), Toast.LENGTH_SHORT).show();
//                    if(isLogin()){
//
//                    }else {
//                        Utils.getLoginDialog(context);
//                    }
                    String wsID = itemList.get(0).getwShopID();
                    Intent intent = new Intent(context, WXShopDetailedActivity.class);
                    intent.putExtra("ID",wsID);
                    context.startActivity(intent);

                }
            });
            if (itemList.size() > 1) {
                vh.ll_mall_page_right.setVisibility(View.VISIBLE);
                new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +itemList.get(1).getwShopUrl(),vh.mall_page_item_pic_right);
                vh.mall_page_item_title_right.setText(itemList.get(1).getwShopTile());
                vh.mall_page_item_price_right.setText(itemList.get(1).getwShopPopularity());
                vh.ll_mall_page_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(context, itemList.get(1).getwShopID(), Toast.LENGTH_SHORT).show();
//                        if(isLogin()){
//
//                        }else {
//                            Utils.getLoginDialog(context);
//                        }

                        String wsID = itemList.get(1).getwShopID();
                        Intent intent = new Intent(context, WXShopDetailedActivity.class);
                        intent.putExtra("ID",wsID);
                        context.startActivity(intent);

                    }
                });
            } else {
                vh.ll_mall_page_right.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    /**
     * 封装ListView中item控件以优化ListView
     *
     * @author tongleer
     */
    public static class ViewHolder {
        LinearLayout ll_mall_page_left;
        ImageView mall_page_item_pic;
        TextView mall_page_item_title;
        TextView mall_page_item_price;
        TextView mall_page_item_fh;
        TextView mall_page_item_buy;

        LinearLayout ll_mall_page_right;
        ImageView mall_page_item_pic_right;
        TextView mall_page_item_title_right;
        TextView mall_page_item_price_right;
        TextView mall_page_item_fh_right;
        TextView mall_page_item_buy_right;
    }
    public boolean  isLogin(){
        String sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
        String userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
        if(!"".equals(sessionID) && !"".equals(userid)){
            return true;
        }
        return false;
    }
}