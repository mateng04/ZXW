package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.OrderZZ;

import java.util.List;

public class OrderZZAdapter extends BaseAdapter  {
    private List<OrderZZ> list = null;
    private Context mContext;

    public OrderZZAdapter(Context mContext, List<OrderZZ> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<OrderZZ> list) {
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
        final OrderZZ orderZZ = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_select_order_ddzz, null);
            viewHolder.item_order_ddzz_clsj = (TextView) view.findViewById(R.id.item_order_ddzz_clsj);
            viewHolder.item_order_ddzz_clxx = (TextView) view.findViewById(R.id.item_order_ddzz_clxx);
            viewHolder.item_order_ddzz_czr = (TextView) view.findViewById(R.id.item_order_ddzz_czr);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.item_order_ddzz_clsj.setText(orderZZ.getDatetime());
        viewHolder.item_order_ddzz_clxx.setText(orderZZ.getChulixinxi());
        viewHolder.item_order_ddzz_czr.setText(orderZZ.getChuliren());

        return view;

    }

    final static class ViewHolder {
        TextView item_order_ddzz_clsj;
        TextView item_order_ddzz_clxx;
        TextView item_order_ddzz_czr;
    }

}