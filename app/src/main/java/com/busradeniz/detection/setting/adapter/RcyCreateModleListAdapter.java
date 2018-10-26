package com.busradeniz.detection.setting.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.busradeniz.detection.R;
import com.busradeniz.detection.bean.NewVersionBean;

import java.util.List;

public class RcyCreateModleListAdapter extends RecyclerView.Adapter<RcyCreateModleListAdapter.ProductFirstSpscHodler> {


    private Context mContext;
    private List<NewVersionBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyCreateModleListAdapter(Context context, List<NewVersionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ProductFirstSpscHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        ProductFirstSpscHodler holder = new ProductFirstSpscHodler(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_create_modle_item, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProductFirstSpscHodler holder, final int position) {
        holder.mTvName.setText(mList.get(position).getName());

        holder.mTvLeftStand.setText(mList.get(position).getLocation().getLeft() + "");
        holder.mTvRightStand.setText(mList.get(position).getLocation().getRight() + "");
        holder.mTvTopStand.setText(mList.get(position).getLocation().getTop() + "");
        holder.mTvBottomStand.setText(mList.get(position).getLocation().getBottom() + "");
        holder.mCbChoose.setChecked(mList.get(position).isStatus());
//        if (mList.get(position).isStatus()) {
//            holder.mRlNameItem.setBackgroundColor(mContext.getResources().getColor(R.color.color_358fc1));
//        } else {
//            holder.mRlNameItem.setBackgroundColor(mContext.getResources().getColor(R.color.ffffff));
//        }

        holder.mCbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mList.get(position).setStatus(isChecked);
            }
        });


        try {

            if (holder.mEtLeftPuls.getTag() instanceof TextWatcher) {
                holder.mEtLeftPuls.removeTextChangedListener((TextWatcher) (holder.mEtLeftPuls.getTag()));
            }
            if (holder.mEtLeftNegative.getTag() instanceof TextWatcher) {
                holder.mEtLeftNegative.removeTextChangedListener((TextWatcher) (holder.mEtLeftNegative.getTag()));
            }
            if (holder.mEtRightPuls.getTag() instanceof TextWatcher) {
                holder.mEtRightPuls.removeTextChangedListener((TextWatcher) (holder.mEtRightPuls.getTag()));
            }
            if (holder.mEtRightNegative.getTag() instanceof TextWatcher) {
                holder.mEtRightNegative.removeTextChangedListener((TextWatcher) (holder.mEtRightNegative.getTag()));
            }
            if (holder.mEtTopPuls.getTag() instanceof TextWatcher) {
                holder.mEtTopPuls.removeTextChangedListener((TextWatcher) (holder.mEtTopPuls.getTag()));
            }
            if (holder.mEtTopNegative.getTag() instanceof TextWatcher) {
                holder.mEtTopNegative.removeTextChangedListener((TextWatcher) (holder.mEtTopNegative.getTag()));
            }
            if (holder.mEtBottomNegative.getTag() instanceof TextWatcher) {
                holder.mEtBottomNegative.removeTextChangedListener((TextWatcher) (holder.mEtBottomNegative.getTag()));
            }
            if (holder.mEtBottomPuls.getTag() instanceof TextWatcher) {
                holder.mEtBottomPuls.removeTextChangedListener((TextWatcher) (holder.mEtBottomPuls.getTag()));
            }

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

        TextWatcher bottomNegativeTatcher = new TextWatcher() {
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
        };
        holder.mEtBottomNegative.addTextChangedListener(bottomNegativeTatcher);
        holder.mEtBottomNegative.setTag(bottomNegativeTatcher);


        TextWatcher bottomPulsWatcher = new TextWatcher() {
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
        };

        holder.mEtBottomPuls.addTextChangedListener(bottomPulsWatcher);
        holder.mEtBottomPuls.setTag(bottomPulsWatcher);


        TextWatcher leftNegativeWatcher = new TextWatcher() {
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
        };

        holder.mEtLeftNegative.addTextChangedListener(leftNegativeWatcher);
        holder.mEtLeftNegative.setTag(leftNegativeWatcher);


        TextWatcher leftPulsWatcher = new TextWatcher() {
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
        };

        holder.mEtLeftPuls.addTextChangedListener(leftPulsWatcher);
        holder.mEtLeftPuls.setTag(leftPulsWatcher);


        TextWatcher rightPulsWatcher = new TextWatcher() {
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
        };

        holder.mEtRightPuls.addTextChangedListener(rightPulsWatcher);
        holder.mEtRightPuls.setTag(rightPulsWatcher);


        TextWatcher rightNegativeWatcher = new TextWatcher() {
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
        };

        holder.mEtRightNegative.addTextChangedListener(rightNegativeWatcher);
        holder.mEtRightNegative.setTag(rightNegativeWatcher);


        TextWatcher topNegativeWatcher = new TextWatcher() {
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
        };

        holder.mEtTopNegative.addTextChangedListener(topNegativeWatcher);
        holder.mEtTopNegative.setTag(topNegativeWatcher);


        TextWatcher topPulsWatcher = new TextWatcher() {
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
        };

        holder.mEtTopPuls.addTextChangedListener(topPulsWatcher);
        holder.mEtTopPuls.setTag(topPulsWatcher);
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
        private final TextView mTvLeftStand;
        private final TextView mTvRightStand;
        private final TextView mTvTopStand;
        private final TextView mTvBottomStand;
        private final CheckBox mCbChoose;

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
            mTvLeftStand = itemView.findViewById(R.id.tv_left_stand);
            mTvRightStand = itemView.findViewById(R.id.tv_right_stand);
            mTvTopStand = itemView.findViewById(R.id.tv_top_stand);
            mTvBottomStand = itemView.findViewById(R.id.tv_bottom_stand);
            mCbChoose = itemView.findViewById(R.id.cb_choose);

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
