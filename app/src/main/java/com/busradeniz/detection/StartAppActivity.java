package com.busradeniz.detection;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.busradeniz.detection.bean.PermissionBean;
import com.busradeniz.detection.check.OPenMachineCheckActivity;
import com.busradeniz.detection.setting.adapter.RcyPermissionAdapter;
import com.busradeniz.detection.utils.IntentUtils;
import com.busradeniz.detection.utils.UiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartAppActivity extends AppCompatActivity {

    private RelativeLayout mTvView;
    private final int REQUEST_CODE = 12138;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_app);

        initView();
    }

    private void initView() {

        mTvView = findViewById(R.id.relativeLayout);
        AlphaAnimation anima = new AlphaAnimation(0.3f, 1.0f);
        anima.setDuration(1000);// 设置动画显示时间

        PackageManager pm = getPackageManager();
        PackageInfo pi;


        final List<PermissionBean> permissionBeanList = new ArrayList<>();

        Map<String, String> permissionMap = new HashMap<>();
        permissionMap.put("android.permission.CAMERA", UiUtils.getString(R.string.camera_permission));
        permissionMap.put("android.permission.WRITE_EXTERNAL_STORAGE", UiUtils.getString(R.string.write_storage));

        Map<String, String> permissionMapInfo = new HashMap<>();
        permissionMapInfo.put("android.permission.CAMERA", UiUtils.getString(R.string.camera_permission_info));
        permissionMapInfo.put("android.permission.WRITE_EXTERNAL_STORAGE", UiUtils.getString(R.string.write_storage_info));

        try {
            // 参数2必须是PackageManager.GET_PERMISSIONS
            pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            if (permissions != null) {
                for (int i = 0; i < permissions.length; i++) {
                    int permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
                    PermissionBean permissionBean = new PermissionBean();
                    permissionBean.setPermissionName(permissionMap.get(permissions[i]));
                    permissionBean.setPermissionCode(permissions[i]);
                    if (permissionMapInfo.get(permissions[i]) != null) {
                        permissionBean.setPermissionInfo(permissionMapInfo.get(permissions[i]));
                        if (permissionCheck == -1) {
                            permissionBeanList.add(permissionBean);
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (permissionBeanList.size() != 0) {
            //弹出dialog获取权限
            final Dialog dialog = new Dialog(this, R.style.dialog);
            View view = View.inflate(this, R.layout.dialog_permission_hint_view, null);
            RecyclerView rcyPermissionList = view.findViewById(R.id.rcy_permission_list);
            rcyPermissionList.setLayoutManager(new LinearLayoutManager(this));
            TextView tvNextStep = view.findViewById(R.id.tv_next_step);
            dialog.setCanceledOnTouchOutside(false);
            RcyPermissionAdapter adapter = new RcyPermissionAdapter(this, permissionBeanList);

            rcyPermissionList.setAdapter(adapter);

            tvNextStep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String[] stringArray = new String[permissionBeanList.size()];
                    for (int i = 0; i < permissionBeanList.size(); i++) {
                        stringArray[i] = permissionBeanList.get(i).getPermissionCode();
                    }

                    ActivityCompat.requestPermissions(StartAppActivity.this,
                            stringArray, REQUEST_CODE);
                }
            });

            dialog.setContentView(view);

            Window dialogWindow = dialog.getWindow();
            WindowManager m = getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.65
            dialogWindow.setAttributes(p);
            dialog.show();
        } else {
            mTvView.startAnimation(anima);
        }

        anima.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                IntentUtils.startActivity(StartAppActivity.this, OPenMachineCheckActivity.class);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentUtils.startActivity(StartAppActivity.this, OPenMachineCheckActivity.class);
                    finish();
                } else {
                    System.exit(0);
                }

                return;
            }
        }
    }

}
