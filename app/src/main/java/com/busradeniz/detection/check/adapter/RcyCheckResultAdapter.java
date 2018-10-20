package com.busradeniz.detection.check.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.check.bean.CheckResultBean;
import com.busradeniz.detection.utils.UiUtils;

import java.util.List;

public class RcyCheckResultAdapter extends RecyclerView.Adapter<RcyCheckResultAdapter.CheckResultHolder> {

    private Context mContext;
    private List<CheckResultBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyCheckResultAdapter(Context context, List<CheckResultBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public CheckResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckResultHolder holder = new CheckResultHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_check_result_item_view, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(CheckResultHolder holder, final int position) {

        holder.mTvCheckPosition.setText(mList.get(position).getName());
        String status = mList.get(position).getIsSuccess();

        if (TextUtils.isEmpty(status)) {    //检测中
            holder.mTvDislikeDesc.setText(UiUtils.getString(R.string.checking));
            holder.mTvDislikeDesc.setTextColor(UiUtils.getColor(R.color.text_black_primary));
        } else if (status.equals("ok")) {   //位置ok
            holder.mTvDislikeDesc.setText(UiUtils.getString(R.string.ok));
            holder.mTvDislikeDesc.setTextColor(UiUtils.getColor(R.color.color_50FF00));
        } else {                            //位置ng
            holder.mTvDislikeDesc.setText(UiUtils.getString(R.string.error));
            holder.mTvDislikeDesc.setTextColor(UiUtils.getColor(R.color.red));
        }
        holder.mTvStatus.setText(mList.get(position).getError());


    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class CheckResultHolder extends RecyclerView.ViewHolder {


        private final TextView mTvCheckPosition;
        private final TextView mTvDislikeDesc;
        private final TextView mTvStatus;

        public CheckResultHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvCheckPosition = itemView.findViewById(R.id.tv_check_position);
            mTvDislikeDesc = itemView.findViewById(R.id.tv_dislike_desc);
            mTvStatus = itemView.findViewById(R.id.tv_stauts);


        }


    }
}
