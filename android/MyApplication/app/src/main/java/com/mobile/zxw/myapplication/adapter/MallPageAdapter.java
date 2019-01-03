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
import com.mobile.zxw.myapplication.activity.ShopDetailsActivity;
import com.mobile.zxw.myapplication.bean.MallBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

public class MallPageAdapter extends BaseAdapter {
    protected Context context;
    protected LayoutInflater inflater;
    protected List<MallBean> list;
//    private SharedPreferencesHelper sharedPreferencesHelper;
//    private String sessionID;
//    private String userid;

    public MallPageAdapter(Context context, List<MallBean> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<>();
        } else {
            this.list = list;
        }
//        sharedPreferencesHelper = new SharedPreferencesHelper(
//                context, "config");
//        sessionID = (String) sharedPreferencesHelper.getSharedPreference("sessionID", "");
//        userid = (String) sharedPreferencesHelper.getSharedPreference("userid", "");
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

            vh.ll_mall_page_right = (LinearLayout) convertView.findViewById(R.id.ll_mall_page_right);
            vh.mall_page_item_pic_right = (ImageView) convertView.findViewById(R.id.mall_page_item_pic_right);
            vh.mall_page_item_title_right = (TextView) convertView.findViewById(R.id.mall_page_item_title_right);
            vh.mall_page_item_price_right = (TextView) convertView.findViewById(R.id.mall_page_item_price_right);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        int distance = list.size() - position * 2;
        int cellCount = distance >= 2 ? 2 : distance;
        final List<MallBean> itemList = list.subList(position * 2, position * 2 + cellCount);
        if (itemList.size() > 0) {
//            new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +(itemList.get(0).getShopPicUrl()),vh.mall_page_item_pic);
            new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +itemList.get(0).getShopUrl(),vh.mall_page_item_pic);
            vh.mall_page_item_title.setText(itemList.get(0).getShopTile());
            vh.mall_page_item_price.setText(itemList.get(0).getShopPrice());
            vh.ll_mall_page_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ShopDetailsActivity.class);
                    intent.putExtra("xxid",itemList.get(0).getShopID());
                    context.startActivity(intent);
//                    Toast.makeText(context, itemList.get(0).getShopID(), Toast.LENGTH_SHORT).show();
                }
            });
            if (itemList.size() > 1) {
                vh.ll_mall_page_right.setVisibility(View.VISIBLE);
                new GlideImageLoader().displayImage(context, HttpUtils.URL +"/" +itemList.get(1).getShopUrl(),vh.mall_page_item_pic_right);
                vh.mall_page_item_title_right.setText(itemList.get(1).getShopTile());
                vh.mall_page_item_price_right.setText(itemList.get(1).getShopPrice());
                vh.ll_mall_page_right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,ShopDetailsActivity.class);
                        intent.putExtra("xxid",itemList.get(1).getShopID());
                        context.startActivity(intent);
//                        Toast.makeText(context, itemList.get(1).getShopID(), Toast.LENGTH_SHORT).show();
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

        LinearLayout ll_mall_page_right;
        ImageView mall_page_item_pic_right;
        TextView mall_page_item_title_right;
        TextView mall_page_item_price_right;
    }

}