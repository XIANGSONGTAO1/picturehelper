package com.tao.picturehelper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import androidx.recyclerview.widget.RecyclerView;

import com.tao.picturehelper.listener.ImageLoaderListener;
import com.tao.picturehelper.R;
import com.tao.picturehelper.base.BaseRecyclerAdapter;
import com.tao.picturehelper.bean.Image;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rick Ge on 2017/1/11.
 */

public class ImageAdapter extends BaseRecyclerAdapter<Image> {
    private ImageLoaderListener loader;

    public ImageAdapter(Context context, ImageLoaderListener loader) {
        super(context, NEITHER);
        this.loader = loader;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        return new ImageViewHolder(mLayoutInflater.inflate(R.layout.item_list_image, parent, false));
    }

    @Override
    protected void onBindNormalViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        if (item.getId() != 0) {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;

            loader.displayImage(viewHolder.mImageView, item.getPath());
        }
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image) ImageView mImageView;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
