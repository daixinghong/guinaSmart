package com.busradeniz.detection.check.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.serialport.SerialPortFinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.busradeniz.detection.R;
import com.busradeniz.detection.message.RecvMessage;
import com.busradeniz.detection.utils.Constant;
import com.busradeniz.detection.utils.Device;
import com.busradeniz.detection.utils.SerialPortManager;
import com.busradeniz.detection.utils.SerialReadThread;
import com.busradeniz.detection.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment implements View.OnClickListener {


    private RadioButton mRbOn;
    private RadioButton mRbOff;
    private EditText mEt1;
    private EditText mEt2;
    private EditText mEt3;
    private EditText mEt4;
    private EditText mEtLength;
    private RelativeLayout mRlSendData;
    private RelativeLayout mRlReadData;
    private List<String> mStringList = new ArrayList<>();
    private String[] mDevices;
    private Device mDevice;
    private boolean mOpened;
    private SerialReadThread mReadThread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View inflate = inflater.inflate(R.layout.fragment_statiscs_view, null);

        initView(inflate);

        initData();

        initEvent();

        return inflate;
    }

    private void initEvent() {
        mRbOn.setChecked(true);
        mRlReadData.setOnClickListener(this);
        mRlSendData.setOnClickListener(this);
    }

    private void initData() {

        SerialPortFinder serialPortFinder = new SerialPortFinder();

        mDevices = serialPortFinder.getAllDevicesPath();

        if (mDevices.length == 0) {
            mDevices = new String[]{
                    getString(R.string.no_serial_device)
            };
        }
        mDevice = new Device(mDevices[0], Constant.BAUDRATES);


        mOpened = SerialPortManager.instance().open(mDevice) != null;

        if (mOpened) {
            ToastUtils.showTextToast("成功");
        } else {
            ToastUtils.showTextToast("失败");
        }


    }

    private void initView(View inflate) {

        mRbOn = inflate.findViewById(R.id.rb_on);
        mRbOff = inflate.findViewById(R.id.rb_off);
        mEt1 = inflate.findViewById(R.id.et_1);
        mEt2 = inflate.findViewById(R.id.et_2);
        mEt3 = inflate.findViewById(R.id.et_3);
        mEt4 = inflate.findViewById(R.id.et_4);
        mEtLength = inflate.findViewById(R.id.et_length);
        mRlSendData = inflate.findViewById(R.id.rl_send_data);
        mRlReadData = inflate.findViewById(R.id.rl_send_read_data);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_send_data:
                mStringList.clear();
                mStringList.add("02");  //添加开始指令
                if (mRbOn.isChecked()) {  //on
                    mStringList.add("45");
                    mStringList.add("37");
                } else {
                    mStringList.add("45");  //of
                    mStringList.add("38");
                }
                //添加地址值
                mStringList.add(mEt1.getText().toString().trim());
                mStringList.add(mEt2.getText().toString().trim());
                mStringList.add(mEt3.getText().toString().trim());
                mStringList.add(mEt4.getText().toString().trim());

//                String length = mEtLength.getText().toString().trim();

//                if (length.length() > 1) {
//                    mStringList.add("3" + length.substring(0, 1));
//                    mStringList.add("3" + length.substring(1, 2));
//                } else {
//                    mStringList.add("30");
//                    mStringList.add("3" + length);
//                }

                mStringList.add("03");      //结束位


                List<String> first = new ArrayList<>();
                List<String> two = new ArrayList<>();
                for (int i = 0; i < mStringList.size(); i++) {

                    if (i == 0)
                        continue;
                    if (i == 1 || i == 2) {
                        first.add(mStringList.get(i));
                        continue;
                    }
                    two.add(mStringList.get(i));

                }

                int number1 = 0;
                int number2 = 0;

                for (int i = 0; i < first.size(); i++) {
                    number1 += Integer.parseInt(first.get(i).substring(0, 1));  //把这两个数的首尾相加
                    number2 += Integer.parseInt(first.get(i).substring(1, 2));
                }

                String string1 = Integer.toHexString(number1);  // 转换成16进制

                String string2 = Integer.toHexString(number2);  //转换成16进制

                String firstHex = string1 + string2;            //将两个16进制数以字符串形式连接起来

                String firstInteger = new BigInteger(firstHex, 16).toString();  //讲16进制数转为10进制


                int number3 = 0;
                int number4 = 0;
                for (int i = 0; i < two.size(); i++) {
                    number3 += Integer.parseInt(two.get(i).substring(0, 1));
                    number4 += Integer.parseInt(two.get(i).substring(1, 2));
                }


                String string3 = Integer.toHexString(number3); // 转换成16进制

                String string4 = Integer.toHexString(number4);     //转换成16进制

                String twoHex = string3 + string4;  //将两个16进制数以字符串形式连接起来

                String twoInteger = new BigInteger(twoHex, 16).toString(); //讲16进制数转为10进制

                String result = Integer.toHexString(Integer.parseInt(firstInteger) + Integer.parseInt(twoInteger)); //获取结果

                if (result.length() > 2) {
                    result = result.substring(result.length() - 2, result.length());
                }

                stringToAscii(result);      //string转ASCII码

                mStringList.add(stringToAscii(result).split(",")[0]);       //校验码
                mStringList.add(stringToAscii(result).split(",")[1]);       //校验码

                Log.e("vivi", "onClick: " + mStringList);


                break;
            case R.id.rl_send_read_data:
                sendData();

                break;
            default:


                break;
        }

    }


    /**
     * 发送数据
     */
    private void sendData() {

        String text = "";

//        if (TextUtils.isEmpty(text) || text.length() % 2 != 0) {
//            ToastUtils.showTextToast("无效数据");
//            return;
//        }

//        SerialPortManager.instance().sendCommand(text);


        EventBus.getDefault().postSticky(new RecvMessage("Mr.sorrow"));

    }




    /**
     * 字符串转ascii码
     *
     * @param val
     * @return
     */
    public static String stringToAscii(String val) {

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < val.length(); i++) {
            char c = val.charAt(i);
            builder.append((byte) c).append(",");
        }

        return builder.toString();
    }

}
