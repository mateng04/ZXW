package com.mobile.zxw.myapplication.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.bean.ShopCarBean;
import com.mobile.zxw.myapplication.http.HttpUtils;
import com.mobile.zxw.myapplication.ui.GlideImageLoader;
import com.mobile.zxw.myapplication.until.SharedPreferencesHelper;

import java.util.List;

public class ShopCarAdapter extends BaseAdapter  {
    private List<ShopCarBean> list = null;
    private Context mContext;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Handler handler;

    public ShopCarAdapter(Context mContext, List<ShopCarBean> list, SharedPreferencesHelper sharedPreferencesHelper,Handler handler) {
        this.mContext = mContext;
        this.list = list;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        this.handler = handler;
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     *
     * @param list
     */
    public void updateListView(List<ShopCarBean> list) {
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
        final ShopCarBean shopCarBean = list.get(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_shop_car, null);
            viewHolder.iv_item_shop_car_url = (ImageView) view.findViewById(R.id.iv_item_shop_car_url);
            viewHolder.tv_item_shop_car_title = (TextView) view.findViewById(R.id.tv_item_shop_car_title);
            viewHolder.tv_item_shop_car_price = (TextView) view.findViewById(R.id.tv_item_shop_car_price);
            viewHolder.tv_item_shop_car_count = (TextView) view.findViewById(R.id.tv_item_shop_car_count);
            viewHolder.tv_item_shop_car_delete = (TextView) view.findViewById(R.id.tv_item_shop_car_delete);
            viewHolder.btn_sub = (ImageView) view.findViewById(R.id.btn_sub);
            viewHolder.btn_add = (ImageView) view.findViewById(R.id.btn_add);
            viewHolder.ll_item_shopcar_num = (LinearLayout) view.findViewById(R.id.ll_item_shopcar_num);
            view.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final TextView text_num = viewHolder.tv_item_shop_car_count;

        new GlideImageLoader().displayImage(mContext, HttpUtils.URL+"/"+this.list.get(position).getShopUrl(),viewHolder.iv_item_shop_car_url);

        viewHolder.tv_item_shop_car_title.setText(this.list.get(position).getShopTile());
        viewHolder.tv_item_shop_car_price.setText("¥"+this.list.get(position).getShopPrice());
        viewHolder.tv_item_shop_car_count.setText(this.list.get(position).getShopNum());

        viewHolder.ll_item_shopcar_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("ll_item_shopcar_num");
            }
        });

        viewHolder.btn_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num_str = text_num.getText().toString().trim();
                int num = Integer.valueOf(num_str);
                num = num  - 1;
                if(num < 1 ){
                    num = 1;
                }
                list.get(position).setShopNum(num+"");
                ShopCarAdapter.this.notifyDataSetChanged();//更新适配器
                String shopcarnum = "";
                String shopcar = "";
                for(ShopCarBean shopCarBean:list){
                    shopcarnum = shopcarnum + "-" + shopCarBean.getShopNum() ;
                    shopcar = shopcar + "-" + shopCarBean.getShopID() ;
                }
                if(shopcarnum.startsWith("-")){
                    shopcarnum =  shopcarnum.substring(1,shopcarnum.length());
                }
                if(shopcar.startsWith("-")){
                    shopcar =  shopcar.substring(1,shopcar.length());
                }
                sharedPreferencesHelper.put("shopcarnum",shopcarnum);
                sharedPreferencesHelper.put("shopcar",shopcar);
                handler.sendEmptyMessage(100);
            }
        });

        viewHolder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String num_str = text_num.getText().toString().trim();
                int num = Integer.valueOf(num_str);
                num = num  + 1;
                list.get(position).setShopNum(num+"");
                ShopCarAdapter.this.notifyDataSetChanged();//更新适配器
                String shopcarnum = "";
                String shopcar = "";
                for(ShopCarBean shopCarBean:list){
                    shopcarnum = shopcarnum + "-" + shopCarBean.getShopNum() ;
                    shopcar = shopcar + "-" + shopCarBean.getShopID() ;
                }
                if(shopcarnum.startsWith("-")){
                    shopcarnum =  shopcarnum.substring(1,shopcarnum.length());
                }
                if(shopcar.startsWith("-")){
                    shopcar =  shopcar.substring(1,shopcar.length());
                }
                sharedPreferencesHelper.put("shopcarnum",shopcarnum);
                sharedPreferencesHelper.put("shopcar",shopcar);
                handler.sendEmptyMessage(100);
            }
        });
        viewHolder.tv_item_shop_car_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                ShopCarAdapter.this.notifyDataSetChanged();//更新适配器
                String shopcarnum = "";
                String shopcar = "";
                for(ShopCarBean shopCarBean:list){
                    shopcarnum = shopcarnum + "-" + shopCarBean.getShopNum() ;
                    shopcar = shopcar + "-" + shopCarBean.getShopID() ;
                }
                if(shopcarnum.startsWith("-")){
                    shopcarnum =  shopcarnum.substring(1,shopcarnum.length());
                }
                if(shopcar.startsWith("-")){
                    shopcar =  shopcar.substring(1,shopcar.length());
                }
                sharedPreferencesHelper.put("shopcarnum",shopcarnum);
                sharedPreferencesHelper.put("shopcar",shopcar);
                handler.sendEmptyMessage(100);
            }
        });

        return view;

    }


    final static class ViewHolder {
        ImageView iv_item_shop_car_url;
        TextView tv_item_shop_car_title;
        TextView tv_item_shop_car_price;
        TextView tv_item_shop_car_count;
        TextView tv_item_shop_car_delete;
        LinearLayout ll_item_shopcar_num;
        ImageView btn_sub;
        ImageView btn_add;
    }

}