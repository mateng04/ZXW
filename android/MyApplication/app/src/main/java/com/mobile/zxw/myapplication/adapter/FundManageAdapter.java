package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.FundManageBean;

import java.util.List;

public class FundManageAdapter extends BaseAdapter  {
    private List<FundManageBean> list = null;
    private Context mContext;

    public FundManageAdapter(Context mContext, List<FundManageBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<FundManageBean> list) {
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
        final FundManageBean fundManageBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_fund_manage, null);
            viewHolder.tv_recharge_time = (TextView) view.findViewById(R.id.tv_fund_manage_time);
            viewHolder.tv_recharge_money = (TextView) view.findViewById(R.id.tv_fund_manage_money);
            viewHolder.tv_recharge_source = (TextView) view.findViewById(R.id.tv_fund_manage_content);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_recharge_time.setText(fundManageBean.getFundTime());
        viewHolder.tv_recharge_money.setText(fundManageBean.getFundMoney());
        viewHolder.tv_recharge_source.setText(fundManageBean.getFundContent());

        return view;

    }


    final static class ViewHolder {
        TextView tv_recharge_time;
        TextView tv_recharge_money;
        TextView tv_recharge_source;
    }

}