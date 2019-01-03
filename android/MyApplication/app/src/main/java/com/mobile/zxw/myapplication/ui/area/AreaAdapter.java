package com.mobile.zxw.myapplication.ui.area;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mobile.zxw.myapplication.databinding.ItemAreaBinding;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Description : 地址列表适配器
 *
 * @author WSoBan
 * @date 2018/05/03
 */
public class AreaAdapter extends BaseQuickAdapter<AreaBean, BaseViewHolder> {

    private int tab_province = 0;
    private int tab_city = 1;
    private int tab_district = 2;
    private int tab_country = 3;

    private AreaBean selectItem;
    private int currentTabPos;
    private Map<Integer, AreaBean> map;
    private OnSelectedListener onSelectedListener;

    public AreaAdapter(int layoutResId) {
        super(layoutResId);
        map = new TreeMap<>();
    }

    public void setData(int tabPos, List<AreaBean> sList) {
        currentTabPos = tabPos;
//        System.out.println("currentTabPos----=="+currentTabPos);
//        System.out.println("sList----=="+sList.size());
        for (AreaBean areaBean :sList) {
//            System.out.println("sList----=="+map.get(tabPos));
            if(map.get(tabPos) != null){
//                System.out.println("sList2----=="+map.get(tabPos).getTid());
//                System.out.println("sList3----=="+ areaBean.getTid());
                if (map.get(tabPos).getTid() == areaBean.getTid()) {
                    areaBean.check.set(true);
                } else {
                    areaBean.check.set(false);
                }
            }else{
                areaBean.check.set(false);
            }

        }
        setNewData(sList);
    }

    @Override
    protected void convert(BaseViewHolder helper, AreaBean item) {
        ItemAreaBinding mBinding = DataBindingUtil.bind(helper.itemView);
        mBinding.setModel(item);
        helper.itemView.setOnClickListener(v -> selectItem(item));
    }

    private void selectItem(AreaBean bean) {
        selectItem = bean;
        if (currentTabPos == tab_province) {
            map.clear();
        } else if (currentTabPos == tab_city) {
            map.remove(tab_country);
            map.remove(tab_district);
        }else if (currentTabPos == tab_district) {
            map.remove(tab_country);
        }
        map.put(currentTabPos, selectItem);

//        getData(currentTabPos);
        for (AreaBean areaBean : getData()) {
            if (selectItem.getTid() == areaBean.getTid()) {
                areaBean.check.set(true);
            } else {
                areaBean.check.set(false);
            }
        }
        if (onSelectedListener != null) {
            onSelectedListener.onSelected(map, currentTabPos);
        }
    }

    public void moveToPosition(LinearLayoutManager manager) {
        manager.scrollToPositionWithOffset(AreaParser.getInstance().getChoosePos(getData()), 0);
        manager.setStackFromEnd(true);
    }

    public void setOnSelectedListener(OnSelectedListener onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public interface OnSelectedListener {
        void onSelected(Map<Integer, AreaBean> map, int pos);
    }
}
