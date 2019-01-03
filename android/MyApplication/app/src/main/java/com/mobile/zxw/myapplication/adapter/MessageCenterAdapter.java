package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.MessageCenterBean;

import java.util.List;

public class MessageCenterAdapter extends BaseAdapter  {
    private List<MessageCenterBean> list = null;
    private Context mContext;

    public MessageCenterAdapter(Context mContext, List<MessageCenterBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<MessageCenterBean> list) {
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
        final MessageCenterBean messageCenterBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_message_center, null);
            viewHolder.tv_message_title = (TextView) view.findViewById(R.id.tv_message_title);
            viewHolder.tv_message_time = (TextView) view.findViewById(R.id.tv_message_time);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_message_title.setText(messageCenterBean.getXxbt());
        viewHolder.tv_message_time.setText(messageCenterBean.getXxsj());

        return view;

    }


    final static class ViewHolder {
        TextView tv_message_title;
        TextView tv_message_time;
    }

}