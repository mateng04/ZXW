package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;

import java.util.List;

public class ShopParamAdapter extends BaseAdapter  {
    private List<String> list = null;
    private Context mContext;

    public ShopParamAdapter(Context mContext, List<String> list) {
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
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_param, null);
            viewHolder.tv_item_shop_param_key = (TextView) view.findViewById(R.id.tv_item_shop_param_key);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_item_shop_param_key.setText(content);
        return view;

    }

    final static class ViewHolder {
        TextView tv_item_shop_param_key;
        TextView tv_item_shop_param_value;
    }

}