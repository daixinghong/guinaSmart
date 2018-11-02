package com.busradeniz.detection.check.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.ConfigureListBean;

import java.util.List;

public class RcyChooseNameAdapter extends RecyclerView.Adapter<RcyChooseNameAdapter.CheckResultHolder> {
    private Context mContext;
    private List<ConfigureListBean.DatasBean> mList;
    private RcyProductListAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(RcyProductListAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyChooseNameAdapter(Context context, List<ConfigureListBean.DatasBean> list) {
        this.mContext = context;
        this.mList = list;
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

        public CheckResultHolder(View itemView, RcyProductListAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvProjectName = itemView.findViewById(R.id.tv_project_name);
            mTvCheckType = itemView.findViewById(R.id.tv_check_type);
            mRlAuto = itemView.findViewById(R.id.rl_auto);
            mRlSeeDetails = itemView.findViewById(R.id.rl_see_details);
        }

    }
}
