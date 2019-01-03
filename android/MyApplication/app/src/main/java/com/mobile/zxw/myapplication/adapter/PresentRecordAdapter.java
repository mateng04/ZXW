package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.PresentRecordBean;

import java.util.List;

public class PresentRecordAdapter extends BaseAdapter  {
    private List<PresentRecordBean> list = null;
    private Context mContext;

    public PresentRecordAdapter(Context mContext, List<PresentRecordBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<PresentRecordBean> list) {
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
        final PresentRecordBean presentRecordBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_present_record, null);
            viewHolder.tv_present_record_time = (TextView) view.findViewById(R.id.tv_present_record_time);
            viewHolder.tv_present_record_money = (TextView) view.findViewById(R.id.tv_present_record_money);
            viewHolder.tv_present_record_yhzh = (TextView) view.findViewById(R.id.tv_present_record_yhzh);
            viewHolder.tv_present_record_zt = (TextView) view.findViewById(R.id.tv_present_record_zt);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_present_record_time.setText(presentRecordBean.getDatatime());
        viewHolder.tv_present_record_money.setText(presentRecordBean.getJine());
        viewHolder.tv_present_record_yhzh.setText(presentRecordBean.getYinhang()+" "+ presentRecordBean.getZhanghao());
        String state = presentRecordBean.getState();
        if("0".equals(state)){
            viewHolder.tv_present_record_zt.setText("未审核");
        }else if("1".equals(state)){
            viewHolder.tv_present_record_zt.setText("未通过审核");
        }else if("2".equals(state)){
            viewHolder.tv_present_record_zt.setText("提现成功");
        }



        return view;

    }


    final static class ViewHolder {
        TextView tv_present_record_time;
        TextView tv_present_record_money;
        TextView tv_present_record_yhzh;
        TextView tv_present_record_zt;
    }

}