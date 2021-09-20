package com.tao.picturehelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.tao.picturehelper.base.BaseActivity;
import com.tao.picturehelper.widget.PreviewViewPager;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;


/**
 * Created by Rick Ge on 2017/1/12.
 */

public class ImageGalleryActivity extends BaseActivity {
    public static final String KEY_IMAGE = "images";
    public static final String KEY_POSITION = "position";

    @BindView(R.id.vp_image)
    PreviewViewPager mImagePager;
    private String[] mImageSources;
    private int mCurPosition;
    private String curentPath = "";

    @Override
    protected int getContentView() {
        return R.layout.activity_image_gallery;
    }

    public static void show(Context context, String[] images, int position) {
        if (images == null || images.length == 0)
            return;
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra(KEY_IMAGE, images);
        intent.putExtra(KEY_POSITION, position);
        context.startActivity(intent);
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mImageSources = bundle.getStringArray(KEY_IMAGE);
        mCurPosition = bundle.getInt(KEY_POSITION, 0);
        return mImageSources != null;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void initData() {
        super.initData();
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;

        mImagePager.setAdapter(new ViewPagerAdapter());
        mImagePager.setCurrentItem(mCurPosition);
        mImagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private class ViewPagerAdapter extends PagerAdapter {

        private View.OnClickListener mFinishClickListener;
        private View.OnTouchListener onTouchListener;

        @Override
        public int getCount() {
            return mImageSources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(container.getContext())
                    .inflate(R.layout.gallery_page_item, container, false);
//            ScaleImageView previewView = view.findViewById(R.id.iv_preview);
//              ImageView previewView = (ImageView) view.findViewById(R.id.iv_preview);
//            ImageView photoView = (ImageView) findViewById(R.id.photo_view);
            PhotoView photoView1 = (PhotoView) view.findViewById(R.id.photo_view);
            photoView1.setImageBitmap(localImage(mImageSources[position]));
            ProgressBar loading = (ProgressBar) view.findViewById(R.id.loading);
            ImageView defaultView = (ImageView) view.findViewById(R.id.iv_default);

            loadImage(mImageSources[position], photoView1, defaultView, loading);

            photoView1.setOnClickListener(getFinishListener());
//            view.setOnTouchListener(getOnTouch());
            container.addView(view);
            curentPath = mImageSources[position];
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private Bitmap localImage(String path) {
            Bitmap bm = null;
//		 加载本地图片，缩放处理
            try {
//		 图片在asset目录中
                InputStream is = getAssets().open(path);
                bm = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bm;
        }

        private View.OnClickListener getFinishListener() {
            if (mFinishClickListener == null) {
                mFinishClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SelectPicPopupWindowActivity.class);
                        Bundle bundle = new Bundle();
                        curentPath = mImageSources[mCurPosition];
                        bundle.putString("path", curentPath);
                        intent.putExtras(bundle);
                        startActivity(intent);


//                        finish();
                    }
                };
            }
            return mFinishClickListener;
        }

        private View.OnTouchListener getOnTouch() {
            if (onTouchListener == null) {
                onTouchListener = new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        // return ArtFilterActivity.this.mGestureDetector.onTouchEvent(event);
                        float baseValue=0;
                        float last_x=5;
                        float last_y=10;
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            baseValue = 0;
                            float x = last_x = event.getRawX();
                            float y = last_y = event.getRawY();
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            if (event.getPointerCount() == 2) {
                                float x = event.getX(0) - event.getX(1);
                                float y = event.getY(0) - event.getY(1);
//                        float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
                                float value = (float) x;// 计算x的距离
                                if (baseValue == 0) {
                                    baseValue = value;
                                } else {
                                    if (value - baseValue >= 10 || value - baseValue <= -10) {
                                        float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
                                        Log.e("ddddddd", scale + "");  //缩放图片
                                    }
                                }
                            } else if (event.getPointerCount() == 1) {
                                float x = event.getRawX();
                                float y = event.getRawY();
                                x -= last_x;
                                y -= last_y;
                                if (x >= 10 || y >= 10 || x <= -10 || y <= -10)
                                    Log.e("移动图片位置", "移动图片位置"); //移动图片位置
                                last_x = event.getRawX();
                                last_y = event.getRawY();
                            }
                        } else if (event.getAction() == MotionEvent.ACTION_UP) {

                        }
                        return true;
                    }
                };
            }
            return onTouchListener;
        }
    }

    private void loadImage(String path, ImageView previewView, final ImageView defaultView, final ProgressBar loading) {
//        TessBaseAPI baseApi = new TessBaseAPI();
//        baseApi.init(SD_PATH, DICTIONARY);
//        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
//        //记得要在对应的文件夹里放上要识别的图片文件，比如我这里就在sd卡根目录放了img.png
//        baseApi.setImage(new File(SD_PATH+"/img.png"));
//        final String result= baseApi.getUTF8Text();
//        //这里，你可以把result的值赋值给你的TextView
//        baseApi.end();
        DrawableRequestBuilder builder = getImageLoader()
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (e != null)
                            e.printStackTrace();
                        loading.setVisibility(View.GONE);
                        defaultView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        loading.setVisibility(View.GONE);
                        return false;
                    }
                }).diskCacheStrategy(DiskCacheStrategy.SOURCE);

        builder.into(previewView);
    }
}
