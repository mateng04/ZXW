package com.mobile.zxw.myapplication.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.zxw.myapplication.R;
import com.mobile.zxw.myapplication.adapter.CityAdapter;
import com.mobile.zxw.myapplication.adapter.SortAdapter;
import com.mobile.zxw.myapplication.bean.CityBean;
import com.mobile.zxw.myapplication.selectcitydome.view.CitySortModel;
import com.mobile.zxw.myapplication.selectcitydome.view.EditTextWithDel;
import com.mobile.zxw.myapplication.selectcitydome.view.PinyinComparator;
import com.mobile.zxw.myapplication.selectcitydome.view.PinyinUtils;
import com.mobile.zxw.myapplication.selectcitydome.view.SideBar;
import com.mobile.zxw.myapplication.until.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityChoiceActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog, mTvTitle;
    private SortAdapter adapter;
    private EditTextWithDel mEtCityName;
    private List<CitySortModel> SourceDateList = new ArrayList<CitySortModel>();
    private Button city_choice_back;

    private List<CityBean> cityList = new ArrayList<CityBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_choice);
        initViews();
    }

    private void initViews() {
        mEtCityName = (EditTextWithDel) findViewById(R.id.et_search);
        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        city_choice_back = (Button) findViewById(R.id.city_choice_back);
        city_choice_back.setOnClickListener(this);
        initDatas();
        initEvents();
        setAdapter();
    }

    private void setAdapter() {
//        SourceDateList = filledData(getResources().getStringArray(R.array.provinces));
//        Collections.sort(SourceDateList, new PinyinComparator());
//        adapter = new SortAdapter(this, SourceDateList);
//        sortListView.addHeaderView(initHeadView());
//        sortListView.setAdapter(adapter);

        new MyAsyncTask().execute();
    }

    private void initEvents() {
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position + 1);
                }
            }
        });

        //ListView的点击事件
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mTvTitle.setText(((CitySortModel) adapter.getItem(position - 1)).getName());
//                Toast.makeText(getApplication(), ((CitySortModel) adapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                System.out.println("-----------------"+position+"====="+id);
                for(int i=0; i<cityList.size(); i++){
                    if(cityList.get(i).getAresname().equals(((CitySortModel) adapter.getItem(position - 1)).getName())){
                        System.out.println("-----------------"+cityList.get(i).getId());
                        System.out.println("-----------------"+cityList.get(i).getAresname());
                        System.out.println("-----------------"+cityList.get(i).getParentid());
                    }
                }
            }
        });

        //根据输入框输入值的改变来过滤搜索
        mEtCityName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initDatas() {
        sideBar.setTextView(dialog);
    }

    private View initHeadView() {
        View headView = getLayoutInflater().inflate(R.layout.headview, null);
        GridView mGvCity = (GridView) headView.findViewById(R.id.gv_hot_city);
        String[] datas = getResources().getStringArray(R.array.city);
        final ArrayList<String> cityList = new ArrayList<>();
        for (int i = 0; i < datas.length; i++) {
            cityList.add(datas[i]);
        }
        CityAdapter adapter = new CityAdapter(getApplicationContext(), R.layout.gridview_item, cityList);
        mGvCity.setAdapter(adapter);
        mGvCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("onItemClick", cityList.get(position));
                Toast.makeText(CityChoiceActivity.this, cityList.get(position), Toast.LENGTH_SHORT).show();

            }
        });

        Button btn_city_name = (Button) headView.findViewById(R.id.btn_city_name);
        btn_city_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if("正在定位...".equals(((Button)view).getText().toString().trim())){
                    Toast.makeText(CityChoiceActivity.this, "正在定位，请稍等", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return headView;
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CitySortModel> mSortList = new ArrayList<>();
        if (TextUtils.isEmpty(filterStr)) {
            mSortList = SourceDateList;
        } else {
            mSortList.clear();
            for (CitySortModel sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(filterStr.toString().toUpperCase()) != -1 || PinyinUtils.getPingYin(name).toUpperCase().startsWith(filterStr.toString().toUpperCase())) {
                    mSortList.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(mSortList, new PinyinComparator());
        adapter.updateListView(mSortList);
    }

    private List<CitySortModel> filledData(String[] date) {
        List<CitySortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            CitySortModel sortModel = new CitySortModel();
            sortModel.setName(date[i]);
            String pinyin = PinyinUtils.getPingYin(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }
            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
        sideBar.setIndexText(indexString);
        return mSortList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.city_choice_back:
                CityChoiceActivity.this.finish();
                break;

            default:
                break;
        }
    }

    class MyAsyncTask extends AsyncTask<String, Void, List<CitySortModel>> {
        List<CitySortModel> mSortListStr = new ArrayList<>();
        ArrayList<String> indexStringStr = new ArrayList<>();
        @Override
        protected List<CitySortModel> doInBackground(String... params) {

            String industryString = Utils.readAssetsTXT(CityChoiceActivity.this,"city_code.txt");
            cityList.clear();
            String[] strings = industryString.split(";");
            for (int i = 0; i < strings.length; i++) {
                String[] items = strings[i].split(",");
                CityBean cityBean = new CityBean();
                cityBean.id = Integer.parseInt(items[0].trim());
                cityBean.aresname = items[1];
                cityBean.parentid = Integer.parseInt(items[2].trim());
                cityList.add(cityBean);

                CitySortModel sortModel = new CitySortModel();
                sortModel.setName(items[1]);
                String sortString = items[3];

                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString);
                    if (!indexStringStr.contains(sortString)) {
                        indexStringStr.add(sortString);
                    }
                }

//                CitySortModel sortModel = new CitySortModel();
//                sortModel.setName(items[1]);
//                String pinyin = PinyinUtils.getPingYin(items[1]);
//                String sortString = pinyin.substring(0, 1).toUpperCase();
//                if (sortString.matches("[A-Z]")) {
//                    sortModel.setSortLetters(sortString.toUpperCase());
//                    if (!indexStringStr.contains(sortString)) {
//                        indexStringStr.add(sortString);
//                    }
//                }

                mSortListStr.add(sortModel);
            }

            return mSortListStr;
        }

        @Override
        protected void onPostExecute(List<CitySortModel> beanDatas) {
            super.onPostExecute(beanDatas);

            Collections.sort(indexStringStr);
            sideBar.setIndexText(indexStringStr);

            SourceDateList.clear();
            SourceDateList.addAll(beanDatas);
            Collections.sort(SourceDateList, new PinyinComparator());
            adapter = new SortAdapter(CityChoiceActivity.this, SourceDateList);
            sortListView.addHeaderView(initHeadView());
            sortListView.setAdapter(adapter);
        }
    }


}
