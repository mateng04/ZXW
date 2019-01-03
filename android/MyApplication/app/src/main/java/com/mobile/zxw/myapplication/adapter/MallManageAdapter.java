package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.activity.SetTopPageActivity;
import com.mobile.zxw.myapplication.bean.RecruitManageBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MallManageAdapter extends BaseAdapter  {
    private List<RecruitManageBean> list = null;
    private Context mContext;
    private Handler handler;
    String dataflag;

    public MallManageAdapter(Context mContext, List<RecruitManageBean> list, Handler handler, String dataflag) {
        this.mContext = mContext;
        this.list = list;
        this.handler = handler;
        this.dataflag = dataflag;
    }

    public void setFlag(String flag){
        dataflag = flag;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<RecruitManageBean> list) {
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
        final RecruitManageBean recruitManageBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_recruit_manage, null);
            viewHolder.tv_item_rm_title = (TextView) view.findViewById(R.id.tv_item_rm_title);
            viewHolder.tv_item_rm_state = (TextView) view.findViewById(R.id.tv_item_rm_state);
            viewHolder.tv_item_rm_top = (TextView) view.findViewById(R.id.tv_item_rm_top);
            viewHolder.tv_item_rm_operation = (TextView) view.findViewById(R.id.tv_item_rm_operation);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.tv_item_rm_title.setText(recruitManageBean.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//yyyy-mm-dd, 会出现时间不对, 因为小写的mm是代表: 秒
        Date utilDate = null;
        try {
            utilDate = sdf.parse(recruitManageBean.getYouxiaoqi());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date nowDate = new Date();
        if(nowDate.getTime() > utilDate.getTime()){
            viewHolder.tv_item_rm_state.setText("过期");
        }else{
            viewHolder.tv_item_rm_state.setText("正常");
        }
//        viewHolder.tv_item_rm_top.setText(recruitManageBean.getHrTop());
//        viewHolder.tv_item_rm_operation.setText(recruitManageBean.getHrOperation());

        String gddq = recruitManageBean.getGudingdaoqi();

        if("".equals(gddq) || gddq == null){
            viewHolder.tv_item_rm_top.setText("置顶");
        }else{
            SimpleDateFormat sdf_gddq = new SimpleDateFormat("yyyy-MM-dd");//yyyy-mm-dd, 会出现时间不对, 因为小写的mm是代表: 秒
            Date gddqDate = null;
            try {
                gddqDate = sdf_gddq.parse(gddq);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(nowDate.getTime() > gddqDate.getTime()){
                viewHolder.tv_item_rm_top.setText("置顶");
            }else{
                viewHolder.tv_item_rm_top.setText("置顶中");
            }

        }
        String topText =  viewHolder.tv_item_rm_top.getText().toString().trim();
        viewHolder.tv_item_rm_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if("置顶中".equals(topText)){
                    Toast.makeText(mContext,"信息已经置顶中",Toast.LENGTH_SHORT).show();
                }else{
                    String xxpd = "";
                    if("0".equals(dataflag)){
                        xxpd = "正信商城";
                    }else{
                        xxpd = "微商专区";
                    }
                    String xxid = recruitManageBean.getId();
                    String xxbt = recruitManageBean.getTitle();
                    Intent intent = new Intent(mContext,SetTopPageActivity.class);
                    intent.putExtra("xxpd",xxpd);
                    intent.putExtra("xxid",xxid);
                    intent.putExtra("xxbt",xxbt);
                    mContext.startActivity(intent);
                }
            }
        });
        viewHolder.tv_item_rm_operation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"删除"+recruitManageBean.getHrID(),Toast.LENGTH_SHORT).show();

                System.out.println("position--------"+position);

                Message message = Message.obtain();
                message.obj =  list.get(position).getId();
                message.arg1 = position;
                message.what = 3; // 结束标志位
                handler.sendMessage(message); // 将数据发送过去~
            }
        });

        return view;

    }


    final static class ViewHolder {

        TextView tv_item_rm_title;
        TextView tv_item_rm_state;
        TextView tv_item_rm_top;
        TextView tv_item_rm_operation;

    }

}