package com.mobile.zxw.myapplication.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.zxw.myapplication.R;

public class LoadListView extends ListView implements AbsListView.OnScrollListener {
    LinearLayout invis;//设置头部布局
    View footer;//底部布局
    int totalItemCount;//总数量
    int lastVisibleItem;//最后一个可见的Item
    boolean isLoading;//判断是否正在加载
    private IloadInterface mIloadInterface;
    boolean isAllDataSuccess = false;//判断数据是否全部加载

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 添加底部加载布局到ListView中
     *
     * @param context 上下文对象
     */
    private void initView(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footer_layout, null);
        //第一次进来下面正在加载是隐藏的
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        this.addFooterView(footer);
        this.setOnScrollListener(this);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

//        System.out.println("------onScrollStateChanged-----");
        if (this.totalItemCount == lastVisibleItem &&
                scrollState == SCROLL_STATE_IDLE && !isAllDataSuccess) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
                //加载更多数据
                mIloadInterface.onLoad();
            }

        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = visibleItemCount + firstVisibleItem;
        this.totalItemCount = totalItemCount;
//        System.out.println("------onScroll-----lastVisibleItem---"+lastVisibleItem);
//        System.out.println("------onScroll-----totalItemCount---"+totalItemCount);

        if( invis != null){
            if (firstVisibleItem >= 1) {
                invis.setVisibility(View.VISIBLE);
            } else {
                invis.setVisibility(View.GONE);
            }
        }

    }


    public void setInterface(IloadInterface iloadInterface) {
        this.mIloadInterface = iloadInterface;
    }

    public void setHeaderView(LinearLayout invis){
        this.invis = invis;
    }

    //加载更多数据的回调方法
    public interface IloadInterface {
        void onLoad();
    }

    /**
     * 加载完毕
     */
    public void loadComplete() {
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
    }

    /**
     * 全部数据加载完毕设置
     */
    public void setAllDataSuccess() {
        isAllDataSuccess = true;
        footer.findViewById(R.id.pg_load_layout).setVisibility(View.GONE);
        ((TextView)footer.findViewById(R.id.tv_load_layout)).setText("全部已加载");
        footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
    }

    /**
     * 取消全部数据加载完毕设置
     */
    public void cancleAllDataSuccess() {
        isAllDataSuccess = false;
        footer.findViewById(R.id.pg_load_layout).setVisibility(View.VISIBLE);
        ((TextView)footer.findViewById(R.id.tv_load_layout)).setText("正在加载");
        footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
    }
}