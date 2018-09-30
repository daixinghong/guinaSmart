package com.busradeniz.detection.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.busradeniz.detection.R;

import java.util.List;

public class RcyModelAdapter extends RecyclerView.Adapter<RcyModelAdapter.ProductFirstSpscHodler> {

    private Context mContext;
    private List<String> mList;
    private OnItemClickListener mOnItemClickListener;
    private String mModelUrl;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyModelAdapter(Context context, List<String> list, String string) {
        this.mContext = context;
        this.mList = list;
        this.mModelUrl = string;
    }

    @Override
    public ProductFirstSpscHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductFirstSpscHodler holder = new ProductFirstSpscHodler(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_model_item, null,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductFirstSpscHodler holder, final int position) {
        holder.mTvName.setText(mList.get(position));
        if (mModelUrl.equals(mList.get(position))) {
            holder.mIvSelected.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ProductFirstSpscHodler extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final TextView mTvName;

        private OnItemClickListener mOnItemClickListener;
        private final ImageView mIvSelected;

        public ProductFirstSpscHodler(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_model_name);
            mIvSelected = itemView.findViewById(R.id.iv_selected);
            this.mOnItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.setOnItemClickListener(v, getPosition());
        }
    }

}
