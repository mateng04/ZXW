package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.OrderManageBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;

import java.util.List;

public class OrderManageAdapter extends BaseAdapter  {
    private List<OrderManageBean> list = null;
    private Context mContext;

    public OrderManageAdapter(Context mContext, List<OrderManageBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<OrderManageBean> list) {
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
        final OrderManageBean orderManageBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_order_manage, null);
            viewHolder.iv_item_ord_man_url = (ImageView) view.findViewById(R.id.iv_item_ord_man_url);
            viewHolder.tv_item_ord_man_ddh = (TextView) view.findViewById(R.id.tv_item_ord_man_ddh);
            viewHolder.tv_item_ord_man_title = (TextView) view.findViewById(R.id.tv_item_ord_man_title);
            viewHolder.tv_item_ord_man_fkm = (TextView) view.findViewById(R.id.tv_item_ord_man_fkm);
            viewHolder.tv_item_ord_mam_price = (TextView) view.findViewById(R.id.tv_item_ord_mam_price);
            viewHolder.tv_item_ord_mam_number = (TextView) view.findViewById(R.id.tv_item_ord_mam_number);
            viewHolder.tv_item_ord_man_ddsj = (TextView) view.findViewById(R.id.tv_item_ord_man_ddsj);

            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        new GlideImageLoader().displayImage(mContext, HttpUtils.URL+"/"+orderManageBean.getImg(),viewHolder.iv_item_ord_man_url);

        viewHolder.tv_item_ord_man_ddh.setText(orderManageBean.getDdh());
        viewHolder.tv_item_ord_man_title.setText(orderManageBean.getTitle());
        viewHolder.tv_item_ord_man_fkm.setText(orderManageBean.getState());
        viewHolder.tv_item_ord_mam_price.setText("¥"+orderManageBean.getJiage());
        viewHolder.tv_item_ord_mam_number.setText("×"+orderManageBean.getShuliang());
        viewHolder.tv_item_ord_man_ddsj.setText(orderManageBean.getDdsj());
        return view;

    }


    final static class ViewHolder {
        ImageView iv_item_ord_man_url;
        TextView tv_item_ord_man_ddh;
        TextView tv_item_ord_man_title;
        TextView tv_item_ord_man_fkm;
        TextView tv_item_ord_mam_price;
        TextView tv_item_ord_mam_number;
        TextView tv_item_ord_man_ddsj;
    }

}