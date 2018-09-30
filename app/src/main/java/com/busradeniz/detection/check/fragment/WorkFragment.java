package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.ChooseVersionBean;
import com.busradeniz.detection.bean.NewVersionBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.adapter.RcyProductListAdapter;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.utils.PinYinUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class WorkFragment extends Fragment  {

    private EditText mEtSearch;
    private RecyclerView mRcyList;
    private List<ChooseVersionBean> mList = new ArrayList<>();
    private SupportBeanDao mSupportBeanDao;
    private List<ChooseVersionBean> mSeachResultList = new ArrayList<>();
    private RcyProductListAdapter mAdapter;
    private List<String> mModelList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_work_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;

    }

    private void initEvent() {

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String string = s.toString();
                searchCompany(string);

            }
        });
    }


    /**
     * 搜索匹配字符串
     *
     * @param s 输入的字符串
     */
    private void searchCompany(CharSequence s) {

        mSeachResultList.clear();
        if (TextUtils.isEmpty(s.toString().trim())) {
            mSeachResultList.addAll(mList);
            mAdapter.notifyDataSetChanged();
            return;
        }

        if (mList != null && mList.size() != 0) {
            for (int i = 0; i < mList.size(); i++) {
                String username = mList.get(i).getProjectName();
                if (TextUtils.isEmpty(username))
                    continue;
                char[] chars = null;
                if (s.toString().matches("^[\\u4e00-\\u9fa5]+$")) {//输入文字是中文
                    chars = s.toString().trim().toCharArray();
                } else {//输入文字是英文
                    username = PinYinUtils.converterToFirstSpell(username).toUpperCase();//转成拼音首字母
                    chars = s.toString().trim().toUpperCase().toCharArray();
                }
                for (int j = 0; j < chars.length; j++) {
                    char aChar = chars[j];
                    if (!username.contains(aChar + "")) {
                        break;
                    }
                    if (username.contains(aChar + "") && j == (chars.length - 1)) {
                        mSeachResultList.add(mList.get(i));
                    }
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {



        mSupportBeanDao = BaseApplication.getApplicatio().getDaoSession().getSupportBeanDao();
        List<SupportBean> supportBeans = mSupportBeanDao.loadAll();
        mList.clear();
        Gson gson = new Gson();

        for (int i = 0; i < supportBeans.size(); i++) {
            String projectName = supportBeans.get(i).getProjectName();
            String data = supportBeans.get(i).getData();
            List<NewVersionBean> arryList = gson.fromJson(data, new TypeToken<List<NewVersionBean>>() {
            }.getType());

            ChooseVersionBean chooseVersionBean = new ChooseVersionBean();
            chooseVersionBean.setProjectName(projectName);
            chooseVersionBean.setList(arryList);
            mList.add(chooseVersionBean);
        }
        mSeachResultList.clear();
        mSeachResultList.addAll(mList);
        mAdapter = new RcyProductListAdapter(getActivity(), mSeachResultList, this, mSupportBeanDao);
        mRcyList.setAdapter(mAdapter);


    }

    private void initView(View inflate) {

        mEtSearch = inflate.findViewById(R.id.et_search);
        mRcyList = inflate.findViewById(R.id.rcy_list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRcyList.setLayoutManager(manager);
    }



}
