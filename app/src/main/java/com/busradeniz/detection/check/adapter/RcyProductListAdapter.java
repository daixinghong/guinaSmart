package com.busradeniz.detection.check.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.ConfigureListBean;
import com.busradeniz.detection.bean.SupportBean;
import com.busradeniz.detection.check.ScanTwoThinkActivity;
import com.busradeniz.detection.check.fragment.DebugFragment;
import com.busradeniz.detection.check.fragment.WorkFragment;
import com.busradeniz.detection.greendaodemo.db.SupportBeanDao;
import com.busradeniz.detection.setting.CreateVersionActivity;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.IntentUtils;

import java.util.List;

public class RcyProductListAdapter extends RecyclerView.Adapter<RcyProductListAdapter.CheckResultHolder> {

    private Context mContext;
    private List<ConfigureListBean.DatasBean> mList;
    private OnItemClickListener mOnItemClickListener;
    private DebugFragment mDebugFragment;
    private WorkFragment mWorkFragment;
    private SupportBeanDao mSupportBeanDao;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyProductListAdapter(Context context, List<ConfigureListBean.DatasBean> list, WorkFragment workFragment, SupportBeanDao supportBeanDao) {
        this.mContext = context;
        this.mList = list;
        this.mWorkFragment = workFragment;
        this.mSupportBeanDao = supportBeanDao;
    }

    @Override
    public CheckResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckResultHolder holder = new CheckResultHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_product_list_item, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(CheckResultHolder holder, final int position) {
        holder.mTvProjectName.setText(mList.get(position).getName());

        holder.mRlSeeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.STATUS, true);
                bundle.putInt(Constant.ID, mList.get(position).getId());
                IntentUtils.startActivityForParms(mContext, CreateVersionActivity.class, bundle);


            }
        });

        holder.mRlAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt(Constant.ID, mList.get(position).getId());
                IntentUtils.startActivityForParms(mContext, ScanTwoThinkActivity.class, bundle);
            }
        });


    }


    /**
     * 更新数据库产品型号数据
     *
     * @param key
     * @return
     */
    public boolean queryData(String key, boolean status) {
        try {
            SupportBean unique = mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.ProjectName.eq(key)).build().unique();
            if (unique != null) {
                unique.setSelectedStatus(status);
                mSupportBeanDao.update(unique);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public boolean queryData(boolean key, boolean status) {
        try {
            SupportBean unique = mSupportBeanDao.queryBuilder().where(SupportBeanDao.Properties.SelectedStatus.eq(key)).build().unique();
            if (unique != null) {
                unique.setSelectedStatus(status);
                mSupportBeanDao.update(unique);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class CheckResultHolder extends RecyclerView.ViewHolder {


        private final TextView mTvProjectName;
        private final TextView mTvCheckType;
        private final RelativeLayout mRlAuto;
        private final RelativeLayout mRlSeeDetails;

        public CheckResultHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvProjectName = itemView.findViewById(R.id.tv_project_name);
            mTvCheckType = itemView.findViewById(R.id.tv_check_type);
            mRlAuto = itemView.findViewById(R.id.rl_auto);
            mRlSeeDetails = itemView.findViewById(R.id.rl_see_details);
        }

    }
}
