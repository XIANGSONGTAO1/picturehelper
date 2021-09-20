package com.tao.picturehelper.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.tao.picturehelper.listener.ImageLoaderListener;
import com.tao.picturehelper.R;
import com.tao.picturehelper.base.BaseRecyclerAdapter;
import com.tao.picturehelper.bean.ImageFolder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rick Ge on 2017/1/11.
 */

public class ImageFolderAdapter extends BaseRecyclerAdapter<ImageFolder> {
    private ImageLoaderListener loader;

    public ImageFolderAdapter(Context context, ImageLoaderListener loader) {
        super(context, NEITHER);
        this.loader = loader;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        return new FolderViewHolder(mLayoutInflater.inflate(R.layout.item_list_folder, parent, false));
    }

    @Override
    protected void onBindNormalViewHolder(RecyclerView.ViewHolder holder, ImageFolder item, int position) {
        FolderViewHolder viewHolder = (FolderViewHolder) holder;
        viewHolder.mFolderNameView.setText(item.getName());
        viewHolder.mFolderSizeView.setText(String.format("(%s)", item.getImages().size()));
        if (loader != null) {
            loader.displayImage(viewHolder.mFolderView, item.getAlbumPath());
        }
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_folder) ImageView mFolderView;
        @BindView(R.id.tv_folder_name) TextView mFolderNameView;
        @BindView(R.id.tv_size) TextView mFolderSizeView;

        public FolderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
