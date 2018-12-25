package com.busradeniz.detection.check.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.check.model.RecordListBean;

import java.util.List;

public class RcyRecordListAdapter extends RecyclerView.Adapter<RcyRecordListAdapter.RecordHolder> {

    private Context mContext;
    private List<RecordListBean.DatasBean> mList;
    private RcyCheckResultAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(RcyCheckResultAdapter.OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyRecordListAdapter(Context context, List<RecordListBean.DatasBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecordHolder holder = new RecordHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_record_item, parent, false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, final int position) {

        holder.mTvName.setText(mList.get(position).getConfig_name());
        holder.mTvStatus.setText(mList.get(position).getDefect());
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class RecordHolder extends RecyclerView.ViewHolder {


        private final TextView mTvName;
        private final TextView mTvStatus;

        public RecordHolder(View itemView, RcyCheckResultAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.name_item);
            mTvStatus = itemView.findViewById(R.id.tv_status);

        }


    }

}
