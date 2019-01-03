package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.ShopCarBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;

import java.util.List;

public class ShopOrderAdapter extends BaseAdapter  {
    private List<ShopCarBean> list = null;
    private Context mContext;
    private SharedPreferencesHelper sharedPreferencesHelper;

    public ShopOrderAdapter(Context mContext, List<ShopCarBean> list, SharedPreferencesHelper sharedPreferencesHelper) {
        this.mContext = mContext;
        this.list = list;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ShopCarBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final ShopCarBean shopCarBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_order, null);
            viewHolder.iv_item_shop_order_url = (ImageView) view.findViewById(R.id.iv_item_shop_order_url);
            viewHolder.tv_item_shop_order_title = (TextView) view.findViewById(R.id.tv_item_shop_order_title);
            viewHolder.tv_item_shop_order_price = (TextView) view.findViewById(R.id.tv_item_shop_order_price);
            viewHolder.tv_item_shop_order_yunfei = (TextView) view.findViewById(R.id.tv_item_shop_order_yunfei);
            viewHolder.tv_item_shop_order_shuliang = (TextView) view.findViewById(R.id.tv_item_shop_order_shuliang);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        new GlideImageLoader().displayImage(mContext, HttpUtils.URL+"/"+this.list.get(position).getShopUrl(),viewHolder.iv_item_shop_order_url);

        viewHolder.tv_item_shop_order_title.setText(this.list.get(position).getShopTile());
        viewHolder.tv_item_shop_order_price.setText("¥"+this.list.get(position).getShopPrice());
        viewHolder.tv_item_shop_order_yunfei.setText("¥"+this.list.get(position).getFreight());
        viewHolder.tv_item_shop_order_shuliang.setText("×"+this.list.get(position).getShopNum());

        return view;

    }


    final static class ViewHolder {
        ImageView iv_item_shop_order_url;
        TextView tv_item_shop_order_title;
        TextView tv_item_shop_order_price;
        TextView tv_item_shop_order_yunfei;
        TextView tv_item_shop_order_shuliang;
    }

}