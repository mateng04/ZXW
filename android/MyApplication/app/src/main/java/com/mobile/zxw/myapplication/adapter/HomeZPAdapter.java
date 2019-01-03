package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.HomeZPBean;
import com.mobile.zxw.myapplication.until.DayUtils;

import java.util.List;

public class HomeZPAdapter extends BaseAdapter  {
    private List<HomeZPBean> list = null;
    private Context mContext;

    public HomeZPAdapter(Context mContext, List<HomeZPBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<HomeZPBean> list) {
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
        final HomeZPBean homeZPBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_select_home_zp, null);
            viewHolder.tv_home_zp_zd = (TextView) view.findViewById(R.id.tv_home_zp_zd);
            viewHolder.tv_home_zp_title = (TextView) view.findViewById(R.id.tv_home_zp_title);
            viewHolder.tv_home_zp_time = (TextView) view.findViewById(R.id.tv_home_zp_time);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        if ("置顶".equals(homeZPBean.getHrQuanZhiZD())) {
            viewHolder.tv_home_zp_zd.setVisibility(View.VISIBLE);

        } else {
            viewHolder.tv_home_zp_zd.setVisibility(View.GONE);
        }

        viewHolder.tv_home_zp_title.setText(this.list.get(position).getHrQuanZhiTile());
        String timeTemp = (this.list.get(position).getHrQuanZhiTime());
//        System.out.println("timeTemp-----"+timeTemp);
        String time = timeTemp.substring(5,10);
//        System.out.println("time-----"+time);
//        .substring(5,9)

        String day = DayUtils.getTitleDay(timeTemp);
        System.out.println("day---------"+day);

        if("今天".equals(day)){
            viewHolder.tv_home_zp_time.setText("今天");
        }else if("昨天".equals(day)){
            viewHolder.tv_home_zp_time.setText("昨天");
        }else{
            viewHolder.tv_home_zp_time.setText(time);
        }



        return view;

    }


    final static class ViewHolder {
        TextView tv_home_zp_zd;
        TextView tv_home_zp_title;
        TextView tv_home_zp_time;
    }

}