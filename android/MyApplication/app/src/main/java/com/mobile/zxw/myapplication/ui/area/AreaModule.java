package com.mobile.zxw.myapplication.ui.area;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.databinding.LayoutBottomSheetDialogBinding;
import com.mobile.zxw.myapplication.until.TabLayoutUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * Description : 弹出框模块
 *
 * @author WSoBan
 * @date 2018/05/03
 */
@Deprecated
public class AreaModule {

    private LayoutBottomSheetDialogBinding mDialogBinding;

    private AreaAdapter mAdapter;
    private Map<Integer, AreaBean> currentMap = new TreeMap<>();
    private Context mContext;
    private OnSelectedResultCallBack resultCallBack;
    private BottomSheetDialog bsd1;

    public AreaModule(Context context, OnSelectedResultCallBack callBack) {
        mContext = context;
        resultCallBack = callBack;
        initView();
    }

    private void initView() {
        mDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_bottom_sheet_dialog, null, false);
        bsd1 = new BottomSheetDialog(mContext);
        bsd1.setContentView(mDialogBinding.getRoot());

        mDialogBinding.ivClose.setOnClickListener(v -> bsd1.dismiss());

        mAdapter = new AreaAdapter(R.layout.item_area);
        mAdapter.setOnSelectedListener((map, pos) -> {
            if (pos >= 2) {
                if (resultCallBack != null) {
//                    resultCallBack.onResult(currentMap.get(pos).getNames());
                    resultCallBack.onResult("1234");
                }
                bsd1.dismiss();
            } else {
                currentMap = map;
                mDialogBinding.tlTitle.removeAllTabs();
                for (Integer in : map.keySet()) {
                    mDialogBinding.tlTitle.addTab(mDialogBinding.tlTitle.newTab().setText(map.get(in).getName()));
                }
                addChooseTab();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(bsd1.getContext());
        mDialogBinding.rv.setLayoutManager(manager);
        mDialogBinding.rv.addItemDecoration(new LineAreaItemDecoration(bsd1.getContext(), 2));
        mDialogBinding.rv.setAdapter(mAdapter);

        mDialogBinding.tlTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        mDialogBinding.tlTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                if (pos == 0) {
                    mAdapter.setData(pos, AreaParser.getInstance().getProvinceList());
                } else {
                    mAdapter.setData(pos, AreaParser.getInstance().getChildList(currentMap.get(pos - 1).getTid()));
                }
                //移动到指定位置
                mAdapter.moveToPosition(manager);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        addChooseTab();
    }

    private void addChooseTab() {
        mDialogBinding.tlTitle.addTab(mDialogBinding.tlTitle.newTab().setText("请选择"), true);
        TabLayoutUtil.reflex(mDialogBinding.tlTitle);
    }

    /**
     * 打开底部弹出框
     */
    public void openDialog() {
        if (bsd1 != null && !bsd1.isShowing()) {
            bsd1.show();
        }
    }

    public interface OnSelectedResultCallBack {

        void onResult(String result);
    }
}
