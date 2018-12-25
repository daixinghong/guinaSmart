package com.busradeniz.detection.setting.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.ChooseVersionBean;

import java.util.List;

public class RcyChooseVersionAdapter extends RecyclerView.Adapter<RcyChooseVersionAdapter.ProductFirstSpscHodler> {

    private Context mContext;
    private List<ChooseVersionBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyChooseVersionAdapter(Context context, List<ChooseVersionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ProductFirstSpscHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductFirstSpscHodler holder = new ProductFirstSpscHodler(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_choose_version_item_view, null,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductFirstSpscHodler holder, final int position) {
        holder.mTvName.setText(mList.get(position).getProjectName());

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ProductFirstSpscHodler extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final TextView mTvName;

        private OnItemClickListener mOnItemClickListener;

        public ProductFirstSpscHodler(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
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
