package com.busradeniz.detection.utils;

import android.os.SystemClock;

import com.busradeniz.detection.R;
import com.busradeniz.detection.message.CallBackInterface;
import com.busradeniz.detection.message.LogManager;
import com.busradeniz.detection.message.RecvMessage;
import com.licheedev.myutils.LogPlus;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 读串口线程
 */
public class SerialReadThread extends Thread {

    private static final String TAG = "SerialReadThread";

    private CallBackInterface mCallBackInterface;

    private BufferedInputStream mInputStream;

    private String mCommand;

    private String[]  mReadList = UiUtils.getStringArray(R.array.read_list);

    public SerialReadThread(InputStream is) {
        mInputStream = new BufferedInputStream(is);
    }


    public void setCallBack(CallBackInterface callBack){
        this.mCallBackInterface = callBack;
    }


    @Override
    public void run() {
        byte[] received = new byte[1024];
        int size;

        LogPlus.e("开始读线程");

        while (true) {

            if (Thread.currentThread().isInterrupted()) {
                break;
            }
            try {

                int available = mInputStream.available();

                if (available > 0) {
                    size = mInputStream.read(received);
                    if (size > 0) {
                        onDataReceive(received, size);
                    }
                } else {
                    // 暂停一点时间，免得一直循环造成CPU占用率过高
                    SystemClock.sleep(1);
                }

            } catch (IOException e) {
                LogPlus.e("读取数据失败", e);
            }
            //Thread.yield();
        }

        LogPlus.e("结束读进程");
    }

    /**
     * 处理获取到的数据
     *
     * @param received
     * @param size
     */
    private void onDataReceive(byte[] received, int size) {
        // TODO: 2018/3/22 解决粘包、分包等
        String hexStr = ByteUtil.bytes2HexStr(received, 0, size);

//      if(mCommand.equals(mReadList[0])){        //设备运行
//          if(mCallBackInterface!=null)
//          mCallBackInterface.runCallBack(hexStr);
//
//      }else if(mCommand.equals(UiUtils.getString(R.string.get_three_color_night_status))){  //三色灯
//          if(mCallBackInterface!=null)
//          mCallBackInterface.tricolorLampCallBack(hexStr);
//
//      }else if(mCommand.equals(UiUtils.getString(R.string.get_left_night_status))){  //左灯
//          if(mCallBackInterface!=null)
//          mCallBackInterface.leftNightCallBack(hexStr);
//
//      }else if(mCommand.equals(UiUtils.getString(R.string.get_right_night_status))){  //右灯
//          if(mCallBackInterface!=null)
//          mCallBackInterface.rightNightCallBack(hexStr);
//      }else{
//          if(mCallBackInterface!=null)
//              mCallBackInterface.runCallBack(hexStr);
//      }

        LogManager.instance().post(new RecvMessage(hexStr));
    }

    /**
     * 停止读线程
     */
    public void close() {

        try {
            mInputStream.close();
        } catch (IOException e) {
            LogPlus.e("异常", e);
        } finally {
            super.interrupt();
        }
    }

    public void setCommand(String command) {
        this.mCommand = command;
    }
}
