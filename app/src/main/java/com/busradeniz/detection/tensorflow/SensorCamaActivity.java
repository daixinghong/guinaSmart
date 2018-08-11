package com.busradeniz.detection.tensorflow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.busradeniz.detection.R;
import com.busradeniz.detection.SelectFileDialog;
import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by LENOVO on 2018-03-07.
 * 单个mo显示详情
 */

public class SensorCamaActivity extends Activity implements View.OnClickListener {

    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private RelativeLayout mRlBack;
    private ImageView mImageView;


    //    jzxx
// j2xx
    public static D2xxManager ftD2xx = null;
    FT_Device ftDev;
    int DevCount = -1;
    int currentPortIndex = -1;
    int portIndex = -1;

    enum DeviceStatus {
        DEV_NOT_CONNECT,
        DEV_NOT_CONFIG,
        DEV_CONFIG
    }

    boolean INTERNAL_DEBUG_TRACE = false; // Toast message for debug

    // menu item
    Menu myMenu;
    final int MENU_CONTENT_FORMAT = Menu.FIRST;
    final int MENU_FONT_SIZE = Menu.FIRST + 1;
    final int MENU_SAVE_CONTENT_DATA = Menu.FIRST + 2;
    final int MENU_CLEAN_SCREEN = Menu.FIRST + 3;
    final int MENU_ECHO = Menu.FIRST + 4;
    final int MENU_HELP = Menu.FIRST + 5;
    final int MENU_SETTING = Menu.FIRST + 6;

    final String[] contentFormatItems = {"Character", "Hexadecimal"};
    final String[] fontSizeItems = {"5", "6", "7", "8", "10", "12", "14", "16", "18", "20"};
    final String[] echoSettingItems = {"On", "Off"};

    // log tag
    final String TT = "Trace";
    final String TXS = "XM-Send";
    final String TXR = "XM-Rec";
    final String TYS = "YM-Send";
    final String TYR = "YM-Rec";
    final String TZS = "ZM-Send";
    final String TZR = "ZM-Rec";

    // handler event
    final int UPDATE_TEXT_VIEW_CONTENT = 0;
    final int UPDATE_SEND_FILE_STATUS = 1;
    final int UPDATE_SEND_FILE_DONE = 2;
    final int ACT_SELECT_SAVED_FILE_NAME = 3;
    final int ACT_SELECT_SAVED_FILE_FOLDER = 4;
    final int ACT_SAVED_FILE_NAME_CREATED = 5;
    final int ACT_SELECT_SEND_FILE_NAME = 6;
    final int MSG_SELECT_FOLDER_NOT_FILE = 7;
    final int MSG_XMODEM_SEND_FILE_TIMEOUT = 8;
    final int UPDATE_MODEM_RECEIVE_DATA = 9;
    final int UPDATE_MODEM_RECEIVE_DATA_BYTES = 10;
    final int UPDATE_MODEM_RECEIVE_DONE = 11;
    final int MSG_MODEM_RECEIVE_PACKET_TIMEOUT = 12;
    final int ACT_MODEM_SELECT_SAVED_FILE_FOLDER = 13;
    final int MSG_MODEM_OPEN_SAVE_FILE_FAIL = 14;
    final int MSG_YMODEM_PARSE_FIRST_PACKET_FAIL = 15;
    final int MSG_FORCE_STOP_SEND_FILE = 16;
    final int UPDATE_ASCII_RECEIVE_DATA_BYTES = 17;
    final int UPDATE_ASCII_RECEIVE_DATA_DONE = 18;
    final int MSG_FORCE_STOP_SAVE_TO_FILE = 19;
    final int UPDATE_ZMODEM_STATE_INFO = 20;
    final int ACT_ZMODEM_AUTO_START_RECEIVE = 21;

    final int MSG_SPECIAL_INFO = 98;
    final int MSG_UNHANDLED_CASE = 99;

    final byte XON = 0x11;    /* Resume transmission */
    final byte XOFF = 0x13;    /* Pause transmission */

    // strings of file transfer protocols
    final String[] protocolItems = {"ASCII", "XModem-CheckSum", "XModem-CRC", "XModem-1KCRC", "YModem", "ZModem"};
    String currentProtocol;

    final int MODE_GENERAL_UART = 0;
    final int MODE_X_MODEM_CHECKSUM_RECEIVE = 1;
    final int MODE_X_MODEM_CHECKSUM_SEND = 2;
    final int MODE_X_MODEM_CRC_RECEIVE = 3;
    final int MODE_X_MODEM_CRC_SEND = 4;
    final int MODE_X_MODEM_1K_CRC_RECEIVE = 5;
    final int MODE_X_MODEM_1K_CRC_SEND = 6;
    final int MODE_Y_MODEM_1K_CRC_RECEIVE = 7;
    final int MODE_Y_MODEM_1K_CRC_SEND = 8;
    final int MODE_Z_MODEM_RECEIVE = 9;
    final int MODE_Z_MODEM_SEND = 10;
    final int MODE_SAVE_CONTENT_DATA = 11;

    int transferMode = MODE_GENERAL_UART;
    int tempTransferMode = MODE_GENERAL_UART;

    // X, Y, Z modem - UART MODE: Asynchronous�B8 data��bits�Bno parity�Bone stop��bit
    // X modem + //
    final int PACTET_SIZE_XMODEM_CHECKSUM = 132; // SOH,pkt,~ptk,128data,checksum
    final int PACTET_SIZE_XMODEM_CRC = 133;     // SOH,pkt,~ptk,128data,CRC-H,CRC-L
    final int PACTET_SIZE_XMODEM_1K_CRC = 1029;     // STX,pkt,~ptk,1024data,CRC-H,CRC-L

    final byte SOH = 1;    /* Start Of Header */
    final byte STX = 2;    /* Start Of Header 1K */
    final byte EOT = 4;    /* End Of Transmission */
    final byte ACK = 6;    /* ACKnowlege */
    final byte NAK = 0x15; /* Negative AcKnowlege */
    final byte CAN = 0x18; /* Cancel */
    final byte CHAR_C = 0x43; /* Character 'C' */
    final byte CHAR_G = 0x47; /* Character 'G' */

    final int DATA_SIZE_128 = 128;
    final int DATA_SIZE_256 = 256;
    final int DATA_SIZE_512 = 512;
    final int DATA_SIZE_1K = 1024;

    final int MODEM_BUFFER_SIZE = 2048;
    int[] modemReceiveDataBytes;
    byte[] modemDataBuffer;
    byte[] zmDataBuffer;
    byte receivedPacketNumber = 1;

    boolean bModemGetNak = false;
    boolean bModemGetAck = false;
    boolean bModemGetCharC = false;
    boolean bModemGetCharG = false;

    int totalModemReceiveDataBytes = 0;
    int totalErrorCount = 0;
    boolean bDataReceived = false;
    boolean bReceiveFirstPacket = false;
    boolean bDuplicatedPacket = false;

    boolean bUartModeTaskSet = true;
    boolean bReadDataProcess = true;
    // X modem -//

    // Y modem +//
    final int Y_MODEM_WAIT_ASK_SEND_FILE = 0;
    final int Y_MODEM_SEND_FILE_INFO_PACKET = 1;
    final int Y_MODEM_SEND_FILE_INFO_PACKET_WAIT_ACK = 2;
    final int Y_MODEM_START_SEND_FILE = 3;
    final int Y_MODEM_START_SEND_FILE_WAIT_ACK = 4;
    final int Y_MODEM_START_SEND_FILE_RESEND = 5;
    final int Y_MODEM_SEND_EOT_PACKET = 6;
    final int Y_MODEM_SEND_EOT_PACKET_WAIT_ACT = 7;
    final int Y_MODEM_SEND_LAST_END_PACKET = 8;
    final int Y_MODEM_SEND_LAST_END_PACKET_WAIT_ACK = 9;
    final int Y_MODEM_SEND_FILE_DONE = 10;

    final int DATA_NONE = 0;
    final int DATA_ACK = 1;
    final int DATA_CHAR_C = 2;
    final int DATA_NAK = 3;

    int ymodemState = 0;
    String modemFileName;
    String modemFileSize;
    int modemRemainData = 0;
    // Y modem -//

    // Z modem +//
    final int ZCRC_HEAD_SIZE = 4;

    final byte ZPAD = 0x2A; // '*' 052 Padding character begins frames
    final byte ZDLE = 0x18;
    final byte ZDLEE = ZDLE ^ 0100;   /* Escaped ZDLE as transmitted */

    final byte ZBIN = 0x41;        // 'A' Binary frame indicator (CRC-16)
    final byte ZHEX = 0x42;        // 'B' HEX frame indicator
    final byte ZBIN32 = 0x43;    // 'C' Binary frame with 32 bit CRC

    final byte LF = 0x0A;
    final byte CR = 0x0D;

    final int ZRQINIT = 0;   /* Request receive init */
    final int ZRINIT = 1;   /* Receive init */
    final int ZSINIT = 2;    /* Send init sequence (optional) */
    final int ZACK = 3;      /* ACK to above */
    final int ZFILE = 4;     /* File name from sender */
    final int ZSKIP = 5;     /* To sender: skip this file */
    final int ZNAK = 6;      /* Last packet was garbled */
    final int ZABORT = 7;    /* Abort batch transfers */
    final int ZFIN = 8;      /* Finish session */
    final int ZRPOS = 9;     /* Resume data trans at this position */
    final int ZDATA = 10;    /* Data packet(s) follow */
    final int ZDATA_HEADER = 21;
    final int ZFIN_ACK = 22;

    final int ZEOF = 11;     /* End of file */
    final int ZFERR = 12;    /* Fatal Read or Write error Detected */
    final int ZCRC = 13;     /* Request for file CRC and response */
    final int ZCHALLENGE = 14;   /* Receiver's Challenge */
    final int ZCOMPL = 15;   /* Request is complete */
    final int ZCAN = 16;     /* Other end canned session with CAN*5 */
    final int ZFREECNT = 17; /* Request for free bytes on filesystem */
    final int ZCOMMAND = 18; /* Command from sending program */
    final int ZSTDERR = 19;  /* Output to standard error, data follows */
    final int ZOO = 20;

    final int ZCRCE = 0x68; // no data
    final int ZCRCG = 0x69; // more data
    final int ZCRCW = 0x6B; // file info end

    final int ZDLE_END_SIZE_4 = 4; // zdle ZCRC? crc1 crc2
    final int ZDLE_END_SIZE_5 = 5; // zdle ZCRC? zdle crc1 crc2 || zdle ZCRC? crc1 zdle crc2
    final int ZDLE_END_SIZE_6 = 6; // zdle ZCRC? zdle crc1 zdle crc2

    final int ZF0 = 3;   /* First flags byte */
    final int ZF1 = 2;
    final int ZF2 = 1;
    final int ZF3 = 0;
    final int ZP0 = 0;   /* Low order 8 bits of position */
    final int ZP1 = 1;
    final int ZP2 = 2;
    final int ZP3 = 3;   /* High order 8 bits of file position */

    int zmodemState = 0;

    // fixed pattern, used to check ZRQINIT
    final int ZMS_0 = 0;
    final int ZMS_1 = 1; // r
    final int ZMS_2 = 2; // z
    final int ZMS_3 = 3; // \r
    final int ZMS_4 = 4; // ZPAD (ZRQINIT)
    final int ZMS_5 = 5; // ZPAD
    final int ZMS_6 = 6; // ZDLE
    final int ZMS_7 = 7; // ZHEX
    final int ZMS_8 = 8; // 0x30
    final int ZMS_9 = 9; // 0x30
    final int ZMS_10 = 10; // 0x30
    final int ZMS_11 = 11; // 0x30
    final int ZMS_12 = 12; // 0x30
    final int ZMS_13 = 13; // 0x30
    final int ZMS_14 = 14; // 0x30
    final int ZMS_15 = 15; // 0x30
    final int ZMS_16 = 16; // 0x30
    final int ZMS_17 = 17; // 0x30
    final int ZMS_18 = 18; // 0x30
    final int ZMS_19 = 19; // 0x30
    final int ZMS_20 = 20; // 0x30
    final int ZMS_21 = 21; // 0x30 (14th 0x30)
    final int ZMS_22 = 22; // 0x0D
    final int ZMS_23 = 23; // 0x0A
    final int ZMS_24 = 24; // 0x11
    int zmStartState = 0;
    // Z modem -//

    // general data count
    int totalReceiveDataBytes = 0;
    int totalUpdateDataBytes = 0;

    SelectFileDialog fileDialog;
    File mPath = new File(android.os.Environment.getExternalStorageDirectory() + "//DIR//");
    File fGetFile = null;

    static RelativeLayout mMenuSetting;
    static RelativeLayout mMenuKey;

    long back_button_click_time;
    boolean bBackButtonClick = false;


    // thread to read the data
    HandlerThread handlerThread; // update data to UI
    ReadThread readThread; // read data from USB

    // graphical objects
    TextView uartInfo;
    TextView contentFormatText;
    ScrollView scrollView;
    //    TextView readText;
    EditText writeText;
    Spinner baudSpinner;
    Spinner stopSpinner;
    Spinner dataSpinner;
    Spinner paritySpinner;
    Spinner flowSpinner;
    Spinner portSpinner;
    ArrayAdapter<CharSequence> baudAdapter;
    ArrayAdapter<CharSequence> portAdapter;

    Button writeButton, configButton, mExit;
    Button ctrlCButton, escButton;

    boolean bSendButtonClick = false;
    boolean bLogButtonClick = false;
    boolean bSendHexData = false;

    CharSequence contentCharSequence; // contain entire text content
    boolean bContentFormatHex = false;
    int contentFontSize = 12;
    boolean bWriteEcho = true;

    // show information message while send data by tapping "Write" button in hex content format
    int timesMessageHexFormatWriteData = 0;

    // note: when this values changed, need to check main.xml - android:id="@+id/ReadValues - android:maxLines="5000"
    final int TEXT_MAX_LINE = 1000;

    // variables
    final int UI_READ_BUFFER_SIZE = 10240; // Notes: 115K:1440B/100ms, 230k:2880B/100ms
    byte[] writeBuffer;
    byte[] readBuffer;
    char[] readBufferToChar;
    int actualNumBytes;

    int baudRate; /* baud rate */
    byte stopBit; /* 1:1stop bits, 2:2 stop bits */
    byte dataBit; /* 8:8bit, 7: 7bit */
    byte parity; /* 0: none, 1: odd, 2: even, 3: mark, 4: space */
    byte flowControl; /* 0:none, 1: CTS/RTS, 2:DTR/DSR, 3:XOFF/XON */
    public Context global_context;
    boolean uart_configured = false;

    String uartSettings = "";

    //public static final int maxReadLength = 256;
    byte[] usbdata;
    char[] readDataToText;
    public int iavailable = 0;

    // file access//
    FileInputStream inputstream;
    FileOutputStream outputstream;

    FileWriter file_writer;
    FileReader file_reader;
    FileInputStream fis_open;
    FileOutputStream fos_save;
    BufferedOutputStream buf_save;
    boolean WriteFileThread_start = false;

    String fileNameInfo;
    String sFileName;
    int iFileSize = 0;
    int sendByteCount = 0;
    long start_time, end_time;
    long cal_time_1, cal_time_2;

    // data buffer
    byte[] writeDataBuffer;
    byte[] readDataBuffer; /* circular buffer */

    int iTotalBytes;
    int iReadIndex;

    final int MAX_NUM_BYTES = 65536;

    boolean bReadTheadEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        {
            try {
                ftD2xx = D2xxManager.getInstance(this);
            } catch (D2xxManager.D2xxException e) {
                Log.e("FTDI_HT", "getInstance fail!!");
            }
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sensor_info);
            intView();
            intDate();
        }

    }

    private void intDate() {
        cameraView = (CameraView) findViewById(R.id.cameraView);
        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = (Button) findViewById(R.id.btnToggleCamera);
        btnDetectObject = (Button) findViewById(R.id.btnDetectObject);
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, false);//400,300实际传过去的图片大小
                imageViewResult.setImageBitmap(bitmap);
                final Bitmap finalBitmap = bitmap;
                imageViewResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SensorCamaActivity.this, DetectorActivity.class);
                        intent.putExtra("BITMAP", finalBitmap); //这里可以放一个bitmap
                        startActivity(intent);

                    }
                });

            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });
        //拍照键
        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });


    }

    private void intView() {

//        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);//顶部titile返回键


//        mRlBack.setOnClickListener(this);

        //j2xx

        global_context = this;

        // init modem variables
        modemReceiveDataBytes = new int[1];
        modemReceiveDataBytes[0] = 0;
        modemDataBuffer = new byte[MODEM_BUFFER_SIZE];
        zmDataBuffer = new byte[MODEM_BUFFER_SIZE];

        // file explore settings:
        fileDialog = new SelectFileDialog(this, handler, mPath);
        fileDialog.setCanceledOnTouchOutside(false);
        fileDialog.addFileListener(new SelectFileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.d(getClass().getName(), "selected file " + file.toString());
                fGetFile = file;
            }
        });


        mMenuSetting = ((RelativeLayout) findViewById(R.id.menuSettings));
        mMenuSetting.setVisibility(View.GONE);

        mMenuKey = ((RelativeLayout) findViewById(R.id.menuSpecialKey));
        mMenuKey.setVisibility(View.GONE);

        // init UI objects


        uartInfo = (TextView) findViewById(R.id.UartInfo);

//		scrollView = (ScrollView) findViewById(R.id.ReadField);
//        readText = (TextView) findViewById(R.id.ReadValues);

        configButton = (Button) findViewById(R.id.ConfigButton);
//        mExit = (Button) findViewById(R.id.exit);
//
        ctrlCButton = (Button) findViewById(R.id.keyCtrlC);
        escButton = (Button) findViewById(R.id.keyESC);

        /* allocate buffer */
        writeBuffer = new byte[512];
        readBuffer = new byte[UI_READ_BUFFER_SIZE];
        readBufferToChar = new char[UI_READ_BUFFER_SIZE];
        readDataBuffer = new byte[MAX_NUM_BYTES];
        actualNumBytes = 0;

        // start main text area read thread
        handlerThread = new HandlerThread(handler);
        handlerThread.start();

        /* setup the baud rate list*/
        baudSpinner = (Spinner) findViewById(R.id.baudRateValue);
        baudAdapter = ArrayAdapter.createFromResource(this,
                R.array.baud_rate_1, R.layout.my_spinner_textview);
        baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        baudSpinner.setAdapter(baudAdapter);
        baudSpinner.setSelection(4);
        baudRate = 9600;

        /* stop bits */
        stopSpinner = (Spinner) findViewById(R.id.stopBitValue);
        ArrayAdapter<CharSequence> stopAdapter = ArrayAdapter.createFromResource(this,
                R.array.stop_bits, R.layout.my_spinner_textview);
        stopAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        stopSpinner.setAdapter(stopAdapter);
        stopBit = 1;

        /* data bits */
        dataSpinner = (Spinner) findViewById(R.id.dataBitValue);
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(this,
                R.array.data_bits, R.layout.my_spinner_textview);
        dataAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        dataSpinner.setAdapter(dataAdapter);
        dataSpinner.setSelection(1);
        dataBit = 8;

        /* parity */
        paritySpinner = (Spinner) findViewById(R.id.parityValue);
        ArrayAdapter<CharSequence> parityAdapter = ArrayAdapter.createFromResource(this,
                R.array.parity, R.layout.my_spinner_textview);
        parityAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        paritySpinner.setAdapter(parityAdapter);
        parity = 0;

        /* flow control */
        flowSpinner = (Spinner) findViewById(R.id.flowControlValue);
        ArrayAdapter<CharSequence> flowAdapter = ArrayAdapter.createFromResource(this,
                R.array.flow_control, R.layout.my_spinner_textview);
        flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        flowSpinner.setAdapter(flowAdapter);
        flowSpinner.setSelection(1);
        flowControl = 1;

        /* port */
        portSpinner = (Spinner) findViewById(R.id.portSelectValue);
        portAdapter = ArrayAdapter.createFromResource(this, R.array.port_list_1,
                R.layout.my_spinner_textview);
        flowAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        portSpinner.setAdapter(portAdapter);
        portIndex = 0;

        baudSpinner.setOnItemSelectedListener(new MyOnBaudSelectedListener());
        stopSpinner.setOnItemSelectedListener(new MyOnStopSelectedListener());
        dataSpinner.setOnItemSelectedListener(new MyOnDataSelectedListener());
        paritySpinner.setOnItemSelectedListener(new MyOnParitySelectedListener());
        flowSpinner.setOnItemSelectedListener(new MyOnFlowSelectedListener());
        portSpinner.setOnItemSelectedListener(new MyOnPortSelectedListener());

        configButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // always check whether there is a device or not
                createDeviceList();
                if (DevCount > 0) {
                    connectFunction();
                }

                if (DeviceStatus.DEV_NOT_CONNECT == checkDevice()) {
                    return;
                }

                setConfig(baudRate, dataBit, stopBit, parity, flowControl);

                uart_configured = true;
                toggleMenuSetting();
            }
        });

        ctrlCButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (DeviceStatus.DEV_CONFIG == checkDevice()) {
                    writeBuffer[0] = 0x03; // Ctrl-C, ETX (End of text)
                    sendData(1, writeBuffer);
                }
            }
        });

        escButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (DeviceStatus.DEV_CONFIG == checkDevice()) {
                    writeBuffer[0] = 0x1B; // ESC
                    sendData(1, writeBuffer);
                }
            }
        });


//save file button -


// send file button -

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.rl_back:
//                finish();
//                break;
        }


    }

    //    j2xx---------------------------------------------------------------------------------
//    / menu function +
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        myMenu = menu;
        myMenu.add(0, MENU_SETTING, 0, "Setting");
        myMenu.add(0, MENU_CONTENT_FORMAT, 0, "Content Format");
        myMenu.add(0, MENU_FONT_SIZE, 0, "Font Size");
        myMenu.add(0, MENU_SAVE_CONTENT_DATA, 0, "Save Content Data");
        myMenu.add(0, MENU_CLEAN_SCREEN, 0, "Clean Screen");
        myMenu.add(0, MENU_ECHO, 0, "Echo - On");
        myMenu.add(0, MENU_HELP, 0, "Online Help");
        return super.onCreateOptionsMenu(myMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SETTING:
                toggleMenuSetting();
                break;

            case MENU_CONTENT_FORMAT:

                new AlertDialog.Builder(global_context).setTitle("Content Format")
                        .setItems(contentFormatItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (0 == which) {
                                    if (true == bContentFormatHex) {
                                        toggleContentHexFormat(false);
                                    }
                                } else if (1 == which) {
                                    if (false == bContentFormatHex) {
                                        toggleContentHexFormat(true);
                                    }
                                }
                            }
                        }).show();

                break;

            case MENU_FONT_SIZE:

                new AlertDialog.Builder(global_context).setTitle("Font Size")
                        .setItems(fontSizeItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                contentFontSize = Integer.parseInt(fontSizeItems[which]);
//                                readText.setTextSize(contentFontSize);
//                                mInputNum.setTextSize(contentFontSize);

                            }
                        }).show();

                break;

            case MENU_SAVE_CONTENT_DATA: {

                if (true == bSendButtonClick || true == bLogButtonClick) {
                    midToast("Can't save content data to file during sending file and saving data.", Toast.LENGTH_LONG);
                } else {
                    bUartModeTaskSet = false;
                    tempTransferMode = MODE_SAVE_CONTENT_DATA;

                    // select file
                    final String[] actItems = {"Create New File", "Save to File"};
                    new AlertDialog.Builder(global_context).setTitle("File Destination")
                            .setItems(actItems, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (0 == which) {
                                        fileDialog.setSelectDirectoryOption(true);
                                        fileDialog.setActionCode(ACT_SELECT_SAVED_FILE_FOLDER);
                                        fileDialog.showDialog();
                                    } else if (1 == which) {
                                        fileDialog.setSelectDirectoryOption(false);
                                        fileDialog.setActionCode(ACT_SELECT_SAVED_FILE_NAME);
                                        fileDialog.showDialog();
                                    }
                                }
                            }).show();
                }
            }
            break;

            case MENU_CLEAN_SCREEN:
//                readText.setText("");
//                mInputNum.setText("");
//                contentCharSequence = mInputNum.getText();
//                contentCharSequence = readText.getText();
                break;

            case MENU_ECHO:
                new AlertDialog.Builder(global_context).setTitle("Echo")
                        .setItems(echoSettingItems, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MenuItem item = myMenu.findItem(MENU_ECHO);
                                if (0 == which) {
                                    bWriteEcho = true;
                                    item.setTitle("Echo - On");
                                } else if (1 == which) {
                                    bWriteEcho = false;
                                    item.setTitle("Echo - Off");
                                }
                            }
                        }).show();

                break;

            case MENU_HELP: {
                File file = new File(android.os.Environment.getExternalStorageDirectory() + "/Download/AN_242_FTDI_UART_Terminal_User_Manual.pdf");

                if (true == file.exists()) {
                    // open file
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        midToast("Can't open help file. Please manually open it in the /Download folder.", Toast.LENGTH_SHORT);
                    }
                } else {
                    // download help file from webstie
                    Uri uri = Uri.parse("http://www.ftdichip.com/Support/Documents/AppNotes/AN_242_FTDI_UART_Terminal_User_Manual.pdf");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        midToast("Can't download help file.", Toast.LENGTH_SHORT);
                    }
                }
            }
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
// menu function -

    boolean checkContentFormat() {
        if (true == bContentFormatHex) {
            midToast("Please change content format to Charater before tranfer file.", Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    void setLogButton() {
        bLogButtonClick = true;

    }

    void resetLogButton() {
        bLogButtonClick = false;

    }

    void setSendButton() {
        bSendButtonClick = true;

        writeButton.setClickable(false);
    }

    void resetSendButton() {
        transferMode = MODE_GENERAL_UART;
        bUartModeTaskSet = true;
        bSendButtonClick = false;

        writeButton.setClickable(true);
    }

    void getSelectedFolder() {
        File fFolder = fileDialog.getChosenFolder();
        if (null == fFolder) {
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.file_name_dialog, (ViewGroup) findViewById(R.id.filenamedialog));

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this).setTitle("Create New File").
                setView(layout).setPositiveButton("OK", null).
                setNegativeButton("Cancel", null);

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button theButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setOnClickListener(new CreateNewFileNameListener(dialog, handler, fFolder.toString()));
    }

    class CreateNewFileNameListener implements View.OnClickListener {
        final Dialog dialog;
        Handler handler;
        String sFolder;
        EditText etCreateFileName;

        public CreateNewFileNameListener(Dialog dialog, Handler handler, String folder) {
            this.dialog = dialog;
            this.handler = handler;
            this.sFolder = folder;
        }

        @Override
        public void onClick(View v) {

            etCreateFileName = (EditText) dialog.findViewById(R.id.createFileName);
            String sfname = etCreateFileName.getText().toString();

            // check whether the file name is valid
            if (0 == sfname.length()) {
                midToast("Please input file name.", Toast.LENGTH_SHORT);
            } else if (sfname.matches(".*[:?\\s/\"<>|\\*]+.*") || sfname.contains("\\")) {
                midToast("A file name can't contain space or any of "
                        + "\nthe following characters: \\ / : * ? \" < > ", Toast.LENGTH_SHORT);
            } else {
                String filePath = sFolder + File.separator + sfname;
                Message msg = handler.obtainMessage(ACT_SAVED_FILE_NAME_CREATED, filePath);
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        }
    }

    // In Y, Z modem, they should selecet a folder for the file transfer
    void getModemSelectedFolder() {
        File fFolder = fileDialog.getChosenFolder();
        if (null == fFolder) {
            return;
        }

        setLogButton();

        if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {
            sendData(CHAR_C);

            updateStatusData("YModem - Wait data...");

            YModemReadDataThread ymReadThread = new YModemReadDataThread(handler);
            ymReadThread.start();

            sendCharCThread charCThread = new sendCharCThread();
            charCThread.start();
        } else if (MODE_Z_MODEM_RECEIVE == transferMode) {
            updateStatusData("ZModem - Wait data...");
            zmodemState = ZRQINIT;
            ZModemReadDataThread zmReadThread = new ZModemReadDataThread(handler);
            zmReadThread.start();
        } else {
        }
    }

    boolean openModemSaveFile() {

        File fFolder = fileDialog.getChosenFolder();
        String filePath;
        if (fFolder != null)
            filePath = fFolder + File.separator + modemFileName;
        else
            filePath = android.os.Environment.getExternalStorageDirectory() + File.separator + modemFileName;

        fGetFile = new File(filePath);

        try {
            fos_save = new FileOutputStream(filePath);
            buf_save = new BufferedOutputStream(fos_save);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        modemRemainData = iFileSize = Integer.parseInt(modemFileSize);
        if (iFileSize <= 1024)
            fileNameInfo = "File:" + modemFileName + "(" + iFileSize + "Bytes)";
        else if (iFileSize <= 1048576)
            fileNameInfo = "File:" + modemFileName + "(" + new java.text.DecimalFormat("#.00").format(iFileSize / (double) 1024) + "KB)";
        else
            fileNameInfo = "File:" + modemFileName + "(" + new java.text.DecimalFormat("#.00").format(iFileSize / (double) 1024 / 1024) + "MB)";

        WriteFileThread_start = true;
        return true;
    }

    void saveFileAction() {
        if (null == fGetFile) {
            midToast("Selected file null!", Toast.LENGTH_SHORT);
            return;
        } else {
            String stemp = fGetFile.toString();
            String[] tokens = stemp.split("/");
            fileNameInfo = tokens[tokens.length - 1];
            midToast("Save data to file:" + fileNameInfo, Toast.LENGTH_SHORT);
        }

        try {
            fos_save = new FileOutputStream(fGetFile);
            buf_save = new BufferedOutputStream(fos_save);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        WriteFileThread_start = true;

        if (MODE_SAVE_CONTENT_DATA == transferMode) {
//            byte[] tmpData = readText.getText().toString().getBytes();
//            byte[] tmpData = mInputNum.getText().toString().getBytes();
            try {
//                buf_save.write(tmpData, 0, tmpData.length);

                midToast("Content data is saved.", Toast.LENGTH_SHORT);

                buf_save.flush();
                buf_save.close();
                fos_save.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(global_context, new String[]{fGetFile.toString()}, null, null);

            transferMode = MODE_GENERAL_UART;
            bUartModeTaskSet = true;
        } else {
            setLogButton();

            if (MODE_X_MODEM_CHECKSUM_RECEIVE == transferMode) {
                sendData(NAK);

                updateStatusData("XModem-Checksum - Wait data...");

                XModemReadDataThread xmReadThread = new XModemReadDataThread(handler);
                xmReadThread.start();

                XModemNakThread nakThread = new XModemNakThread();
                nakThread.start();
            } else if (MODE_X_MODEM_CRC_RECEIVE == transferMode) {
                sendData(CHAR_C);

                updateStatusData("XModem-CRC - Wait data...");

                XModemReadDataThread xmReadThread = new XModemReadDataThread(handler);
                xmReadThread.start();

                sendCharCThread charCThread = new sendCharCThread();
                charCThread.start();
            } else if (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode) {
                sendData(CHAR_C);

                updateStatusData("XModem-1KCRC - Wait data...");

                XModemReadDataThread xmReadThread = new XModemReadDataThread(handler);
                xmReadThread.start();

                sendCharCThread charCThread = new sendCharCThread();
                charCThread.start();
            } else // Ascii mode
            {
                totalReceiveDataBytes = 0;
                AsciiReadDataThread asciiReadThread = new AsciiReadDataThread(handler);
                asciiReadThread.start();
            }
        }
    }

    class XModemNakThread extends Thread {
        public void run() {
            int errorCount = 0;
            while (0 == modemReceiveDataBytes[0] && 0 == totalModemReceiveDataBytes) {
                errorCount++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (false == bReadDataProcess) {
                    break;
                }

                if (0 == modemReceiveDataBytes[0] && 0 == totalModemReceiveDataBytes) {
                    sendData(NAK);
                }

                if (60 == errorCount) {
                    break;
                }
            }
        }
    }

    class sendCharCThread extends Thread {
        public void run() {
            int errorCount = 0;

            if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {
                while (false == bReceiveFirstPacket) {
                    errorCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (false == bReadDataProcess) {
                        break;
                    }

                    if (false == bDataReceived) {
                        sendData(CHAR_C);
                    }

                    if (60 == errorCount) {
                        break;
                    }
                }
            } else {
                while (0 == modemReceiveDataBytes[0] && 0 == totalModemReceiveDataBytes) {
                    errorCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (false == bReadDataProcess) {
                        break;
                    }

                    if (0 == modemReceiveDataBytes[0] && 0 == totalModemReceiveDataBytes) {
                        sendData(CHAR_C);
                    }

                    if (60 == errorCount) {
                        break;
                    }
                }
            }
        }
    }

    void saveFileActionDone() {

        WriteFileThread_start = false;

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (buf_save == null) // YModem saved file fail case
        {
            midToast("Stop saving data.", Toast.LENGTH_SHORT);
        } else {
            midToast("Stop saving data and close file.", Toast.LENGTH_SHORT);
            try {
                buf_save.flush();
                buf_save.close();
                fos_save.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(global_context, new String[]{fGetFile.toString()}, null, null);
        }

        resetLogButton();
    }

    void sendFileAction() {
        if (null == fGetFile) {
            midToast("Selected file null!", Toast.LENGTH_SHORT);
            resetSendButton();
            return;
        }

        try {
            File filenew = fGetFile;
            iFileSize = Integer.parseInt(String.valueOf(filenew.length()));
            fis_open = new FileInputStream(filenew);
        } catch (FileNotFoundException e) {
            resetSendButton();
            e.printStackTrace();
            return;
        }

        if (0 == iFileSize) {
            midToast("Selected file is 0 byte!", Toast.LENGTH_SHORT);
            resetSendButton();
            return;
        }

        String[] tokens = fGetFile.toString().split("/");
        sFileName = tokens[tokens.length - 1];

        if (iFileSize <= 1024)
            fileNameInfo = "File:" + sFileName + "(" + iFileSize + "Bytes)";
        else if (iFileSize <= 1048576)
            fileNameInfo = "File:" + sFileName + "(" + new java.text.DecimalFormat("#.00").format(iFileSize / (double) 1024) + "KB)";
        else
            fileNameInfo = "File:" + sFileName + "(" + new java.text.DecimalFormat("#.00").format(iFileSize / (double) 1024 / 1024) + "MB)";

        setSendButton();
        if (MODE_GENERAL_UART == transferMode) {
            updateStatusData(currentProtocol + " - Sending data...");
        } else {
            updateStatusData(currentProtocol + " - Wait receiver response...");
        }


        if (MODE_Z_MODEM_SEND == transferMode) {
            ZModemSendFileThread zModemSendFileThread = new ZModemSendFileThread(handler, fis_open);
            zModemSendFileThread.start();
        } else if (MODE_Y_MODEM_1K_CRC_SEND == transferMode) {
            sendData(CHAR_C);
            YModemSendFileThread yModemSendFileThread = new YModemSendFileThread(handler, fis_open);
            yModemSendFileThread.start();
        } else if ((MODE_X_MODEM_CHECKSUM_SEND == transferMode)
                || (MODE_X_MODEM_CRC_SEND == transferMode)
                || (MODE_X_MODEM_1K_CRC_SEND == transferMode)) {
            XModemSendFileThread xModemSendFileThread = new XModemSendFileThread(handler, fis_open);
            xModemSendFileThread.start();
        } else {
            SendFileThread sendFileThread = new SendFileThread(handler, fis_open);
            sendFileThread.start();
        }
    }

    // call this API to show message
    void midToast(String str, int showTime) {
        Toast toast = Toast.makeText(global_context, str, showTime);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);

        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.YELLOW);
        toast.show();
    }

    void toggleMenuSetting() {
        if (View.GONE == mMenuSetting.getVisibility()) {
            mMenuSetting.setVisibility(View.VISIBLE);
        } else {
            mMenuSetting.setVisibility(View.GONE);
        }
    }

    void toggleMenuKey() {
        if (View.GONE == mMenuKey.getVisibility()) {
            mMenuKey.setVisibility(View.VISIBLE);
        } else {
            mMenuKey.setVisibility(View.GONE);
        }
    }

    void toggleContentHexFormat(boolean setHex) {
        int times = 0;

        if (true == bSendButtonClick || true == bLogButtonClick) {
            midToast("Can't change content format during sending file and saving data.", Toast.LENGTH_LONG);
            return;
        }

        if (true == setHex) {
            bContentFormatHex = true;
            int line = 0;
            int i = 0;
            int dataLength = 0;
            int len = 0;
            char cData;
            StringBuffer strBuffer = new StringBuffer();
            StringBuffer strBuf1 = new StringBuffer(80);
            StringBuffer strBuf2 = new StringBuffer();
            String temp;
//            contentCharSequence = readText.getText();
//            contentCharSequence = mInputNum.getText();
            dataLength = contentCharSequence.length();
//            readText.setText("");
//            mInputNum.setText("");
            for (; line * 16 < dataLength; line++) {


                strBuf1.delete(0, strBuf1.length());
                temp = Integer.toHexString(line * 16);
                len = temp.length();
                for (int j = 0; j < (7 - len); j++) {
                    strBuf1.append("0");
                }
                strBuf1.append(temp);
                strBuf1.append("h:");

                strBuf2.delete(0, strBuf2.length());
                for (; (i < (line + 1) * 16) && (i < dataLength); i++) {
                    cData = contentCharSequence.charAt(i);
                    switch (cData) {
                        case 0x00:
                        case 0x01:
                        case 0x02:
                        case 0x03:
                        case 0x04:
                        case 0x05:
                        case 0x06:
                        case 0x07:
                        case 0x08:
                        case 0x09:
                        case 0x0a:
                        case 0x0b:
                        case 0x0c:
                        case 0x0d:
                        case 0x0e:
                        case 0x0f:
                            strBuf1.append(" 0");
                            strBuf1.append(Integer.toHexString(cData));
                            break;

                        default:
                            temp = Integer.toHexString(cData);

                            if (temp.length() == 4) {
                                strBuf1.append(" ");
                                strBuf1.append(temp.substring(2, 4));
                            } else {
                                strBuf1.append(" ");
                                strBuf1.append(temp);
                            }
                            break;
                    }

                    switch (cData) {
                        case 0x00:
                        case 0x0a:
                        case 0x0d:
                            strBuf2.append(" ");
                            break;
                        default:
                            strBuf2.append(cData);
                            break;
                    }
                }

                if (i == dataLength && (i < (line + 1) * 16)) {
                    for (; i < (line + 1) * 16; i++) {
                        strBuf1.append("   ");
                    }
                }

                temp = "\n";
                temp = temp.replace("\\n", "\n");
                strBuffer.append(strBuf1.toString() + "; " + strBuf2.toString() + temp);

            }
//			readText.append(strBuffer.toString());
//            readText.append(times+"");

        } else {
//			readText.setText(contentCharSequence);
//            readText.setText(times+"");
            bContentFormatHex = false;
        }

        resetStatusData();
    }

    // add data to UI(@+id/ReadValues)
    void appendData(String data) {
        if (true == bContentFormatHex) {
            if (timesMessageHexFormatWriteData < 3) {
                timesMessageHexFormatWriteData++;
                midToast("The writing data won��t be showed on data area while content format is hexadecimal format.", Toast.LENGTH_LONG);
            }
            return;
        }
        int k = 0;
        if (true == bSendHexData) {
            SpannableString text = new SpannableString(data);
            text.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, data.length(), 0);
//            readText.append(text);
//            mInputNum.append(text);
            bSendHexData = false;
        } else {
            if (data.contains("Y1")) {
                k++;
                Log.e("Y1", "y1111111");//每次感应只执行一次
//                mPresenter.submitTimes();
                cameraView.captureImage();//捕获图片
                cameraView.addCameraKitListener(new CameraKitEventListener() {
                    @Override
                    public void onEvent(CameraKitEvent cameraKitEvent) {

                    }

                    @Override
                    public void onError(CameraKitError cameraKitError) {

                    }

                    @Override
                    public void onImage(CameraKitImage cameraKitImage) {
                        Bitmap bitmap = cameraKitImage.getBitmap();
                        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, false);//400,300实际传过去的图片大小
                        imageViewResult.setImageBitmap(bitmap);
                        final Bitmap finalBitmap = bitmap;
                              if(bitmap!=null) {
                                  Intent intent = new Intent(SensorCamaActivity.this, DetectorActivity.class);
                                  intent.putExtra("BITMAP", finalBitmap); //这里可以放一个bitmap
                                  startActivity(intent);
                                  finish();
                              }
                    }

                    @Override
                    public void onVideo(CameraKitVideo cameraKitVideo) {

                    }
                });
            }

        }
    }

    // for uart settings: buad rate, stop bit and etc. selection
    class MyOnBaudSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            baudRate = Integer.parseInt(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class MyOnStopSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            stopBit = (byte) Integer.parseInt(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class MyOnDataSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            dataBit = (byte) Integer.parseInt(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class MyOnParitySelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String tempString = new String(parent.getItemAtPosition(pos)
                    .toString());
            if (tempString.compareTo("None") == 0) {
                parity = 0;
            } else if (tempString.compareTo("Odd") == 0) {
                parity = 1;
            } else if (tempString.compareTo("Even") == 0) {
                parity = 2;
            } else if (tempString.compareTo("Mark") == 0) {
                parity = 3;
            } else if (tempString.compareTo("Space") == 0) {
                parity = 4;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class MyOnFlowSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String flowString = new String(parent.getItemAtPosition(pos).toString());
            if (flowString.compareTo("None") == 0) {
                flowControl = 0;
                midToast("When using None Flow control option, please consider you know how data flow between the receiver and the transmitter."
                        , Toast.LENGTH_LONG);
            } else if (flowString.compareTo("CTS/RTS") == 0) {
                flowControl = 1;
            } else if (flowString.compareTo("DTR/DSR") == 0) {
                flowControl = 2;
            } else if (flowString.compareTo("XOFF/XON") == 0) {
                flowControl = 3;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public class MyOnPortSelectedListener implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            portIndex = Integer.parseInt(parent.getItemAtPosition(pos).toString());
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    public void updatePortNumberSelector() {
        midToast(DevCount + " port device attached", Toast.LENGTH_SHORT);

        switch (DevCount) {
            case 2:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_2, R.layout.my_spinner_textview);
                break;
            case 3:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_3, R.layout.my_spinner_textview);
                break;
            case 4:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_4, R.layout.my_spinner_textview);
                break;
            case 5:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_5, R.layout.my_spinner_textview);
                break;
            case 6:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_6, R.layout.my_spinner_textview);
                break;
            case 7:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_7, R.layout.my_spinner_textview);
                break;
            case 8:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_8, R.layout.my_spinner_textview);
                break;
            case 1:
            default:
                portAdapter = ArrayAdapter.createFromResource(global_context, R.array.port_list_1, R.layout.my_spinner_textview);
                break;
        }

        portAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        portSpinner.setAdapter(portAdapter);
        portAdapter.notifyDataSetChanged();
    }

    void updateBaudRateSelector(int baudListNum) {
        if (1 == baudListNum) {
            baudAdapter = ArrayAdapter.createFromResource(this, R.array.baud_rate_1,
                    R.layout.my_spinner_textview);
        } else {
            baudAdapter = ArrayAdapter.createFromResource(this, R.array.baud_rate_2,
                    R.layout.my_spinner_textview);
            if (baudRate >= 230400)
                baudRate = 115200;
        }
        baudAdapter.setDropDownViewResource(R.layout.my_spinner_textview);
        baudSpinner.setAdapter(baudAdapter);
        baudAdapter.notifyDataSetChanged();

        switch (baudRate) {
            case 300:
                baudSpinner.setSelection(0);
                break;
            case 600:
                baudSpinner.setSelection(1);
                break;
            case 1200:
                baudSpinner.setSelection(2);
                break;
            case 4800:
                baudSpinner.setSelection(3);
                break;
            case 9600:
                baudSpinner.setSelection(4);
                break;
            case 19200:
                baudSpinner.setSelection(5);
                break;
            case 38400:
                baudSpinner.setSelection(6);
                break;
            case 57600:
                baudSpinner.setSelection(7);
                break;
            case 115200:
                baudSpinner.setSelection(8);
                break;
            case 230400:
                baudSpinner.setSelection(9);
                break;
            case 460800:
                baudSpinner.setSelection(10);
                break;
            case 921600:
                baudSpinner.setSelection(11);
                break;
            default:
                baudSpinner.setSelection(4);
                break;
        }
    }

    public void onAttachedToWindow() {
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
//        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if(KeyEvent.KEYCODE_HOME == keyCode)
//        {
//            DLog.e(TT,"Home key pressed");
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void onBackPressed() {
        if (false == bBackButtonClick) {
//            IntentUtils.startActivityAndFinish(this, MoInforActivity.class);
//            midToast("Are you sure you will exit the program? Press again to exit.", Toast.LENGTH_LONG);
//
//            back_button_click_time = System.currentTimeMillis();
//            bBackButtonClick = true;
//
//            ResetBackButtonThread backButtonThread = new ResetBackButtonThread();
//            backButtonThread.start();
        } else {
            super.onBackPressed();
        }
    }

    class ResetBackButtonThread extends Thread {
        public void run() {
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bBackButtonClick = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        createDeviceList();
        if (DevCount > 0) {
            connectFunction();
            setUARTInfoString();
            setConfig(baudRate, dataBit, stopBit, parity, flowControl);
        }
    }

    protected void onResume() {
        super.onResume();
        cameraView.start();
        if (null == ftDev || false == ftDev.isOpen()) {
            createDeviceList();
            if (DevCount > 0) {
                connectFunction();
                setUARTInfoString();
                setConfig(baudRate, dataBit, stopBit, parity, flowControl);
            }
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStop() {
        cameraView.stop();
        super.onStop();
    }

    protected void onDestroy() {
        disconnectFunction();
//        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

    // j2xx functions +
    public void createDeviceList() {
        int tempDevCount = ftD2xx.createDeviceInfoList(global_context);
        if (tempDevCount > 0) {
            if (DevCount != tempDevCount) {
                DevCount = tempDevCount;
                updatePortNumberSelector();
            }
        } else {
            DevCount = -1;
            currentPortIndex = -1;
        }
    }

    public void disconnectFunction() {
        DevCount = -1;
        currentPortIndex = -1;
        bReadTheadEnable = false;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ftDev != null) {
            if (true == ftDev.isOpen()) {
                ftDev.close();
            }
        }
    }

    public void connectFunction() {
        if (portIndex + 1 > DevCount) {
            portIndex = 0;
        }

        if (currentPortIndex == portIndex
                && ftDev != null
                && true == ftDev.isOpen()) {
            //Toast.makeText(global_context,"Port("+portIndex+") is already opened.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (true == bReadTheadEnable) {
            bReadTheadEnable = false;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (null == ftDev) {
            ftDev = ftD2xx.openByIndex(global_context, portIndex);
        } else {
            ftDev = ftD2xx.openByIndex(global_context, portIndex);
        }
        uart_configured = false;

        if (ftDev == null) {
            midToast("Open port(" + portIndex + ") NG!", Toast.LENGTH_LONG);
            return;
        }

        if (true == ftDev.isOpen()) {
            currentPortIndex = portIndex;
            //Toast.makeText(global_context, "open device port(" + portIndex + ") OK", Toast.LENGTH_SHORT).show();

            if (false == bReadTheadEnable) {
                readThread = new ReadThread(handler);
                readThread.start();
            }
        } else {
            midToast("Open port(" + portIndex + ") NG!", Toast.LENGTH_LONG);
        }
    }

    DeviceStatus checkDevice() {
        if (ftDev == null || false == ftDev.isOpen()) {
            midToast("Need to connect to cable.", Toast.LENGTH_SHORT);
            return DeviceStatus.DEV_NOT_CONNECT;
        } else if (false == uart_configured) {
            //midToast("CHECK: uart_configured == false", Toast.LENGTH_SHORT);
            midToast("Need to configure UART.", Toast.LENGTH_SHORT);
            return DeviceStatus.DEV_NOT_CONFIG;
        }

        return DeviceStatus.DEV_CONFIG;

    }

    void setUARTInfoString() {
        String parityString, flowString;

        switch (parity) {
            case 0:
                parityString = new String("None");
                break;
            case 1:
                parityString = new String("Odd");
                break;
            case 2:
                parityString = new String("Even");
                break;
            case 3:
                parityString = new String("Mark");
                break;
            case 4:
                parityString = new String("Space");
                break;
            default:
                parityString = new String("None");
                break;
        }

        switch (flowControl) {
            case 0:
                flowString = new String("None");
                break;
            case 1:
                flowString = new String("CTS/RTS");
                break;
            case 2:
                flowString = new String("DTR/DSR");
                break;
            case 3:
                flowString = new String("XOFF/XON");
                break;
            default:
                flowString = new String("None");
                break;
        }

        uartSettings = "Port " + portIndex + "; UART Setting  -  Baudrate:" + baudRate + "  StopBit:" + stopBit
                + "  DataBit:" + dataBit + "  Parity:" + parityString
                + "  FlowControl:" + flowString;

        resetStatusData();
    }

    void setConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        // configure port
        // reset to UART mode for 232 devices
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, XON, XOFF);

        setUARTInfoString();
        midToast(uartSettings, Toast.LENGTH_SHORT);

        uart_configured = true;
    }

    void sendData(int numBytes, byte[] buffer) {
        if (ftDev.isOpen() == false) {
            Toast.makeText(global_context, "Device not open!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numBytes > 0) {
            ftDev.write(buffer, numBytes);
        }
    }

    void sendData(byte buffer) {
        byte tmpBuf[] = new byte[1];
        tmpBuf[0] = buffer;
        ftDev.write(tmpBuf, 1);
    }
// j2xx functions -

    // get the first byte of incoming data
    byte firstData() {
        if (iTotalBytes > 0) {
            return readDataBuffer[iReadIndex];
        }

        return 0x00;
    }

    // For zmWaitReadData: Write data at offset of buffer.
    byte readData(int numBytes, int offset, byte[] buffer) {
        byte intstatus = 0x00; /* success by default */

        /* should be at least one byte to read */
        if ((numBytes < 1) || (0 == iTotalBytes)) {
            actualNumBytes = 0;
            intstatus = 0x01;
            return intstatus;
        }

        if (numBytes > iTotalBytes) {
            numBytes = iTotalBytes;
        }

        /* update the number of bytes available */
        iTotalBytes -= numBytes;
        actualNumBytes = numBytes;

        /* copy to the user buffer */
        for (int count = offset; count < numBytes + offset; count++) {
            buffer[count] = readDataBuffer[iReadIndex];
            iReadIndex++;
            iReadIndex %= MAX_NUM_BYTES;
        }

        return intstatus;
    }

    byte readData(int numBytes, byte[] buffer) {
        byte intstatus = 0x00; /* success by default */

        /* should be at least one byte to read */
        if ((numBytes < 1) || (0 == iTotalBytes)) {
            actualNumBytes = 0;
            intstatus = 0x01;
            return intstatus;
        }

        if (numBytes > iTotalBytes) {
            numBytes = iTotalBytes;
        }

        /* update the number of bytes available */
        iTotalBytes -= numBytes;
        actualNumBytes = numBytes;

        /* copy to the user buffer */
        for (int count = 0; count < numBytes; count++) {
            buffer[count] = readDataBuffer[iReadIndex];
            iReadIndex++;
            iReadIndex %= MAX_NUM_BYTES;
        }

        return intstatus;
    }

    void resetStatusData() {
        String tempStr = "Format - " + (bContentFormatHex ? "Hexadecimal" : "Character") + "\n" + uartSettings;
        String tmp = tempStr.replace("\\n", "\n");
        uartInfo.setText(tmp);
    }

    void updateStatusData(String str) {
        String temp;
        if (null == fileNameInfo)
            temp = "\n" + str;
        else
            temp = fileNameInfo + "\n" + str;

        String tmp = temp.replace("\\n", "\n");
        uartInfo.setText(tmp);
    }

    int m = 0;//光电感应次数
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT_VIEW_CONTENT:
                    if (actualNumBytes > 0) {
                        totalUpdateDataBytes += actualNumBytes;
                        for (int i = 0; i < actualNumBytes; i++) {
                            readBufferToChar[i] = (char) readBuffer[i];
                        }
                        if (String.copyValueOf(readBufferToChar, 0, actualNumBytes).contains("Y1"))
                            m++;
                        Log.e("y3", "y3333333");//每次感应执行两次
                        appendData(String.copyValueOf(readBufferToChar, 0, actualNumBytes));
                    }
                    break;

                case UPDATE_SEND_FILE_STATUS: {
                    String temp = currentProtocol;
                    if (sendByteCount <= 10240)
                        temp += " Send:" + sendByteCount + "B("
                                + new java.text.DecimalFormat("#.00").format(sendByteCount / (iFileSize / (double) 100)) + "%)";
                    else
                        temp += " Send:" + new java.text.DecimalFormat("#.00").format(sendByteCount / (double) 1024) + "KB("
                                + new java.text.DecimalFormat("#.00").format(sendByteCount / (iFileSize / (double) 100)) + "%)";

                    updateStatusData(temp);
                }
                break;

                case UPDATE_SEND_FILE_DONE: {
                    midToast("Send file Done.", Toast.LENGTH_SHORT);

                    String temp = currentProtocol;
                    if (0 == iFileSize) {
                        temp += " - The sent file is 0 byte";
                    } else if (iFileSize < 100) {
                        temp += " Send:" + sendByteCount + "B("
                                + new java.text.DecimalFormat("#.00").format(sendByteCount * 100 / iFileSize) + "%)";
                    } else {
                        if (sendByteCount <= 10240)
                            temp += " Send:" + sendByteCount + "B("
                                    + new java.text.DecimalFormat("#.00").format(sendByteCount / (iFileSize / (double) 100)) + "%)";
                        else
                            temp += " Send:" + new java.text.DecimalFormat("#.00").format(sendByteCount / (double) 1024) + "KB("
                                    + new java.text.DecimalFormat("#.00").format(sendByteCount / (iFileSize / (double) 100)) + "%)";
                    }

                    Double diffime = (double) (end_time - start_time) / 1000;
                    temp += " in " + diffime.toString() + " seconds";

                    updateStatusData(temp);

                    resetSendButton();
                }
                break;

                case ACT_SELECT_SAVED_FILE_NAME:
                    setProtocolMode();
                    saveFileAction();
                    break;

                case ACT_SELECT_SAVED_FILE_FOLDER:
                    getSelectedFolder();
                    break;

                case ACT_SAVED_FILE_NAME_CREATED:
                    setProtocolMode();
                    fGetFile = new File((String) msg.obj);
                    saveFileAction();
                    break;

                case ACT_SELECT_SEND_FILE_NAME:
                    setProtocolMode();

                    sendFileAction();
                    break;

                case MSG_SELECT_FOLDER_NOT_FILE:
                    midToast("Do not pick a file.\n" +
                            "Plesae press \"Select Directory\" button to select current directory.", Toast.LENGTH_LONG);
                    break;

                case MSG_XMODEM_SEND_FILE_TIMEOUT: {
                    String temp = currentProtocol + " - No response when send file.";
                    midToast(temp, Toast.LENGTH_LONG);
                    updateStatusData(temp);

                    resetSendButton();
                }
                break;

                case UPDATE_MODEM_RECEIVE_DATA:
                    midToast(currentProtocol + " - Receiving data...", Toast.LENGTH_LONG);

                case UPDATE_MODEM_RECEIVE_DATA_BYTES: {
                    String temp = currentProtocol;
                    if (totalModemReceiveDataBytes <= 10240)
                        temp += " Receive " + totalModemReceiveDataBytes + "Bytes";
                    else
                        temp += " Receive " + new java.text.DecimalFormat("#.00").format(totalModemReceiveDataBytes / (double) 1024) + "KBytes";

                    updateStatusData(temp);
                }
                break;

                case UPDATE_MODEM_RECEIVE_DONE: {
                    saveFileActionDone();

                    String temp = currentProtocol;
                    if (totalModemReceiveDataBytes <= 10240)
                        temp += " Receive " + totalModemReceiveDataBytes + "Bytes";
                    else
                        temp += " Receive " + new java.text.DecimalFormat("#.00").format(totalModemReceiveDataBytes / (double) 1024) + "KBytes";

                    Double diffime = (double) (end_time - start_time) / 1000;
                    temp += " in " + diffime.toString() + " seconds";

                    updateStatusData(temp);
                }
                break;

                case MSG_MODEM_RECEIVE_PACKET_TIMEOUT: {
                    midToast(currentProtocol + " - No Incoming Data.", Toast.LENGTH_LONG);
                    String temp = currentProtocol;
                    if (totalModemReceiveDataBytes <= 10240)
                        temp += " Receive " + totalModemReceiveDataBytes + "Bytes";
                    else
                        temp += " Receive " + new java.text.DecimalFormat("#.00").format(totalModemReceiveDataBytes / (double) 1024) + "KBytes";

                    updateStatusData(temp);
                    saveFileActionDone();
                }
                break;

                case ACT_MODEM_SELECT_SAVED_FILE_FOLDER:
                    setProtocolMode();

                    getModemSelectedFolder();
                    break;

                case MSG_MODEM_OPEN_SAVE_FILE_FAIL:
                    midToast(currentProtocol + " - Open save file fail!", Toast.LENGTH_LONG);
                    break;

                case MSG_YMODEM_PARSE_FIRST_PACKET_FAIL:
                    midToast("YModem - Can't parse packet due to incorrect data format!", Toast.LENGTH_LONG);
                    resetLogButton();
                    break;

                case MSG_FORCE_STOP_SEND_FILE:
                    midToast("Stop sending file.", Toast.LENGTH_LONG);
                    break;

                case UPDATE_ASCII_RECEIVE_DATA_BYTES: {
                    String temp = currentProtocol;
                    if (totalReceiveDataBytes <= 10240)
                        temp += " Receive " + totalReceiveDataBytes + "Bytes";
                    else
                        temp += " Receive " + new java.text.DecimalFormat("#.00").format(totalReceiveDataBytes / (double) 1024) + "KBytes";

                    long tempTime = System.currentTimeMillis();
                    Double diffime = (double) (tempTime - start_time) / 1000;
                    temp += " in " + diffime.toString() + " seconds";

                    updateStatusData(temp);
                }
                break;

                case UPDATE_ASCII_RECEIVE_DATA_DONE:
                    saveFileActionDone();
                    break;

                case MSG_FORCE_STOP_SAVE_TO_FILE:
                    midToast("Stop saving to file.", Toast.LENGTH_LONG);
                    break;

                case UPDATE_ZMODEM_STATE_INFO:
                    updateStatusData("zmodemState:" + zmodemState);

                    if (ZOO == zmodemState) {
                        midToast("ZModem revice file done.", Toast.LENGTH_SHORT);
                    }
                    break;

                case ACT_ZMODEM_AUTO_START_RECEIVE:
                    bUartModeTaskSet = false;
                    transferMode = MODE_Z_MODEM_RECEIVE;
                    currentProtocol = "ZModem";


                    receivedPacketNumber = 1;
                    modemReceiveDataBytes[0] = 0;
                    totalModemReceiveDataBytes = 0;
                    bDataReceived = false;
                    bReceiveFirstPacket = false;
                    fileNameInfo = null;

                    setLogButton();

                    zmodemState = ZRINIT;
                    start_time = System.currentTimeMillis();
                    ZModemReadDataThread zmReadThread = new ZModemReadDataThread(handler);
                    zmReadThread.start();
                    break;


                case MSG_SPECIAL_INFO:

                    midToast("INFO:" + (String) (msg.obj), Toast.LENGTH_LONG);
                    break;

                case MSG_UNHANDLED_CASE:
                    if (msg.obj != null)
                        midToast("UNHANDLED CASE:" + (String) (msg.obj), Toast.LENGTH_LONG);
                    else
                        midToast("UNHANDLED CASE ?", Toast.LENGTH_LONG);
                    break;
                default:
                    midToast("NG CASE", Toast.LENGTH_LONG);
                    //Toast.makeText(global_context, ".", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    void setProtocolMode() {
        if (true == bUartModeTaskSet) {
            transferMode = MODE_GENERAL_UART;
            currentProtocol = "Ascii";
        } else {
            transferMode = tempTransferMode;
            switch (transferMode) {
                case MODE_X_MODEM_CHECKSUM_RECEIVE:
                case MODE_X_MODEM_CHECKSUM_SEND:
                    currentProtocol = "XModem-Checksum";
                    break;

                case MODE_X_MODEM_CRC_RECEIVE:
                case MODE_X_MODEM_CRC_SEND:
                    currentProtocol = "XModem-CRC";
                    break;

                case MODE_X_MODEM_1K_CRC_RECEIVE:
                case MODE_X_MODEM_1K_CRC_SEND:
                    currentProtocol = "XModem-1KCRC";
                    break;

                case MODE_Y_MODEM_1K_CRC_RECEIVE:
                case MODE_Y_MODEM_1K_CRC_SEND:
                    currentProtocol = "YModem";
                    break;

                case MODE_Z_MODEM_RECEIVE:
                case MODE_Z_MODEM_SEND:
                    currentProtocol = "ZModem";
                    break;

                default:
                    currentProtocol = "unknown";
                    break;
            }
        }
    }

    // Update UI content
    class HandlerThread extends Thread {
        Handler mHandler;

        HandlerThread(Handler h) {
            mHandler = h;
        }

        public void run() {
            byte status;
            Message msg;

            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (true == bContentFormatHex) // consume input data at hex content format
                {
                    status = readData(UI_READ_BUFFER_SIZE, readBuffer);
                } else if (MODE_GENERAL_UART == transferMode) {
                    status = readData(UI_READ_BUFFER_SIZE, readBuffer);

                    if (0x00 == status) {
                        if (false == WriteFileThread_start) {
                            checkZMStartingZRQINIT();
                        }

                        // save data to file
                        if (true == WriteFileThread_start && buf_save != null) {
                            try {
                                buf_save.write(readBuffer, 0, actualNumBytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        msg = mHandler.obtainMessage(UPDATE_TEXT_VIEW_CONTENT);
                        mHandler.sendMessage(msg);
                    }
                }
            }
        }
    }

    class ReadThread extends Thread {
        final int USB_DATA_BUFFER = 8192;

        Handler mHandler;

        ReadThread(Handler h) {
            mHandler = h;
            this.setPriority(MAX_PRIORITY);
        }

        public void run() {
            byte[] usbdata = new byte[USB_DATA_BUFFER];
            int readcount = 0;
            int iWriteIndex = 0;
            bReadTheadEnable = true;

            while (true == bReadTheadEnable) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                while (iTotalBytes > (MAX_NUM_BYTES - (USB_DATA_BUFFER + 1))) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                readcount = ftDev.getQueueStatus();
                //Log.e(">>@@","iavailable:" + iavailable);
                if (readcount > 0) {
                    if (readcount > USB_DATA_BUFFER) {
                        readcount = USB_DATA_BUFFER;
                    }
                    ftDev.read(usbdata, readcount);

                    if ((MODE_X_MODEM_CHECKSUM_SEND == transferMode)
                            || (MODE_X_MODEM_CRC_SEND == transferMode)
                            || (MODE_X_MODEM_1K_CRC_SEND == transferMode)) {
                        for (int i = 0; i < readcount; i++) {
                            modemDataBuffer[i] = usbdata[i];
                        }

                        if (NAK == modemDataBuffer[0]) {
                            bModemGetNak = true;
                        } else if (ACK == modemDataBuffer[0]) {
                            bModemGetAck = true;
                        } else if (CHAR_C == modemDataBuffer[0]) {
                            bModemGetCharC = true;
                        }
                        if (CHAR_G == modemDataBuffer[0]) {
                            bModemGetCharG = true;
                        }
                    } else {
                        totalReceiveDataBytes += readcount;
                        //DLog.e(TT,"totalReceiveDataBytes:"+totalReceiveDataBytes);

                        //DLog.e(TT,"readcount:"+readcount);
                        for (int count = 0; count < readcount; count++) {
                            readDataBuffer[iWriteIndex] = usbdata[count];
                            iWriteIndex++;
                            iWriteIndex %= MAX_NUM_BYTES;
                        }

                        if (iWriteIndex >= iReadIndex) {
                            iTotalBytes = iWriteIndex - iReadIndex;
                        } else {
                            iTotalBytes = (MAX_NUM_BYTES - iReadIndex) + iWriteIndex;
                        }

                        //DLog.e(TT,"iTotalBytes:"+iTotalBytes);
                        if ((MODE_X_MODEM_CHECKSUM_RECEIVE == transferMode)
                                || (MODE_X_MODEM_CRC_RECEIVE == transferMode)
                                || (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode)
                                || (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode)
                                || (MODE_Z_MODEM_RECEIVE == transferMode)
                                || (MODE_Z_MODEM_SEND == transferMode)) {
                            modemReceiveDataBytes[0] += readcount;
                        }
                    }
                }
            }

            ;
        }
    }

    class AsciiReadDataThread extends Thread {
        Handler mHandler;

        AsciiReadDataThread(Handler h) {
            mHandler = h;
        }

        public void run() {
            Message msg;
            start_time = System.currentTimeMillis();
            bReadDataProcess = true;
            while (bReadDataProcess) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                msg = mHandler.obtainMessage(UPDATE_ASCII_RECEIVE_DATA_BYTES);
                mHandler.sendMessage(msg);
            }

            msg = mHandler.obtainMessage(UPDATE_ASCII_RECEIVE_DATA_BYTES);
            mHandler.sendMessage(msg);

            end_time = System.currentTimeMillis();
            msg = mHandler.obtainMessage(UPDATE_ASCII_RECEIVE_DATA_DONE);
            mHandler.sendMessage(msg);
        }
    }

    class SendFileThread extends Thread {
        Handler mHandler;
        FileInputStream instream;

        SendFileThread(Handler h, FileInputStream stream) {
            mHandler = h;
            instream = stream;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            Message msg;
            byte[] usbdata = new byte[64];
            int readcount = 0;
            sendByteCount = 0;
            start_time = System.currentTimeMillis();

            if (instream != null) {
                cal_time_1 = System.currentTimeMillis();
                try {
                    readcount = instream.read(usbdata, 0, 64);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                while (readcount > 0) {
                    sendData(readcount, usbdata);
                    sendByteCount += readcount;
                    try {
                        readcount = instream.read(usbdata, 0, 64);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    cal_time_2 = System.currentTimeMillis();
                    if ((cal_time_2 - cal_time_1) >= 200) // update progress every 200 milliseconds
                    {
                        msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                        mHandler.sendMessage(msg);
                        cal_time_1 = cal_time_2;
                    }

                    if (false == bSendButtonClick) {
                        msg = mHandler.obtainMessage(MSG_FORCE_STOP_SEND_FILE);
                        mHandler.sendMessage(msg);
                        break;
                    }
                }
            }

            end_time = System.currentTimeMillis();

            msg = mHandler.obtainMessage(UPDATE_SEND_FILE_DONE);
            mHandler.sendMessage(msg);
        }
    }

    // XModem + ===============================================================================================
    class XModemReadDataThread extends Thread {
        Handler mHandler;

        XModemReadDataThread(Handler h) {
            mHandler = h;
            this.setPriority(MAX_PRIORITY);
        }

        public void run() {
            Message msg;
            byte status;
            int xmodemErrorCount = 0;
            boolean bXModemPktParseOK = false;
            long check_data_time_1 = 0, check_data_time_2;
            int xmodemPacketSize = 0;
            byte ackData = NAK;
            int waitCount = 0;
            int resendCount = 20;
            int tempDataCount;
            boolean bStopReceive = false;

            int getDataState = 0;

            totalErrorCount = 0;
            switch (transferMode) {
                case MODE_X_MODEM_CHECKSUM_RECEIVE:
                    xmodemPacketSize = PACTET_SIZE_XMODEM_CHECKSUM;
                    break;
                case MODE_X_MODEM_CRC_RECEIVE:
                    xmodemPacketSize = PACTET_SIZE_XMODEM_CRC;
                    break;
                case MODE_X_MODEM_1K_CRC_RECEIVE:
                    xmodemPacketSize = PACTET_SIZE_XMODEM_1K_CRC;
                    break;
                default:
                    // incorrect transfer mode
                    return;
            }


            bReadDataProcess = true;

            while (bReadDataProcess) {

                waitCount = 0;
                resendCount = 20;
                tempDataCount = 0;
                while (modemReceiveDataBytes[0] < xmodemPacketSize) {
                    waitCount++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (EOT == firstData()) {
                        break;
                    }

                    if (false == bLogButtonClick) {
                        break;
                    }

                    // re-send nak/ack for xmomdem revice when there is no packet in for a period of time
                    if (10 == waitCount) {
                        tempDataCount = modemReceiveDataBytes[0];
                    } else if (waitCount > resendCount) {
                        resendCount += 10;
                        if (tempDataCount == modemReceiveDataBytes[0]) // no incoming data
                        {
                            if (true == bDataReceived) // transfer starting
                            {
                                if (0 == getDataState) {
                                    if (modemReceiveDataBytes[0] == 0) {
                                        sendData(ackData);
                                    } else {
                                        getDataState = 1;
                                    }
                                } else if (1 == getDataState) {
                                    getDataState = 2;
                                } else if (2 == getDataState) {
                                    getDataState = 3;

                                } else if (3 == getDataState) {

                                    readData(iTotalBytes, modemDataBuffer);
                                    iTotalBytes = 0;
                                    modemReceiveDataBytes[0] = 0;

                                    sendData(NAK);
                                }
                            }
                        } else if (false == bDataReceived) {
                            bDataReceived = true;
                            msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA);
                            mHandler.sendMessage(msg);
                        } else {
                            getDataState = 1;
                            tempDataCount = modemReceiveDataBytes[0];
                        }
                    }

                    // stop when long time no data 6000 * 10 ms = 60 sec.
                    if (waitCount > 6000) {
                        bStopReceive = true;
                        break;
                    }
                }

                if (false == bLogButtonClick) {
                    msg = mHandler.obtainMessage(MSG_FORCE_STOP_SAVE_TO_FILE);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                if (bStopReceive) {
                    msg = mHandler.obtainMessage(MSG_MODEM_RECEIVE_PACKET_TIMEOUT);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                if (EOT == firstData()) {
                    readData(1, modemDataBuffer);
                    end_time = System.currentTimeMillis();
                    sendData(ACK);
                    sendData(ACK);
                    sendData(ACK);
                    msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DONE);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                status = readData(xmodemPacketSize, modemDataBuffer);


                if (0x00 != status) {
                    //midToast("XMRead - status error",Toast.LENGTH_SHORT); Exception: Do not update UI in other thread!
                }


                // parse packet
                bXModemPktParseOK = parseModemPacket();

                if (true == bReceiveFirstPacket) {
                    check_data_time_2 = System.currentTimeMillis();
                    if ((check_data_time_2 - check_data_time_1) >= 200) // update progress every 200 milliseconds
                    {
                        msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA_BYTES);
                        mHandler.sendMessage(msg);
                        check_data_time_1 = check_data_time_2;
                    }
                }

                if (true == bXModemPktParseOK) {
                    xmodemErrorCount = 0;
                    receivedPacketNumber++;
                    // write received data to data area or update user area
                    if (false == bReceiveFirstPacket) {
                        // notify receiving process starting
                        check_data_time_1 = System.currentTimeMillis();
                        start_time = System.currentTimeMillis();
                        bReceiveFirstPacket = true;
                        bDataReceived = true;
                    }

                    ackData = ACK;
                } else {
                    totalErrorCount++;
                    xmodemErrorCount++;
                    if (xmodemErrorCount > 10) {
                        msg = mHandler.obtainMessage(MSG_MODEM_RECEIVE_PACKET_TIMEOUT);
                        mHandler.sendMessage(msg);
                        transferMode = MODE_GENERAL_UART;
                        bUartModeTaskSet = true;
                        bReadDataProcess = false;
                        continue;
                    }
                    ackData = NAK;
                }
                sendData(ackData);
            }
        }
    }

    class XModemSendFileThread extends Thread {
        Handler mHandler;
        FileInputStream instream;

        XModemSendFileThread(Handler h, FileInputStream stream) {
            mHandler = h;
            instream = stream;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            Message msg;
            byte[] usbdata;
            int readcount = 0;

            long check_timeout_1, check_timeout_2;
            boolean bSendFileProcess = true;
            boolean bSendFileDone = false;
            boolean bStartSendPacket = false;
            boolean bSendEOT = false;
            byte sendPacketNumber = 0;
            int readDataSize = 128;
            int errorCount = 0;

            sendByteCount = 0;
            start_time = System.currentTimeMillis();
            totalErrorCount = 0;
            if (MODE_X_MODEM_1K_CRC_SEND == transferMode) {
                readDataSize = DATA_SIZE_1K;
                usbdata = new byte[DATA_SIZE_1K];
            } else // checksum, crc
            {
                readDataSize = DATA_SIZE_128;
                usbdata = new byte[DATA_SIZE_128];
            }

            if (instream != null) {
                while (true == bSendFileProcess) {
                    if (true == bSendEOT) {
                        check_timeout_1 = System.currentTimeMillis();
                        while (false == bModemGetAck) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            sendData(EOT);
                            check_timeout_2 = System.currentTimeMillis();
                            if ((check_timeout_2 - check_timeout_1) / 1000 > 10) // over 10 seconds no response
                            {
                                break;
                            }
                        }

                        bSendFileDone = true;
                        bModemGetAck = false;
                        bSendFileProcess = false;
                        continue;
                    }

                    if (false == bStartSendPacket) {
                        // wait receiver ask to send file
                        check_timeout_1 = System.currentTimeMillis();

                        while (((MODE_X_MODEM_CHECKSUM_SEND == transferMode) && (false == bModemGetNak))
                                || ((MODE_X_MODEM_CRC_SEND == transferMode) && (false == bModemGetCharC))
                                || ((MODE_X_MODEM_1K_CRC_SEND == transferMode) && (false == bModemGetCharC)))  // not CharG
                        {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            check_timeout_2 = System.currentTimeMillis();
                            if ((check_timeout_2 - check_timeout_1) / 1000 > 90) // over 90 seconds no response
                            {
                                bSendFileProcess = false;
                                bStartSendPacket = true; // No packet was sent, but need to set it to skip sending first packet.
                                break;
                            }
                        }
                    } else {
                        // wait receiver notification
                        int noAckCount = 0;
                        while (false == bModemGetAck && false == bModemGetNak) {
                            noAckCount++;
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // 300 baud 1024kbytes: 1024/(300/8) = 27.3 sec(ideal) -> 60 sec
                            if (noAckCount > 6000) // 10ms * 6000 = 60 sec
                            {
                                bSendFileProcess = false;
                                break;
                            }
                        }
                    }

                    // ACK - send next packet
                    if (true == bModemGetAck || false == bStartSendPacket) {
                        errorCount = 0;
                        if (false == bStartSendPacket) {
                            cal_time_1 = System.currentTimeMillis();
                            bStartSendPacket = true;
                            bModemGetNak = false;
                            bModemGetCharC = false;
                            bModemGetCharG = false;
                            msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                            mHandler.sendMessage(msg);
                        }
                        bModemGetAck = false;

                        sendPacketNumber++;

                        // generate packet
                        if (MODE_X_MODEM_1K_CRC_SEND == transferMode) {
                            modemDataBuffer[0] = STX;
                        } else {
                            modemDataBuffer[0] = SOH;
                        }
                        modemDataBuffer[1] = sendPacketNumber;
                        modemDataBuffer[2] = (byte) ~sendPacketNumber;

                        try {
                            readcount = instream.read(usbdata, 0, readDataSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // information update
                        if (readcount > 0) {
                            sendByteCount += readcount;
                        }
                        cal_time_2 = System.currentTimeMillis();
                        if ((cal_time_2 - cal_time_1) >= 200) // update progress every 200 ms
                        {
                            msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                            mHandler.sendMessage(msg);
                            cal_time_1 = cal_time_2;
                        }


                        if (readDataSize == readcount) {
                            if (MODE_X_MODEM_CHECKSUM_SEND == transferMode) {
                                byte checksum = 0;
                                for (int i = 0; i < DATA_SIZE_128; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                    checksum += usbdata[i];
                                }
                                modemDataBuffer[131] = checksum;
                                sendData(PACTET_SIZE_XMODEM_CHECKSUM, modemDataBuffer);
                            } else if (MODE_X_MODEM_CRC_SEND == transferMode) {
                                for (int i = 0; i < DATA_SIZE_128; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                }

                                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_128);
                                modemDataBuffer[131] = crcHL[0];
                                modemDataBuffer[132] = crcHL[1];
                                sendData(PACTET_SIZE_XMODEM_CRC, modemDataBuffer);
                            }
                            if (MODE_X_MODEM_1K_CRC_SEND == transferMode) {
                                for (int i = 0; i < DATA_SIZE_1K; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                }

                                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_1K);
                                modemDataBuffer[1027] = crcHL[0];
                                modemDataBuffer[1028] = crcHL[1];
                                sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                            }
                        } else if (readcount > 0) // not a complete packet
                        {
                            if (MODE_X_MODEM_CHECKSUM_SEND == transferMode) {
                                byte checksum = 0;
                                for (int i = 0; i < readcount; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                    checksum += usbdata[i];
                                }

                                for (int j = 0; j < (DATA_SIZE_128 - readcount); j++) {
                                    modemDataBuffer[j + 3 + readcount] = 0;
                                }

                                modemDataBuffer[131] = checksum;
                                sendData(PACTET_SIZE_XMODEM_CHECKSUM, modemDataBuffer);
                            } else if (MODE_X_MODEM_CRC_SEND == transferMode) {
                                for (int i = 0; i < readcount; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                }

                                for (int j = 0; j < (DATA_SIZE_128 - readcount); j++) {
                                    modemDataBuffer[j + 3 + readcount] = 0x1a;        // no data, set as 0x1A(^Z)
                                }

                                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_128);
                                modemDataBuffer[131] = crcHL[0];
                                modemDataBuffer[132] = crcHL[1];
                                sendData(PACTET_SIZE_XMODEM_CRC, modemDataBuffer);
                            } else if (MODE_X_MODEM_1K_CRC_SEND == transferMode) {
                                for (int i = 0; i < readcount; i++) {
                                    modemDataBuffer[i + 3] = usbdata[i];
                                }

                                for (int j = 0; j < (DATA_SIZE_1K - readcount); j++) {
                                    modemDataBuffer[j + 3 + readcount] = 0x1a;        // no data, set as 0x1A(^Z)
                                }

                                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_1K);
                                modemDataBuffer[1027] = crcHL[0];
                                modemDataBuffer[1028] = crcHL[1];
                                sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                            } else {
                            }
                        } else if (0 == readcount || -1 == readcount)// no data
                        {
                            sendData(EOT);
                            bSendEOT = true;
                            //bSendFileDone = true;
                        } else {
                        }
                    }
                    // NAK - Re-send previous packet
                    else if (true == bModemGetNak) {
                        totalErrorCount++;
                        errorCount++;
                        if (errorCount > 10) {
                            bSendFileProcess = false;
                            continue;
                        }

                        bModemGetNak = false;
                        if (MODE_X_MODEM_1K_CRC_SEND == transferMode) {
                            sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                        } else if (MODE_X_MODEM_CRC_SEND == transferMode) {
                            sendData(PACTET_SIZE_XMODEM_CRC, modemDataBuffer);
                        } else// if(MODE_X_MODEM_CHECKSUM_SEND == transferMode)
                        {
                            sendData(PACTET_SIZE_XMODEM_CHECKSUM, modemDataBuffer);
                        }
                    }

                    if (false == bSendButtonClick) {
                        break;
                    }
                }
            }

            transferMode = MODE_GENERAL_UART;
            bUartModeTaskSet = true;

            if (true == bSendFileDone) {
                end_time = System.currentTimeMillis();
                msg = mHandler.obtainMessage(UPDATE_SEND_FILE_DONE);
                mHandler.sendMessage(msg);
            } else if (false == bSendButtonClick) {
                msg = mHandler.obtainMessage(MSG_FORCE_STOP_SEND_FILE);
                mHandler.sendMessage(msg);
            } else {
                msg = mHandler.obtainMessage(MSG_XMODEM_SEND_FILE_TIMEOUT);
                mHandler.sendMessage(msg);
            }
        }
    }

    boolean parseModemPacket() {
        boolean parseOK = true;
        byte pktnum, notpktnum;
        int packetSize = 132;

        if (MODE_X_MODEM_CHECKSUM_RECEIVE == transferMode) {
            packetSize = PACTET_SIZE_XMODEM_CHECKSUM;
        } else if (MODE_X_MODEM_CRC_RECEIVE == transferMode) {
            packetSize = PACTET_SIZE_XMODEM_CRC;
        } else if (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode ||
                MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {
            packetSize = PACTET_SIZE_XMODEM_1K_CRC;
        }


        pktnum = modemDataBuffer[1];
        notpktnum = modemDataBuffer[2];


        if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode && 0 == pktnum && false == bReceiveFirstPacket) // y modem:duplicated file info packet
        {
            bDuplicatedPacket = true;
            modemReceiveDataBytes[0] -= PACTET_SIZE_XMODEM_CRC;

            int tmpRange = packetSize - PACTET_SIZE_XMODEM_CRC; // Need to move 896(1029 - 133) data.

            for (int i = 0; i < tmpRange; i++) {
                modemDataBuffer[i] = modemDataBuffer[i + PACTET_SIZE_XMODEM_CRC];
            }
            return true;
        }


        if (pktnum != (~notpktnum)) {
            //DLog.e(TXR,"pktnum:"+pktnum+" notpktnum:"+notpktnum);
            parseOK = false;
        }
        if (pktnum != receivedPacketNumber) {
            parseOK = false;
        }


        if (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode) {
            if (modemDataBuffer[0] != STX) {
                parseOK = false;
            }
        } else if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {

            if (modemDataBuffer[0] == SOH) {
                packetSize = PACTET_SIZE_XMODEM_CRC;
            } else if (modemDataBuffer[0] == STX) {
            } else {
                parseOK = false;
            }
        } else // checksum, crc
        {
            if (modemDataBuffer[0] != SOH) {
                parseOK = false;
            }
        }
        modemReceiveDataBytes[0] -= packetSize;

        if (true == parseOK) {
            if (MODE_X_MODEM_CHECKSUM_RECEIVE == transferMode) {
                byte checksum = 0;
                for (int i = 3; i < (DATA_SIZE_128 + 3); i++) {
                    checksum += modemDataBuffer[i];
                }

                if (modemDataBuffer[131] != checksum) {
                    Log.e(TXR, "checksum check NG");
                    parseOK = false;
                }
            } else if (MODE_X_MODEM_CRC_RECEIVE == transferMode) {
                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_128);

                if (crcHL[0] != modemDataBuffer[131] || crcHL[1] != modemDataBuffer[132]) {
                    parseOK = false;
                }
            } else if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode && PACTET_SIZE_XMODEM_CRC == packetSize) {
                // TODO: should check crc; last packet, assume it is ok.
            } else if (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode ||
                    MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {
                byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_1K);

                if (crcHL[0] != modemDataBuffer[1027] || crcHL[1] != modemDataBuffer[1028]) {
                    parseOK = false;
                }
            } else {
                parseOK = false;
            }
        }

        // save data to file
        if (true == parseOK && true == WriteFileThread_start && buf_save != null) {
            try {
                if (MODE_Y_MODEM_1K_CRC_RECEIVE == transferMode) {
                    if (modemRemainData >= DATA_SIZE_1K) {
                        totalModemReceiveDataBytes += DATA_SIZE_1K;
                        buf_save.write(modemDataBuffer, 3, DATA_SIZE_1K); // data size: 1024
                    } else {
                        totalModemReceiveDataBytes += modemRemainData;
                        buf_save.write(modemDataBuffer, 3, modemRemainData); // remain data and this packet should be last packet
                    }
                    modemRemainData -= DATA_SIZE_1K;
                } else if (MODE_X_MODEM_1K_CRC_RECEIVE == transferMode) {
                    totalModemReceiveDataBytes += DATA_SIZE_1K;
                    buf_save.write(modemDataBuffer, 3, DATA_SIZE_1K); // data size: 1024
                } else // checksum, crc
                {
                    totalModemReceiveDataBytes += DATA_SIZE_128;
                    buf_save.write(modemDataBuffer, 3, DATA_SIZE_128); // data size: 128
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return parseOK;
    }
// XModem - ===============================================================================================

    // YModem + ===============================================================================================
    class YModemSendFileThread extends Thread {
        Handler mHandler;
        FileInputStream instream;

        YModemSendFileThread(Handler h, FileInputStream stream) {
            mHandler = h;
            instream = stream;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void run() {
            Message msg;
            byte[] usbdata = new byte[DATA_SIZE_1K];
            int readcount = 0;
            boolean bSendFileProcess = true;
            byte sendPacketNumber = 0;
            int readDataSize = DATA_SIZE_1K;
            int iGetRespone = 0;

            if (instream == null)
                return;

            sendByteCount = 0;
            totalErrorCount = 0;
            start_time = System.currentTimeMillis();
            cal_time_1 = System.currentTimeMillis();
            ymodemState = Y_MODEM_WAIT_ASK_SEND_FILE;

            while (true == bSendFileProcess) {
                if (false == bSendButtonClick)
                    break;

                if (ymodemState >= Y_MODEM_START_SEND_FILE) {
                    cal_time_2 = System.currentTimeMillis();
                    if ((cal_time_2 - cal_time_1) > 200) // update status every 200ms
                    {
                        msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                        mHandler.sendMessage(msg);
                        cal_time_1 = cal_time_2;
                    }
                }

                switch (ymodemState) {
                    case Y_MODEM_WAIT_ASK_SEND_FILE: {
                        // wait receiver ask to send file
                        iGetRespone = waitAck(90000);

                        if (DATA_CHAR_C == iGetRespone) {
                            ymodemState = Y_MODEM_SEND_FILE_INFO_PACKET;
                        } else {
                            bSendFileProcess = false;
                        }
                    }
                    continue;

                    case Y_MODEM_SEND_FILE_INFO_PACKET: {
                        byte[] bFileName = sFileName.getBytes();
                        byte[] bFileSize = Integer.toString(iFileSize).getBytes();
                        int lenName = bFileName.length;
                        int lenSize = bFileSize.length;


                        modemDataBuffer[0] = SOH;
                        modemDataBuffer[1] = sendPacketNumber;
                        modemDataBuffer[2] = (byte) ~sendPacketNumber;

                        int i;
                        for (i = 3; i < lenName + 3; i++) {
                            modemDataBuffer[i] = bFileName[i - 3];
                        }
                        modemDataBuffer[i] = 0;
                        i++;
                        int j;
                        for (j = i; j < lenSize + i; j++) {
                            modemDataBuffer[j] = bFileSize[j - i];
                        }

                        for (; j < 130; j++) {
                            modemDataBuffer[j] = 0;
                        }

                        byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_128);
                        modemDataBuffer[131] = crcHL[0];
                        modemDataBuffer[132] = crcHL[1];
                        sendData(PACTET_SIZE_XMODEM_CRC, modemDataBuffer);
                        ymodemState = Y_MODEM_SEND_FILE_INFO_PACKET_WAIT_ACK;
                    }
                    continue;

                    case Y_MODEM_SEND_FILE_INFO_PACKET_WAIT_ACK: {
                        iGetRespone = waitAck(60000);

                        if (DATA_NAK == iGetRespone) {
                            ymodemState = Y_MODEM_SEND_FILE_INFO_PACKET;
                        } else if (DATA_ACK == iGetRespone) {
                            if (DATA_CHAR_C == waitAck(10000)) {
                                ymodemState = Y_MODEM_START_SEND_FILE;
                            } else {
                                ymodemState = Y_MODEM_SEND_FILE_INFO_PACKET;
                            }
                        } else {
                            bSendFileProcess = false;
                        }
                    }
                    continue;

                    case Y_MODEM_START_SEND_FILE: {
                        sendPacketNumber++;

                        // generate packet
                        modemDataBuffer[0] = STX;

                        modemDataBuffer[1] = sendPacketNumber;
                        modemDataBuffer[2] = (byte) ~sendPacketNumber;

                        try {
                            readcount = instream.read(usbdata, 0, readDataSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // information update
                        if (readcount > 0) {
                            sendByteCount += readcount;
                        }
                        cal_time_2 = System.currentTimeMillis();
                        if ((cal_time_2 - cal_time_1) >= 200) // update progress every 200 ms
                        {
                            msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                            mHandler.sendMessage(msg);
                            cal_time_1 = cal_time_2;
                        }

                        if (readDataSize == readcount) {
                            for (int i = 0; i < DATA_SIZE_1K; i++) {
                                modemDataBuffer[i + 3] = usbdata[i];
                            }

                            byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_1K);
                            modemDataBuffer[1027] = crcHL[0];
                            modemDataBuffer[1028] = crcHL[1];
                            sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                            ymodemState = Y_MODEM_START_SEND_FILE_WAIT_ACK;
                        } else if (readcount > 0) // not a complete packet, send last data packet
                        {

                            for (int i = 0; i < readcount; i++) {
                                modemDataBuffer[i + 3] = usbdata[i];
                            }

                            for (int j = 0; j < (DATA_SIZE_1K - readcount); j++) {
                                modemDataBuffer[j + 3 + readcount] = 0x1a;        // no data, set as 0x1A(^Z)
                            }

                            byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_1K);
                            modemDataBuffer[1027] = crcHL[0];
                            modemDataBuffer[1028] = crcHL[1];
                            sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                            ymodemState = Y_MODEM_START_SEND_FILE_WAIT_ACK;
                        } else if (0 == readcount || -1 == readcount)// no data
                        {
                            ymodemState = Y_MODEM_SEND_EOT_PACKET;
                        }
                    }
                    continue;

                    case Y_MODEM_START_SEND_FILE_WAIT_ACK: {
                        iGetRespone = waitAck(60000);

                        if (DATA_NAK == iGetRespone) {
                            totalErrorCount++;
                            ymodemState = Y_MODEM_START_SEND_FILE_RESEND;
                        } else if (DATA_ACK == iGetRespone) {
                            ymodemState = Y_MODEM_START_SEND_FILE;
                        } else {
                            bSendFileProcess = false;
                        }
                    }
                    continue;
                    case Y_MODEM_START_SEND_FILE_RESEND: {
                        sendData(PACTET_SIZE_XMODEM_1K_CRC, modemDataBuffer);
                        ymodemState = Y_MODEM_START_SEND_FILE_WAIT_ACK;
                    }
                    continue;
                    case Y_MODEM_SEND_EOT_PACKET: {

                        sendData(EOT);
                        ymodemState = Y_MODEM_SEND_EOT_PACKET_WAIT_ACT;
                    }
                    continue;

                    case Y_MODEM_SEND_EOT_PACKET_WAIT_ACT: {
                        iGetRespone = waitAck(10000);
                        if (DATA_NAK == iGetRespone) {
                            ymodemState = Y_MODEM_SEND_EOT_PACKET;
                        } else if (DATA_ACK == iGetRespone) {
                            if (DATA_CHAR_C == waitAck(10000))
                                ymodemState = Y_MODEM_SEND_LAST_END_PACKET;
                        } else {
                            bSendFileProcess = false;
                        }
                    }
                    continue;

                    case Y_MODEM_SEND_LAST_END_PACKET: {
                        modemDataBuffer[0] = SOH;
                        modemDataBuffer[1] = 0;
                        modemDataBuffer[2] = ~0;
                        for (int i = 0; i < DATA_SIZE_128; i++) {
                            modemDataBuffer[i + 3] = 0;
                        }
                        byte[] crcHL = calCrc(modemDataBuffer, 3, DATA_SIZE_128);
                        modemDataBuffer[131] = crcHL[0];
                        modemDataBuffer[132] = crcHL[1];
                        sendData(PACTET_SIZE_XMODEM_CRC, modemDataBuffer);
                        ymodemState = Y_MODEM_SEND_LAST_END_PACKET_WAIT_ACK;
                    }
                    continue;

                    case Y_MODEM_SEND_LAST_END_PACKET_WAIT_ACK: {
                        iGetRespone = waitAck(40000);
                        if (DATA_NAK == iGetRespone) {
                            ymodemState = Y_MODEM_SEND_LAST_END_PACKET;
                        } else if (DATA_ACK == iGetRespone) {
                            ymodemState = Y_MODEM_SEND_FILE_DONE;
                        } else {
                            ymodemState = Y_MODEM_SEND_FILE_DONE;
                        }
                    }
                    continue;

                    case Y_MODEM_SEND_FILE_DONE: {
                        bSendFileProcess = false;
                    }
                    continue;

                    default:
                        // NG case, stop send file
                        bSendFileProcess = false;
                        break;
                }
            }


            transferMode = MODE_GENERAL_UART;
            bUartModeTaskSet = true;

            if (Y_MODEM_SEND_FILE_DONE == ymodemState) {
                end_time = System.currentTimeMillis();
                msg = mHandler.obtainMessage(UPDATE_SEND_FILE_DONE);
                mHandler.sendMessage(msg);
            } else if (false == bSendButtonClick) {
                msg = mHandler.obtainMessage(MSG_FORCE_STOP_SEND_FILE);
                mHandler.sendMessage(msg);
            } else if (Y_MODEM_START_SEND_FILE_WAIT_ACK == ymodemState) {
                msg = mHandler.obtainMessage(MSG_XMODEM_SEND_FILE_TIMEOUT);
                mHandler.sendMessage(msg);
            } else {
                msg = mHandler.obtainMessage(MSG_UNHANDLED_CASE);
                mHandler.sendMessage(msg);
            }
        }
    }

    int waitAck(int waitTime) {
        byte[] tmpdata = new byte[1];
        byte status;
        long time_1, time_2;
        time_1 = System.currentTimeMillis();

        do {
            if (false == bSendButtonClick)
                return DATA_NONE;

            if (iTotalBytes > 0) {
                status = readData(1, tmpdata);
                if (0x00 != status) {
                }

                if (NAK == tmpdata[0]) {
                    return DATA_NAK;
                } else if (ACK == tmpdata[0]) {
                    return DATA_ACK;
                } else if (CHAR_C == tmpdata[0]) {
                    return DATA_CHAR_C;
                } else {
                    time_2 = System.currentTimeMillis();
                }

            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                time_2 = System.currentTimeMillis();
            }
        }
        while ((time_2 - time_1) < waitTime);

        return DATA_NONE;
    }

    class YModemReadDataThread extends Thread // MODE_Y_MODEM_1K_CRC_RECEIVE - YModem-1K
    {
        Handler mHandler;

        YModemReadDataThread(Handler h) {
            mHandler = h;
            this.setPriority(MAX_PRIORITY);
        }

        public void run() {
            Message msg;
            byte status;
            int ymodemErrorCount = 0;
            boolean bYModemPktParseOK = false;
            long check_data_time_1 = 0, check_data_time_2;
            int ymodemPacketSize = 0;
            byte ackData = NAK;
            int waitCount = 0;
            int resendCount = 20;
            int tempDataCount;
            boolean bStopReceive = false;
            boolean bGetEOT = false;
            int parseFirstPktCount = 0;

            int getDataState = 0;

            bReadDataProcess = true;
            bReceiveFirstPacket = false;
            totalErrorCount = 0;

            while (bReadDataProcess) {

                waitCount = 0;
                resendCount = 20;
                tempDataCount = 0;

                while (modemReceiveDataBytes[0] < 1) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (false == bLogButtonClick) {
                        break;
                    }
                }

                if (STX == firstData()) // check packet size
                {
                    ymodemPacketSize = PACTET_SIZE_XMODEM_1K_CRC;
                } else {
                    ymodemPacketSize = PACTET_SIZE_XMODEM_CRC;
                }


                getDataState = 0;

                while (modemReceiveDataBytes[0] < ymodemPacketSize) {
                    waitCount++;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (EOT == firstData()) {
                        break;
                    }

                    if (false == bLogButtonClick) {
                        break;
                    }

                    // re-send nak/ack for xmomdem revice when there is no packet in for a period of time
                    if (10 == waitCount) {
                        tempDataCount = modemReceiveDataBytes[0];
                    } else if (waitCount > resendCount) {
                        resendCount += 10;
                        if (tempDataCount == modemReceiveDataBytes[0]) // no incoming data
                        {
                            if (true == bDataReceived) // transfer starting
                            {
                                if (0 == getDataState) {
                                    if (modemReceiveDataBytes[0] == 0) {
                                        sendData(ackData);
                                    } else {
                                        getDataState = 1;
                                    }
                                } else if (1 == getDataState) {
                                    getDataState = 2;
                                } else if (2 == getDataState) {
                                    getDataState = 3;

                                } else if (3 == getDataState) {

                                    readData(iTotalBytes, modemDataBuffer);
                                    iTotalBytes = 0;
                                    modemReceiveDataBytes[0] = 0;

                                    sendData(NAK);
                                }
                            }
                        } else if (false == bDataReceived) {
                            bDataReceived = true;
                            msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA);
                            mHandler.sendMessage(msg);
                        } else {
                            getDataState = 1;
                            tempDataCount = modemReceiveDataBytes[0];
                        }
                    }

                    // stop when long time no data 6000 * 10 ms = 60 sec.
                    if (waitCount > 6000) {
                        bStopReceive = true;
                        break;
                    }
                }

                if (false == bLogButtonClick) {
                    msg = mHandler.obtainMessage(MSG_FORCE_STOP_SAVE_TO_FILE);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                if (bStopReceive) {
                    msg = mHandler.obtainMessage(MSG_MODEM_RECEIVE_PACKET_TIMEOUT);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                if (EOT == firstData()) {
                    readData(1, modemDataBuffer);
                    end_time = System.currentTimeMillis();
                    bGetEOT = true;
                    sendData(ACK);
                    sendData(CHAR_C);
                    continue;
                }

                status = readData(ymodemPacketSize, modemDataBuffer);

                if (true == bGetEOT) {
                    sendData(ACK);
                    msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DONE);
                    mHandler.sendMessage(msg);
                    transferMode = MODE_GENERAL_UART;
                    bUartModeTaskSet = true;
                    bReadDataProcess = false;
                    continue;
                }

                if (0x00 != status) {
                    //midToast("XMRead - status error",Toast.LENGTH_SHORT); Exception: Do not update UI in other thread!
                }


                if (false == bReceiveFirstPacket) {
                    // parse first packet for filename, filesize
                    bYModemPktParseOK = parseYModemFirstPacket();

                    if (false == bYModemPktParseOK) {
                        parseFirstPktCount++;

                        if (10 == parseFirstPktCount) {
                            msg = mHandler.obtainMessage(MSG_YMODEM_PARSE_FIRST_PACKET_FAIL);
                            mHandler.sendMessage(msg);
                            // In this case, the saved file is not open correctly, so need to skip the action to close file
                            transferMode = MODE_GENERAL_UART;
                            bUartModeTaskSet = true;
                            bReadDataProcess = false;
                            continue;
                        } else {
                            sendData(NAK);
                            continue;
                        }
                    } else if (false == openModemSaveFile()) {
                        msg = mHandler.obtainMessage(MSG_MODEM_OPEN_SAVE_FILE_FAIL);
                        mHandler.sendMessage(msg);

                        // In this case, the saved file is not open correctly, so need to skip the action to close file
                        transferMode = MODE_GENERAL_UART;
                        bUartModeTaskSet = true;
                        bReadDataProcess = false;
                        continue;
                    }
                } else {
                    // parse packet
                    bYModemPktParseOK = parseModemPacket();
                }

                if (true == bReceiveFirstPacket) {
                    check_data_time_2 = System.currentTimeMillis();
                    if ((check_data_time_2 - check_data_time_1) >= 200) // update progress every 200 milliseconds
                    {
                        msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA_BYTES);
                        mHandler.sendMessage(msg);
                        check_data_time_1 = check_data_time_2;
                    }
                }

                if (true == bYModemPktParseOK) {

                    ymodemErrorCount = 0;
                    // write received data to data area or update user area
                    if (false == bReceiveFirstPacket) {
                        // notify receiving process starting
                        check_data_time_1 = System.currentTimeMillis();
                        start_time = System.currentTimeMillis();
                        msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA);
                        mHandler.sendMessage(msg);
                        bReceiveFirstPacket = true;
                        bDataReceived = true;
                        sendData(ACK);
                        ackData = CHAR_C;
                    } else if (true == bDuplicatedPacket) {
                        bDuplicatedPacket = false;
                        ackData = ACK;
                    } else {
                        receivedPacketNumber++;
                        ackData = ACK;
                    }
                } else {
                    ymodemErrorCount++;
                    totalErrorCount++;

                    if (ymodemErrorCount > 10) {
                        msg = mHandler.obtainMessage(MSG_MODEM_RECEIVE_PACKET_TIMEOUT);
                        mHandler.sendMessage(msg);
                        transferMode = MODE_GENERAL_UART;
                        bUartModeTaskSet = true;
                        bReadDataProcess = false;
                        continue;
                    }
                    ackData = NAK;
                }
                sendData(ackData);
            }
        }
    }


    boolean parseYModemFirstPacket() {
        boolean parseOK = true;
        char filename[] = new char[128];
        char filesize[] = new char[12];

        modemReceiveDataBytes[0] -= PACTET_SIZE_XMODEM_CRC;


        if (modemDataBuffer[0] != SOH) {
            parseOK = false;
        }

        if (modemDataBuffer[1] != 0x00) {
            parseOK = false;
        }

        if (modemDataBuffer[2] != ~modemDataBuffer[1]) {
            parseOK = false;
        }

        if (true == parseOK) {
            int i = 0;
            while (modemDataBuffer[i + 3] != 0x00) {
                filename[i] = (char) modemDataBuffer[i + 3];
                i++;
            }

            i++;
            int j = 0;
            while ((modemDataBuffer[j + i + 3] != 0x00) && ((j + i + 3) < (PACTET_SIZE_XMODEM_CRC - 1))) {
                filesize[j] = (char) modemDataBuffer[j + i + 3];
                j++;
            }


            modemFileName = String.copyValueOf(filename, 0, i - 1);
            modemFileSize = String.copyValueOf(filesize, 0, j);

        }

        return parseOK;
    }
// YModem - ===============================================================================================

    // ZModem + ===============================================================================================
    class ZModemSendFileThread extends Thread {
        Handler mHandler;
        FileInputStream instream;

        ZModemSendFileThread(Handler h, FileInputStream stream) {
            mHandler = h;
            instream = stream;
            this.setPriority(Thread.MAX_PRIORITY);
        }

        public void run() {
            Message msg;
            boolean bSendFileDone = false;
            boolean bSendFileProcess = true;
            byte[] tempBuffer = new byte[2048];
            byte[] crcHL = new byte[2];

            // TODO: dynamic setting size for different baud rate?
            /*
             *  256 bytes below 2400 bps, 512 at 2400 bps,
             *  and 1024 above 4800 bps	or when	the data link is known to be relatively error free.
             */
            int zmReadDataSize = 0;
//			if(baudRate < 2400)
//				zmReadDataSize= DATA_SIZE_256;
//			else if(2400 == baudRate)
//				zmReadDataSize= DATA_SIZE_512;
//			else
//				zmReadDataSize= DATA_SIZE_1K;

            zmReadDataSize = DATA_SIZE_512;
            int readcount = 0;

            sendByteCount = 0;
            zmodemState = ZRQINIT;

            cal_time_1 = System.currentTimeMillis();
            start_time = System.currentTimeMillis();

            while (true == bSendFileProcess) {
                if (false == bSendButtonClick)
                    break;

                if (zmodemState >= ZDATA) {
                    cal_time_2 = System.currentTimeMillis();
                    if ((cal_time_2 - cal_time_1) > 200) // update status every 200ms
                    {
                        msg = mHandler.obtainMessage(UPDATE_SEND_FILE_STATUS);
                        mHandler.sendMessage(msg);
                        cal_time_1 = cal_time_2;
                    }
                }

                switch (zmodemState) {
                    case ZRQINIT:
                        tempBuffer[0] = 0x72; // r
                        tempBuffer[1] = 0x7A; // z
                        tempBuffer[2] = 0x0D; // \r
                        tempBuffer[3] = 0x2A; // ZPAD
                        tempBuffer[4] = 0x2A; // ZPAD
                        tempBuffer[5] = 0x18; // ZDLE
                        tempBuffer[6] = 0x42; // ZHEX
                        tempBuffer[7] = 0x30;
                        tempBuffer[8] = 0x30; //2
                        tempBuffer[9] = 0x30;
                        tempBuffer[10] = 0x30; //4
                        tempBuffer[11] = 0x30;
                        tempBuffer[12] = 0x30; //6
                        tempBuffer[13] = 0x30;
                        tempBuffer[14] = 0x30; //8
                        tempBuffer[15] = 0x30;
                        tempBuffer[16] = 0x30; //10
                        tempBuffer[17] = 0x30;
                        tempBuffer[18] = 0x30; //12
                        tempBuffer[19] = 0x30;
                        tempBuffer[20] = 0x30; //14
                        tempBuffer[21] = 0x0D;
                        tempBuffer[22] = 0x0A;
                        tempBuffer[23] = 0x11;
                        sendData(24, tempBuffer);

                        zmodemState = ZRINIT;
                        break;


                    case ZRINIT:

                        if (true == zmWaitReadData(21, 0, 5000)) {
                            int frame = zmGetFrameType(modemDataBuffer, 0);
                            if (ZRINIT == frame) {
                                zmodemState = ZFILE;
                            } else {
                                zmodemState = ZRQINIT;
                            }
                        } else {
                            zmodemState = ZRQINIT;
                        }
                        break;

                    case ZFILE: {
                        byte[] bFileName = sFileName.getBytes();
                        byte[] bFileSize = Integer.toString(iFileSize).getBytes();
                        int lenName = bFileName.length;
                        int lenSize = bFileSize.length;


                        tempBuffer[0] = ZPAD;
                        tempBuffer[1] = ZDLE;
                        tempBuffer[2] = ZBIN;
                        tempBuffer[3] = 0x04; // ZFILE
                        tempBuffer[4] = 0x00;
                        tempBuffer[5] = 0x00;
                        tempBuffer[6] = 0x00;
                        tempBuffer[7] = 0x00;    // PComm
                        tempBuffer[8] = (byte) 0x89;
                        tempBuffer[9] = 0x06;
//					modemDataBuffer[7] = 0x01; // XP-HT: ZBIN
//					modemDataBuffer[8] = (byte)0x99;
//					modemDataBuffer[9] = 0x27;

                        sendData(10, tempBuffer);

                        int i;

                        for (i = 0; i < lenName; i++) {
                            tempBuffer[i] = bFileName[i];
                        }
                        tempBuffer[i++] = 0x00;

                        int j;
                        for (j = i; j < lenSize + i; j++) {
                            tempBuffer[j] = bFileSize[j - i];
                        }

                        tempBuffer[j++] = 0x00;
                        crcHL = calCrc(tempBuffer, 0, j);

                        tempBuffer[j++] = ZDLE;
                        tempBuffer[j++] = ZCRCW;
                        accumulateCrc(crcHL, tempBuffer, j - 1, 1);


                        j = zmAppendCRC(tempBuffer, crcHL, j);

                        tempBuffer[j++] = 0x11;
                        sendData(j, tempBuffer);

                        zmodemState = ZRPOS;
                    }
                    break;

                    case ZRPOS:
                        // TODO: handle error cases
//				    final int ZSKIP = 5;     /* To sender: skip this file */
//				    final int ZNAK = 6;      /* Last packet was garbled */
//				    final int ZABORT = 7;    /* Abort batch transfers */

                        if (true == zmWaitReadData(21, 0, 5000)) {
                            int frame = zmGetFrameType(modemDataBuffer, 0);

                            if (ZRPOS == frame) {
                                zmodemState = ZDATA_HEADER;
                            } else if (ZSKIP == frame || ZABORT == frame) {
                                zmodemState = ZOO;
                            } else {
                                zmodemState = ZFILE;
                            }
                        } else {
                            zmodemState = ZFILE;
                        }
                        break;

                    case ZDATA_HEADER:
                        tempBuffer[0] = ZPAD;
                        tempBuffer[1] = ZDLE;
                        tempBuffer[2] = ZBIN;
                        tempBuffer[3] = 0x0A; // ZDATA
                        tempBuffer[4] = 0x00;
                        tempBuffer[5] = 0x00;
                        tempBuffer[6] = 0x00;
                        tempBuffer[7] = 0x00;
                        tempBuffer[8] = 0x46;
                        tempBuffer[9] = (byte) 0xAE;
                        sendData(10, tempBuffer);
                        zmodemState = ZDATA;

                    case ZDATA:
                        try {
                            readcount = instream.read(tempBuffer, 0, zmReadDataSize);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (readcount > 0) {
                            sendByteCount += readcount;
                        }

                        if (readcount > 0) {

                            crcHL = calCrc(tempBuffer, 0, readcount);

                            int numSendDataBytes = zmGenerateDataPacket(tempBuffer, readcount);

                            zmDataBuffer[numSendDataBytes] = ZDLE;

                            int k = 1;
                            if (readcount == zmReadDataSize && sendByteCount != iFileSize) {
                                zmDataBuffer[numSendDataBytes + k] = ZCRCG;
                            } else // not a complete packet or send all data, send last data packet
                            {
                                zmDataBuffer[numSendDataBytes + k] = ZCRCE;
                            }

                            accumulateCrc(crcHL, zmDataBuffer[numSendDataBytes + k]);
                            k++; // 2

                            if (ZDLE == crcHL[0] ||
                                    0x10 == crcHL[0] || 0x11 == crcHL[0] || 0x13 == crcHL[0] ||
                                    (byte) 0x90 == crcHL[0] || (byte) 0x91 == crcHL[0] || (byte) 0x93 == crcHL[0]
                                    || ((crcHL[0] & 0x60) == 0)) {
                                zmDataBuffer[numSendDataBytes + (k++)] = ZDLE;
                                crcHL[0] ^= 0x40;
                                zmDataBuffer[numSendDataBytes + (k++)] = crcHL[0]; //PComm treat ZDLE crc, k:3
                            } else {
                                zmDataBuffer[numSendDataBytes + (k++)] = crcHL[0];
                            }

                            if (ZDLE == crcHL[1] ||
                                    0x10 == crcHL[1] || 0x11 == crcHL[1] || 0x13 == crcHL[1] ||
                                    (byte) 0x90 == crcHL[1] || (byte) 0x91 == crcHL[1] || (byte) 0x93 == crcHL[1]
                                    || ((crcHL[1] & 0x60) == 0)) {
                                zmDataBuffer[numSendDataBytes + (k++)] = ZDLE;
                                crcHL[1] ^= 0x40;
                                zmDataBuffer[numSendDataBytes + (k++)] = crcHL[1]; //PComm treat ZDLE crc, k:4 or 5
                            } else {
                                zmDataBuffer[numSendDataBytes + (k++)] = crcHL[1];
                            }


                            sendData(numSendDataBytes + k, zmDataBuffer);

                            if (readcount != zmReadDataSize || sendByteCount == iFileSize) {
                                bSendFileDone = true;
                                zmodemState = ZEOF;
                            }
                        } else {
                            if (true == INTERNAL_DEBUG_TRACE) {
                                msg = mHandler.obtainMessage(MSG_UNHANDLED_CASE, String.valueOf("ZM send: ZDATA unhandle case!!"));
                                mHandler.sendMessage(msg);
                            }
                        }
                        break;

                    case ZEOF:

                        int offset = iFileSize;

                        tempBuffer[0] = 0x2A; // ZPAD
                        tempBuffer[1] = 0x18; // ZDLE
                        tempBuffer[2] = 0x41; // ZBIN
                        tempBuffer[3] = 0x0B; // ZEOF

                        tempBuffer[4] = (byte) (offset);
                        tempBuffer[5] = (byte) (offset >> 8);
                        tempBuffer[6] = (byte) (offset >> 16);
                        tempBuffer[7] = (byte) (offset >> 24);

                        crcHL = calCrc(tempBuffer, 3, 5); // calculate CRC first before padding zdle

                        int i = 4;
                        tempBuffer[i] = (byte) (offset);
                        i = zmCheckDLE(tempBuffer, tempBuffer[i], i);
                        tempBuffer[i] = (byte) (offset >> 8);
                        i = zmCheckDLE(tempBuffer, tempBuffer[i], i);
                        tempBuffer[i] = (byte) (offset >> 16);
                        i = zmCheckDLE(tempBuffer, tempBuffer[i], i);
                        tempBuffer[i] = (byte) (offset >> 24);
                        i = zmCheckDLE(tempBuffer, tempBuffer[i], i);

                        int j = i;

                        j = zmAppendCRC(tempBuffer, crcHL, j);

                        sendData(j, tempBuffer); // send ZEOF

                        if (true == zmWaitReadData(21, 0, 5000)) {
                            int frame = zmGetFrameType(modemDataBuffer, 0);
                            if (ZRINIT == frame) {
                                zmodemState = ZFIN;
                            } else {
                            }
                        } else {
                        }
                        break;

                    case ZFIN:
                        tempBuffer[0] = 0x2A; // ZPAD
                        tempBuffer[1] = 0x2A; // ZPAD
                        tempBuffer[2] = 0x18; // ZDLE
                        tempBuffer[3] = 0x42; // ZHEX
                        tempBuffer[4] = 0x30;
                        tempBuffer[5] = 0x38;
                        tempBuffer[6] = 0x30;
                        tempBuffer[7] = 0x30;
                        tempBuffer[8] = 0x30;
                        tempBuffer[9] = 0x30;
                        tempBuffer[10] = 0x30;
                        tempBuffer[11] = 0x30;
                        tempBuffer[12] = 0x30;
                        tempBuffer[13] = 0x30;
                        tempBuffer[14] = 0x30;
                        tempBuffer[15] = 0x32;
                        tempBuffer[16] = 0x32;
                        tempBuffer[17] = 0x64;
                        tempBuffer[18] = 0x0d;
                        tempBuffer[19] = (byte) 0x8a;
                        sendData(20, tempBuffer); // send ZFIN

                        zmodemState = ZFIN_ACK;
                        break;

                    case ZFIN_ACK: {
                        if (true == zmWaitReadData(20, 0, 10000)) // get ZFIN
                        {
                            int frame = zmGetFrameType(modemDataBuffer, 0);
                            if (ZFIN == frame) {
                                zmodemState = ZOO;
                            } else if (ZRINIT == frame) {
                                zmWaitReadData(1, 0, 10000); // ZRINIT, should read one more data
                                zmodemState = ZFIN;
                            } else {
                                zmodemState = ZFIN;
                            }
                        } else {
                            zmodemState = ZFIN;
                        }
                    }
                    break;

                    case ZOO:
                        tempBuffer[0] = 0x4f;
                        tempBuffer[1] = 0x4f;
                        sendData(2, tempBuffer);

                        bSendFileDone = true;
                        bSendFileProcess = false;
                        break;

                    default:
                        break;
                }
            }

            transferMode = MODE_GENERAL_UART;
            bUartModeTaskSet = true;

            if (true == bSendFileDone) {
                end_time = System.currentTimeMillis();
                msg = mHandler.obtainMessage(UPDATE_SEND_FILE_DONE);
                mHandler.sendMessage(msg);
            } else if (false == bSendButtonClick) {
                msg = mHandler.obtainMessage(MSG_FORCE_STOP_SEND_FILE);
                mHandler.sendMessage(msg);
            } else {
                msg = mHandler.obtainMessage(MSG_UNHANDLED_CASE);
                mHandler.sendMessage(msg);
            }
        }
    }

    class ZModemReadDataThread extends Thread {
        Handler mHandler;

        ZModemReadDataThread(Handler h) {
            mHandler = h;
            this.setPriority(MAX_PRIORITY);
        }

        public void run() {
            Message msg;
            boolean bFileReciveDone = false;
            int[] getDataNum = new int[1];
            byte[] tempBuffer = new byte[24];

            bReadDataProcess = true;
            totalModemReceiveDataBytes = 0;

            cal_time_1 = System.currentTimeMillis();

            while (bReadDataProcess) {

                if (false == bLogButtonClick) {
                    msg = mHandler.obtainMessage(MSG_FORCE_STOP_SAVE_TO_FILE);
                    mHandler.sendMessage(msg);
                    continue;
                }

                if (zmodemState >= ZDATA) {
                    cal_time_2 = System.currentTimeMillis();
                    if ((cal_time_2 - cal_time_1) > 200) // update status every 200ms
                    {
                        msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA_BYTES);
                        mHandler.sendMessage(msg);
                        cal_time_1 = cal_time_2;
                    }
                }

                switch (zmodemState) {
                    case ZRQINIT:
                        if (true == zmWaitReadData(3, 0, 20000)) {
                            if (modemDataBuffer[0] == 0x72 &&  // r
                                    modemDataBuffer[1] == 0x7A &&  // z
                                    modemDataBuffer[2] == 0x0D)      // \r
                            {
                                start_time = System.currentTimeMillis();

                                if (true == zmWaitReadData(21, 0, 10000)) {
                                    if (zmGetHeaderType() > 0) {
                                        zmodemState = ZRINIT;

                                        msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DATA);
                                        mHandler.sendMessage(msg);
                                    } else {
                                    }
                                }
                            } else {
                            }
                        } else {
                        }

                        break;

                    case ZEOF:
                    case ZRINIT:
                        // TODO: how to set ZRINIT packet
                        tempBuffer[0] = 0x2A;
                        tempBuffer[1] = 0x2A;
                        tempBuffer[2] = 0x18;
                        tempBuffer[3] = 0x42;
                        tempBuffer[4] = 0x30;
                        tempBuffer[5] = 0x31;
                        tempBuffer[6] = 0x30;
                        tempBuffer[7] = 0x30;
                        tempBuffer[8] = 0x30;
                        tempBuffer[9] = 0x30;
                        tempBuffer[10] = 0x30;
                        tempBuffer[11] = 0x30;
                        tempBuffer[12] = 0x30;
                        tempBuffer[13] = 0x33;
                        tempBuffer[14] = 0x39;
                        tempBuffer[15] = 0x61;
                        tempBuffer[16] = 0x33;
                        tempBuffer[17] = 0x32;
                        tempBuffer[18] = 0x0D;
                        tempBuffer[19] = 0x0A;
                        tempBuffer[20] = 0x11;
                        sendData(21, tempBuffer);

                        if (ZRINIT == zmodemState) {
                            zmodemState = ZFILE;
                        } else if (ZEOF == zmodemState) {
                            zmodemState = ZFIN;
                        }
                        break;

                    case ZFILE:

                        if (true == zmWaitReadData(10, 0, 10000)) {
                            if (modemDataBuffer[0] == ZPAD &&
                                    modemDataBuffer[1] == ZDLE &&
                                    modemDataBuffer[2] == ZBIN &&
                                    modemDataBuffer[3] == ZFILE) {
                                // file info packet size:  fname + 1 + fsize + 1 + 5 -> 9
                                if (true == zmWaitReadData(9, 0, 1000)) {
                                    getDataNum[0] = 9;
                                } else {
                                    getDataNum[0] = 0;
                                }

                                zmWaitFileInfoData(getDataNum);

                                zmParseFileInfo(getDataNum[0]);

                                if (false == openModemSaveFile()) {
                                    msg = mHandler.obtainMessage(MSG_MODEM_OPEN_SAVE_FILE_FAIL);
                                    mHandler.sendMessage(msg);
                                    bReadDataProcess = false;
                                    continue;
                                }
                            } else {
                            }
                        }


                        zmodemState = ZRPOS;

                        try {                    // clear remain data after zdle zcrcw
                            Thread.sleep(300);
                        } catch (Exception e) {
                        }
                        zmReadAllData(1000);

                        msg = mHandler.obtainMessage(UPDATE_ZMODEM_STATE_INFO);
                        mHandler.sendMessage(msg);
                        break;

                    case ZRPOS:
                        // TODO: how to set ZRPOS packet
                        tempBuffer[0] = 0x2A;
                        tempBuffer[1] = 0x2A;
                        tempBuffer[2] = 0x18;
                        tempBuffer[3] = 0x42;
                        tempBuffer[4] = 0x30;
                        tempBuffer[5] = 0x39;
                        tempBuffer[6] = 0x30;
                        tempBuffer[7] = 0x30;
                        tempBuffer[8] = 0x30;
                        tempBuffer[9] = 0x30;
                        tempBuffer[10] = 0x30;
                        tempBuffer[11] = 0x30;
                        tempBuffer[12] = 0x30;
                        tempBuffer[13] = 0x30;
                        tempBuffer[14] = 0x61;
                        tempBuffer[15] = 0x38;
                        tempBuffer[16] = 0x37;
                        tempBuffer[17] = 0x63;
                        tempBuffer[18] = 0x0D;
                        tempBuffer[19] = 0x0A;
                        tempBuffer[20] = 0x11;
                        sendData(21, tempBuffer);

                        zmodemState = ZDATA_HEADER;

                        break;

                    case ZDATA_HEADER:
                        if (true == zmWaitReadData(10, 0, 10000)) {
                            for (int i = 0; i < 10; i++)

                                if (modemDataBuffer[0] == ZPAD &&
                                        modemDataBuffer[1] == ZDLE &&
                                        modemDataBuffer[2] == ZBIN &&
                                        modemDataBuffer[3] == 0x0A) // ZDATA
                                {
                                    zmodemState = ZDATA;
                                } else {
                                    bReadDataProcess = false;
                                }
                        } else {
                            bReadDataProcess = false;
                        }

                        break;

                    case ZDATA: {
                        getDataNum[0] = zmReadAllData(1000);

                        int zeofPos = zmCheckZEOF(getDataNum);
                        int numSaveDataBytes = zmParseDataPacket(getDataNum, zeofPos);

                        try {
                            buf_save.write(zmDataBuffer, 0, numSaveDataBytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        totalModemReceiveDataBytes += numSaveDataBytes;

                        if (zeofPos != -1) {
                            zmodemState = ZEOF;
                            bFileReciveDone = true;
                        }
                    }
                    break;

                    case ZFIN: {
                        if (true == zmWaitReadData(20, 0, 10000)) // get ZFIN
                        {
                            tempBuffer[0] = 0x2A; // ZPAD
                            tempBuffer[1] = 0x2A; // ZPAD
                            tempBuffer[2] = 0x18; // ZDLE
                            tempBuffer[3] = 0x42; // ZHEX
                            tempBuffer[4] = 0x30;
                            tempBuffer[5] = 0x38;
                            tempBuffer[6] = 0x30;
                            tempBuffer[7] = 0x30;
                            tempBuffer[8] = 0x30;
                            tempBuffer[9] = 0x30;
                            tempBuffer[10] = 0x30;
                            tempBuffer[11] = 0x30;
                            tempBuffer[12] = 0x30;
                            tempBuffer[13] = 0x30;
                            tempBuffer[14] = 0x30;
                            tempBuffer[15] = 0x32;
                            tempBuffer[16] = 0x32;
                            tempBuffer[17] = 0x64;
                            tempBuffer[18] = 0x0d;
                            tempBuffer[19] = (byte) 0x8a;
                            sendData(20, tempBuffer); // send ZFIN
                            zmodemState = ZOO;
                        } else {
                            bReadDataProcess = false;
                        }
                    }
                    break;

                    case ZOO:
                        if (true == zmWaitReadData(2, 0, 10000)) // get ZFIN
                        {
                            if (0x4F == modemDataBuffer[0] && 0x4F == modemDataBuffer[1]) {
                                bReadDataProcess = false;
                            } else {
                                bReadDataProcess = false;

                                int num = zmReadAllData(10);
                            }
                        } else {
                            bReadDataProcess = false;
                        }

                        break;

                    default:
                        break;
                }
            }

            transferMode = MODE_GENERAL_UART;
            bUartModeTaskSet = true;
            end_time = System.currentTimeMillis();
            if (true == bFileReciveDone) {
                msg = mHandler.obtainMessage(UPDATE_MODEM_RECEIVE_DONE);
                mHandler.sendMessage(msg);
            } else if (true == bLogButtonClick) {
                msg = mHandler.obtainMessage(MSG_MODEM_RECEIVE_PACKET_TIMEOUT);
                mHandler.sendMessage(msg);
            }
        }

    }

    boolean zmParseFileInfo(int dataNum) {
        boolean parseOK = true;
        char filename[] = new char[128];
        char filesize[] = new char[12];


        int i = 0;
        while (modemDataBuffer[i] != 0x00) {
            filename[i] = (char) modemDataBuffer[i];
            i++;
        }

        i++;
        int j = 0;
        // file size: xp hyperterm  end with 0x20, pcomm end with 0x00
        while ((modemDataBuffer[j + i] != 0x00 && modemDataBuffer[j + i] != 0x20) && ((j + i) < (dataNum - 1))) {
            filesize[j] = (char) modemDataBuffer[j + i];
            j++;
        }


        modemFileName = String.copyValueOf(filename, 0, i - 1);
        modemFileSize = String.copyValueOf(filesize, 0, j);


        return parseOK;
    }

    int zmReadAllData(int waitTime) // for Z modem read data
    {
        long time_1, time_2;
        time_1 = System.currentTimeMillis();

        do {
            if (modemReceiveDataBytes[0] > 0) {
                int dataNum = modemReceiveDataBytes[0];
                if (dataNum > DATA_SIZE_256)        // read 256 bytes data each time
                {
                    dataNum = DATA_SIZE_256;
                }

                readData(dataNum, modemDataBuffer);
                modemReceiveDataBytes[0] -= dataNum;
                return dataNum;
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (false == bLogButtonClick && false == bSendButtonClick) {
                return 0;
            }

            time_2 = System.currentTimeMillis();
        }
        while ((time_2 - time_1) < waitTime);

        return 0;
    }

    int zmCheckZEOF(int[] dataNum) {
        for (int i = 0; i < dataNum[0]; i++) {
            if (ZPAD == modemDataBuffer[i]) {

                if (dataNum[0] < i + 3 + 1) // check whether there is enough data for parsing: zeof
                {
                    zmWaitReadData((i + 3 + 1 - dataNum[0]), dataNum[0], 1000);
                    dataNum[0] += (i + 3 + 1 - dataNum[0]);
                }

                if ((i + 3 + 1) <= dataNum[0]) {
                    if (ZDLE == modemDataBuffer[i + 1] &&
                            ZBIN == modemDataBuffer[i + 2] &&
                            0x0b == modemDataBuffer[i + 3]) // ZEOF
                    {

                        if (dataNum[0] < i + 9 + 1) // ZEOF, get remain data
                        {
                            zmWaitReadData((i + 9 + 1 - dataNum[0]), dataNum[0], 2000);
                            dataNum[0] += (i + 9 + 1 - dataNum[0]);
                        }

                        return i;
                    }
                } else {
                    if (true == INTERNAL_DEBUG_TRACE) {
                        Message msg = handler.obtainMessage(MSG_UNHANDLED_CASE, String.valueOf("zmCheckZEOF unhandle case"));
                        handler.sendMessage(msg);
                    }
                }
            }
        }
        return -1;
    }

    boolean zmWaitReadData(int numByte, int offset, int waitTime) // for Z modem read data
    {
        long time_1, time_2;
        time_1 = System.currentTimeMillis();

        if (offset != 0) {
        }

        do {
            if (modemReceiveDataBytes[0] >= numByte) {
                readData(numByte, offset, modemDataBuffer);
                modemReceiveDataBytes[0] -= numByte;
                return true;
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (false == bLogButtonClick && false == bSendButtonClick) {
                return false;
            }

            time_2 = System.currentTimeMillis();
        }
        while ((time_2 - time_1) < waitTime);

        return false;
    }

    void zmWaitFileInfoData(int[] dataNum) {

        int i = 0;
        while (true) {
            if (dataNum[0] < i + 1 + 1) {
                zmWaitReadData(1, dataNum[0], 1000); // // check whether there is enough data for parsing: zlde zcrcw
                dataNum[0] += 1;

            }

            if (ZDLE == modemDataBuffer[i])  // find zdle
            {
                if (ZCRCW == modemDataBuffer[i + 1]) {
                    return;
                }
            }

            i++;

            if (i > 256)
                break;
        }
    }

    int zmGetHeaderType() {
        byte headerType = 0;

        String logStr = new String("Data: ");
        for (int i = 0; i < 21; i++) {
            logStr += Integer.toHexString(modemDataBuffer[i]) + " ";
        }

        if (modemDataBuffer[0] != ZPAD) {
            return -1;
        }

        if (ZPAD == modemDataBuffer[1]) {

            if (modemDataBuffer[2] != ZDLE) {
                return -1;
            }

            if (modemDataBuffer[3] != ZHEX) {
                return -1;
            } else {
                headerType = ZHEX;
            }
        } else if (ZDLE == modemDataBuffer[1]) {

            if (ZBIN == modemDataBuffer[2]) {
                headerType = ZBIN;
            } else if (ZBIN32 == modemDataBuffer[2]) {
                headerType = ZBIN32;
            } else {
                return -1;
            }
        } else {
            return -1;
        }


        switch (headerType) {
            case ZHEX:
                break;
            case ZBIN:
                break;
            case ZBIN32:
                break;
            default:
                break;
        }

        return (headerType);
    }

    int zmGetFrameType(byte[] dataBuf, int offset) {
        int frameType = -1;

        if (dataBuf[offset] == 0x2A && dataBuf[offset + 1] == 0x2A && dataBuf[offset + 2] == 0x18 &&
                dataBuf[offset + 3] == 0x42 && dataBuf[offset + 4] == 0x30) {
            switch (dataBuf[offset + 5]) {
                case 0x31:
                    frameType = ZRINIT;
                    break;
                case 0x35:
                    frameType = ZSKIP;
                    break;
                case 0x36:
                    frameType = ZNAK;
                    break;
                case 0x37:
                    frameType = ZABORT;
                    break;
                case 0x38:
                    frameType = ZFIN;
                    break;
                case 0x39:
                    frameType = ZRPOS;
                    break;
                default:
                    break;
            }
        } else {
        }

        return frameType;
    }

    int zmGenerateDataPacket(byte[] dataBuf, int dataNum) {
        int j = 0;

        for (int i = 0; i < dataNum; i++) {
            zmDataBuffer[j++] = dataBuf[i];

            switch (dataBuf[i]) {
                case ZDLE:
                case 0x10:
                case (byte) 0x90:
                case 0x11:
                case (byte) 0x91:
                case 0x13:
                case (byte) 0x93:
                    zmDataBuffer[j - 1] = ZDLE;
                    dataBuf[i] ^= 0x40;
                    zmDataBuffer[j++] = dataBuf[i];
                    break;
                default:
                    if ((dataBuf[i] & 0x60) == 0) {
                        zmDataBuffer[j - 1] = ZDLE;
                        dataBuf[i] ^= 0x40;
                        zmDataBuffer[j++] = dataBuf[i];
                    }
                    break;
            }
        }

        return j;
    }

    int zmCheckDLE(byte[] dataBuf, byte data, int i) {
        dataBuf[i++] = data;

        switch (data) {
            case ZDLE:
            case 0x10:
            case (byte) 0x90:
            case 0x11:
            case (byte) 0x91:
            case 0x13:
            case (byte) 0x93:
                dataBuf[i - 1] = ZDLE;
                data ^= 0x40;
                dataBuf[i++] = data;
                break;
            default:
                //				if((dataBuf[i] & 0x60) == 0)
                //				{
                //					dataBuf[i-1] = ZDLE;
                //					data ^= 0x40;
                //					dataBuf[i++] = data;
                //				}
                break;
        }


        return i;
    }

    int zmAppendCRC(byte[] dataBuf, byte[] crcHL, int i) {
        for (int j = 0; j < 2; j++) {
            dataBuf[i++] = crcHL[j];

            switch (crcHL[j]) {
                case ZDLE:
                case 0x10:
                case (byte) 0x90:
                case 0x11:
                case (byte) 0x91:
                case 0x13:
                case (byte) 0x93:
                    dataBuf[i - 1] = ZDLE;
                    crcHL[j] ^= 0x40;
                    dataBuf[i++] = crcHL[j];
                    break;
                default:

//				if((dataBuf[i] & 0x60) == 0)
//				{
//					dataBuf[i-1] = ZDLE;
//					crcHL[j] ^= 0x40;
//					dataBuf[i++] = crcHL[j];
//				}
                    break;
            }
        }

        return i;
    }

    // Input readdata and zeofpos, save data to zmDataBuffer, return data number for saving
    int zmParseDataPacket(int[] dataNum, int zeofPos) {
        int j = 0;
        int frameendByte = ZDLE_END_SIZE_4;

        if (zeofPos >= 0) {
            dataNum[0] = zeofPos;
        }

        for (int i = 0; i < dataNum[0]; i++) {
            if (ZDLE == modemDataBuffer[i])  // find zdle
            {
                if (0 == i)

                    // check whether there is enough data for parsing: zlde frameend crc1 crc2
                    if (dataNum[0] < i + 3 + 1) {

                        zmWaitReadData((i + 3 + 1 - dataNum[0]), dataNum[0], 1000);
                        dataNum[0] += (i + 3 + 1 - dataNum[0]);

                    }


                // check next char is special word or not

                if (((modemDataBuffer[i + 1] & 0x40) != 0) && ((modemDataBuffer[i + 1] & 0x20) == 0)) {
                    modemDataBuffer[i + 1] &= 0xbf;
                    zmDataBuffer[j++] = modemDataBuffer[i + 1];
                    i++; // i+1 is converted, skip this one

                    // not zdle frame, keep scan next char
                } else {

                    // check two zdle case
                    if (ZDLE == modemDataBuffer[i + 2] || ZDLE == modemDataBuffer[i + 3]) {
                        zmWaitReadData(1, dataNum[0], 1000);
                        dataNum[0] += 1;
                        frameendByte = ZDLE_END_SIZE_5;

                        if (ZDLE == modemDataBuffer[i + 4])  // check three zlde case
                        {
                            zmWaitReadData(1, dataNum[0], 1000);
                            dataNum[0] += 1;
                            frameendByte = ZDLE_END_SIZE_6;
                        }
                    }

                    // skip frame data
                    i += frameendByte - 1;
                }
            } else {
                zmDataBuffer[j++] = modemDataBuffer[i];
            }
        }

        return j; // return j: data number for saving
    }

    void checkZMStartingZRQINIT() {
        Message msg;
        for (int i = 0; i < actualNumBytes; i++) {
            switch (zmStartState) {
                case ZMS_0:
                    if (0x72 == readBuffer[i]) zmStartState = ZMS_1;
                    break;
                case ZMS_1:
                    if (0x7A == readBuffer[i]) zmStartState = ZMS_2;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_2:
                    if (0x0D == readBuffer[i]) zmStartState = ZMS_3;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_3:
                    if (ZPAD == readBuffer[i]) zmStartState = ZMS_4;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_4:
                    if (ZPAD == readBuffer[i]) zmStartState = ZMS_5;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_5:
                    if (ZDLE == readBuffer[i]) zmStartState = ZMS_6;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_6:
                    if (ZHEX == readBuffer[i]) zmStartState = ZMS_7;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_7:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_8;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_8:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_9;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_9:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_10;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_10:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_11;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_11:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_12;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_12:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_13;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_13:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_14;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_14:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_15;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_15:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_16;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_16:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_17;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_17:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_18;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_18:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_19;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_19:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_20;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_20:
                    if (0x30 == readBuffer[i]) zmStartState = ZMS_21;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_21:
                    if (0x0D == readBuffer[i]) zmStartState = ZMS_22;
                    else zmStartState = ZMS_0;
                    break;
                case ZMS_22:
                    if (0x0A == readBuffer[i] || (byte) 0x8A == readBuffer[i])
                        zmStartState = ZMS_23;
                    else {
                        zmStartState = ZMS_0;
                    }
                    break;
                case ZMS_23:
                    if (0x11 == readBuffer[i]) zmStartState = ZMS_24;
                    else zmStartState = ZMS_0;
                    break;
                default:
                    break;
            }

            if (zmStartState >= ZMS_1) {
            }

            if (ZMS_24 == zmStartState) {
                zmStartState = ZMS_0;
                msg = handler.obtainMessage(ACT_ZMODEM_AUTO_START_RECEIVE);
                handler.sendMessage(msg);
            }
        }
    }
// ZModem - ===============================================================================================

    // calculate CRC
    byte[] calCrc(byte[] buffer, int startPos, int count) {
        int crc = 0, i;
        byte[] crcHL = new byte[2];

        while (--count >= 0) {
            crc = crc ^ (int) buffer[startPos++] << 8;
            for (i = 0; i < 8; ++i) {
                if ((crc & 0x8000) != 0) crc = crc << 1 ^ 0x1021;
                else crc = crc << 1;
            }
        }
        crc &= 0xFFFF;

        crcHL[0] = (byte) ((crc >> 8) & 0xFF);
        crcHL[1] = (byte) (crc & 0xFF);

        return crcHL;
    }

    void accumulateCrc(byte[] crcHL, byte[] buffer, int startPos, int count) {
        int crc = 0, i;
        short val = (short) (((crcHL[0] & 0xFF) << 8) | (crcHL[1] & 0xFF));
        crc = (int) val;

        while (--count >= 0) {
            crc = crc ^ (int) buffer[startPos++] << 8;
            for (i = 0; i < 8; ++i) {
                if ((crc & 0x8000) != 0) crc = crc << 1 ^ 0x1021;
                else crc = crc << 1;
            }
        }
        crc &= 0xFFFF;

        crcHL[0] = (byte) ((crc >> 8) & 0xFF);
        crcHL[1] = (byte) (crc & 0xFF);
    }

    void accumulateCrc(byte[] crcHL, byte buffer) {
        int crc = 0, i;

        short val = (short) (((crcHL[0] & 0xFF) << 8) | (crcHL[1] & 0xFF));

        crc = (int) val;

        crc = crc ^ (int) buffer << 8;
        for (i = 0; i < 8; ++i) {
            if ((crc & 0x8000) != 0) crc = crc << 1 ^ 0x1021;
            else crc = crc << 1;
        }

        crc &= 0xFFFF;

        crcHL[0] = (byte) ((crc >> 8) & 0xFF);
        crcHL[1] = (byte) (crc & 0xFF);
    }

    String hexToAscii(String s) throws IllegalArgumentException {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n / 2);
        for (int i = 0; i < n; i += 2) {
            char a = s.charAt(i);
            char b = s.charAt(i + 1);
            sb.append((char) ((hexToInt(a) << 4) | hexToInt(b)));
        }
        return sb.toString();
    }

    static int hexToInt(char ch) {
        if ('a' <= ch && ch <= 'f') {
            return ch - 'a' + 10;
        }
        if ('A' <= ch && ch <= 'F') {
            return ch - 'A' + 10;
        }
        if ('0' <= ch && ch <= '9') {
            return ch - '0';
        }
        throw new IllegalArgumentException(String.valueOf(ch));
    }
}
