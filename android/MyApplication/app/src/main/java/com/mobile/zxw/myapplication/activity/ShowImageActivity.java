package com.mobile.zxw.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.http.HttpUtils;

public class ShowImageActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout activity_show_image;
    private ImageView iv_show_image;
    String imgurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        mContext = this;

        imgurl = getIntent().getStringExtra("imgUrl");

        initView();
        initData();
    }



    public void initView(){
        activity_show_image = (RelativeLayout) findViewById(R.id.activity_show_image);
        activity_show_image.setOnClickListener(this);
        iv_show_image = (ImageView) findViewById(R.id.iv_show_image);
    }

    public void initData(){
        if(imgurl != null && !"".equals(imgurl)){
            Glide.with(mContext).load(HttpUtils.URL+"/"+imgurl).into(iv_show_image);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_show_image :
                finish();
                break;

            default:
                break;
        }
    }

}


