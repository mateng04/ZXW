package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.GridInfo;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private List<GridInfo> mList = new ArrayList<GridInfo>();

    private Context mContext;

    public GridViewAdapter(Context context,List<GridInfo> list) {
        super();
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public GridInfo getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChildHolderOne holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_recruit_gv, parent, false);
            holder = new ChildHolderOne();
            holder.tvTitle = (TextView)convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolderOne) convertView.getTag();
        }
        final GridInfo gridInfo = mList.get(position);
        if(gridInfo.isSelect() == true){
            holder.tvTitle.setBackgroundColor(mContext.getResources().getColor(R.color.red));
        } else {
            holder.tvTitle.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }
        final String number = gridInfo.getTitle();
        holder.tvTitle.setText(number);


        return convertView;
    }

    class ChildHolderOne {
        TextView tvTitle;
    }

}
