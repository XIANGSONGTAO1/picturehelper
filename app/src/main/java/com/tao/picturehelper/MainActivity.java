package com.tao.picturehelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tao.picturehelper.base.BaseActivity;
import com.tao.picturehelper.db.DBManager;
import com.tao.picturehelper.fragment.AlbumFragment;
import com.tao.picturehelper.service.MyService;
import com.tao.picturehelper.util.CommonUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Rick Ge on 2017/1/10.
 */

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    @BindView(R.id.search_et)
    TextView searchTv;
    @BindView(R.id.search_iv_submit)
    ImageView searchSubmit;
    @BindView(R.id.my_center)
    ImageView my_center;
    @BindView(R.id.refresh)
    ImageView refreshIM;
    private static final int RC_EXTERNAL_STORAGE = 0x02;
    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initWidget() {

        searchSubmit.setOnClickListener(this);
        my_center.setOnClickListener(this);
        refreshIM.setOnClickListener(this);
        super.initWidget();
        requestExternalStorage("");

    }

    private void handleView(String searchParam) {

        try {
            AlbumFragment fragment = new AlbumFragment();
            Bundle bundle = new Bundle();
            bundle.putString("searchParam", searchParam);

            fragment.setArguments(bundle);
            fragment.getActivity();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_content, fragment)
                    .commitNowAllowingStateLoss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestExternalStorage(String searchParam) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            handleView(searchParam);

        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        String searchParam = searchTv.getText().toString();
        handleView(searchParam);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_iv_submit:
                String searchParam = searchTv.getText().toString();
                requestExternalStorage(searchParam);
                break;
            case R.id.my_center:
                Map<String, Object> map = DBManager.queryUser();
                if (map != null && map.size() != 0) {
                    Intent intent = new Intent();
                    intent.setClass(this, MyCenterActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(this, LoginActivity.class);
                    startActivity(intent);

                }
                break;
            case R.id.refresh:
                boolean flag = CommonUtil.judgeThreadByName("identityThread");
                if (flag){
                    Toast.makeText(this,this.getString(R.string.is_identifying),Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(this,this.getString(R.string.start_identifying),Toast.LENGTH_SHORT).show();
                Intent startIntent = new Intent(this, MyService.class);
                startService(startIntent);
                break;
        }
    }
}
