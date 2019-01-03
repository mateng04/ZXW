package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;

import java.util.List;

public class OrderWLNRAdapter extends BaseAdapter  {
    private List<String> list = null;
    private Context mContext;

    public OrderWLNRAdapter(Context mContext, List<String> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<String> list) {
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
        final String content = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_select_order_wlnr, null);
            viewHolder.item_order_wlnr = (TextView) view.findViewById(R.id.item_order_wlnr);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String text = content.replace("&nbsp;"," ");
        viewHolder.item_order_wlnr.setText(text);

        return view;

    }

    final static class ViewHolder {
        TextView item_order_wlnr;
    }

}