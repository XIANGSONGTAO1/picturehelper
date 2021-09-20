package com.tao.picturehelper.base;

import android.os.Bundle;
import android.os.StrictMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import butterknife.ButterKnife;

/**
 * Created by Rick Ge on 2016/12/4.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected RequestManager mImageLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(initBundle(getIntent().getExtras())){
            setContentView(getContentView());

            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            ButterKnife.bind(this);
            initWindow();
            initWidget();
            initData();
        }
        else{
            finish();
        }
    }

    protected abstract int getContentView();

    protected boolean initBundle(Bundle bundle) {
        return true;
    }

    protected void initWindow() {
    }

    protected void initData() {
    }

    protected void initWidget() {
    }

    public synchronized RequestManager getImageLoader() {
        if (mImageLoader == null)
            mImageLoader = Glide.with(this);
        return mImageLoader;
    }
}
