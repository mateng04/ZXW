package com.mobile.zxw.myapplication.ui.area;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.databinding.LayoutBottomSheetDialogBinding;
import com.mobile.zxw.myapplication.until.TabLayoutUtil;

import java.util.Map;
import java.util.TreeMap;

/**
 * Description :
 *
 * @author WSoBan
 * @date 2018/05/03
 */
public class BottomDialog extends Dialog {

    private OnSelectedResultCallBack resultCallBack;
    private LayoutBottomSheetDialogBinding mDialogBinding;

    private AreaAdapter mAdapter;
    private Map<Integer, AreaBean> currentMap = new TreeMap<>();
    Context context;

    public BottomDialog(Context context) {
        super(context, R.style.bottom_dialog);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        mDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_bottom_sheet_dialog, null, false);
        setContentView(mDialogBinding.getRoot());
        initView();

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.height = DensityUtils.dp2px(context, 400);
        window.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);
    }

    private void initView() {
        mDialogBinding.ivClose.setOnClickListener(v -> dismiss());
        mDialogBinding.ivSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentMap.size() < 1 ){
                    Toast.makeText(context,"请选择城市",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(currentMap.size() < 2){
//                    System.out.println("xxxx----"+currentMap.get(0).getName());
                    if("全国".equals(currentMap.get(0).getName())){
                        if (resultCallBack != null) {
                            resultCallBack.onResult(currentMap);
                            dismiss();
                            return;
                        }
                    }else{
                        Toast.makeText(context,"请选择城市",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                if (resultCallBack != null) {
                    resultCallBack.onResult(currentMap);
                }
                dismiss();
            }
        });

        mAdapter = new AreaAdapter(R.layout.item_area);
        mAdapter.setOnSelectedListener((map, pos) -> {
            System.out.println("xxxx----"+pos);
            if(pos == 0){
                currentMap = map;
                if("全国".equals(currentMap.get(0).getName())){
                    if (resultCallBack != null) {
                        resultCallBack.onResult(currentMap);
                    }
                    dismiss();
                    return;
                }
            }


            if (pos >= 3) {
                if (resultCallBack != null) {
////                    resultCallBack.onResult(currentMap.get(pos).getNames());
//                    StringBuilder names = new StringBuilder();
//                    for (Integer in : currentMap.keySet()) {
//                        names.append(currentMap.get(in).getName());
//                    }
                    resultCallBack.onResult(currentMap);
                }
                dismiss();
            } else {
                currentMap = map;
                mDialogBinding.tlTitle.removeAllTabs();
                for (Integer in : map.keySet()) {
                    mDialogBinding.tlTitle.addTab(
                            mDialogBinding.tlTitle.newTab().setText(map.get(in).getName()));
                }
                addChooseTab();
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mDialogBinding.rv.setLayoutManager(manager);
        mDialogBinding.rv.addItemDecoration(new LineAreaItemDecoration(getContext(), 3));
        mDialogBinding.rv.setAdapter(mAdapter);

        mDialogBinding.tlTitle.setTabMode(TabLayout.MODE_SCROLLABLE);
        mDialogBinding.tlTitle.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                if (pos == 0) {
                    mAdapter.setData(pos, AreaParser.getInstance().getProvinceList2());
                } else if(pos == 1){
                    mAdapter.setData(pos, AreaParser.getInstance().getCityList(currentMap.get(pos - 1).getTid()));
                }else if(pos == 2){
                    mAdapter.setData(pos, AreaParser.getInstance().getRegionList(currentMap.get(pos - 1).getTid()));
                }else if(pos == 3){
                    mAdapter.setData(pos, AreaParser.getInstance().getChildList(currentMap.get(pos - 1).getTid()));
                }else {
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

    public BottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    public BottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    public void setResultCallBack(OnSelectedResultCallBack resultCallBack) {
        this.resultCallBack = resultCallBack;
    }

    public interface OnSelectedResultCallBack {

        void onResult( Map<Integer, AreaBean> currentMap);
    }
}
