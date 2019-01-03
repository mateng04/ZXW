package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.ConsumeDetailedBean;

import java.util.List;

public class ConsumeDetailedAdapter extends BaseAdapter  {
    private List<ConsumeDetailedBean> list = null;
    private Context mContext;

    public ConsumeDetailedAdapter(Context mContext, List<ConsumeDetailedBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ConsumeDetailedBean> list) {
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
        final ConsumeDetailedBean consumeDetailedBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_consume_detailed, null);
            viewHolder.tv_consume_time = (TextView) view.findViewById(R.id.tv_consume_time);
            viewHolder.tv_consume_leixing = (TextView) view.findViewById(R.id.tv_consume_leixing);
            viewHolder.tv_consume_money = (TextView) view.findViewById(R.id.tv_consume_money);
            viewHolder.tv_consume_content = (TextView) view.findViewById(R.id.tv_consume_content);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_consume_time.setText(consumeDetailedBean.getTime());
        viewHolder.tv_consume_leixing.setText(consumeDetailedBean.getLeixing());
        viewHolder.tv_consume_money.setText(consumeDetailedBean.getJine());
        viewHolder.tv_consume_content.setText(consumeDetailedBean.getBeizhu().replaceAll("<(.*?)>", ""));

        return view;

    }


    final static class ViewHolder {
        TextView tv_consume_time;
        TextView tv_consume_leixing;
        TextView tv_consume_money;
        TextView tv_consume_content;
    }

}