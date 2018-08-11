package adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.busradeniz.detection.tensorflow.Classifier;
import com.busradeniz.detection.R;

import java.text.DecimalFormat;
import java.util.List;

public class LvAdapter extends BaseAdapter {

    private Context mContext;
    private List<Classifier.Recognition> mList;
    private final DecimalFormat mMDf;

    public LvAdapter(Context context, List<Classifier.Recognition> list) {
        mMDf = new DecimalFormat("######0.00");
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoder hoder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item, null);
            hoder = new ViewHoder(convertView);
            convertView.setTag(hoder);
        } else {
            hoder = (ViewHoder) convertView.getTag();
        }

        hoder.mTvData.setText(mList.get(position).getTitle());
        hoder.mTvSame.setText(mMDf.format(mList.get(position).getConfidence()));

        return convertView;
    }


    class ViewHoder {

        private final TextView mTvData;
        private final TextView mTvSame;

        public ViewHoder(View view) {
            mTvData = view.findViewById(R.id.tv_data);
            mTvSame = view.findViewById(R.id.tv_same);
        }

    }


}
