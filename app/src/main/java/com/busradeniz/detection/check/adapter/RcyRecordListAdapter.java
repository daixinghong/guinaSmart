package com.busradeniz.detection.check.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.busradeniz.detection.R;
import com.busradeniz.detection.check.bean.RecordListBean;
import com.busradeniz.detection.utils.Constant;

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
                mContext).inflate(R.layout.rcy_record_item, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, final int position) {

        holder.mTvName.setText(mList.get(position).getConfig_name() + " " + mList.get(position).getDefect());
        Glide.with(mContext).load(Constant.URL + "api/record/download?url=" + mList.get(position).getPath()).into(holder.mImageView);

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class RecordHolder extends RecyclerView.ViewHolder {


        private final ImageView mImageView;
        private final TextView mTvName;

        public RecordHolder(View itemView, RcyCheckResultAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_item);
            mTvName = itemView.findViewById(R.id.name_item);

        }


    }

}
