package com.busradeniz.detection.check.view.adapter;

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
import com.busradeniz.detection.bean.ParmsListBean;
import com.busradeniz.detection.utils.PlcCommandUtils;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.ToastUtils;
import com.busradeniz.detection.utils.UiUtils;

import java.util.List;

public class RcyParmsListAdapter extends RecyclerView.Adapter<RcyParmsListAdapter.ParmsHolder> {

    private Context mContext;
    private List<ParmsListBean> mList;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void setOnItemClickListener(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public RcyParmsListAdapter(Context context, List<ParmsListBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public ParmsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ParmsHolder holder = new ParmsHolder(LayoutInflater.from(
                mContext).inflate(R.layout.rcy_parms_item, parent,
                false), mOnItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ParmsHolder holder, final int position) {
        holder.mTvName.setText(mList.get(position).getName());
        if (holder.mEtInput.getTag() instanceof TextWatcher) {
            holder.mEtInput.removeTextChangedListener((TextWatcher) (holder.mEtInput.getTag()));
        }
        holder.mEtInput.setText(mList.get(position).getParms());

        //应用设定
        holder.mRlUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String command = PlcCommandUtils.plcCommand2String(mList.get(position).getCommand(), true, Integer.parseInt(holder.mEtInput.getText().toString().trim()));
                SerialPortManager.instance().sendCommand(command);
                ToastUtils.showTextToast(UiUtils.getString(R.string.use_success));
            }
        });

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
                    mList.get(position).setParms(s.toString());
                }
            }
        };
        holder.mEtInput.addTextChangedListener(bottomNegativeTatcher);
        holder.mEtInput.setTag(bottomNegativeTatcher);

    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ParmsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private OnItemClickListener mOnItemClickListener;
        private final TextView mTvName;
        private final EditText mEtInput;
        private final RelativeLayout mRlUse;

        public ParmsHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            mTvName = itemView.findViewById(R.id.tv_name);
            mEtInput = itemView.findViewById(R.id.et_input);
            mRlUse = itemView.findViewById(R.id.rl_use);

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
