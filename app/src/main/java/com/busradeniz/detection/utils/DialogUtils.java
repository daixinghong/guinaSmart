package com.busradeniz.detection.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;

import com.busradeniz.detection.BaseApplication;
import com.busradeniz.detection.R;

import java.util.ArrayList;
import java.util.List;

import top.limuyang2.customldialog.IOSMsgDialog;
import top.limuyang2.ldialog.LDialog;
import top.limuyang2.ldialog.base.BaseLDialog;
import top.limuyang2.ldialog.base.ViewHandlerListener;
import top.limuyang2.ldialog.base.ViewHolder;

public class DialogUtils {

    private static View popLayout;

    private static AlertDialog dialog;
    private static Context mContext;
    public static List<String> sList = new ArrayList<>();

    public static OnClickCallBack mOnClickCallBack;

    public interface OnClickCallBack {
        void setOnClickCallBack();
    }

    public static void setOnClickCallBack(OnClickCallBack onClickCallBack) {
        mOnClickCallBack = onClickCallBack;
    }


    public static View inflateView(Context context, int layoutId) {

        mContext = context;
        popLayout = null;
        if (popLayout == null) {
            popLayout = View.inflate(context, layoutId, null);
        }
        //【2new构建者设置布局+创建对话框】

        return popLayout;
    }

    public static AlertDialog createDialogFour(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.dialog);

        dialog = builder.create();


        dialog.setView(view, 0, 0, 0, 0);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return dialog;
    }

    public static AlertDialog createDialog(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.dialog);


        dialog = builder.create();


        dialog.setView(view, 0, 0, 0, 0);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        return dialog;
    }

    public static AlertDialog createDialogNotShow(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.dialog);


        dialog = builder.create();


        dialog.setView(view, 0, 0, 0, 0);

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        return dialog;
    }


    public static AlertDialog createDialogs(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.DialogStyle);

        dialog = builder.create();

        dialog.setView(view, 0, 0, 0, 0);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        return dialog;
    }


    public static void dissDialog() {
        dialog.dismiss();
    }


    public static void showTopDialog(FragmentManager fragmentManager, int layoutId, final long showTime) {
        LDialog.Companion.init(fragmentManager).setTag("topTips").setLayoutRes(layoutId)
                .setGravity(Gravity.TOP)
                .setWidthScale(1f)
                .setKeepWidthScale(true)
                .setAnimStyle(R.style.LDialogHorizontalAnimation)
                .setViewHandlerListener(new ViewHandlerListener() {
                    @Override
                    public void convertView(ViewHolder viewHolder, final BaseLDialog<?> baseLDialog) {
                        BaseApplication.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                baseLDialog.dismiss();
                            }
                        }, showTime);

                    }
                }).show();
    }

    private static IosDialogListener mIosDialogListener;

    public interface IosDialogListener {
        void onConfirmClickListener(View view);
    }

    public static void setOnConfirmClickListener(IosDialogListener iosDialogListener) {
        mIosDialogListener = iosDialogListener;
    }

    public static void showIosDialog(FragmentManager fragmentManager, String message) {
        IOSMsgDialog.Companion.init(fragmentManager)
                .setTitle(UiUtils.getString(R.string.hint))
                .setAnimStyle(R.style.LDialogScaleAnimation)
                .setMessage(message)
                .setNegativeButton(UiUtils.getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mIosDialogListener != null)
                            mIosDialogListener.onConfirmClickListener(view);

                    }
                }).setPositiveButton(UiUtils.getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }


}
