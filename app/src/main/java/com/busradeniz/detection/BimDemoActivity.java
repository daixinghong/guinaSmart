package com.busradeniz.detection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bimsdk.usb.UsbConnect;
import com.bimsdk.usb.io.OnDataReceive;


public class BimDemoActivity extends Activity implements OnClickListener{

	private Button mClearBt;
	private EditText mDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bim_demo);

		initView();

		getData();
//		if (!UsbConnect.isConnect(this)) {
//			UsbConnect.Connect(this);
//		}
		UsbConnect.Connect(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	//关闭程序掉用处理部分
	public void onDestroy(){
		super.onDestroy();

	}
	private void initView() {
		mClearBt = (Button) findViewById(R.id.clear_bt);
		mClearBt.setOnClickListener(this);
        mDate= (EditText) findViewById(R.id.ReadValues);

	}


	/***
	 * 扫描完成后数据回调（接受数据）
	 */
	private  void getData() {
		UsbConnect.SetOnDataReceive(new OnDataReceive() {
			@Override
			public void DataReceive(byte[] data, boolean flg) {
				mDate.append(new String(data));
				Toast.makeText(BimDemoActivity.this, "saodaole", Toast.LENGTH_SHORT).show();

			}
		});

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.clear_bt:
				UsbConnect.Connect(this);
				break;
			default:
				break;
		}
	}

}
