package com.busradeniz.detection.check.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.HandOpreateBean;

import java.util.List;

public class RcyHandOpreateAdapter extends RecyclerView.Adapter<RcyHandOpreateAdapter.HandOpreateHolder> {

    private Context mContext;
    private List<HandOpreateBean> mList;
    private OnItemClickListener mOnItemClickListener;


    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyHandOpreateAdapter(Context context, List<HandOpreateBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public HandOpreateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HandOpreateHolder holder = new HandOpreateHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_hand_opreate_item, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(HandOpreateHolder holder, final int position) {
        holder.mTvName.setText(mList.get(position).getName());
        if(mList.get(position).isStatus()){
            holder.mRlItem.setBackgroundResource(R.drawable.button_shape);
        }else{
            holder.mRlItem.setBackgroundResource(R.drawable.button_selected_shape);
        }


    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class HandOpreateHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final TextView mTvName;
        private OnItemClickListener mOnItemClickListener;
        private final RelativeLayout mRlItem;

        public HandOpreateHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mRlItem = itemView.findViewById(R.id.rl_item);
            this.mOnItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.setOnItemClickListener(view, getPosition());
        }
    }
}
