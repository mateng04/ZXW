package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.activity.AdvertisingManageActivity;
import com.mobile.zxw.myapplication.bean.AdverManageBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.area.AreaBean;

import java.util.List;

public class AdverManageAdapter extends BaseAdapter  {
    private List<AdverManageBean> list = null;
    private Context mContext;

    public AdverManageAdapter(Context mContext, List<AdverManageBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<AdverManageBean> list) {
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
        final AdverManageBean adverManageBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_adver_manage, null);
            viewHolder.tv_item_advermanage_url = (ImageView) view.findViewById(R.id.tv_item_advermanage_url);
            viewHolder.tv_item_advermanage_sspd = (TextView) view.findViewById(R.id.tv_item_advermanage_sspd);
            viewHolder.tv_item_advermanage_qy = (TextView) view.findViewById(R.id.tv_item_advermanage_qy);
            viewHolder.tv_item_advermanage_dqsj = (TextView) view.findViewById(R.id.tv_item_advermanage_dqsj);
            viewHolder.tv_item_advermanage_shzt = (TextView) view.findViewById(R.id.tv_item_advermanage_shzt);
//            viewHolder.tv_item_advermanage_shzt.setTag(position);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
//            viewHolder.tv_item_advermanage_shzt.setTag(position);
        }
        Glide.with(mContext).load(HttpUtils.URL +"/upload/image/"+adverManageBean.getImg()).into( viewHolder.tv_item_advermanage_url);

        viewHolder.tv_item_advermanage_sspd.setText(adverManageBean.getSspd());
        String sheng = "";
        if(adverManageBean.getQysheng() != null && !"".equals(adverManageBean.getQysheng())){
            for(AreaBean areaBean : AdvertisingManageActivity.provinceList){
                if(adverManageBean.getQysheng().equals(areaBean.getTid())){
                    sheng = areaBean.getName();
                }
            }
        }

        viewHolder.tv_item_advermanage_qy.setText(adverManageBean.getQysheng()+adverManageBean.getQyshi());
        viewHolder.tv_item_advermanage_dqsj.setText(adverManageBean.getDqsj());
        String state = adverManageBean.getState();
        if("0".equals(state)){
            viewHolder.tv_item_advermanage_shzt.setText("未审核");
        }else if("1".equals(state)){
            viewHolder.tv_item_advermanage_shzt.setText("已通过");
        }else if("2".equals(state)){
            viewHolder.tv_item_advermanage_shzt.setText("未通过");
        }
        return view;

    }


    final static class ViewHolder {
        ImageView tv_item_advermanage_url;
        TextView tv_item_advermanage_sspd;
        TextView tv_item_advermanage_qy;
        TextView tv_item_advermanage_dqsj;
        TextView tv_item_advermanage_shzt;

    }

}