package com.busradeniz.detection.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.NewVersionBean;

import java.util.List;

public class RcyProductDetailsAdapter extends RecyclerView.Adapter<RcyProductDetailsAdapter.ProductFirstSpscHodler> {

    private Context mContext;
    private List<NewVersionBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyProductDetailsAdapter(Context context, List<NewVersionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ProductFirstSpscHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductFirstSpscHodler holder = new ProductFirstSpscHodler(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_create_modle_item, null,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductFirstSpscHodler holder, final int position) {
        holder.mTvName.setText(mList.get(position).getName());
        try {
            holder.mEtTopPuls.setText(mList.get(position).getDeviation().getTop().getPuls() + "");
            holder.mEtTopPuls.setSelection((mList.get(position).getDeviation().getTop().getPuls() + "").length());
            holder.mEtTopNegative.setText(mList.get(position).getDeviation().getTop().getNegative() + "");
            holder.mEtLeftNegative.setText(mList.get(position).getDeviation().getLeft().getNegative() + "");
            holder.mEtLeftPuls.setText(mList.get(position).getDeviation().getLeft().getPuls() + "");
            holder.mEtRightNegative.setText(mList.get(position).getDeviation().getRight().getNegative() + "");
            holder.mEtRightPuls.setText(mList.get(position).getDeviation().getRight().getPuls() + "");
            holder.mEtBottomPuls.setText(mList.get(position).getDeviation().getBottom().getPuls() + "");
            holder.mEtBottomNegative.setText(mList.get(position).getDeviation().getBottom().getNegative() + "");
            setData(holder, position);
        } catch (Exception e) {

        }

    }

    private void setData(ProductFirstSpscHodler holder, final int position) {

        holder.mEtBottomNegative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getBottom().setNegative(Integer.parseInt(s.toString()));
                }
            }
        });
        holder.mEtBottomPuls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getBottom().setPuls(Integer.parseInt(s.toString()));
                }
            }
        });

        holder.mEtLeftNegative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getLeft().setNegative(Integer.parseInt(s.toString()));
                }
            }
        });

        holder.mEtLeftPuls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getLeft().setPuls(Integer.parseInt(s.toString()));
                }
            }
        });
        holder.mEtRightPuls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getRight().setPuls(Integer.parseInt(s.toString()));
                }
            }
        });
        holder.mEtRightNegative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getRight().setNegative(Integer.parseInt(s.toString()));
                }
            }
        });
        holder.mEtTopNegative.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getTop().setNegative(Integer.parseInt(s.toString()));
                }
            }
        });
        holder.mEtTopPuls.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    mList.get(position).getDeviation().getTop().setPuls(Integer.parseInt(s.toString()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class ProductFirstSpscHodler extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final TextView mTvName;
        private final EditText mEtLeftPuls;
        private final EditText mEtLeftNegative;
        private final EditText mEtRightPuls;
        private final EditText mEtRightNegative;
        private final EditText mEtTopPuls;
        private final EditText mEtTopNegative;
        private final EditText mEtBottomPuls;
        private final EditText mEtBottomNegative;
        private OnItemClickListener mOnItemClickListener;
        private final RelativeLayout mRlNameItem;

        public ProductFirstSpscHodler(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mEtLeftPuls = itemView.findViewById(R.id.et_left_puls);
            mEtLeftNegative = itemView.findViewById(R.id.et_left_negative);
            mEtRightPuls = itemView.findViewById(R.id.et_et_right_puls);
            mEtRightNegative = itemView.findViewById(R.id.et_right_negative);
            mEtTopPuls = itemView.findViewById(R.id.et_top_puls);
            mEtTopNegative = itemView.findViewById(R.id.et_top_negative);
            mEtBottomPuls = itemView.findViewById(R.id.et_bottom_puls);
            mEtBottomNegative = itemView.findViewById(R.id.et_bottom_negative);
            mRlNameItem = itemView.findViewById(R.id.rl_name_item);
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
