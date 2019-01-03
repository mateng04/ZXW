package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.ShopEvaluateBean;
import com.mobile.zxw.myapplication.ui.StarBar;

import java.util.List;

public class ShopEvaluateAdapter extends BaseAdapter  {
    private List<ShopEvaluateBean> list = null;
    private Context mContext;

    public ShopEvaluateAdapter(Context mContext, List<ShopEvaluateBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ShopEvaluateBean> list) {
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
        final ShopEvaluateBean shopEvaluateBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_evaluate, null);
            viewHolder.tv_it_shopeva_name = (TextView) view.findViewById(R.id.tv_it_shopeva_name);
            viewHolder.tv_it_shopeva_hy = (TextView) view.findViewById(R.id.tv_it_shopeva_hy);
            viewHolder.tv_it_shopeva_gmrq = (TextView) view.findViewById(R.id.tv_it_shopeva_gmrq);
            viewHolder.starBar_it_shopeva_msxf = (StarBar) view.findViewById(R.id.starBar_it_shopeva_msxf);
            viewHolder.starBar_it_shopeva_fwtd = (StarBar) view.findViewById(R.id.starBar_it_shopeva_fwtd);
            viewHolder.starBar_it_shopeva_wlfw = (StarBar) view.findViewById(R.id.starBar_it_shopeva_wlfw);
            viewHolder.tv_it_shopeva_pjnr = (TextView) view.findViewById(R.id.tv_it_shopeva_pjnr);
            viewHolder.tv_it_shopeva_pjrq = (TextView) view.findViewById(R.id.tv_it_shopeva_pjrq);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String name = shopEvaluateBean.getPjry();
        char[] names = name.toCharArray();
        for (int i = 1; i < names.length; i++){
            names[i] = '*';
        }
        name =  String.valueOf(names);

        if("0".equals(shopEvaluateBean.getHyjb())){
            viewHolder.tv_it_shopeva_hy.setText("普通");
        }else{
            viewHolder.tv_it_shopeva_hy.setText("VIP");
        }
        String time = shopEvaluateBean.getDatatime().split(" ")[0];
        viewHolder.tv_it_shopeva_gmrq.setText(time);
        viewHolder.tv_it_shopeva_name.setText(name);
        viewHolder.starBar_it_shopeva_msxf.setStarMark(Float.valueOf(shopEvaluateBean.getMsxf()));
        viewHolder.starBar_it_shopeva_fwtd.setStarMark(Float.valueOf(shopEvaluateBean.getFwtd()));
        viewHolder.starBar_it_shopeva_wlfw.setStarMark(Float.valueOf(shopEvaluateBean.getWlfw()));
        viewHolder.tv_it_shopeva_pjnr.setText(shopEvaluateBean.getContent());
        viewHolder.tv_it_shopeva_pjrq.setText(shopEvaluateBean.getDatatime());

        return view;

    }


    final static class ViewHolder {
        TextView tv_it_shopeva_name;
        TextView tv_it_shopeva_hy;
        TextView tv_it_shopeva_gmrq;
        StarBar starBar_it_shopeva_msxf;
        StarBar starBar_it_shopeva_fwtd;
        StarBar starBar_it_shopeva_wlfw;
        TextView tv_it_shopeva_pjnr;
        TextView tv_it_shopeva_pjrq;
    }

}