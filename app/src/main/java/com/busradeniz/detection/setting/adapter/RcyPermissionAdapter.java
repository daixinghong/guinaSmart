package com.busradeniz.detection.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.PermissionBean;

import java.util.List;

public class RcyPermissionAdapter extends RecyclerView.Adapter<RcyPermissionAdapter.PermissionHolder> {


    private Context mContext;
    private List<PermissionBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyPermissionAdapter(Context context, List<PermissionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public PermissionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PermissionHolder holder = new PermissionHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_permission_list_item_view, null,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(PermissionHolder holder, final int position) {
        holder.mTvPermissionTitle.setText(position + 1 + ":" + mList.get(position).getPermissionName());
        holder.mTvPermissionDesc.setText(mList.get(position).getPermissionInfo());
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class PermissionHolder extends RecyclerView.ViewHolder {


        private final TextView mTvPermissionTitle;
        private final TextView mTvPermissionDesc;

        public PermissionHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvPermissionTitle = itemView.findViewById(R.id.tv_permission_title);
            mTvPermissionDesc = itemView.findViewById(R.id.tv_permission_desc);

        }


    }


}
