package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.HomeSCBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;

import java.util.List;

public class HomeSCAdapter  extends RecyclerView.Adapter<HomeSCAdapter.ViewHolder> implements View.OnClickListener {
    private static final String TAG = HomeSCAdapter.class.getSimpleName();
    private List<HomeSCBean> data;
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;//用来计算Child位置
    private OnItemClickListener onItemClickListener;
    private Context context;
    private String tag;
    //对外提供接口初始化方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public HomeSCAdapter(Context context,List<HomeSCBean> data,String tag) {
        this.data = data;
        this.context = context;
        this.tag = tag;
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView home_sc_item_pic;
        TextView home_sc_item_title;
        TextView home_sc_item_fh;
        TextView home_sc_item_price;
        TextView home_sc_item_buy;

        public ViewHolder(View itemView) {
            super(itemView);
            home_sc_item_pic= (ImageView) itemView.findViewById(R.id.home_sc_item_pic);
            home_sc_item_title= (TextView) itemView.findViewById(R.id.home_sc_item_title);
            home_sc_item_fh= (TextView) itemView.findViewById(R.id.home_sc_item_fh);
            home_sc_item_price= (TextView) itemView.findViewById(R.id.home_sc_item_price);
            home_sc_item_buy= (TextView) itemView.findViewById(R.id.home_sc_item_buy);
        }
    }

    /**
     * 创建VIewHolder，导入布局，实例化itemView
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_select_home_recy_sc, parent, false);
        //导入itemView，为itemView设置点击事件
        itemView.setOnClickListener(this);
        return new ViewHolder(itemView);
    }

    /**
     * 绑定VIewHolder，加载数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println("onBindViewHolder-----"+position);
        holder.home_sc_item_title.setText(data.get(position).getShopTile());//加载数据
        holder.home_sc_item_price.setText(data.get(position).getShopPrice());//加载数据
        if("sc".equals(tag)){
            holder.home_sc_item_buy.setText("立即购买");
        }else{
            holder.home_sc_item_buy.setText("查看详情");
            holder.home_sc_item_fh.setText("人气：");
            holder.home_sc_item_fh.setTextColor(context.getResources().getColor(R.color.s_gray));
        }

        new GlideImageLoader().displayImage(context,HttpUtils.URL +"/" +(data.get(position).getShopPicUrl()),holder.home_sc_item_pic);
//        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
//        layoutParams.height=data.get(position).getHeight();
//        holder.itemView.setLayoutParams(layoutParams);
    }

    /**
     * 数据源的数量，item的个数
     * @return
     */
    @Override
    public int getItemCount() {
        return data!=null?data.size():0;
    }

    /**
     * 适配器绑定到RecyclerView 的时候，回将绑定适配器的RecyclerView 传递过来
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView=recyclerView;
    }

    /**
     *
     * @param v 点击的View
     */
    @Override
    public void onClick(View v) {
        //RecyclerView可以计算出这是第几个Child
        int childAdapterPosition = mRecyclerView.getChildAdapterPosition(v);
        Log.e(TAG, "onClick: "+childAdapterPosition );
        if (onItemClickListener!=null) {
            onItemClickListener.onItemClick(childAdapterPosition,data.get(childAdapterPosition),tag);
        }
    }

    /**
     * 接口回调
     * 1、定义接口，定义接口中的方法
     * 2、在数据产生的地方持有接口，并提供初始化方法，在数据产生的时候调用接口的方法
     * 3、在需要处理数据的地方实现接口，实现接口中的方法，并将接口传递到数据产生的地方
     */
    public interface OnItemClickListener{
        void onItemClick(int position, HomeSCBean model,String tag);
    }


    public interface OnLoadingMore {    void onLoadMore();}
}